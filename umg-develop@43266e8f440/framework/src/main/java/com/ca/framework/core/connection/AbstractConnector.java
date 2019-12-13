/**
 * 
 */
package com.ca.framework.core.connection;

/**
 * @author kamathan
 *
 */
public abstract class AbstractConnector implements Connector {

    private String host;
    private String username;
    private String password;

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
