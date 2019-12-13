/**
 * 
 */
package com.ca.umg.business.version.info;

import java.io.Serializable;
import java.util.List;

import com.ca.umg.business.mid.extraction.info.DatatypeInfo;

/**
 * @author kamathan
 *
 */
public class TenantParamInfo implements Serializable {

    private static final long serialVersionUID = 8304614798403971522L;

    private String name;

    private DatatypeInfo datatype;

    private List<TenantParamInfo> children;
    
    private boolean mandatory;
    
    /**
	 * @return the mandatory
	 */
	public boolean isMandatory() {
		return mandatory;
	}

	/**
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

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

    public List<TenantParamInfo> getChildren() {
        return children;
    }

    public void setChildren(List<TenantParamInfo> children) {
        this.children = children;
    }
}
