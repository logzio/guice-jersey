package io.logz.guice.jersey;

import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.JerseyConfigurationBuilder;
import io.logz.guice.jersey.filters.FilterHeaderService;
import io.logz.guice.jersey.filters.TestFilter;
import io.logz.guice.jersey.resources.TestResource;
import io.logz.guice.jersey.supplier.JerseyServerSupplier;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class FiltersWithGuiceIntegrationTest {

    @Test
    public void testFilterWorksWithGuice() throws Exception {
        JerseyConfigurationBuilder configurationBuilder = JerseyConfiguration.builder()
                .addResourceClass(TestResource.class)
                .registerClasses(TestFilter.class);

        JerseyServerSupplier.createServerAndTest(configurationBuilder, target -> {
            Response response = target.path(TestResource.PATH).request().get();
            String testResourceResponse = response.readEntity(String.class);

            assertEquals(TestResource.MESSAGE, testResourceResponse);
            assertEquals(FilterHeaderService.TEST_HEADER_VALUE, response.getHeaderString(TestFilter.TEST_HEADER));
        });
    }
}
