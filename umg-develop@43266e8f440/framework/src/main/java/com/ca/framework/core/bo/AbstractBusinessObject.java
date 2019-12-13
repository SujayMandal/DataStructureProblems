package com.ca.framework.core.bo;

import static com.ca.framework.core.exception.BusinessException.raiseBusinessException;

import java.util.Iterator;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.ca.framework.core.db.domain.AbstractPersistable;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;

public class AbstractBusinessObject implements BusinessObject {

    private static final long serialVersionUID = -8936643000140278588L;

    public <T extends AbstractPersistable> void validate(T info) throws BusinessException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(info);
        if (constraintViolations.size() > 0) {
            String message = buildMessage(constraintViolations);
            raiseBusinessException(FrameworkExceptionCodes.FSE0000001, new Object[] { message });
        }
    }

    public <T extends AbstractPersistable> String buildMessage(Set<ConstraintViolation<T>> constraintViolations) {
        Iterator<ConstraintViolation<T>> it = constraintViolations.iterator();
        StringBuffer message = new StringBuffer();
        while (it.hasNext()) {
            ConstraintViolation constraintViolation = it.next();
            message.append(constraintViolation.getMessage()).append(" ");
        }
        return message.toString();
    }
}
