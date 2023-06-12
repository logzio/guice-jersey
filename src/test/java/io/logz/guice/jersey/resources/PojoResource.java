package io.logz.guice.jersey.resources;

import io.logz.guice.jersey.beans.TestBean;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;
import java.util.Random;

@Path(PojoResource.PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PojoResource {

    public static final String PATH = "pojo";
    public static final TestBean TEST_BEAN = new TestBean(1, "John Doe", new Date());

    @GET
    public Response get() {
        return Response.ok(TEST_BEAN).build();
    }

    @POST
    public TestBean create(TestBean bean) {
        bean.setId(new Random().nextInt());
        return bean;
    }

}
