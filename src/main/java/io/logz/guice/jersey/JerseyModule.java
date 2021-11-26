package io.logz.guice.jersey;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.servlet.ServletModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import org.eclipse.jetty.server.Server;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JerseyModule extends AbstractModule {

    private final JerseyConfiguration jerseyConfiguration;
    private final JettyServerCreator jettyServerCreator;
    private final List<JettyFilterDefinition> jettyFilterDefinitions;

    public JerseyModule(JerseyConfiguration jerseyConfiguration) {
        this(jerseyConfiguration, Server::new);
    }

    public JerseyModule(JerseyConfiguration jerseyConfiguration, JettyServerCreator jettyServerCreator) {
        this(jerseyConfiguration, jettyServerCreator, Collections.emptyList());
    }

    public JerseyModule(JerseyConfiguration jerseyConfiguration, JettyServerCreator jettyServerCreator, List<JettyFilterDefinition> jettyFilterDefinitions) {
        this.jerseyConfiguration = Objects.requireNonNull(jerseyConfiguration);
        this.jettyServerCreator = Objects.requireNonNull(jettyServerCreator);
        this.jettyFilterDefinitions = Objects.requireNonNull(jettyFilterDefinitions);
    }

    protected void configure() {
        Provider<Injector> injectorProvider = getProvider(Injector.class);

        install(new ServletModule());
        bind(JerseyServer.class).toInstance(new JerseyServer(jerseyConfiguration, injectorProvider::get, jettyServerCreator, jettyFilterDefinitions));
        bind(JerseyConfiguration.class).toInstance(jerseyConfiguration);
    }

}
