package io.logz.guice.jersey;

import org.eclipse.jetty.server.Server;

public interface JettyServerCreator {
    Server create();
}
