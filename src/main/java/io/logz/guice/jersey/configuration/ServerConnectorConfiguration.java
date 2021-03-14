package io.logz.guice.jersey.configuration;

import org.eclipse.jetty.server.HttpConfiguration;

import java.util.Objects;

public class ServerConnectorConfiguration {

    private static final String ALL_INTERFACES_HOST = "0.0.0.0";

    private String name;
    private String host;
    private int port;
    private HttpConfiguration httpConfiguration;

    ServerConnectorConfiguration(int port) {
        this(ALL_INTERFACES_HOST, port);
    }

    ServerConnectorConfiguration(String host, int port) {
        this(String.format("%s-%s", host, port), host, port);
    }

    ServerConnectorConfiguration(int port, HttpConfiguration httpConfiguration) {
        this(String.format("%s-%s", ALL_INTERFACES_HOST, port), ALL_INTERFACES_HOST, port, httpConfiguration);
    }

    ServerConnectorConfiguration(String name, String host, int port){
        this(name, host, port, new HttpConfiguration());
    }

    ServerConnectorConfiguration(String name, String host, int port, HttpConfiguration httpConfiguration) {
        this.name = Objects.requireNonNull(name);
        this.host = Objects.requireNonNull(host);
        this.port = validatePort(port);
        this.httpConfiguration = Objects.requireNonNull(httpConfiguration);
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public HttpConfiguration getHttpConfiguration() {
        return httpConfiguration;
    }

    private int validatePort(int port) {
        if (port < 0) {
            throw new RuntimeException("Port must be greater then zero");
        }

        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerConnectorConfiguration that = (ServerConnectorConfiguration) o;

        return port == that.port &&
                (name != null ? name.equals(that.name) : that.name == null) &&
                (host != null ? host.equals(that.host) : that.host == null);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + port;
        return result;
    }

}
