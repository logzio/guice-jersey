package io.logz.guice.jersey.resource;

import org.jolokia.http.AgentServlet;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.Enumeration;

@Singleton
@Path("jolokia")
public class JolokiaResource {

    private final AgentServlet agentServlet;

    @Context
    private ServletContext context;

    public JolokiaResource() {
        this.agentServlet = new AgentServlet();
    }

    @PostConstruct
    public void init() {
        try {
            agentServlet.init(new ServletConfig() {
                @Override
                public String getServletName() {
                    return "Jolokia";
                }

                @Override
                public ServletContext getServletContext() {
                    return context;
                }

                @Override
                public String getInitParameter(String s) {
                    return getServletContext().getInitParameter(s);
                }

                @Override
                public Enumeration<String> getInitParameterNames() {
                    return getServletContext().getInitParameterNames();
                }
            });
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void destroy() {
        agentServlet.destroy();
    }

    @GET
    @Path("/{sub_path:.*}")
    public void doGet(@PathParam("sub_path") String sub_path,
                      @Context HttpServletRequest httpServletRequest,
                      @Context HttpServletResponse httpServletResponse) throws ServletException, IOException {
        justDoIt(sub_path, httpServletRequest, httpServletResponse);
    }

    @PUT
    @Path("/{sub_path:.*}")
    public void doPut(@PathParam("sub_path") String sub_path,
                        @Context HttpServletRequest httpServletRequest,
                        @Context HttpServletResponse httpServletResponse) throws ServletException, IOException {
        justDoIt(sub_path, httpServletRequest, httpServletResponse);
    }

    @POST
    @Path("/{sub_path:.*}")
    public void doPost(@PathParam("sub_path") String sub_path,
                         @Context HttpServletRequest httpServletRequest,
                         @Context HttpServletResponse httpServletResponse) throws ServletException, IOException {
        justDoIt(sub_path, httpServletRequest, httpServletResponse);
    }

    @DELETE
    @Path("/{sub_path:.*}")
    public void doDelete(@PathParam("sub_path") String sub_path,
                         @Context HttpServletRequest httpServletRequest,
                         @Context HttpServletResponse httpServletResponse) throws ServletException, IOException {
        justDoIt(sub_path, httpServletRequest, httpServletResponse);
    }

    @HEAD
    @Path("/{sub_path:.*}")
    public void doHead(@PathParam("sub_path") String sub_path,
                         @Context HttpServletRequest httpServletRequest,
                         @Context HttpServletResponse httpServletResponse) throws ServletException, IOException {
        justDoIt(sub_path, httpServletRequest, httpServletResponse);
    }

    @OPTIONS
    @Path("/{sub_path:.*}")
    public void doOptions(@PathParam("sub_path") String sub_path,
                         @Context HttpServletRequest httpServletRequest,
                         @Context HttpServletResponse httpServletResponse) throws ServletException, IOException {
        justDoIt(sub_path, httpServletRequest, httpServletResponse);
    }


    private void justDoIt(final String placeholder,
                          @Context final HttpServletRequest httpServletRequest,
                          @Context HttpServletResponse httpServletResponse) throws ServletException, IOException {
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpServletRequest) {
            @Override
            public String getPathInfo() {
                return placeholder;
            }
        };
        agentServlet.service(requestWrapper, httpServletResponse);
    }
}
