/**
 * 
 */
package com.ca.umg.business.transaction.query.factory;

import com.ca.umg.business.transaction.query.generator.QueryParamGenerator;

/**
 * 
 * @author kamathan
 *
 */
public interface QueryFactory {

    /**
     * Gets the appropriate query generator for generating query parameters.
     * 
     * @param evidenceSource
     * @return {@link QueryParamGenerator}
     */
    QueryParamGenerator getQueryParamGenerator(String generatorType);
}
