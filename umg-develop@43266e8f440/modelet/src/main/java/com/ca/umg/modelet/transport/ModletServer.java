package com.ca.umg.modelet.transport;

import com.ca.framework.core.exception.SystemException;

public interface ModletServer {
	
	void initializeServer(int port) throws SystemException;
	
	void destroyServer() throws SystemException;
	
}
