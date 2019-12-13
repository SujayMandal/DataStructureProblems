package com.ca.umg.business.validation;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.stereotype.Service;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.version.info.VersionInfo;

@Service
public class UpdateVersionValidator {

    private final Validator validator;

    public UpdateVersionValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public final List<ValidationError> validate(VersionInfo version) throws BusinessException, SystemException {
        List<ValidationError> errors = new ArrayList<>();
        validateAnnotions(version, errors);
        return errors;
    }

    protected void validateAnnotions(VersionInfo version, List<ValidationError> errors) {
        Set<ConstraintViolation<VersionInfo>> constraintViolations = validator.validateProperty(version, "versionDescription");
        if (isNotEmpty(constraintViolations)) {
            errors.addAll(buildErrorMessage(constraintViolations));
        }
    }

    protected final <T> List<ValidationError> buildErrorMessage(final Set<ConstraintViolation<T>> constraintViolations) {
        Iterator<ConstraintViolation<T>> itr = constraintViolations.iterator();
        List<ValidationError> errors = new ArrayList<ValidationError>();
        while (itr.hasNext()) {
            ConstraintViolation<T> constraintViolation = itr.next();
            errors.add(new ValidationError(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage()));
        }
        return errors;
    }
}
