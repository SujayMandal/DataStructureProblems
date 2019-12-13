package com.ca.umg.rt.transformer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

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
import com.ca.framework.core.util.KeyValuePair;
import com.ca.pool.model.RequestType;
import com.ca.umg.rt.flows.container.EnvironmentVariables;
import com.ca.umg.rt.flows.generator.FlowMetaData;
import com.ca.umg.rt.flows.version.VersionInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MappingInputTransformerTest {

    @Inject
    ApplicationContext context;

    @Inject
    MappingInputTransformer mappingTransformer;

    @Inject
    CacheRegistry cacheRegistry;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testDoTransform() throws Exception {
	    FlowMetaData flowMetaData = new FlowMetaData();
	    flowMetaData.setModelName("TEST");
	    flowMetaData.setMajorVersion(1);
	    flowMetaData.setMinorVersion(0);
	    flowMetaData.getModelLibrary().setJarName("testjarname");
	    flowMetaData.getModelLibrary().setLanguage("matlab");
	    flowMetaData.getMappingMetaData().setMappingInput(getBytes("classpath:com/ca/umg/rt/transformer/mapping-1/mapping-11.json"));
	    flowMetaData.getMappingMetaData().setModelIoData(getBytes("classpath:com/ca/umg/rt/transformer/mapping-1/mid-11.json"));
	    flowMetaData.getMappingMetaData().setTenantInputDefinition(getBytes("classpath:com/ca/umg/rt/transformer/mapping-1/tntInputDefn-11.json"));
	    cacheRegistry.getMap("TEST").put(
	            new VersionInfo("TEST",1,0), flowMetaData);
	    
	    Resource resource = context.getResource("classpath:com/ca/umg/rt/transformer/mapping-1/input-11.json");
	    ObjectMapper mapper = new ObjectMapper();
	    try{
	    Map<String,Object> data = mapper.readValue(resource.getInputStream(), new TypeReference<HashMap<String, Object>>() {});
	    Message<Map<String, Object>> message = MessageBuilder.withPayload(data)
	            .setHeader(EnvironmentVariables.FLOW_CONTAINER_NAME, "TEST").setHeader(EnvironmentVariables.MODEL_NAME, "TEST")
	            .setHeader(EnvironmentVariables.MAJOR_VERSION, 1).setHeader(EnvironmentVariables.MINOR_VERSION, 0)
	            .setHeader(EnvironmentVariables.TENANT_CODE, "TEST")
	            .setHeader(RequestType.TEST.toString().toLowerCase(), 1).build();
	    PayloadToMapTransformer payloadToMapTransformer = new PayloadToMapTransformer();
	    
	    Map<String, Map<String, KeyValuePair<String, String>>> execEnvMap = new HashMap<>();
	    Map<String, KeyValuePair<String, String>> execEnvMapTnt = new HashMap<>();
	    execEnvMapTnt.put("TEST-1-0", new KeyValuePair<String, String>("matlab", "7.16"));
	    execEnvMap.put("TEST", execEnvMapTnt);
	    
	    message = (Message<Map<String, Object>>) payloadToMapTransformer.doTransform(message);
	    Object newMessage = mappingTransformer.doTransform(message);
	    Assert.notNull(newMessage);
	    } finally{
	    	if(resource.getInputStream() != null){
	    		resource.getInputStream().close();
	    	}
	    }
    }
    
    private byte[] getBytes(String path) throws IOException{
        Resource resource = context.getResource(path);
        byte[] byteArray = new byte[resource.getInputStream().available()];
        try {
        resource.getInputStream().read(byteArray);
        } finally{
        	if(resource.getInputStream() != null){
        		resource.getInputStream().close();
        	}
        }
        return byteArray;
    }

}
