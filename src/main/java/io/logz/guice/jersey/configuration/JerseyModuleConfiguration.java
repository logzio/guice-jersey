package io.logz.guice.jersey.configuration;

import org.glassfish.jersey.server.ResourceConfig;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by Asaf Alima on 19/12/2016.
 */
public class JerseyModuleConfiguration {

    private final List<ServerConnectorConfiguration> serverConnectors;
    private final Class<? extends ResourceConfig> resourceConfigClass;
    private final String contextRoot;

    public JerseyModuleConfiguration(int port, Class<? extends ResourceConfig> resourceConfigClass, String contextRoot) {
        this(new ServerConnectorConfiguration(port), resourceConfigClass, contextRoot);
    }

    public JerseyModuleConfiguration(String host, int port, Class<ResourceConfig> resourceConfigClass, String contextRoot) {
        this(new ServerConnectorConfiguration(host, port), resourceConfigClass, contextRoot);
    }

    public JerseyModuleConfiguration(ServerConnectorConfiguration serverConnectorConfiguration,
                                     Class<? extends ResourceConfig> resourceConfigClass,
                                     String contextRoot) {
        this(Collections.singletonList(serverConnectorConfiguration), resourceConfigClass, contextRoot);
    }

    public JerseyModuleConfiguration(List<ServerConnectorConfiguration> serverConnectors,
                                     Class<? extends ResourceConfig> resourceConfigClass,
                                     String contextRoot) {
        this.serverConnectors = Objects.requireNonNull(serverConnectors);
        this.resourceConfigClass = Objects.requireNonNull(resourceConfigClass);
        this.contextRoot = Objects.requireNonNull(contextRoot);

        if (serverConnectors.size() == 0) {
            throw new RuntimeException("Must supply at least one server connector");
        }
    }

    public List<ServerConnectorConfiguration> getServerConnectors() {
        return serverConnectors;
    }

    public Class<? extends ResourceConfig> getResourceConfigClass() {
        return resourceConfigClass;
    }

    public String getContextRoot() {
        return contextRoot;
    }

}
