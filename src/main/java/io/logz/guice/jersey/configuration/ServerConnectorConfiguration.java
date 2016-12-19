package io.logz.guice.jersey.configuration;

import java.util.Objects;

/**
 * Created by Asaf Alima on 19/12/2016.
 */
public class ServerConnectorConfiguration {

    private static final String ALL_INTERFACES_HOST = "0.0.0.0";

    private String name;
    private String host;
    private int port;

    public ServerConnectorConfiguration(int port) {
        this(ALL_INTERFACES_HOST, port);
    }

    public ServerConnectorConfiguration(String host, int port) {
        this(String.format("%s-%s", host, port), host, port);
    }

    public ServerConnectorConfiguration(String name, String host, int port) {
        this.name = Objects.requireNonNull(name);
        this.host = Objects.requireNonNull(host);
        this.port = validatePort(port);
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

    private int validatePort(int port) {
        if (port < 0) {
            throw new RuntimeException("Port must be greater then zero");
        }

        return port;
    }

}
