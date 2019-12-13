/****
Product Name : Universal Model Gateway
Version      : 0.1 alpha
Author       :
Date Created :




 *****/
package com.ca.umg.business.syndicatedata.delegate;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mid.extraction.info.TidSqlInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryResponseInfo;

/**
 * Delegate that provides access to BO.
 */
public interface SyndicateDataQueryDelegate {

    /**
     * List all syndicate data queries
     * 
     * @return list of syndicate data query objects
     * @throws BusinessException
     * @throws SystemException
     */
    List<SyndicateDataQueryInfo> listAll() throws BusinessException, SystemException;

    /**
     * This method creates the syndicate data query item in the persistence.
     * 
     * @param synDataQryInfo
     * @throws BusinessException
     * @throws SystemException
     */
    SyndicateDataQueryInfo createSyndicateDataQuery(SyndicateDataQueryInfo synDataQryInfo) throws BusinessException,
            SystemException;

    /**
     * Update execution sequence of queries
     * 
     * @param queries
     *            whose sequence needs to be updated
     * @throws BusinessException
     * @throws SystemException
     */
    void updateExecutionSequence(List<SyndicateDataQueryInfo> queries) throws BusinessException, SystemException;

    /**
     * This method updates the syndicate data query item.
     * 
     * @param synDataQryInfo
     * @throws BusinessException
     * @throws SystemException
     */
    void updateSyndicateDataQuery(SyndicateDataQueryInfo synDataQryInfo) throws BusinessException, SystemException;

    /**
     * Test the query object created.
     * 
     * @param queryObject
     * @return query results.
     * @throws BusinessException
     * @throws SystemException
     */
    SyndicateDataQueryResponseInfo fetchQueryTestData(SyndicateDataQueryInfo synDataQryInfo) throws BusinessException,
            SystemException;

    /**
     * List all syndicate data queries by mapping id
     * 
     * @return list of syndicate data query objects
     * @param mapping
     *            name
     * @throws BusinessException
     * @throws SystemException
     */
    List<SyndicateDataQueryInfo> listByMappingName(String mappingName) throws BusinessException, SystemException;

    /**
     * List all syndicate data queries by mapping id and mapping type
     * 
     * @return list of syndicate data query objects
     * @param mapping
     *            name and mapping type
     * @throws BusinessException
     * @throws SystemException
     */
    List<SyndicateDataQueryInfo> listByMappingNameAndType(String mappingName, String mappingType) throws BusinessException,
            SystemException;

    /**
     * Get interface definition SQL info based on mapping id and mapping type.
     * 
     * @return list of TidSqlInfo objects
     * @param mapping
     *            name and mapping type
     * @throws BusinessException
     * @throws SystemException
     */
    List<TidSqlInfo> getInterfaceDefinitionSqlInfos(String mappingName, String mappingType) throws BusinessException,
            SystemException;
}
