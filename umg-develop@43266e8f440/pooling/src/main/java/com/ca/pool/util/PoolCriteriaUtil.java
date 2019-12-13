
package com.ca.pool.util;

import static com.ca.framework.core.exception.SystemException.newSystemException;
import com.ca.pool.model.ExecutionLanguage;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.pool.PoolCriteriasEnum;
import com.ca.pool.model.TransactionCriteria;

@SuppressWarnings("PMD")
public class PoolCriteriaUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoolCriteriaUtil.class);

    private static final String AND_OPERATOR = "&";

    private static final String EQUAL_OPERATOR = "=";

    public static String getReplacedPoolCriteria(String poolWithCrit, TransactionCriteria transactionCriteria)
            throws SystemException {
        String poolLowerCase = StringUtils.lowerCase(poolWithCrit);
        LOGGER.info("Pool Criteria to be replaced: PoolCriteriaUtil::getReplacedPoolCriteria " + poolWithCrit);
        String[] strArray = StringUtils.splitByWholeSeparator(poolLowerCase, AND_OPERATOR);
        StringBuilder sb = new StringBuilder(60);
        String str;
        for (int i = 0; i < strArray.length; i++) {
            str = strArray[i];
            String replacedComparison = getFormattedString(str, transactionCriteria);
            sb.append(replacedComparison);

            if (i < strArray.length - 1) {
                sb.append(" ").append("&and").append(" ");
            }
        }
        String replacedCriteria = sb.toString();
        LOGGER.info("Pool Criteria after replacement: PoolCriteriaUtil::getReplacedPoolCriteria " + replacedCriteria);
        return replacedCriteria;
    }

    public static String getFormattedString(String copmareStr, TransactionCriteria transactionCriteria) throws SystemException {
        String[] splitStr = StringUtils.splitByWholeSeparator(copmareStr, EQUAL_OPERATOR);
        StringBuilder sb = new StringBuilder(60);
        String str;
        for (int i = 0; i < splitStr.length; i++) {
            str = splitStr[i];
            if (StringUtils.contains(str, "#")) {
                String replaceStrPlaceHolder = StringUtils.substringBetween(str, "#", "#");
                String replacedString = getPlaceHolderValue(replaceStrPlaceHolder, transactionCriteria);
                sb.append("'").append(replacedString).append("'");
            } else {
                sb.append(" eq ").append("'").append(str.trim()).append("'");
            }
        }
        return sb.toString();
    }

    public static String getPlaceHolderValue(String replaceStrPlaceHolder, TransactionCriteria transactionCriteria)
            throws SystemException {
        String placeHolderValue = null;
        Field fld[] = transactionCriteria.getClass().getDeclaredFields();
        for (Field x : fld) {
            if (StringUtils.equalsIgnoreCase(PoolCriteriasEnum.getCriteriaFieldForNameInDb(replaceStrPlaceHolder), x.getName())) {
                x.setAccessible(true);
                try {
                    placeHolderValue = (String) x.get(transactionCriteria);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    LOGGER.error(
                            "Error occured while getting property for replaceStrPlaceHolder : {} in TransactionCriteria {} from "
                                    + "PoolCriteriaUtil::getPlaceHolderValue ",
                            replaceStrPlaceHolder, transactionCriteria.toString(), e);
                    throw newSystemException("MSE0000401",
                            new Object[] { replaceStrPlaceHolder, transactionCriteria.toString() });
                }
                break;
            }
        }
        if (placeHolderValue == null) {
            LOGGER.error("No property found for one criteria {} in TransactionCriteria PoolCriteriaUtil::getPlaceHolderValue ",
                    replaceStrPlaceHolder);
            throw newSystemException("MSE0000401", new Object[] { replaceStrPlaceHolder, transactionCriteria.toString() });
        }
        return placeHolderValue.toLowerCase();
    }

    public static String getReplacedPoolCriteriaForAny(String replacedPoolCrit) {
        LOGGER.info("Pool Criteria to be replaced for any or both: PoolCriteriaUtil::getReplacedPoolCriteriaForAny "
                + replacedPoolCrit);
        String[] splitStr1 = StringUtils.splitByWholeSeparator(replacedPoolCrit, "&and");
        StringBuilder sb = new StringBuilder(60);
        String str = null;
        for (int i = 0; i < splitStr1.length; i++) {
            str = splitStr1[i];
            if (StringUtils.contains(str, "any") || StringUtils.contains(str, "both")) {
                // sb.append(" ").append(true).append(" ");
                sb.append(true);
            } else {
                sb.append(str.trim());
            }

            if (i < splitStr1.length - 1) {
                sb.append(" and ");
            }
        }
        LOGGER.info("Pool Criteria after replacement for any or both: PoolCriteriaUtil::getReplacedPoolCriteriaForAny "
                + sb.toString());
        return sb.toString();
    }

    public static void getCriteraValues(final String criteria, final Map<String, String> criteriaValueMap) {
        if (criteria != null) {
            final String criteriaLowerCase = StringUtils.lowerCase(criteria);
            final String[] criteriaArray = StringUtils.splitByWholeSeparator(criteriaLowerCase, AND_OPERATOR);

            for (final String value : criteriaArray) {
                final String[] splitStr = StringUtils.splitByWholeSeparator(value, EQUAL_OPERATOR);
                criteriaValueMap.put(StringUtils.substringBetween(splitStr[0], "#", "#"), splitStr[1].trim());
            }
        }
    }

    public static void getCriteraValues1(final String criteria, final Map<String, String> criteriaValueMap) {
        if (criteria != null) {
            final String[] criteriaArray = StringUtils.splitByWholeSeparator(criteria, AND_OPERATOR);

            for (final String value : criteriaArray) {
                final String[] splitStr = StringUtils.splitByWholeSeparator(value, EQUAL_OPERATOR);
                criteriaValueMap.put(StringUtils.substringBetween(splitStr[0], "#", "#").toLowerCase(), splitStr[1].trim());
            }
        }
    }

    /**
     * gets the {@link TransactionCriteria} object for passed criteria value. this criteria value is POOL_CRITERIA_VALUE from the
     * pool_criteria_def_mapping table
     * 
     * @param criteria
     * @return
     * @throws SystemException
     */
    public static TransactionCriteria getCriteriaObject(String criteria) throws SystemException {
        TransactionCriteria transactionCriteria = new TransactionCriteria();
        final String criteriaLowerCase = StringUtils.lowerCase(criteria);
        final String[] criteriaArray = StringUtils.splitByWholeSeparator(criteriaLowerCase, AND_OPERATOR);

        for (final String value : criteriaArray) {
            final String[] splitStr = StringUtils.splitByWholeSeparator(value, EQUAL_OPERATOR);
            setPlaceHolderValue(StringUtils.substringBetween(splitStr[0], "#", "#"), splitStr[1].trim(), transactionCriteria);
        }

        return transactionCriteria;
    }

    public static void setPlaceHolderValue(String replaceStrPlaceHolder, String replaceStrValue,
            TransactionCriteria transactionCriteria) throws SystemException {
        Field fld[] = transactionCriteria.getClass().getDeclaredFields();
        for (Field x : fld) {
            if (StringUtils.equalsIgnoreCase(PoolCriteriasEnum.getCriteriaFieldForNameInDb(replaceStrPlaceHolder), x.getName())) {
                x.setAccessible(true);
                try {
                    x.set(transactionCriteria, replaceStrValue);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    LOGGER.error(
                            "Error occured while setting property for replaceStrPlaceHolder : {} in TransactionCriteria {} from "
                                    + "PoolCriteriaUtil::setPlaceHolderValue ",
                            replaceStrPlaceHolder, transactionCriteria.toString(), e);
                    throw newSystemException("MSE0000401",
                            new Object[] { replaceStrPlaceHolder, transactionCriteria.toString() });
                }
                break;
            }
        }
    }

    public static final String getModelNameWithVersion(final String modelName, final Integer majorVersion,
            final Integer minorVersion) {
        return modelName + PoolConstants.MODEL_SEPERATOR + majorVersion + PoolConstants.MODEL_VERION_SEPERATOR + minorVersion;
    }

    public static final String getEnvironmentWithVersion(final String environmentName, final String environmentVersion) {
        return environmentName + PoolConstants.ENV_SEPERATOR + environmentVersion;
    }

    public static final String getModelNameWithVersion(final String modelName, final String modelVersion) {
        return modelName + PoolConstants.MODEL_SEPERATOR + modelVersion;
    }

    public static boolean isMatlab(final String environment) {
        if (environment != null) {
            return StringUtils.equalsIgnoreCase(environment,  ExecutionLanguage.MATLAB.getValue());
        } else {
            return false;
        }
    }

    public static boolean isR(final String environment) {
    	if (environment != null) {
            return StringUtils.equalsIgnoreCase(environment,  ExecutionLanguage.R.getValue());
        } else {
            return false;
        }
    }
    
    public static boolean isExcel(final String environment) {
    	if (environment != null) {
            return StringUtils.equalsIgnoreCase(environment,  ExecutionLanguage.EXCEL.getValue());
        } else {
            return false;
        }
    }
    
    public static String getModeleCapacity(final String environment) {
        if (isMatlab(environment)) {
            return PoolConstants.MATLAB_MODELET_CAPACITY;
        } else {
            return PoolConstants.R_MODELET_CAPACITY;
        }
    }

    public static boolean isTenantAny(final String tenant) {
        if (tenant != null) {
            return tenant.trim().equalsIgnoreCase(PoolConstants.ANY);
        } else {
            return false;
        }
    }

    public static boolean isModelAny(final String model) {
        if (model != null) {
            return model.trim().equalsIgnoreCase(PoolConstants.ANY);
        } else {
            return false;
        }
    }

    public static String getModelName(final String model) {
        final int seperatorIndex = model.lastIndexOf(PoolConstants.MODEL_SEPERATOR);
        return model.substring(0, seperatorIndex);
    }

    public static String getModelVersion(final String model) {
        final int seperatorIndex = model.lastIndexOf(PoolConstants.MODEL_SEPERATOR);
        return model.substring(seperatorIndex + 1);
    }
}