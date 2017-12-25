package io.logz.guice.jersey;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.servlet.ServletModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.WebsocketConfiguration;

import java.util.Objects;

public class JerseyModule extends AbstractModule {

    private final JerseyConfiguration jerseyConfiguration;
    private final WebsocketConfiguration websocketConfiguration;

    /**
     * Creates a module that enables the REST resources.
     *
     * @param jerseyConfiguration the config, not null.
     * @throws NullPointerException if jerseyConfiguration is null.
     */
    public JerseyModule(JerseyConfiguration jerseyConfiguration) {
        this(jerseyConfiguration, null);
    }

    /**
     * Creates a module that enables the REST resources and the websocket feature.
     *
     * @param jerseyConfiguration    the config, not null.
     * @param websocketConfiguration the websocket configuration. Can be null, in which case websockets will be disabled.
     * @throws NullPointerException if jerseyConfiguration is null.
     */
    public JerseyModule(JerseyConfiguration jerseyConfiguration, WebsocketConfiguration websocketConfiguration) {
        this.jerseyConfiguration = Objects.requireNonNull(jerseyConfiguration);
        this.websocketConfiguration = websocketConfiguration;
    }

    protected void configure() {
        Provider<Injector> injectorProvider = getProvider(Injector.class);

        install(new ServletModule());
        bind(JerseyServer.class).toInstance(new JerseyServer(
                jerseyConfiguration, websocketConfiguration, injectorProvider::get));
        bind(JerseyConfiguration.class).toInstance(jerseyConfiguration);
        if (websocketConfiguration != null) bind(WebsocketConfiguration.class).toInstance(websocketConfiguration);
    }

}
