package io.logz.guice.jersey.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path(HeavyResource.PATH)
public class HeavyResource {

    public static final String PATH = "heavy";
    public static final String MESSAGE = "Hello World!";
    public static final long DELAY = 1000;

    @GET
    public String sayHelloWithLongDelay() throws InterruptedException {
        Thread.sleep(DELAY);
        return MESSAGE;
    }
}
