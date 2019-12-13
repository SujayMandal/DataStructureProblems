package com.ca.umg.business.validation;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.stereotype.Service;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.version.bo.VersionBO;
import com.ca.umg.business.version.info.VersionInfo;

@Service
public class CreateVersionValidator {

    @Inject
    private VersionBO versionBO;

    private final Validator validator;

    public CreateVersionValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public final List<ValidationError> validate(VersionInfo version) throws BusinessException, SystemException {
        List<ValidationError> errors = new ArrayList<>();
        validateAnnotions(version, errors);
        validateVersionAlreadyExists(version);
        isModelTypeCorrect(version, errors);
        return errors;
    }

    protected void validateAnnotions(VersionInfo version, List<ValidationError> errors) {
        Set<ConstraintViolation<VersionInfo>> constraintViolations = validator.validate(version);
        if (isNotEmpty(constraintViolations)) {
            errors.addAll(buildMessage(constraintViolations));
        }
    }

    private final <T> List<ValidationError> buildMessage(final Set<ConstraintViolation<T>> constraintViolations) {
        Iterator<ConstraintViolation<T>> it = constraintViolations.iterator();
        List<ValidationError> errors = new ArrayList<ValidationError>();
        while (it.hasNext()) {
            ConstraintViolation<T> constraintViolation = it.next();
            errors.add(new ValidationError(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage()));
        }
        return errors;
    }

    private void validateVersionAlreadyExists(VersionInfo version) throws BusinessException, SystemException {
        if (isVersionAlreadyExists(version)) {
            throw new BusinessException(BusinessExceptionCodes.BSE000062, new String[] { version.getName(),
                    version.getMapping().getName(), version.getModelLibrary().getUmgName() });
        }
    }

    private boolean isVersionAlreadyExists(VersionInfo version) throws BusinessException, SystemException {
        return versionBO.findByNameAndMappingNameAndModelLibraryUmgName(version.getName(), version.getMapping().getName(),
                version.getModelLibrary().getUmgName()) != null;
    }
    
    private void isModelTypeCorrect(final VersionInfo version, final List<ValidationError> errors) throws BusinessException, SystemException {
    	final ModelType mt = ModelType.getModelType(version.getModelType());
    	if (mt == null || mt == ModelType.ALL) {
    		final ValidationError ve = new ValidationError("Model Type", "Model Type is wrong, it should be Online / Bulk");
    		errors.add(ve);
    		throw new BusinessException(BusinessExceptionCodes.BSE000062, new String[] { version.getName(), version.getMapping().getName(), 
    				version.getModelLibrary().getUmgName() });
    	} else {
    		version.setModelType(mt.getType());
    	}
    }
}
