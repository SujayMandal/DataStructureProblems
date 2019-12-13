
package com.ca.umg.rt.metrics.reporter.entity;


/**
 * @author callegustafsson
 *
 */
public class GaugeEntity extends CoreEntity {



    private Object value;


    public Object getValue() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }


}
