package io.logz.guice.jersey.resources.recursive;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path(FooResource.PATH)
public class FooResource {

    public static final String PATH = "foo";
    public static final String MESSAGE = "bar";

    @GET
    public String bar() {
        return MESSAGE;
    }

}
