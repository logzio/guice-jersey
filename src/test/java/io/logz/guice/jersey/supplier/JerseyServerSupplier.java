package io.logz.guice.jersey.supplier;

import com.google.inject.Guice;
import com.google.inject.Module;
import io.logz.guice.jersey.JerseyModule;
import io.logz.guice.jersey.JerseyServer;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.JerseyConfigurationBuilder;
import org.apache.mina.util.AvailablePortFinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.List;

public class JerseyServerSupplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyServerSupplier.class);

    public static void createServerAndTest(ResourceConfig resourceConfig, Tester tester) throws Exception {
        JerseyConfigurationBuilder configurationBuilder = JerseyConfiguration.builder()
                .withResourceConfig(resourceConfig);

        createServerAndTest(configurationBuilder, tester);
    }

    public static void createServerAndTest(JerseyConfigurationBuilder configurationBuilder, Tester tester) throws Exception {
        int port = AvailablePortFinder.getNextAvailable();
        configurationBuilder.addPort(port);
        JerseyConfiguration configuration = configurationBuilder.build();

        JerseyServer server = createServer(configuration);
        try {
            server.start();
            LOGGER.info("Started server on port: {}", port);

            Client client = ClientBuilder.newClient();
            WebTarget target = client.target("http://localhost:" + port).path(configuration.getContextPath());
            tester.test(target);
        } finally {
            server.stop();
        }
    }

    private static JerseyServer createServer(JerseyConfiguration configuration) {
        List<Module> modules = new ArrayList<>();
        modules.add(new JerseyModule(configuration));

        return Guice.createInjector(modules)
                .getInstance(JerseyServer.class);
    }

    public interface Tester {

        void test(WebTarget target) throws Exception;

    }
}
