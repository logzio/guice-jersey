package io.logz.guice.jersey;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.servlet.ServletModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;

import java.util.Objects;

public class JerseyModule extends AbstractModule {

    private final JerseyConfiguration jerseyConfiguration;

    public JerseyModule(JerseyConfiguration jerseyConfiguration) {
        this.jerseyConfiguration = Objects.requireNonNull(jerseyConfiguration);
    }

    protected void configure() {
        Provider<Injector> injectorProvider = getProvider(Injector.class);

        install(new ServletModule());
        bind(JerseyServer.class).toInstance(new JerseyServer(jerseyConfiguration, injectorProvider::get));
    }

}
