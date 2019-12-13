/**
 * 
 */
package com.fa.dp.core.rest.info;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

public class MidParamInfo extends ParamInfo {

    private static final long serialVersionUID = 1L;
    private List<MidParamInfo> children;
    private Object value;

    public List<MidParamInfo> getChildren() {
        return children;
    }

    public void setChildren(List<MidParamInfo> children) {
        this.children = children;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((children == null) ? 0 : children.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = true;
        if (this == obj) {
            equal = true;
        } else if (!super.equals(obj)) {
            equal = false;
        } else if (getClass() != obj.getClass()) {
            equal = false;
        }
        if (equal) {
            MidParamInfo other = (MidParamInfo) obj;
            if (CollectionUtils.isEmpty(children) && CollectionUtils.isNotEmpty(other.children)) {
                equal = false;
            } else if (CollectionUtils.isNotEmpty(children) && CollectionUtils.isEmpty(other.children)) {
                equal = false;
            }
        }
        return equal;
    }
}
