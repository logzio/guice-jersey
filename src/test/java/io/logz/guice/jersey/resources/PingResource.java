package io.logz.guice.jersey.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path(PingResource.PATH)
public class PingResource {

    public static final String PATH = "ping";
    public static final String MESSAGE = "pong";

    @GET
    public String ping() {
        return MESSAGE;
    }

}
