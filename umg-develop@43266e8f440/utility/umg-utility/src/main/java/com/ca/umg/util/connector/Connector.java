/**
 * 
 */
package com.ca.umg.util.connector;

import org.omg.CORBA.SystemException;

/**
 * @author kamathan
 *
 */
public interface Connector {

    /**
     * 
     * @throws SystemException
     */
    public void openConnection();

    /**
     * 
     * @throws SystemException
     */
    public void closeConnection();

    /**
     * 
     * @param connectionAttribute
     * @throws SystemException
     */
    public void setConnectionAttributes(ConnectionAttribute connectionAttribute);

}
