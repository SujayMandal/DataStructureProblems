package com.ca.umg.sdc.rest.controller;

import java.util.List;
import java.util.Set;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.mappingnotification.delegate.MappingNotificationDelegate;
import com.ca.umg.business.mappingnotification.entity.MappingNotification;
import com.ca.umg.business.mappingnotification.entity.NotificationData;
import com.ca.umg.notification.model.MailDetails;
import com.ca.umg.sdc.rest.utils.RestResponse;

@Controller
@RequestMapping("/mappingNotification")
@SuppressWarnings("PMD")
public class MappingNotificationController {
	private static final Logger LOGGER = LoggerFactory.getLogger(MappingNotificationController.class);

	@Inject
	private MappingNotificationDelegate delegate;

	@RequestMapping(value = "/createMapping",  consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<String> createMapping(@RequestBody final MappingNotification mn) {
		LOGGER.info("Request reached for creating a new createMapping. New mapping Details : {}" + mn);	
		final RestResponse<String> response = new RestResponse<>();
		try {
				delegate.createMapping(mn);
				response.setMessage("Notification added successfully ");
				response.setError(false);
		//send succuss
		} catch (BusinessException | SystemException e) {
			 LOGGER.error(e.getLocalizedMessage());
             response.setErrorCode(e.getCode());
             response.setError(true);
             response.setMessage(e.getLocalizedMessage());
             
             //send failure message
		} catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            //response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage("Notification Add is failed. Try again in some time or contact RA support at RealAnalyticsSupport@altisource.com for "
            		+ "assistance’ is displayed to the user");
        }
		
		return response;
	}

	@RequestMapping(value = "/updateMapping",  consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<String> updateMapping(@RequestBody final MappingNotification nd) {
		LOGGER.info("Request reached for updateMapping. New Mapping Details : {}" + nd);		
		final RestResponse<String> response = new RestResponse<>();
		try {
			delegate.updateMapping(nd);
			response.setError(false);
			response.setMessage("Notification edited successfully");
		} catch (BusinessException | SystemException e) {
			 LOGGER.error(e.getLocalizedMessage());
             response.setErrorCode(e.getCode());
             response.setError(true);
             response.setMessage(e.getLocalizedMessage());
             //send failure message
		} catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            //response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage("Notification Update is failed. Try again in some time or contact RA support at RealAnalyticsSupport@altisource.com for "
            		+ "assistance’ is displayed to the user");
        } 
		return response;
	}

	
	@RequestMapping(value = "/deleteMapping",method = RequestMethod.POST)
	@ResponseBody
	public RestResponse<String> deleteMapping(@RequestParam(value = "id") String id) {
		LOGGER.info("Request reached forupdateMapping. id is : {}" + id);		
		final RestResponse<String> response = new RestResponse<>();
		try {
			delegate.deleteMapping(id);
			response.setError(false);
			response.setMessage("Notification deleted successfully");
		} catch (BusinessException | SystemException e) {
			 LOGGER.error(e.getLocalizedMessage());
             response.setErrorCode(e.getCode());
             response.setError(true);
             response.setMessage(e.getLocalizedMessage());
		} catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            //response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage("Notification Delete is failed. Try again in some time or contact RA support at RealAnalyticsSupport@altisource.com for "
            		+ "assistance’ is displayed to the user");
        }
		return response;
	}

	
	@RequestMapping(value = "/getMappingDataByMappingId",  method= RequestMethod.POST)
	@ResponseBody
	public RestResponse<MailDetails> getMappingDataByMappingId(@RequestParam(value="id") String mappingID) {
		MailDetails  mailDetails = null;
		RestResponse<MailDetails>  response = new RestResponse<MailDetails>();
		RequestContext requestContext = RequestContext.getRequestContext();
		try {
			requestContext.setAdminAware(true);
			mailDetails = delegate.getMappingDataByMappingId(mappingID);
			response.setResponse(mailDetails);
			response.setError(false);
		} catch (BusinessException | SystemException e) {
			 LOGGER.error(e.getLocalizedMessage());
             response.setErrorCode(e.getCode());
             response.setError(true);
             response.setMessage(e.getLocalizedMessage());
		} catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            //response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage("Fecthing Notification Details is failed. Try again in some time or contact RA support at RealAnalyticsSupport@altisource.com for "
            		+ "assistance’ is displayed to the user");
        } finally{
			requestContext.setAdminAware(false);
		} 
		return response;
	}

	@RequestMapping(value = "/getAllMappingData", method= RequestMethod.GET)
	@ResponseBody
	public RestResponse<List<NotificationData>> getAllFetcherEvents() {
		RestResponse<List<NotificationData>> response = new RestResponse<List<NotificationData>>();
        List<NotificationData>  mailDetails = null;
        try {
               mailDetails = delegate.getAllFeatureNotification();
               response.setResponse(mailDetails);
               response.setError(false);
        } catch (BusinessException | SystemException e) {
               LOGGER.error(e.getLocalizedMessage());
               response.setErrorCode(e.getCode());
               response.setError(true);
               response.setMessage(e.getLocalizedMessage());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            //response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage("Fecthing Notification Details is failed. Try again in some time or contact RA support at RealAnalyticsSupport@altisource.com for "
            		+ "assistance’ is displayed to the user");
        }
        return response;
	}
	
	@RequestMapping(value = "/getNotificationEventNames", method= RequestMethod.GET)
	@ResponseBody
	public RestResponse<List<NotificationData>> getAllEvents() {
		RestResponse<List<NotificationData>> response = new RestResponse<List<NotificationData>>();
		 List<NotificationData>  eventDetails = null;
        try {
        	   eventDetails = delegate.getAllEventDetails();
               response.setResponse(eventDetails);
               response.setError(false);
        } catch (BusinessException | SystemException e) {
               LOGGER.error(e.getLocalizedMessage());
               response.setErrorCode(e.getCode());
               response.setError(true);
               response.setMessage(e.getLocalizedMessage());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            //response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage("Fecthing Notification Details is failed. Try again in some time or contact RA support at RealAnalyticsSupport@altisource.com for "
            		+ "assistance’ is displayed to the user");
        }
        
        return response;
	}
	
	@RequestMapping(value = "/getAllModelNames", method= RequestMethod.GET)
	@ResponseBody
	public RestResponse<Set<String>> getAllModels() {
		RestResponse<Set<String>> response = new RestResponse<Set<String>>();
        Set<String> allModels = null;
        try {
               allModels = delegate.getAllModels();
               response.setResponse(allModels);
               response.setError(false);
        } catch (BusinessException | SystemException e) {
               LOGGER.error(e.getLocalizedMessage());
               response.setErrorCode(e.getCode());
               response.setError(true);
               response.setMessage(e.getLocalizedMessage());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            //response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage("Fecthing Notification Details is failed. Try again in some time or contact RA support at RealAnalyticsSupport@altisource.com for "
            		+ "assistance’ is displayed to the user");
        }
        
        return response;
	}
	
	@RequestMapping(value = "/getTemplateDataByEventId",  method= RequestMethod.POST)
	@ResponseBody
	public RestResponse<MailDetails> getTemplateDataByEventId(@RequestParam(value="id") String eventID) {
		MailDetails  mailDetails = null;
		RestResponse<MailDetails>  response = new RestResponse<MailDetails>();
		try {
			mailDetails = delegate.getTemplateDataByEventId(eventID);
			response.setResponse(mailDetails);
			response.setError(false);
		} catch (BusinessException | SystemException e) {
			 LOGGER.error(e.getLocalizedMessage());
             response.setErrorCode(e.getCode());
             response.setError(true);
             response.setMessage(e.getLocalizedMessage());
		} catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            //response.setErrorCode(e.getCode());
            response.setError(true);
            response.setMessage("Fecthing Notification Template is failed. Try again in some time or contact RA support at RealAnalyticsSupport@altisource.com for "
            		+ "assistance’ is displayed to the user");
        }
		return response;
	}
}
