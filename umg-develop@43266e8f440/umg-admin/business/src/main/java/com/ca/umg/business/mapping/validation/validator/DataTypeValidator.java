package com.ca.umg.business.mapping.validation.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ca.umg.business.mapping.validation.core.AbstractValidator;
import com.ca.umg.business.mapping.validation.core.MappingValidatorConstants;
import com.ca.umg.business.mapping.validation.core.MappingValidatorContainer;
import com.ca.umg.business.mid.extraction.info.DatatypeInfo.Datatype;
import com.ca.umg.business.mid.extraction.info.MidMapping;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.validation.ValidationError;

/**
 * 
 * @author mahantat
 * 
 */
@Component(value = MappingValidatorConstants.DATA_TYPE_VALIDATOR)
public class DataTypeValidator extends AbstractValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataTypeValidator.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.mapping.validation.core.Validator#execute(java.util .Map, java.util.List)
     */
    @Override
    public boolean execute(MappingValidatorContainer inputs, List<ValidationError> errors, Map<String,List<String>> mappingToDelete) {
        MidMapping midMapping = inputs.getMidMapping();
        MidParamInfo midParamInfo = inputs.getMidParamInfoMap().get(midMapping.getMappedTo());
        boolean isValid = true;
        List<ValidationError> datatypeValErrors = new ArrayList<>();
        for (String tidElement : midMapping.getInputs()) {
            TidParamInfo tidParamInfo = inputs.getTidParamInfoMap().get(tidElement);
            validateDatatype(inputs, datatypeValErrors, midMapping, midParamInfo, tidParamInfo);
        }
        if (CollectionUtils.isNotEmpty(datatypeValErrors)) {
            isValid = false;
            errors.addAll(datatypeValErrors);
        }
        return isValid;
    }

    private void validateDatatype(MappingValidatorContainer inputs, List<ValidationError> errors, MidMapping midMapping,
            MidParamInfo midParamInfo, TidParamInfo tidParamInfo) {

        if (midParamInfo != null && midParamInfo.getDatatype() != null && tidParamInfo != null
                && tidParamInfo.getDatatype() != null) {
            if (!midParamInfo.getDatatype().equals(tidParamInfo.getDatatype())) {
                errors.add(new ValidationError(midMapping.getMappedTo(), MappingValidatorConstants.DATATYPE_ERROR_MSG));
                LOGGER.info(" Data type does not match between ");
                LOGGER.info("Mid param info - " + midParamInfo.getFlatenedName() + " {"
                        + midParamInfo.getDatatype().getType() + "} ");
                LOGGER.info("Tid Param info - " + tidParamInfo.getFlatenedName() + " {"
                        + tidParamInfo.getDatatype().getType() + "}");
            } else if (Datatype.OBJECT.toString().equals(midParamInfo.getDatatype().getType())) {
                validateObject(inputs, errors, midMapping, midParamInfo, tidParamInfo);
            }
        } else {
            errors.add(new ValidationError(midMapping.getMappedTo(), MappingValidatorConstants.MAPPING_PARAM_NOT_FOUND));
        }

    }

    private void validateObject(MappingValidatorContainer inputs, List<ValidationError> errors, MidMapping midMapping,
            MidParamInfo midParamInfo, TidParamInfo tidParamInfo) {
        for (MidParamInfo midChild : midParamInfo.getChildren()) {
            boolean isChildFound = false;
            for (TidParamInfo tidChild : tidParamInfo.getChildren()) {
                if (midChild.getApiName().equals(tidChild.getApiName())) {
                    isChildFound = true;
                    validateDatatype(inputs, errors, midMapping, midChild, tidChild);
                    break;
                }
            }
            if (!isChildFound) {
                errors.add(new ValidationError(midMapping.getMappedTo(), MappingValidatorConstants.DATATYPE_ERROR_MSG));
            }
            if (CollectionUtils.isNotEmpty(errors)) {
                break;
            }
        }
    }

}
