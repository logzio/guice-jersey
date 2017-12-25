package io.logz.guice.jersey;

import com.google.inject.Injector;
import org.eclipse.jetty.server.Server;

import java.util.function.Supplier;

class AbstractContextConfigurator {

    protected final Server server;
    protected final Supplier<Injector> injectorSupplier;

    AbstractContextConfigurator(Server server, Supplier<Injector> injectorSupplier) {
        this.server = server;
        this.injectorSupplier = injectorSupplier;
    }

}
