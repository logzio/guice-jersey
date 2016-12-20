package io.logz.guice.jersey.resources;

import org.glassfish.jersey.server.ManagedAsync;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

/**
 * Created by Asaf Alima on 20/12/2016.
 */
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
