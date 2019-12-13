package com.ca.umg.business.mapping.validation.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ca.umg.business.mapping.validation.core.AbstractValidator;
import com.ca.umg.business.mapping.validation.core.MappingValidatorConstants;
import com.ca.umg.business.mapping.validation.core.MappingValidatorContainer;
import com.ca.umg.business.mid.extraction.info.MidMapping;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.validation.ValidationError;

@Component(value = MappingValidatorConstants.NULL_VALIDATOR)
public class NullValidator extends AbstractValidator {

    @Override
    public boolean execute(MappingValidatorContainer inputs, List<ValidationError> errors, Map<String,List<String>> mappingToDelete) {
        boolean isValid = true;
        List<ValidationError> nullErrors = new ArrayList<>();
        this.validateMidParamInfo(inputs, nullErrors);
        this.validateTidParameterInfo(inputs, nullErrors);
        if (CollectionUtils.isNotEmpty(nullErrors)) {
            isValid = false;
            errors.addAll(nullErrors);
        }
        return isValid;
    }

    private void validateTidParameterInfo(MappingValidatorContainer inputs, List<ValidationError> errors) {
        MidMapping midMapping = inputs.getMidMapping();
        List<String> mappedTidList = midMapping.getInputs();
        if (CollectionUtils.isNotEmpty(mappedTidList)) {
            for (String tidName : mappedTidList) {
                validateTidParameterInfo(inputs, errors, tidName);
            }
        }
    }

    private void validateTidParameterInfo(MappingValidatorContainer inputs, List<ValidationError> errors, String tidName) {
        MidMapping midMapping = inputs.getMidMapping();
        if (StringUtils.isEmpty(tidName)) {
            errors.add(new ValidationError(midMapping.getMappedTo(), MappingValidatorConstants.TID_MAPPING_NOT_FOUND));
        } else {
            TidParamInfo tidParamInfo = inputs.getTidParamInfoMap().get(tidName);
            if (tidParamInfo == null) {
                errors.add(new ValidationError(midMapping.getMappedTo(), MappingValidatorConstants.TID_MAPPING_NOT_FOUND_IN_MAP));
            } else {
                if (tidParamInfo.getDatatype() == null) {
                    errors.add(new ValidationError(midMapping.getMappedTo(),
                            MappingValidatorConstants.TID_MAPPING_DATATYPE_NOT_FOUND));
                }
            }
        }
    }

    private void validateMidParamInfo(MappingValidatorContainer inputs, List<ValidationError> errors) {
        MidMapping midMapping = inputs.getMidMapping();
        if (StringUtils.isEmpty(midMapping.getMappedTo())) {
            errors.add(new ValidationError("", MappingValidatorConstants.MID_MAPPING_NOT_FOUND));
        } else {
            MidParamInfo midParamInfo = inputs.getMidParamInfoMap().get(midMapping.getMappedTo());
            if (midParamInfo == null) {
                errors.add(new ValidationError(midMapping.getMappedTo(), MappingValidatorConstants.MID_MAPPING_NOT_FOUND_IN_MAP));
            } else {
                if (midParamInfo.getDatatype() == null) {
                    errors.add(new ValidationError(midMapping.getMappedTo(),
                            MappingValidatorConstants.MID_MAPPING_DATATYPE_NOT_FOUND));
                }
            }
        }
    }

}
