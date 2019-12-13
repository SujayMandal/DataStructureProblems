/**
 * 
 */
package com.fa.dp.core.rest.info;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * @author
 * 
 */
@SuppressWarnings({ "PMD.NPathComplexity", "PMD.CyclomaticComplexity", "PMD.TooManyFields" })
public class ParamInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String description;
    private boolean mandatory;
    private boolean syndicate;
    private String name;
    private String apiName;
    private String modelParamName;
    private String text;
    private String flatenedName;
    private int sequence;
    private DatatypeInfo datatype;
    private String dataTypeStr;
    private boolean mapped;
    private String dataFormat;
    private int size;
    private int precision;
    private boolean userSelected;
    private String nativeDataType;
    private String acceptableValues;
    private Object[] acceptableValueArr;
	
	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isSyndicate() {
        return syndicate;
    }

    public void setSyndicate(boolean syndicate) {
        this.syndicate = syndicate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        if (StringUtils.isNotBlank(name)) {
            text = name;
        }
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFlatenedName() {
        return flatenedName;
    }

    public void setFlatenedName(String flatenedName) {
        this.flatenedName = flatenedName;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

	public String getNativeDataType() {
		return nativeDataType;
	}

	public void setNativeDataType(String nativeDataType) {
		this.nativeDataType = nativeDataType;
	}

    public DatatypeInfo getDatatype() {
        return datatype;
    }

    public void setDatatype(DatatypeInfo datatype) {
        this.datatype = datatype;
    }

    public String getDataTypeStr() {
        if (this.datatype != null) {
            dataTypeStr = datatype.readDataTypeString();
        }
        return dataTypeStr;
    }

    public void setDataTypeStr(String dataTypeStr) {
        this.dataTypeStr = dataTypeStr;
    }

    public boolean isMapped() {
        return mapped;
    }

    public void setMapped(boolean mapped) {
        this.mapped = mapped;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public boolean isUserSelected() {
        return userSelected;
    }

    public void setUserSelected(boolean userSelected) {
        this.userSelected = userSelected;
    }
    
    
	public String getApiName() {
		return apiName;
	}
	
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public String getModelParamName() {
		return modelParamName;
	}
	
	public void setModelParamName(String modelParamName) {
		this.modelParamName = modelParamName;
	}
	

    public String getAcceptableValues() {
		return acceptableValues;
	}
    
	public void setAcceptableValues(String acceptableValues) {
		this.acceptableValues = acceptableValues;
	}
	
	public Object[] getAcceptableValueArr() {
		return acceptableValueArr;
	}

	public void setAcceptableValueArr(Object[] acceptableValueArr) {
		this.acceptableValueArr = acceptableValueArr;
	}

	

	@Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((datatype == null) ? 0 : datatype.hashCode());
        result = prime * result + (mandatory ? 1231 : 1237);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (syndicate ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = true;
        if (this == obj) {
            equal = true;
        } else if (obj == null) {
            equal = false;
        } else if (getClass() != obj.getClass()) {
            equal = false;
        }
        if (equal) {
            ParamInfo other = (ParamInfo) obj;
            if (datatype == null) {
                if (other.datatype != null) {
                    equal = false;
                }
            } else if (!datatype.equals(other.datatype)) {
                equal = false;
            }
            if (equal && mandatory != other.mandatory) {
                equal = false;
            }
            if (equal && name == null) {
                if (other.name != null) {
                    equal = false;
                }
            } else if (equal && !name.equals(other.name)) {
                equal = false;
            }
            if (equal && syndicate != other.syndicate) {
                equal = false;
            }
        }
        return equal;
    }
}