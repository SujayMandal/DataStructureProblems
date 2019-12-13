package com.ca.umg.business.syndicatedata.bo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.syndicatedata.dao.SyndicateDataQueryDAO;
import com.ca.umg.business.syndicatedata.dao.SyndicateDataQueryInputDAO;
import com.ca.umg.business.syndicatedata.dao.SyndicateDataQueryOutputDAO;
import com.ca.umg.business.syndicatedata.entity.SyndicateDataQuery;

@Named
public class QueryMappingIntegrationImpl implements QueryMappingIntegration {

    @Inject
    private SyndicateDataQueryDAO dataQueryDAO;

    @Inject
    private SyndicateDataQueryInputDAO dataQueryInputDAO;

    @Inject
    private SyndicateDataQueryOutputDAO dataQueryOutputDAO;

    @Override
    public void copyQueries(String fromMappingId, String toMappingId, List<TidParamInfo> copiedSqlInfos) throws SystemException,
            BusinessException {
        List<SyndicateDataQuery> tosyndicateDataQueries = dataQueryDAO.findByMappingId(toMappingId);
        for (SyndicateDataQuery savedSynDataQuery : tosyndicateDataQueries) {
            dataQueryInputDAO.delete(savedSynDataQuery.getInputParameters());
            dataQueryOutputDAO.delete(savedSynDataQuery.getOutputParameters());
            dataQueryDAO.delete(savedSynDataQuery);
            dataQueryInputDAO.flush();
            dataQueryOutputDAO.flush();
            dataQueryDAO.flush();

        }

        List<SyndicateDataQuery> fromsyndicateDataQueries = dataQueryDAO.findByMappingId(fromMappingId);
        Mapping mapping = new Mapping();
        List<SyndicateDataQuery> copiedSyndicateDataQueries = new ArrayList<>();
        SyndicateDataQuery newQuery = null;
        if (CollectionUtils.isNotEmpty(fromsyndicateDataQueries)) {
            for (SyndicateDataQuery query : fromsyndicateDataQueries) {
                if (checkIfCopiedSqlHasQuery(query, copiedSqlInfos)) {
                    newQuery = query.copyAsNonEntity();
                    mapping.setId(toMappingId);
                    newQuery.setMapping(mapping);
                    copiedSyndicateDataQueries.add(newQuery);
                }
            }
        }
        dataQueryDAO.save(copiedSyndicateDataQueries);

    }

    private Boolean checkIfCopiedSqlHasQuery(SyndicateDataQuery query, List<TidParamInfo> copiedSqlInfos) {
        Boolean queryCanBeCopied = Boolean.FALSE;
        if (CollectionUtils.isNotEmpty(copiedSqlInfos)) {
            for (TidParamInfo tidParamInfo : copiedSqlInfos) {
                if (StringUtils.equals(query.getName(), tidParamInfo.getApiName())) {
                    queryCanBeCopied = Boolean.TRUE;
                }
            }
        }
        return queryCanBeCopied;
    }

}
