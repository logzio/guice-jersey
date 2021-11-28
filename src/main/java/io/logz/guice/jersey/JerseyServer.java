package io.logz.guice.jersey;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.JettyServerConfiguration;
import io.logz.guice.jersey.configuration.ServerConnectorConfiguration;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class JerseyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyServer.class);

    private final JerseyConfiguration jerseyConfiguration;
    private final JettyServerConfiguration jettyServerConfiguration;
    private final Supplier<Injector> injectorSupplier;
    private final Server server;

    JerseyServer(JerseyConfiguration jerseyConfiguration,
                 Supplier<Injector> injectorSupplier,
                 JettyServerCreator jettyServerCreator,
                 JettyServerConfiguration jettyServerConfiguration) {
        this.jerseyConfiguration = jerseyConfiguration;
        this.injectorSupplier = injectorSupplier;
        this.server = jettyServerCreator.create();
        this.jettyServerConfiguration = jettyServerConfiguration;

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
        if (jettyServerConfiguration != null)
            jettyServerConfiguration.webAppContextConfiguration(webAppContext);

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
