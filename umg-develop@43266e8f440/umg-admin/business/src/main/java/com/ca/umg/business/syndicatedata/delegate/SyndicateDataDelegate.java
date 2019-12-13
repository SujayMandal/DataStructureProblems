/*
 * SyndicateDataDelegate.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.syndicatedata.delegate;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataVersionInfo;

/**
 * Syndicate Data delegate that provides access to DAO from controller.
 * 
 * @author mandavak
 *
 */
public interface SyndicateDataDelegate {
    /**
     * It fetches all Versions of a given Container details from SYNDICATE_DATA table.
     *
     * @param containerName
     *
     * @return
     *
     * @throws BusinessException
     * @throws SystemException
     **/
    SyndicateDataVersionInfo listVersions(String containerName) throws BusinessException, SystemException;

    /**
     * Fetches Syndicate Data from SYNDICATE_DATA table, for a given VersionId and ContainerName.
     *
     * @param versionId
     * @param containerName
     *
     * @return
     *
     * @throws BusinessException
     * @throws SystemException
     **/
    SyndicateDataContainerInfo getContainerVersionInformation(Long versionId, String containerName) throws BusinessException,
            SystemException;

    /**
     * Fetches Syndicate Data from SYNDICATE_DATA table, for a given container name;
     *
     * @param containerName
     *
     * @return
     *
     * @throws BusinessException
     * @throws SystemException
     **/
    SyndicateDataContainerInfo getContainerInformation(String containerName) throws BusinessException, SystemException;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws BusinessException
     *             DOCUMENT ME!
     * @throws SystemException
     *             DOCUMENT ME!
     **/
    /**
     * Fetches Syndicate Data from SYNDICATE_DATA table for all containers.
     * 
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    List<SyndicateDataContainerInfo> getContainerInformation() throws BusinessException, SystemException;
    
    /**
     * Fetches Syndicate Data from SYNDICATE_DATA table based on Pagination and Search Request.
     * 
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    PageRecord<SyndicateDataContainerInfo> getContainerInformation(SearchOptions pageInfo) throws BusinessException, SystemException;

    /**
     * Delete particular version data for the syndicate data container.
     * 
     * @param versionId
     * @param containerName
     * @throws BusinessException
     */
    void deleteContainerVersion(Long versionId, String containerName) throws BusinessException, SystemException;

    void createProvider(SyndicateDataContainerInfo sContainerInfo) throws BusinessException, SystemException;

    /**
     * 
     * Update container information.
     * 
     * @author mandavak
     * 
     * @param sContainerInfo
     * @throws BusinessException
     * @throws SystemException
     */
    void updateProvider(SyndicateDataContainerInfo sContainerInfo) throws BusinessException, SystemException;

    void createProviderVersion(SyndicateDataContainerInfo sContainerInfo) throws BusinessException, SystemException;

    void updateProviderVersion(SyndicateDataContainerInfo sContainerInfo) throws BusinessException, SystemException;

    /**
     * This method downloads syndicate Versiondata with the columns and data
     * 
     * @param outputStream
     * @param containerName
     * @param versionName
     * @throws BusinessException
     * @throws SystemException
     */
    List<String> downloadSyndTableData(String containerName, Long versionId) throws BusinessException,
            SystemException;

    
    String downloadSyndContainerDefinition(String providerName) throws BusinessException, SystemException;
}
