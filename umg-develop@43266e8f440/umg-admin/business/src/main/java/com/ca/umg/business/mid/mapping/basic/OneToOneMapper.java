/**
 * 
 */
package com.ca.umg.business.mid.mapping.basic;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.mid.extraction.info.MappingViewInfo;
import com.ca.umg.business.mid.extraction.info.MappingViews;
import com.ca.umg.business.mid.extraction.info.MidIOInfo;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.mapping.MidMapper;

/**
 * @author chandrsa
 * 
 */
@Named
public class OneToOneMapper implements MidMapper {

    @Override
    public MappingViews createMappingViews(MidIOInfo midIOInfo) throws BusinessException, SystemException {
        List<MappingViewInfo> mappings = null;
        MappingViews mappingViews = null;
        if (midIOInfo != null) {
            mappingViews = new MappingViews();
            if (CollectionUtils.isNotEmpty(midIOInfo.getMidInput())) {
                mappings = createMappingViews(midIOInfo.getMidInput());
                mappingViews.setInputMappingViews(mappings);
            }
            if (CollectionUtils.isNotEmpty(midIOInfo.getMidOutput())) {
                mappings = createMappingViews(midIOInfo.getMidOutput());
                mappingViews.setOutputMappingViews(mappings);
            }
        }
        return mappingViews;
    }

    public MappingViews createMappingViews(MidIOInfo midIOInfo, String type) throws BusinessException, SystemException {
        List<MappingViewInfo> mappings = null;
        final MappingViews mappingViews = new MappingViews();
        if (midIOInfo != null && StringUtils.isNotBlank(type)) {
            if (StringUtils.equalsIgnoreCase(type, BusinessConstants.TYPE_INPUT_MAPPING)
                    && CollectionUtils.isNotEmpty(midIOInfo.getMidInput())) {
                mappings = createMappingViews(midIOInfo.getMidInput());
                mappingViews.setInputMappingViews(mappings);
            } else if (StringUtils.equalsIgnoreCase(type, BusinessConstants.TYPE_OUTPUT_MAPPING)
                    && CollectionUtils.isNotEmpty(midIOInfo.getMidOutput())) {
                mappings = createMappingViews(midIOInfo.getMidOutput());
                mappingViews.setOutputMappingViews(mappings);
            }
        }
        return mappingViews;
    }

    private List<MappingViewInfo> createMappingViews(List<MidParamInfo> paramInfos) {
        List<MappingViewInfo> mappings = new ArrayList<>();
        MappingViewInfo midMapping = null;
        for (MidParamInfo paramInfo : paramInfos) {
            midMapping = new MappingViewInfo();
            midMapping.setMappedTo(paramInfo.getFlatenedName());
            midMapping.setMappingParam(paramInfo.getFlatenedName());
            mappings.add(midMapping);
        }
        return mappings;
    }

}
