/**
 * 
 */
package com.ca.umg.business.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author chandrsa
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface XmlAttribute {
    public enum DataType {
        BOOLEAN, INT, LONG, DOUBLE, STRING, CHAR
    }

    String name();

    DataType type() default DataType.STRING;
}
