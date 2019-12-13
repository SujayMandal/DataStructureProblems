/*
 * SyndicateVersionDataDAO.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.syndicatedata.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.stereotype.Repository;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQuery;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryOutput;
import com.ca.umg.business.syndicatedata.info.ColumnNames;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.util.SyndicateDataUtil;

/**
 * 
 * Syndicate Version Details DAO to fetch meta data or key's of a given table.
 * 
 * @author repvenk
 * 
 */
@Repository
@SuppressWarnings("unchecked")
public class SyndicateVersionDataDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyndicateVersionDataDAO.class);

    @Inject
    @Named(value = "umgAdminDataSource")
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    /**
     * Initializes JDBC template with data source.
     */
    @PostConstruct
    public void initializeTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * Executes DDL statements. e.g. create table, create index, drop table etc.
     */
    public void executeQuery(String query) {
        jdbcTemplate.execute(query);
    }

    /**
     * Retrieves column information for a given database table. Column information is fetched using Database MetaData.
     * 
     * @param tableName
     * @return List
     * @throws SystemException
     */
    public List<SyndicateDataColumnInfo> getTableColumnInfo(final String tableName) throws SystemException {
        Object dataColumnInfos = null;
        try {
            dataColumnInfos = JdbcUtils.extractDatabaseMetaData(dataSource, new DatabaseMetaDataCallback() {

                @Override
                public Object processMetaData(DatabaseMetaData metaData) throws SQLException, MetaDataAccessException {
                    SyndicateDataColumnInfo dataColumnInfo = null;
                    List<SyndicateDataColumnInfo> dataColumnInfos = new ArrayList<>();
                    ResultSet resultSet = null;
                    try {
                        resultSet = metaData.getColumns(null, null, tableName, null);
                        int colSize = 0;
                        int precision = 0;
                        while (resultSet.next()) {
                            if (StringUtils.equals(resultSet.getString(ColumnNames.COLUMNNAME.getName()),
                                    ColumnNames.SYND_VER_ID.getName())) {
                                continue;
                            }
                            dataColumnInfo = new SyndicateDataColumnInfo();
                            
                            // Modified to fix UMG-4459
                            dataColumnInfo.setDisplayName(formatName(resultSet.getString(ColumnNames.COLUMNNAME.getName())));
                            dataColumnInfo.setField(formatName(resultSet.getString(ColumnNames.COLUMNNAME.getName())));
                         // Modified to fix UMG-4459
                            dataColumnInfo.setColumnType(SyndicateDataUtil.getUIDataType(resultSet.getString(
                                    ColumnNames.DATATYPENAME.getName()).toUpperCase(Locale.ENGLISH)));
                            colSize = resultSet.getInt(ColumnNames.COLUMNSIZE.getName());
                            precision = resultSet.getInt(ColumnNames.PRECISION.getName());
                            dataColumnInfo.setColumnSize(colSize - precision);
                            dataColumnInfo.setPrecision(precision);
                            dataColumnInfo.setMandatory(!resultSet.getBoolean(ColumnNames.NULLABLEFIELD.getName()));
                            dataColumnInfo.setDescription(resultSet.getString(ColumnNames.DESCRIPTION.getName()));
                            dataColumnInfos.add(dataColumnInfo);
                        }
                    } finally {
                        if (resultSet != null) {
                            resultSet.close();
                        }
                    }
                    return dataColumnInfos;
                }
            });
        } catch (MetaDataAccessException e) {
            LOGGER.error("BSE000016:Exception occured accessing table metadata", e);
            SystemException.newSystemException("BSE000016", new String[] { "Exception occured accessing table metadata" }, e);
        }
        return (List<SyndicateDataColumnInfo>) ObjectUtils.defaultIfNull(dataColumnInfos,
                new ArrayList<SyndicateDataColumnInfo>());
    }

 // UMG-4459 start
    private String formatName(String name) {
        String formattedName = name;
        if (StringUtils.isNotBlank(formattedName) && Character.isDigit(formattedName.charAt(0))) {
            formattedName = StringUtils.join(BusinessConstants.SYND_CLMN_NAME_ESC_CHAR, formattedName);
        }
        return formattedName;
    }
    // UMG-4459 end

    /**
     * Retrieves indexes information for a given database table. Result set is as below INDEXNAME1 COLUMN1 1 INDEXNAME1 COLUMN2 2
     * INDEXNAME2 COLUMN1 1 The number column above is the sequence of the columns for index. Result generated from this method is
     * a map which holds index name as key and the list of the columns as value. {{INDEXNAME1: [COLUMN1, COLUMN2]}, {INDEXNAME2:
     * [COLUMN1]}}
     * 
     * @param tableName
     * @return Map
     * @throws SystemException
     */
    public Map<String, List<String>> getTableKeys(final String tableName) throws SystemException {
        Object indexInfo = null;
        try {
            indexInfo = JdbcUtils.extractDatabaseMetaData(dataSource, new DatabaseMetaDataCallback() {

                @Override
                public Object processMetaData(DatabaseMetaData metaData) throws SQLException, MetaDataAccessException {
                    Map<String, List<String>> indexInfo = new HashMap<>();
                    List<String> columnNames = null;
                    ResultSet resultSet = null;
                    String indexName = null;
                    try {
                        resultSet = metaData.getIndexInfo(null, null, tableName, false, true);
                        while (resultSet.next()) {
                            indexName = StringUtils.remove(resultSet.getString(ColumnNames.INDEXNAME.getName()), tableName + "_");
                            if (indexInfo.containsKey(indexName)) {
                                indexInfo.get(indexName).add(resultSet.getString(ColumnNames.COLUMNNAME.getName()));
                            } else {
                                columnNames = new ArrayList<>();
                                columnNames.add(resultSet.getString(ColumnNames.COLUMNNAME.getName()));
                                indexInfo.put(indexName, columnNames);
                            }
                        }
                    } finally {
                        if (resultSet != null) {
                            resultSet.close();
                        }
                    }
                    return indexInfo;
                }
            });
        } catch (MetaDataAccessException e) {
            LOGGER.error("BSE000017:Exception occured accessing table indexes.", e);
            SystemException.newSystemException("BSE000017", new String[] { "Exception occured accessing table indexes" }, e);
        }
        return (Map<String, List<String>>) ObjectUtils.defaultIfNull(indexInfo, new HashMap<String, List<String>>());
    }

    public void deleteVersionData(String tableName, Long versionId) {
        executeQuery("delete from " + tableName + " where " + ColumnNames.SYND_VER_ID.getName() + "=" + versionId);
    }

    public void insertData(String[] statements) throws SystemException {
        DataSource ds = jdbcTemplate.getDataSource();
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = ds.getConnection();
            connection.setAutoCommit(false);
            ps = connection.prepareStatement(statements[0]);
            for (int i = 0; i < statements.length; i++) {
                ps.addBatch(statements[i]);
            }
            ps.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            LOGGER.error("BSE000085:Error during inserting records for syndicate data.", e);
            SystemException.newSystemException(BusinessExceptionCodes.BSE000085,
                    new String[] { "Error during inserting records for syndicate data." }, e);
        } finally {
            try {
               if(ps != null) {
            		ps.close();
            	}
            	if(connection!=null) {
                connection.close();
            	}
            	
            } catch (SQLException e) {
                LOGGER.error("Error during closing connection", e);
            }
        }
    }

    /**
     * This method gets the Syndicated data for the version id from the table name
     * 
     * @param tableName
     * @param versionId
     * @return
     */
    public List<Map<String, Object>> getData(String tableName, Long versionId) {
        return jdbcTemplate.queryForList("select * from " + tableName + " where " + ColumnNames.SYND_VER_ID.getName() + "="
                + versionId);
    }

    public void dropTable(String tableName) {
        executeQuery("drop table " + tableName);
    }

    public List<Map<String, Object>> fetchSyndDataQueryOutputs(SyndicateDataQuery synDataQuery, MapSqlParameterSource valueMap)
            throws SystemException {
        List<Map<String, Object>> queryList = null;
        try {
            LOGGER.error("synDataQuery.getQueryObject().getExecutableQuery() is :"
                    + synDataQuery.getQueryObject().getExecutableQuery());

            QueryResultObject resultObject = namedJdbcTemplate.query(synDataQuery.getQueryObject().getExecutableQuery(),
                    valueMap, new QueryResultSetExtractor());
            synDataQuery.setOutputParameters(resultObject.getQueryOutputs());

            LOGGER.error("resultObject.getResultMapList() size is :" + resultObject.getResultMapList().size());

            queryList = resultObject.getResultMapList();
        } catch (DataAccessException exception) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE000046,
                    new String[] { exception.getCause().getMessage() });
        }

        return queryList;
    }

    private final class QueryResultSetExtractor implements ResultSetExtractor<QueryResultObject> {
        @Override
        public QueryResultObject extractData(ResultSet rs) throws SQLException, DataAccessException {
            QueryResultObject queryResultObject = new QueryResultObject();
            ResultSetMetaData metaData = rs.getMetaData();
            Set<SyndicateDataQueryOutput> queryOutputs = populateQueryOutputs(metaData);
            List<Map<String, Object>> resultMapList = populateQueryResult(rs, queryOutputs);
            queryResultObject.setQueryOutputs(queryOutputs);
            queryResultObject.setResultMapList(resultMapList);
            return queryResultObject;
        }

        private List<Map<String, Object>> populateQueryResult(ResultSet rs, Set<SyndicateDataQueryOutput> queryOutputs)
                throws SQLException {
            List<Map<String, Object>> resultMapList = new ArrayList<Map<String, Object>>();
            while (rs.next()) {
                Map<String, Object> resultMap = new LinkedHashMap<String, Object>();
                for (SyndicateDataQueryOutput syndicateDataQueryOutput : queryOutputs) {
                    switch (syndicateDataQueryOutput.getDataType()) {
                    case "STRING":
                        resultMap.put(syndicateDataQueryOutput.getName(), rs.getString(syndicateDataQueryOutput.getName()));
                        break;
                    case "INTEGER":
                        resultMap.put(syndicateDataQueryOutput.getName(), rs.getInt(syndicateDataQueryOutput.getName()));
                        break;
                    case "BOOLEAN":
                        resultMap.put(syndicateDataQueryOutput.getName(), rs.getInt(syndicateDataQueryOutput.getName()));
                        break;
                    case "DATE":
                        resultMap.put(syndicateDataQueryOutput.getName(), rs.getDate(syndicateDataQueryOutput.getName()));
                        break;
                    case "DOUBLE":
                    case "DECIMAL":
                        resultMap.put(syndicateDataQueryOutput.getName(), rs.getDouble(syndicateDataQueryOutput.getName()));
                        break;
                    default:
                        break;
                    }
                }
                resultMapList.add(resultMap);
            }
            return resultMapList;
        }

        private Set<SyndicateDataQueryOutput> populateQueryOutputs(ResultSetMetaData metaData) throws SQLException {
            Set<SyndicateDataQueryOutput> queryOutputs = new LinkedHashSet<SyndicateDataQueryOutput>();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                SyndicateDataQueryOutput queryOutput = new SyndicateDataQueryOutput();
                queryOutput.setName(StringUtils.isEmpty(metaData.getColumnLabel(i)) ? metaData.getColumnName(i) : metaData
                        .getColumnLabel(i));
                queryOutput.setSequence(i);
                switch (metaData.getColumnTypeName(i)) {
                case "VARCHAR":
                    queryOutput.setDataType("STRING");
                    break;
                case "INTEGER":
                case "INT":
                    queryOutput.setDataType("INTEGER");
                    break;
                case "TINYINT":
                    queryOutput.setDataType("BOOLEAN");
                    break;
                case "DATE":
                    queryOutput.setDataType("DATE");
                    break;
                case "DOUBLE":
                case "DECIMAL":
                    queryOutput.setDataType("DOUBLE");
                    break;
                default:
                    break;
                }
                queryOutputs.add(queryOutput);
            }
            return queryOutputs;
        }
    }

    private final class QueryResultObject {

        private Set<SyndicateDataQueryOutput> queryOutputs;

        private List<Map<String, Object>> resultMapList;

        public Set<SyndicateDataQueryOutput> getQueryOutputs() {
            return queryOutputs;
        }

        public void setQueryOutputs(Set<SyndicateDataQueryOutput> queryOutputs) {
            this.queryOutputs = queryOutputs;
        }

        public List<Map<String, Object>> getResultMapList() {
            return resultMapList;
        }

        public void setResultMapList(List<Map<String, Object>> resultMapList) {
            this.resultMapList = resultMapList;
        }
    }
}
