package com.ca.framework.core.ioreduce;

@SuppressWarnings("PMD")
public enum DataTypeValueEnum {

	OBJECT("object", 1), //
	INTEGER("integer", 2), //
	DOUBLE("double", 3), //
	STRING("string", 4), //
	DATE("date", 5), //
	BOOLEAN("boolean", 6);
	
	private final String strValue;
	private final int intValue;
	
	private DataTypeValueEnum(final String strValue, final int intValue) {
		this.strValue = strValue;
		this.intValue = intValue;
	}

	public String getStrValue() {
		return strValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public static DataTypeValueEnum getDataTypeValueEnum(final Object value) {
		DataTypeValueEnum vv = null;
		final DataTypeValueEnum values[] = DataTypeValueEnum.values();
		
		if (value instanceof String) {
			for (final DataTypeValueEnum v : values) {
				if (v.getStrValue().equalsIgnoreCase(value.toString()) || String.valueOf(v.getIntValue()).equals(value.toString())) {
					vv = v;
					break;
				}
			}
		} else if (value instanceof Integer) {
			for (final DataTypeValueEnum v : values) {
				if (v.getIntValue() == Integer.valueOf(value.toString()).intValue()) {
					vv = v;
					break;
				}
			}
		}
		
		if (vv == null) {
			throw new IllegalArgumentException("Data type is not found, passed data type is : " + value);
		}
		
		return vv;
	}
	
	public static DataTypeValueEnum getDataTypeValueEnum(final int value) {
		DataTypeValueEnum vv = null;
		final DataTypeValueEnum values[] = DataTypeValueEnum.values();
		for (final DataTypeValueEnum v : values) {
			if (v.getIntValue() == value) {
				vv = v;
				break;
			}
		}
		
		if (vv == null) {
			throw new IllegalArgumentException("Data type is not found, passed data type is : " + value);
		}
		
		return vv;
	}
}
