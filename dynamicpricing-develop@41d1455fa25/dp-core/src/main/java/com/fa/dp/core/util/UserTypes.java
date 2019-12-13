/**
 * 
 */
package com.fa.dp.core.util;

public enum UserTypes {

	STATIC("STATIC"),

	DYNAMIC("DYNAMIC");

	private String type;

	private UserTypes(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
