/**
 * 
 */
package com.fa.dp.core.rest.info;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import lombok.Data;

/**
 * @author
 * 
 */
@Data
public class TidParamInfo extends ParamInfo {

	private static final long serialVersionUID = 7628388599040007947L;

	private List<TidParamInfo> children;
	private Object value;
	private boolean sqlOutput;
	private boolean exprsnOutput;
	private String sqlId;
	private String expressionId;
	private boolean exposedToTenant;
	private int userExposedToTenant;

	/**
	 * This method would copy all parameters from MID parameter info. While copying
	 * though if the MID parameter is found to be syndicate the TID parameter is
	 * marked optional. The syndicate flag is never copied as it does not have a
	 * value on the TID side. UMG-621
	 * 
	 * @param midParamInfo
	 * @return
	 */
	public TidParamInfo copy(MidParamInfo midParamInfo) {
		TidParamInfo info = null;
		if (midParamInfo != null) {
			this.setDescription(midParamInfo.getDescription());
			/*
			 * if (midParamInfo.isSyndicate()) { this.setMandatory(false); } else {
			 * this.setMandatory(midParamInfo.isMandatory()); } As part of default mapping
			 * the syndicate mid will carry the mandatory flag as is.
			 */
			this.setMandatory(midParamInfo.isMandatory());// UMG-2706
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
