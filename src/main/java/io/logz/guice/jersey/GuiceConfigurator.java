package io.logz.guice.jersey;

import com.google.inject.Injector;

import javax.websocket.server.ServerEndpointConfig;
import java.util.function.Supplier;

public class GuiceConfigurator extends ServerEndpointConfig.Configurator {

    private final Supplier<Injector> injectorSupplier;

    GuiceConfigurator(Supplier<Injector> injectorSupplier){
        this.injectorSupplier = injectorSupplier;
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        return injectorSupplier.get().getInstance(endpointClass);
    }
}
