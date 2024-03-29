package io.logz.guice.jersey;

import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.JerseyConfigurationBuilder;
import io.logz.guice.jersey.configuration.ServerConnectorConfiguration;
import io.logz.guice.jersey.resources.PingResource;
import io.logz.guice.jersey.resources.TestResource;
import io.logz.guice.jersey.resources.recursive.FooResource;
import io.logz.guice.jersey.supplier.JerseyServerSupplier;
import me.alexpanov.net.FreePortFinder;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

public class JerseyServerTest {

    @Test
    public void testBasicConfiguration() throws Exception {
        ResourceConfig resourceConfig = new ResourceConfig().registerClasses(TestResource.class);
        JerseyServerSupplier.createServerAndTest(resourceConfig, target -> {
            String response = target.path(TestResource.PATH).request().get().readEntity(String.class);
            assertEquals(TestResource.MESSAGE, response);
        });
    }

    @Test
    public void testMaxHeaderRequestSize() throws Exception {
        int port = FreePortFinder.findFreeLocalPort();
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setRequestHeaderSize(16384);

        ServerConnectorConfiguration connectorConfiguration =
                ServerConnectorConfiguration.builder(port).withHttpConfiguration(httpConfiguration).build();

        JerseyConfigurationBuilder connectorConfigurationBuilder = JerseyConfiguration.builder()
                .addConnectorConfig(connectorConfiguration)
                .addResourceClass(TestResource.class);

        JerseyServerSupplier.createServerAndTest(connectorConfigurationBuilder, Server::new, target -> {
            String response = target.path(TestResource.PATH).request()
                    .header("test", RandomString.make(16384 / Character.BYTES)).get().readEntity(String.class);
            assertEquals(TestResource.MESSAGE, response);
        }, port);
    }

    @Test
    public void testPackageScanningConfiguration() throws Exception {
        JerseyConfigurationBuilder configurationBuilder = JerseyConfiguration.builder()
                .addPackage(false, TestResource.class.getPackage().toString());

        JerseyServerSupplier.createServerAndTest(configurationBuilder, target -> {
            String testResourceResponse = target.path(TestResource.PATH).request().get().readEntity(String.class);
            assertEquals(TestResource.MESSAGE, testResourceResponse);

            String pingResourceResponse = target.path(PingResource.PATH).request().get().readEntity(String.class);
            assertEquals(PingResource.MESSAGE, pingResourceResponse);

            int status = target.path(FooResource.PATH).request().get().getStatus();
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), status);
        });
    }

    @Test
    public void testRecursivePackageScanningConfiguration() throws Exception {
        JerseyConfigurationBuilder configurationBuilder = JerseyConfiguration.builder()
                .addPackage(TestResource.class.getPackage().toString());

        JerseyServerSupplier.createServerAndTest(configurationBuilder, target -> {
            String pingResourceResponse = target.path(PingResource.PATH).request().get().readEntity(String.class);
            assertEquals(PingResource.MESSAGE, pingResourceResponse);

            // Try to access resource package that located inside the package specified
            String fooResourceResponse = target.path(FooResource.PATH).request().get().readEntity(String.class);
            assertEquals(FooResource.MESSAGE, fooResourceResponse);
        });
    }

    @Test
    public void testContextPatchConfiguration() throws Exception {
        JerseyConfigurationBuilder configurationBuilder = JerseyConfiguration.builder()
                .withContextPath("resources")
                .addResourceClass(TestResource.class);

        JerseyServerSupplier.createServerAndTest(configurationBuilder, target -> {
            String testResourceResponse = target.path(TestResource.PATH).request().get().readEntity(String.class);
            assertEquals(TestResource.MESSAGE, testResourceResponse);

            // Try to access the resource without the context path
            int port = target.getUri().getPort();
            WebTarget targetWithoutContextRoot = ClientBuilder.newClient().target("http://localhost:" + port);
            int status = targetWithoutContextRoot.path(TestResource.PATH).request().get().getStatus();
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), status);
        });
    }

    @Test
    public void testHostConfiguration() throws Exception {
        int port = FreePortFinder.findFreeLocalPort();
        String address = new Socket("www.google.com", 80).getLocalAddress().getHostAddress();

        JerseyConfigurationBuilder namedHostConfigurationBuilder = JerseyConfiguration.builder()
                .addHost("127.0.0.1", port)
                .addResourceClass(TestResource.class);

        JerseyServerSupplier.createServerAndTest(namedHostConfigurationBuilder, target -> assertNoAccessFromIp(target, address, port));

        JerseyConfigurationBuilder configurationBuilder = JerseyConfiguration.builder()
                .addNamedHost("test-host", "localhost", port)
                .addResourceClass(TestResource.class);

        JerseyServerSupplier.createServerAndTest(configurationBuilder, target -> assertNoAccessFromIp(target, address, port));
    }

    @Test
    public void testServerHandlerCanBeConfiguredFromJettyServerCreator() throws Exception {
        JerseyConfigurationBuilder configurationBuilder = JerseyConfiguration.builder()
                .addResourceClass(TestResource.class);

        StatisticsHandler statisticsHandler = new StatisticsHandler();
        JettyServerCreator jettyServerCreator = () -> {
            Server server = new Server();
            server.setHandler(statisticsHandler);

            return server;
        };

        JerseyServerSupplier.createServerAndTest(configurationBuilder, jettyServerCreator, target -> {
            assertThat(statisticsHandler.getRequests()).isEqualTo(0);

            String testResourceResponse = target.path(TestResource.PATH).request().get().readEntity(String.class);
            assertEquals(TestResource.MESSAGE, testResourceResponse);

            assertThat(statisticsHandler.getRequests()).isEqualTo(1);
        });
    }

    private void assertNoAccessFromIp(WebTarget target, String address, int port) {
        String testResourceResponse = target.path(TestResource.PATH).request().get().readEntity(String.class);
        assertEquals(TestResource.MESSAGE, testResourceResponse);

        WebTarget targetWithIpAddress = ClientBuilder.newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .build()
                .target("http://" + address + ":" + port);
        assertThatThrownBy(() -> targetWithIpAddress.path(TestResource.PATH).request().get()).isInstanceOf(ProcessingException.class);
    }

}
