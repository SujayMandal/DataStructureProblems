package com.ca.framework.core.custom.serializer;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;

public class DoubleSerializerModuleCodehaus extends SimpleModule {
	
	private static final String UMG_DOUBLE_SERIALIZER = "UMG-Double-Serializer";
	
	public final static Version VERSION = new Version(1, 0, 0, UMG_DOUBLE_SERIALIZER);
	
	public DoubleSerializerModuleCodehaus(String name, Version version) {
		super(UMG_DOUBLE_SERIALIZER, VERSION);
		addSerializer(Double.class, new DoubleSerializerCodeHaus(Double.class));
	}

}
