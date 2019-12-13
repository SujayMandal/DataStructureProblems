package com.ca.umg.rt.batching.delegate;

import java.util.List;
import java.util.Map;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.rt.batching.data.BatchFileStatusInput;

/**
 * @author basanaga
 * 
 */
public interface BatchFileStatusDelegate {

    /**
     * This method used to get the batch file status for the tenant input file name and date
     * 
     * @param batchFileStatusInput
     * @return
     * @throws BusinessException
     */
    List<Map<String, Object>> getBatchFileStatus(BatchFileStatusInput batchFileStatusInput) throws BusinessException;

}
