/**
 * 
 */
package com.ca.umg.business.mid.mapping;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mid.extraction.info.MappingViews;
import com.ca.umg.business.mid.extraction.info.MidIOInfo;

/**
 * @author chandrsa
 * 
 */
public interface MidMapper {

    MappingViews createMappingViews(MidIOInfo midIOInfo) throws BusinessException, SystemException;

    MappingViews createMappingViews(MidIOInfo midIOInfo, String type) throws BusinessException, SystemException;
}
