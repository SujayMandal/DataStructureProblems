package com.ca.umg.business.mapping.info;

import java.util.Comparator;
import java.util.List;

public class MappingHierarchyInfo {
    private String modelName;
    private List<MappingInfo> mappingInfos;

    /**
     * sort ascending based on model name 
     */
    public static final Comparator<MappingHierarchyInfo> MODEL_ASCENDING = new Comparator<MappingHierarchyInfo>() {
		@Override
		public int compare(MappingHierarchyInfo o1, MappingHierarchyInfo o2){
			return o1.getModelName().compareToIgnoreCase(o2.getModelName())>0?1:-1;
		}
    };
    /**
     * sort descending based on model name 
     */
    public static final Comparator<MappingHierarchyInfo> MODEL_DESCENDING = new Comparator<MappingHierarchyInfo>() {
		@Override
		public int compare(MappingHierarchyInfo o1, MappingHierarchyInfo o2){
			return o1.getModelName().compareToIgnoreCase(o2.getModelName())>0?-1:1;
		}
    };
    
    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public List<MappingInfo> getMappingInfos() {
        return mappingInfos;
    }

    public void setMappingInfos(List<MappingInfo> mappingInfos) {
        this.mappingInfos = mappingInfos;
    }
    
    

    
}
