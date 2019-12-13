package com.ca.umg.business.syndicatedata.info;

import java.io.Serializable;

public class SyndicateDataQueryParameterInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String dataType;
    
    private String dataTypeFormat;

    private String sampleValue;
	
	private boolean mandatory;

    private int sequence;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getSampleValue() {
        return sampleValue;
    }

    public void setSampleValue(String sampleValue) {
        this.sampleValue = sampleValue;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
	
    public boolean isMandatory() {
        return mandatory;
    }
    
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

	public String getDataTypeFormat() {
		return dataTypeFormat;
	}

	public void setDataTypeFormat(String dataTypeFormat) {
		this.dataTypeFormat = dataTypeFormat;
	}
    
}
