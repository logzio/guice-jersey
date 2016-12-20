package io.logz.guice.jersey.configuration;

import org.glassfish.jersey.server.ResourceConfig;

import java.util.List;
import java.util.Objects;

/**
 * Created by Asaf Alima on 19/12/2016.
 */
public class JerseyConfiguration {

    private final List<ServerConnectorConfiguration> serverConnectors;
    private final ResourceConfig resourceConfig;
    private final String contextRoot;

    public static JerseyConfigurationBuilder builder() {
        return new JerseyConfigurationBuilder();
    }

    JerseyConfiguration(List<ServerConnectorConfiguration> serverConnectors,
                        ResourceConfig resourceConfig,
                        String contextRoot) {
        this.serverConnectors = Objects.requireNonNull(serverConnectors);
        this.resourceConfig = Objects.requireNonNull(resourceConfig);
        this.contextRoot = appendLeadingSlashIfMissing(contextRoot);

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

    private String appendLeadingSlashIfMissing(String contextRoot) {
        contextRoot = Objects.requireNonNull(contextRoot);
        if (!contextRoot.startsWith("/")) {
            contextRoot = "/" + contextRoot;
        }

        return contextRoot;
    }

}
