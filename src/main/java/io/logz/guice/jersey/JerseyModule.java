package io.logz.guice.jersey;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.servlet.ServletModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Singleton;
import java.util.Objects;

public class JerseyModule extends AbstractModule {

    private final JerseyConfiguration jerseyConfiguration;

    public JerseyModule(JerseyConfiguration jerseyConfiguration) {
        this.jerseyConfiguration = Objects.requireNonNull(jerseyConfiguration);
    }

    protected void configure() {
        Provider<Injector> injectorProvider = getProvider(Injector.class);

        install(new ServletModule());
        bind(ServiceLocator.class).toProvider(ServiceLocatorProvider.class).in(Singleton.class);
        bind(JerseyServer.class).toInstance(new JerseyServer(jerseyConfiguration, injectorProvider::get));
        bind(JerseyConfiguration.class).toInstance(jerseyConfiguration);
    }

}
