package io.logz.guice.jersey;

import com.google.inject.Injector;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.ServerConnectorConfiguration;
import io.logz.guice.jersey.configuration.WebsocketConfiguration;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.websocket.api.InvalidWebSocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class JerseyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyServer.class);

    private final JerseyConfiguration jerseyConfiguration;
    private final WebsocketConfiguration websocketConfiguration;
    private final Supplier<Injector> injectorSupplier;
    private final Server server;

    JerseyServer(JerseyConfiguration jerseyConfiguration,
                 WebsocketConfiguration websocketConfiguration,
                 Supplier<Injector> injectorSupplier) {
        this.jerseyConfiguration = jerseyConfiguration;
        this.websocketConfiguration = websocketConfiguration;
        this.injectorSupplier = injectorSupplier;
        this.server = new Server();

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
            server.addConnector(connector);
        });

        if (websocketConfiguration != null && "/".equals(jerseyConfiguration.getContextPath())) {
            throw new InvalidWebSocketException(
                    "The websocket context path cannot be the same as jersey context path. " +
                            "Either set a context path for the rest API or disable the websocket feature. " +
                            "Also make sure that a REST resource does not have the same path as a websocket path.");
        }

        Optional<ContextHandler> webAppContext = new RestContextConfigurator(server, injectorSupplier)
                .configure(jerseyConfiguration);

        Optional<ContextHandler> websocketContext = new WebsocketContextConfigurator(server, injectorSupplier)
                .configure(websocketConfiguration);

        ContextHandlerCollection contexts = new ContextHandlerCollection();

        List<Handler> handlers = new ArrayList<>();
        webAppContext.ifPresent(handlers::add);
        websocketContext.ifPresent(handlers::add);

        contexts.setHandlers(handlers.toArray(new Handler[]{}));

        server.setHandler(contexts);
    }

}
