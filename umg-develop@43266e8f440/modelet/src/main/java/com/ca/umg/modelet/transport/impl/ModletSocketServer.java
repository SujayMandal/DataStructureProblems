package com.ca.umg.modelet.transport.impl;

import java.io.IOException;
import java.net.ServerSocket;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.transport.ModletServer;
import com.ca.umg.modelet.transport.handler.ModeletSocketHandler;

@Named(value = "socketModletServer")
@SuppressWarnings("PMD")
public class ModletSocketServer implements ModletServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModletSocketServer.class);

    private static final String ZERO_IP_ADRS = "0.0.0.0";

    @Inject
    @Named(value = "socketHandler")
    private ModeletSocketHandler socketHandler;

    private ServerSocket serverSocket;

    public void initializeServer(final int port) throws SystemException {
        LOGGER.debug("Initializing modelet server on port {}.", port);
        try {
            serverSocket = new ServerSocket(Integer.valueOf(port));
            final Runnable runnable = new Runnable() {
                public void run() {
                    try {
                        socketHandler.handle(serverSocket);
                    } catch (SystemException e) {
                        LOGGER.error("Thread execution halted due to exception", e);
                    }
                }
            };
            final Thread handlerThread = new Thread(runnable);
            handlerThread.start();
            LOGGER.debug("Initialized server on port {} successfully.", port);
        } catch (IOException e) {
            SystemException.newSystemException("", new String[] { "" }, e);
        } catch (IllegalArgumentException e) {
            SystemException.newSystemException("", new String[] { "" }, e);
        }
    }

    public void destroyServer() throws SystemException {
        LOGGER.info("Destroy called on server {}.", serverSocket.getInetAddress().getHostAddress());
        try {
            if (!serverSocket.getInetAddress().getHostAddress().equals(ZERO_IP_ADRS) && serverSocket != null
                    && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            SystemException.newSystemException("", new String[] { "" }, e);
        }
    }

}
