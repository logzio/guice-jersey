package io.logz.guice.jersey.configuration;

import java.util.*;

public class WebsocketConfiguration {

    private List<Class<?>> endpointClasses = new ArrayList<>();

    public List<Class<?>> getEndpointClasses() {
        return Collections.unmodifiableList(endpointClasses);
    }

    public WebsocketConfiguration withEndpointClass(Class<?> endpointClass) {
        Objects.requireNonNull(endpointClass);
        endpointClasses.add(endpointClass);
        return this;
    }

    public WebsocketConfiguration withEndpointClasses(Class<?>... endpointClasses) {
        Objects.requireNonNull(endpointClasses);
        for (Class<?> endpointClass : endpointClasses) {
            withEndpointClass(endpointClass);
        }
        return this;
    }

}
