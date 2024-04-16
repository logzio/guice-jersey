package io.logz.guice.jersey.configuration;

import org.eclipse.jetty.ee10.webapp.WebAppContext;

/**
 * Implement to provide additional configuration for the underlying {@link org.eclipse.jetty.ee10.webapp.WebAppContext},
 * like adding a {@link jakarta.servlet.Filter} or {@link java.util.EventListener}, etc.
 */
@FunctionalInterface
public interface JerseyWebApplicationConfigurator {
    void configure(WebAppContext webAppContext);
}
