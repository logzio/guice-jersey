package io.logz.guice.jersey;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import io.logz.guice.jersey.configuration.ServerConnectorConfiguration;
import io.logz.guice.jersey.servlet.JerseyServletContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.server.ResourceConfig;
import org.jolokia.http.AgentServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by Asaf Alima on 19/12/2016.
 */
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

        ResourceConfig resourceConfig = jerseyConfiguration.getResourceConfig();
        ServletHolder holder = new ServletHolder(new JerseyServletContainer(resourceConfig, injectorSupplier));

        webAppContext.addServlet(holder, "/*");
        jerseyConfiguration.getServlets().forEach((pathSpec, servlet) ->
                webAppContext.addServlet(servlet, pathSpec));
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

    //TODO:nir: !!! this is not part of the pull request. Only a demonstration of the concept. !!!
    public static void main(String[] args) throws Exception {
        Injector injector = Guice.createInjector();
        JerseyConfiguration jerseyConfiguration = JerseyConfiguration.builder().addHost("localhost", 9999)
                .withServlet(org.jolokia.http.AgentServlet.class, "/jolokia/*")
                .build();
        JerseyServer jerseyServer = new JerseyServer(jerseyConfiguration, () -> injector);
        jerseyServer.start();
    }

}
