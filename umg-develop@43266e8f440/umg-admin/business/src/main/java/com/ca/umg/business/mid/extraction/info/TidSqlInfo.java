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
public class TidSqlInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String sqlName;
    private String sqlId;
    private List<TidParamInfo> inputParams;
    private List<TidParamInfo> outputParams;
    private DatatypeInfo datatypeInfo;

    public String getSqlName() {
        return sqlName;
    }

    public void setSqlName(String sqlName) {
        this.sqlName = sqlName;
    }

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
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

    public DatatypeInfo getDatatypeInfo() {
        return datatypeInfo;
    }

    public void setDatatypeInfo(DatatypeInfo datatypeInfo) {
        this.datatypeInfo = datatypeInfo;
    }
}
