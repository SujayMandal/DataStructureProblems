package com.ca.umg.modelet.transport.handler;

import static java.lang.Long.valueOf;
import static java.lang.System.currentTimeMillis;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.common.ModelRequestInfo;
import com.ca.umg.modelet.common.ModelResponseInfo;
import com.ca.umg.modelet.common.ResponseHeaderInfo;
import com.ca.umg.modelet.common.SystemInfo;
import com.ca.umg.modelet.constants.ErrorCodes;
import com.ca.umg.modelet.runtime.RuntimeProcess;
import com.ca.umg.modelet.runtime.factory.RuntimeFactory;
import com.ca.umg.modelet.runtime.factory.RuntimeProcessFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@Named(value="httpHandler")
public class ModeletHttpHandler implements InboundRequestHandler, HttpHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ModeletHttpHandler.class);
	
	@Inject
	private RuntimeProcessFactory factory;
	
	@Inject
	private SystemInfo systemInfo;
	
	@Inject
	private RuntimeFactory runtimeFactory;
	final private ObjectMapper mapper = new ObjectMapper();
	
	public void handle(final HttpExchange httpExchange) throws IOException {
		InputStream inputStream = null;
	    byte[] bytes = null;
	    String requestJSON = null;
	    ModelRequestInfo modelRequestInfo = null;
	    ModelResponseInfo modelResponseInfo = null;
	    String response = "";
	    ResponseHeaderInfo headerInfo = new ResponseHeaderInfo();
		long start = currentTimeMillis();
		try {
            inputStream = httpExchange.getRequestBody();
            start = currentTimeMillis();
            bytes = IOUtils.toByteArray(inputStream);
            requestJSON = new String(bytes);
            inputStream.close();
            httpExchange.getRequestBody().close();
            modelRequestInfo = transformRequestToJSON(requestJSON);
            LOGGER.info("Received model execution request for model {}.", modelRequestInfo.getHeaderInfo().getModelName());
            LOGGER.info("Received request JSON {}.", requestJSON);
            modelResponseInfo = getRuntimeProcess(modelRequestInfo).execute(modelRequestInfo, systemInfo);
            setModeletExecutionTime(start, modelResponseInfo);
            headerInfo.setError(String.valueOf(Boolean.FALSE));
            modelResponseInfo.setResponseHeaderInfo(headerInfo);
            LOGGER.info("Executed model {}.", modelRequestInfo.getHeaderInfo().getModelName());
            response = responseToJSON(modelResponseInfo);
        } catch (SystemException e) {
            headerInfo.setError(String.valueOf(Boolean.TRUE));
            headerInfo.setErrorMessage(e.getMessage());
            headerInfo.setErrorCode(ErrorCodes.ME0005);
            modelResponseInfo = new ModelResponseInfo();
            setModeletExecutionTime(start, modelResponseInfo);
            modelResponseInfo.setResponseHeaderInfo(headerInfo);
            response = mapper.writeValueAsString(modelResponseInfo);
        } catch (Exception e) { // NOPMD
            headerInfo.setError(String.valueOf(Boolean.TRUE));
            headerInfo.setErrorMessage(e.getMessage());
            headerInfo.setErrorCode(ErrorCodes.ME0003);
            modelResponseInfo = new ModelResponseInfo();
            setModeletExecutionTime(start, modelResponseInfo);
            modelResponseInfo.setResponseHeaderInfo(headerInfo);
        }
		LOGGER.info("Sending response JSON {}.", response);
		httpExchange.sendResponseHeaders(200, response.length());
		httpExchange.getResponseBody().write(response.getBytes());
		httpExchange.getResponseBody().close();
	}

    private void setModeletExecutionTime(final long start, final ModelResponseInfo modelResponseInfo) {
    	final long end = currentTimeMillis();
    	modelResponseInfo.setModeletExecutionTime(valueOf(end - start));
    }
    
	public RuntimeProcess getRuntimeProcess(ModelRequestInfo modelRequestInfo) {
		return factory.getRuntimeProcessInstance(modelRequestInfo.getHeaderInfo().getEngine(), runtimeFactory);
	}

    public RuntimeProcessFactory getFactory() {
        return factory;
    }

    public void setFactory(final RuntimeProcessFactory factory) {
        this.factory = factory;
    }

    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(final SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }
    
    private ModelRequestInfo transformRequestToJSON(String requestJSON) throws SystemException {
        ModelRequestInfo modelRequestInfo = null;
        try {
            modelRequestInfo = mapper.readValue(requestJSON, ModelRequestInfo.class);
        } catch (JsonParseException | JsonMappingException e) {
            LOGGER.error("Exception occured while parsing request json", e);
            SystemException.newSystemException(ErrorCodes.ME0004, new String[]{"request", e.getMessage()}, e);
        } catch(IOException e) {
            LOGGER.error("Exception occured while parsing response json", e);
            SystemException.newSystemException(ErrorCodes.ME0004, new String[] { "request", e.getMessage() }, e);
        }
        return modelRequestInfo;
    }
    
    private String responseToJSON(ModelResponseInfo modelResponseInfo) throws SystemException {
        String response = null;
        try {
            response = mapper.writeValueAsString(modelResponseInfo);
        } catch (JsonParseException | JsonMappingException e) {
            LOGGER.error("Exception occured while parsing response json", e);
            SystemException.newSystemException(ErrorCodes.ME0004, new String[] { "response", e.getMessage() }, e);
        } catch(IOException e) {
            LOGGER.error("Exception occured while parsing response json", e);
            SystemException.newSystemException(ErrorCodes.ME0004, new String[] { "response", e.getMessage() }, e);
        }
        return response;
    }
	
}
