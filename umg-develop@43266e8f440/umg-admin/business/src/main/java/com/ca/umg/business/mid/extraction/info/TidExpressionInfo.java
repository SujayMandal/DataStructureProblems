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
public class TidExpressionInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String expressionName;
    private String expressionText;
    private List<TidParamInfo> inputParams;
    private List<TidParamInfo> outputParams;

    public String getExpressionName() {
        return expressionName;
    }

    public void setExpressionName(String expressionName) {
        this.expressionName = expressionName;
    }

    public String getExpressionText() {
        return expressionText;
    }

    public void setExpressionText(String expressionText) {
        this.expressionText = expressionText;
    }

    public List<TidParamInfo> getInputParams() {
        return inputParams;
    }

    public void setInputParams(List<TidParamInfo> inputParams) {
        this.inputParams = inputParams;
    }

    public List<TidParamInfo> getOutputParams() {
        return outputParams;
    }

    public void setOutputParams(List<TidParamInfo> outputParams) {
        this.outputParams = outputParams;
    }
}