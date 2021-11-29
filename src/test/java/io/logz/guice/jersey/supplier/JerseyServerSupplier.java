package io.logz.guice.jersey.supplier;

import com.google.inject.Guice;
import com.google.inject.Module;
import io.logz.guice.jersey.JerseyModule;
import io.logz.guice.jersey.JerseyServer;
import io.logz.guice.jersey.JettyServerCreator;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.JerseyConfigurationBuilder;
import io.logz.guice.jersey.configuration.JerseyWebApplicationConfigurator;
import me.alexpanov.net.FreePortFinder;
import org.eclipse.jetty.server.Server;
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
        createServerAndTest(configurationBuilder, Server::new, tester);
    }

    public static void createServerAndTest(JerseyConfigurationBuilder configurationBuilder,
                                           JettyServerCreator jettyServerCreator,
                                           Tester tester) throws Exception {
        int port = FreePortFinder.findFreeLocalPort();
        configurationBuilder.addPort(port);

        createServerAndTest(configurationBuilder, jettyServerCreator, tester, port);
    }

    public static void createServerAndTest(JerseyConfigurationBuilder configurationBuilder,
                                           JettyServerCreator jettyServerCreator,
                                           Tester tester,
                                           int port) throws Exception {
        JerseyConfiguration configuration = configurationBuilder.build();

        JerseyServer server = createServer(configuration, jettyServerCreator, null);
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

    public static void createServerAndTest(JerseyConfigurationBuilder configurationBuilder,
                                           JettyServerCreator jettyServerCreator,
                                           Tester tester,
                                           JerseyWebApplicationConfigurator jerseyWebApplicationConfigurator) throws Exception {
        int port = FreePortFinder.findFreeLocalPort();
        configurationBuilder.addPort(port);
        JerseyConfiguration configuration = configurationBuilder.build();

        JerseyServer server = createServer(configuration, jettyServerCreator, jerseyWebApplicationConfigurator);
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


    private static JerseyServer createServer(JerseyConfiguration configuration, JettyServerCreator jettyServerCreator, JerseyWebApplicationConfigurator jerseyWebApplicationConfigurator) {
        List<Module> modules = new ArrayList<>();
        modules.add(new JerseyModule(configuration, jettyServerCreator, jerseyWebApplicationConfigurator));

        return Guice.createInjector(modules)
                .getInstance(JerseyServer.class);
    }

    public interface Tester {

        void test(WebTarget target) throws Exception;

    }
}
