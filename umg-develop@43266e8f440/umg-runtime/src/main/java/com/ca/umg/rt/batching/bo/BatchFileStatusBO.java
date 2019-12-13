package com.ca.umg.rt.batching.bo;

import java.util.List;
import java.util.Map;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;

/**
 * 
 * API to get the batch details based on the fileName, start time
 * 
 * Users of this API - Controller classes that read batch status from various sources.
 * 
 * @author basanaga
 * 
 */
public interface BatchFileStatusBO {


    /**
     * Retrieve all the batches based on the fileName and startTime
     * 
     * @param batchFileName
     * @param startTime
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    List<Map<String, Object>> getBatcheStatusByFileNameAndStartTime(String batchFileName, long startTime) throws SystemException,
            BusinessException;

}
