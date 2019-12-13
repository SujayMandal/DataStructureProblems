/*
 * SyndicateDataQueryBO.java
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

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQuery;

/**
 * Business Object to fetch Syndicate data Query
 */
public interface SyndicateDataQueryBO {

    /**
     * List all Queries
     * 
     * @return List of SyndicateDataQuery
     * 
     * @throws BusinessException
     * @throws SystemException
     **/
    List<SyndicateDataQuery> listAll() throws BusinessException, SystemException;

    /**
     * Creates a syndicate data query item.
     * 
     * @param synDataQuery
     * @return Created Query Item
     * @throws BusinessException
     * @throws SystemException
     */
    SyndicateDataQuery create(SyndicateDataQuery synDataQuery) throws BusinessException, SystemException;

    /**
     * Update syndicate data query execution sequence.
     * 
     * @param queries
     * @throws BusinessException
     * @throws SystemException
     */
    void updateQueryExecutionSequence(List<SyndicateDataQuery> queries) throws BusinessException, SystemException;

    /**
     * Updates the syndicate data query item.
     * 
     * @param synDataQuery
     * @return updated syndicate data query item.
     * @throws BusinessException
     * @throws SystemException
     */
    SyndicateDataQuery update(SyndicateDataQuery synDataQuery) throws BusinessException, SystemException;

    /**
     * Fetch test data from executable query
     * 
     * @throws BusinessException
     * @throws SystemException
     */
    List<Map<String, Object>> fetchTestData(SyndicateDataQuery synDataQuery) throws BusinessException, SystemException;

    /**
     * Get the constructed query out of query object which is used for execution.
     * 
     * @param synDataQuery
     * @return Executable Query
     */
    String getExecutableQuery(SyndicateDataQuery synDataQuery);

    /**
     * List queries by mapping name.
     * 
     * @return List of SyndicateDataQuery
     * @param mappingName
     * @throws BusinessException
     * @throws SystemException
     **/
    List<SyndicateDataQuery> listByMappingName(String mappingName) throws BusinessException, SystemException;

    /**
     * List queries by mapping name and mapping type.
     * 
     * @return List of SyndicateDataQuery
     * @param mappingName
     *            and mappingType
     * @throws BusinessException
     * @throws SystemException
     **/
    List<SyndicateDataQuery> listByMappingNameAndType(String mappingName, String mappingType) throws BusinessException,
            SystemException;

    /**
     * List all deleted output parameters of a query.
     * 
     * @return list of parameter names
     * @param SyndicateDataQuery
     */
    Map<String, Object> systemParameters(SyndicateDataQuery dataQueryTested) throws BusinessException, SystemException;

    /**
     * This method is added for running JUnit Test cases.
     * 
     * @param isTestMode
     */
    void runInTestMode(boolean isTestMode);
}
