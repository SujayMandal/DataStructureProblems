/**
 * 
 */
package com.fa.dp.core.systemparam.provider;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.systemparam.delegate.SystemParameterDelegate;
import com.fa.dp.core.systemparam.info.SystemParameterInfo;
import com.hazelcast.core.HazelcastInstance;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
@Named
public class SystemParameterProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemParameterProvider.class);

    public static final String SYSTEM_PARAM = "SYSTEM_PARAM";
    @Inject
    private SystemParameterDelegate systemParameterDelegate;

    @Inject
    private HazelcastInstance hazelcastInstance;

    @PostConstruct
    public void init() {
        List<SystemParameterInfo> systemParameterInfos;
        try {
            systemParameterInfos = systemParameterDelegate.getAllSystemParamters();
            if (CollectionUtils.isNotEmpty(systemParameterInfos)) {
                for (SystemParameterInfo systemParameterInfo : systemParameterInfos) {
                    hazelcastInstance.getMap(SYSTEM_PARAM).put(StringUtils.lowerCase(systemParameterInfo.getKey()),
                            systemParameterInfo.getValue());
                }
            }

        } catch (SystemException e) {
            LOGGER.error("An error occurred while building system parameter cache, aborting startup.", e);
            System.exit(1);
        }
    }

    public String getSystemParamValue(String key) {
        if (!hazelcastInstance.getMap(SYSTEM_PARAM).containsKey(StringUtils.lowerCase(key))) {
            SystemParameterInfo systemParameterInfo = null;
            try {
                systemParameterInfo = systemParameterDelegate.getSystemParameterByKey(key);
            } catch (SystemException e) {
                LOGGER.error("An error occurred while fetching system parameter {}", key);
            }
            if (systemParameterInfo != null) {
                hazelcastInstance.getMap(SYSTEM_PARAM).put(StringUtils.lowerCase(systemParameterInfo.getKey()),
                        systemParameterInfo.getValue());
            }
        }
        return (String) hazelcastInstance.getMap(SYSTEM_PARAM).get(StringUtils.lowerCase(key));
    }

    public boolean updateSystemParameter(SystemParameterInfo systemParameterInfo) {
    	try {
    		systemParameterInfo = systemParameterDelegate.save(systemParameterInfo);
    		hazelcastInstance.getMap(SYSTEM_PARAM).put(StringUtils.lowerCase(systemParameterInfo.getKey()),
                    systemParameterInfo.getValue());
    		return true;
		} catch (SystemException e) {
			LOGGER.error("An error occurred while updating system parameter {}", systemParameterInfo.getKey());
			return false;
		}
    }

}
