package com.ca.umg.rt.base;

import java.util.Properties;

import org.junit.Before;

import com.ca.framework.core.requestcontext.RequestContext;

public class TenantAwareBaseTest {
    
    @Before
    public void setup() {
        Properties properties = new Properties();
        properties.put(RequestContext.TENANT_CODE, "localhost");
        new RequestContext(properties);
    }

}
