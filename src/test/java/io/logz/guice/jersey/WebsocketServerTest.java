package io.logz.guice.jersey;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.JerseyConfigurationBuilder;
import io.logz.guice.jersey.configuration.WebsocketConfiguration;
import io.logz.guice.jersey.resources.TestResource;
import io.logz.guice.jersey.resources.TestSocket;
import io.logz.guice.jersey.supplier.WebsocketServerSupplier;
import org.junit.Test;

import javax.websocket.Session;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

public class WebsocketServerTest {

    @Test
    public void testContextPathConfiguration() throws Exception {
        JerseyConfigurationBuilder configurationBuilder = JerseyConfiguration.builder()
                .withContextPath("resources")
                .addResourceClass(TestResource.class);

        WebsocketConfiguration websocketConfiguration = new WebsocketConfiguration()
                .withEndpointClass(TestSocket.class);

        String message = "hello world";
        AtomicBoolean called = new AtomicBoolean();

        new WebsocketServerSupplier(configurationBuilder, websocketConfiguration)
                .createServerAndTest(
                        createTester(
                                session -> session.getBasicRemote().sendText(message),
                                new AbstractModule() {
                                    @Override
                                    protected void configure() {
                                        bind(TestSocket.SocketCallback.class).toInstance(s -> {
                                            assertThat(s).isEqualTo(message);
                                            called.set(true);
                                        });
                                    }
                                }));

        assertThat(called).isTrue();
    }

    private WebsocketServerSupplier.Tester createTester(Callable consumer, Module module) {
        return new WebsocketServerSupplier.Tester() {
            @Override
            public void test(Session session) throws Exception {
                consumer.test(session);
            }

            @Override
            public Module getTestModule() {
                return module;
            }
        };
    }

    private interface Callable {
        void test(Session session) throws Exception;
    }
}
