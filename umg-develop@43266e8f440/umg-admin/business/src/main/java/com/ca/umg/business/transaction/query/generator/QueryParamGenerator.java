package com.ca.umg.business.transaction.query.generator;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.transaction.query.QueryStatement;

/**
 * 
 * @author kamathan
 *
 */
public interface QueryParamGenerator {

    /**
     * 
     * 
     * @param queryStatement
     * @return
     * @throws SystemException
     */
    public String generateQuery(QueryStatement queryStatement) throws SystemException;
}
