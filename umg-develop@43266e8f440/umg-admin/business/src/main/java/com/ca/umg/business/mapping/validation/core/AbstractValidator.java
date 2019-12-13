package com.ca.umg.business.mapping.validation.core;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.ca.umg.business.validation.ValidationError;

public abstract class AbstractValidator {
    public final boolean validate(MappingValidatorContainer inputs, List<ValidationError> errors,
            Queue<AbstractValidator> validatorQueue, boolean validateAll, Map<String,List<String>> mappingToDelete) {
        boolean isValid = this.execute(inputs, errors, mappingToDelete);
        if (validatorQueue != null && (isValid || validateAll)) {
            AbstractValidator nextValidator = validatorQueue.poll();
            if (nextValidator != null) {
                isValid = nextValidator.validate(inputs, errors, validatorQueue, validateAll, mappingToDelete) && isValid;
            }

        }
        return isValid;
    }

    public final boolean validate(MappingValidatorContainer inputs, List<ValidationError> errors,
            Queue<AbstractValidator> validatorQueue, Map<String,List<String>> mappingToDelete) {
        return this.validate(inputs, errors, validatorQueue, true, mappingToDelete);
    }

    public final boolean validate(MappingValidatorContainer inputs, List<ValidationError> errors, Map<String,List<String>> mappingToDelete) {
        return this.validate(inputs, errors, null, true, mappingToDelete);
    }

    public final boolean validate(MappingValidatorContainer inputs, List<ValidationError> errors, boolean validateAll, Map<String,List<String>> mappingToDelete) {
        return this.validate(inputs, errors, null, validateAll, mappingToDelete);
    }

    public abstract boolean execute(MappingValidatorContainer inputs, List<ValidationError> errors, Map<String,List<String>> mappingToDelete);
}
