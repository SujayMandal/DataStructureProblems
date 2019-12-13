package com.ca.umg.sdc.rest.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.umg.sdc.rest.utils.RestResponse;

@Controller
@RequestMapping("/dragAndDrop")
public class SourceDNDController {
    // private static final Logger LOGGER = LoggerFactory.getLogger(SourceDNDController.class);
    @RequestMapping(value = "/source")
    @ResponseBody
    public RestResponse<Object> getSource() {
        RestResponse<Object> response = new RestResponse<Object>();
        // JSONObject jsonObj = new JSONObject("[{ \"text\" : \"Borrower\"}]");
        Object[] data = new Object[2];
        Map<String, Object> responseObject = new HashMap<String, Object>();
        responseObject.put("text", "Borower");
        Map<String, Object> responseObject1 = new HashMap<String, Object>();
        responseObject1.put("text", "Borower1");
        Map<String, Object> responseObject2 = new HashMap<String, Object>();
        responseObject2.put("text", "borrower11");
        responseObject1.put("children", new Object[] { responseObject2 });

        data[0] = responseObject;
        data[1] = responseObject1;
        /*
         * try { source="dasd";
         * 
         * } catch (BusinessException | SystemException e) { logger.error(e.getLocalizedMessage(), e);
         * response.setErrorCode(e.getCode()); response.setError(true); response.setMessage(e.getLocalizedMessage()); return
         * response; }
         */
        response.setError(false);
        response.setMessage("Done");
        response.setResponse(data);
        return response;
    }

}
