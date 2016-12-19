package io.logz.guice.jersey;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by Asaf Alima on 19/12/2016.
 */
@Path("test")
public class TestResource {
    static final String MESSAGE = "Hello World!";

    @GET
    public String sayHello() {
        return MESSAGE;
    }

}
