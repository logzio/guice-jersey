package io.logz.guice.jersey;

import io.logz.guice.jersey.beans.TestBean;
import io.logz.guice.jersey.resources.PojoResource;
import io.logz.guice.jersey.supplier.JerseyServerSupplier;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JacksonFeatureTest {

    @Test
    public void testGetObject() throws Exception {
        ResourceConfig resourceConfig = new ResourceConfig().registerClasses(PojoResource.class);
        JerseyServerSupplier.createServerAndTest(resourceConfig, target -> {
            TestBean response = target.path(PojoResource.PATH).request().get(TestBean.class);
            assertEquals(PojoResource.TEST_BEAN, response);
        });
    }

    @Test
    public void testSendObject() throws Exception {
        ResourceConfig resourceConfig = new ResourceConfig().registerClasses(PojoResource.class);
        JerseyServerSupplier.createServerAndTest(resourceConfig, target -> {
            TestBean beanToSend = createBeanToSend();

            TestBean response = target.path(PojoResource.PATH).request()
                    .post(Entity.entity(beanToSend, MediaType.APPLICATION_JSON_TYPE))
                    .readEntity(TestBean.class);

            assertEquals(beanToSend.getName(), response.getName());
            assertEquals(beanToSend.getCreated(), response.getCreated());
            assertNotNull(beanToSend.getId());
        });
    }

    private TestBean createBeanToSend() {
        TestBean beanToSend = new TestBean();
        beanToSend.setId(99);
        beanToSend.setName("Jane Doe");
        beanToSend.setCreated(new Date());

        return beanToSend;
    }

}
