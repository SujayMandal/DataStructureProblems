/*
 * SyndicateDataBO.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.syndicatedata.bo;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.syndicatedata.entity.SyndicateData;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataKeyInfo;
import com.ca.umg.business.tenant.entity.SystemKey;

/**
 * 
 * Business Object to fetch Syndicate Data.
 * 
 * @author mandavak
 *
 */
public interface SyndicateDataBO {

    /**
     * Fetches Syndicate Data of all versions for a given container name.
     * 
     * @param containerName
     *            List<{@code SyndicateData}>
     * @return
     */
    List<SyndicateData> findSyndicateContainerVersions(String containerName) throws BusinessException, SystemException;

    /**
     * Fetches Syndicate Data for the given containerName.
     * 
     * @param containerName
     * @return
     */
    SyndicateData findSyndicateDataContainer(String containerName) throws BusinessException, SystemException;

    /**
     * 
     * fetches Syndicate Data for the given VersionId and Container Name.
     * 
     * @param versionId
     * @param containerName
     * @return
     */
    SyndicateData findSyndicateDataByVersionId(Long versionId, String containerName) throws BusinessException, SystemException;

    /**
     * fetches Syndicate Data which is either maximum version or Minimum version of all containers
     * 
     * @return List<{@code SyndicateData}>
     * @throws SystemException
     */
    List<SyndicateData> getContainers() throws BusinessException, SystemException;
    
    /**
     * fetches Syndicate Data which is either maximum version or Minimum version of filtered containers
     * 
     * @return Page<{@code SyndicateData}>
     * @throws SystemException
     */
    Page<SyndicateData> getContainers(SearchOptions pageInfo) throws BusinessException, SystemException;

    /**
     * Retrieves indexes information for a given database table.
     */
    Map<String, List<String>> getTableKeys(String tablename) throws BusinessException, SystemException;

    /**
     * Retrieves column information for a given database table.
     * 
     * @param tableName
     * @return List<{@code SyndicateDataColumnInfo}>
     * @throws SystemException
     */
    List<SyndicateDataColumnInfo> getTableColumnInfo(String tableName) throws BusinessException, SystemException;

    /**
     * Create dynamic table for Syndicated data container
     * 
     * @param containerName
     * @param dataColumnInfos
     */
    void createSyndicateDataTable(String containerName, List<SyndicateDataColumnInfo> dataColumnInfos);

    /**
     * Creates new version of the Syndicated Data.
     * 
     * @param syndicateData
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    SyndicateData createSyndicateDataVersion(SyndicateData syndicateData) throws BusinessException, SystemException;

    /**
     * Updated the valid from/valid until values for the given version.
     * 
     * @param syndicateData
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    SyndicateData updateSyndicateDataVersion(SyndicateData syndicateData) throws BusinessException, SystemException;

    /**
     * Delete a version of a syndicate data container.
     * 
     * @param versionId
     * @param containerName
     * @throws BusinessException
     * @throws SystemException
     */
    void delete(Long versionId, String containerName) throws BusinessException, SystemException;

    void createSyndicateDataKeyDefs(String containerName, List<SyndicateDataKeyInfo> dataKeyInfos) throws BusinessException,
            SystemException;

    void deleteSyndicateDataKeyDefs(String containerName) throws BusinessException, SystemException;

    void dropSyndicateDataContainer(String containerName) throws BusinessException, SystemException;

    void insertSyndicateData(SyndicateDataContainerInfo containerInfo, SyndicateData syndicateData) throws BusinessException,
            SystemException;

    /**
     * 
     * update container information.
     * 
     * @param sContainerInfo
     * @throws BusinessException
     * @throws SystemException
     */
    void updateContainerInfor(SyndicateDataContainerInfo sContainerInfo) throws BusinessException, SystemException;

    void updateMetadataAndKeyInfo(SyndicateDataContainerInfo syndicateDataInfo, SyndicateData syndicateData)
            throws BusinessException, SystemException;

    SyndicateDataContainerInfo getSyndicateDataKeysAndColumnInfo(SyndicateDataContainerInfo syndicateDataContainerInfo,
            SyndicateData syndicateData) throws BusinessException, SystemException;

    /**
     * API for fetching the container definition containing the table definition along with keys.
     * @param providerName
     * @return JSON representation of the container definition.
     * @throws BusinessException
     * @throws SystemException
     */
    String getSyndicateContainerDefinition(String providerName) throws BusinessException, SystemException;

    List<SyndicateData> findPreviousVersion(String providerName, Long versionId);

    SyndicateData findProviderMaxVersion(String containerName) throws SystemException, BusinessException;

    SystemKey findByKey(String key);
    
    /**
     * This method returns the Syndicate Table columns and data based on container and version
     * 
     * @param providerName
     * @param versionName
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    List<String> getSyndicateTableData(String providerName, Long versionId) throws SystemException, BusinessException;
}