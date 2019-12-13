package com.ca.umg.business.syndicatedata.daohelper;

import static java.lang.String.format;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQuery;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryInput;
import com.ca.umg.business.syndicatedata.info.ColumnNames;
import com.ca.umg.business.util.AdminUtil;

@Service
public class SyndicateDataQueryHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyndicateDataQueryHelper.class);
    private static final String INPUT_PARAMS = "InputParams";
    private static final String TABLE_ALIAS = "TableAlias";
    private static final String INPUT_PARAM_SEPARATOR = "#";
    private static final String RUNTIME_SPEC = "payload.request.";

    public static final String VERSION_SUB_QUERY_MYSQL = "(SELECT "
            + ColumnNames.VERSIONID.getName()
            + " FROM SYNDICATED_DATA WHERE UNIX_TIMESTAMP(STR_TO_DATE(:payload.request.TESTDATE, '%M-%d-%Y %H:%i')) BETWEEN VALID_FROM/1000 AND COALESCE(VALID_TO, 9223372036854775807)/1000"
            + " AND TABLE_NAME = UPPER('${TABLENAME}'))";
    public static final String VERSION_SUB_QUERY_HSQL = "(SELECT " + ColumnNames.VERSIONID.getName()
            + " FROM SYNDICATED_DATA WHERE "
            + "UNIX_TIMESTAMP(TO_DATE(:payload.request.TESTDATE, 'MON-DD-YYYY')) BETWEEN VALID_FROM AND VALID_TO "
            + "AND TABLE_NAME = UPPER('${TABLENAME}'))";
    public static final String EXEC_QUERY_DATE_FILTER = "${ALIAS_NAME}." + ColumnNames.SYND_VER_ID.getName()
            + " = ${TABLESUBQUERY}";
    
    public static final Map<String, String> APP_TO_SQL_DATE_FORMATS_MAP;

    public static enum DB_TYPE {
        MYSQL, HSQL;
    }
    
    static {
    	APP_TO_SQL_DATE_FORMATS_MAP = new HashMap<String, String>();
    	APP_TO_SQL_DATE_FORMATS_MAP.put("DD-MM-YYYY", "%d-%m-%Y");
    	APP_TO_SQL_DATE_FORMATS_MAP.put("DD-MMM-YYYY", "%d-%M-%Y");
    	APP_TO_SQL_DATE_FORMATS_MAP.put("MMM-DD-YYYY", "%M-%d-%Y");
    	APP_TO_SQL_DATE_FORMATS_MAP.put("MM-DD-YYYY", "%m-%d-%Y");
    	APP_TO_SQL_DATE_FORMATS_MAP.put("YYYY-MM-DD", "%Y-%m-%d");
    	APP_TO_SQL_DATE_FORMATS_MAP.put("YYYY-MMM-DD", "%Y-%M-%d");
    	APP_TO_SQL_DATE_FORMATS_MAP.put("DD/MM/YYYY", "%d/%m/%Y");
    	APP_TO_SQL_DATE_FORMATS_MAP.put("DD/MMM/YYYY", "%d/%M/%Y");
    	APP_TO_SQL_DATE_FORMATS_MAP.put("MMM/DD/YYYY", "%M/%d/%Y");
    	APP_TO_SQL_DATE_FORMATS_MAP.put("MM/DD/YYYY", "%m/%d/%Y");
    	APP_TO_SQL_DATE_FORMATS_MAP.put("YYYY/MM/DD", "%Y/%m/%d");
    	APP_TO_SQL_DATE_FORMATS_MAP.put("YYYY/MMM/DD", "%Y/%M/%d");
    }

    public Map<String, String> createVersionSubQueries(Collection<String> tableNames, DB_TYPE dbType) {
        Map<String, String> map = new HashMap<>();
        StrSubstitutor substitutor = new StrSubstitutor(map);
        Map<String, String> filterMap = new HashMap<>();
        for (String tableName : tableNames) {
            map.put("TABLENAME", tableName);
            switch (dbType) {
            case MYSQL:
                filterMap.put(tableName, substitutor.replace(VERSION_SUB_QUERY_MYSQL));
                break;
            case HSQL:
                filterMap.put(tableName, substitutor.replace(VERSION_SUB_QUERY_HSQL));
                break;
            default:
                break;
            }
        }
        return filterMap;
    }

    public String createFilterQuery(Map<String, String> aliases, DB_TYPE dbType) {
        Map<String, String> subQueries = createVersionSubQueries(aliases.values(), dbType);
        List<String> tableFilter = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        StrSubstitutor substitutor = new StrSubstitutor(map);
        for (Map.Entry<String, String> entry : aliases.entrySet()) {
            map.put("ALIAS_NAME", entry.getKey());
            map.put("TABLESUBQUERY", subQueries.get(entry.getValue()));
            tableFilter.add(substitutor.replace(EXEC_QUERY_DATE_FILTER));
        }
        return StringUtils.join(tableFilter.toArray(), " AND ");
    }

    /**
     * key as alias name and value as table name
     * 
     * @param fromString
     * @return
     */
    public Map<String, String> fetchTableAliases(String fromString) {
        String[] tables = fromString.split(",");
        String[] tableWithAlias = null;
        Map<String, String> tableAliases = new HashMap<>();
        boolean hasAliasName = Boolean.FALSE;
        boolean separatedByAs = Boolean.FALSE;
        for (String tabAlias : tables) {
            tableWithAlias = StringUtils.split(tabAlias);
            hasAliasName = tableWithAlias.length == BusinessConstants.NUMBER_ONE ? Boolean.FALSE : Boolean.TRUE;
            separatedByAs = tableWithAlias.length == BusinessConstants.NUMBER_THREE
                    && StringUtils.equalsIgnoreCase(tableWithAlias[1], "AS") ? Boolean.TRUE : Boolean.FALSE;
            if (separatedByAs) {
                tableAliases.put(tableWithAlias[2], tableWithAlias[0]);
            } else if (hasAliasName) {
                tableAliases.put(tableWithAlias[1].toUpperCase(Locale.ENGLISH), tableWithAlias[0]);
            } else {
                tableAliases.put(tableWithAlias[0], tableWithAlias[0]);
            }
        }
        return tableAliases;
    }

    /**
     * This method creates a map of colname with its respective table alias name. For Ex: if the where clause is : <br>
     * a. t1.col1 = #jjhjh# AND t2.col2 < #jhjh# OR t1.col3 > 'XYZ' AND t2.col4 != t1.col3 it returns {col4=t2, col1=t1, col3=t1,
     * col2=t2} <br>
     * b. col1 = #jjhjh# AND col2 < #jhjh# OR col3 > 'XYZ' AND col4 != t1.col3 it returns {}
     * 
     * @param whereClause
     * @return map of colname with its respective table alias name.
     * @throws BusinessException
     */
    public Map<String, String> fetchColumnNameWithTableAlias(String whereClause) throws BusinessException {
        return getMappingFor(TABLE_ALIAS, whereClause);
    }

    private Map<String, String> getMappingFor(String mapFor, String whereClause) throws BusinessException {
        Map<String, String> whereClauseMap = new HashMap<>();
        if (StringUtils.isNotEmpty(whereClause)) {
            String[] colsExpressionArr = whereClause.split(" AND | OR | or | and ");
            for (String colExpression : colsExpressionArr) {
                colExpression = colExpression.trim();
                String[] colExpressionArr = colExpression.split("=|>|<|!=| in | IN | In | iN ");
                String colNameWithTableAlias = colExpressionArr[0];
                colNameWithTableAlias = colNameWithTableAlias.trim().toUpperCase(Locale.US);
                if (StringUtils.contains(colNameWithTableAlias, '.')) {
                    if (INPUT_PARAMS.equals(mapFor)) {
                        if (isRhsAbsent(colExpressionArr)) {
                            LOGGER.error(format("Where clause should well defined expression, parameter : %s",
                                    colExpressionArr[0]));
                            throw new BusinessException(BusinessExceptionCodes.BSE000054, new Object[] { colExpressionArr[0] });
                        }
                        String[] colWithTableAliasArr = colNameWithTableAlias.split("\\.");
                        String rippedColName = colWithTableAliasArr[1].trim();
                        String valueName = colExpressionArr[1].replaceAll(INPUT_PARAM_SEPARATOR, "").trim();
                        whereClauseMap.put(rippedColName, valueName);
                    } else if (TABLE_ALIAS.equals(mapFor)) {
                        String[] colWithTableAliasArr = colNameWithTableAlias.split("\\.");
                        String rippedColName = colWithTableAliasArr[1].trim();
                        String tableAliasName = colWithTableAliasArr[0].trim();
                        whereClauseMap.put(rippedColName, tableAliasName);
                    }

                } else {
                    LOGGER.error("BSE000058:Exception occured as Table Alias for Column : " + colNameWithTableAlias
                            + " not found.");
                    BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000058,
                            new String[] { colNameWithTableAlias });
                }
            }
        }
        return whereClauseMap;
    }

    private boolean isRhsAbsent(String[] colExpressionArr) {
        return colExpressionArr.length < 2;
    }

    public String generateExecutableQuery(SyndicateDataQuery synDataQuery, String filterQuery, DB_TYPE dbType) {
        StringBuilder builder = new StringBuilder(BusinessConstants.NUMBER_FIFTY);
      //added for fixing the bug-2230 to remove tab in queries
        String selectStringWithNoTabs = synDataQuery.getQueryObject().getSelectString().replaceAll(
				BusinessConstants.REPLACE_TAB_REGEX, BusinessConstants.SPACE);
        builder.append("SELECT ").append(selectStringWithNoTabs);
        builder.append(" FROM ").append(synDataQuery.getQueryObject().getFromString());
        builder.append(" WHERE ");
        if (StringUtils.isNotEmpty(synDataQuery.getQueryObject().getWhereClause())) {
            builder.append(generateRuntimeExecutableQuery(synDataQuery, dbType)).append(" AND ");
        }
        builder.append(filterQuery);
        if (StringUtils.isNotEmpty(synDataQuery.getQueryObject().getOrderByString())) {
            builder.append(" ORDER BY ").append(synDataQuery.getQueryObject().getOrderByString());
        }
        return builder.toString();
    }

    public String generateRuntimeExecutableQuery(SyndicateDataQuery synDataQuery, DB_TYPE dbType) {
        String whereClause = synDataQuery.getQueryObject().getWhereClause();
        if (CollectionUtils.isNotEmpty(synDataQuery.getInputParameters())) {
            for (SyndicateDataQueryInput inputParam : synDataQuery.getInputParameters()) {
                if ("DATE".equals(inputParam.getDataType())) {
                    switch (dbType) {
                    case HSQL:
                        whereClause = whereClause.replaceAll(
                                StringUtils.join(INPUT_PARAM_SEPARATOR, inputParam.getName(), INPUT_PARAM_SEPARATOR),
                                StringUtils.join("TO_DATE(:", RUNTIME_SPEC, inputParam.getName(), ", 'MON-DD-YYYY')"));
                        break;
                    case MYSQL:
                    default:
                        whereClause = whereClause.replaceAll(StringUtils.join(INPUT_PARAM_SEPARATOR, inputParam.getName(),
                                INPUT_PARAM_SEPARATOR), StringUtils.join("STR_TO_DATE(:", RUNTIME_SPEC,
                                StringUtils.replace(inputParam.getName(), BusinessConstants.SLASH, BusinessConstants.DOT),
                                ", '" + APP_TO_SQL_DATE_FORMATS_MAP.get(inputParam.getDataTypeFormat()) + "')"));
                        break;
                    }
                } else if ("BOOLEAN".equals(inputParam.getDataType())) {
                    whereClause = whereClause
                            .replaceAll(StringUtils.join(INPUT_PARAM_SEPARATOR, inputParam.getName(), INPUT_PARAM_SEPARATOR),
                                    StringUtils.join("IF(:", RUNTIME_SPEC, StringUtils.replace(inputParam.getName(),
                                            BusinessConstants.SLASH, BusinessConstants.DOT), ", 1, 0)"));
                } else {
                    whereClause = whereClause.replaceAll(
                            StringUtils.join(INPUT_PARAM_SEPARATOR, inputParam.getName(), INPUT_PARAM_SEPARATOR),
                            StringUtils.join(":", RUNTIME_SPEC,
                                    inputParam.getName().replaceAll(BusinessConstants.SLASH, BusinessConstants.DOT)));
                }
            }
        }
        return whereClause;
    }


    public MapSqlParameterSource inputParameters(SyndicateDataQuery synDataQuery) throws SystemException {
        String paramName = null;
        String paramValue = null;
        MapSqlParameterSource mSource = new MapSqlParameterSource();
        if (CollectionUtils.isNotEmpty(synDataQuery.getInputParameters())) {
            for (SyndicateDataQueryInput input : synDataQuery.getInputParameters()) {
                paramName = input.getName().replaceAll("/", ".");
                paramValue = input.getSampleValue();
                LOGGER.error("Param name is :" + paramName);
                LOGGER.error("Param value is :" + paramValue);
                try {
					if ("TESTDATE".equals(paramName)) {
						long dateInMillis = AdminUtil.getMillisFromEstToUtc(
								paramValue,
								BusinessConstants.SYND_DATE_TIME_FORMAT);
						paramValue = AdminUtil.getFormattedDate(dateInMillis,
								BusinessConstants.SYND_DATE_TIME_FORMAT);
						LOGGER.info("TESTDATE in GMT :" + paramValue);
					}
				} catch (BusinessException e) {
					LOGGER.error("TESTDATE could not convert into GMT.");
				}
                mSource = this.prepareInputParamsQuery(input, paramName, paramValue, mSource);

            }
        }
        return mSource;
    }

    private MapSqlParameterSource prepareInputParamsQuery(SyndicateDataQueryInput input, String paramName, String paramValue,
            MapSqlParameterSource mSource) throws SystemException {
        switch (input.getDataType()) {
        case "DATE":
        case "STRING":
            mSource.addValue(RUNTIME_SPEC + paramName, paramValue, Types.VARCHAR);
            break;
        case "DOUBLE":
        case "DECIMAL":
            try {
                mSource.addValue(RUNTIME_SPEC + paramName, BigDecimal.valueOf(Double.valueOf(paramValue)), Types.DECIMAL);
            } catch (NumberFormatException nfe) {
                LOGGER.error("BSE000057:Data format exception occurred while parsing data for " + paramName + " with value "
                        + paramValue);
                SystemException.newSystemException(BusinessExceptionCodes.BSE000057, new String[] { paramName, paramValue });
            }
            break;
        case "INTEGER":
            mSource.addValue(RUNTIME_SPEC + paramName, paramValue, Types.INTEGER);
            break;
        case "BOOLEAN":
            mSource.addValue(RUNTIME_SPEC + paramName, Boolean.valueOf(paramValue) ? 1 : 0, Types.TINYINT);
            break;
        default:
            break;
        }
        return mSource;
    }

    public Map<String, String> fetchColumnNameWithValueName(String whereClause) throws BusinessException {
        return getMappingFor(INPUT_PARAMS, whereClause);
    }
}
