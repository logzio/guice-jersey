package io.logz.guice.jersey;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.logz.guice.jersey.configuration.JerseyModuleConfiguration;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;

/**
 * Created by Asaf Alima on 19/12/2016.
 */
public class JerseyServerTest {

    private JerseyServer jerseyServer;

    @Before
    public void setUp() throws Exception {
        ResourceConfig resourceConfig = new ResourceConfig().registerClasses(TestResource.class);
        JerseyModuleConfiguration jerseyModuleConfiguration = new JerseyModuleConfiguration(8080, resourceConfig, "/resources");
        AtomicReference<Injector> injectorRef = new AtomicReference<>();

        List<Module> modules = new ArrayList<>();
        modules.add(new JerseyModule(jerseyModuleConfiguration, injectorRef::get));

        Injector injector = Guice.createInjector(modules);
        injectorRef.set(injector);

        jerseyServer = injector.getInstance(JerseyServer.class);
        jerseyServer.start();
    }

    @After
    public void tearDown() throws Exception {
        jerseyServer.stop();
    }

    @Test
    public void sanityCheck() throws InterruptedException {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080").path("resources").path("test");

        String response = target.request().get().readEntity(String.class);
        assertEquals(TestResource.MESSAGE, response);
    }

}
