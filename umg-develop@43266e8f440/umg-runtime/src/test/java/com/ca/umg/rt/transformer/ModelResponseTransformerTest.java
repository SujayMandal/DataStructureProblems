package com.ca.umg.rt.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.Message;
import org.springframework.integration.support.MessageBuilder;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.me2.util.ModelExecResponse;
import com.ca.umg.rt.core.deployment.bo.DeploymentBO;
import com.ca.umg.rt.core.deployment.bo.DeploymentBOImpl;
import com.ca.umg.rt.core.flow.entity.ModelLibrary;
import com.ca.umg.rt.flows.container.EnvironmentVariables;
import com.ca.umg.rt.flows.generator.FlowMetaData;
import com.ca.umg.rt.flows.generator.MappingMetaData;
import com.ca.umg.rt.flows.version.VersionInfo;
import com.ca.umg.rt.response.ModelResponseFactory;
import com.ca.umg.rt.util.MessageVariables;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.hazelcast.core.IMap;


public class ModelResponseTransformerTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelResponseTransformerTest.class);

    private static final String MODEL_OUTPUT = "com/ca/umg/rt/transformer/mapping-1/Model-Output.txt";
    private static final String MAPPING_JSON = "com/ca/umg/rt/transformer/mapping-1/mapping-11.json";
    private static final String MID_INPUT = "com/ca/umg/rt/transformer/mapping-1/mid-11.json";
    private static final String TNT_INPUT_DEFN = "com/ca/umg/rt/transformer/mapping-1/tntInputDefn-11.json";
    private static final String EXPECTED_MODEL_RES_TRANSLATED = "com/ca/umg/rt/transformer/mapping-1/expectedModelRespnse.json";

    @Inject
    ApplicationContext context;

    @InjectMocks
    ModelResponseTransformer modelResponseTransformer = new ModelResponseTransformer();

    @Mock
    private DeploymentBO deploymentBO = new DeploymentBOImpl();

    @Mock
    private CacheRegistry cacheRegistry;   
 
    @Mock
    private RequestContext requestContext;
    
    private Map<String,Object> tenantMap = new HashMap<String,Object>();
    

    @Mock
    private IMap<Object, Object> iMap;
    
    @Mock
    private SystemParameterProvider systemParameterProvider;
    
   
    @Spy
    @InjectMocks
    private ModelResponseFactory modelResponseFactory = new ModelResponseFactory();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        TenantInfo tenantInfo = new TenantInfo();
        Map<String, String> tenantConfigsMap = new HashMap<String,String>();
        tenantInfo.setTenantConfigsMap(tenantConfigsMap);        
        tenantMap.put("localhost", tenantInfo);
        Mockito.when(cacheRegistry.getMap(FrameworkConstant.TENANT_MAP)).thenReturn(iMap);
        Mockito.when((TenantInfo)iMap.get("localhost")).thenReturn(buildTenantInfo());
     
        
        Properties properties = new Properties();
        properties.put(RequestContext.TENANT_CODE, "localhost");
        requestContext =  new RequestContext(properties);
        Mockito.when(systemParameterProvider
				.getParameter(SystemParameterProvider.REQUIRE_MODEL_SIZE_REDUCTION)).thenReturn("false");
    
    }

    @SuppressWarnings("unchecked")
    @Test
    public void simpleTestDoTransform() throws IOException {
    	 InputStream is = null;
        try {

             is = ModelResponseTransformerTest.class.getClassLoader().getResourceAsStream(MODEL_OUTPUT);
            byte[] mappingOutput = IOUtils.toByteArray(is);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data;
            data = mapper.readValue(mappingOutput, new TypeReference<HashMap<String, Object>>() {});
            
            
            
            Mockito.when(cacheRegistry.getMap(FrameworkConstant.TENANT_MAP)).thenReturn(iMap);
            
            
            /*List<String> rActive = new ArrayList<String>();
            rActive.add("R-3.2.1");
            List<String> matlabActive = new ArrayList<String>();
            matlabActive.add("Matlab-7.16");
            
            cacheRegistry.getMap(PoolConstants.ACTIVE_EXECUTION_ENVIRONMENTS).put("R",rActive);
            cacheRegistry.getMap(PoolConstants.ACTIVE_EXECUTION_ENVIRONMENTS).put("MATLAB",matlabActive);*/

            Map<String, Object> modelExecResponseMap =(Map<String, Object>) data.get(MessageVariables.ME2_RESPONSE); 
            ModelExecResponse<Map<String, Object>> execResponse = new ModelExecResponse<>();
            execResponse.setResponse((Map<String, Object>) 
                    mapper.readValue((String)modelExecResponseMap.get("response"), new TypeReference<HashMap<String, Object>>() {}));
            execResponse.setSuccess((boolean)modelExecResponseMap.get("success"));
            execResponse.setMe2ExecutionTime(Long.valueOf(Integer.parseInt(modelExecResponseMap.get("me2ExecutionTime").toString())));
            data.put(MessageVariables.ME2_RESPONSE, execResponse);
            
            FlowMetaData flowMetaData = getFlowMetaData(MAPPING_JSON, MID_INPUT, TNT_INPUT_DEFN);
            List<FlowMetaData> flowMetaDataList = new ArrayList<>();
            flowMetaDataList.add(flowMetaData);

            iMap.put(new VersionInfo("TEST", 1, 0), flowMetaData);
            Mockito.when(cacheRegistry.getMap("TEST")).thenReturn(iMap);
            Mockito.when(deploymentBO.gatherVersionData("TEST", 1, 0)).thenReturn(flowMetaDataList);

            Message<Map<String, Object>> message = MessageBuilder.withPayload(data)
                    .setHeader(EnvironmentVariables.FLOW_CONTAINER_NAME, "TEST")
                    .setHeader(EnvironmentVariables.MODEL_NAME, "TEST").setHeader(EnvironmentVariables.MAJOR_VERSION, 1)
                    .setHeader(EnvironmentVariables.MINOR_VERSION, 0).build();
            
            Message<Map<String, Object>> transformedMsg = (Message<Map<String, Object>>) modelResponseTransformer
                    .doTransform(message);

            Map<String, Object> modelResponseTranslated = (Map<String, Object>) transformedMsg.getPayload().get(
                    "modelResponseTranslated");
            InputStream is1 = null;
            Map<String, Object> expectedmodelResponseTranslated = null;
            try{
            	is1 = ModelResponseTransformerTest.class.getClassLoader().getResourceAsStream(EXPECTED_MODEL_RES_TRANSLATED);
            expectedmodelResponseTranslated = mapper.readValue(
                    IOUtils.toByteArray(is1),
                    new TypeReference<HashMap<String, Object>>() {
                    });

            } finally{
            	if(is1 != null){
            		is1.close();
            	}
            }
            Boolean mapsDiff = Maps.difference(expectedmodelResponseTranslated, modelResponseTranslated).areEqual();
            Assert.assertTrue(mapsDiff);
        } catch (SystemException | BusinessException | IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
         finally{
        	 if(is != null){
        		 is.close();
        	 }
         }
    }    
  

    public FlowMetaData getFlowMetaData(String mappingJson, String mappingInput, String tenantinputdefn) throws IOException {
        FlowMetaData flowMetaData = new FlowMetaData();
        flowMetaData.setModelName("TEST");
        flowMetaData.setMajorVersion(1);
        flowMetaData.setMinorVersion(0);
        MappingMetaData mappingMetaData = new MappingMetaData();
        flowMetaData.setMappingMetaData(mappingMetaData);
        ClassLoader classLoader = ModelResponseTransformerTest.class.getClassLoader();
        try{
        flowMetaData.getMappingMetaData().setMappingInput(IOUtils.toByteArray(classLoader.getResourceAsStream(mappingJson)));
        flowMetaData.getMappingMetaData().setModelIoData(IOUtils.toByteArray(classLoader.getResourceAsStream(mappingInput)));
        flowMetaData.getMappingMetaData().setTenantInputDefinition(
                IOUtils.toByteArray(classLoader.getResourceAsStream(tenantinputdefn)));
        ModelLibrary modelLibrary = new ModelLibrary();
        modelLibrary.setLanguage("Matlab");
        flowMetaData.setModelLibrary(modelLibrary);
        } finally{
        	if(classLoader.getResourceAsStream(mappingJson) != null){
        		classLoader.getResourceAsStream(mappingJson).close();
        	}
        	if(classLoader.getResourceAsStream(tenantinputdefn) != null){
        		classLoader.getResourceAsStream(tenantinputdefn).close();
        	}
        	if(classLoader.getResourceAsStream(mappingInput) != null){
        		classLoader.getResourceAsStream(mappingInput).close();
        	}
        }
        return flowMetaData;
    }
    
    private <T> String convertToJsonString(T data) throws SystemException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = null;
        try {
            if (data != null) {
                jsonStr = objectMapper.writeValueAsString(data);
            }
        } catch (IOException e) {
        	LOGGER.error("IOException: ", e);
        }
        return jsonStr;
    }
    
    private TenantInfo buildTenantInfo(){
    	Map<String,String> tenantConfigsMap = new HashMap<String, String>();
    	tenantConfigsMap.put("BULK_ENABLED", "BULK_ENABLED");
    	TenantInfo info = new TenantInfo();
    	info.setCode("localhost");
    	info.setTenantConfigsMap(tenantConfigsMap);
    	return info;
    }

}
