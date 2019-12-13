/**
 * 
 */
package com.ca.umg.rt.flows.container;

import org.springframework.context.ApplicationContext;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.rt.repository.IntegrationRepository;

/**
 * @author chandrsa
 * 
 */
public interface WrapperDeployer {

    /**
     * @param wrapperType
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    ApplicationContext deployWrapper(String wrapperType) throws SystemException,
            BusinessException;
    
    void setIntegrationRepository(IntegrationRepository integrationRepository);
    
    void setName(String name);
    
    void setParentContext(ApplicationContext parentContext);
}
