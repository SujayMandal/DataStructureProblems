/**
 * 
 */
package com.ca.umg.util.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * @author kamathan
 *
 */
public class SSHConnector implements Connector {

    private static final Logger LOGGER = LoggerFactory.getLogger(SSHConnector.class);

    private Session sshSession;

    private ConnectionAttribute connectionAttribute;

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.me2.connection.Connector#connect()
     */
    @Override
    public void openConnection() {
        LOGGER.info("Received request to connect to server {} with user name {}.", connectionAttribute.getHost(),
                connectionAttribute.getUsername());
        try {
            JSch jSch = new JSch();
            if (connectionAttribute.getIdentityKey() != null) {
                jSch.addIdentity(connectionAttribute.getIdentityKey());
            }
            sshSession = jSch.getSession(connectionAttribute.getUsername(), connectionAttribute.getHost(), 22);

            SshUser sshUser = new SshUser();
            sshUser.setPassword(connectionAttribute.getPassword());
            sshSession.setUserInfo(sshUser);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(config);

            sshSession.connect();
            LOGGER.info("Connected successfully to host {}.", connectionAttribute.getHost());
        } catch (JSchException e) {
            LOGGER.error("An error occurred while connecting to {} with user name {}.", connectionAttribute.getHost(),
                    connectionAttribute.getUsername(), e);

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.me2.connection.Connector#close()
     */
    @Override
    public void closeConnection() {
        if (sshSession.isConnected()) {
            LOGGER.info("Received request to close the connection to server {}.");
            sshSession.disconnect();
            LOGGER.info("Connection closed successfully to server {}.");
        }
        // assign connection object to null to forcefully get garbage collected
        sshSession = null; // NOPMD
    }

    @Override
    public void setConnectionAttributes(ConnectionAttribute connectionAttribute) {
        this.connectionAttribute = connectionAttribute;
    }

}
