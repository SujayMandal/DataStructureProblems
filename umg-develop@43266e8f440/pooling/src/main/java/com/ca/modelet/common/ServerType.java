/**
 * 
 */
package com.ca.modelet.common;

/**
 * @author kamathan
 *
 */
public enum ServerType {

    HTTP("HTTP"), SOCKET("SOCKET");

    private ServerType(final String serverType) {
        this.serverType = serverType;
    }

    private String serverType;

    public String getServerType() {
    	if(serverType==null){
    		serverType = ServerType.SOCKET.getServerType();    		
    	}
        return serverType;
    }

    public void setServerType(final String serverType) {
        this.serverType = serverType;
    }
}
