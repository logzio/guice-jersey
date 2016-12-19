package io.logz.guice.jersey;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import io.logz.guice.jersey.configuration.JerseyModuleConfiguration;
import io.logz.guice.jersey.configuration.ServerConnectorConfiguration;
import io.logz.guice.jersey.servlet.JerseyServletContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
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

    private final JerseyModuleConfiguration jerseyModuleConfiguration;
    private final Supplier<Injector> injectorSupplier;
    private final Server server;

    JerseyServer(JerseyModuleConfiguration jerseyModuleConfiguration,
                 Supplier<Injector> injectorSupplier) {
        this.jerseyModuleConfiguration = jerseyModuleConfiguration;
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
        List<ServerConnectorConfiguration> serverConnectorConfigurations = jerseyModuleConfiguration.getServerConnectors();
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

        ServletHolder holder = new ServletHolder(new JerseyServletContainer(injectorSupplier));
        holder.setInitParameter("javax.ws.rs.Application", jerseyModuleConfiguration.getResourceConfigClass().getName());

        webAppContext.addServlet(holder, "/*");
        webAppContext.setResourceBase("/");
        webAppContext.setContextPath(jerseyModuleConfiguration.getContextRoot());
        webAppContext.addEventListener(new GuiceServletContextListener() {
            @Override
            protected Injector getInjector() {
                return injectorSupplier.get();
            }
        });

        server.setHandler(webAppContext);
    }

}
