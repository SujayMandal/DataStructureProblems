/**
 * 
 */
package com.ca.framework.core.util;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.custom.serializer.DoubleSerializerModuleCodehaus;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;

/**
 * @author kamathan
 * 
 */
public final class ConversionUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionUtil.class);

    private ConversionUtil() {

    }

    /**
     * Converts the given json string to the object to given class.
     * 
     * @param jsonString
     * @param clazz
     * @return
     * @throws SystemException
     */
    public static <T> T convertJson(String jsonString, Class<T> clazz) throws SystemException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        T resultObject = null;
        try {
        	LOGGER.info("Input jsonString is +++" + jsonString);
            resultObject = objectMapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            SystemException.newSystemException(FrameworkExceptionCodes.BSE000009,
                    new Object[] { String.format("An error occurred while converting json to %s.", clazz) });
        }
        return resultObject;
    }
    
    public static <T> T convertJson(String jsonString, TypeReference typeRef) throws SystemException {
        ObjectMapper objectMapper = new ObjectMapper();
        T resultObject = null;
        try {
            resultObject = objectMapper.readValue(jsonString, typeRef);
        } catch (IOException e) {
            SystemException.newSystemException(FrameworkExceptionCodes.BSE000009,
                    new Object[] { String.format("An error occurred while converting json to %s.", typeRef) });
        }
        return resultObject;
    }

    /**
     * Converts the given json string to the object to given class.
     * 
     * @param jsonString
     * @param clazz
     * @return
     * @throws SystemException
     */
    public static <T> T convertJson(byte[] jsonByteArray, Class<T> clazz) throws SystemException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,true);
        objectMapper.configure(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS, true);
//        objectMapper.registerModule(new DoubleSerializerModuleCodehaus(null, null));
        T resultObject = null;
        try {
            resultObject = objectMapper.readValue(jsonByteArray, clazz);
        } catch (IOException e) {
            SystemException.newSystemException(FrameworkExceptionCodes.BSE000009,
                    new Object[] { String.format("An error occurred while converting json to %s.", clazz) });
        }
        return resultObject;
    }

    /**
     * Converts the given json string to the object to given class.
     * 
     * @param jsonString
     * @param clazz
     * @return
     * @throws SystemException
     */
    public static <T, G> T convertJson(String jsonString, Class<T> clazz, Class<G> genericClass) throws SystemException {
        ObjectMapper objectMapper = new ObjectMapper();
        T resultObject = null;
        try {
            JavaType tidInputs = objectMapper.getTypeFactory().constructParametricType(clazz, genericClass);
            resultObject = objectMapper.readValue(jsonString, tidInputs);
        } catch (IOException e) {
            SystemException.newSystemException(FrameworkExceptionCodes.BSE000009, new Object[] { String.format(
                    "An error occurred while converting json to %s, with type %s.", clazz, genericClass) });
        }
        return resultObject;
    }

    /**
     * Converts the given json string to the object to given class.
     * 
     * @param jsonString
     * @param clazz
     * @return
     * @throws SystemException
     */
    public static <T, G> T convertJson(File jsonString, Class<T> clazz, Class<G> genericClass) throws SystemException {
        T resultObject = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JavaType tidInputs = objectMapper.getTypeFactory().constructParametricType(clazz, genericClass);
            resultObject = objectMapper.readValue(jsonString, tidInputs);
        } catch (IOException exp) {
            SystemException.newSystemException(FrameworkExceptionCodes.BSE000009, new Object[] { String.format(
                    "An error occurred while converting json to %s, with type %s.", clazz, genericClass) });
        }
        return resultObject;
    }

    /**
     * Converts the given json string to the object to given class.
     * 
     * @param jsonString
     * @param clazz
     * @return
     * @throws SystemException
     */
    public static <T, G> T convertJson(byte[] jsonString, Class<T> clazz, Class<G> genericClass) throws SystemException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationConfig.Feature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        T resultObject = null;
        try {
            JavaType tidInputs = objectMapper.getTypeFactory().constructParametricType(clazz, genericClass);
            resultObject = objectMapper.readValue(jsonString, tidInputs);
        } catch (IOException e) {
            SystemException.newSystemException(FrameworkExceptionCodes.BSE000009, new Object[] { String.format(
                    "An error occurred while converting json to %s, with type %s.", clazz, genericClass) });
        }
        return resultObject;
    }

    /**
     * This method would convert any object to JSON String
     * 
     * @param data
     * @return
     * @throws SystemException
     */
    public static <T> String convertToJsonString(T data) throws SystemException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = null;
        try {
            if (data != null) {
                jsonStr = objectMapper.writeValueAsString(data);
            }
        } catch (IOException e) {
            SystemException.newSystemException(FrameworkExceptionCodes.BSE000009,
                    new Object[] { String.format("An error occurred while converting %s to json string.", data.getClass()) });
        }
        return jsonStr;
    }
    
    public static byte[] convertToFormattedJsonStringByteArray(byte[] data) throws SystemException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new DoubleSerializerModuleCodehaus(null, null));
        String jsonStr = null;
        byte[] result = null;
        try {
            if (data != null && data.length > 0) {
                Object jsonObject = objectMapper.readValue(data, Object.class);
                jsonStr = objectMapper.writeValueAsString(jsonObject);
                result = jsonStr.getBytes();
            }
        } catch (IOException e) {
            LOGGER.error("Error while doing the Pretty Print for the data :" + data);
            result = data;
        }
        return result;
    }
}
