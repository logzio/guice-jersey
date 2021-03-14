package io.logz.guice.jersey.configuration;

import org.eclipse.jetty.server.HttpConfiguration;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JerseyConfigurationBuilder {

    private String contextPath;
    private Set<Class<?>> classes;
    private Map<String, Boolean> packages;
    private ResourceConfig resourceConfig;
    private Set<ServerConnectorConfiguration> connectors;

    JerseyConfigurationBuilder() {
        contextPath = "";
        connectors = new HashSet<>();
        packages = new HashMap<>();
        classes = new HashSet<>();
    }

    public JerseyConfigurationBuilder withContextPath(String contextPath) {
        this.contextPath = contextPath;
        return this;
    }

    public JerseyConfigurationBuilder withResourceConfig(ResourceConfig resourceConfig) {
        this.resourceConfig = resourceConfig;
        return this;
    }

    public JerseyConfigurationBuilder addPort(int port) {
        connectors.add(new ServerConnectorConfiguration(port));
        return this;
    }

    public JerseyConfigurationBuilder addPortWithHttpConfiguration(int port, HttpConfiguration httpConfiguration) {
        connectors.add(new ServerConnectorConfiguration(port, httpConfiguration));
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

    public JerseyConfigurationBuilder registerClasses(Class<?> clazz) {
        classes.add(clazz);
        return this;
    }

    public JerseyConfiguration build() {
        if (resourceConfig == null) resourceConfig = new ResourceConfig();
        if (contextPath == null) contextPath = "/";

        resourceConfig.registerClasses(classes);
        packages.forEach((packageToScan, recursive) -> resourceConfig.packages(recursive, packageToScan));

        return new JerseyConfiguration(new ArrayList<>(connectors), resourceConfig, contextPath);
    }

}
