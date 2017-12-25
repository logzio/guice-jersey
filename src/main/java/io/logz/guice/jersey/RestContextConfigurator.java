package io.logz.guice.jersey;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Supplier;

class RestContextConfigurator extends AbstractContextConfigurator {

    RestContextConfigurator(Server server, Supplier<Injector> injectorSupplier){
        super(server, injectorSupplier);
    }

    Optional<ContextHandler> configure(JerseyConfiguration jerseyConfiguration) {
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
        return Optional.of(webAppContext);
    }
}
