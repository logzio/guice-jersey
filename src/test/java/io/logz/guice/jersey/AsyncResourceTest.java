package io.logz.guice.jersey;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.logz.guice.jersey.resources.AsyncResource;
import io.logz.guice.jersey.supplier.JerseyServerSupplier;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Asaf Alima on 20/12/2016.
 */
public class AsyncResourceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncResourceTest.class);

    @Test
    public void testAsyncRequestWorks() throws Exception {
        ResourceConfig resourceConfig = new ResourceConfig().registerClasses(AsyncResource.class);
        JerseyServerSupplier.createServerAndTest(resourceConfig, target -> {
            String response = target.path(AsyncResource.PATH).request().get().readEntity(String.class);
            assertEquals(AsyncResource.MESSAGE, response);
        });
    }

    @Test
    public void testMultiRequests() throws Exception {
        final int NUM_OF_REQUESTS = 100;
        ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
                .setNameFormat("async-test-%02d").build());

        ResourceConfig resourceConfig = new ResourceConfig().registerClasses(AsyncResource.class);
        JerseyServerSupplier.createServerAndTest(resourceConfig, target -> {
            try {
                WebTarget resourceTarget = target.path(AsyncResource.PATH);
                Map<Integer, String> sendValuesResponses = sendValues(executor, resourceTarget, NUM_OF_REQUESTS);
                Map<Integer, String> getValuesResponses = getValues(executor, resourceTarget, NUM_OF_REQUESTS);

                assertEquals(sendValuesResponses, getValuesResponses);
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail("Error while testing async: " + e.getMessage());
            }
        });
    }

    private Map<Integer, String> sendValues(ExecutorService executor, WebTarget target, int numOfRequests) throws InterruptedException {
        Map<Integer, String> responses = new ConcurrentHashMap<>(numOfRequests);
        CountDownLatch latch = new CountDownLatch(numOfRequests);

        for (int i = 0; i < numOfRequests; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    int numOfAttempts = 0;
                    while (true) {
                        numOfAttempts++;
                        try {
                            String response = target.request().post(Entity.entity(id, MediaType.TEXT_PLAIN_TYPE), String.class);
                            responses.put(id, response);
                            break;
                        } catch (Throwable t) {
                            LOGGER.warn("Error sending post request <{}> for {}. time", id, numOfAttempts, t);
                        }
                        if (numOfAttempts > 3) {
                            break;
                        }
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        return responses;
    }

    private Map<Integer, String> getValues(ExecutorService executor, WebTarget target, int numOfRequests) throws InterruptedException {
        Map<Integer, String> responses = new ConcurrentHashMap<>(numOfRequests);
        CountDownLatch latch = new CountDownLatch(numOfRequests);

        for (int i = 0; i < numOfRequests; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    int numOfAttempts = 0;
                    while (true) {
                        numOfAttempts++;
                        try {
                            String response = target.path(String.valueOf(id)).request().get(String.class);
                            responses.put(id, response);
                            break;
                        } catch (Throwable t) {
                            LOGGER.warn("Error sending get request <{}> for {}. time", id, numOfAttempts, t);
                        }
                        if (numOfAttempts > 3) {
                            break;
                        }
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        return responses;
    }

}
