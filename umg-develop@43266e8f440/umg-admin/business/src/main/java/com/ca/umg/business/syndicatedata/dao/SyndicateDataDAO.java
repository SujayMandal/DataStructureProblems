/*
 * SyndicateDataDAO.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.syndicatedata.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.ca.umg.business.syndicatedata.entity.SyndicateData;

/**
 * 
 * Syndicate Data DAO class that is repository class for SyndicateData bean.
 * 
 * @author mandavak
 *
 */
public interface SyndicateDataDAO extends JpaRepository<SyndicateData, String>, JpaSpecificationExecutor<SyndicateData> {

    /**
     * List all versions of a given container
     * 
     * @param containerName
     * @return List
     */
    List<SyndicateData> findByContainerNameOrderByVersionIdDesc(String containerName);

    /**
     * Fetches smallest version for the given container name
     * 
     * @param containerName
     * @return
     */
    @Query("SELECT SD FROM SyndicateData SD WHERE SD.versionId = (SELECT MIN(SD.versionId) FROM SyndicateData SD WHERE SD.containerName = ?1) AND SD.containerName = ?1")
    SyndicateData findFirstProviderVersion(String containerName);

    /**
     * Fetches maximum version present for Syndicate Provider for a given name.
     * 
     * @param containerName
     * @return
     */
    @Query("SELECT SD FROM SyndicateData SD WHERE SD.versionId = (SELECT MAX(SD.versionId) FROM SyndicateData SD WHERE SD.containerName = ?1) AND SD.containerName = ?1")
    SyndicateData findProviderMaxVersion(String containerName);

    /**
     * 
     * Fetches Syndicate Data by given ContainerId (This containerId is a unique Id in the table that is ID column).
     * 
     * @param containerId
     * @return
     */
    SyndicateData findById(String containerId);

    /**
     * 
     * Fetche's Syndicate Data for a given versionId and containerName.
     * 
     * @param versionId
     * @param containerName
     * @return
     */
    SyndicateData findByVersionIdAndContainerName(Long versionId, String containerName);

    /**
     * List only records with maximum or minimum version of all containers
     * 
     * @return List
     */
    @Query(nativeQuery = true, value = "select * from SYNDICATED_DATA D,"
            + "(select CONTAINER_NAME, min(VERSION_ID) AS MINVERSION from SYNDICATED_DATA"
            + " group by CONTAINER_NAME) T where D.CONTAINER_NAME = T.CONTAINER_NAME"
            + " AND D.VERSION_ID = T.MINVERSION ORDER BY D.CONTAINER_NAME, D.VERSION_ID")
    List<SyndicateData> getMinVersionOfEachContainer();

    /**
     * List only records with maximum or minimum version of all containers
     * 
     * @return List
     */
    @Query(nativeQuery = true, value = "select * from SYNDICATED_DATA D,"
            + "(select CONTAINER_NAME, max(VERSION_ID) AS MAXVERSION, min(VERSION_ID) AS MINVERSION from SYNDICATED_DATA"
            + " group by CONTAINER_NAME) T where D.CONTAINER_NAME = T.CONTAINER_NAME AND D.CONTAINER_NAME = ?1"
            + " AND (D.VERSION_ID = T.MAXVERSION OR D.VERSION_ID = T.MINVERSION) ORDER BY D.CONTAINER_NAME, D.VERSION_ID")
    List<SyndicateData> getMinMaxVerContainer(String containerName);

    List<SyndicateData> findByContainerNameAndVersionIdGreaterThanOrderByVersionIdAsc(String containerName, Long versionId,
            Pageable pageable);

    List<SyndicateData> findByContainerNameAndVersionIdLessThanOrderByVersionIdDesc(String containerName, Long versionId,
            Pageable pageable);

    List<SyndicateData> findByContainerNameAndVersionIdLessThanOrderByVersionIdDesc(String containerName, Long versionId);

    @Query("from SyndicateData S where S.containerName = ?1 and S.versionId <= ?2 order by S.versionId desc")
    List<SyndicateData> findByContainerNameAndVersionIdLessThanOrEqualOrderByVersionIdDesc(String containerName, Long versionId);

}
