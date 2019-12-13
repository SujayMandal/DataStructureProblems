
/**
 * 
 */
package com.ca.modelet;

import java.io.Serializable;

import org.pojomatic.Pojomatic;

/**
 * @author kamathan
 * 
 */
public class BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Don't override. Use Pojomatic annotations instead.
     */
    @Override
    public final String toString() {
        return Pojomatic.toString(this);
    }

    /*
     * Method is final because derived classes should use Pojomatic annotations.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object obj) {
        return Pojomatic.equals(this, obj);
    }

    /*
     * Method is final because derived classes should use Pojomatic annotations.
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return Pojomatic.hashCode(this);
    }

}
