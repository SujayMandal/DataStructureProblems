/*
 * VersionMap.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.flows.version;

import static java.util.Locale.getDefault;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.ca.framework.core.exception.BusinessException;
import com.ca.pool.TransactionMode;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;

/**
 * Data structure to store version details. It stores version data in a tree structur. model-1 | - 1 | - 1 - 2 - 2 | - 1 - 2
 **/
public class VersionMap {
    private final Map<String, Map<Integer, Map<Integer, VersionInfo>>> versions = new ConcurrentHashMap<String, Map<Integer, Map<Integer, VersionInfo>>>();

    /**
     * Add new {@link VersionInfo} to {@link VersionMap}
     *
     * @param version
     *            DOCUMENT ME!
     *
     * @throws BusinessException
     *             DOCUMENT ME!
     **/
    public void add(VersionInfo version) throws BusinessException {
        String modelName = version.getModelName();
        if (modelName == null) {
            throw new BusinessException(RuntimeExceptionCode.RVE000200, new Object[] {});
        }

        Map<Integer, Map<Integer, VersionInfo>> majorVersionMap = versions.get(modelName.toLowerCase(getDefault()));

        if (majorVersionMap == null) {
            majorVersionMap = new ConcurrentHashMap<Integer, Map<Integer, VersionInfo>>();
            versions.put(modelName.toLowerCase(getDefault()), majorVersionMap);
        }
        int majorVersion = version.getMajorVersion();

        Map<Integer, VersionInfo> minorVersionMap = majorVersionMap.get(majorVersion);

        if (minorVersionMap == null) {
            minorVersionMap = new ConcurrentHashMap<Integer, VersionInfo>();
            majorVersionMap.put(majorVersion, minorVersionMap);
        }
        int minorVersion = version.getMinorVersion();
        VersionInfo versionInfo = minorVersionMap.get(minorVersion);

        if ((versionInfo == null) || !versionInfo.equals(version)) {
            minorVersionMap.put(minorVersion, version);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param version
     *            DOCUMENT ME!
     *
     * @throws BusinessException
     *             DOCUMENT ME!
     **/
    public void remove(VersionInfo version) throws BusinessException {
        if(version == null){
            throw new BusinessException(RuntimeExceptionCode.RVE000200,new Object[]{});
        }
        String modelName = version.getModelName();

        if (modelName == null) {
            throw new BusinessException(RuntimeExceptionCode.RVE000200, new Object[] {});
        }

        Map<Integer, Map<Integer, VersionInfo>> majorVersionMap = versions.get(modelName.toLowerCase(getDefault()));

        if (majorVersionMap == null) {
            return;
        }
        int majorVersion = version.getMajorVersion();
        Map<Integer, VersionInfo> minorVersionMap = majorVersionMap.get(majorVersion);

        if (minorVersionMap == null) {
            return;
        }
        int minorVersion = version.getMinorVersion();
        VersionInfo versionInfo = minorVersionMap.get(minorVersion);

        if ((versionInfo != null) && versionInfo.equals(version)) {
            minorVersionMap.remove(minorVersion);
        }

    }

    /**
     * DOCUMENT ME!
     *
     * @param modelName
     *            DOCUMENT ME!
     * @param majorVersion
     *            DOCUMENT ME!
     * @param minorVersion
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    public VersionInfo get(String modelName, Integer majorVersion, Integer minorVersion, boolean test,String transactionMode) {
        Map<Integer, Map<Integer, VersionInfo>> majorVersionMap = versions.get(modelName.toLowerCase(getDefault()));

        if (majorVersionMap == null || majorVersionMap.isEmpty()) {
            return null;
        }
        Integer majorVer = majorVersion;
        if (majorVer == null) {
            majorVer = Collections.max(majorVersionMap.keySet());
        }

        Map<Integer, VersionInfo> minorVersionMap = majorVersionMap.get(majorVer);

        if (minorVersionMap == null || minorVersionMap.isEmpty()) {
            return null;
        }
        Integer minorVer = minorVersion;
        if (minorVer == null) {
        	for (Map.Entry<Integer, VersionInfo> entry : minorVersionMap.entrySet())
        	{
                 if (entry.getValue().getModelType().equalsIgnoreCase(transactionMode) && (minorVer == null || entry.getValue().getMinorVersion() > minorVer )){
                		 minorVer = entry.getValue().getMinorVersion();
                 }
                 
                 if(StringUtils.equals(transactionMode, TransactionMode.ONLINE.getMode()) && (StringUtils.equals(entry.getValue().getModelType(), TransactionMode.BULK.getMode())) && (minorVer == null || entry.getValue().getMinorVersion() > minorVer )){
                    		 minorVer = entry.getValue().getMinorVersion();
                    	
                 }
        	}
           // minorVer = Collections.max(minorVersionMap.keySet());
        }

        VersionInfo versionInfo = minorVersionMap.get(minorVer);

        return versionInfo;
    }
}
