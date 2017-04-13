package io.logz.guice.jersey;

import io.logz.guice.jersey.resources.AsyncResource;
import io.logz.guice.jersey.supplier.JerseyServerSupplier;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AsyncResourceTest {

    @Test
    public void testAsyncRequestWorks() throws Exception {
        ResourceConfig resourceConfig = new ResourceConfig().registerClasses(AsyncResource.class);
        JerseyServerSupplier.createServerAndTest(resourceConfig, target -> {
            String response = target.path(AsyncResource.PATH).request().get().readEntity(String.class);
            assertEquals(AsyncResource.MESSAGE, response);
        });
    }

}
