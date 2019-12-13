/**
 * 
 */
package com.ca.umg.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.umg.util.connector.ConnectionAttribute;
import com.ca.umg.util.connector.Connector;
import com.ca.umg.util.connector.SSHConnector;

/**
 * @author kamathan
 *
 */
public class ServerConnectivityTester {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConnectivityTester.class);

    /**
     * @param args
     */
    public static void main(String[] args) {

        String allTargetServer = System.getProperty("targetServers");
        String username = System.getProperty("username");
        String identityFile = System.getProperty("identityFile");
        String password = System.getProperty("develop");

        String[] targetServers = allTargetServer.split(",");

        for (String targetServer : targetServers) {

            LOGGER.info("Trying to connect to server : {} with user name  {}, id key file  {} and password  {}.", targetServer,
                    username, identityFile, password);
            Connector connector = new SSHConnector();
            ConnectionAttribute connectionAttribute = new ConnectionAttribute();
            connectionAttribute.setHost(targetServer);
            connectionAttribute.setUsername(username);
            connectionAttribute.setPassword(password);
            connectionAttribute.setIdentityKey(identityFile);

            connector.setConnectionAttributes(connectionAttribute);

            connector.openConnection();

            connector.closeConnection();
        }
        LOGGER.info("Exiting main class.");
    }
}
