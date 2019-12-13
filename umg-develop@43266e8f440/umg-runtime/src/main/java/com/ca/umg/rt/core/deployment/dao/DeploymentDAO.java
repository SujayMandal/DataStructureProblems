/*
 * DeploymentDAO.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.core.deployment.dao;

import java.util.List;
import java.util.Map;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.umg.rt.core.flow.entity.Version;
import com.ca.umg.rt.core.flow.entity.VersionMapping;
import com.ca.umg.rt.core.flow.entity.VersionModelLibrary;
import com.ca.umg.rt.core.flow.entity.VersionQuery;

/**
 * DOCUMENT ME!
 **/
public interface DeploymentDAO {
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
    public List<Version> getVersion(String modelName, Integer majorVersion, Integer minorVersion);
    
    /**
     * DOCUMENT ME!
     *
     * @param modelName
     *            DOCUMENT ME!
     * @param majorVersion
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     **/
    public List<Integer> getMaxMinorVersion(String modelName, Integer majorVersion);

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
    public Map<Version, VersionMapping> getVersionMapping(String modelName, Integer majorVersion, Integer minorVersion);

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
    public Map<Version, List<VersionQuery>> getVersionQuery(String modelName, Integer majorVersion, Integer minorVersion);

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
    public Map<Version, VersionModelLibrary> getVersionModelLibrary(String modelName, Integer majorVersion, Integer minorVersion);

    /**
     * Returns execution langauage and version for the given version details.
     * 
     * @param modelName
     * @param majorVersion
     * @param minorVersion
     * @return
     */
    public VersionExecInfo getExecutionLanguage(final String modelName, final Integer majorVersion,
            final Integer minorVersion);

    /**
     * Returns execution environment and model checksum for the given version details.
     * 
     * @return
     * @throws SystemException
     */
    public Map<String,String> getExecutionEnvironment(final String modelName, final Integer majorVersion, final Integer minorVersion)
            throws SystemException;
}
