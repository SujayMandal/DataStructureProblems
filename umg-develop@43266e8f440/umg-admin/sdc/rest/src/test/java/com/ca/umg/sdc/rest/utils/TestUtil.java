/**
 * 
 */
package com.ca.umg.sdc.rest.utils;

import java.io.IOException;
import java.nio.charset.Charset;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.MediaType;

/**
 * @author nigampra
 * 
 */
public class TestUtil {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsBytes(object);
    }

}
