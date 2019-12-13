package com.ca.umg.business.syndicatedata.query;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.ca.framework.core.custom.mapper.OrikaConverter;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQuery;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryInput;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQueryOutput;
import com.ca.umg.business.syndicatedata.info.SyndicateDataQueryInfo;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.metadata.Type;

@OrikaConverter
public class SyndicateDataQueryCustomConverter extends BidirectionalConverter<SyndicateDataQueryInfo, SyndicateDataQuery> {

    private final MapperFacade inMapper = new SyndicateCustomConvertorMapper();
    
    private class SyndicateCustomConvertorMapper extends ConfigurableMapper {
        @Override
        protected void configure(MapperFactory factory) {
            factory.getConverterFactory().registerConverter(new PassThroughConverter(org.joda.time.DateTime.class));
            factory.getConverterFactory().registerConverter(new PassThroughConverter(org.joda.time.LocalDateTime.class));
            factory.getConverterFactory().registerConverter(new PassThroughConverter(org.joda.time.LocalTime.class));
        }
    };

    private <T> void populateQueryObjectInParams(Set<T> parameters, SyndicateDataQuery queryObject) {
        if (isNotEmpty(parameters)) {
            for (Object param : parameters) {
                if (param instanceof SyndicateDataQueryInput) {
                    ((SyndicateDataQueryInput) param).setQuery(queryObject);
                    ((SyndicateDataQueryInput) param).setDataTypeFormat(StringUtils.substringAfter(((SyndicateDataQueryInput) param).getDataType(), "-"));
                    ((SyndicateDataQueryInput) param).setDataType(StringUtils.substringBefore(((SyndicateDataQueryInput) param).getDataType(), "-"));
                } else {
                    ((SyndicateDataQueryOutput) param).setQuery(queryObject);
                }
            }
        }
    }

    @Override
    public SyndicateDataQuery convertTo(SyndicateDataQueryInfo synDataQueryInfo, Type<SyndicateDataQuery> destinationType) {
        SyndicateDataQuery synDataQry = null;
        if (synDataQueryInfo != null) {
            synDataQry = inMapper.map(synDataQueryInfo, SyndicateDataQuery.class);
            if (synDataQry != null) {
	            if (synDataQry.getQueryObject() != null && synDataQueryInfo.getQueryObject() != null) {
	            	synDataQry.getQueryObject().setFromString(synDataQueryInfo.getQueryObject().getFromString().trim().toUpperCase());
	            }
	            populateQueryObjectInParams(synDataQry.getInputParameters(), synDataQry);
	            populateQueryObjectInParams(synDataQry.getOutputParameters(), synDataQry);
            }
        }
        return synDataQry;
    }

    @Override
    public SyndicateDataQueryInfo convertFrom(SyndicateDataQuery synDataQuery, Type<SyndicateDataQueryInfo> destinationType) {
        SyndicateDataQueryInfo synDataQryInfo = null;
        if (synDataQuery != null) {
            synDataQryInfo = inMapper.map(synDataQuery, SyndicateDataQueryInfo.class);
            synDataQryInfo.setCreatedDate(synDataQuery.getCreatedDate());
            synDataQryInfo.setLastModifiedDate(synDataQuery.getLastModifiedDate());
        }
        return synDataQryInfo;
    }

}