package com.ca.umg.business.validation;

import java.util.ArrayList;
import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.syndicatedata.entity.SyndicateData;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;

public class AddSyndicateDataVersionValidator extends SyndicateDataValidator {

    private static final String VERSION_NAME = "versionName";

    private final DataValidator dataValidator = new DataValidator();

    private final DateValidator dateValidator = new DateValidator();

    public final List<ValidationError> validateAddNewVersion(final SyndicateDataContainerInfo bean,
            final List<SyndicateData> previousVersions) throws BusinessException {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        validateVersionName(bean, previousVersions, errors);
        validateAnnotationsOnContainerNameAndDescription(bean, errors);
        validateAnnotationsOnIndividualKeys(bean, errors);
        validateKeys(bean.getKeyDefinitions(), errors);
        dataValidator.validateData(bean, errors);
        dateValidator.validateDates(bean, previousVersions, errors);
        return errors;
    }

    public void validateVersionName(final SyndicateDataContainerInfo bean, final List<SyndicateData> previousVersions,
            List<ValidationError> errors) {
        if (bean.getVersionName().equalsIgnoreCase(bean.getContainerName())) {
            errors.add(new ValidationError(VERSION_NAME, "Cannot have version name same as syndicate container name"));
        }
        for (SyndicateData synData : previousVersions) {
            if (bean.getVersionName().equalsIgnoreCase(synData.getVersionName())) {
                errors.add(new ValidationError(VERSION_NAME, "Cannot have duplicate version name."));
                break;
            }
        }
    }

}
