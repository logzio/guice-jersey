package io.logz.guice.jersey;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.servlet.ServletModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.JerseyWebApplicationConfigurator;
import org.eclipse.jetty.server.Server;

import java.util.Objects;

public class JerseyModule extends AbstractModule {

    private final JerseyConfiguration jerseyConfiguration;
    private final JettyServerCreator jettyServerCreator;
    private final JerseyWebApplicationConfigurator jerseyWebApplicationConfigurator;

    public JerseyModule(JerseyConfiguration jerseyConfiguration) {
        this(jerseyConfiguration, Server::new);
    }

    public JerseyModule(JerseyConfiguration jerseyConfiguration, JettyServerCreator jettyServerCreator) {
        this(jerseyConfiguration, jettyServerCreator, null);
    }

    public JerseyModule(JerseyConfiguration jerseyConfiguration, JettyServerCreator jettyServerCreator, JerseyWebApplicationConfigurator jerseyWebApplicationConfigurator) {
        this.jerseyConfiguration = Objects.requireNonNull(jerseyConfiguration);
        this.jettyServerCreator = Objects.requireNonNull(jettyServerCreator);
        this.jerseyWebApplicationConfigurator = jerseyWebApplicationConfigurator;
    }

    protected void configure() {
        Provider<Injector> injectorProvider = getProvider(Injector.class);

        install(new ServletModule());
        bind(JerseyServer.class).toInstance(new JerseyServer(jerseyConfiguration, injectorProvider::get, jettyServerCreator, jerseyWebApplicationConfigurator));
        bind(JerseyConfiguration.class).toInstance(jerseyConfiguration);
    }

}
