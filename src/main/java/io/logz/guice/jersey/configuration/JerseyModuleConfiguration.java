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
    private final ResourceConfig resourceConfig;
    private final String contextRoot;

    public JerseyModuleConfiguration(int port, ResourceConfig resourceConfig, String contextRoot) {
        this(new ServerConnectorConfiguration(port), resourceConfig, contextRoot);
    }

    public JerseyModuleConfiguration(String host, int port, ResourceConfig resourceConfig, String contextRoot) {
        this(new ServerConnectorConfiguration(host, port), resourceConfig, contextRoot);
    }

    public JerseyModuleConfiguration(ServerConnectorConfiguration serverConnectorConfiguration,
                                     ResourceConfig resourceConfig,
                                     String contextRoot) {
        this(Collections.singletonList(serverConnectorConfiguration), resourceConfig, contextRoot);
    }

    public JerseyModuleConfiguration(List<ServerConnectorConfiguration> serverConnectors,
                                     ResourceConfig resourceConfig,
                                     String contextRoot) {
        this.serverConnectors = Objects.requireNonNull(serverConnectors);
        this.resourceConfig = Objects.requireNonNull(resourceConfig);
        this.contextRoot = Objects.requireNonNull(contextRoot);

        if (serverConnectors.size() == 0) {
            throw new RuntimeException("Must supply at least one server connector");
        }
    }

    public List<ServerConnectorConfiguration> getServerConnectors() {
        return serverConnectors;
    }

    public ResourceConfig getResourceConfig() {
        return resourceConfig;
    }

    public String getContextRoot() {
        return contextRoot;
    }

}
