/**
 * 
 */
package com.ca.framework.core.info.tenant;

import com.ca.framework.core.info.BaseInfo;

/**
 * 
 * @author kamathan
 * @version 1.0
 */
public class SystemKeyInfo extends BaseInfo {

    private static final long serialVersionUID = 4032835483979846053L;

    private String key;

    private String type;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
