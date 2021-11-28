package io.logz.guice.jersey.configuration;

import org.eclipse.jetty.webapp.WebAppContext;

@FunctionalInterface
public interface JettyServerConfiguration {
    void webAppContextConfiguration(WebAppContext webAppContext);
}
