package io.logz.guice.jersey;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.servlet.ServletModule;
import io.logz.guice.jersey.configuration.JerseyModuleConfiguration;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Created by Asaf Alima on 19/12/2016.
 */
public class JerseyModule extends AbstractModule {

    private final JerseyModuleConfiguration jerseyModuleConfiguration;
    private final Supplier<Injector> injectorSupplier;

    public JerseyModule(JerseyModuleConfiguration jerseyModuleConfiguration, Supplier<Injector> injectorSupplier) {
        this.jerseyModuleConfiguration = Objects.requireNonNull(jerseyModuleConfiguration);
        this.injectorSupplier = Objects.requireNonNull(injectorSupplier);
    }

    protected void configure() {
        install(new ServletModule());
        bind(JerseyServer.class).toInstance(new JerseyServer(jerseyModuleConfiguration, injectorSupplier));
    }

}
