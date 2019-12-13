package com.ca.framework.core.ioreduce;

@SuppressWarnings("PMD")
public enum NativeDataTypeValueEnum {

	MATRIX("matrix", 0), //
	OBJECT("object", 1), //
	INTEGER("integer", 2), //
	NUMERIC("numeric", 3), //
	CHARACTER("character", 4), //
	DATE("date", 5), //
	LOGICAL("logical", 6), //
	FACTOR("factor", 7), //
	VECTOR("vector", 8), //
	LIST("list", 9 ), //
	DATA_FRAME("data.frame", 10), //
	RAW("raw", 11), //
	ARRAY("array", 12), //
	COMPLEX("complex", 13), //
	DOUBLE("double", 14), //
	STRING("string", 15), //
	BOOLEAN("boolean", 16);
	
	private final String strValue;
	private final int intValue;
	
	private NativeDataTypeValueEnum(final String strValue, final int intValue) {
		this.strValue = strValue;
		this.intValue = intValue;
	}

	public String getStrValue() {
		return strValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public static NativeDataTypeValueEnum getNativeDataTypeValueEnum(final Object value) {
		if (value == null) {
			return null;
		}
		NativeDataTypeValueEnum vv = null;
		final NativeDataTypeValueEnum values[] = NativeDataTypeValueEnum.values();
		
		if (value instanceof String) {
			for (final NativeDataTypeValueEnum v : values) {
				if (v.getStrValue().equalsIgnoreCase(value.toString()) || String.valueOf(v.getIntValue()).equals(value.toString())) {
					vv = v;
					break;
				}
			}
		} else if (value instanceof Integer) {
			for (final NativeDataTypeValueEnum v : values) {
				if (v.getIntValue() == Integer.valueOf(value.toString()).intValue()) {
					vv = v;
					break;
				}
			}
		}
		
		if (vv == null) {
			throw new IllegalArgumentException("Native Data type is not found, passed data type is : " + value);
		}
		
		return vv;
	}
	
	public static NativeDataTypeValueEnum getNativeDataTypeValueEnum(final int value) {
		NativeDataTypeValueEnum vv = null;
		final NativeDataTypeValueEnum values[] = NativeDataTypeValueEnum.values();
		for (final NativeDataTypeValueEnum v : values) {
			if (v.getIntValue() == value) {
				vv = v;
				break;
			}
		}
		
		if (vv == null) {
			throw new IllegalArgumentException("Native Data type is not found, passed data type is : " + value);
		}
		
		return vv;
	}
}
