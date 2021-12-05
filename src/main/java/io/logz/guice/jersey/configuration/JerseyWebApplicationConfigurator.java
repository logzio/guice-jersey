package io.logz.guice.jersey.configuration;

import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Implement to provide additional configuration for the underlying {@link org.eclipse.jetty.webapp.WebAppContext},
 * like adding a {@link javax.servlet.Filter} or {@link java.util.EventListener}, etc.
 */
@FunctionalInterface
public interface JerseyWebApplicationConfigurator {
    void configure(WebAppContext webAppContext);
}
