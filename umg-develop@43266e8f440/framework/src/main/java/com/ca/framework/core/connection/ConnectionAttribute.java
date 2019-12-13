/**
 * 
 */
package com.ca.framework.core.connection;

import java.io.Serializable;

/**
 * @author kamathan
 *
 */
public class ConnectionAttribute implements Serializable {

    private static final long serialVersionUID = -4570374846047088323L;

    private String host;

    private String username;

    private String password;

    private String identityKey;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdentityKey() {
        return identityKey;
    }

    public void setIdentityKey(String identityKey) {
        this.identityKey = identityKey;
    }
    
    @Override
    public String toString() {
    	final StringBuilder sb = new StringBuilder();
    	sb.append("Host : " + getHost());
    	sb.append(", Username : " + getUsername());
    	sb.append(", Identity Key : " + getIdentityKey());
    	
    	return sb.toString();
    }
}
