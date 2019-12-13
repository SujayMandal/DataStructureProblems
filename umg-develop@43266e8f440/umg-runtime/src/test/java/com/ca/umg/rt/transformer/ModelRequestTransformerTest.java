package com.ca.umg.rt.transformer;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.integration.Message;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.rmodel.info.SupportPackage;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.pool.model.RequestType;
import com.ca.umg.rt.flows.container.EnvironmentVariables;
import com.ca.umg.rt.flows.generator.FlowMetaData;
import com.ca.umg.rt.flows.version.VersionInfo;
import com.ca.umg.rt.util.MessageVariables;
import com.ca.umg.rt.util.container.StaticDataContainer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ModelRequestTransformerTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelRequestTransformerTest.class);

    @Inject
    ApplicationContext context;

    @Inject
    MappingInputTransformer mappingTransformer;
    
    @Inject
    ModelRequestTransformer modelTransformer;

    @Inject
    CacheRegistry cacheRegistry;
    
    private static final String EXPECTED_MODEL_REQUEST_PAYLD_TRANSLATED = "com/ca/umg/rt/transformer/mapping-1/expectedModelReqst-payload.json";
    
    @Mock
    private StaticDataContainer staticDataContainer = new StaticDataContainer();
    
    @Mock
    private SystemParameterProvider systemParameterProvider;
    
    @Mock
    private RequestContext requestContext;
    
    @Before
    public void setup() {
        initMocks(this);
        modelTransformer.setStaticDataContainer(staticDataContainer);
        modelTransformer.setSystemParameterProvider(systemParameterProvider);
        TenantInfo tenantInfo = new TenantInfo();
        Map<String, String> tenantConfigsMap = new HashMap<String,String>();
        tenantInfo.setTenantConfigsMap(tenantConfigsMap);  
        
        		
        cacheRegistry.getMap(FrameworkConstant.TENANT_MAP).put("localhost", tenantInfo);
        
      
            Properties properties = new Properties();
            properties.put(RequestContext.TENANT_CODE, "localhost");
            new RequestContext(properties);
        

      
    }
    
    /**
     * tests the matlab test model
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    @Ignore
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
        
        List<String> rActive = new ArrayList<String>();
        rActive.add("R-3.2.1");
        List<String> matlabActive = new ArrayList<String>();
        matlabActive.add("Matlab-7.16");
        
        cacheRegistry.getMap(PoolConstants.ACTIVE_EXECUTION_ENVIRONMENTS).put("R",rActive);
        cacheRegistry.getMap(PoolConstants.ACTIVE_EXECUTION_ENVIRONMENTS).put("MATLAB",matlabActive);
        
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
        
        Map<String, Map<String, VersionExecInfo>> execEnvMap = new HashMap<>();
        Map<String,VersionExecInfo> execEnvMapTnt = new HashMap<>();
        VersionExecInfo  versionExecInfo = new VersionExecInfo();
        versionExecInfo.setExecEnv("Linux");
        versionExecInfo.setExecLanguage("R");
        versionExecInfo.setExecLangVer("3.2.1");
        execEnvMapTnt.put("TEST-1-0", versionExecInfo);
        execEnvMap.put("TEST", execEnvMapTnt);
        when(staticDataContainer.getAllVersionExecEnvMap()).thenReturn(execEnvMap);
        when(systemParameterProvider.getParameter(SystemParameterProvider.REQUIRE_MODEL_SIZE_REDUCTION)).thenReturn(null);
        
        message = (Message<Map<String, Object>>) payloadToMapTransformer.doTransform(message);
        Mockito.when(requestContext.getTenantCode()).thenReturn("loclahost");
        Object newMessage = mappingTransformer.doTransform(message);
        Assert.notNull(newMessage);
        //Object modelRequest = modelTransformer.doTransform(message);
        Message<Map<String, Object>> transformedMsg = (Message<Map<String, Object>>) modelTransformer.doTransform(message);

        String jsonStr = convertToJsonString(transformedMsg);
        Map<String, Object> transformedmodelRequest = mapper.readValue(jsonStr.getBytes(), new TypeReference<HashMap<String, Object>>() {});
        Map<String, Object> transformedModelRequestPayld = (Map<String, Object>) transformedmodelRequest.get("payload");
        Map<String, Object> transformedmodelrequestmap = (Map<String, Object>) transformedModelRequestPayld.get(MessageVariables.MODEL_REQUEST);
        
        Map<String, Object> expectedmodelTransformermap = mapper.readValue(
                IOUtils.toByteArray(ModelRequestTransformerTest.class.getClassLoader().getResourceAsStream(EXPECTED_MODEL_REQUEST_PAYLD_TRANSLATED)),
                new TypeReference<HashMap<String, Object>>() {
                });
        Map<String, Object> expectedModelRequestPayload = (Map<String, Object>) expectedmodelTransformermap.get("payload");
        Map<String, Object> expectedModelRequestMap = (Map<String, Object>) expectedModelRequestPayload.get("modelRequest");
        
        Boolean mapsDiff = Maps.difference(transformedmodelrequestmap, expectedModelRequestMap).areEqual();
        assertTrue(mapsDiff);
        } finally{
        	if(resource.getInputStream() != null){
        		resource.getInputStream().close();
        	}
        }
    }
    
    /**
     * tests the r rdt-test model
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testDoTransform2() throws Exception {
        FlowMetaData flowMetaData = new FlowMetaData();
        flowMetaData.setModelName("TEST");
        flowMetaData.setMajorVersion(1);
        flowMetaData.setMinorVersion(0);
        flowMetaData.getModelLibrary().setJarName("testjarname");
        flowMetaData.getModelLibrary().setLanguage("R");
        flowMetaData.getMappingMetaData().setMappingInput(getBytes("classpath:com/ca/umg/rt/transformer/R-jsons/r-mapping-1.json"));
        flowMetaData.getMappingMetaData().setModelIoData(getBytes("classpath:com/ca/umg/rt/transformer/R-jsons/r-mid-1.json"));
        flowMetaData.getMappingMetaData().setTenantInputDefinition(getBytes("classpath:com/ca/umg/rt/transformer/R-jsons/r-tntInputDefn-1.json"));
        cacheRegistry.getMap("TEST").put(
                new VersionInfo("TEST",1,0), flowMetaData);
        
        Resource resource = context.getResource("classpath:com/ca/umg/rt/transformer/R-jsons/r-input-1.json");
        try{
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> data = mapper.readValue(resource.getInputStream(), new TypeReference<HashMap<String, Object>>() {});
        Message<Map<String, Object>> message = MessageBuilder.withPayload(data)
                .setHeader(EnvironmentVariables.FLOW_CONTAINER_NAME, "TEST").setHeader(EnvironmentVariables.MODEL_NAME, "TEST")
                .setHeader(EnvironmentVariables.MAJOR_VERSION, 1).setHeader(EnvironmentVariables.MINOR_VERSION, 0)
                .setHeader(EnvironmentVariables.TENANT_CODE, "TEST")
                .setHeader(RequestType.TEST.toString().toLowerCase(), 1).build();
        PayloadToMapTransformer payloadToMapTransformer = new PayloadToMapTransformer();
        
        Map<String, Map<String, VersionExecInfo>> execEnvMap = new HashMap<>();
        Map<String, VersionExecInfo> execEnvMapTnt = new HashMap<>();
        VersionExecInfo info = new VersionExecInfo();
        info.setExecEnv("Windows");
        info.setExecLanguage("R");
        info.setExecLangVer("3.2.1");
        execEnvMapTnt.put("TEST-1-0",info);
        execEnvMap.put("TEST", execEnvMapTnt);
        when(staticDataContainer.getAllVersionExecEnvMap()).thenReturn(execEnvMap);
        
        Map<String, Map<String, String>> allPackageNamesMap = new HashMap<String, Map<String, String>>();
        Map<String, String> packageNameMap = new HashMap<>();
        packageNameMap.put("TEST-1-0", "testR");
        allPackageNamesMap.put("TEST", packageNameMap);
        when(staticDataContainer.getAllPackageNames()).thenReturn(allPackageNamesMap);
        
        Map<String, Map<String, List<SupportPackage>>> allSupportPackages = new HashMap<>();
        Map<String, List<SupportPackage>> allSupportPackagesMap = new HashMap<>();
        List<SupportPackage> supportPackages = new ArrayList<>();
        SupportPackage supportPackage = new SupportPackage();
        supportPackage.setJarName("testJar");
        supportPackage.setPackageName("testPackage");
        supportPackages.add(supportPackage);
        allSupportPackagesMap.put("TEST-1-0", supportPackages);
        allSupportPackages.put("TEST", allSupportPackagesMap);
        when(staticDataContainer.getAllSupportPackages()).thenReturn(allSupportPackages);
        
        message = (Message<Map<String, Object>>) payloadToMapTransformer.doTransform(message);
        Object newMessage = mappingTransformer.doTransform(message);
        Assert.notNull(newMessage);
        //Object modelRequest = modelTransformer.doTransform(message);
        Message<Map<String, Object>> transformedMsg = (Message<Map<String, Object>>) modelTransformer.doTransform(message);

        String jsonStr = convertToJsonString(transformedMsg);
        Map<String, Object> transformedmodelRequest = mapper.readValue(jsonStr.getBytes(), new TypeReference<HashMap<String, Object>>() {});
        Map<String, Object> transformedModelRequestPayld = (Map<String, Object>) transformedmodelRequest.get("payload");
        Map<String, Object> transformedmodelrequestmap = (Map<String, Object>) transformedModelRequestPayld.get("modelRequest");
        
        Map<String, Object> expectedmodelTransformermap = mapper.readValue(
                IOUtils.toByteArray(ModelRequestTransformerTest.class.getClassLoader().
                		getResourceAsStream("com/ca/umg/rt/transformer/R-jsons/r-expectedModelReqst-payload.json")),
                new TypeReference<HashMap<String, Object>>() {
                });
        
        Map<String, Object> expectedModelRequestPayload = (Map<String, Object>) expectedmodelTransformermap.get("payload");
        Map<String, Object> expectedModelRequestMap = (Map<String, Object>) expectedModelRequestPayload.get("modelRequest");
        
        Boolean mapsDiff = Maps.difference(transformedmodelrequestmap, expectedModelRequestMap).areEqual();
        assertTrue(mapsDiff);
        } finally{
        	if(resource.getInputStream() != null){
        		resource.getInputStream().close();
        	}
        }
    }
    
    /**
     * tests the symetric io (matrix and vector data type) model
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })    
    public void testDoTransform3() throws Exception {
        FlowMetaData flowMetaData = new FlowMetaData();
        flowMetaData.setModelName("TEST");
        flowMetaData.setMajorVersion(1);
        flowMetaData.setMinorVersion(0);
        flowMetaData.getModelLibrary().setJarName("testjarname");
        flowMetaData.getModelLibrary().setLanguage("R");
        flowMetaData.getMappingMetaData().setMappingInput(getBytes("classpath:com/ca/umg/rt/transformer/R-jsons/r-mapping-11-matrix-vector.json"));
        flowMetaData.getMappingMetaData().setModelIoData(getBytes("classpath:com/ca/umg/rt/transformer/R-jsons/r-mid-11-matrix-vector.json"));
        flowMetaData.getMappingMetaData().setTenantInputDefinition(getBytes("classpath:com/ca/umg/rt/transformer/R-jsons/r-tntInputDefn-11-matrix-vector.json"));
        cacheRegistry.getMap("TEST").put(
                new VersionInfo("TEST",1,0), flowMetaData);
        
        Resource resource = context.getResource("classpath:com/ca/umg/rt/transformer/R-jsons/r-input-11-matrix-vector.json");
        try{
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> data = mapper.readValue(resource.getInputStream(), new TypeReference<HashMap<String, Object>>() {});
        Message<Map<String, Object>> message = MessageBuilder.withPayload(data)
                .setHeader(EnvironmentVariables.FLOW_CONTAINER_NAME, "TEST").setHeader(EnvironmentVariables.MODEL_NAME, "TEST")
                .setHeader(EnvironmentVariables.MAJOR_VERSION, 1).setHeader(EnvironmentVariables.MINOR_VERSION, 0)
                .setHeader(EnvironmentVariables.TENANT_CODE, "TEST")
                .setHeader(RequestType.TEST.toString().toLowerCase(), 1).build();
        PayloadToMapTransformer payloadToMapTransformer = new PayloadToMapTransformer();
        
        Map<String, Map<String, VersionExecInfo>> execEnvMap = new HashMap<>();
        Map<String, VersionExecInfo> execEnvMapTnt = new HashMap<>();
        
        VersionExecInfo info = new VersionExecInfo();
        info.setExecEnv("Linux");
        info.setExecLanguage("R");
        info.setExecLangVer("3.2.1");
        execEnvMapTnt.put("TEST-1-0", info);
        execEnvMap.put("TEST", execEnvMapTnt);
        when(staticDataContainer.getAllVersionExecEnvMap()).thenReturn(execEnvMap);
        
        Map<String, Map<String, String>> allPackageNamesMap = new HashMap<String, Map<String, String>>();
        Map<String, String> packageNameMap = new HashMap<>();
        packageNameMap.put("TEST-1-0", "testR");
        allPackageNamesMap.put("TEST", packageNameMap);
        when(staticDataContainer.getAllPackageNames()).thenReturn(allPackageNamesMap);
        
        Map<String, Map<String, List<SupportPackage>>> allSupportPackages = new HashMap<>();
        Map<String, List<SupportPackage>> allSupportPackagesMap = new HashMap<>();
        List<SupportPackage> supportPackages = new ArrayList<>();
        SupportPackage supportPackage = new SupportPackage();
        supportPackage.setJarName("testJar");
        supportPackage.setPackageName("testPackage");
        supportPackages.add(supportPackage);
        allSupportPackagesMap.put("TEST-1-0", supportPackages);
        allSupportPackages.put("TEST", allSupportPackagesMap);
        when(staticDataContainer.getAllSupportPackages()).thenReturn(allSupportPackages);
        
        message = (Message<Map<String, Object>>) payloadToMapTransformer.doTransform(message);
        Object newMessage = mappingTransformer.doTransform(message);
        Assert.notNull(newMessage);
        //Object modelRequest = modelTransformer.doTransform(message);
        Message<Map<String, Object>> transformedMsg = (Message<Map<String, Object>>) modelTransformer.doTransform(message);

        String jsonStr = convertToJsonString(transformedMsg);
        Map<String, Object> transformedmodelRequest = mapper.readValue(jsonStr.getBytes(), new TypeReference<HashMap<String, Object>>() {});
        Map<String, Object> transformedModelRequestPayld = (Map<String, Object>) transformedmodelRequest.get("payload");
        Map<String, Object> transformedmodelrequestmap = (Map<String, Object>) transformedModelRequestPayld.get("modelRequest");
        
        Map<String, Object> expectedmodelTransformermap = mapper.readValue(
                IOUtils.toByteArray(ModelRequestTransformerTest.class.getClassLoader().
                		getResourceAsStream("com/ca/umg/rt/transformer/R-jsons/r-expectedModelReqst-payload-matrix-vector.json")),
                new TypeReference<HashMap<String, Object>>() {
                });
        
        Map<String, Object> expectedModelRequestPayload = (Map<String, Object>) expectedmodelTransformermap.get("payload");
        Map<String, Object> expectedModelRequestMap = (Map<String, Object>) expectedModelRequestPayload.get("modelRequest");
        
        Boolean mapsDiff = Maps.difference(transformedmodelrequestmap, expectedModelRequestMap).areEqual();
        assertTrue(mapsDiff);
    
    } finally{
    	if(resource.getInputStream() != null){
    		resource.getInputStream().close();
    	}
    }
    }
    
    /**
     * tests the symetric io (factor data type) model
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })    
    public void testDoTransform4() throws Exception {
        FlowMetaData flowMetaData = new FlowMetaData();
        flowMetaData.setModelName("TEST");
        flowMetaData.setMajorVersion(1);
        flowMetaData.setMinorVersion(0);
        flowMetaData.getModelLibrary().setJarName("testjarname");
        flowMetaData.getModelLibrary().setLanguage("R");
        flowMetaData.getMappingMetaData().setMappingInput(getBytes("classpath:com/ca/umg/rt/transformer/R-jsons/r-mapping-12-factor.json"));
        flowMetaData.getMappingMetaData().setModelIoData(getBytes("classpath:com/ca/umg/rt/transformer/R-jsons/r-mid-12-factor.json"));
        flowMetaData.getMappingMetaData().setTenantInputDefinition(getBytes("classpath:com/ca/umg/rt/transformer/R-jsons/r-tntInputDefn-12-factor.json"));
        cacheRegistry.getMap("TEST").put(
                new VersionInfo("TEST",1,0), flowMetaData);
        
        Resource resource = context.getResource("classpath:com/ca/umg/rt/transformer/R-jsons/r-input-12-factor.json");
        ObjectMapper mapper = new ObjectMapper();
        try{
        Map<String,Object> data = mapper.readValue(resource.getInputStream(), new TypeReference<HashMap<String, Object>>() {});
        Message<Map<String, Object>> message = MessageBuilder.withPayload(data)
                .setHeader(EnvironmentVariables.FLOW_CONTAINER_NAME, "TEST").setHeader(EnvironmentVariables.MODEL_NAME, "TEST")
                .setHeader(EnvironmentVariables.MAJOR_VERSION, 1).setHeader(EnvironmentVariables.MINOR_VERSION, 0)
                .setHeader(EnvironmentVariables.TENANT_CODE, "TEST")
                .setHeader(RequestType.TEST.toString().toLowerCase(), 1).build();
        PayloadToMapTransformer payloadToMapTransformer = new PayloadToMapTransformer();
        
        Map<String, Map<String, VersionExecInfo>> execEnvMap = new HashMap<>();
        Map<String, VersionExecInfo> execEnvMapTnt = new HashMap<>();
        VersionExecInfo info = new VersionExecInfo();
        info.setExecEnv("Linux");
        info.setExecLanguage("R");
        info.setExecLangVer("3.2.1");
        execEnvMapTnt.put("TEST-1-0", info);
        execEnvMap.put("TEST", execEnvMapTnt);
        when(staticDataContainer.getAllVersionExecEnvMap()).thenReturn(execEnvMap);
        
        Map<String, Map<String, String>> allPackageNamesMap = new HashMap<String, Map<String, String>>();
        Map<String, String> packageNameMap = new HashMap<>();
        packageNameMap.put("TEST-1-0", "testR");
        allPackageNamesMap.put("TEST", packageNameMap);
        when(staticDataContainer.getAllPackageNames()).thenReturn(allPackageNamesMap);
        
        Map<String, Map<String, List<SupportPackage>>> allSupportPackages = new HashMap<>();
        Map<String, List<SupportPackage>> allSupportPackagesMap = new HashMap<>();
        List<SupportPackage> supportPackages = new ArrayList<>();
        SupportPackage supportPackage = new SupportPackage();
        supportPackage.setJarName("testJar");
        supportPackage.setPackageName("testPackage");
        supportPackages.add(supportPackage);
        allSupportPackagesMap.put("TEST-1-0", supportPackages);
        allSupportPackages.put("TEST", allSupportPackagesMap);
        when(staticDataContainer.getAllSupportPackages()).thenReturn(allSupportPackages);
        
        message = (Message<Map<String, Object>>) payloadToMapTransformer.doTransform(message);
        Object newMessage = mappingTransformer.doTransform(message);
        Assert.notNull(newMessage);
        //Object modelRequest = modelTransformer.doTransform(message);
        Message<Map<String, Object>> transformedMsg = (Message<Map<String, Object>>) modelTransformer.doTransform(message);

        String jsonStr = convertToJsonString(transformedMsg);
        Map<String, Object> transformedmodelRequest = mapper.readValue(jsonStr.getBytes(), new TypeReference<HashMap<String, Object>>() {});
        Map<String, Object> transformedModelRequestPayld = (Map<String, Object>) transformedmodelRequest.get("payload");
        Map<String, Object> transformedmodelrequestmap = (Map<String, Object>) transformedModelRequestPayld.get("modelRequest");
        
        Map<String, Object> expectedmodelTransformermap = mapper.readValue(
                IOUtils.toByteArray(ModelRequestTransformerTest.class.getClassLoader().
                		getResourceAsStream("com/ca/umg/rt/transformer/R-jsons/r-expectedModelReqst-payload-factor.json")),
                new TypeReference<HashMap<String, Object>>() {
                });
        
        Map<String, Object> expectedModelRequestPayload = (Map<String, Object>) expectedmodelTransformermap.get("payload");
        Map<String, Object> expectedModelRequestMap = (Map<String, Object>) expectedModelRequestPayload.get("modelRequest");
        
        Boolean mapsDiff = Maps.difference(transformedmodelrequestmap, expectedModelRequestMap).areEqual();
        assertTrue(mapsDiff);
        } finally{
        	if(resource.getInputStream() != null){
        		resource.getInputStream().close();
        	}
        }
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
    
    private byte[] getBytes(String path) throws IOException{
    	byte[] byteArray  = null;
  
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
