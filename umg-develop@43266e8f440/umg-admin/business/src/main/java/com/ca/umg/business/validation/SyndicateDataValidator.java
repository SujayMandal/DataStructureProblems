package com.ca.umg.business.validation;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang3.StringUtils;

import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataKeyColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataKeyInfo;
import com.ca.umg.business.tenant.entity.SystemKey;

public class SyndicateDataValidator {

    private static final String KEYS_FIELD = "keys";

    private final Validator validator;

    public SyndicateDataValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    protected final void validateKeys(final List<SyndicateDataKeyInfo> keys, final List<ValidationError> errors) {
        Set<String> indexes = new HashSet<>();
        Set<String> indexColumns = new HashSet<>();
        List<String> columns = null;
        if (isNotEmpty(keys)) {
            for (SyndicateDataKeyInfo dataKeyInfo : keys) {
                if (dataKeyInfo.getKeyName() != null && !indexes.add(dataKeyInfo.getKeyName().toUpperCase())) {
                    errors.add(new ValidationError(KEYS_FIELD, "Cannot have duplicate keys : " + dataKeyInfo.getKeyName()));
                    break;
                }
                columns = buildColumns(dataKeyInfo);
                if (columns.isEmpty()) {
                    errors.add(new ValidationError(KEYS_FIELD, "No columns mapped for key : " + dataKeyInfo.getKeyName()));
                }
                if (!indexColumns.add(StringUtils.join(columns.toArray()))) {
                    errors.add(new ValidationError(KEYS_FIELD, "Cannot have same columns for different keys : " + dataKeyInfo.getKeyName()));
                    break;
                }
            }
        }
    }
    
    public List<ValidationError> validateKeys(final List<SyndicateDataKeyInfo> keys) {
    	List<ValidationError> errors = new ArrayList<ValidationError>();
    	validateKeys(keys, errors);
    	return errors;
    }

    private List<String> buildColumns(SyndicateDataKeyInfo dataKeyInfo) {
        List<String> columns = new ArrayList<>();
        if (isNotEmpty(dataKeyInfo.getsColumnInfos())) {
            for (SyndicateDataKeyColumnInfo info : dataKeyInfo.getsColumnInfos()) {
                if (info.isStatus()) {
                    columns.add(info.getColumnName());
                }
            }
        }
        return columns;
    }

    public final <T> List<ValidationError> buildMessage(final Set<ConstraintViolation<T>> constraintViolations) {
        Iterator<ConstraintViolation<T>> it = constraintViolations.iterator();
        List<ValidationError> errors = new ArrayList<ValidationError>();
        while (it.hasNext()) {
            ConstraintViolation<T> constraintViolation = it.next();
            errors.add(new ValidationError(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage()));
        }
        return errors;
    }

    protected void validateAnnotationsOnContainerNameAndDescription(SyndicateDataContainerInfo bean, List<ValidationError> errors) {
        // We are targeting individual properties as we should not validate all properties in case of update operation.
        Set<ConstraintViolation<SyndicateDataContainerInfo>> violations = getValidator().validateProperty(bean, "containerName");
        violations.addAll(getValidator().validateProperty(bean, "description"));
        errors.addAll(buildMessage(violations));
    }

    protected void validateAnnotationsOnIndividualKeys(SyndicateDataContainerInfo bean, List<ValidationError> errors) {
        List<SyndicateDataKeyInfo> keys = bean.getKeyDefinitions();
        if (isNotEmpty(keys)) {
            for (SyndicateDataKeyInfo key : keys) {
                errors.addAll(buildMessage(getValidator().validate(key)));
            }
        }
    }

    public Validator getValidator() {
        return validator;
    }

    /**
     * 
     * @param column
     * @param columnKeyType
     * @return
     */
    private boolean isColumnNameNotValid(SyndicateDataColumnInfo column, List<String> columnKeyType) {
        boolean valid = Boolean.FALSE;
        if (column != null) {
            String columnName = column.getDisplayName();
            if (StringUtils.containsWhitespace(columnName)) {
                valid = Boolean.TRUE;
            } else if (columnName != null && columnKeyType.contains(columnName.toUpperCase(Locale.ENGLISH))) {
                valid = Boolean.TRUE;
            }
        }
        return valid;
    }

    /**
     * 
     * @param systemKey
     * @return
     */
    public List<String> getColumnKeyType(SystemKey systemKey) {
        String keyType = systemKey == null ? BusinessConstants.EMPTY_STRING : systemKey.getType();
        return keyType == null ? new ArrayList<String>() : Arrays.asList(keyType.split(BusinessConstants.CHAR_COMMA));
    }

    /**
     * 
     * @param column
     * @param columnKeyType
     * @param errors
     */
    public void validateColumnName(List<SyndicateDataColumnInfo> listSyndicateDataColumnInfo, List<String> columnKeyType,
            List<ValidationError> errors) {
        for (SyndicateDataColumnInfo column : listSyndicateDataColumnInfo) {
            if (isColumnNameNotValid(column, columnKeyType)) {
                errors.add(new ValidationError("column", "InValid Column Name '" + column.getDisplayName() + "' defined"));
            }
        }
    }
}
