package io.logz.guice.jersey.filters;

import javax.inject.Singleton;

@Singleton
public class FilterHeaderService {

    public static final String TEST_HEADER_VALUE = "Filters works !";

    String getHeaderContent() {
        return TEST_HEADER_VALUE;
    }

}
