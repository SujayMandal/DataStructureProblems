/**
 * 
 */
package com.ca.umg.business.mapping.info;

import java.io.Serializable;
import java.util.Map;

import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;

/**
 * @author raddibas
 * 
 */
public class QueryLaunchInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String tidName;
    private String type;
    private Map<String, TidParamInfo> tidInput;
    private Map<String, MidParamInfo> midOutput;

    public String getTidName() {
        return tidName;
    }

    public void setTidName(String tidName) {
        this.tidName = tidName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, TidParamInfo> getTidInput() {
        return tidInput;
    }

    public void setTidInput(Map<String, TidParamInfo> tidInput) {
        this.tidInput = tidInput;
    }

    public Map<String, MidParamInfo> getMidOutput() {
        return midOutput;
    }

    public void setMidOutput(Map<String, MidParamInfo> midOutput) {
        this.midOutput = midOutput;
    }
}