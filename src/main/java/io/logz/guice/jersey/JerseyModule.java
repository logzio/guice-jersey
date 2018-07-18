package io.logz.guice.jersey;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.servlet.ServletModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.JettyConfiguration;

import java.util.Objects;

public class JerseyModule extends AbstractModule {

    private final JerseyConfiguration jerseyConfiguration;
    private JettyConfiguration jettyConfiguration;

    public JerseyModule(JerseyConfiguration jerseyConfiguration) {
        this.jerseyConfiguration = Objects.requireNonNull(jerseyConfiguration);
    }

    public JerseyModule(JerseyConfiguration jerseyConfiguration, JettyConfiguration jettyConfiguration) {
        this.jerseyConfiguration = Objects.requireNonNull(jerseyConfiguration);
        this.jettyConfiguration = Objects.requireNonNull(jettyConfiguration);
    }

    protected void configure() {
        Provider<Injector> injectorProvider = getProvider(Injector.class);

        install(new ServletModule());
        bind(JerseyServer.class).toInstance(createNewJerseyServer(injectorProvider));
        bind(JerseyConfiguration.class).toInstance(jerseyConfiguration);
    }

    private JerseyServer createNewJerseyServer(Provider<Injector> injectorProvider) {
        if (jettyConfiguration == null) {
            return new JerseyServer(jerseyConfiguration, injectorProvider::get);
        } else {
            return new JerseyServer(jerseyConfiguration, jettyConfiguration, injectorProvider::get);
        }
    }

}
