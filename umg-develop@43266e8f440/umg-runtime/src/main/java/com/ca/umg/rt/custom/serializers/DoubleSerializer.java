package com.ca.umg.rt.custom.serializers;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class DoubleSerializer extends StdSerializer<Double> {

	private static final long serialVersionUID = 1L;
	
	protected DoubleSerializer(Class<Double> dblClass) {
		super(dblClass);
	}

	@Override
	public void serialize(Double value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
		String dblStringVal = BigDecimal.valueOf(value).toPlainString();
		BigDecimal bgDcl = BigDecimal.valueOf(value);
		if (!dblStringVal.contains(".")) {
			bgDcl = bgDcl.setScale(1);
		}
		jgen.writeNumber(bgDcl);
	}

}
