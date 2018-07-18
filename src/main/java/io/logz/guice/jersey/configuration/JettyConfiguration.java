package io.logz.guice.jersey.configuration;

public class JettyConfiguration {

    private int maxThreads;
    private int minThreads;
    private int idleTimeout;
    private int maxQueueSize;

    public JettyConfiguration(int minThreads, int maxThreads, int idleTimeout, int maxQueueSize) {
        this.minThreads = minThreads;
        this.maxThreads = maxThreads;
        this.idleTimeout = idleTimeout;
        this.maxQueueSize = maxQueueSize;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public int getMinThreads() {
        return minThreads;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    @Override
    public String toString() {
        return "JettyConfiguration{" +
                "maxThreads=" + maxThreads +
                ", minThreads=" + minThreads +
                ", idleTimeout=" + idleTimeout +
                ", maxQueueSize=" + maxQueueSize +
                '}';
    }
}
