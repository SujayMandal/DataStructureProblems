/*
 * SyndicateDataQueryBOImpl.java
 *
 * -----------------------------------------------------------
 * Copyright 2012 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.syndicatedata.bo;

import static com.ca.umg.business.constants.BusinessConstants.SYNDICATE_DATA_SEQUENCE;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.getLast;
import static java.lang.String.format;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.mapping.dao.MappingDAO;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.syndicatedata.dao.SyndicateDataQueryDAO;
import com.ca.umg.business.syndicatedata.dao.SyndicateDataQueryInputDAO;
import com.ca.umg.business.syndicatedata.dao.SyndicateDataQueryOutputDAO;
import com.ca.umg.business.syndicatedata.dao.SyndicateVersionDataDAO;
import com.ca.umg.business.syndicatedata.daohelper.SyndicateDataQueryHelper;
import com.ca.umg.business.syndicatedata.daohelper.SyndicateDataQueryHelper.DB_TYPE;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQuery;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryInput;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryOutput;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.query.validator.SyndicateDataQueryValidator;

/**
 * Business Object for syndicate data extraction
 **/

@Service
public class SyndicateDataQueryBOImpl implements SyndicateDataQueryBO {

    private static final int WITH_ALIAS = 2;

	private static final Logger LOGGER = LoggerFactory.getLogger(SyndicateDataQueryBOImpl.class);

    @Inject
    private SyndicateDataQueryDAO syndicateDataqueryDAO;
    
    @Inject
    private MappingDAO mappingDAO;

    @Inject
    private SyndicateDataQueryHelper sQueryHelper;

    @Inject
    private SyndicateVersionDataDAO syndicateVersionDataDAO;

    @Inject
    private SyndicateDataQueryInputDAO dataQueryInputDAO;

    @Inject
    private SyndicateDataQueryOutputDAO dataQueryOutputDAO;

    // Added for test case execution.
    private SyndicateDataQueryHelper.DB_TYPE dbType = SyndicateDataQueryHelper.DB_TYPE.MYSQL;

    /**
     * List all Queries
     * 
     * @return List of SyndicateDataQuery
     * 
     * @throws BusinessException
     * @throws SystemException
     **/
    @Override
    public List<SyndicateDataQuery> listAll() throws BusinessException, SystemException {
        List<SyndicateDataQuery> syndicateDataQueries = new ArrayList<>();
        try {
            syndicateDataQueries = syndicateDataqueryDAO.findAll(new Sort(Sort.Direction.ASC, SYNDICATE_DATA_SEQUENCE));
        } catch (DataAccessException e) {
            LOGGER.error("Unable to fetch syndicate data queries", e);
            throw new SystemException(BusinessExceptionCodes.BSE000037, new Object[] {}, e);
        }
        return syndicateDataQueries;
    }

    @Override
    public SyndicateDataQuery create(SyndicateDataQuery synDataQuery) throws BusinessException, SystemException {
        SyndicateDataQuery savedSynDataQuery = null;
        if (syndicateDataqueryDAO.findByNameAndMappingId(synDataQuery.getName(), synDataQuery.getMapping().getId()) != null) {
            LOGGER.error("A {} with the name 1} already exists", BusinessConstants.SYNDICATE_DATA_QUERY, synDataQuery.getName());
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000039, new Object[] {
                    BusinessConstants.SYNDICATE_DATA_QUERY, synDataQuery.getName() });
        }

        try {
            synDataQuery.getQueryObject().setExecutableQuery(getExecutableQuery(synDataQuery));
            Integer execSequence = (Integer) ObjectUtils.defaultIfNull(
                    syndicateDataqueryDAO.getMaxSequenceQueryMappingID(synDataQuery.getMapping().getId()), Integer.valueOf(0));
            synDataQuery.setExecSequence(execSequence + 1);
            savedSynDataQuery = syndicateDataqueryDAO.save(synDataQuery);
            LOGGER.debug("{} created successfully.", synDataQuery.getName());
        } catch (DataAccessException dae) {
            LOGGER.error("An error occurred during {} of syndicate data query {}.", "INSERT", synDataQuery.getName());
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000038,
                    new String[] { "INSERT", synDataQuery.getName() });
        }
        return savedSynDataQuery;
    }

    @Override
    public String getExecutableQuery(SyndicateDataQuery synDataQuery) {
        Map<String, String> aliases = sQueryHelper.fetchTableAliases(synDataQuery.getQueryObject().getFromString());
        String filterQuery = sQueryHelper.createFilterQuery(aliases, SyndicateDataQueryHelper.DB_TYPE.MYSQL);
        filterQuery = sQueryHelper.createFilterQuery(aliases, dbType);
        return sQueryHelper.generateExecutableQuery(synDataQuery, filterQuery, dbType);
    }

    @Override
    public List<Map<String, Object>> fetchTestData(SyndicateDataQuery synDataQuery) throws BusinessException, SystemException {
    	String executableQuery = getExecutableQuery(synDataQuery);
        synDataQuery.getQueryObject().setExecutableQuery(executableQuery);
        MapSqlParameterSource mSource = sQueryHelper.inputParameters(synDataQuery);
        // NEW REFACTORED CODE - START
        SyndicateDataQueryHelper queryHelper = new SyndicateDataQueryHelper();
        Map<String, String> tableAliasMap = queryHelper.fetchTableAliases(synDataQuery.getQueryObject().getFromString());
        Map<String, String> colWithTableAliasMap = queryHelper.fetchColumnNameWithTableAlias(synDataQuery.getQueryObject()
                .getWhereClause());
        Map<String, String> colWithValueNameMap = queryHelper.fetchColumnNameWithValueName(synDataQuery.getQueryObject()
                .getWhereClause());
        if (colWithTableAliasMap.isEmpty() && StringUtils.isNotEmpty(synDataQuery.getQueryObject().getWhereClause())) {
            LOGGER.error("BSE000053:Exception occured as no table aliases were found in input parameters");
            SystemException.newSystemException(BusinessExceptionCodes.BSE000053,
                    new String[] { "Exception occured as no table aliases were found in input parameters" });
        }
        SyndicateDataQueryValidator syndicateDataQueryValidator = new SyndicateDataQueryValidator();
        Map<String, List<SyndicateDataColumnInfo>> tableColumnInfoMap = buildTableColumnInfo(tableAliasMap);
        List<String> dataTypeMismatchList = syndicateDataQueryValidator.validate(tableAliasMap, colWithTableAliasMap,
                colWithValueNameMap, synDataQuery.getInputParameters(), tableColumnInfoMap, synDataQuery);
        if (!dataTypeMismatchList.isEmpty()) {
            LOGGER.error("BSE000052:Exception occured during datatype validation for input params");
            SystemException.newSystemException(BusinessExceptionCodes.BSE000052, dataTypeMismatchList.toArray());
        }
        // NEW REFACTORED CODE - END

        LOGGER.error("synDataQuery is " + synDataQuery);
        LOGGER.error("mSource is " + mSource);

        List<Map<String, Object>> testData = syndicateVersionDataDAO.fetchSyndDataQueryOutputs(synDataQuery, mSource);

        LOGGER.error("testData size is :" + testData.size());

        // Implemented because once fetch Data is available.
        syndicateDataQueryValidator.validateQReturnTypes(synDataQuery);

        return testData;
    }

    private Map<String, List<SyndicateDataColumnInfo>> buildTableColumnInfo(Map<String, String> tableAliasMap)
            throws SystemException {
        Iterator<String> tableAliasMapIterator = tableAliasMap.values().iterator();
        Map<String, List<SyndicateDataColumnInfo>> tableColumnInfoMap = new HashMap<String, List<SyndicateDataColumnInfo>>();
        while (tableAliasMapIterator.hasNext()) {
            String tableName = tableAliasMapIterator.next();
            tableColumnInfoMap.put(tableName, syndicateVersionDataDAO.getTableColumnInfo(tableName));
        }
        return tableColumnInfoMap;
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public SyndicateDataQuery update(SyndicateDataQuery synDataQuery) throws BusinessException, SystemException {
        SyndicateDataQuery savedSynDataQuery = syndicateDataqueryDAO.findByNameAndMappingId(synDataQuery.getName(), synDataQuery
                .getMapping().getId());
        synDataQuery.setId(savedSynDataQuery.getId());
        synDataQuery.setTenantId(savedSynDataQuery.getTenantId());
        synDataQuery.getQueryObject().setExecutableQuery(getExecutableQuery(synDataQuery));
        populateParameters(synDataQuery);
        try {
            dataQueryInputDAO.delete(savedSynDataQuery.getInputParameters());
            dataQueryOutputDAO.delete(savedSynDataQuery.getOutputParameters());
            savedSynDataQuery = syndicateDataqueryDAO.save(synDataQuery);
            LOGGER.debug("{} query updated successfully.", synDataQuery.getName());
            dataQueryInputDAO.flush();
            dataQueryOutputDAO.flush();
            syndicateDataqueryDAO.flush();
        } catch (DataAccessException dae) {
            LOGGER.error("An error occurred during {} of syndicate data query {}.", "UPDATE", synDataQuery.getName());
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000038,
                    new String[] { "UPDATE", synDataQuery.getName() });
        }
        return savedSynDataQuery;
    }

    @Override
    public List<SyndicateDataQuery> listByMappingName(String mappingName) throws BusinessException, SystemException {
        List<SyndicateDataQuery> syndicateDataQueries = new ArrayList<>();
        try {
            syndicateDataQueries = syndicateDataqueryDAO.findByMappingNameOrderByExecSequenceAsc(mappingName);
        } catch (DataAccessException e) {
            LOGGER.error(format("Unable to fetch syndicate data queries, mapping name : %s", mappingName), e);
            throw new SystemException(BusinessExceptionCodes.BSE000050, new Object[] { mappingName }, e);
        }
        return syndicateDataQueries;
    }

    @Override
    public List<SyndicateDataQuery> listByMappingNameAndType(String mappingName, String mappingType) throws BusinessException,
            SystemException {
        List<SyndicateDataQuery> syndicateDataQueries = new ArrayList<>();
        try {
            syndicateDataQueries = syndicateDataqueryDAO.findByMappingNameAndMappingTypeOrderByExecSequenceAsc(mappingName,
                    mappingType);
        } catch (DataAccessException e) {
            LOGGER.error(
                    format("Unable to fetch syndicate data queries, mapping name : %s, mapping type : %s", mappingName,
                            mappingType), e);
            throw new SystemException(BusinessExceptionCodes.BSE000051, new Object[] { mappingName, mappingType }, e);
        }
        return syndicateDataQueries;
    }

    private void populateParameters(SyndicateDataQuery query) {
        if (isNotEmpty(query.getInputParameters())) {
            for (SyndicateDataQueryInput input : query.getInputParameters()) {
                input.setQuery(query);
            }
        }
        if (isNotEmpty(query.getOutputParameters())) {
            for (SyndicateDataQueryOutput output : query.getOutputParameters()) {
                output.setQuery(query);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = { Exception.class })
    public void updateQueryExecutionSequence(List<SyndicateDataQuery> queries) throws BusinessException, SystemException {
        if (isNotEmpty(queries)) {
            validateQueryAttributes(queries);
            validateSequences(queries);
            updateSequence(queries);
        }
    }

    private void updateSequence(List<SyndicateDataQuery> queries) throws SystemException {
        // we have to save the complete entity because otherwise we loose the auditing feature built in the framework level
        for (SyndicateDataQuery query : queries) {
            try {
                SyndicateDataQuery queryForUpdation = find(query);
                updateSequenceAttribute(queryForUpdation, query.getExecSequence());
                save(queryForUpdation);
            } catch (DataAccessException e) {
                LOGGER.error("Unable to update syndicate data query execution sequence", e);
                throw new SystemException(BusinessExceptionCodes.BSE000040, new Object[] { query.getName(),
                        query.getExecSequence() }, e);
            }
        }
    }

    private void save(SyndicateDataQuery queryForUpdation) {
        syndicateDataqueryDAO.save(queryForUpdation);
    }

    private void updateSequenceAttribute(SyndicateDataQuery queryForUpdation, Integer sequence) {
        queryForUpdation.setExecSequence(sequence);
    }

    private SyndicateDataQuery find(SyndicateDataQuery query) {
        // TODO : this will have to be changed to also query by mapping id
        return syndicateDataqueryDAO.findByName(query.getName());
    }

    private void validateSequences(List<SyndicateDataQuery> queries) throws BusinessException {
        Set<Integer> sequences = new TreeSet<>();
        for (SyndicateDataQuery query : queries) {
            Integer sequence = query.getExecSequence();
            if (isNotPositive(sequence)) {
                LOGGER.error("Syndicate data query sequence number has to be a positive integer, sequence number : {}", sequence);
                throw new BusinessException(BusinessExceptionCodes.BSE000044, new Object[] { sequence });
            } else {
                // Add to a Set and check for duplicate values.
                if (!sequences.add(sequence)) {
                    LOGGER.error("Cannot have two queries with same sequence number, sequence number : {}", sequence);
                    throw new BusinessException(BusinessExceptionCodes.BSE000041, new Object[] { sequence });
                }
            }
        }
        int expectedNoOfSequences = queries.size();
        if (isNotEmpty(sequences) && isNotSetOfExpectedSequences(sequences, expectedNoOfSequences)) {
            LOGGER.error("Query execution sequences have to be positive numbers starting from one and in increments of one.");
            throw new BusinessException(BusinessExceptionCodes.BSE000042, new Object[] {});
        }
    }

    private boolean isNotSetOfExpectedSequences(Set<Integer> sequences, int expectedNoOfSequences) {
        return getFirst(sequences, 0) != 1 || getLast(sequences) != expectedNoOfSequences;
    }

    private void validateQueryAttributes(List<SyndicateDataQuery> queries) throws BusinessException {
        for (SyndicateDataQuery query : queries) {
            if (isEmpty(query.getName())) {
                LOGGER.error("Syndicate data query name cannot be empty.");
                throw new BusinessException(BusinessExceptionCodes.BSE000043, new Object[] {});
            }
        }
    }

    private boolean isNotPositive(Integer intValue) {
        return intValue == null || intValue <= 0;
    }

    @Override
    public Map<String, Object> systemParameters(SyndicateDataQuery dataQueryTested) throws BusinessException, SystemException {
        Map<String, Object> queryParamMap = new HashMap<String, Object>();
        Map<String, String> newAliasColNameMap = null;
        Map<String, String> testedAliasColNameMap = null;
        List<String> deletedParams = new ArrayList<>();
        List<String> unDeletedParams = new ArrayList<>();
        Mapping mapping = mappingDAO.findByName(dataQueryTested.getMapping().getName());
        SyndicateDataQuery dataQueryDB = syndicateDataqueryDAO.findByNameAndMappingId(dataQueryTested.getName(), mapping.getId());
        if (dataQueryDB != null) {
            Set<SyndicateDataQueryOutput> params = dataQueryDB.getOutputParameters();
            String selectStringOfNewParams = dataQueryDB.getQueryObject().getSelectString();
            newAliasColNameMap = prepareAliasColNameMap(selectStringOfNewParams);
            Set<SyndicateDataQueryOutput> testedParams = dataQueryTested.getOutputParameters();
            String selectStringOfTestedParams = dataQueryTested.getQueryObject().getSelectString();
            testedAliasColNameMap = prepareAliasColNameMap(selectStringOfTestedParams);
            boolean available = Boolean.FALSE;
            for (SyndicateDataQueryOutput outputParam : params) {
                available = Boolean.FALSE;
                for (SyndicateDataQueryOutput testOutputParam : testedParams) {
                    if (StringUtils.equals(outputParam.getName(), testOutputParam.getName())) {
                        unDeletedParams.add(StringUtils.join(dataQueryTested.getName(), "/", outputParam.getName()));
                        available = Boolean.TRUE;
                        break;
                    }
                }
                if (!available) {
                    deletedParams.add(StringUtils.join(dataQueryTested.getName(), "/", outputParam.getName()));
                }
            }
        }
        queryParamMap.put(BusinessConstants.UNDELETED_PARAMS, unDeletedParams);
        queryParamMap.put(BusinessConstants.DELETED_PARAMS, deletedParams);
        queryParamMap.put(BusinessConstants.NEW_ALIAS_COLNAME_MAP, newAliasColNameMap);
        queryParamMap.put(BusinessConstants.TESTED_ALIAS_COLNAME_MAP, testedAliasColNameMap);
        LOGGER.debug("Output Parameters Deleted Successfully.");
        return queryParamMap;
    }

    /**
     * This method is for JUnit Test
     * 
     * @param dbType
     */
    private void setDbType(SyndicateDataQueryHelper.DB_TYPE dbType) {
        this.dbType = dbType;
    }

    @Override
    public void runInTestMode(boolean isTestMode) {
        if (isTestMode) {
            setDbType(DB_TYPE.HSQL);
        }
    }

    private Map<String, String> prepareAliasColNameMap(String selectStringOfNewParams) {
        Map<String, String> aliasColNameMap = new HashMap<String, String>();
        //added for fixing the bug-2230 to remove tab in queries
        String selectStringWithoutTab = selectStringOfNewParams.replaceAll(
				BusinessConstants.REPLACE_TAB_REGEX, BusinessConstants.SPACE);
        String[] newParams = selectStringWithoutTab.split(",");
        for (String newParam : newParams) {
            String[] names = newParam.split(BusinessConstants.COL_ALIAS_REGEX);
            if (names.length == WITH_ALIAS) {
            	aliasColNameMap.put(names[1].trim(), names[0].trim());
            } else {
            	aliasColNameMap.put(names[0].trim(), names[0].trim());
            }
            
        }
        return aliasColNameMap;
    }

}
