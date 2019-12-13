package com.ca.umg.modelet.transport.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.transport.ModletServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@Named(value="httpModletServer")
public class HttpModletServer implements ModletServer {
	
	private HttpServer server;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpModletServer.class);
	
	@Inject
	@Named(value="httpHandler")
	private HttpHandler httpHandler;

	public void initializeServer(final int port) throws SystemException {
	    LOGGER.info("Initializing server on port {}.", port);
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
		} catch (IOException e) {
            SystemException.newSystemException("", new String[] { "" }, e);
        } catch (IllegalArgumentException e) {
            SystemException.newSystemException("", new String[] { "" }, e);
        }
        server.createContext("/", httpHandler);
        server.setExecutor(Executors.newFixedThreadPool(1));
        server.start();
        LOGGER.info("Initialized server on port {} successfully.", port);
	}

	public void destroyServer() throws SystemException {
	    if(server != null) {
	        LOGGER.info("Destroy called on server {}.", server.getAddress().getHostName());
	        server.stop(0);
	    }
	}

}
