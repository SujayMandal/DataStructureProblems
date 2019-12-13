package com.ca.umg.me2;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by repvenk on 5/24/2016.
 */
public class Sample {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Sample.class);

    public static void main(String[] args) {
        BigDecimal bigDecimal = new BigDecimal(12345678901234567890.0);
        BigInteger bi = new BigInteger("12345678912345678912");
        System.out.println(bigDecimal.toPlainString());
        System.out.println(bi.toString());
        System.out.println("Hello World!!!!");

        ObjectMapper mapper = new ObjectMapper();
        /*mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);*/
        System.out.println(Long.MAX_VALUE);
        long d = 1234567890123456789l;
        long c = 2147483648l;


        try {
            Map map = mapper.readValue("{\"key\":123456789012}".getBytes(), Map.class);
            System.out.println(map.toString());
        } catch (IOException e) {
        	LOGGER.error("IOException: {}",e);
        }
    }

}
