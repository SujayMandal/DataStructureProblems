package com.ca.umg.business.mapping.validation.validator;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.mapping.validation.core.AbstractValidator;
import com.ca.umg.business.mapping.validation.core.MappingValidatorConstants;
import com.ca.umg.business.mapping.validation.core.MappingValidatorContainer;
import com.ca.umg.business.mapping.validation.core.ValidatorFactory;
import com.ca.umg.business.validation.ValidationError;

@Component(value = MappingValidatorConstants.MANDATORY_MAPPING_VALIDATOR)
public class MandatoryMappingValidator extends AbstractValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MandatoryMappingValidator.class.getName());
    @Autowired
    private ValidatorFactory validatorFactory;
    private static final String MAND_MID_MAPPING_VALIDATOR = MappingValidatorConstants.MAND_MID_MAPPING_VALIDATOR;

    @Override
    public boolean execute(MappingValidatorContainer inputs, List<ValidationError> errors, Map<String,List<String>> mappingToDelete) {
        AbstractValidator validator;
        boolean isValid = true;

        if (inputs.getMidMapping().getInputs() != null
                && inputs.getMidMapping().getInputs().size() < BusinessConstants.NUMBER_ONE) {
            final String mappedTo = inputs.getMidMapping().getMappedTo();
            errors.add(new ValidationError(mappedTo,
                    MappingValidatorConstants.LESS_THAN_ONE_MAPP_ERROR));
            LOGGER.info("No Mapping found for mandatory mid param info - " + mappedTo);
            isValid = false;
        } else {
            validator = validatorFactory.getValidator(MAND_MID_MAPPING_VALIDATOR);
            isValid = validator.validate(inputs, errors,mappingToDelete);
        }

        return isValid;
    }

}
