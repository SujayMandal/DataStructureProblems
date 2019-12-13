/**
 * 
 */
package com.ca.umg.rt.cache.event;

import java.io.Serializable;

/**
 * @author kamathan
 *
 */
public class StaticDataRefreshEvent implements Serializable {

    private static final long serialVersionUID = -616409205217472731L;

    public static final String REFRESH_TENANT_EVENT = "REFRESH_TENANT_EVENT";

    public static final String REFRESH_SUPPORT_PACKAGES_EVENT = "REFRESH_SUPPORT_PACKAGES_EVENT";

    private String event;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

}
