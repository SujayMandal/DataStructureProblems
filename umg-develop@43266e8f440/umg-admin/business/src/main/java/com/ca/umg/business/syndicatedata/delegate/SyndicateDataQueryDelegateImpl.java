/*
 * SyndicateDataQueryDelegateImpl.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.syndicatedata.delegate;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.mapping.bo.MappingBO;
import com.ca.umg.business.mapping.delegate.MappingDelegate;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mid.extraction.info.TidSqlInfo;
import com.ca.umg.business.syndicatedata.bo.SyndicateDataQueryBO;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQuery;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryOutput;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryParameterInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryResponseInfo;
import com.ca.umg.business.syndicatedata.query.validator.SyndicateDataQueryValidator;
import com.ca.umg.business.util.AdminUtil;

/**
 * Delegate that provides access to BO.
 * 
 **/
@Component
public class SyndicateDataQueryDelegateImpl extends AbstractDelegate implements SyndicateDataQueryDelegate {

    private static final int ONE = 1;

    @Inject
    private SyndicateDataQueryBO syndicateDataQueryBO;

    @Inject
    private MappingBO mappingBO;

    @Inject
    private InterfaceDefinitionConversionHelper conversionHelper;

    @Inject
    private MappingDelegate mappingDelegate;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyndicateDataQueryDelegateImpl.class);

    /**
     * List all syndicate data queries
     * 
     * @return list of SyndicateDataQueryInfo
     * 
     * @throws BusinessException
     * @throws SystemException
     **/
    @Override
    public List<SyndicateDataQueryInfo> listAll() throws BusinessException, SystemException {
        List<SyndicateDataQueryInfo> infoObjects = new ArrayList<>();
        List<SyndicateDataQuery> syndicateDataQueryList = syndicateDataQueryBO.listAll();
        if (isNotEmpty(syndicateDataQueryList)) {
            infoObjects = convertToList(syndicateDataQueryList, SyndicateDataQueryInfo.class);
        }
        return infoObjects;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public SyndicateDataQueryInfo createSyndicateDataQuery(SyndicateDataQueryInfo synDataQryInfo) throws BusinessException,
            SystemException {
        SyndicateDataQuery synDataQuery = convert(synDataQryInfo, SyndicateDataQuery.class);
        Mapping mapping = mappingBO.findByName(synDataQryInfo.getMapping().getName());
        synDataQuery.setMapping(mapping);
        synDataQuery = syndicateDataQueryBO.create(synDataQuery);
        return convert(synDataQuery, SyndicateDataQueryInfo.class);
    }

    @Override
    public void updateExecutionSequence(List<SyndicateDataQueryInfo> queryInfos) throws BusinessException, SystemException {
        List<SyndicateDataQuery> queries = convertToList(queryInfos, SyndicateDataQuery.class);
        syndicateDataQueryBO.updateQueryExecutionSequence(queries);
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public void updateSyndicateDataQuery(SyndicateDataQueryInfo synDataQryInfo) throws BusinessException, SystemException {
        SyndicateDataQuery synDataQuery = convert(synDataQryInfo, SyndicateDataQuery.class);
        Mapping mapping = mappingBO.findByName(synDataQryInfo.getMapping().getName());
        synDataQuery.setMapping(mapping);
        synDataQuery = syndicateDataQueryBO.update(synDataQuery);
    }

    @Override
    public List<SyndicateDataQueryInfo> listByMappingName(String mappingName) throws BusinessException, SystemException {
        List<SyndicateDataQueryInfo> infoObjects = new ArrayList<>();
        List<SyndicateDataQuery> syndicateDataQueryList = syndicateDataQueryBO.listByMappingName(mappingName);
        if (isNotEmpty(syndicateDataQueryList)) {
            infoObjects = convertToList(syndicateDataQueryList, SyndicateDataQueryInfo.class);
            for (SyndicateDataQueryInfo syndicateDataQueryInfo : infoObjects) {
                syndicateDataQueryInfo.setCreatedOn(AdminUtil.getDateFormatMillisForEst(syndicateDataQueryInfo.getCreatedDate().getMillis(), null));
                syndicateDataQueryInfo.setUpdatedOn(AdminUtil.getDateFormatMillisForEst(syndicateDataQueryInfo.getLastModifiedDate().getMillis(), null));
            }
        }
        return infoObjects;
    }

    @Override
    public List<SyndicateDataQueryInfo> listByMappingNameAndType(String mappingName, String mappingType)
            throws BusinessException, SystemException {
        List<SyndicateDataQueryInfo> infoObjects = new ArrayList<>();
        List<SyndicateDataQuery> syndicateDataQueryList = syndicateDataQueryBO.listByMappingNameAndType(mappingName, mappingType);
        if (isNotEmpty(syndicateDataQueryList)) {
            infoObjects = convertToList(syndicateDataQueryList, SyndicateDataQueryInfo.class);
        }
        return infoObjects;
    }

    @Override
    public List<TidSqlInfo> getInterfaceDefinitionSqlInfos(String mappingName, String mappingType) throws BusinessException,
            SystemException {
        List<SyndicateDataQueryInfo> allQueries = listByMappingNameAndType(mappingName, mappingType);
        return conversionHelper.convertToInterfaceDefinitionInfos(allQueries);
    }

    @SuppressWarnings("unchecked")
    public SyndicateDataQueryResponseInfo fetchQueryTestData(SyndicateDataQueryInfo synDataQryInfo) throws BusinessException,
            SystemException {
        SyndicateDataQuery queryObject = convert(synDataQryInfo, SyndicateDataQuery.class);
        SyndicateDataQueryValidator queryValidator = new SyndicateDataQueryValidator();
        queryValidator.validateInputParameters(queryObject);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Map<String, Object>> queryResponse = null;
        queryResponse = syndicateDataQueryBO.fetchTestData(queryObject);
        Map<String, Object> systemParams = syndicateDataQueryBO.systemParameters(queryObject);
        Map<String, String> newAliasColNameMap = (Map<String, String>) systemParams.get(BusinessConstants.NEW_ALIAS_COLNAME_MAP);
        Map<String, String> testedAliasColNameMap = (Map<String, String>) systemParams
                .get(BusinessConstants.TESTED_ALIAS_COLNAME_MAP);
        List<String> deletedParams = (List<String>) systemParams.get(BusinessConstants.DELETED_PARAMS);
        List<String> unDeletedParams = (List<String>) systemParams.get(BusinessConstants.UNDELETED_PARAMS);
        Map<String, Boolean> references = mappingDelegate.isReferenced(deletedParams, queryObject.getMapping().getName(),
                queryObject.getMappingType());
        Map<String, Boolean> unDeletedReferences = mappingDelegate.isReferenced(unDeletedParams, queryObject.getMapping()
                .getName(), queryObject.getMappingType());
        if (MapUtils.isNotEmpty(references)) {
            queryValidator.validateReferences(references);
        }
        if (MapUtils.isNotEmpty(unDeletedReferences)) {
            queryValidator.validateReferences(unDeletedReferences, newAliasColNameMap, testedAliasColNameMap);
        }
        Set<SyndicateDataQueryOutput> queryOutputParams = queryObject.getOutputParameters();
        List<SyndicateDataQueryOutput> queryOutputParamsList = new ArrayList<SyndicateDataQueryOutput>();
        queryOutputParamsList.addAll(queryOutputParams);
        List<SyndicateDataQueryParameterInfo> syndicateDataQryOutput = convertToList(queryOutputParamsList,
                SyndicateDataQueryParameterInfo.class);
        stopWatch.stop();
        SyndicateDataQueryResponseInfo queryResponseInfo = new SyndicateDataQueryResponseInfo();

        LOGGER.error("queryResponse size is :" + queryResponse.size());
        queryResponseInfo.setQueryResponse(queryResponse);
        queryResponseInfo.setQueryExecutionTime(stopWatch.getTime());
        queryResponseInfo.setExecutedQuery(syndicateDataQueryBO.getExecutableQuery(queryObject));
        queryResponseInfo.setSyndicateDataQryOutput(syndicateDataQryOutput);
        queryResponseInfo.setResponseAnArray(checkIfResponseCanbeArray(queryObject));
        LOGGER.error("queryResponseInfo response size:" + queryResponseInfo.getQueryResponse().size());
        return queryResponseInfo;
    }

    private boolean checkIfResponseCanbeArray(SyndicateDataQuery queryObject) {
        Set<SyndicateDataQueryOutput> outputParams = queryObject.getOutputParameters();
        Set<String> dataTypes = new HashSet<String>();
        boolean isArray = false;
        for (SyndicateDataQueryOutput syndicateDataQueryOutput : outputParams) {
            dataTypes.add(syndicateDataQueryOutput.getDataType());
        }
        if (dataTypes.size() == ONE) {
            isArray = true;
        }
        return isArray;
    }

}
