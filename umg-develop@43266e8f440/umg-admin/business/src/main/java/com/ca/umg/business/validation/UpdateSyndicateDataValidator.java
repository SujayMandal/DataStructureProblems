package com.ca.umg.business.validation;

import java.util.ArrayList;
import java.util.List;

import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;
import com.ca.umg.business.tenant.entity.SystemKey;

public class UpdateSyndicateDataValidator extends SyndicateDataValidator {

    public final List<ValidationError> validateForUpdate(final SyndicateDataContainerInfo bean, SystemKey systemKey) {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        validateAnnotationsOnContainerNameAndDescription(bean, errors);
        validateAnnotationsOnIndividualKeys(bean, errors);
        validateKeys(bean.getKeyDefinitions(), errors);
        List<SyndicateDataColumnInfo> listSyndicateDataColumnInfo = bean.getMetaData();
        if (listSyndicateDataColumnInfo != null) {
            validateColumnName(listSyndicateDataColumnInfo, getColumnKeyType(systemKey), errors);
        }
        return errors;
    }

}
