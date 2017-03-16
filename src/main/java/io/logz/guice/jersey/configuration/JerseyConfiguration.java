package io.logz.guice.jersey.configuration;

import org.glassfish.jersey.server.ResourceConfig;

import javax.servlet.Servlet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Asaf Alima on 19/12/2016.
 */
public class JerseyConfiguration {

    private final List<ServerConnectorConfiguration> serverConnectors;
    private final Map<String, Class<? extends Servlet>> servlets;
    private final ResourceConfig resourceConfig;
    private final String contextPath;

    public static JerseyConfigurationBuilder builder() {
        return new JerseyConfigurationBuilder();
    }

    JerseyConfiguration(List<ServerConnectorConfiguration> serverConnectors,
                        Map<String, Class<? extends Servlet>> servlets,
                        ResourceConfig resourceConfig,
                        String contextPath) {
        this.serverConnectors = Objects.requireNonNull(serverConnectors);
        this.resourceConfig = Objects.requireNonNull(resourceConfig);
        this.contextPath = appendLeadingSlashIfMissing(contextPath);
        this.servlets = Objects.requireNonNull(servlets);

        if (serverConnectors.size() == 0) {
            throw new RuntimeException("Must supply at least one server connector");
        }
    }

    public List<ServerConnectorConfiguration> getServerConnectors() {
        return serverConnectors;
    }

    public Map<String, Class<? extends Servlet>> getServlets() {
        return servlets;
    }

    public ResourceConfig getResourceConfig() {
        return resourceConfig;
    }

    public String getContextPath() {
        return contextPath;
    }

    private String appendLeadingSlashIfMissing(String contextRoot) {
        contextRoot = Objects.requireNonNull(contextRoot);
        if (!contextRoot.startsWith("/")) {
            contextRoot = "/" + contextRoot;
        }

        return contextRoot;
    }
}
