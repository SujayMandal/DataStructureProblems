package com.ca.umg.business.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.syndicatedata.entity.SyndicateData;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;

public class UpdateVersionActiveDuration extends SyndicateDataValidator {

    private final DateValidator dateValidator = new DateValidator();

    public final List<ValidationError> validateForUpdate(final SyndicateDataContainerInfo bean,
            final List<SyndicateData> previousVersions) throws BusinessException {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        validateOldAndNewValues(bean.getOldValidFrom(), bean.getValidFrom(), errors, "Active From");
        validateOldAndNewValues(bean.getOldValidTo(), bean.getValidTo(), errors, "Active Until");
        if (CollectionUtils.isEmpty(errors)) {
            dateValidator.validateDates(bean, previousVersions, errors);
        }
        validateKeys(bean.getKeyDefinitions(), errors);
        return errors;
    }

    private void validateOldAndNewValues(Long oldTime, Long newTime, List<ValidationError> errors, String field) {
        if (oldTime != null && oldTime.longValue() != newTime.longValue() && oldTime < System.currentTimeMillis()) {
            errors.add(new ValidationError(field, "Current '" + field + "' date is in past.Hence can not be updated."));
        }
    }
}
