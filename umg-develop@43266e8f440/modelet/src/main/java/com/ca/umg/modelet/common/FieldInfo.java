package com.ca.umg.modelet.common;

import static com.ca.framework.core.ioreduce.BooleanValueEnum.getBooleanValueEnum;
import static com.ca.framework.core.ioreduce.DataTypeValueEnum.getDataTypeValueEnum;
import static com.ca.framework.core.ioreduce.FieldInfoEnum.COLLECTION;
import static com.ca.framework.core.ioreduce.FieldInfoEnum.DATA_TYPE;
import static com.ca.framework.core.ioreduce.FieldInfoEnum.FIELD_NAME;
import static com.ca.framework.core.ioreduce.FieldInfoEnum.NATIVE_DATA_TYPE;
import static com.ca.framework.core.ioreduce.FieldInfoEnum.SEQUENCE;
import static com.ca.framework.core.ioreduce.FieldInfoEnum.VALUE;
import static com.ca.framework.core.ioreduce.NativeDataTypeValueEnum.getNativeDataTypeValueEnum;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import com.ca.framework.core.ioreduce.FieldInfoEnum;

@SuppressWarnings("PMD")
public class FieldInfo implements Comparable<FieldInfo> {

    private String modelParameterName;
    private String apiName;
    private String sequence;
    private String dataType;
    private boolean collection;
    private Object value;
    private String nativeDataType;

    private boolean rowwise;
    
    private int precession;
    
    public String getModelParameterName() {
        return modelParameterName;
    }

    public void setModelParameterName(final String modelParameterName) {
        this.modelParameterName = modelParameterName;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(final String sequence) {
        if (this.sequence == null) {
            this.sequence = sequence;
        }
    }

    public String getNativeDataType() {
        return nativeDataType;
    }

    public void setNativeDataType(final String nativeDataType) {
        if (this.nativeDataType == null) {
            this.nativeDataType = nativeDataType;
        }
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(final String dataType) {
        if (this.dataType == null) {
            this.dataType = dataType;
        }
    }

    public boolean isCollection() {
        return collection;
    }

    public void setCollection(final boolean collection) {
        this.collection = collection;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    @Override
    public int compareTo(final FieldInfo info) {
        int val1 = NumberUtils.toInt(sequence);
        int val2 = NumberUtils.toInt(info.sequence);
        return val1 == val2 ? 0 : val1 < val2 ? -1 : 1;
    }

    @Override
    public int hashCode() {
        return NumberUtils.toInt(this.sequence);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof FieldInfo) {
            int val1 = NumberUtils.toInt(this.sequence);
            int val2 = NumberUtils.toInt(((FieldInfo) obj).sequence);

            return val1 == val2;
        }

        return false;
    }

    public FieldInfo() {
        // Empty Constructor
    }

    public FieldInfo(final Object obj) {
        if (obj instanceof FieldInfo) {
            FieldInfo fi = (FieldInfo) obj;
            collection = fi.collection;
            dataType = fi.dataType;
            modelParameterName = fi.modelParameterName;
            nativeDataType = fi.nativeDataType;
            sequence = fi.sequence;
            value = fi.value;
            precession = fi.precession;
        } else if (obj instanceof Map<?, ?>) {
            HashMap<String, Object> hm = (HashMap<String, Object>) obj;
            setP(hm.get("p"));
            if (hm.containsKey(COLLECTION.getName())) {
                collection = Boolean.valueOf(hm.get(COLLECTION.getName()).toString());
            }
            if (hm.containsKey(DATA_TYPE.getName())) {
                dataType = hm.get(DATA_TYPE.getName()).toString();
            }

            if (hm.containsKey(FIELD_NAME.getName())) {
                modelParameterName = hm.get(FIELD_NAME.getName()).toString();
            }

            if (hm.containsKey(NATIVE_DATA_TYPE.getName())) {
                nativeDataType = hm.get(NATIVE_DATA_TYPE.getName()) != null ? hm.get(NATIVE_DATA_TYPE.getName()).toString()
                        : null;
            }

            if (hm.containsKey(SEQUENCE.getName())) {
                sequence = hm.get(SEQUENCE.getName()).toString();
            }

            if (hm.containsKey(VALUE.getName())) {
                value = hm.get(VALUE.getName());
            }
            if (hm.containsKey(FieldInfoEnum.PRECESSION.getName())) {
                precession = (int) hm.get(Integer.valueOf(FieldInfoEnum.PRECESSION.getName()));
            }

            // value = hm.get("value");
        } 
        else if (obj instanceof List)	{
			List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) obj;
			for(int i = 0 ; i < list.size() ; i ++ ){
				HashMap<String, Object> hm = (HashMap<String, Object>) list.get(i);
			setP(hm.get("p"));
			if (hm.containsKey(COLLECTION.getName())) { 
				collection = Boolean.valueOf(hm.get(COLLECTION.getName()).toString());
			}
			if (hm.containsKey(DATA_TYPE.getName())) {
				dataType = hm.get(DATA_TYPE.getName()).toString();
			}
			
			if (hm.containsKey(FIELD_NAME.getName())) {
				modelParameterName = hm.get(FIELD_NAME.getName()).toString();
			}
			
			if (hm.containsKey(NATIVE_DATA_TYPE.getName())) {
				nativeDataType = hm.get(NATIVE_DATA_TYPE.getName()) != null ? hm.get(NATIVE_DATA_TYPE.getName()).toString() : null;
			}
			
			if (hm.containsKey(SEQUENCE.getName())) {
				sequence = hm.get(SEQUENCE.getName()).toString();
			}
			
			if (hm.containsKey(VALUE.getName())) {
				value = hm.get(VALUE.getName());
			}
			 if (hm.containsKey(FieldInfoEnum.PRECESSION.getName())) {
	                precession = (int) hm.get(Integer.valueOf(FieldInfoEnum.PRECESSION.getName()));
	            }

//			value = hm.get("value");
		}
		}
        else {
            collection = false;
            value = obj;

            if (obj instanceof Integer) {
                dataType = "integer";
            }
            if (obj instanceof Long) {
                dataType = "long";
            } else if (obj instanceof String) {
                dataType = "string";
            } else if (obj instanceof Boolean) {
                dataType = "boolean";
            } else if (obj instanceof Double || obj instanceof BigDecimal || obj instanceof BigInteger) {
                dataType = "double";
            }
        }
    }

    private Object p;

    public Object getP() {
        return p;
    }

    public void setP(final Object p) {
        this.p = p;
        if (p != null && p instanceof List) {
            final List<Object> parameterList = (ArrayList<Object>) p;

            setModelParameterName(parameterList.get(FIELD_NAME.getIndex()).toString());
            setSequence(parameterList.get(SEQUENCE.getIndex()).toString());
            setDataType(getDataTypeValueEnum(parameterList.get(DATA_TYPE.getIndex()).toString()).getStrValue());
            setNativeDataType(
                    getNativeDataTypeValueEnum(parameterList.get(NATIVE_DATA_TYPE.getIndex()).toString()).getStrValue());
            setCollection(getBooleanValueEnum((Integer) parameterList.get(COLLECTION.getIndex())).isBoolValue());
            setValue(parameterList.get(VALUE.getIndex()));
            setPrecession((int) parameterList.get(FieldInfoEnum.PRECESSION.getIndex()));
        } else if (p != null) {
            setValue(p);
        }
    }

    public boolean isRowwise() {
        return rowwise;
    }

    public void setRowwise(boolean rowwise) {
        this.rowwise = rowwise;
    }
    
    public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public int getPrecession() {
		return precession;
	}

	public void setPrecession(int precession) {
		this.precession = precession;
	}
	
}
