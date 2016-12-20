package io.logz.guice.jersey;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.logz.guice.jersey.configuration.JerseyModuleConfiguration;
import org.apache.mina.util.AvailablePortFinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Created by Asaf Alima on 19/12/2016.
 */
class JerseyServerSupplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyServerSupplier.class);

    static void createServerAndTest(ResourceConfig resourceConfig, Consumer<WebTarget> tester) throws Exception {
        int port = AvailablePortFinder.getNextAvailable();
        JerseyServer server = createServer(port, resourceConfig);
        try {
            server.start();
            LOGGER.info("Started server on port: {}", port);

            Client client = ClientBuilder.newClient();
            WebTarget target = client.target("http://localhost:" + port).path("resources");
            tester.accept(target);
        } finally {
            server.stop();
        }
    }

    private static JerseyServer createServer(int port, ResourceConfig resourceConfig) {
        JerseyModuleConfiguration jerseyModuleConfiguration = new JerseyModuleConfiguration(port, resourceConfig, "/resources");
        AtomicReference<Injector> injectorRef = new AtomicReference<>();

        List<Module> modules = new ArrayList<>();
        modules.add(new JerseyModule(jerseyModuleConfiguration, injectorRef::get));

        Injector injector = Guice.createInjector(modules);
        injectorRef.set(injector);

        return injector.getInstance(JerseyServer.class);
    }

}
