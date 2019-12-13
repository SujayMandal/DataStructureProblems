/**
 * 
 */
package com.ca.umg.business.mid.extraction.info;

import java.io.Serializable;
import java.util.List;

/**
 * @author chandrsa
 * 
 */
public class MappingViews implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<MappingViewInfo> inputMappingViews;
    private List<MappingViewInfo> outputMappingViews;

    public List<MappingViewInfo> getInputMappingViews() {
        return inputMappingViews;
    }

    public void setInputMappingViews(List<MappingViewInfo> inputMappingViews) {
        this.inputMappingViews = inputMappingViews;
    }

    public List<MappingViewInfo> getOutputMappingViews() {
        return outputMappingViews;
    }

    public void setOutputMappingViews(List<MappingViewInfo> outputMappingViews) {
        this.outputMappingViews = outputMappingViews;
    }

}
