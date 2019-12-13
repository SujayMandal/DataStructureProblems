/**
 * 
 */
package com.ca.umg.business.execution.bo;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.ModeletClientInfo;
import com.ca.umg.business.execution.entity.ModelExecutionEnvironment;
import com.ca.umg.business.model.info.ModelExecutionEnvironmentInfo;

/**
 * @author nigampra
 *
 */
public interface ModelExecutionEnvironmentBO {

    ModelExecutionEnvironment getModelExecutionEnvironment(final String executionEnvironment, final String environmentVersion)
            throws BusinessException, SystemException;

    /**
     * This method used to get all R environments
     * 
     * @return list of R Environments
     * @throws BusinessException
     * @throws SystemException
     */
    List<ModelExecutionEnvironment> getAllRModelExecutionEnvironment() throws BusinessException, SystemException;

    ModelExecutionEnvironment getModelExecutionEnvByName(final String name);
    
    String getActiveRVersion(String executionEnvironment);

    /**
     * fetch execution environment by environment id
     * @param id
     *
     * @return
     *
     * @throws SystemException
     */
    ModelExecutionEnvironmentInfo getModelExecutionEnvById(String id) throws SystemException;

    /**
     * Get active execution environment list
     *
     * @return
     *
     * @throws SystemException
     */
    List<ModelExecutionEnvironmentInfo> getActiveModelExecutionEnvList() throws SystemException;

    public void restartModelets(List<ModeletClientInfo> modeletClientInfoList) throws SystemException, BusinessException;

    public String downloadModeletLogs(ModeletClientInfo modeletClientInfo) throws SystemException, BusinessException;
}
