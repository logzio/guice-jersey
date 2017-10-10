package io.logz.guice.jersey;

import io.logz.guice.jersey.resources.SseResource;
import io.logz.guice.jersey.supplier.JerseyServerSupplier;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.sse.SseEventSource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ServerSentEventsTest {

    @Test
    public void testEventSource() throws Exception {
        ResourceConfig resourceConfig = new ResourceConfig().registerClasses(SseResource.class);
        JerseyServerSupplier.createServerAndTest(resourceConfig, target -> {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicReference<String> receivedMessage = new AtomicReference<>();
            SseEventSource eventSource = SseEventSource.target(target.path(SseResource.PATH)).build();
            eventSource.register((inboundEvent) -> {
                receivedMessage.set(inboundEvent.readData());
                latch.countDown();
            });

            eventSource.open();
            target.path(SseResource.PATH).request().post(Entity.text("message"));

            try {
                assertThat(latch.await(30, TimeUnit.SECONDS)).isTrue()
                        .withFailMessage("Waiting for message to be delivered has timed out.");
            } finally {
                eventSource.close();
            }

            assertThat(receivedMessage.get()).isEqualTo("message")
                    .withFailMessage("Unexpected SSE event data value.");
        });
    }

}
