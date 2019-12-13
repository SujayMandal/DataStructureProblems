package com.ca.umg.business.tenant.report.model;

import com.google.common.base.Objects;

@SuppressWarnings("PMD")
public class TabularInfo {

	private String key;
	private String dataType;
	private String tenantValue;
	private String modelValue;

	public TabularInfo(final String key, final String dataType, final String tenantValue, final String modelValue) {
		this.key = key;
		this.dataType = dataType;
		this.tenantValue = tenantValue;
		this.modelValue = modelValue;
	}

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(final String dataType) {
		this.dataType = dataType;
	}

	public String getTenantValue() {
		return tenantValue;
	}

	public void setTenantValue(final String tenantValue) {
		this.tenantValue = tenantValue;
	}

	public String getModelValue() {
		return modelValue;
	}

	public void setModelValue(final String modelValue) {
		this.modelValue = modelValue;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(TabularInfo.class).add("Key", key).add("Data Type", dataType).add("Tenant Value", tenantValue)
				.add("Model Value", modelValue).toString();
	}
}