/**
 *
 */
package com.ca.framework.core.connection;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.connection.identity.SshUser;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;
import com.ca.framework.core.systemparameter.SystemParameterConstants;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author kamathan
 *
 */
@Named(SSHConnector.BEAN_NAME)
@Scope("prototype")
public class SSHConnector implements Connector {

	private static final Logger LOGGER = LoggerFactory.getLogger(SSHConnector.class);

	public static final String BEAN_NAME = "SSHConnector";

	private Session sshSession;

	private static final String EXECUTION_CHANNEL_NAME = "exec";

	private ConnectionAttribute connectionAttribute;

	@Inject
	private CacheRegistry cacheRegistry;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ca.umg.me2.connection.Connector#connect()
	 */
	@Override
	public void openConnection() throws SystemException {
		LOGGER.info("Received request to connect to server {} with user name {}.", connectionAttribute.getHost(), connectionAttribute.getUsername());
		try {
			JSch jSch = new JSch();
			if(connectionAttribute.getIdentityKey() != null) {
				jSch.addIdentity(connectionAttribute.getIdentityKey());
			}
			sshSession = jSch.getSession(connectionAttribute.getUsername(), connectionAttribute.getHost(),
					Integer.parseInt((String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER).get(SystemParameterConstants.SSH_PORT)));

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
			SystemException.newSystemException(FrameworkExceptionCodes.MSE0000504,
					new Object[] {connectionAttribute.getHost(), connectionAttribute.getUsername()});
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ca.umg.me2.connection.Connector#execute(java.lang.String)
	 */
	@Override
	public boolean executeCommand(String command) throws SystemException {
		LOGGER.info("Recieved request to execute {} commands.", command);
		Channel executionChannel = null;
		try {
			executionChannel = sshSession.openChannel(EXECUTION_CHANNEL_NAME);

			if(executionChannel != null) {
				LOGGER.info("Recieved request to execute command {}.", command);
				((ChannelExec) executionChannel).setCommand(command);
				executionChannel.setInputStream(null);

				ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
				((ChannelExec) executionChannel).setErrStream(errorStream);
				executionChannel.connect();
				LOGGER.info("Connection successful for execution of command {}." + command);

				LOGGER.info("Error stream" + errorStream == null ? "" : errorStream.toString());

				LOGGER.info("Execucted command {} successfully.", command);

			} else {
				LOGGER.error("Could not open connection to remote host {} with user {}.", connectionAttribute.getHost(),
						connectionAttribute.getUsername());
			}
		} catch (JSchException e) {
			LOGGER.error("An error occurred while executing command.", e);
			SystemException.newSystemException(FrameworkExceptionCodes.MSE0000505, new Object[] {command});
		} catch (Exception e) { // NOPMD
			LOGGER.error("Generic exception occurred while tryig execute command.", e);
			SystemException.newSystemException(FrameworkExceptionCodes.MSE0000505, new Object[] {command});
		} finally {
			LOGGER.info("DIsconnecting the connection for execution.");
			if(executionChannel != null) {
				executionChannel.disconnect();
			}
		}
		return true;
	}

	@Override
	public String getExecuteCommandResult(String command) throws SystemException {
		LOGGER.info("Recieved request to fetch {} commands result.", command);

		String result = "";
		StringBuilder outputBuffer = new StringBuilder();
		Channel executionChannel = null;
		try {
			executionChannel = sshSession.openChannel(EXECUTION_CHANNEL_NAME);

			if(executionChannel != null) {
				LOGGER.info("Recieved request to execute command {} result.", command);
				((ChannelExec) executionChannel).setCommand(command);
				executionChannel.setInputStream(null);

				ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
				((ChannelExec) executionChannel).setErrStream(errorStream);
				InputStream in = executionChannel.getInputStream();
				executionChannel.connect();
				LOGGER.info("Connection successful for execution of command {} result.", command);

				int readByte = in.read();
				while (readByte != 0xffffffff) {
					outputBuffer.append((char) readByte);
					readByte = in.read();
				}
				result = outputBuffer.toString();

                /*byte[] tmp = new byte[1024];
                while (true) {
                    while (in.available() > 0) {
                        int i = in.read(tmp, 0, 1024);
                        if (i < 0) break;
                        ret = new String(tmp, 0, i);
                    }
                    if (executionChannel.isClosed()) break;
                }*/

				LOGGER.info("Error stream" + errorStream == null ? "" : errorStream.toString());

				LOGGER.info("Execucted command {} result fetched successfully with result : {}", command, result);

			} else {
				LOGGER.error("Could not open connection to remote host {} with user {}.", connectionAttribute.getHost(),
						connectionAttribute.getUsername());
			}
		} catch (JSchException e) {
			LOGGER.error("An error occurred while executing command.", e);
			SystemException.newSystemException(FrameworkExceptionCodes.MSE0000505, new Object[] {command});
		} catch (Exception e) { // NOPMD
			LOGGER.error("Generic exception occurred while tryig execute command.", e);
			SystemException.newSystemException(FrameworkExceptionCodes.MSE0000505, new Object[] {command});
		} finally {
			LOGGER.info("DIsconnecting the connection for execution.");
			if(executionChannel != null) {
				executionChannel.disconnect();
			}
		}

		LOGGER.info("Results is : {}", result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ca.umg.me2.connection.Connector#close()
	 */
	@Override
	public void closeConnection() throws SystemException {
		LOGGER.info("Received request to close the connection to server {}.");
		sshSession.disconnect();
		// assign connection object to null to forcefully get garbage collected
		sshSession = null; // NOPMD
		LOGGER.info("Connection closed successfully to server {}.");
	}

	@Override
	public void setConnectionAttributes(ConnectionAttribute connectionAttribute) throws SystemException {
		if(connectionAttribute == null) {
			SystemException.newSystemException(FrameworkExceptionCodes.MSE0000506, new Object[] {});
		}
		this.connectionAttribute = connectionAttribute;
	}

}
