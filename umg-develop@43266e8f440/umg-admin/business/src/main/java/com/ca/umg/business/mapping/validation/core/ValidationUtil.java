package com.ca.umg.business.mapping.validation.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidationUtil {
    @Autowired
    private ValidatorFactory validatorFactory;

    public Queue<AbstractValidator> getValidators(List<String> keys) {
        Queue<AbstractValidator> valQueue = new LinkedList<AbstractValidator>();
        for (String key : keys) {
            valQueue.add(validatorFactory.getValidator(key));
        }
        return valQueue;
    }

}
