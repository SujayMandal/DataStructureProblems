package com.ca.umg.rt.web.rest.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.rt.batching.data.BatchFileStatusInput;
import com.ca.umg.rt.batching.data.BatchFileStatusResponse;
import com.ca.umg.rt.batching.delegate.BatchFileStatusDelegate;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;



/**
 * 
 * @author basanaga
 * 
 */
@Controller
@RequestMapping("/api/batchfile")
public class BatchFileStatusController {
    
    @Inject
    private BatchFileStatusDelegate batchFileStatusDelegate;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchFileStatusController.class);

    private static final String ERROR_MESSAGE = "errorMessage";


    /**
     * This method returns status of all the batch files with the tenant input filename
     * 
     * @param fileName
     * @param extn
     * @return
     */
    @RequestMapping(value = "/status/{fileName}/{extn}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    BatchFileStatusResponse getBatchFileStatus(@PathVariable String fileName, @PathVariable String extn) {
        BatchFileStatusResponse batchFileStatusResponse = new BatchFileStatusResponse();
        List<Map<String, Object>> batchStatusInfoList = new ArrayList<Map<String, Object>>();
        Map<String, Object> header = new LinkedHashMap<String, Object>();
        header.put("Tenant input file name", fileName + "/" + extn);
        try {
            BatchFileStatusInput batchFileStatusInput = new BatchFileStatusInput();
            batchFileStatusInput.setFileName(fileName + RuntimeConstants.CHAR_DOT + extn);
            batchStatusInfoList = batchFileStatusDelegate.getBatchFileStatus(batchFileStatusInput);
            header.put("count", batchStatusInfoList.size());
            setData(batchFileStatusResponse, batchStatusInfoList, header);
        } catch (BusinessException exception) {
            setHeader(batchFileStatusResponse, header, exception.getLocalizedMessage());
        } catch (Exception exception) { // NOPMD
            setHeader(batchFileStatusResponse, header, exception.getMessage());
        }
        return batchFileStatusResponse;
    }



    private void setData(BatchFileStatusResponse batchFileStatusResponse, List<Map<String, Object>> batchStatusInfoList,
            Map<String, Object> header) {
        if (batchStatusInfoList.size() <= RuntimeConstants.INT_ZERO) {
            Map<String, Object> batchStatus = new LinkedHashMap<String, Object>();
            batchStatus.put("Batch status", RuntimeConstants.YET_TO_BE_PICKED);
            batchStatusInfoList.add(batchStatus);
        }
        header.put(RuntimeConstants.BATCH_API_STATUS, RuntimeConstants.SUCCESS);
        batchFileStatusResponse.setHeader(header);
        batchFileStatusResponse.setData(batchStatusInfoList);
    }



    /**
     * This method returns status of all the batch files with the tenant input filename and the date
     * 
     * @param fileName
     * @param extn
     * @param date
     * @return
     */
    @RequestMapping(value = "/status/{fileName}/{extn}/{date}", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    BatchFileStatusResponse getBatchFileStatus(@PathVariable String fileName, @PathVariable String extn, @PathVariable String date) {
        BatchFileStatusResponse batchFileStatusResponse = new BatchFileStatusResponse();
        LOGGER.info("fileName is " + fileName + " date is :" + date);
        List<Map<String, Object>> batchStatusInfoList = new ArrayList<Map<String, Object>>();
        Map<String, Object> header = new LinkedHashMap<String, Object>();
        header.put("Tenant input file name", fileName + "/" + extn);
        try {
            BatchFileStatusInput batchFileStatusInput = new BatchFileStatusInput();
            batchFileStatusInput.setFileName(fileName + RuntimeConstants.CHAR_DOT + extn);
            batchFileStatusInput.setDate(date);
            batchStatusInfoList = batchFileStatusDelegate.getBatchFileStatus(batchFileStatusInput);
            header.put("count", batchStatusInfoList.size());
            setData(batchFileStatusResponse, batchStatusInfoList, header);
        } catch (BusinessException exception) {
            setHeader(batchFileStatusResponse, header, exception.getLocalizedMessage());
        } catch (Exception exception) { // NOPMD
            setHeader(batchFileStatusResponse, header, exception.getMessage());
        }
        return batchFileStatusResponse;
    }



    private void setHeader(BatchFileStatusResponse batchFileStatusResponse, Map<String, Object> header, String expMessage) {
        header.put(RuntimeConstants.BATCH_API_STATUS, RuntimeConstants.ERROR);
        header.put(ERROR_MESSAGE, expMessage);
        batchFileStatusResponse.setHeader(header);
    }


}
