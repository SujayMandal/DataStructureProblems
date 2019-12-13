package com.ca.framework.core.ioreduce;

@SuppressWarnings("PMD")
public enum BooleanValueEnum {

	TRUE("true", 1, Boolean.TRUE), //
	FALSE("false", 0, Boolean.FALSE);
	
	private final String strValue;
	private final int intValue;
	private final boolean boolValue;
	
	private BooleanValueEnum(final String strValue, final int intValue, final boolean boolValue) {
		this.strValue = strValue;
		this.intValue = intValue;
		this.boolValue = boolValue;
	}

	public String getStrValue() {
		return strValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public boolean isBoolValue() {
		return boolValue;
	}

	public static BooleanValueEnum getBooleanValueEnum(final Object value) {
		if (value instanceof String) {
			if (TRUE.getStrValue().equalsIgnoreCase(value.toString())) {
				return TRUE;
			} else {
				return FALSE;
			}			
		} else if (value instanceof Integer) {
			if (TRUE.getIntValue() == Integer.valueOf(value.toString()).intValue()) {
				return TRUE;
			} else {
				return FALSE;
			}
		}
		
		throw new IllegalArgumentException("Boolean value is not fouond : value is : " + value);
	}
	
	public static BooleanValueEnum getBooleanValueEnum(final int value) {
		if (TRUE.getIntValue() == value) {
			return TRUE;
		} else {
			return FALSE;
		}
	}
}
