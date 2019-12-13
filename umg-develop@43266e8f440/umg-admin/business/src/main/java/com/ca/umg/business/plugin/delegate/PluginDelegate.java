/**
 * 
 */
package com.ca.umg.business.plugin.delegate;

import java.util.Map;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;

/**
 * @author raddibas
 *
 */
public interface PluginDelegate {
    
    /**
     * gets the plugins mapped to tenant
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    Map<String, Boolean> getPluginsMappedForTenant() throws SystemException, BusinessException ;
    
    byte[] getModelTemplate(final String modelName, final ModelType modelType) throws SystemException, BusinessException;

}
