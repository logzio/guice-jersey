package io.logz.guice.jersey;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.EnumSet;
import java.util.Map;

public class JettyFilterDefinition {
    private final Class<? extends Filter> filterClass;
    private final String pathSpec;
    private final EnumSet<DispatcherType> dispatches;
    private final Map<String, String> initParameters;

    public JettyFilterDefinition(Class<? extends Filter> filterClass, String pathSpec, EnumSet<DispatcherType> dispatches, Map<String, String> initParameters) {
        this.filterClass = filterClass;
        this.pathSpec = pathSpec;
        this.dispatches = dispatches;
        this.initParameters = initParameters;
    }

    public Class<? extends Filter> getFilterClass() {
        return filterClass;
    }

    public String getPathSpec() {
        return pathSpec;
    }

    public EnumSet<DispatcherType> getDispatches() {
        return dispatches;
    }

    public Map<String, String> getInitParameters() {
        return initParameters;
    }
}
