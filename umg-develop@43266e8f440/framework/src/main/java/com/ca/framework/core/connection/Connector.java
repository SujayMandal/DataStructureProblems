/**
 * 
 */
package com.ca.framework.core.connection;

import com.ca.framework.core.exception.SystemException;

/**
 * @author kamathan
 *
 */
public interface Connector {

    /**
     * 
     * @throws SystemException
     */
    public void openConnection() throws SystemException;

    /**
     * 
     * @param command
     * @throws SystemException
     */
    public boolean executeCommand(String command) throws SystemException;

    /**
     * Fetch result of command
     * @param command
     *
     * @return
     *
     * @throws SystemException
     */
    String getExecuteCommandResult(String command) throws SystemException;

    /**
     * 
     * @throws SystemException
     */
    public void closeConnection() throws SystemException;

    /**
     * 
     * @param connectionAttribute
     * @throws SystemException
     */
    public void setConnectionAttributes(ConnectionAttribute connectionAttribute) throws SystemException;

}
