package com.ca.umg.business.plugin.bo;

import java.util.Map;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;

/**
 * @author raddibas
 *
 */
public interface PluginBO {
    
    /**
     * gets the plugins mapped to tenant
     * @param tenantCode
     * @param type
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    Map<String, Boolean> getPluginsMappedForTenant(String tenantCode, String type) throws SystemException, BusinessException ;

}
