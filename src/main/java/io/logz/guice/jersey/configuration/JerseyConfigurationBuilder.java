package io.logz.guice.jersey.configuration;

import org.glassfish.jersey.server.ResourceConfig;

import javax.servlet.Servlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Asaf Alima on 20/12/2016.
 */
public class JerseyConfigurationBuilder {

    private String contextPath;
    private Set<Class<?>> classes;
    private Map<String, Boolean> packages;
    private ResourceConfig resourceConfig;
    private Set<ServerConnectorConfiguration> connectors;
    private Map<String, Class<? extends Servlet>> servlets;

    JerseyConfigurationBuilder() {
        contextPath = "";
        connectors = new HashSet<>();
        packages = new HashMap<>();
        classes = new HashSet<>();
        servlets = new HashMap<>();
    }

    public JerseyConfigurationBuilder withContextPath(String contextPath) {
        this.contextPath = contextPath;
        return this;
    }

    public JerseyConfigurationBuilder withResourceConfig(ResourceConfig resourceConfig) {
        this.resourceConfig = resourceConfig;
        return this;
    }

    public JerseyConfigurationBuilder withServlet(Class<? extends Servlet> servletClass, String pathSpec) {
        this.servlets.put(pathSpec, servletClass);
        return this;
    }

    public JerseyConfigurationBuilder addPort(int port) {
        connectors.add(new ServerConnectorConfiguration(port));
        return this;
    }

    public JerseyConfigurationBuilder addHost(String host, int port) {
        connectors.add(new ServerConnectorConfiguration(host, port));
        return this;
    }

    public JerseyConfigurationBuilder addNamedHost(String name, String host, int port) {
        connectors.add(new ServerConnectorConfiguration(name, host, port));
        return this;
    }

    public JerseyConfigurationBuilder addPackage(boolean recursive, String packageToScan) {
        packages.put(packageToScan, recursive);
        return this;
    }

    public JerseyConfigurationBuilder addPackage(String packageToScan) {
        return addPackage(true, packageToScan);
    }

    public JerseyConfigurationBuilder addResourceClass(Class<?> resourceClass) {
        classes.add(resourceClass);
        return this;
    }

    public JerseyConfiguration build() {
        if (resourceConfig == null) resourceConfig = new ResourceConfig();
        if (contextPath == null) contextPath = "/";

        resourceConfig.registerClasses(classes);
        packages.forEach((packageToScan, recursive) -> resourceConfig.packages(recursive, packageToScan));

        return new JerseyConfiguration(new ArrayList<>(connectors), servlets, resourceConfig, contextPath);
    }

}
