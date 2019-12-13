package com.ca.umg.modelet.transport.factory;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.common.ServerType;
import com.ca.umg.modelet.transport.ModletServer;
import com.ca.umg.modelet.transport.impl.HttpModletServer;
import com.ca.umg.modelet.transport.impl.ModletSocketServer;

@Named
public class ModeletServerFactory {
	
	@Inject
	@Named(value="httpModletServer")
	private HttpModletServer httpServer;
	
	@Inject
	@Named(value="socketModletServer")
	private ModletSocketServer socketServer;
	
	public ModletServer getServerInstance(final String type) {
	    ModletServer server = null;
	    switch(ServerType.valueOf(type.toUpperCase(Locale.getDefault()))) {
	    case HTTP:
	        server = httpServer;
	        break;
	    case SOCKET:
    	    server = socketServer;
    	    break;
    	default:
    	    break;
	    }
		return server;
	}
	
	public void initializeServer(final String type, final int port) throws SystemException {
	    final ModletServer modletServer = getServerInstance(type);
	    modletServer.initializeServer(port);
	}
	
	public void destroyServer(final String type) throws SystemException {
	    final ModletServer modletServer = getServerInstance(type);
	    modletServer.destroyServer();
	}

}
