package com.ca.umg.rt.custom.serializers;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class DoubleSerializerModule extends SimpleModule {
	
	private static final long serialVersionUID = 1L;

    private static String name = "DoubleSerializerModule";

	public DoubleSerializerModule() {
		super(name);
		addSerializer(Double.class, new DoubleSerializer(Double.class));
	}

}
