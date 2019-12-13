/**
 * 
 */
package com.ca.umg.plugin.commons.version.entity;

import com.ca.framework.core.custom.mapper.UMGPassThrough;
import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author chandrsa
 * 
 */
@UMGPassThrough
public class DatatypeInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String ARRAY = "ARRAY";
    private static final String NUMERICTYPE = "numerictype";
    private static final String FRACTION_DIGITS = "fractionDigits";
    private static final String MAX_LENGTH = "maxLength";
    private static final String MIN_LENGTH = "minLength";
    private static final String TOTAL_DIGITS = "totalDigits";
    private static final String PATTERN = "pattern";
    public static final String DIMENSIONS = "dimensions";

    private boolean isArrayField;
    private String type;
    private Map<String, Object> properties;

    public boolean isArray() {
        return isArrayField;
    }

    public void setArray(boolean isArray) {
        this.isArrayField = isArray;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getProperties() {
        if (properties == null) {
            properties = new HashMap<String, Object>();
        }
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    @JsonIgnore
    public String getDataTypeString() {
        String type = null;
        StringBuffer dataTypeBuff = null;
        if (StringUtils.isNotBlank(this.getType())) {
            dataTypeBuff = new StringBuffer();
            type = this.getType().toUpperCase(Locale.getDefault());
            dataTypeBuff.append(type);

            deriveDatatype(type, dataTypeBuff);
            setArrayDefinitions(dataTypeBuff);
        }
        return dataTypeBuff != null ? dataTypeBuff.toString().substring(0, dataTypeBuff.toString().length() - 1) : null;
    }

    private void deriveDatatype(String type, StringBuffer dataTypeBuff) {
        switch (Datatype.valueOf(type)) {
            case STRING:
                addStringInfo(dataTypeBuff);
                break;
            case DATE:
            case DATETIME:
                addDateTimeInfo(dataTypeBuff);
                break;
            case DOUBLE:
                addDoubleDataInfo(dataTypeBuff);
                break;
            case INTEGER:
                addIntegerInfo(dataTypeBuff);
                break;
            case LONG:
                addLongInfo(dataTypeBuff);
                break;
            case BIGINTEGER:
                addBigIntegerInfo(dataTypeBuff);
                break;
            case BIGDECIMAL:
                addBigDecimalDataInfo(dataTypeBuff);
                break;
            case NUMERIC:
                if (MapUtils.isNotEmpty(getProperties())) {
                    dataTypeBuff.append(ExcelConstants.CORR_ID_HYPHEN).append(getProperties().get(NUMERICTYPE));
                }
                break;
            default:
                break;
        }
    }

    private void addIntegerInfo(StringBuffer dataTypeBuff) {
        if (MapUtils.isNotEmpty(getProperties()) && this.getProperties().containsKey(TOTAL_DIGITS)) {
            dataTypeBuff.append(ExcelConstants.CORR_ID_HYPHEN).append(getProperties().get(TOTAL_DIGITS));
        }
    }

    private void addLongInfo(StringBuffer dataTypeBuff) {
        if (MapUtils.isNotEmpty(getProperties()) && this.getProperties().containsKey(TOTAL_DIGITS)) {
            dataTypeBuff.append(ExcelConstants.CORR_ID_HYPHEN).append(getProperties().get(TOTAL_DIGITS));
        }
    }

    private void addBigIntegerInfo(StringBuffer dataTypeBuff) {
        if (MapUtils.isNotEmpty(getProperties()) && this.getProperties().containsKey(TOTAL_DIGITS)) {
            dataTypeBuff.append(ExcelConstants.CORR_ID_HYPHEN).append(getProperties().get(TOTAL_DIGITS));
        }
    }

    private void addDateTimeInfo(StringBuffer dataTypeBuff) {
        if (MapUtils.isNotEmpty(getProperties()) && this.getProperties().containsKey(PATTERN)) {
            dataTypeBuff.append(ExcelConstants.CORR_ID_HYPHEN).append(getProperties().get(PATTERN));
        }
    }

    private void addStringInfo(StringBuffer dataTypeBuff) {
        if (MapUtils.isNotEmpty(getProperties())) {
            if (this.getProperties().containsKey(PATTERN)) {
                dataTypeBuff.append(ExcelConstants.CORR_ID_HYPHEN).append(getProperties().get(PATTERN));
            }
            if (this.getProperties().containsKey(MIN_LENGTH)) {
                dataTypeBuff.append(ExcelConstants.CORR_ID_HYPHEN).append(getProperties().get(MIN_LENGTH));
            }
            if (this.getProperties().containsKey(MAX_LENGTH)) {
                dataTypeBuff.append(ExcelConstants.CORR_ID_HYPHEN).append(getProperties().get(MAX_LENGTH));
            }
        }
    }

    private void addDoubleDataInfo(StringBuffer dataTypeBuff) {
        if (MapUtils.isNotEmpty(getProperties())) {
            if (this.getProperties().containsKey(TOTAL_DIGITS)) {
                dataTypeBuff.append(ExcelConstants.CORR_ID_HYPHEN).append(getProperties().get(TOTAL_DIGITS));
            }
            if (this.getProperties().containsKey(FRACTION_DIGITS)) {
                dataTypeBuff.append(ExcelConstants.CORR_ID_HYPHEN).append(getProperties().get(FRACTION_DIGITS));
            }
        }
    }

    private void addBigDecimalDataInfo(StringBuffer dataTypeBuff) {
        if (MapUtils.isNotEmpty(getProperties())) {
            if (this.getProperties().containsKey(TOTAL_DIGITS)) {
                dataTypeBuff.append(ExcelConstants.CORR_ID_HYPHEN).append(getProperties().get(TOTAL_DIGITS));
            }
            if (this.getProperties().containsKey(FRACTION_DIGITS)) {
                dataTypeBuff.append(ExcelConstants.CORR_ID_HYPHEN).append(getProperties().get(FRACTION_DIGITS));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void setArrayDefinitions(StringBuffer dataTypeBuff) {
        List<Integer> dimensions = null;
        StringBuffer dimension = null;
        if (isArray()) {
            dataTypeBuff.append(ExcelConstants.CORR_ID_SEPERATOR).append(ARRAY);
            if (MapUtils.isNotEmpty(getProperties())) {
                dimension = new StringBuffer();
                dimensions = (List<Integer>) getProperties().get(DIMENSIONS);
                if (CollectionUtils.isNotEmpty(dimensions)) {
                    dimension.append(ExcelConstants.CORR_ID_SEPERATOR);
                    getDimensions(dataTypeBuff, dimensions, dimension);
                } else {
                    dataTypeBuff.append(ExcelConstants.CORR_ID_COMMA);
                }
            } else {
                dataTypeBuff.append(ExcelConstants.CORR_ID_COMMA);
            }
        } else {
            dataTypeBuff.append(ExcelConstants.CORR_ID_COMMA);
        }
    }

    private void getDimensions(StringBuffer dataTypeBuff, List<Integer> dimensions, StringBuffer dimension) {
        int calcDim = ExcelConstants.NUMBER_ONE;
        for (Integer dim : dimensions) {
            calcDim = calcDim * dim;
            dimension.append(dim);
            dimension.append(ExcelConstants.CORR_ID_COMMA);
        }

        if (calcDim != ExcelConstants.NUMBER_ONE) {
            dataTypeBuff.append(dimension.toString());
        } else {
            dataTypeBuff.append(ExcelConstants.CORR_ID_COMMA);
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public boolean equals(Object obj) {
        boolean isEqual = false;
        if (obj instanceof DatatypeInfo) {
            DatatypeInfo targetObj = (DatatypeInfo) obj;
            if (this.getType().equalsIgnoreCase(targetObj.getType())) {
                if (this.getDataTypeString() == null && targetObj.getDataTypeString() == null) {
                    isEqual = true;
                } else if (this.getDataTypeString() != null
                        && this.getDataTypeString().equalsIgnoreCase(targetObj.getDataTypeString())) {
                    isEqual = true;
                }
            }
        }
        return isEqual;
    }

    public enum Datatype {
        STRING("STRING"), DOUBLE("DOUBLE"), OBJECT("OBJECT"), INTEGER("INTEGER"), LONG("LONG"), NUMERIC("NUMERIC"), BOOLEAN("BOOLEAN"), DATE(
                "DATE"), ANY("ANY"), DATETIME("DATETIME"), BIGINTEGER("BIGINTEGER"), BIGDECIMAL("BIGDECIMAL");

        private String datatype;

        Datatype(String datatype) {
            this.datatype = datatype;
        }

        public String getDatatype() {
            return datatype;
        }
        
     }
}
