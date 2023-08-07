package io.logz.guice.jersey.resources;

import org.glassfish.jersey.server.ManagedAsync;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;

@Path(AsyncResource.PATH)
public class AsyncResource {

    public static final String PATH = "async";
    public static final String MESSAGE = "Async Response";

    @GET
    @ManagedAsync
    public void asyncResource(@Suspended AsyncResponse asyncResponse) {
        asyncResponse.resume(MESSAGE);
    }

}
