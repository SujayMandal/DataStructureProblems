package com.ca.umg.rt.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.rt.endpoint.http.ModelRequest;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RequestValidatorTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestValidatorTest.class);
    
    @Inject
    ApplicationContext context;
    
    @Before
    public void setup() {
        initMocks(this);
        
    }
    
    @Test
    public void testValidateRequestOnline() {
    	 Resource resource = context.getResource("classpath:com/ca/umg/rt/util/input.json");
        try {
           
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object> data = mapper.readValue(resource.getInputStream(), new TypeReference<HashMap<String, Object>>() {});
            String inputRequest = convertToJsonString(data);
            KeyValuePair<String, byte[]> bytesOfFile = new KeyValuePair<>();
            String sanBase = "abc";
            ModelRequest modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            
            Map<String, Object> passedInputHeader =(Map<String,Object>) data.get(MessageVariables.HEADER);
            assertNotNull(modelRequest);
            assertEquals(passedInputHeader.get(MessageVariables.MODEL_NAME), modelRequest.getHeader().getModelName());
            assertEquals(passedInputHeader.get(MessageVariables.TRANSACTION_ID), modelRequest.getHeader().getTransactionId());
            assertEquals(passedInputHeader.get("transactionType"), modelRequest.getHeader().getTransactionType());
            
        } catch (SystemException | IOException | BusinessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally{
        	try {
				if(resource.getInputStream() != null){
					resource.getInputStream().close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    
    /**
     * test for handling user, transactionType and execution group fields
     */
    @Test
    public void testValidateRequestOnline1() {
        try {
            Resource resource = context.getResource("classpath:com/ca/umg/rt/util/input.json");
            try{
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object> data = mapper.readValue(resource.getInputStream(), new TypeReference<HashMap<String, Object>>() {});
            
            Map<String,Object> inputFileHdr = (Map<String,Object>)data.get("header");
            
            //check for user field
            inputFileHdr.remove(MessageVariables.USER);
            String inputRequest = convertToJsonString(data);
            KeyValuePair<String, byte[]> bytesOfFile = new KeyValuePair<>();
            String sanBase = "abc";
            ModelRequest modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            
            //check for user field not valid
            try {
                inputFileHdr.put(MessageVariables.USER,"test error user");
                inputRequest = convertToJsonString(data);
                modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000202);
            }
            
            //check for user field not valid
            try {
                inputFileHdr.put(MessageVariables.USER,"testerroruser@");
                inputRequest = convertToJsonString(data);
                modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000202);
            }
            
            //check for transaction type null
            inputFileHdr.put(MessageVariables.USER,"test_user@Validation.com");
            inputFileHdr.remove(MessageVariables.TRANSACTION_TYPE);
            inputRequest = convertToJsonString(data);
            modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            assertEquals(null,modelRequest.getHeader().getTransactionType());
            
          //check for error scenario for transaction type not equal to test or blank
            try {
                inputFileHdr.put(MessageVariables.TRANSACTION_TYPE,"prod");
                inputRequest = convertToJsonString(data);
                modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000202);
            }
            
            //check for execution group null
            inputFileHdr.put(MessageVariables.TRANSACTION_TYPE,"test");
            inputFileHdr.remove(MessageVariables.EXECUTION_GROUP);
            inputRequest = convertToJsonString(data);
            modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            assertEquals (MessageVariables.DEFAULT_EXECUTION_GROUP,modelRequest.getHeader().getExecutionGroup());
            
          //check for error scenario for execution group 
            try {
                inputFileHdr.put(MessageVariables.EXECUTION_GROUP,"testError");
                inputRequest = convertToJsonString(data);
                modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000202);
            }
            } finally{
            	if(resource.getInputStream() != null){
            		resource.getInputStream().close();
            	}
            }
        } catch (SystemException | IOException | BusinessException e) {
            e.printStackTrace();
        }
    }
    
    
    @Test
    public void testValidateRequestBulk() {
        try {
            Resource resource = context.getResource("classpath:com/ca/umg/rt/util/input-bulk.json");
            try{
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object> data = mapper.readValue(resource.getInputStream(), new TypeReference<HashMap<String, Object>>() {});
            String inputRequest = convertToJsonString(data);
            KeyValuePair<String, byte[]> bytesOfFile = new KeyValuePair<>();
            
            Resource sanBaseResource = context.getResource("classpath:com/ca/umg/rt/util");
            String sanBase = sanBaseResource.getFile().getAbsolutePath();
            final Properties properties = new Properties();
            properties.put(RequestContext.TENANT_CODE, "testtenant");
            new RequestContext(properties);
            
            ModelRequest modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            
            Map<String, Object> passedInputHeader =(Map<String,Object>) data.get(MessageVariables.HEADER);
            assertNotNull(modelRequest);
            assertEquals(passedInputHeader.get(MessageVariables.MODEL_NAME), modelRequest.getHeader().getModelName());
            assertEquals(passedInputHeader.get(MessageVariables.TRANSACTION_ID), modelRequest.getHeader().getTransactionId());
            
        }
            finally{
            	if(resource.getInputStream() != null){
            		resource.getInputStream().close();
            	}
            }
        }catch (SystemException | IOException |BusinessException e) {
            e.printStackTrace();
        }finally {
            RequestContext.getRequestContext().destroy();
        }
    }
    
    /**
     * file not available test
     */
    @Test
    public void testValidateRequestBulkError() {
        try {
            Resource resource = context.getResource("classpath:com/ca/umg/rt/util/input-bulk.json");
           try{
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object> data = mapper.readValue(resource.getInputStream(), new TypeReference<HashMap<String, Object>>() {});
            String inputRequest = convertToJsonString(data);
            KeyValuePair<String, byte[]> bytesOfFile = new KeyValuePair<>();
            
            Resource sanBaseResource = context.getResource("classpath:com/ca/umg/rt/util");
            String sanBase = sanBaseResource.getFile().getAbsolutePath();
            final Properties properties = new Properties();
            properties.put(RequestContext.TENANT_CODE, "testtenant");
            new RequestContext(properties);

            Map<String,Object> inputFileHdr = (Map<String,Object>)data.get("header");
            
          //check for sanbase not available
            try {
                inputRequest = convertToJsonString(data);
                ModelRequest modelRequest = RequestValidator.validateRequest(inputRequest, null, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000223);
            }
            
            //check for file not available
            try {
                inputFileHdr.put("fileName", "test_validation-1-123234354-2.json");
                inputRequest = convertToJsonString(data);
                ModelRequest modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000222);
            }
            
            //check for model-name not matching
            try {
                inputFileHdr.put("fileName", "test_validation-1-123234354-1.json");
                inputFileHdr.put(MessageVariables.MODEL_NAME, "test_validation1");
                inputRequest = convertToJsonString(data);
                ModelRequest modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000222);
            }
            
            //check for major version not matchin
            try {
                inputFileHdr.put(MessageVariables.MODEL_NAME, "test_validation");
                inputFileHdr.put(MessageVariables.MAJOR_VERSION, 2);
                inputRequest = convertToJsonString(data);
                ModelRequest modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000222);
            }
            
            //check for data not available in file
            try {
                inputFileHdr.put("fileName", "test_validationError-1-123234354-1.json");
                inputFileHdr.put(MessageVariables.MAJOR_VERSION, 1);
                inputRequest = convertToJsonString(data);
                ModelRequest modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000222);
            }
            
            //check for file empty
            try {
                inputFileHdr.put("fileName", "test_validnErrorEmpty-1-123234354-1.json");
                inputRequest = convertToJsonString(data);
                ModelRequest modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000222);
            }
            
            //check for file json conversion failure
            try {
                inputFileHdr.put("fileName", "test_validnError1-1-123234354-1.json");
                inputRequest = convertToJsonString(data);
                ModelRequest modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000222);
            }
            
            //insert error in input request json
            try {
                ModelRequest modelRequest = RequestValidator.validateRequest("abc", sanBase, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000210);
            }
           } finally{
        	   if( resource.getInputStream() != null){
        		   resource.getInputStream().close();
        	   }
           }
            
        } catch (SystemException | IOException  e) {
            e.printStackTrace();
        }finally {
            RequestContext.getRequestContext().destroy();
        }
    }
    
    @Test
    public void testValidateRequestError() {
        try {
            Resource resource = context.getResource("classpath:com/ca/umg/rt/util/input-bulk.json");
            ObjectMapper mapper = new ObjectMapper();
            try{
            Map<String,Object> data = mapper.readValue(resource.getInputStream(), new TypeReference<HashMap<String, Object>>() {});
            String inputRequest = convertToJsonString(data);
            KeyValuePair<String, byte[]> bytesOfFile = new KeyValuePair<>();
            
            Resource sanBaseResource = context.getResource("classpath:com/ca/umg/rt/util");
            String sanBase = sanBaseResource.getFile().getAbsolutePath();
            final Properties properties = new Properties();
            properties.put(RequestContext.TENANT_CODE, "testtenant");
            new RequestContext(properties);

            Map<String,Object> inputFileHdr = (Map<String,Object>)data.get("header");
            
            //request header having unacceptable key 
            try {
                inputFileHdr.put("dummyParam", "dummy");
                inputRequest = convertToJsonString(data);
                ModelRequest modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000213);
            }
            
            //request data is not a map
            try {
                inputFileHdr.remove("dummyParam");
                data.put(MessageVariables.DATA, new ArrayList<>());
                inputRequest = convertToJsonString(data);
                ModelRequest modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000215);
            }
            
            //request does not contain data field
            try {
                inputFileHdr.remove("dummyParam");
                data.remove(MessageVariables.DATA);
                inputRequest = convertToJsonString(data);
                ModelRequest modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000214);
            }
            
            //request header empty
            try {
                data.put(MessageVariables.HEADER, new HashMap<>());
                inputRequest = convertToJsonString(data);
                ModelRequest modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000212);
            }
            
            //request header not present
            try {
                data.remove(MessageVariables.HEADER);
                inputRequest = convertToJsonString(data);
                ModelRequest modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000211);
            }
            } finally{
            	if(resource.getInputStream() != null){
            		resource.getInputStream().close();
            	}
            }
        } catch (SystemException | IOException  e) {
            e.printStackTrace();
        }finally {
            RequestContext.getRequestContext().destroy();
        }
    }
    
    @Test
    public void testValidateRequestError1() {
        try {
            Resource resource = context.getResource("classpath:com/ca/umg/rt/util/input.json");
            try{
            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object> data = mapper.readValue(resource.getInputStream(), new TypeReference<HashMap<String, Object>>() {});
            String inputRequest = convertToJsonString(data);
            KeyValuePair<String, byte[]> bytesOfFile = new KeyValuePair<>();
            String sanBase = "abc";
            
            Map<String,Object> inputFileHdr = (Map<String,Object>)data.get("header");
            
            //to test for major-version zero
            try {
                inputFileHdr.put(MessageVariables.MAJOR_VERSION, 0);
                inputRequest = convertToJsonString(data);
                ModelRequest modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
            } catch (SystemException | BusinessException e) {
                assertEquals(e.getCode(),RuntimeExceptionCode.RVE000202);
            }
            
            //test for wrong date format 
                try {
                    inputFileHdr.put(MessageVariables.MAJOR_VERSION, 1);
                    inputFileHdr.put(MessageVariables.DATE, "2016-13-29");
                    inputRequest = convertToJsonString(data);
                    ModelRequest modelRequest = RequestValidator.validateRequest(inputRequest, sanBase, bytesOfFile);
                } catch (SystemException | BusinessException e) {
                    assertEquals(e.getCode(),RuntimeExceptionCode.RVE000701);
                }
            } finally{
            	if (resource.getInputStream() != null ){
            		resource.getInputStream().close();
            	}
            }
        } catch (SystemException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
}
