/**
 * 
 */
package com.fa.dp.business.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to identify the commands in the application context.All the command classes must be annotated with this
 * annotation.
 * 
 * @author mandasuj
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandDescription {

    String name();
}
