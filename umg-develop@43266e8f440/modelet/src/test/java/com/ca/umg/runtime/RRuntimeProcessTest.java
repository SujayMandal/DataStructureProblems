package com.ca.umg.runtime;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.mockito.Mockito;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.common.ModelKey;
import com.ca.umg.modelet.common.ModelRequestInfo;
import com.ca.umg.modelet.common.ModelResponseInfo;
import com.ca.umg.modelet.common.RServeDelegator;
import com.ca.umg.modelet.common.RServeModel;
import com.ca.umg.modelet.runtime.ModeletRuntime;
import com.ca.umg.modelet.runtime.impl.RRuntimeProcess;
import com.ca.umg.modelet.runtime.impl.RserveRuntime;

import junit.framework.Assert;

public class RRuntimeProcessTest {

	ModeletRuntime rRuntime = Mockito.mock(RserveRuntime.class);
	RServeDelegator rEngineInvoker = Mockito.mock(RServeDelegator.class);

	RServeModel model = new RServeModel(rEngineInvoker);

	private RRuntimeProcess runtimeProcess = new RRuntimeProcess(rRuntime);
	

	@Test
	public void executeTest() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("com/ca/umg/runtime/RModelInput.json");
		ModelRequestInfo requestInfo = mapper.readValue(is, ModelRequestInfo.class);

		InputStream isCommand = this.getClass().getClassLoader()
				.getResourceAsStream("com/ca/umg/runtime/RCommand.json");
		String rCommnd = IOUtils.toString(isCommand, "UTF-8");

		ModelKey modelKey = new ModelKey();
		modelKey.setFilePath("abc");
		modelKey.setModelClass("symmetricIO");
		modelKey.setModelLibrary("SymmetricIO");
		modelKey.setModelMethod("symmetricIONew");
		modelKey.setModelName("SymmetricIO");

		try {
			when(rRuntime.getModel(requestInfo.getHeaderInfo())).thenReturn(model);
			when(rEngineInvoker.getModelKey()).thenReturn(modelKey);
			ModelResponseInfo modelResponseInfo = runtimeProcess.execute(requestInfo, null);
			Assert.assertEquals(modelResponseInfo.getResponseHeaderInfo().getExecutionCommand(), rCommnd);
			Assert.assertEquals(requestInfo.getHeaderInfo().getModelName(), modelResponseInfo.getModelName());
		} catch (BusinessException | SystemException e) {
			Assert.fail();
		}
		finally{
			if(is != null){
				is.close();
			}
			if(isCommand != null){
				isCommand.close();
			}
		}
	}

}
