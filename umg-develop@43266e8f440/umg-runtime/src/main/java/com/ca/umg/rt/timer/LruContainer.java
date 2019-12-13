/**
 * 
 */
package com.ca.umg.rt.timer;

import com.ca.framework.core.exception.SystemException;

/**
 * @author chandrsa
 * 
 */
public interface LruContainer {

    public BatchLru getBatchLRU(String batchId);

    public void setBatchLRU(String batchId, BatchLru batchLru) throws SystemException;

    public BatchLru removeBatchLRU(String batchId);
}
