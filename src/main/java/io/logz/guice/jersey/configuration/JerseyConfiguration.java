package io.logz.guice.jersey.configuration;

import org.glassfish.jersey.server.ResourceConfig;

import java.util.List;
import java.util.Objects;

public class JerseyConfiguration {

    private final List<ServerConnectorConfiguration> serverConnectors;
    private final ResourceConfig resourceConfig;
    private final String contextPath;
    private final String threadsNamePrefix;

    public static JerseyConfigurationBuilder builder() {
        return new JerseyConfigurationBuilder();
    }

    JerseyConfiguration(List<ServerConnectorConfiguration> serverConnectors,
                        ResourceConfig resourceConfig,
                        String contextPath,
                        String threadsNamePrefix) {
        this.serverConnectors = Objects.requireNonNull(serverConnectors);
        this.resourceConfig = Objects.requireNonNull(resourceConfig);
        this.contextPath = appendLeadingSlashIfMissing(contextPath);
        this.threadsNamePrefix = threadsNamePrefix;

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

    public String getContextPath() {
        return contextPath;
    }

    public String getThreadsNamePrefix() {
        return threadsNamePrefix;
    }

    private String appendLeadingSlashIfMissing(String contextRoot) {
        contextRoot = Objects.requireNonNull(contextRoot);
        if (!contextRoot.startsWith("/")) {
            contextRoot = "/" + contextRoot;
        }

        return contextRoot;
    }
}
