/**
 * 
 */
package com.ca.framework.core.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;

/**
 * @author kamathan
 *
 */
@Named(LocalConnector.BEAN_NAME)
public class LocalConnector implements Connector {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalConnector.class);

    public static final String BEAN_NAME = "LocalConnector";

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.framework.core.connection.Connector#openConnection()
     */
    @Override
    public void openConnection() throws SystemException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.framework.core.connection.Connector#executeCommand(java.util.List)
     */
    @Override
    public boolean executeCommand(String command) throws SystemException {
        String cmdTrace;
        BufferedReader stdError = null;
        try {
            Process p = Runtime.getRuntime().exec(command);
            int exitStatus = p.waitFor();
            // read any errors from the attempted command
            if (exitStatus != 0) {
                stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                LOGGER.error("R Serve command execution failed because of error.");
                while ((cmdTrace = stdError.readLine()) != null) {
                    LOGGER.error(cmdTrace);
                }
                return false;
            } else {
                LOGGER.error("R Serve command execution successful.");
            }
            return true;
        } catch (IOException | InterruptedException e) {
            LOGGER.error("R Serve command execution failed : ", e);
            return false;
        } finally {
            if (stdError != null) {
                try {
                    stdError.close();
                } catch (IOException e) {
                    stdError = null;
                }
            }
        }
    }

    @Override
    public String getExecuteCommandResult(String command) throws SystemException {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.framework.core.connection.Connector#closeConnection()
     */
    @Override
    public void closeConnection() throws SystemException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.ca.framework.core.connection.Connector#setConnectionAttributes(com.ca.framework.core.connection.ConnectionAttribute)
     */
    @Override
    public void setConnectionAttributes(ConnectionAttribute connectionAttribute) throws SystemException {
        // TODO Auto-generated method stub

    }

}
