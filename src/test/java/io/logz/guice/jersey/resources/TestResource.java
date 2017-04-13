package io.logz.guice.jersey.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path(TestResource.PATH)
public class TestResource {

    public static final String PATH = "test";
    public static final String MESSAGE = "Hello World!";

    @GET
    public String sayHello() {
        return MESSAGE;
    }

}
