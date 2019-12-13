/**
 * 
 */
package com.ca.pool.manager;

import com.ca.modelet.ModeletClientInfo;
import com.ca.modelet.client.HttpModeletClient;
import com.ca.modelet.client.ModeletClient;
import com.ca.modelet.client.SocketModeletClient;
import com.ca.modelet.common.ServerType;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

/**
 * @author kamathan
 *
 */
@Named
public class ModeletHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModeletHelper.class);

    public ModeletClient buildModeletClient(final ModeletClientInfo modeletClientInfo) {
        ModeletClient modeletClient = null;
        if(StringUtils.trimToNull(modeletClientInfo.getServerType()) == null){
        	modeletClientInfo.setServerType(ServerType.SOCKET.getServerType());
        }
        LOGGER.error("Creating modelet client for modelel {}.", modeletClientInfo);
        switch (ServerType.valueOf(modeletClientInfo.getServerType())) {
        case HTTP:
            modeletClient = new HttpModeletClient(modeletClientInfo.getHost(), modeletClientInfo.getPort(),
                    modeletClientInfo.getContextPath());
            break;
        case SOCKET:
            modeletClient = new SocketModeletClient(modeletClientInfo.getHost(), modeletClientInfo.getPort());
            break;
        default:
            break;
        }
        LOGGER.info("Modelet client for modelet {} created successfully.", modeletClient, modeletClientInfo);
        return modeletClient;
    }
}
