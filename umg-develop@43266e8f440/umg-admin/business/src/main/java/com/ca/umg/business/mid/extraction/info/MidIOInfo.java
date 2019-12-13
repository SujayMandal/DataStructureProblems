/**
 * 
 */
package com.ca.umg.business.mid.extraction.info;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This object holds the IO definition for the given MID data.
 * 
 * @author chandrsa
 */
public class MidIOInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Map<String, String> metadata;
    private List<MidParamInfo> midInput;
    private List<MidParamInfo> midOutput;

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public List<MidParamInfo> getMidInput() {
        return midInput;
    }

    public void setMidInput(List<MidParamInfo> modelInput) {
        this.midInput = modelInput;
    }

    public List<MidParamInfo> getMidOutput() {
        return midOutput;
    }

    public void setMidOutput(List<MidParamInfo> modelOutput) {
        this.midOutput = modelOutput;
    }
}
