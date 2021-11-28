package io.logz.guice.jersey;

import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.JerseyConfigurationBuilder;
import io.logz.guice.jersey.filters.JettyTestFilter;
import io.logz.guice.jersey.resources.TestResource;
import io.logz.guice.jersey.supplier.JerseyServerSupplier;
import me.alexpanov.net.FreePortFinder;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import javax.servlet.DispatcherType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JettyFilterRegistrationTest {

    @Test
    public void testJettyFilter() throws Exception {
        ResourceConfig resourceConfig = new ResourceConfig().registerClasses(TestResource.class);
        JerseyConfigurationBuilder configurationBuilder = JerseyConfiguration.builder()
                .withResourceConfig(resourceConfig);
        int port = FreePortFinder.findFreeLocalPort();
        configurationBuilder.addPort(port);
        JettyFilterDefinition jettyFilterDefinition = new JettyFilterDefinition(
                JettyTestFilter.class, "/*", EnumSet.allOf(DispatcherType.class), Collections.emptyMap()
        );
        List<JettyFilterDefinition> jettyFilterDefinitions = new ArrayList<>();
        jettyFilterDefinitions.add(jettyFilterDefinition);
        JerseyServerSupplier.Tester tester = target -> {
            Response response = target.path(TestResource.PATH).request().get();
            String headerString = response.getHeaderString(JettyTestFilter.TEST_HEADER);
            String responseBody = response.readEntity(String.class);
            assertEquals(TestResource.MESSAGE, responseBody);
            assertEquals(headerString, "filter-worked");
        };

        JerseyServerSupplier.createServerAndTest(configurationBuilder, Server::new, tester, port, jettyFilterDefinitions);
    }
}
