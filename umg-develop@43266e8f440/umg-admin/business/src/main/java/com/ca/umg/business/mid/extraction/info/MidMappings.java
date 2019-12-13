/**
 * 
 */
package com.ca.umg.business.mid.extraction.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;

/**
 * This object holds all mapping done for the MID
 * 
 * @author chandrsa
 */
public class MidMappings implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<MidMapping> midInputMappings;
    private List<MidMapping> midOutputMappings;

    public List<MidMapping> getMidInputMappings() {
        return midInputMappings;
    }

    public void setMidInputMappings(List<MidMapping> midInputMappings) {
        this.midInputMappings = midInputMappings;
    }

    public List<MidMapping> getMidOutputMappings() {
        return midOutputMappings;
    }

    public void setMidOutputMappings(List<MidMapping> midOutputMappings) {
        this.midOutputMappings = midOutputMappings;
    }

    public MidMappings prepare(MappingViews mappingViews) {
        if (mappingViews != null) {
            this.setMidInputMappings(getMidMappings(mappingViews.getInputMappingViews()));
            this.setMidOutputMappings(getMidMappings(mappingViews.getOutputMappingViews()));
        }
        return this;
    }

    private List<MidMapping> getMidMappings(List<MappingViewInfo> mappingViewInfos) {
        List<MidMapping> mappings = null;
        Map<String, List<String>> tempMapping = null;
        List<String> inputs = null;
        MidMapping mapping = null;
        if (CollectionUtils.isNotEmpty(mappingViewInfos)) {
            tempMapping = new HashMap<String, List<String>>();
            mappings = new ArrayList<>();
            for (MappingViewInfo mappingViewInfo : mappingViewInfos) {
                if (tempMapping.containsKey(mappingViewInfo.getMappedTo())) {
                    tempMapping.get(mappingViewInfo.getMappedTo()).add(mappingViewInfo.getMappingParam());
                } else {
                    inputs = new ArrayList<String>();
                    inputs.add(mappingViewInfo.getMappingParam());
                    tempMapping.put(mappingViewInfo.getMappedTo(), inputs);
                }
            }
            for (Entry<String, List<String>> mapEntry : tempMapping.entrySet()) {
                mapping = new MidMapping();
                mapping.setInputs(mapEntry.getValue());
                mapping.setMappedTo(mapEntry.getKey());
                mappings.add(mapping);
            }
        }
        return mappings;
    }
}