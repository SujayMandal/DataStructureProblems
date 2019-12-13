package com.ca.umg.business.version.command.impl;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.mid.extraction.MidExtractor;
import com.ca.umg.business.mid.extraction.info.MidIOInfo;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.version.command.annotation.CommandDescription;
import com.ca.umg.business.version.command.base.AbstractCommand;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.business.version.info.VersionInfo;

/**
 * This class would do the following steps:</br>
 * <table BORDER CELLPADDING=3 CELLSPACING=1>
 * <tr>
 * <td ALIGN=CENTER COLSPAN = 1><b>Create ({@link #execute(Object)})</b></td>
 * <td ALIGN=CENTER COLSPAN = 1><b>Rollback ({@link #rollback(Object)})</b></td>
 * </tr>
 * <tr>
 * <td>Symantic check of ModelIO.It checks for double datatype (total and fraction digits) and array dimensions</td>
 * <td>No Rollback is required.So just added log</td>
 * <tr>
 * 
 * </table>
 * 
 * @author basanaga
 * 
 */
@Named
@Scope(BusinessConstants.SCOPE_PROTOTYPE)
@CommandDescription(name = "symanticCheckModelIOXml")
public class SymanticCheckModelIOXml extends AbstractCommand {

    @Inject
    private MidExtractor midExtractor;

    private static final String SYMANTIC_CHECK_MODEL = "symanticCheckModelIOXml";
    private static final String OBJECT="object";
    private static final Logger LOGGER = LoggerFactory.getLogger(SymanticCheckModelIOXml.class);

    @Override
    public void execute(Object data) throws BusinessException, SystemException {
        List<Error> errors = new ArrayList<Error>();
        Boolean execBreak = Boolean.FALSE;
        VersionInfo versionInfo = null;
        try {
        if (checkData(errors, data, SYMANTIC_CHECK_MODEL, VersionInfo.class)) {
            versionInfo = (VersionInfo) data;
            if (checkInfo(versionInfo)) {
                ModelInfo modelInfo = versionInfo.getMapping().getModel();
                if (modelInfo.getXml() != null) {
                    setExecuted(Boolean.TRUE);
                    byte[] xmlArray = modelInfo.getXml().getDataArray();
                    MidIOInfo modelIOInfo = midExtractor.extractModelIO(new ByteArrayInputStream(xmlArray));
                    List<MidParamInfo> midInput = modelIOInfo.getMidInput();
                    for (MidParamInfo midInfo : midInput) {
                        checkForDoubleAndArray(errors, midInfo);
                    }
                    if (!errors.isEmpty()) {
                        execBreak = Boolean.TRUE;
                    }
                } else if (modelInfo.getId() == null) {
                    execBreak = Boolean.TRUE;
                    errors.add(new Error("SYSTEM ERROR :Model definitin is empty for existing model.", SYMANTIC_CHECK_MODEL, ""));
                }

            } else {
                errors.add(new Error("SYSTEM ERROR : Data provided is empty, could not proceed!", SYMANTIC_CHECK_MODEL, ""));
                execBreak = Boolean.TRUE;
            }
        } else {
            execBreak = Boolean.TRUE;
        }
        } catch (SystemException | BusinessException ex) {
            errors.add(new Error("SYSTEM ERROR : " + ex.getLocalizedMessage(), SYMANTIC_CHECK_MODEL, ex.getCode()));
            execBreak = Boolean.TRUE;

        }
        getErrorController().setErrors(errors);
        getErrorController().setExecutionBreak(execBreak);

    }

    private boolean checkInfo(VersionInfo versionInfo) {
        return versionInfo != null && versionInfo.getMapping() != null && versionInfo.getMapping().getModel() != null;
    }

	private void checkForDoubleAndArray(List<Error> errors, MidParamInfo midInfo) {
		if (midInfo.getDatatype().isArray() || StringUtils.equals(OBJECT, midInfo.getDatatype().getType())) {
			symanticCheckForArray(errors, midInfo);
		} else if (midInfo.getDataTypeStr().contains("DOUBLE")) {
			symanticCheckForDouble(errors, midInfo);
		}
	}

    private void symanticCheckForDouble(List<Error> errors, MidParamInfo midParamInfo) {
        if (midParamInfo.getChildren() != null) {
            for (MidParamInfo midInfo : midParamInfo.getChildren()) {
                checkForDoubleAndArray(errors, midInfo);
            }
        } else {
            doubleCheck(errors, midParamInfo);
        }
    }

    private void doubleCheck(List<Error> errors, MidParamInfo midParamInfo) {
        Map<String, Object> pro = midParamInfo.getDatatype().getProperties();
        Double val = null;
        if (pro.get("defaultValue") != null) {
            try {
                val = Double.valueOf((String) pro.get("defaultValue"));
                if (val != null) {
                    doubleValueCheck(errors, val);
                }
            } catch (NumberFormatException ex) {
                errors.add(new Error("SYSTEM ERROR : Invalid input value provided for input :" + midParamInfo.getFlatenedName()
                        + ".Invalid value is :" + pro.get("defaultValue"), SYMANTIC_CHECK_MODEL, ""));

            }
        }
    }

    /**
     * @param errors
     * @param midParamInfo
     * @param val
     */
    private void doubleValueCheck(List<Error> errors, Double val) {
        String doubleVal = String.valueOf(val);
        String doubleValArr[] = doubleVal.split("\\.");

        if (doubleValArr.length > BusinessConstants.NUMBER_TWO) {
            errors.add(new Error("SYSTEM ERROR :ModelIO xml does not construct correctly.", SYMANTIC_CHECK_MODEL, ""));
        }
    }

    public void validateDouble(List<Error> errors, MidParamInfo midParamInfo, String totalDigits, String fractionDigits,
            String precision, Integer totalDigitsinVal) {
        try {
            if (totalDigits != null && totalDigitsinVal > Integer.valueOf(totalDigits)) {
                errors.add(new Error("SYSTEM ERROR : Length of input : " + midParamInfo.getFlatenedName()
                        + " value is greater than given totalDigits attribute value", SYMANTIC_CHECK_MODEL, ""));

            }
            if (fractionDigits != null && precision.length() > Integer.valueOf(fractionDigits)) {
                errors.add(new Error(
                        "SYSTEM ERROR :precision length should not greater than fractionDigits attribute value.For input "
                                + midParamInfo.getFlatenedName() + ", fractionDigits is :" + fractionDigits
                                + " and precision length is :" + precision.length(), SYMANTIC_CHECK_MODEL, ""));

            }
            if (fractionDigits != null && totalDigits != null && Integer.valueOf(fractionDigits) > Integer.valueOf(totalDigits)) {
                errors.add(new Error("SYSTEM ERROR : fractionDigits should not be greater than totalDigits.For input:"
                        + midParamInfo.getFlatenedName() + ", fractionDigits is :" + fractionDigits + " and totalDigits is :"
                        + totalDigits, SYMANTIC_CHECK_MODEL, ""));

            }
        } catch (NumberFormatException ex) {
            errors.add(new Error("SYSTEM ERROR : Invalid input value provided for input :" + midParamInfo.getFlatenedName() + "."
                    + ex.getMessage(),
                    SYMANTIC_CHECK_MODEL, ""));
            

        }
    }

    @Override
    public void rollback(Object data) throws BusinessException, SystemException {
        LOGGER.info("Rollback called for " + SYMANTIC_CHECK_MODEL + ".But No Action required");

    }

    @Override
    public boolean isCreated() throws BusinessException, SystemException {
        Boolean isCreated = Boolean.FALSE;
        if (midExtractor != null) {
            isCreated = Boolean.TRUE;
        }
        return isCreated;

    }

    private void symanticCheckForArray(List<Error> errors, MidParamInfo midParamInfo) {
        if (midParamInfo.getChildren() != null) {
            for (MidParamInfo midInfo : midParamInfo.getChildren()) {
                checkForDoubleAndArray(errors, midInfo);
            }
        } else {
            String dataType = midParamInfo.getDataTypeStr();
            if (dataType != null) {
                String[] dimensionsArray = dataType.split("\\|");
                if (dimensionsArray.length > BusinessConstants.NUMBER_TWO) {
                    String dimensions[] = dimensionsArray[2].split(BusinessConstants.CHAR_COMMA);
                    for (String dimension : dimensions) {
                        Integer dim = Integer.valueOf(dimension);
                        if (dim < BusinessConstants.NEGETIVE_NUMBER_ONE) {
                            errors.add(new Error("SYSTEM ERROR : array dimension  should not lessthan -1 . For input :"
                                    + midParamInfo.getFlatenedName() + ",One of the dim is :" + dim, SYMANTIC_CHECK_MODEL, ""));
                        }

                    }

                }

            }
        }

    }

}
