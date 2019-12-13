package com.ca.umg.business.validation;

import static com.ca.umg.business.validation.DataTypeUtils.isNotBoolean;
import static com.ca.umg.business.validation.DataTypeUtils.isNotChar;
import static com.ca.umg.business.validation.DataTypeUtils.isNotDate;
import static com.ca.umg.business.validation.DataTypeUtils.isNotInteger;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;

@Service
public class DataValidator {

    private static final String DATA_SIZE_EXCEEDS = "Data size is greater than column size defined";

    private static final String EMPTY_FIELD = "Mandatory column cannot contain empty fields";

    private static final String COLUMN_TYPE_MISMATCH = "Cell does not comply to the column type";

    private static final String DATA_FIELD = "data";

    private static final String ROW_COUNT_FIELD = "rowCount";

    private static final int TWO = 2;

    // TODO : throw system exception.
    protected final void validateData(final SyndicateDataContainerInfo bean, final List<ValidationError> errors) {
        if (bean.getTotalRows() == null || bean.getTotalRows() == 0) {
            errors.add(new ValidationError(ROW_COUNT_FIELD, "Row count cannot be empty"));
        }
        if (bean.getSyndicateVersionData() != null) {
            if (bean.getTotalRows() != bean.getSyndicateVersionData().size()) {
                errors.add(new ValidationError(ROW_COUNT_FIELD, "Row count mentioned does not match the no of rows of data"));
            }
            long rowCount = 0;
            for (Map<String, String> row : bean.getSyndicateVersionData()) {
                rowCount = rowCount + 1;
                Iterator<SyndicateDataColumnInfo> columnHeaderIterator = bean.getMetaData().iterator();
                int columnCount = 0;
                for (Entry<String, String> column : row.entrySet()) {
                    columnCount = columnCount + 1;
                    validateColumnValue(errors, rowCount, columnHeaderIterator.next(), columnCount, column.getValue().trim());
                }
            }
        }
    }

    private void validateColumnValue(final List<ValidationError> errors, long rowCount, SyndicateDataColumnInfo columnHeader,
            int columnCount, String value) {
        String columnType = columnHeader.getColumnType();
        int columnSize = columnHeader.getColumnSize();
        int precission = columnHeader.getPrecision();
        boolean isMandatory = columnHeader.isMandatory();
        String cellMessagePrefix = getCellMessagePrefix(rowCount, columnCount);

        if (isEmpty(value)) {
            if (isMandatory) {
                errors.add(new ValidationError(DATA_FIELD, cellMessagePrefix + EMPTY_FIELD));
            }
        } else {
            validateBasedOnDataTypes(errors, value, columnType, columnSize, precission, cellMessagePrefix);
        }
    }

    private void validateBasedOnDataTypes(final List<ValidationError> errors, String value, String columnType, int columnSize,
            int precission, String cellMessagePrefix) {
        if (isColumnTypeDouble(columnType)
                && isColumnTypeDoubleAndInValidData(columnSize-precission, precission, value, errors, cellMessagePrefix)) {
            errors.add(new ValidationError(DATA_FIELD, cellMessagePrefix + "Invalid Data."));
        } else if (isColumnTypeString(columnType) && value.length() > columnSize) {
            errors.add(new ValidationError(DATA_FIELD, cellMessagePrefix + DATA_SIZE_EXCEEDS));
        } else if (isExpectedIntegerButNotInteger(value, columnType)) {
            errors.add(new ValidationError(DATA_FIELD, cellMessagePrefix + COLUMN_TYPE_MISMATCH));
        } else if (isExpectedCharButNotChar(value, columnType)) {
            errors.add(new ValidationError(DATA_FIELD, cellMessagePrefix + COLUMN_TYPE_MISMATCH));
        } else if (isExpectedDateButNotDate(value, columnType)) {
            errors.add(new ValidationError(DATA_FIELD, cellMessagePrefix + COLUMN_TYPE_MISMATCH));
        } else if (isExpectedBooleanButNotBoolean(value, columnType)) {
            errors.add(new ValidationError(DATA_FIELD, cellMessagePrefix + COLUMN_TYPE_MISMATCH));
        }
    }

    private boolean isExpectedIntegerButNotInteger(String value, String columnType) {
        return isColumnTypeInteger(columnType) && isNotInteger(value);
    }

    private boolean isColumnTypeDoubleAndInValidData(int columnSize, int precission, String value,
            final List<ValidationError> errors, String cellMessagePrefix) {
        boolean validity = Boolean.FALSE;
        StringTokenizer doubleValueSplit = new StringTokenizer(value, ".");

        if (doubleValueSplit.countTokens() > TWO) {
            validity = Boolean.TRUE;
            errors.add(new ValidationError(DATA_FIELD, cellMessagePrefix + "More than One dot's (.) exist."));
        }
        String decimalValue = doubleValueSplit.nextToken();
        String precisionValue = null;
        if (doubleValueSplit.hasMoreTokens()) {
            precisionValue = doubleValueSplit.nextToken();
        }

        if (isDecimalInValid(columnSize, decimalValue) || isPrecsionInValid(precission, precisionValue)) {
            errors.add(new ValidationError(DATA_FIELD, cellMessagePrefix
                    + "Size of either decimal value or precision value invalid."));
            validity = Boolean.TRUE;
        }

        try {
            NumberUtils.createDouble(value);
        } catch (NumberFormatException e) {
            validity = Boolean.TRUE;
            errors.add(new ValidationError(DATA_FIELD, cellMessagePrefix + "Character present between data."));
        }
        return validity;
    }

    private boolean isDecimalInValid(int columnSize, String decimalValue) {
        boolean valid = Boolean.FALSE;
        
        if (columnSize < NumberUtils.INTEGER_ZERO ) {
            valid = Boolean.TRUE;
        } else if (columnSize == NumberUtils.INTEGER_ZERO && StringUtils.isNotEmpty(decimalValue)) {
            valid = Boolean.TRUE;
        } else if (columnSize > NumberUtils.INTEGER_ZERO && StringUtils.isNotEmpty(decimalValue) && decimalValue.length() > columnSize) {
            valid = Boolean.TRUE;
        }
        return valid;
    }

    private boolean isPrecsionInValid(int precission, String precisionValue) {
        boolean valid = Boolean.FALSE;
        if (precission == 0 && StringUtils.isNotEmpty(precisionValue)) {
            valid = Boolean.TRUE;
        } else if (precission > 0 && StringUtils.isNotEmpty(precisionValue) && precisionValue.length() > precission) {
            valid = Boolean.TRUE;
        }
        return valid;
    }

    private boolean isExpectedBooleanButNotBoolean(String value, String columnType) {
        return isColumnTypeBoolean(columnType) && isNotBoolean(value);
    }

    private boolean isExpectedDateButNotDate(String value, String columnType) {
        return isColumnTypeDate(columnType) && isNotDate(value);
    }

    private boolean isExpectedCharButNotChar(String value, String columnType) {
        return isColumnTypeChar(columnType) && isNotChar(value);
    }

    private boolean isColumnTypeString(String columnType) {
        return columnType != null && columnType.equalsIgnoreCase("String");
    }

    private boolean isColumnTypeDouble(String columnType) {
        return columnType != null && columnType.equalsIgnoreCase("DOUBLE");
    }

    private boolean isColumnTypeInteger(String columnType) {
        // type Number is only added for backward compatibility, should be removed later.
        return columnType != null && (columnType.equalsIgnoreCase("Integer") || columnType.equalsIgnoreCase("Number"));
    }

    private boolean isColumnTypeChar(String columnType) {
        return columnType != null && columnType.equalsIgnoreCase("Char");
    }

    private boolean isColumnTypeDate(String columnType) {
        return columnType != null && columnType.equalsIgnoreCase("Date");
    }

    private boolean isColumnTypeBoolean(String columnType) {
        return columnType != null && columnType.equalsIgnoreCase("Boolean");
    }

    private String getCellMessagePrefix(long rowCount, int columnCount) {
        return "Data in cell[" + rowCount + "][" + columnCount + "] : ";
    }
}
