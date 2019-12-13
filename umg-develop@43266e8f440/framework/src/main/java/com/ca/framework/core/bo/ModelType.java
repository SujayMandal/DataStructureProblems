package com.ca.framework.core.bo;

public enum ModelType {

	ONLINE("Online"), //
	BULK("Bulk"),//
	ALL("All"),
	BATCH("Batch");
	private final String type;
	
	private ModelType (final String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	public static ModelType getModelType(final String type) {
		ModelType mt;
		if (ONLINE.getType().equalsIgnoreCase(type)) {
			mt = ONLINE;
		} else if (BULK.getType().equalsIgnoreCase(type)) {
			mt = BULK;
		} else if (BATCH.getType().equalsIgnoreCase(type)) {
			mt = BATCH;
		}else {
			mt = ALL;
		}
		
		return mt;
	}
}
