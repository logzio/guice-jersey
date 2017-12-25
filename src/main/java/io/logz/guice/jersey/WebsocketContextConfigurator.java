package io.logz.guice.jersey;

import com.google.inject.Injector;
import io.logz.guice.jersey.configuration.WebsocketConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.api.InvalidWebSocketException;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import javax.servlet.ServletException;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

class WebsocketContextConfigurator extends AbstractContextConfigurator {

    WebsocketContextConfigurator(Server server, Supplier<Injector> injectorSupplier) {
        super(server, injectorSupplier);
    }

    Optional<ContextHandler> configure(WebsocketConfiguration configuration) {

        if (configuration == null) return Optional.empty();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setServer(server);

        try {
            ServerContainer wsContainer = WebSocketServerContainerInitializer.configureContext(context);
            GuiceConfigurator configurator = new GuiceConfigurator(injectorSupplier);
            configuration.getEndpointClasses().forEach(endpointClass -> {
                try {
                    ServerEndpointConfig config = createEndpointConfig(
                            endpointClass, configurator);
                    wsContainer.addEndpoint(config);
                } catch (DeploymentException e) {
                    throw new InvalidWebSocketException("Cannot add endpoint " + endpointClass.getName(), e);
                }
            });

            return Optional.of(context);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    private ServerEndpointConfig createEndpointConfig(
            Class<?> endpointClass,
            GuiceConfigurator configurator) {

        ServerEndpoint annotation = endpointClass.getAnnotation(ServerEndpoint.class);
        if (annotation == null) {
            throw new InvalidWebSocketException("Unsupported WebSocket object, missing @" + ServerEndpoint.class + " annotation");
        }

        return ServerEndpointConfig.Builder.create(endpointClass, annotation.value())
                .subprotocols(Arrays.asList(annotation.subprotocols()))
                .decoders(Arrays.asList(annotation.decoders()))
                .encoders(Arrays.asList(annotation.encoders()))
                .configurator(configurator)
                .build();
    }

}
