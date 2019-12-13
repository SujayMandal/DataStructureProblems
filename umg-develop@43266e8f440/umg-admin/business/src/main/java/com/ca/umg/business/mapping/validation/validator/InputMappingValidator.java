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
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.validation.ValidationError;

@Component(value = MappingValidatorConstants.INPUT_MAPPING_VALIDATOR)
public class InputMappingValidator extends AbstractValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(InputMappingValidator.class.getName());
    @Autowired
    private ValidatorFactory validatorFactory;
    private static final String MANDATORY_MAPPING_VALIDATOR = MappingValidatorConstants.MANDATORY_MAPPING_VALIDATOR;
    private static final String OPTIONAL_MAPPING_VALIDATOR = MappingValidatorConstants.OPTIONAL_MAPPING_VALIDATOR;

    @Override
    public boolean execute(MappingValidatorContainer inputs, List<ValidationError> errors, Map<String,List<String>> mappingToDelete) {
        AbstractValidator validator;
        boolean isValid = true;
        MidParamInfo midParamInfo = inputs.getMidParamInfoMap().get(inputs.getMidMapping().getMappedTo());
        if (inputs.getMidMapping().getInputs() != null && inputs.getMidMapping().getInputs().size() > 2) {
            errors.add(new ValidationError(inputs.getMidMapping().getMappedTo(),
                    MappingValidatorConstants.MORE_THAN_TWO_MAPP_ERROR));
            LOGGER.info("More than two mappings found for mid param info " + midParamInfo.getFlatenedName());
            isValid = false;
        }
        if (midParamInfo.isMandatory()) {
            validator = validatorFactory.getValidator(MANDATORY_MAPPING_VALIDATOR);
        } else {
            validator = validatorFactory.getValidator(OPTIONAL_MAPPING_VALIDATOR);
        }
        return isValid && validator.validate(inputs, errors, mappingToDelete);
    }

}
