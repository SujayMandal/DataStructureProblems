package com.ca.framework.core.db.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(value={METHOD,FIELD})
@Retention(value=RUNTIME)
public @interface Encrypt {

}
