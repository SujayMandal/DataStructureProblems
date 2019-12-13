package com.ca.umg.business.mapping.validation;

import java.util.List;
import java.util.Map;

import com.ca.umg.business.mapping.validation.core.MappingValidatorContainer;
import com.ca.umg.business.mid.extraction.info.MidIOInfo;
import com.ca.umg.business.mid.extraction.info.MidMapping;
import com.ca.umg.business.mid.extraction.info.TidIOInfo;
import com.ca.umg.business.validation.ValidationError;

public interface MappingValidator {

    public List<ValidationError> validateMidMapping(MidIOInfo midIOInfo, TidIOInfo tidIOInfo, List<MidMapping> midInputMappings,
    		Map<String,List<String>> mappingToDelete);

    public List<ValidationError> validateMidMapping(MidIOInfo midIOInfo, TidIOInfo ioInfo, List<MidMapping> midInputMappings,
            List<String> inputValidatorList, Map<String,List<String>>  mappingToDelete);

    public MappingValidatorContainer prepareValidatorContainer(MidIOInfo midIOInfo, TidIOInfo ioInfo,
            List<MidMapping> midInputMappings, List<ValidationError> validationErrors);

}
