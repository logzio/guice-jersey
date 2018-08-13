package io.logz.guice.jersey;

import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.JerseyConfigurationBuilder;
import io.logz.guice.jersey.resources.HeavyResource;
import io.logz.guice.jersey.resources.PingResource;
import io.logz.guice.jersey.resources.TestResource;
import io.logz.guice.jersey.resources.recursive.FooResource;
import io.logz.guice.jersey.supplier.JerseyServerSupplier;
import org.apache.mina.util.AvailablePortFinder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class JerseyServerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyServerTest.class);

    @Test
    public void testBasicConfiguration() throws Exception {
        ResourceConfig resourceConfig = new ResourceConfig().registerClasses(TestResource.class);
        JerseyServerSupplier.createServerAndTest(resourceConfig, target -> {
            String response = target.path(TestResource.PATH).request().get().readEntity(String.class);
            assertEquals(TestResource.MESSAGE, response);
        });
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
        int port = AvailablePortFinder.getNextAvailable();
        String address = InetAddress.getLocalHost().getHostAddress();

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
    public void testJettyThreadPoolConfiguration() throws Exception {

        ResourceConfig resourceConfig = new ResourceConfig().registerClasses(HeavyResource.class);

        int jettyThreadsNum = 6;
        int queueSize = 1;

        BlockingQueue< Runnable > queue = new ArrayBlockingQueue<>(queueSize);
        QueuedThreadPool jettyBoundedThreadPool = new QueuedThreadPool(jettyThreadsNum,
                jettyThreadsNum,
                60000,
                queue);

        JerseyServerSupplier.createServerAndTest(resourceConfig, jettyBoundedThreadPool, target -> {

            int overloadingThreadsNum = 1;
            int threadsNum = queueSize + jettyThreadsNum + overloadingThreadsNum;

            CountDownLatch countDownLatch = new CountDownLatch(threadsNum);
            AtomicInteger successCounter = new AtomicInteger(0);
            AtomicInteger failureCounter = new AtomicInteger(0);

            Stream.generate(() -> new Thread(new ResourceResponseChecker(successCounter,
                                                                         failureCounter,
                                                                         countDownLatch,
                                                                         target)))
                  .limit(threadsNum).forEach(Thread::start);

            countDownLatch.await();

            LOGGER.info("successCounter={}, failureCounter={}", successCounter.get(), failureCounter.get());

            assertTrue(queueSize + jettyThreadsNum >= successCounter.get());
            assertTrue(overloadingThreadsNum <= failureCounter.get());
        });
    }

    private void assertNoAccessFromIp(WebTarget target, String address, int port) {
        String testResourceResponse = target.path(TestResource.PATH).request().get().readEntity(String.class);
        assertEquals(TestResource.MESSAGE, testResourceResponse);

        // Try to access the resource without the context path
        WebTarget targetWithoutContextRoot = ClientBuilder.newClient().target("http://" + address + ":" + port);
        assertThrown(ProcessingException.class, () -> targetWithoutContextRoot.path(TestResource.PATH).request().get());
    }

    private void assertThrown(Class<? extends Throwable> connectExceptionClass, Callable callable) {
        try {
            callable.call();
            fail("Exception was not thrown");
        } catch (Throwable e) {
            assertEquals(connectExceptionClass, e.getClass());
        }
    }

    private class ResourceResponseChecker implements Runnable {
        private final AtomicInteger successCounter;
        private final AtomicInteger failureCounter;
        private final CountDownLatch countDownLatch;
        private final WebTarget target;

        ResourceResponseChecker(AtomicInteger successCounter, AtomicInteger failureCounter, CountDownLatch countDownLatch, WebTarget target) {
            this.successCounter = successCounter;
            this.failureCounter = failureCounter;
            this.countDownLatch = countDownLatch;
            this.target = target;
        }

        @Override
        public void run() {
            try {
                Response response = target.path(HeavyResource.PATH).request().get();
                if (response.getStatus() == 200) {
                    String responseMessage = response.readEntity(String.class);
                    assertEquals(HeavyResource.MESSAGE, responseMessage);
                    successCounter.incrementAndGet();
                } else {
                    LOGGER.error("response status is {}, response message: {}", response.getStatus(), response.readEntity(String.class));
                    failureCounter.incrementAndGet();
                }
            } catch (Throwable t) {
                LOGGER.error("failed to get response from server", t);
                failureCounter.incrementAndGet();
            } finally {
                countDownLatch.countDown();
            }
        }
    }
}
