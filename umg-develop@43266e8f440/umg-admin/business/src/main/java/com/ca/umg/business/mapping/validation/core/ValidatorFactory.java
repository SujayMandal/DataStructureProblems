package com.ca.umg.business.mapping.validation.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ValidatorFactory {
    @Autowired
    private ApplicationContext applicationContext;

    public AbstractValidator getValidator(String key) {
        return applicationContext.getBean(key, AbstractValidator.class);
    }
}
