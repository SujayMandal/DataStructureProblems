package com.ca.umg.rt.endpoint.http;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ModelRequestTest {

    @Autowired
    ApplicationContext context;
    
    @Test
    public void testRequestConversion() throws JsonParseException, JsonMappingException, IOException {
        Assert.assertNotNull(context);
        Resource resource = context.getResource("classpath:com/ca/umg/rt/endpoint/http/AQMK-tenant-input-1.json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        try{
        ModelRequest request = mapper.readValue(resource.getInputStream(), ModelRequest.class);
       
        Assert.assertNotNull(request);
        Assert.assertNotNull(request.getHeader());;
        Assert.assertNotNull(request.getData());
        }
        finally{
        	if(resource.getInputStream() != null){
        		resource.getInputStream() .close();
        	}
        }
    }
}
