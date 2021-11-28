package io.logz.guice.jersey.filters;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JettyTestFilter implements Filter {

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
