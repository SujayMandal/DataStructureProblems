package com.ca.modelet.util;

import java.util.HashMap;
import java.util.Map;

public class FieldInfo {

	private String fieldName;
	private String sequence;
	private String dataType;
	private boolean collection;
	private Object value;
	private String nativeDataType;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(final String fieldName) {
		this.fieldName = fieldName;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(final String sequence) {
		this.sequence = sequence;
	}

	public String getNativeDataType() {
		return nativeDataType;
	}

	public void setNativeDataType(final String nativeDataType) {
		this.nativeDataType = nativeDataType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(final String dataType) {
		this.dataType = dataType;
	}

	public boolean isCollection() {
		return collection;
	}

	public void setCollection(final boolean collection) {
		this.collection = collection;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(final Object value) {
		this.value = value;
	}

	/**
	 * Empty Constructor
	 */
	public FieldInfo() {
		// Empty Constructor
	}

	public FieldInfo(final Object obj) {
		if (obj instanceof FieldInfo) {
			FieldInfo fi = (FieldInfo) obj;
			collection = fi.collection;
			dataType = fi.dataType;
			fieldName = fi.fieldName;
			nativeDataType = fi.nativeDataType;
			sequence = fi.sequence;
			value = fi.value;

		} else if (obj instanceof Map<?, ?>) {
			HashMap<String, Object> hm = (HashMap<String, Object>) obj;
			collection = Boolean.valueOf(hm.get("collection").toString());
			dataType = hm.get("dataType").toString();
			fieldName = hm.get("fieldName").toString();
			nativeDataType = hm.get("nativeDataType") != null ? hm.get("nativeDataType").toString() : null;
			sequence = hm.get("sequence").toString();
			value = hm.get("value");
		} else {
			collection = false;
			value = obj;

			if (obj instanceof Integer) {
				dataType = "integer";
			} else if (obj instanceof String) {
				dataType = "string";
			} else if (obj instanceof Boolean) {
				dataType = "boolean";
			} else if (obj instanceof Double) {
				dataType = "double";
			}
		}
	}
}
