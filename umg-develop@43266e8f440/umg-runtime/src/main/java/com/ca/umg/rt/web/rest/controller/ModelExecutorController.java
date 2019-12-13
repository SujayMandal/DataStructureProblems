/**
* 
*/
package com.ca.umg.rt.web.rest.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.pool.model.PoolStatus;
import com.ca.pool.model.TransactionCriteria;
import com.ca.umg.me2.bo.ModelExecutorBO;
import com.ca.umg.me2.util.ModelExecResponse;

/**
 * @author kamathan
 *
 */
@Controller
@RequestMapping("modelExecEngine")
@SuppressWarnings("PMD")
public class ModelExecutorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelExecutorController.class);

    @Inject
    private SystemParameterProvider systemParameterProvider;

    @Inject
    private ModelExecutorBO modelExecutorBO;

    /*
     * method to refresh the me2 cache
     * 
     * @return
     */
    @RequestMapping(value = "/refreshMe2Cache", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ModelExecResponse<String> refreshMe2Cache() {
        LOGGER.info("Received request for refreshRetryCount ");
        ModelExecResponse<String> execResponse = new ModelExecResponse<String>();
        systemParameterProvider.refreshCache();
        execResponse.setResponse("Me2 Refresh cache initiated : ");
        LOGGER.info("Request completed for refreshRetryCount");
        return execResponse;
    }

    @RequestMapping(value = "/getAllModeletInfo", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public void getAllModeletInfo() {
        ModelExecResponse<String> execResponse = new ModelExecResponse<String>();
        try {
            modelExecutorBO.getAllModeletInfo();
            execResponse.setResponse("Me2 Refresh cache initiated : ");
        } catch (SystemException e) {
            execResponse.setSuccess(false);
            execResponse.setErrorCode(e.getCode());
            execResponse.setMessage(e.getLocalizedMessage());
            execResponse.setResponse("Error getting all modelet info");
        }
    }

    @RequestMapping(value = "/getProbablePoolAndCount", consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @ResponseBody
    public PoolStatus getProbablePoolandCount(@RequestBody final TransactionCriteria transactionCriteria) throws SystemException {
        return modelExecutorBO.getModeletPoolandCount(transactionCriteria);
    }

}