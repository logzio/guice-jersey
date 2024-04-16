package io.logz.guice.jersey;

import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.JerseyConfigurationBuilder;
import io.logz.guice.jersey.filters.AddHeaderJettyFilter;
import io.logz.guice.jersey.resources.TestResource;
import io.logz.guice.jersey.supplier.JerseyServerSupplier;
import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.server.Server;
import org.junit.Test;

import jakarta.servlet.DispatcherType;
import jakarta.ws.rs.core.Response;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JettyFilterRegistrationTest {

    @Test
    public void testJettyFilter() throws Exception {
        JerseyConfigurationBuilder configurationBuilder = JerseyConfiguration.builder()
                .addResourceClass(TestResource.class);

        String myTestValue = "param";

        JerseyServerSupplier.Tester tester = target -> {
            Response response = target.path(TestResource.PATH).request().get();
            String headerParamValue = response.getHeaderString(AddHeaderJettyFilter.TEST_HEADER);
            String responseBody = response.readEntity(String.class);
            assertEquals(TestResource.MESSAGE, responseBody);
            assertEquals(headerParamValue, myTestValue);
        };

        JerseyServerSupplier.createServerAndTest(configurationBuilder, Server::new, tester, webAppContext -> {
            FilterHolder filterHolder = new FilterHolder(AddHeaderJettyFilter.class);
            Map<String, String> initParams = new HashMap<>();
            initParams.put(AddHeaderJettyFilter.INIT_PARAM_KEY, myTestValue);
            filterHolder.setInitParameters(initParams);
            webAppContext.addFilter(filterHolder, "/*", EnumSet.allOf(DispatcherType.class));
        });
    }
}
