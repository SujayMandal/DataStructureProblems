/**
 * 
 */
package com.ca.umg.business.mapping.validation.validator;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ca.umg.business.mapping.validation.core.AbstractValidator;
import com.ca.umg.business.mapping.validation.core.MappingValidatorConstants;
import com.ca.umg.business.mapping.validation.core.MappingValidatorContainer;
import com.ca.umg.business.mapping.validation.core.ValidatorFactory;
import com.ca.umg.business.validation.ValidationError;

/**
 * @author mahantat
 * 
 */
@Component(value = MappingValidatorConstants.OPTIONAL_MAPPING_VALIDATOR)
public class OptionalMappingValidator extends AbstractValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(OptionalMappingValidator.class.getName());
    private static final int ONE = 1;
    @Autowired
    private ValidatorFactory validatorFactory;
    private static final String OPT_MID_MAPPING_VALIDATOR = MappingValidatorConstants.OPT_MID_MAPPING_VALIDATOR;

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.mapping.validation.core.Validator#execute(java.util .Map, java.util.List)
     */
    @Override
    public boolean execute(MappingValidatorContainer inputs, List<ValidationError> errors, Map<String,List<String>> mappingToDelete) {
        AbstractValidator validator;
        boolean isValid = true;
        if (inputs.getMidMapping().getInputs() != null && inputs.getMidMapping().getInputs().size() > ONE) {
            final String mappedTo = inputs.getMidMapping().getMappedTo();
            errors.add(new ValidationError(mappedTo,
                    MappingValidatorConstants.MORE_THAN_ONEMAPP_ERROR));
            LOGGER.info("More than one mapping found for optional mid param info - " + mappedTo);
            isValid = false;
        } else {
            validator = validatorFactory.getValidator(OPT_MID_MAPPING_VALIDATOR);
            isValid = validator.validate(inputs, errors, mappingToDelete);
        }
        return isValid;
    }
}
