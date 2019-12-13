/**
 * 
 */
package com.ca.umg.rt.custom.serializers;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author chandrsa
 *
 */
public class FasterDoubleSerializer extends JsonSerializer<Double>{

    public FasterDoubleSerializer() {//NOPMD
        super();
    }

    @Override
    public void serialize(Double value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
            JsonProcessingException {
        if (null == value) {
            //write the word 'null' if there's no value available
            jgen.writeNull();
        } else {
            /*final String pattern = ".##";
            //final String pattern = "###,###,##0.00";
            final DecimalFormat myFormatter = new DecimalFormat(pattern);
            final String dblStringVal = myFormatter.format(value);*/
            String dblStringVal = BigDecimal.valueOf(value).toPlainString();
            jgen.writeNumber(dblStringVal);
        }
    }
    
    @Override
    public Class<Double> handledType() { 
        return Double.class; 
    }

}
