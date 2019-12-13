package com.ca.umg.rt.transformer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.util.ModelLanguages;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.util.MessageVariables;
import com.ca.umg.rt.validator.DataTypes;

/**
 * @author basanaga
 * 
 *         This class used to check the datatype is valid or not
 *
 */
public final class ModelResponseUtil {

    public static final String ERRORMESSAGE = "Expected %s for %s but received %s from model output.";
    
    public static final String PRECISION_ERRORMESSAGE = "Expected precision %s for %s but received precision %s from model output.";


    private ModelResponseUtil() {

    }

    public static void dataTypeDouble(List returnList, Object object, StringBuilder sb, String flattendName,Integer fractionDigits) {
        if (object instanceof Double || object instanceof Integer) {
        	if(fractionDigits != null && fractionDigits>0 && StringUtils.length(StringUtils.substringAfter(String.valueOf(object), ".")) > fractionDigits) {
        		sb.append(String.format(PRECISION_ERRORMESSAGE, fractionDigits,
                        flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, "."), StringUtils.length(StringUtils.substringAfter(String.valueOf(object), "."))));
        	}else {
                returnList.add(object);
        	}
        }
        else {
            sb.append(String.format(ERRORMESSAGE, DataTypes.DOUBLE,
                    flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, "."), object.getClass().getName()));

        }
    }

    public static void dataTypeInteger(List returnList, Object object, StringBuilder sb, String flattendName, String modelEnv) {
        if (StringUtils.equalsIgnoreCase(ModelLanguages.R.getLanguage(), modelEnv)
                && (object instanceof Integer || object instanceof Double)) {
            if (object instanceof Double) {
                returnList.add(((Double) object).intValue());
            } else {
                returnList.add(object);
            }
        } else if (!StringUtils.equalsIgnoreCase(ModelLanguages.R.getLanguage(), modelEnv) && object instanceof Integer) {
            returnList.add(object);
        } else {
            sb.append(String.format(ERRORMESSAGE, DataTypes.INTEGER,
                    flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, "."), object.getClass().getName()));
        }
    }

    public static void dataTypeLong(List returnList, Object object, StringBuilder sb, String flattendName) {
        if (object instanceof Integer || object instanceof Long) {
            returnList.add(object);
        } else {
            sb.append(String.format(ERRORMESSAGE, DataTypes.LONG,
                    flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, "."), object.getClass().getName()));
        }
    }

    public static void dataTypeBigInteger(List returnList, Object object, StringBuilder sb, String flattendName) {
        if (object instanceof Integer || object instanceof Long || object instanceof BigInteger) {
            returnList.add(object);
        } else {
            sb.append(String.format(ERRORMESSAGE, DataTypes.BIGINTEGER,
                    flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, "."), object.getClass().getName()));

        }
    }

    public static void dataTypeBigDecimal(List returnList, Object object, StringBuilder sb, String flattendName,Integer fractionDigits) {
        if (object instanceof Integer || object instanceof Long || object instanceof BigInteger || object instanceof Double
                || object instanceof BigDecimal) {
        	if(fractionDigits != null && fractionDigits>0 && StringUtils.length(StringUtils.substringAfter(String.valueOf(object), ".")) > fractionDigits) {
        		sb.append(String.format(PRECISION_ERRORMESSAGE, fractionDigits,
                        flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, "."), StringUtils.length(StringUtils.substringAfter(String.valueOf(object), "."))));
        	}else {
                returnList.add(object);
        	}
        } 
        else {
            sb.append(String.format(ERRORMESSAGE, DataTypes.BIGDECIMAL,
                    flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, "."), object.getClass().getName()));

        }
    }

    public static void dataTypeString(List returnList, Object object, StringBuilder sb, String flattendName) {
        if (object instanceof String) {
            returnList.add(object);
        } else {
            sb.append(String.format(ERRORMESSAGE, DataTypes.STRING,
                    flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, "."), object.getClass().getName()));
        }
    }

    public static void dataTypeBoolean(List returnList, Object object, StringBuilder sb, String flattendName) {
        if (object instanceof Boolean) {
            returnList.add(object);
        } else {
            sb.append(String.format(ERRORMESSAGE, DataTypes.BOOLEAN,
                    flattendName.replaceAll(RuntimeConstants.REGREX_CHAR_SLASH, "."), object.getClass().getName()));
        }
    }

    public static List<String> setAddOnValidation(Map<String, Object> payload, CacheRegistry cacheRegistry) {
        Map<String, TenantInfo> tenantMap = cacheRegistry.getMap(FrameworkConstant.TENANT_MAP);
        TenantInfo tenantInfo = tenantMap.get(RequestContext.getRequestContext().getTenantCode());
        Map<String, String> tenantConfigsMap = tenantInfo.getTenantConfigsMap();
        Map<String, Object> tenantReqHeader = (Map<String, Object>) payload.get(MessageVariables.TENANT_REQUEST_HEADER);
        List<String> addOnValidation = null;

        if ((tenantReqHeader != null && tenantReqHeader.get(FrameworkConstant.ADD_ON_VALIDATION) != null
                && ((List<String>) tenantReqHeader.get(FrameworkConstant.ADD_ON_VALIDATION))
                        .contains(FrameworkConstant.ACCEPTABLE_VALUES))
                || Boolean.valueOf(tenantConfigsMap.get(FrameworkConstant.ACCEPTABLE_VALUES))) {
            addOnValidation = new ArrayList<String>();
            addOnValidation.add(FrameworkConstant.ACCEPTABLE_VALUES);

        }
        if ((tenantReqHeader != null && tenantReqHeader.get(FrameworkConstant.ADD_ON_VALIDATION) != null
                && ((List<String>) tenantReqHeader.get(FrameworkConstant.ADD_ON_VALIDATION))
                        .contains(FrameworkConstant.MODEL_OUTPUT))
                || Boolean.valueOf(tenantConfigsMap.get(FrameworkConstant.MODELOUTPUT_VALIDATION))) {
            if (addOnValidation == null) {
                addOnValidation = new ArrayList<String>();
            }
            addOnValidation.add(FrameworkConstant.MODEL_OUTPUT);
        }
        return addOnValidation;
    }

}