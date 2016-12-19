package io.logz.guice.jersey.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by Asaf Alima on 19/12/2016.
 */
@Path("test")
public class TestResource {

    public static final String MESSAGE = "Hello World!";

    @GET
    public String sayHello() {
        return MESSAGE;
    }

}
