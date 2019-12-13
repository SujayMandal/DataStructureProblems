/**
 * 
 */

package com.ca.framework.core.restclient;

import javax.inject.Named;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * @author kamathan
 * 
 */
@Named
public class UmgHttpConnectionManager extends PoolingHttpClientConnectionManager {

    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;

    private static final int DFLT_MAX_CON_PER_ROUTE = 25;

    public UmgHttpConnectionManager() {
        super();
        setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
        setDefaultMaxPerRoute(DFLT_MAX_CON_PER_ROUTE);
    }

}