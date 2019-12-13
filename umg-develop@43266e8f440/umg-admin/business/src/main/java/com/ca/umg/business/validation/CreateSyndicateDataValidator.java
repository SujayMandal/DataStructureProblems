package com.ca.umg.business.validation;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.stereotype.Component;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.syndicatedata.entity.SyndicateData;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;
import com.ca.umg.business.tenant.entity.SystemKey;

@Component
public class CreateSyndicateDataValidator extends SyndicateDataValidator {

    private static final String NAME_FIELD = "name";

    private static final String COLUMN_FIELD = "column";

    private final DateValidator dateValidator;

    private final DataValidator dataValidator;

    public CreateSyndicateDataValidator() {
        super();
        dateValidator = new DateValidator();
        dataValidator = new DataValidator();
    }

    // TODO : previous versions should be removed
    public final List<ValidationError> validateForCreate(final SyndicateDataContainerInfo bean,
            final List<SyndicateData> allSyndicateDatas, SystemKey systemKey) throws BusinessException {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        validateContainerName(bean, allSyndicateDatas, errors);
        validateBasedOnBeanAnnotations(bean, errors);
        validateColumnDefinition(bean, errors, getColumnKeyType(systemKey));
        validateKeys(bean.getKeyDefinitions(), errors);
        dataValidator.validateData(bean, errors);
        dateValidator.validateFromAndToDates(bean, errors);
        return errors;
    }

    protected void validateColumnDefinition(SyndicateDataContainerInfo bean, List<ValidationError> errors,
            List<String> columnKeyType) {
        Set<String> columnNames = new HashSet<>();
        if (isNotEmpty(bean.getMetaData())) {
            List<SyndicateDataColumnInfo> listSyndicateDataColumnInfo = bean.getMetaData();
            validateColumnName(listSyndicateDataColumnInfo, columnKeyType, errors);
            for (SyndicateDataColumnInfo column : listSyndicateDataColumnInfo) {
                if (isDoubleAndInValidData(column)) {
                    errors.add(new ValidationError(COLUMN_FIELD,
                            "Decimal and Precision greater than Zero and less than Sixty Five."));
                    break;
                }
                if (isZero(column.getColumnSize()) && isDateOrBoolean(column)) {
                    errors.add(new ValidationError(COLUMN_FIELD, "Column size cannot be empty"));
                    break;
                }
                if (!columnNames.add(column.getDisplayName().trim())) {
                    errors.add(new ValidationError(COLUMN_FIELD, "Cannot have duplicate column names"));
                    break;
                }
            }
        }
    }

    private boolean isDoubleAndInValidData(SyndicateDataColumnInfo column) {
        boolean valid = Boolean.FALSE;
        if (column.getColumnType().equalsIgnoreCase("DOUBLE")) {
            int decimal = column.getColumnSize();
            int precision = column.getPrecision();
            if (decimal == 0 && precision == 0) {
                valid = Boolean.TRUE;
            } else if (decimal > 65 || precision > 65) {
                valid = Boolean.TRUE;
            }
        }
        return valid;
    }

    private boolean isDateOrBoolean(SyndicateDataColumnInfo column) {
        return !(column.getColumnType().equalsIgnoreCase("DATE") || column.getColumnType().equalsIgnoreCase("BOOLEAN") || column
                .getColumnType().equalsIgnoreCase("INTEGER"));
    }

    private boolean isZero(int value) {
        return value == 0;
    }

    private void validateContainerName(final SyndicateDataContainerInfo bean, final List<SyndicateData> allSyndicateDatas,
            final List<ValidationError> errors) {
        for (SyndicateData data : allSyndicateDatas) {
            if (isNameExisting(bean.getContainerName(), data)) {
                errors.add(new ValidationError(NAME_FIELD, "Syndicate data with the same container name already present."));
            }
        }
    }

    private boolean isNameExisting(final String name, final SyndicateData data) {
        return name.equalsIgnoreCase(data.getContainerName());
    }

    protected final void validateBasedOnBeanAnnotations(final SyndicateDataContainerInfo bean, final List<ValidationError> errors) {
        Set<ConstraintViolation<SyndicateDataContainerInfo>> constraintViolations = getValidator().validate(bean);
        if (isNotEmpty(constraintViolations)) {
            errors.addAll(buildMessage(constraintViolations));
        }
    }

}
