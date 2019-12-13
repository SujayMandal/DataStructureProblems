package com.ca.framework.core.custom.serializer;

import java.io.IOException;
import java.math.BigDecimal;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.NonTypedScalarSerializerBase;

public class DoubleSerializerCodeHaus extends NonTypedScalarSerializerBase<Double> {

	protected DoubleSerializerCodeHaus(Class<Double> dblClass) {
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
