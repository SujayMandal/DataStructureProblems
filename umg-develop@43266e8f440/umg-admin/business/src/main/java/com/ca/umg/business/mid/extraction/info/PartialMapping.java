/**
 * 
 */
package com.ca.umg.business.mid.extraction.info;

import java.io.Serializable;
import java.util.List;

/**
 * @author chandrsa
 *
 */
public class PartialMapping<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<T> partials;

    public List<T> getPartials() {
        return partials;
    }

    public void setPartials(List<T> partials) {
        this.partials = partials;
    }
}