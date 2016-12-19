package io.logz.guice.jersey.servlet;

import com.google.inject.Injector;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.servlet.ServletException;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Created by Asaf Alima on 19/12/2016.
 */
public class JerseyServletContainer extends ServletContainer {

    private final Supplier<Injector> injectorSupplier;

    public JerseyServletContainer(ResourceConfig resourceConfig, Supplier<Injector> injectorSupplier) {
        super(resourceConfig);
        this.injectorSupplier = Objects.requireNonNull(injectorSupplier);
    }

    @Override
    public void init() throws ServletException {
        super.init();
        setupGuiceBridge();
    }

    private void setupGuiceBridge() {
        Injector injector = injectorSupplier.get();
        GuiceBridge gb = GuiceBridge.getGuiceBridge();
        ServiceLocator locator = getApplicationHandler().getServiceLocator();

        gb.initializeGuiceBridge(locator);
        GuiceIntoHK2Bridge guiceBridge = locator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(injector);
    }

}
