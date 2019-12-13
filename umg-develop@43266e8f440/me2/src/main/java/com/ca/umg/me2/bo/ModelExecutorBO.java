/**
 * 
 */
package com.ca.umg.me2.bo;

import java.util.List;
import java.util.Map;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.model.PoolAllocationInfo;
import com.ca.pool.model.PoolStatus;
import com.ca.pool.model.PoolStatusStats;
import com.ca.pool.model.TransactionCriteria;
import com.ca.umg.me2.util.ModeletResult;

/**
 * @author kamathan
 *
 */
public interface ModelExecutorBO {

    public Map<String, Object> executeModel(final String modelInfo, final ModeletResult modeletResult)
            throws SystemException, BusinessException;

    public void getAllModeletInfo() throws SystemException;

    public List<PoolStatusStats> getPoolStatusStats(final List<String> poolNames) throws SystemException;

    public void allocateModelets(List<PoolAllocationInfo> modeletClientInfoList) throws SystemException;

    public PoolStatus getModeletPoolandCount(TransactionCriteria transactionCriteria) throws SystemException;

    public void startModelet(final ModeletClientInfo modeletClientInfo) throws SystemException, BusinessException;

    public void stopModelet(final ModeletClientInfo modeletClientInfo) throws SystemException;

    void startRserveProcess(ModeletClientInfo modeletClientInfo) throws SystemException;

	String fetchModeletResponse(ModeletClientInfo modeletClientInfo) throws SystemException, BusinessException;

    public void restartModelets(List<ModeletClientInfo> modeletClientInfoList);

    String fetchModeletLogs(ModeletClientInfo modeletClientInfo) throws SystemException;
}
