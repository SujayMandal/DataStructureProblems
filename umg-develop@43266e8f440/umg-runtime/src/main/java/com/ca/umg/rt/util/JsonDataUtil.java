/**
 * 
 */
package com.ca.umg.rt.util;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.omg.CORBA.SystemException;

import com.ca.framework.core.custom.serializer.DoubleSerializerModuleCodehaus;

/**
 * @author basanaga
 * 
 */
public final class JsonDataUtil {

    private JsonDataUtil() {
    }

    /**
     * Converts the given JSON string to the object of a given class.
     * 
     * @param jsonString
     * @param clazz
     * @return
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @throws SystemException
     */
    public static <T> T convertJson(String jsonString, Class<T> clazz) throws JsonParseException, JsonMappingException,
            IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new DoubleSerializerModuleCodehaus(null, null));
        T resultObject = null;
        resultObject = objectMapper.readValue(jsonString, clazz);
        return resultObject;

    }

    /**
     * Converts object into JSON string representation.
     * 
     * @param data
     * @return
     * @throws SystemException
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static <T> String convertToJsonString(T data) throws SystemException, JsonGenerationException, JsonMappingException,
            IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new DoubleSerializerModuleCodehaus(null, null));
        String jsonStr = null;
        if (data != null) {
            jsonStr = objectMapper.writeValueAsString(data);
        }
        return jsonStr;

    }
    
    /**
     * Converts object into JSON string representation.
     * 
     * @param data
     * @return
     * @throws SystemException
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static <T> String convertToJsonStringPrettyPrint(T data) throws SystemException, JsonGenerationException, JsonMappingException,
            IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new DoubleSerializerModuleCodehaus(null, null));
        String jsonStr = null;
        if (data != null) {
            jsonStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        }
        return jsonStr;

    }
}
