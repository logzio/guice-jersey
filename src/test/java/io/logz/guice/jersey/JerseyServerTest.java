package io.logz.guice.jersey;

import io.logz.guice.jersey.resources.PingResource;
import io.logz.guice.jersey.resources.TestResource;
import org.apache.mina.util.AvailablePortFinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static org.junit.Assert.assertEquals;

/**
 * Created by Asaf Alima on 19/12/2016.
 */
public class JerseyServerTest {

    @Test
    public void testBasicConfiguration() throws Exception {
        int port = AvailablePortFinder.getNextAvailable();
        ResourceConfig resourceConfig = new ResourceConfig().registerClasses(TestResource.class);
        JerseyServer server = JerseyServerHelper.createServer(port, resourceConfig);

        try {
            server.start();

            Client client = ClientBuilder.newClient();
            WebTarget target = client.target("http://localhost:" + port).path("resources").path("test");

            String response = target.request().get().readEntity(String.class);
            assertEquals(TestResource.MESSAGE, response);
        } finally {
            server.stop();
        }
    }

    @Test
    public void testPackageScanningConfiguration() throws Exception {
        int port = AvailablePortFinder.getNextAvailable();
        ResourceConfig resourceConfig = new ResourceConfig().packages(getClass().getPackage().toString());
        JerseyServer server = JerseyServerHelper.createServer(port, resourceConfig);

        try {
            server.start();

            Client client = ClientBuilder.newClient();
            WebTarget target = client.target("http://localhost:" + port).path("resources");

            String testResourceResponse = target.path("test").request().get().readEntity(String.class);
            assertEquals(TestResource.MESSAGE, testResourceResponse);

            String pingResourceResponse = target.path("ping").request().get().readEntity(String.class);
            assertEquals(PingResource.MESSAGE, pingResourceResponse);
        } finally {
            server.stop();
        }
    }

}
