/**
 *
 */
package com.ca.pool.manager;

import java.util.Map;

import com.ca.framework.core.entity.ModeletRestartInfo;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.ModeletClientInfo;

/**
 * @author kamathan
 *
 */
public interface ModeletManager {

    String RESTART_REASON_1 = "Transaction Timeout";
    String RESTART_REASON_2 = "Model Execution Failed";
    String RESTART_REASON_3 = "Model Change";
    String RESTART_REASON_4 = "Pool Change";
    String RESTART_REASON_5 = "Modelet Restart setup count reached";
    String RESTART_REASON_6 = "Model change on pool update";
    String EXEC_RESTART_COUNT_NOT_PRESENT = "Restart Transaction Count not set up for Model";

    /**
     *
     * @param modeletClientInfo
     * @param connectionType
     * @throws SystemException
     */
    public void startModelet(ModeletClientInfo modeletClientInfo, String connectionType)
            throws SystemException, BusinessException;

    /**
     *
     * @param modeletClientInfo
     * @throws SystemException
     */
    public void stopModelet(ModeletClientInfo modeletClientInfo) throws SystemException;

    /**
     *
     * @param modeletClientInfo
     * @throws SystemException
     */
    public void restartModelet(ModeletClientInfo modeletClientInfo, Map<String, String> info) throws SystemException;

    public long getModeletStartDelay();

    public long getModeletStopDelay();

    public boolean isModeletRestartReq(ModeletClientInfo info);

    public ModeletRestartInfo getRestartAndExecCount(ModeletClientInfo info);

    public void setExecCounttoZero(ModeletClientInfo info);

    public void startRServe(ModeletClientInfo modeletClientInfo) throws SystemException;

    String fetchModeletResponse(ModeletClientInfo modeletClientInfo, String connectionType) throws SystemException, BusinessException;

    String fetchModeletLogs(ModeletClientInfo modeletClientInfo, String connectionType) throws SystemException;
}
