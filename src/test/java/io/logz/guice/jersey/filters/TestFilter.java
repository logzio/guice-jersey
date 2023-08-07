package io.logz.guice.jersey.filters;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class TestFilter implements ContainerResponseFilter {

    public static final String TEST_HEADER = "X-TEST-HEADER";

    private final FilterHeaderService filterHeaderService;

    @Inject
    public TestFilter(FilterHeaderService filterHeaderService) {
        this.filterHeaderService = requireNonNull(filterHeaderService);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().add(TEST_HEADER, filterHeaderService.getHeaderContent());
    }

}
