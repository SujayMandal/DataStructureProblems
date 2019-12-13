/**
 * 
 */
package com.ca.umg.business.execution.delegate;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.model.info.ModelExecutionEnvironmentInfo;

/**
 * @author nigampra
 *
 */
public interface ModelExecutionEnvironmentDelegate {

    ModelExecutionEnvironmentInfo getModelExecutionEnvironment(final String executionEnvironment, final String environmentVersion)
            throws BusinessException, SystemException;
    
    public ModelExecutionEnvironmentInfo getModelExnEnvtListLibraries(final String executionEnvironment, final String environmentVersion)
            throws BusinessException, SystemException;

    /**
     * This method used to get the R envionments Versions
     * 
     * @return list of R Versions
     * @throws BusinessException
     * @throws SystemException
     */
    List<String> getAllRModelExecutionEnvironment() throws BusinessException, SystemException;

}
