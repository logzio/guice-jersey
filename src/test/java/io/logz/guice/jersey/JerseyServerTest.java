package io.logz.guice.jersey;

import io.logz.guice.jersey.resources.PingResource;
import io.logz.guice.jersey.resources.TestResource;
import io.logz.guice.jersey.supplier.JerseyServerSupplier;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Asaf Alima on 19/12/2016.
 */
public class JerseyServerTest {

    @Test
    public void testBasicConfiguration() throws Exception {
        ResourceConfig resourceConfig = new ResourceConfig().registerClasses(TestResource.class);
        JerseyServerSupplier.createServerAndTest(resourceConfig, target -> {
            String response = target.path("test").request().get().readEntity(String.class);
            assertEquals(TestResource.MESSAGE, response);
        });
    }

    @Test
    public void testPackageScanningConfiguration() throws Exception {
        ResourceConfig resourceConfig = new ResourceConfig().packages(getClass().getPackage().toString());
        JerseyServerSupplier.createServerAndTest(resourceConfig, target -> {
            String testResourceResponse = target.path("test").request().get().readEntity(String.class);
            assertEquals(TestResource.MESSAGE, testResourceResponse);

            String pingResourceResponse = target.path("ping").request().get().readEntity(String.class);
            assertEquals(PingResource.MESSAGE, pingResourceResponse);
        });
    }

}
