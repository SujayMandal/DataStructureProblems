/**
 * 
 */
package com.ca.umg.business.mid.extraction.info;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * @author chandrsa
 * 
 */
public class TidParamInfo extends ParamInfo {

    private static final long serialVersionUID = 1L;
    private List<TidParamInfo> children;
    private Object value;
    private boolean sqlOutput;
    private boolean exprsnOutput;
    private String sqlId;
    private String expressionId;
    private boolean exposedToTenant;
    private int userExposedToTenant;
    
    public boolean isExposedToTenant() {
		return exposedToTenant;
	}

	public void setExposedToTenant(boolean exposeToTenant) {
		this.exposedToTenant = exposeToTenant;
	}

    public int getUserExposedToTenant() {
		return userExposedToTenant;
	}

	public void setUserExposedToTenant(int userExposedToTenant) {
		this.userExposedToTenant = userExposedToTenant;
	}

	public List<TidParamInfo> getChildren() {
        return children;
    }

    public void setChildren(List<TidParamInfo> children) {
        this.children = children;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isSqlOutput() {
        return sqlOutput;
    }

    public void setSqlOutput(boolean sqlOutput) {
        this.sqlOutput = sqlOutput;
    }

    public boolean isExprsnOutput() {
        return exprsnOutput;
    }

    public void setExprsnOutput(boolean exprsnOutput) {
        this.exprsnOutput = exprsnOutput;
    }

    public String getSqlId() {
        return sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    public String getExpressionId() {
        return expressionId;
    }

    public void setExpressionId(String expressionId) {
        this.expressionId = expressionId;
    }

    /**
     * This method would copy all parameters from MID parameter info. While copying though if the MID parameter is found to be
     * syndicate the TID parameter is marked optional. The syndicate flag is never copied as it does not have a value on the TID
     * side. UMG-621
     * 
     * @param midParamInfo
     * @return
     */
    public TidParamInfo copy(MidParamInfo midParamInfo) {
        TidParamInfo info = null;
        if (midParamInfo != null) {
            this.setDescription(midParamInfo.getDescription());
            /*if (midParamInfo.isSyndicate()) {
                this.setMandatory(false);
            } else {
                this.setMandatory(midParamInfo.isMandatory());
            }
            As part of default mapping the syndicate mid will carry the mandatory flag as is.
            */
            this.setMandatory(midParamInfo.isMandatory());//UMG-2706
            this.setName(midParamInfo.getName());
            this.setApiName(midParamInfo.getName() != null ? midParamInfo.getName() : midParamInfo.getApiName());
            this.setModelParamName(midParamInfo.getModelParamName());
            this.setFlatenedName(midParamInfo.getFlatenedName());
            this.setSequence(midParamInfo.getSequence());
            this.setDatatype(midParamInfo.getDatatype());
            this.setDataTypeStr(midParamInfo.getDataTypeStr());
            this.setAcceptableValueArr(midParamInfo.getAcceptableValueArr());
            if (CollectionUtils.isNotEmpty(midParamInfo.getChildren())) {
                this.children = new ArrayList<>();
                for (MidParamInfo childMidParam : midParamInfo.getChildren()) {
                    info = new TidParamInfo();
                    info.copy(childMidParam);
                    this.children.add(info);
                }
            }
        }
        return this;
    }
    
    @Override
    public String toString() {
    	final StringBuilder sb = new StringBuilder(128);
    	sb.append("value:").append(value);
    	sb.append("sqlOutput:").append(sqlOutput);
    	sb.append("exprsnOutput:").append(exprsnOutput);
    	sb.append("sqlId:").append(sqlId);
    	sb.append("expressionId:").append(expressionId);
    	sb.append("children");
    	for (TidParamInfo child : children) {
    		sb.append(child.toString());
    	}
    	return sb.toString();
    }
}
