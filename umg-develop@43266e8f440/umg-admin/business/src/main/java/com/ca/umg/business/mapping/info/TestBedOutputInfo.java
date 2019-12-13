/**
 * 
 */
package com.ca.umg.business.mapping.info;

import java.util.List;

import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.integration.info.RuntimeResponse;
import com.ca.umg.report.model.ReportInfo;

/**
 * @author mahantat
 * 
 */
public class TestBedOutputInfo extends RuntimeResponse {

    private static final long serialVersionUID = 1L;

    private List<KeyValuePair<String, String>> outputData;

    private String outputJson;
    
    private ReportInfo reportInfo;
    
    public List<KeyValuePair<String, String>> getOutputData() {
        return outputData;
    }

    public void setOutputData(List<KeyValuePair<String, String>> outputData) {
        this.outputData = outputData;
    }

    public String getOutputJson() {
        return outputJson;
    }

    public void setOutputJson(String outputJson) {
        this.outputJson = outputJson;
    }
    
    public ReportInfo getReportInfo() {
    	return reportInfo;
    }
    
    public void setReportInfo(final ReportInfo reportInfo) {
    	this.reportInfo = reportInfo;
    }
}