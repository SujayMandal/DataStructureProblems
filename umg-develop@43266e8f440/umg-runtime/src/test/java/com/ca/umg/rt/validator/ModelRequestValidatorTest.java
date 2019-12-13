package com.ca.umg.rt.validator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.integration.Message;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.umg.rt.flows.container.EnvironmentVariables;
import com.ca.umg.rt.flows.generator.FlowMetaData;
import com.ca.umg.rt.flows.version.VersionInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ModelRequestValidatorTest {
    @Inject
    ApplicationContext context;

    @Inject
    ModelRequestValidator modelRequestValidator;

    @Inject
    CacheRegistry cacheRegistry;

    @SuppressWarnings({ "unchecked" })
    @Ignore
    @Test
    public void testDoTransform2() throws Exception {
        FlowMetaData flowMetaData = new FlowMetaData();
        flowMetaData.setModelName("TEST");
        flowMetaData.setMajorVersion(1);
        flowMetaData.setMinorVersion(0);
        flowMetaData.getModelLibrary().setJarName("testjarname");
        flowMetaData.getMappingMetaData().setTenantInputDefinition(getBytes("classpath:com/ca/umg/rt/validator/files/tid.json"));
        cacheRegistry.getMap("TEST").put(new VersionInfo("TEST", 1, 0), flowMetaData);
        Resource resource = context.getResource("classpath:com/ca/umg/rt/validator/files/input.json");
       try{
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = mapper.readValue(resource.getInputStream(), new TypeReference<HashMap<String, Object>>() {
        });
        Message<Map<String, Object>> message = MessageBuilder.withPayload(data)
                .setHeader(EnvironmentVariables.FLOW_CONTAINER_NAME, "TEST").setHeader(EnvironmentVariables.MODEL_NAME, "TEST")
                .setHeader(EnvironmentVariables.MAJOR_VERSION, 1).setHeader(EnvironmentVariables.MINOR_VERSION, 0)
                .setHeader(EnvironmentVariables.TENANT_CODE, "TEST").build();
        message = (Message<Map<String, Object>>) modelRequestValidator.doTransform(message);
        Assert.notNull(message);
    } finally{
    	if(resource.getInputStream() != null){
    		resource.getInputStream().close();
    	}
    }
    }

    private byte[] getBytes(String path) throws IOException {
    	byte[] byteArray = null;
    	 Resource resource = context.getResource(path);
    	try{
        byteArray = new byte[resource.getInputStream().available()];
        resource.getInputStream().read(byteArray);
    	} finally{
    		if(resource.getInputStream() != null){
    			resource.getInputStream().close();
    		}
    	}
        return byteArray;
    }
}
