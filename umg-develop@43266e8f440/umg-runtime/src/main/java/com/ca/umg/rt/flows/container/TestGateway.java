package com.ca.umg.rt.flows.container;

import java.util.Map;

public interface TestGateway {
    
    public Map<String,Object> executeFlow(Map<String,Object> rqeust);
}
