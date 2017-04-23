package io.logz.guice.jersey;

import com.google.inject.Injector;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.resources.TestResource;
import org.apache.mina.util.AvailablePortFinder;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static io.logz.guice.jersey.supplier.JerseyServerSupplier.createInjector;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InjectableServiceLocatorTest {

    @Test
    public void testServiceLocatorIsInjectableByGuice() throws Exception {
        int port = AvailablePortFinder.getNextAvailable();
        JerseyConfiguration configuration = JerseyConfiguration.builder()
                .addPort(port)
                .withContextPath("resources")
                .addResourceClass(TestResource.class)
                .build();

        WebTarget target = ClientBuilder.newClient().target("http://localhost:" + port).path(configuration.getContextPath());

        Injector injector = createInjector(configuration);
        injector.getInstance(JerseyServer.class).start();

        assertEquals(TestResource.MESSAGE, target.path(TestResource.PATH).request().get(String.class));

        injector.getInstance(JerseyServer.class).stop();
        ServiceLocator serviceLocator = injector.getInstance(ServiceLocator.class);
        assertNotNull(serviceLocator);
    }

}
