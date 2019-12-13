package com.ca.umg.modelet.transport.handler;

@SuppressWarnings("PMD")
public enum FieldInfoEnum {

	FIELD_NAME("modelParameterName", 0), //
	SEQUENCE("sequence", 1), //
	DATA_TYPE("dataType", 2), //
	NATIVE_DATA_TYPE("nativeDataType", 3), //
	COLLECTION("collection", 4), //
	VALUE("value", 5);
	
	private final String name;
	private final int index;
	
	private FieldInfoEnum (final String name, final int index) {
		this.name = name;
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}
	
	public static FieldInfoEnum getFieldInfoEnum(final String name) {
		FieldInfoEnum field = null;
		final FieldInfoEnum values[] = FieldInfoEnum.values();
		for (FieldInfoEnum value : values) {
			if (value.getName().equals(name)) {
				field = value;
				break;
			}
		}
		
		if (field == null) {
			throw new IllegalArgumentException("Field not filed, please add this new field or check whether name is full or not, Name is : " + name);
		}
		
		return field;
	}
}