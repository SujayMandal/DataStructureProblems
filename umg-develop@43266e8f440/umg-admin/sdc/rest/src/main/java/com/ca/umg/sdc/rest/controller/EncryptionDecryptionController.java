package com.ca.umg.sdc.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.altisource.common.exception.SystemException;
import com.ca.framework.core.encryption.EncryptionUtil;
import com.ca.umg.sdc.rest.utils.RestResponse;
@Controller
@RequestMapping("/security")
public class EncryptionDecryptionController {
	private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionDecryptionController.class);
	private static final String ERROR_MSG = "Given Token is Invalid EncryptionDecryption Issue";
	@RequestMapping(value = "/enc/{encString}", method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<String> getEncryptedToken(@PathVariable final String encString) {
		final RestResponse<String> response = new RestResponse<>();
		try{
			String token = EncryptionUtil.encryptToken(encString);
		    response.setResponse(token);
		}
		 catch (SystemException e){
		LOGGER.error(ERROR_MSG, e);
		response.setError(true);
		response.setMessage(ERROR_MSG);
		}
		return response;
		
	}
	
	@RequestMapping(value = "/dec/{decString}", method = RequestMethod.GET)
	@ResponseBody
	public RestResponse<String> getDecryptedToken(@PathVariable final String decString) {
		final RestResponse<String> response = new RestResponse<>();
		try{
			String token = EncryptionUtil.decryptToken(decString);
		    response.setResponse(token);
		}
		 catch (SystemException e){
		LOGGER.error(ERROR_MSG, e);
		response.setError(true);
		response.setMessage(ERROR_MSG);
		}
		return response;
		
	}
}
