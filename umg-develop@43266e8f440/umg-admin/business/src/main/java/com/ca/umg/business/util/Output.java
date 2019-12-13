package com.ca.umg.business.util;

import java.util.ArrayList;
import java.util.List;

public class Output {
    private List<Object> nativefieldlist = new ArrayList<Object>();
    private List<Object> fieldlist = new ArrayList<Object>();

    public List<Object> getNativefieldlist() {
        return nativefieldlist;
    }

    public void setNativefieldlist(List<Object> nativefieldlist) {
        this.nativefieldlist = nativefieldlist;
    }

    public List<Object> getFieldlist() {
        return fieldlist;
    }

    public void setFieldlist(List<Object> fieldlist) {
        this.fieldlist = fieldlist;
    }
}
