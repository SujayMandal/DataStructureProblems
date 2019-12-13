/**
 * 
 */
package com.ca.umg.business.transaction.query.generator;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.transaction.query.QueryStatement;

/**
 * 
 * @author kamathan
 *
 */
@Named("mongoParamGenerator")
public class MongoQueryParamGenerator implements QueryParamGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoQueryParamGenerator.class);

    private static final String MONGO_REGEX_OPT = "$options";
    private static final String MONGO_OPT_IGNORE_CASE = "i";

    private NumberFormat numberFormat;

    @PostConstruct
    public void init() {
        numberFormat = NumberFormat.getInstance();
    }

    @Override
    public String generateQuery(final QueryStatement queryStatement) throws SystemException {
        JSONObject jsonObject = generateQueryCriteria(queryStatement);
        return (jsonObject == null) ? StringUtils.EMPTY : jsonObject.toString();
    }

    private JSONObject generateQueryCriteria(QueryStatement queryStatement) throws SystemException {
        List<JSONObject> docs = null;
        JSONObject docQuery = null;
        JSONObject docQuerySingle = null;
        try {
            if (queryStatement != null) {
                if (CollectionUtils.isNotEmpty(queryStatement.getQueryStatements())) {
                    for (QueryStatement childQueryStatements : queryStatement.getQueryStatements()) {
                        if (StringUtils.isNotBlank(childQueryStatements.getOperator().getOperator())) {
                            docQuery = generateQueryByOperator(childQueryStatements);
                        }
                        if (docs == null) {
                            docs = new ArrayList<>();
                        }
                        docs.add(docQuery);
                    }
                    docQuery = genSubQuery(queryStatement, docs);
                } else {
                    docs = new ArrayList<JSONObject>();
                    docQuery = genSubQuery(queryStatement, null);
                    docs.add(docQuery);
                    docQuerySingle = new JSONObject();

                    if (queryStatement.getOperator() != null) {
                        docQuerySingle.put(queryStatement.getOperator().getOperator(), docs);
                    }
                }

            }
        } catch (JSONException jexp) {
            LOGGER.error("Error in generateQueryCriteria :: " + jexp.getMessage(), jexp);
            SystemException.newSystemException(BusinessExceptionCodes.BSE000501, new Object[] { jexp.getMessage() });
        }
        return docQuery;
    }

    private JSONObject generateQueryByOperator(QueryStatement childQueryStatements) throws JSONException, SystemException {
        JSONObject docQuery;
        switch (childQueryStatements.getOperator()) {
        case GREATER_THAN:
        case LESS_THAN:
        case GREATER_THAN_EQUAL:
        case LESS_THAN_EQUAL:
            docQuery = generateRangeQuery(childQueryStatements);
            break;
        case IN:
            docQuery = generateInQuery(childQueryStatements);
            break;
        case EQUAL:
            docQuery = generateEqualQuery(childQueryStatements);
            break;
        case NOT_EQUAL:
            docQuery = generateNotEqualQuery(childQueryStatements);
            break;
        case LIKE:
            docQuery = generateLikeQuery(childQueryStatements);
            break;
        default:
            docQuery = generateQueryCriteria(childQueryStatements);
            break;
        }
        return docQuery;
    }

    /**
     * This method would generate the <code> like </code> clause from the given query statements.
     * 
     * @param childQueryStatements
     * @return
     * @throws JSONException
     */
    private JSONObject generateLikeQuery(QueryStatement childQueryStatements) throws JSONException {
        JSONObject likeOpt = null;
        JSONObject docQuery = new JSONObject();
        if (CollectionUtils.isNotEmpty(childQueryStatements.getParamValues())) {
            for (KeyValuePair<String, Object> keyVal : childQueryStatements.getParamValues()) {
                likeOpt = new JSONObject();
                likeOpt.putOpt(childQueryStatements.getOperator().getOperator(), keyVal.getValue());
                // To make query case insensitive
                likeOpt.putOpt(MONGO_REGEX_OPT, MONGO_OPT_IGNORE_CASE);
                docQuery.put(keyVal.getKey(), likeOpt);
            }
        }
        return docQuery;
    }

    /**
     * This method would generate the <code>equals</code> clause from the given query statements.
     * 
     * @param childQueryStatements
     * @return
     * @throws SystemException
     * @throws JSONException
     */
    private JSONObject generateEqualQuery(QueryStatement childQueryStatements) throws SystemException, JSONException {
        JSONObject docQuery = new JSONObject();
        if (CollectionUtils.isNotEmpty(childQueryStatements.getParamValues())) {
            for (KeyValuePair<String, Object> keyVal : childQueryStatements.getParamValues()) {
                docQuery.put(keyVal.getKey(), getValue(keyVal.getValue()));
            }
        }
        return docQuery;
    }

    private Object getValue(Object value) {
        Object modifiedValue = value;
        if (value instanceof String) {
            if (StringUtils.equalsIgnoreCase((String) value, BusinessConstants.STR_TRUE)
                    || StringUtils.equalsIgnoreCase((String) value, BusinessConstants.STR_FALSE)) {
                modifiedValue = Boolean.parseBoolean((String) value);
            } else if (((String) value).matches("^-?[0-9.]*")) {
                try {
                    modifiedValue = numberFormat.parse((String) value);
                } catch (ParseException e) {
                    // the sent value is not of number type hence reasign previous value and return
                    modifiedValue = value;
                }
            }
        }
        return modifiedValue;
    }
    
    /**
     * This method would generate the <code>not equals</code> clause from the given query statements.
     * 
     * @param childQueryStatements
     * @return
     * @throws SystemException
     * @throws JSONException
     */
    private JSONObject generateNotEqualQuery(QueryStatement childQueryStatements) throws SystemException, JSONException {
        JSONObject neOpt = null;
        JSONObject docQuery = new JSONObject();
        if (CollectionUtils.isNotEmpty(childQueryStatements.getParamValues())) {
            for (KeyValuePair<String, Object> keyVal : childQueryStatements.getParamValues()) {
                neOpt = new JSONObject();
                neOpt.putOpt(childQueryStatements.getOperator().getOperator(), getValue(keyVal.getValue()));
                docQuery.put(keyVal.getKey(), neOpt);
            }
        }
        return docQuery;
    }

    /**
     * This method would generate <code>in</code> clause from the given query statements.
     * 
     * @param queryStatement
     * @return
     * @throws JSONException
     */
    private JSONObject generateInQuery(QueryStatement queryStatement) throws JSONException {
        JSONObject inOpt = null;
        JSONObject docQuery = new JSONObject();
        for (KeyValuePair<String, Object> keyValuePair : queryStatement.getParamValues()) {
            inOpt = new JSONObject();
            inOpt.accumulate(queryStatement.getOperator().getOperator(), new JSONArray((List<Object>) keyValuePair.getValue()));
            docQuery.put(keyValuePair.getKey(), inOpt);
        }
        return docQuery;
    }

    /**
     * This method would generate <code>range</code> clause from the given query statements.
     * 
     * @param queryStatement
     * @return
     * @throws JSONException
     */
    private JSONObject generateRangeQuery(QueryStatement queryStatement) throws JSONException {
        JSONObject rangeOpt = null;
        JSONObject docQuery = new JSONObject();
        for (KeyValuePair<String, Object> keyValuePair : queryStatement.getParamValues()) {
            rangeOpt = new JSONObject();
            rangeOpt.putOpt(queryStatement.getOperator().getOperator(), getValue(keyValuePair.getValue()));
            docQuery.put(keyValuePair.getKey(), rangeOpt);
        }
        return docQuery;
    }

    private JSONObject genSubQuery(QueryStatement queryStatement, List<JSONObject> parnDocs) throws JSONException {
        Map<String, Object> paramMap = null;
        List<JSONObject> docs = new ArrayList<JSONObject>();
        JSONObject docQuery = new JSONObject();
        String key = null;
        JSONArray array = null;
        if (CollectionUtils.isNotEmpty(queryStatement.getParamValues())) {
            for (KeyValuePair<String, Object> keyVal : queryStatement.getParamValues()) {
                paramMap = new HashMap<>();
                paramMap.put(keyVal.getKey(), keyVal.getValue());
                JSONObject params = new JSONObject(paramMap);
                docs.add(params);
            }
        }
        if (CollectionUtils.isNotEmpty(parnDocs)) {
            docs.addAll(parnDocs);
        }
        if (queryStatement.getOperator() == null) {
            docQuery = new JSONObject();
            for (JSONObject jsonObject : docs) {
                array = jsonObject.names();
                for (int i = 0; i < array.length(); i++) {
                    key = array.getString(i);
                    docQuery.put(key, jsonObject.get(key));
                }
            }
        } else {
            docQuery.put(queryStatement.getOperator().getOperator(), docs);
        }
        return docQuery;
    }

}
