package io.logz.guice.jersey.filters;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AddHeaderJettyFilter implements Filter {

    public static final String TEST_HEADER = "X-JETTY-FILTER-TEST-HEADER";
    public static final String INIT_PARAM_KEY = "INIT_PARAM_KEY";
    private String initParamValue;

    @Override
    public void init(FilterConfig filterConfig) {
        initParamValue = filterConfig.getInitParameter(INIT_PARAM_KEY);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ((HttpServletResponse) response).addHeader(TEST_HEADER, initParamValue);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
