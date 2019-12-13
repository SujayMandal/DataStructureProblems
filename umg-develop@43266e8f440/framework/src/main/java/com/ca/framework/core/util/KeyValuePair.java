/**
 * 
 */
package com.ca.framework.core.util;

import java.io.Serializable;

/**
 * This is a utility class to hold data in key value pairs.
 * 
 * @author kamathan
 * 
 */
public class KeyValuePair<K, V> implements Serializable {

    private static final long serialVersionUID = 1L;

    private K key = null;

    private V value = null;

    public KeyValuePair() {
        // empty constructor
    }

    public KeyValuePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return the key
     */
    public K getKey() {
        return key;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(K key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public V getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(V value) {
        this.value = value;
    }

}
