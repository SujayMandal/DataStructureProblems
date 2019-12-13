/**
 * 
 */
package com.ca.umg.business.mid.extraction.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

/**
 * @author chandrsa
 * 
 */
public class TidIOInfo implements Serializable {// NO PMD

    private static final long serialVersionUID = 1L;
    private Map<String, String> metadata;
    private List<TidParamInfo> tidInput;
    private List<TidParamInfo> tidOutput;
    private List<TidParamInfo> tidSystemInput;
    private List<TidSqlInfo> sqlInfos;
    private List<TidExpressionInfo> expressionInfos;

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public List<TidParamInfo> getTidInput() {
        return tidInput;
    }

    public void setTidInput(List<TidParamInfo> tidInput) {
        this.tidInput = tidInput;
    }

    public List<TidParamInfo> getTidOutput() {
        return tidOutput;
    }

    public void setTidOutput(List<TidParamInfo> tidOutput) {
        this.tidOutput = tidOutput;
    }

    public List<TidSqlInfo> getSqlInfos() {
        return sqlInfos;
    }

    public void setSqlInfos(List<TidSqlInfo> sqlInfos) {
        this.sqlInfos = sqlInfos;
    }

    public List<TidExpressionInfo> getExpressionInfos() {
        return expressionInfos;
    }

    public void setExpressionInfos(List<TidExpressionInfo> expressionInfos) {
        this.expressionInfos = expressionInfos;
    }

    public List<TidParamInfo> getTidSystemInput() {
        return tidSystemInput;
    }

    public void setTidSystemInput(List<TidParamInfo> tidSystemInput) {
        this.tidSystemInput = tidSystemInput;
    }

    public TidIOInfo copy(MidIOInfo midIOInfo) {
        TidParamInfo tidParamInfo = null;
        if (midIOInfo != null) {
            if (CollectionUtils.isNotEmpty(midIOInfo.getMidInput())) {
                tidInput = new ArrayList<>();
                for (MidParamInfo midParamInfo : midIOInfo.getMidInput()) {
                    tidParamInfo = new TidParamInfo();
                    tidParamInfo.copy(midParamInfo);
                    tidParamInfo.setMapped(true);
                    tidInput.add(tidParamInfo);
                }
            }

            if (CollectionUtils.isNotEmpty(midIOInfo.getMidOutput())) {
                tidOutput = new ArrayList<>();
                for (MidParamInfo midParamInfo : midIOInfo.getMidOutput()) {
                    tidParamInfo = new TidParamInfo();
                    tidParamInfo.copy(midParamInfo);
                    tidOutput.add(tidParamInfo);
                }
            }
        }
        this.setMetadata(midIOInfo.getMetadata());
        return this;
    }

    public TidIOInfo copy(TidIOInfo tidIOInfo) {
        if (tidIOInfo != null) {
            copyMetaData(tidIOInfo);
            copyOutIn(tidIOInfo);
            this.setSqlInfos(tidIOInfo.getSqlInfos() != null ? new ArrayList<TidSqlInfo>(tidIOInfo.getSqlInfos()) : null);
            this.setExpressionInfos(tidIOInfo.getExpressionInfos() != null ? new ArrayList<TidExpressionInfo>(tidIOInfo
                    .getExpressionInfos()) : null);
        }
        return this;
    }

    protected void copyOutIn(TidIOInfo tidIOInfo) {
        this.setTidOutput(tidIOInfo.getTidOutput() != null ? new ArrayList<TidParamInfo>(tidIOInfo.getTidOutput()) : null);
        this.setTidSystemInput(tidIOInfo.getTidSystemInput() != null ? new ArrayList<TidParamInfo>(tidIOInfo.getTidSystemInput())
                : null);
    }

    protected void copyMetaData(TidIOInfo tidIOInfo) {
        this.setMetadata(tidIOInfo.getMetadata());
        this.setTidInput(tidIOInfo.getTidInput() != null ? new ArrayList<TidParamInfo>(tidIOInfo.getTidInput()) : null);
    }

}