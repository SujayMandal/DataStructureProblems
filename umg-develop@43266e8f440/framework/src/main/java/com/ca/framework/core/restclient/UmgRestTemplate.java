/**
 * 
 */

package com.ca.framework.core.restclient;

import javax.inject.Named;

import org.springframework.web.client.RestTemplate;

/**
 * @author kamathan
 *
 */
@Named("umgRestTemplate")
public class UmgRestTemplate extends RestTemplate {

    /**
     * added to avoid pmd check
     */
    // TODO discuss
    private long timeout;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
