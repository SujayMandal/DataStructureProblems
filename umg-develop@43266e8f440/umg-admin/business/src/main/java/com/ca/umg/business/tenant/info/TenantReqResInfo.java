package com.ca.umg.business.tenant.info;

import java.io.Serializable;
import java.util.List;

import com.ca.umg.business.mid.extraction.info.DatatypeInfo;

public class TenantReqResInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String name;
    
    private DatatypeInfo datatype;
    
    private boolean mandatory;
    
    private List<TenantReqResInfo> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DatatypeInfo getDatatype() {
        return datatype;
    }

    public void setDatatype(DatatypeInfo datatype) {
        this.datatype = datatype;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public List<TenantReqResInfo> getChildren() {
        return children;
    }

    public void setChildren(List<TenantReqResInfo> children) {
        this.children = children;
    }
    
}
