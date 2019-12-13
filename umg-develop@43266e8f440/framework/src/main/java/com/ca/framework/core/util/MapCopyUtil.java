/**
 * 
 */
package com.ca.framework.core.util;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author kamathan
 *
 */
public final class MapCopyUtil implements Serializable {

    private static final long serialVersionUID = -8941636612671828969L;

    private MapCopyUtil() {

    }

    public static Map<String, Object> deepCopy(Map<String, Object> request) {
        Map<String, Object> response = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : request.entrySet()) {
            if (entry.getValue() instanceof Map) {
                response.put(entry.getKey(), deepCopy((Map) entry.getValue()));
            } else {
                response.put(entry.getKey(), entry.getValue());
            }
        }
        return response;
    }

}
