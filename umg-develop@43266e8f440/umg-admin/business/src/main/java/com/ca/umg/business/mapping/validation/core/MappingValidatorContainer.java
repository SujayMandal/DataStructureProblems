package com.ca.umg.business.mapping.validation.core;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.ca.umg.business.mid.extraction.info.MidMapping;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidExpressionInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidSqlInfo;

public class MappingValidatorContainer {
    private Map<String, MidParamInfo> midParamInfoMap;
    private Map<String, TidParamInfo> tidParamInfoMap;
    private Map<String, TidSqlInfo> tidSqlInfoMap;
    private Map<String, TidExpressionInfo> tidExpressionInfoMap;
    // Map of tid name and list of MidParamInfo where it is being used
    private Map<String, List<MidParamInfo>> tidMidParamInfoListMap;

    private Map<String, MidMapping> midMappingMap;

    private MidMapping midMapping;

    public Map<String, MidParamInfo> getMidParamInfoMap() {
        return midParamInfoMap;
    }

    public void setMidParamInfoMap(Map<String, MidParamInfo> midParamInfoMap) {
        this.midParamInfoMap = midParamInfoMap;
    }

    public Map<String, TidParamInfo> getTidParamInfoMap() {
        return tidParamInfoMap;
    }

    public void setTidParamInfoMap(Map<String, TidParamInfo> tidParamInfoMap) {
        this.tidParamInfoMap = tidParamInfoMap;
    }

    public Map<String, TidSqlInfo> getTidSqlInfoMap() {
        return tidSqlInfoMap;
    }

    public void setTidSqlInfoMap(Map<String, TidSqlInfo> tidSqlInfoMap) {
        this.tidSqlInfoMap = tidSqlInfoMap;
    }

    public Map<String, TidExpressionInfo> getTidExpressionInfoMap() {
        return tidExpressionInfoMap;
    }

    public void setTidExpressionInfoMap(Map<String, TidExpressionInfo> tidExpressionInfoMap) {
        this.tidExpressionInfoMap = tidExpressionInfoMap;
    }

    public MidMapping getMidMapping() {
        return midMapping;
    }

    public void setMidMapping(MidMapping midMapping) {
        this.midMapping = midMapping;
    }

    public Map<String, List<MidParamInfo>> getTidMidParamInfoListMap() {
        return tidMidParamInfoListMap;
    }

    public void setTidMidParamInfoListMap(Map<String, List<MidParamInfo>> tidMidParamInfoListMap) {
        this.tidMidParamInfoListMap = tidMidParamInfoListMap;
    }

    public Map<String, MidMapping> getMidMappingMap() {
        return midMappingMap;
    }

    public void setMidMappingMap(Map<String, MidMapping> midMappingMap) {
        this.midMappingMap = midMappingMap;
    }

	public boolean isMandatory(TidParamInfo tidParamInfo) {
		return tidParamInfoMap.get(tidParamInfo.getFlatenedName())
				.isMandatory();
	}
    public boolean isAssociatedWithMandatoryMidParam(TidParamInfo tidParamInfo) {
        boolean isMandatory = false;
        if (CollectionUtils.isNotEmpty(tidMidParamInfoListMap.get(tidParamInfo.getFlatenedName()))) {
            isMandatory = isTidAssociatedWithMandatoryMid(tidParamInfo);
        }
        return isMandatory;
    }

    private boolean isTidAssociatedWithMandatoryMid(TidParamInfo tidParamInfo) {
        boolean isMandatory = false;
        for (MidParamInfo midParamInfo : tidMidParamInfoListMap.get(tidParamInfo.getFlatenedName())) {
            if (midParamInfo.isMandatory()) {
                isMandatory = true;
                break;
            }
        }
        return isMandatory;
    }
    
    public boolean isExposedToTenant (TidParamInfo tidParamInfo) {
    	return tidParamInfoMap.get(tidParamInfo.getFlatenedName())
				.isExposedToTenant();
    }

}
