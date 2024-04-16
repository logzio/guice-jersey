package io.logz.guice.jersey;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.JerseyWebApplicationConfigurator;
import io.logz.guice.jersey.configuration.ServerConnectorConfiguration;
import jakarta.servlet.DispatcherType;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;

public class JerseyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyServer.class);

    private final JerseyConfiguration jerseyConfiguration;
    private final JerseyWebApplicationConfigurator jerseyWebApplicationConfigurator;
    private final Supplier<Injector> injectorSupplier;
    private final Server server;

    JerseyServer(JerseyConfiguration jerseyConfiguration,
                 Supplier<Injector> injectorSupplier,
                 JettyServerCreator jettyServerCreator,
                 JerseyWebApplicationConfigurator jerseyWebApplicationConfigurator) {
        this.jerseyConfiguration = jerseyConfiguration;
        this.injectorSupplier = injectorSupplier;
        this.server = jettyServerCreator.create();
        this.jerseyWebApplicationConfigurator = jerseyWebApplicationConfigurator;

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
        if (jerseyWebApplicationConfigurator != null)
            jerseyWebApplicationConfigurator.configure(webAppContext);
        webAppContext.setServer(server);

        webAppContext.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));

        ServletHolder holder = new ServletHolder(ServletContainer.class);
        holder.setInitParameter("jakarta.ws.rs.Application", GuiceJerseyResourceConfig.class.getName());

        webAppContext.addServlet(holder, "/*");
        webAppContext.setBaseResourceAsString("/");
        webAppContext.setContextPath(jerseyConfiguration.getContextPath());
        webAppContext.addEventListener(new GuiceServletContextListener() {
            @Override
            protected Injector getInjector() {
                return injectorSupplier.get();
            }
        });

        setHandler(server, webAppContext);
    }

    private static void setHandler(Handler.Wrapper handlerWrapper, Handler handlerToAdd) {
        Handler currentInnerHandler = handlerWrapper.getHandler();
        if (currentInnerHandler == null) {
            handlerWrapper.setHandler(handlerToAdd);
        } else if (currentInnerHandler instanceof Handler.Collection) {
            ((Handler.Collection) currentInnerHandler).addHandler(handlerToAdd);
        } else if (currentInnerHandler instanceof Handler.Wrapper) {
            setHandler((Handler.Wrapper) currentInnerHandler, handlerToAdd);
        } else {
            Handler.Sequence handlerList = new Handler.Sequence();
            handlerList.addHandler(currentInnerHandler);
            handlerList.addHandler(handlerToAdd);
            handlerWrapper.setHandler(handlerWrapper);
        }
    }
}
