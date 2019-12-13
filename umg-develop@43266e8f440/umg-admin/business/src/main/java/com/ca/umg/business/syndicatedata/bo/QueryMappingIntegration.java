package com.ca.umg.business.syndicatedata.bo;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;

public interface QueryMappingIntegration {
    

    public void copyQueries(String fromMappingId, String toMappingId, List<TidParamInfo> copiedSqlInfos) throws SystemException,
            BusinessException;

}
