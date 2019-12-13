package com.ca.pool.util;

import java.util.HashMap;
import java.util.Map;

import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.ModeletStatus;

/**
 * Created by repvenk on 6/10/2016.
 */
public class BeanToMapUtil {

    public static Map<String, String> getModeletClientInfoMap(ModeletClientInfo clientInfo) {
        Map<String, String> modeletClientInfoMap = new HashMap<>();
        modeletClientInfoMap.put("executionLanguage", clientInfo.getExecutionLanguage());
        modeletClientInfoMap.put("port", String.valueOf(clientInfo.getPort()));
        modeletClientInfoMap.put("memberHost", clientInfo.getMemberHost());
        modeletClientInfoMap.put("newPoolName", clientInfo.getNewPoolName());
        modeletClientInfoMap.put("poolName", clientInfo.getPoolName());
        modeletClientInfoMap.put("loadedModel", clientInfo.getLoadedModel());
        modeletClientInfoMap.put("loadedModelVersion", clientInfo.getLoadedModelVersion());
        modeletClientInfoMap.put("modelLibraryVersionName", clientInfo.getModelLibraryVersionName());
        modeletClientInfoMap.put("serverType", clientInfo.getServerType());
        modeletClientInfoMap.put("host", clientInfo.getHost());
        modeletClientInfoMap.put("contextPath", clientInfo.getContextPath());
        modeletClientInfoMap.put("memberPort", String.valueOf(clientInfo.getMemberPort()));
        modeletClientInfoMap.put("modeletStatus", clientInfo.getModeletStatus() != null ? clientInfo.getModeletStatus() : ModeletStatus.UNREGISTERED.getStatus());
        modeletClientInfoMap.put("tenantCode", clientInfo.getTenantCode());
        modeletClientInfoMap.put("requestMode", clientInfo.getRequestMode());
        modeletClientInfoMap.put("poolWaitTimeOut", String.valueOf(clientInfo.getPoolWaitTimeOut()));
        modeletClientInfoMap.put("rServePort", String.valueOf(clientInfo.getrServePort()));
        return modeletClientInfoMap;
    }

}
