/**
 * 
 */
package com.ca.umg.business.tid.copy;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mapping.info.MappingDescriptor;
import com.ca.umg.business.mid.extraction.info.MidIOInfo;
import com.ca.umg.business.mid.extraction.info.TidSqlInfo;

/**
 * @author chandrsa
 * 
 */
public interface TidCopy {

    /**
     * @param midIOInfo
     * @param mappingDescriptor
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    MappingDescriptor copyTid(MidIOInfo midIOInfo, MappingDescriptor mappingDescriptor, List<TidSqlInfo> sqlInfos)
            throws BusinessException, SystemException;
}
