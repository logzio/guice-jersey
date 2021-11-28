package io.logz.guice.jersey;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.servlet.ServletModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.JettyServerConfiguration;
import org.eclipse.jetty.server.Server;

import java.util.Objects;

public class JerseyModule extends AbstractModule {

    private final JerseyConfiguration jerseyConfiguration;
    private final JettyServerCreator jettyServerCreator;
    private final JettyServerConfiguration jettyServerConfiguration;

    public JerseyModule(JerseyConfiguration jerseyConfiguration) {
        this(jerseyConfiguration, Server::new);
    }

    public JerseyModule(JerseyConfiguration jerseyConfiguration, JettyServerCreator jettyServerCreator) {
        this(jerseyConfiguration, jettyServerCreator, null);
    }

    public JerseyModule(JerseyConfiguration jerseyConfiguration, JettyServerCreator jettyServerCreator, JettyServerConfiguration jettyServerConfiguration) {
        this.jerseyConfiguration = Objects.requireNonNull(jerseyConfiguration);
        this.jettyServerCreator = Objects.requireNonNull(jettyServerCreator);
        this.jettyServerConfiguration = jettyServerConfiguration;
    }

    protected void configure() {
        Provider<Injector> injectorProvider = getProvider(Injector.class);

        install(new ServletModule());
        bind(JerseyServer.class).toInstance(new JerseyServer(jerseyConfiguration, injectorProvider::get, jettyServerCreator, jettyServerConfiguration));
        bind(JerseyConfiguration.class).toInstance(jerseyConfiguration);
    }

}
