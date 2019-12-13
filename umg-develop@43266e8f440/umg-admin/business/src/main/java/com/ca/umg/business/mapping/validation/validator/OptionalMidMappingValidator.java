/**
 * 
 */
package com.ca.umg.business.mapping.validation.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.ca.umg.business.mapping.validation.core.AbstractValidator;
import com.ca.umg.business.mapping.validation.core.MappingValidatorConstants;
import com.ca.umg.business.mapping.validation.core.MappingValidatorContainer;
import com.ca.umg.business.mid.extraction.info.MidMapping;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.validation.ValidationError;

/**
 * @author mahantat
 * 
 */
@Component(value = MappingValidatorConstants.OPT_MID_MAPPING_VALIDATOR)
public class OptionalMidMappingValidator extends AbstractValidator {

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.mapping.validation.core.Validator#execute(java.util .Map, java.util.List)
     */
    @Override
    public boolean execute(MappingValidatorContainer inputs, List<ValidationError> errors, Map<String,List<String>> mappingToDelete) {
        boolean isValid = true;
        List<ValidationError> optionalValErrors = new ArrayList<>();
        MidMapping midMapping = inputs.getMidMapping();
        if (CollectionUtils.isNotEmpty(midMapping.getInputs())) {
            TidParamInfo tidParamInfo = inputs.getTidParamInfoMap().get(midMapping.getInputs().get(0));
            if (inputs.isMandatory(tidParamInfo)) {
                isAssociatedWithMandatoryMidParam(inputs, optionalValErrors, tidParamInfo);
            }
        }
        if (CollectionUtils.isNotEmpty(optionalValErrors)) {
            isValid = false;
            errors.addAll(optionalValErrors);
        }
        return isValid;
    }

    private void isAssociatedWithMandatoryMidParam(MappingValidatorContainer inputs, List<ValidationError> errors,
            TidParamInfo tidParamInfo) {
        MidMapping midMapping = inputs.getMidMapping();
        if (!inputs.isAssociatedWithMandatoryMidParam(tidParamInfo)) {
            errors.add(new ValidationError(midMapping.getMappedTo(), MappingValidatorConstants.TID_OPTIONAL_ERROR_MSG));
        }
    }

}
