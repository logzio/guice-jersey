package io.logz.guice.jersey.resources;

import io.logz.guice.jersey.beans.TestBean;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.Random;

/**
 * Created by Asaf Alima on 20/12/2016.
 */
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
