/****
Product Name : Universal Model Gateway
Version      : 0.1 alpha
Author       :
Date Created :




 *****/
package com.ca.umg.business.syndicatedata.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.ca.umg.business.syndicatedata.entity.SyndicateDataQuery;

/**
 * DAO for syndicate data extraction
 */
public interface SyndicateDataQueryDAO extends JpaRepository<SyndicateDataQuery, String>,
        JpaSpecificationExecutor<SyndicateDataQuery> {

    /**
     * Finds the query by name.
     * 
     * @param name
     * @return SyndicateDataQuery object with specified name.
     */
    SyndicateDataQuery findByName(String name);

    /**
     * Find the query by name and mapping id.
     * 
     * @param name
     * @param mappingId
     * @return SyndicateDataQuery object with specified name and object.
     */
    SyndicateDataQuery findByNameAndMappingId(String name, String mappingId);

    @Query(value = "SELECT MAX(execSequence) FROM SyndicateDataQuery QRY WHERE QRY.mapping.id = ?1")
    Integer getMaxSequenceQueryMappingID(String mappingId);

    /*
     * Find queries by mapping id.
     * 
     * @param mappingId
     * 
     * @return SyndicateDataQuery objects
     */
    List<SyndicateDataQuery> findByMappingNameOrderByExecSequenceAsc(String mappingName);

    /**
     * Find queries by mapping id and mapping type.
     * 
     * @param mappingId
     *            and mappingType
     * @return SyndicateDataQuery objects
     */
    List<SyndicateDataQuery> findByMappingNameAndMappingTypeOrderByExecSequenceAsc(String mappingName, String mappingType);
    
    List<SyndicateDataQuery> findByMappingIdAndNameIn(String mappingId, List<String> queryNames);

    List<SyndicateDataQuery> findByMappingId(String mappingId);

}
