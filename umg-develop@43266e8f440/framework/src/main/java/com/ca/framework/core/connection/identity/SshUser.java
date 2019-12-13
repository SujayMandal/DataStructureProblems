/**
 * 
 */
package com.ca.framework.core.connection.identity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.UserInfo;

/**
 * @author kamathan
 *
 */
public class SshUser implements UserInfo {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshUser.class);

    private String password;

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean promptPassphrase(String arg0) {
        return false;
    }

    @Override
    public boolean promptPassword(String arg0) {
        return false;
    }

    @Override
    public boolean promptYesNo(String arg0) {
        return false;
    }

    @Override
    public void showMessage(String message) {
        LOGGER.info("SSH USer messaage : {}.", message);
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
