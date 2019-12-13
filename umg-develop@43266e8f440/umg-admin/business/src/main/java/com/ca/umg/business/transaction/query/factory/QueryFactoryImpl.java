/**
 * 
 */
package com.ca.umg.business.transaction.query.factory;

import javax.inject.Inject;
import javax.inject.Named;

import com.ca.umg.business.transaction.query.generator.QueryParamGenerator;

/**
 * 
 * @author kamathan
 *
 */
@Named
public class QueryFactoryImpl implements QueryFactory {

    @Inject
    @Named("mongoParamGenerator")
    private QueryParamGenerator mongoParamGenerator;

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.docx.service.query.factory.QueryFactory#getQueryParamGenerator(com.ca.docx.service.model.common.DocSource)
     */

    @Override
    public QueryParamGenerator getQueryParamGenerator(String generatorType) {
        QueryParamGenerator returnQueryParameter = null;
        if (QueryGeneratorType.GENERATOR_TYPE_MONGO.equalsIgnoreCase(generatorType)) {
            returnQueryParameter = mongoParamGenerator;
        }

        return returnQueryParameter;
    }

}
