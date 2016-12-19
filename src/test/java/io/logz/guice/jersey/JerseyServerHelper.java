package io.logz.guice.jersey;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.logz.guice.jersey.configuration.JerseyModuleConfiguration;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Asaf Alima on 19/12/2016.
 */
public class JerseyServerHelper {

    public static JerseyServer createServer(int port, ResourceConfig resourceConfig) {
        JerseyModuleConfiguration jerseyModuleConfiguration = new JerseyModuleConfiguration(port, resourceConfig, "/resources");
        AtomicReference<Injector> injectorRef = new AtomicReference<>();

        List<Module> modules = new ArrayList<>();
        modules.add(new JerseyModule(jerseyModuleConfiguration, injectorRef::get));

        Injector injector = Guice.createInjector(modules);
        injectorRef.set(injector);

        return injector.getInstance(JerseyServer.class);
    }

}
