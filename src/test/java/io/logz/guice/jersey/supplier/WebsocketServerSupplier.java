package io.logz.guice.jersey.supplier;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.logz.guice.jersey.JerseyModule;
import io.logz.guice.jersey.JerseyServer;
import io.logz.guice.jersey.configuration.JerseyConfigurationBuilder;
import io.logz.guice.jersey.configuration.WebsocketConfiguration;
import org.apache.mina.util.AvailablePortFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WebsocketServerSupplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketServerSupplier.class);
    private final JerseyConfigurationBuilder configurationBuilder;
    private final WebsocketConfiguration websocketConfiguration;

    public WebsocketServerSupplier(JerseyConfigurationBuilder configurationBuilder,
                                   WebsocketConfiguration websocketConfiguration) {
        this.configurationBuilder = configurationBuilder;
        this.websocketConfiguration = websocketConfiguration;
    }

    public void createServerAndTest(Tester tester) throws Exception {

        int port = AvailablePortFinder.getNextAvailable();
        configurationBuilder.addPort(port);

        JerseyServer server = createServer(tester);
        try {
            server.start();
            LOGGER.info("Started server on port: {}", port);

            Class<?> endpointClass = websocketConfiguration.getEndpointClasses().get(0);
            String path = endpointClass.getAnnotation(ServerEndpoint.class).value();
            URI uri = URI.create("ws://localhost:" + port + path);

            Injector injector = Guice.createInjector(tester.getTestModule());
            Object instance = injector.getInstance(endpointClass);
            Session session = ContainerProvider.getWebSocketContainer().connectToServer(instance, uri);

            tester.test(session);
            session.close();
        } finally {
            server.stop();
        }
    }

    private JerseyServer createServer(Tester tester) {

        List<Module> modules = new ArrayList<>();
        modules.add(new JerseyModule(configurationBuilder.build(), websocketConfiguration));
        modules.add(tester.getTestModule());

        return Guice.createInjector(modules)
                .getInstance(JerseyServer.class);
    }

    public interface Tester {

        void test(Session session) throws Exception;

        Module getTestModule();

    }
}
