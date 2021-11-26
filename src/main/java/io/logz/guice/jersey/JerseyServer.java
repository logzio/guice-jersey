package io.logz.guice.jersey;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.ServerConnectorConfiguration;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;

public class JerseyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyServer.class);

    private final JerseyConfiguration jerseyConfiguration;
    private final Supplier<Injector> injectorSupplier;
    private final Server server;
    private final List<JettyFilterDefinition> jettyFilterDefinitions;

    JerseyServer(JerseyConfiguration jerseyConfiguration,
                 Supplier<Injector> injectorSupplier,
                 JettyServerCreator jettyServerCreator,
                 List<JettyFilterDefinition> jettyFilterDefinitions) {
        this.jerseyConfiguration = jerseyConfiguration;
        this.injectorSupplier = injectorSupplier;
        this.server = jettyServerCreator.create();
        this.jettyFilterDefinitions = jettyFilterDefinitions;

        configureServer();
    }

    public void start() throws Exception {
        LOGGER.info("Starting embedded jetty server");
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
        LOGGER.info("Embedded jetty server stopped");
    }

    private void configureServer() {
        List<ServerConnectorConfiguration> serverConnectorConfigurations = jerseyConfiguration.getServerConnectors();
        serverConnectorConfigurations.forEach(configuration -> {
            ServerConnector connector = new ServerConnector(server);
            connector.setName(configuration.getName());
            connector.setHost(configuration.getHost());
            connector.setPort(configuration.getPort());
            connector.setConnectionFactories(Collections.singleton(new HttpConnectionFactory(configuration.getHttpConfiguration())));
            server.addConnector(connector);
        });

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setServer(server);

        webAppContext.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        jettyFilterDefinitions.forEach(jettyFilterDefinition -> {
            FilterHolder filterHolder = new FilterHolder(jettyFilterDefinition.getFilterClass());
            if (!jettyFilterDefinition.getInitParameters().isEmpty())
                filterHolder.setInitParameters(jettyFilterDefinition.getInitParameters());
            webAppContext.addFilter(filterHolder, jettyFilterDefinition.getPathSpec(), jettyFilterDefinition.getDispatches());
        });

        ServletHolder holder = new ServletHolder(ServletContainer.class);
        holder.setInitParameter("javax.ws.rs.Application", GuiceJerseyResourceConfig.class.getName());

        webAppContext.addServlet(holder, "/*");
        webAppContext.setResourceBase("/");
        webAppContext.setContextPath(jerseyConfiguration.getContextPath());
        webAppContext.addEventListener(new GuiceServletContextListener() {
            @Override
            protected Injector getInjector() {
                return injectorSupplier.get();
            }
        });

        setHandler(server, webAppContext);
    }

    private static void setHandler(HandlerWrapper handlerWrapper, Handler handlerToAdd) {
        Handler currentInnerHandler = handlerWrapper.getHandler();
        if (currentInnerHandler == null) {
            handlerWrapper.setHandler(handlerToAdd);
        } else if (currentInnerHandler instanceof HandlerCollection) {
            ((HandlerCollection) currentInnerHandler).addHandler(handlerToAdd);
        } else if (currentInnerHandler instanceof HandlerWrapper) {
            setHandler((HandlerWrapper) currentInnerHandler, handlerToAdd);
        } else {
            HandlerList handlerList = new HandlerList();
            handlerList.addHandler(currentInnerHandler);
            handlerList.addHandler(handlerToAdd);
            handlerWrapper.setHandler(handlerWrapper);
        }
    }

}
