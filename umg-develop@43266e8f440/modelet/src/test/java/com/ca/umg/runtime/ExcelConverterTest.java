package com.ca.umg.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.umg.modelet.common.ModelRequestInfo;
import com.ca.umg.modelet.converter.Converter;
import com.ca.umg.modelet.converter.impl.ExcelConverter;

import junit.framework.Assert;

public class ExcelConverterTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelConverterTest.class);
	
	Converter rConverter =new ExcelConverter();	
	@Test
	public void executeTest() throws JsonParseException, JsonMappingException, IOException{		
		ObjectMapper mapper = new ObjectMapper();
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("com/ca/umg/runtime/ExcelModelInput.json");
		ModelRequestInfo requestInfo  = mapper.readValue(is, ModelRequestInfo.class);		
		try {
			Map<String,Object> modelRequestInfo = (Map<String,Object>)rConverter.marshall(requestInfo);	
			Assert.assertTrue(modelRequestInfo.get("Employee")!=null);		
		} catch(Exception e){		
			LOGGER.error("exception is :",e);			
			Assert.fail();
		}
		finally{
			if(is != null){
				is.close();
			}
		}
	}

}
