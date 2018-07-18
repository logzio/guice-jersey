package io.logz.guice.jersey;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.JettyConfiguration;
import io.logz.guice.jersey.configuration.ServerConnectorConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

public class JerseyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyServer.class);

    private final JerseyConfiguration jerseyConfiguration;
    private final Supplier<Injector> injectorSupplier;
    private final Server server;

    JerseyServer(JerseyConfiguration jerseyConfiguration,
                 Supplier<Injector> injectorSupplier) {
        this.jerseyConfiguration = jerseyConfiguration;
        this.injectorSupplier = injectorSupplier;
        this.server = new Server();

        configureServer();
    }

    JerseyServer(JerseyConfiguration jerseyConfiguration,
                 JettyConfiguration jettyConfiguration,
                 Supplier<Injector> injectorSupplier) {
        this.jerseyConfiguration = jerseyConfiguration;
        this.injectorSupplier = injectorSupplier;
        validate(jettyConfiguration);

        BlockingQueue< Runnable > queue = new ArrayBlockingQueue<>(jettyConfiguration.getMaxQueueSize());
        QueuedThreadPool threadPool = new QueuedThreadPool(jettyConfiguration.getMaxThreads(),
                                                           jettyConfiguration.getMinThreads(),
                                                           jettyConfiguration.getIdleTimeout(),
                                                           queue);
        LOGGER.info("Creating embedded jetty server with bounded requests queue. Config {}", jettyConfiguration);

        this.server = new Server(threadPool);

        configureServer();
    }

    public void start() throws Exception {
        LOGGER.info("Starting embedded jetty server");
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
        LOGGER.info("Embedded jetty server stopped");
    }

    private void configureServer() {
        List<ServerConnectorConfiguration> serverConnectorConfigurations = jerseyConfiguration.getServerConnectors();
        serverConnectorConfigurations.forEach(configuration -> {
            ServerConnector connector = new ServerConnector(server);
            connector.setName(configuration.getName());
            connector.setHost(configuration.getHost());
            connector.setPort(configuration.getPort());
            server.addConnector(connector);
        });

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setServer(server);

        webAppContext.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));

        ServletHolder holder = new ServletHolder(ServletContainer.class);
        holder.setInitParameter("javax.ws.rs.Application", GuiceJerseyResourceConfig.class.getName());

        webAppContext.addServlet(holder, "/*");
        webAppContext.setResourceBase("/");
        webAppContext.setContextPath(jerseyConfiguration.getContextPath());
        webAppContext.addEventListener(new GuiceServletContextListener() {
            @Override
            protected Injector getInjector() {
                return injectorSupplier.get();
            }
        });

        server.setHandler(webAppContext);
    }

    private void validate(JettyConfiguration jettyConfiguration) {
        if (jettyConfiguration.getMinThreads() < 1) {
            throw new ConfigurationException(String.format("minThreads (%s) must be bigger than 0", jettyConfiguration.getMinThreads()));
        }
        if (jettyConfiguration.getMaxThreads() < jettyConfiguration.getMinThreads()) {
            throw new ConfigurationException(String.format("maxThreads (%s) must be bigger than minThreads (%s)", jettyConfiguration.getMaxThreads(), jettyConfiguration.getMinThreads()));
        }
        if (jettyConfiguration.getIdleTimeout() < 0) {
            throw new ConfigurationException(String.format("idleTimeout (%s) must be 0 or bigger", jettyConfiguration.getIdleTimeout()));
        }
        if (jettyConfiguration.getMaxQueueSize() > jettyConfiguration.getMaxThreads()) {
            LOGGER.warn("maxQueueSize ({}) is bigger than the maxThreads ({}) - this may lead to server hanging onto queued requests (potentially long after the client has timed out)",
                    jettyConfiguration.getMaxQueueSize(), jettyConfiguration.getMaxThreads());
        }
    }

    private class ConfigurationException extends RuntimeException {
        private ConfigurationException(String s) {
            super(s);
        }
    }

}
