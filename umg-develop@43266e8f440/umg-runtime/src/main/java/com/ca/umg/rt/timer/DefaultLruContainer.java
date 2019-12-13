/**
 * 
 */
package com.ca.umg.rt.timer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Named;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;

/**
 * @author chandrsa
 * container for initialising/removing the lru 
 */
@Named
public class DefaultLruContainer implements LruContainer {

    private final Map<String, BatchLru> lruHolder = new ConcurrentHashMap<>();

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.rt.timer.LruContainer#getBatchLRU(java.lang.String)
     */
    @Override
    public BatchLru getBatchLRU(String batchId) {
        BatchLru batchLru = null;
        if (lruHolder.containsKey(batchId)) {
            batchLru = lruHolder.get(batchId);
        }
        return batchLru;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.rt.timer.LruContainer#setBatchLRU(java.lang.String, com.ca.umg.rt.timer.BatchLru)
     */
    @Override
    public void setBatchLRU(String batchId, BatchLru batchLru) throws SystemException {
        if (!lruHolder.containsKey(batchId)) {
            lruHolder.put(batchId, batchLru);
        } else {
            SystemException.newSystemException(RuntimeExceptionCode.RSE000501, new Object[] { "Batch LRU already exists." });
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.rt.timer.LruContainer#removeBatchLRU(java.lang.String)
     */
    @Override
    public BatchLru removeBatchLRU(String batchId) {
        BatchLru batchLru = null;
        if (lruHolder.containsKey(batchId)) {
            batchLru = lruHolder.remove(batchId);
        }
        return batchLru;
    }

}
