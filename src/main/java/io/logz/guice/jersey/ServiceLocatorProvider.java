package io.logz.guice.jersey;

import com.google.inject.Provider;
import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class ServiceLocatorProvider implements Provider<ServiceLocator> {

    private final AtomicReference<ServiceLocator> serviceLocatorReference = new AtomicReference<>();

    @Override
    public ServiceLocator get() {
        return serviceLocatorReference.get();
    }

    void setServiceLocator(ServiceLocator serviceLocator) {
        serviceLocatorReference.set(serviceLocator);
    }
    
}
