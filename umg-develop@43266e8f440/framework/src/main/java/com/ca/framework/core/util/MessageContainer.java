package com.ca.framework.core.util;

import java.util.Locale;

import javax.inject.Named;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Named
public class MessageContainer implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    public static String getMessage(String key, Object[] arguments) {
        return applicationContext.getMessage(key, arguments, Locale.getDefault());
    }
}
