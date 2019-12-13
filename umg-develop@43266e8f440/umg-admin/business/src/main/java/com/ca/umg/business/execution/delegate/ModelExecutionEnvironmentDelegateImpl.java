/**
 * 
 */
package com.ca.umg.business.execution.delegate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.execution.bo.ModelExecutionEnvironmentBO;
import com.ca.umg.business.execution.entity.ModelExecutionEnvironment;
import com.ca.umg.business.model.info.ModelExecutionEnvironmentInfo;

/**
 * @author nigampra
 *
 */

@Component
public class ModelExecutionEnvironmentDelegateImpl extends AbstractDelegate implements ModelExecutionEnvironmentDelegate {

    @Inject
    private ModelExecutionEnvironmentBO executionEnvironmentBO;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ca.umg.business.execution.delegate.ModelExecutionEnvironmentDelegate#getModelExecutionEnvironmentInfo(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ModelExecutionEnvironmentInfo getModelExecutionEnvironment(String executionEnvironment, String environmentVersion)
            throws BusinessException, SystemException {
        return getModelExecEnvt(executionEnvironment, environmentVersion);
    }
    
    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getSupportLibManage())")
    public ModelExecutionEnvironmentInfo getModelExnEnvtListLibraries(final String executionEnvironment, final String environmentVersion)
            throws BusinessException, SystemException {
        return getModelExecEnvt(executionEnvironment, environmentVersion);
    }
    
    private ModelExecutionEnvironmentInfo getModelExecEnvt(String executionEnvironment, String environmentVersion) 
            throws BusinessException, SystemException {
        ModelExecutionEnvironment modelExecutionEnvironment = executionEnvironmentBO.getModelExecutionEnvironment(
                executionEnvironment, environmentVersion);
        return convert(modelExecutionEnvironment, ModelExecutionEnvironmentInfo.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ca.umg.business.execution.delegate.ModelExecutionEnvironmentDelegate#getModelExecutionEnvironmentInfo(java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<String> getAllRModelExecutionEnvironment() throws BusinessException, SystemException {
        boolean isAdminAware = RequestContext.getRequestContext().isAdminAware();
        RequestContext.getRequestContext().setAdminAware(true);
        List<ModelExecutionEnvironment> modelExecutionEnvironments = (List<ModelExecutionEnvironment>) executionEnvironmentBO
                .getAllRModelExecutionEnvironment();
        RequestContext.getRequestContext().setAdminAware(isAdminAware);
        List<String> modelExecutionEnvs = new ArrayList<String>();
        for (ModelExecutionEnvironment modelExecutionEnvironment : modelExecutionEnvironments) {
            modelExecutionEnvs.add(modelExecutionEnvironment.getExecutionEnvironment() + BusinessConstants.CHAR_HYPHEN
                    + modelExecutionEnvironment.getEnvironmentVersion());
        }
        return modelExecutionEnvs;
    }

}
