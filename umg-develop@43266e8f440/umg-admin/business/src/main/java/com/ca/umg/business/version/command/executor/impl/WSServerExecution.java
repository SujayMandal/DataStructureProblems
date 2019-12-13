package com.ca.umg.business.version.command.executor.impl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/counter/{clientId}")
public class WSServerExecution { // NOPMD
	private static final Logger LOGGER = LoggerFactory.getLogger(WSServerExecution.class);
	
	 private static Map< String , Session> webSocketSessions = new ConcurrentHashMap<String, Session>();

	 @OnOpen
	 public void init(@PathParam("clientId") String clientId, Session session) throws IOException {
		 webSocketSessions.put(clientId , session);
	 }

    @OnClose
    public void onClose(Session session) {
    	webSocketSessions.values().remove(session);
    	for (Map.Entry<String, Session> entry : webSocketSessions.entrySet())
    	{
    	   if(!entry.getValue().isOpen()){
    		   webSocketSessions.values().remove(entry.getValue()); 
    	   }
    	}
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
        	LOGGER.error("Error with receving message from client side" + e.getMessage());
        }
    }

    @OnError
    public void onError(Throwable e) {
    	LOGGER.error("Error with Session " + e.getMessage());
    }

    public void sendStatusMessage(String message, String clientId) {
    	if(webSocketSessions.containsKey(clientId) && webSocketSessions.get(clientId).isOpen()){
    		try {
				webSocketSessions.get(clientId).getBasicRemote().sendText(message);
			} catch (IOException e) {
				LOGGER.error("Error occured while sending status message " + e.getMessage() );
			}
    	}
    }
}
