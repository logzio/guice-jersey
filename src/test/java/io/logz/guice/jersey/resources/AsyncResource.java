package io.logz.guice.jersey.resources;

import org.glassfish.jersey.server.ManagedAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Asaf Alima on 20/12/2016.
 */
@Path(AsyncResource.PATH)
public class AsyncResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncResource.class);
    private static final Map<Integer, String> VALUES = new ConcurrentHashMap<>();

    public static final String PATH = "async";
    public static final String MESSAGE = "Async Response";

    @GET
    @ManagedAsync
    public void asyncResource(@Suspended AsyncResponse asyncResponse) {
        asyncResponse.resume(MESSAGE);
    }

    @GET
    @Path("{id}")
    @ManagedAsync
    public void get(@Suspended AsyncResponse asyncResponse, @PathParam("id") int id) {
        String value = VALUES.get(id);
        LOGGER.info("Got value: {} for id: {}", value, id);
        asyncResponse.resume(value);
    }

    @POST
    @ManagedAsync
    public void create(@Suspended AsyncResponse asyncResponse, int id) {
        String value = UUID.randomUUID().toString();
        VALUES.put(id, value);
        LOGGER.info("Putting value: {} for id: {}", value, id);
        asyncResponse.resume(value);
    }

}
