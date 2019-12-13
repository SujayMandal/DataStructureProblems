/**
 * 
 */
package com.ca.umg.business.version.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.version.command.base.AbstractCommand;

/**
 * THis class has no implementation now. It will be used while edit version flow is created.
 * 
 * @author chandrsa
 *
 */
public class ValidateMapping extends AbstractCommand {

    private final static Logger LOGGER = LoggerFactory.getLogger(ValidateMapping.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.Command#execute(java.lang.Object)
     */
    @Override
    public void execute(Object data) throws BusinessException, SystemException {
        LOGGER.debug("ValidateMapping command executed!");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.Command#rollback(java.lang.Object)
     */
    @Override
    public void rollback(Object data) throws BusinessException, SystemException {
        LOGGER.debug("ValidateMapping rollback command executed!");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.version.command.Command#isCreated()
     */
    @Override
    public boolean isCreated() throws BusinessException, SystemException {
        return true;
    }

}
