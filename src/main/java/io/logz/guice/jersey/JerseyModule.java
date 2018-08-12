package io.logz.guice.jersey;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.servlet.ServletModule;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import org.eclipse.jetty.util.thread.ThreadPool;

import static java.util.Objects.requireNonNull;

public class JerseyModule extends AbstractModule {

    private final JerseyConfiguration jerseyConfiguration;
    private ThreadPool jettyThreadPool;

    public JerseyModule(JerseyConfiguration jerseyConfiguration) {
        this(jerseyConfiguration, null);
    }

    public JerseyModule(JerseyConfiguration jerseyConfiguration, ThreadPool jettyThreadPool) {
        this.jerseyConfiguration = requireNonNull(jerseyConfiguration);
        this.jettyThreadPool = jettyThreadPool;
    }

    protected void configure() {
        Provider<Injector> injectorProvider = getProvider(Injector.class);

        install(new ServletModule());
        bind(JerseyServer.class).toInstance(createNewJerseyServer(injectorProvider));
        bind(JerseyConfiguration.class).toInstance(jerseyConfiguration);
    }

    private JerseyServer createNewJerseyServer(Provider<Injector> injectorProvider) {
        return new JerseyServer(jerseyConfiguration, jettyThreadPool, injectorProvider::get);
    }

}
