package io.logz.guice.jersey;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.servlet.ServletModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import org.eclipse.jetty.server.Server;

import java.util.Objects;
import java.util.function.Consumer;

public class JerseyModule extends AbstractModule {

    private final JerseyConfiguration jerseyConfiguration;
    private Consumer<Server> jettyConfigurer;

    public JerseyModule(JerseyConfiguration jerseyConfiguration) {
        this(jerseyConfiguration, server -> {});
    }

    public JerseyModule(JerseyConfiguration jerseyConfiguration, Consumer<Server> jettyConfigurer) {
        this.jerseyConfiguration = Objects.requireNonNull(jerseyConfiguration);
        this.jettyConfigurer = jettyConfigurer;
    }

    protected void configure() {
        Provider<Injector> injectorProvider = getProvider(Injector.class);

        install(new ServletModule());
        bind(JerseyServer.class).toInstance(new JerseyServer(jerseyConfiguration, injectorProvider::get, jettyConfigurer));
        bind(JerseyConfiguration.class).toInstance(jerseyConfiguration);
    }

}
