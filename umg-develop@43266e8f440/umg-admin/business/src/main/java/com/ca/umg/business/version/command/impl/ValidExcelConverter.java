package com.ca.umg.business.version.command.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.version.command.annotation.CommandDescription;
import com.ca.umg.business.version.command.base.AbstractCommand;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.plugin.commons.excel.xmlconverter.ExceltoXmlConverter;

@Named
@Scope("prototype")
@CommandDescription(name = "validExcelConverter")
public class ValidExcelConverter extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidExcelConverter.class);
    private static final String VALID_EXCEL_CONVERT = "validExcelConverter";
    
    @Inject
    private ExceltoXmlConverter excelToXmlConverter;

    /**
     * Validates if the excel has metadata,Inputs and Outputs
     * 
     * @param sheetsMap
     * @return
     * @throws BusinessException
     */
    public byte[] convertValidExcel(VersionInfo versionInfo, Map<String, Object> excelErrors, List<Error> errors)
            throws BusinessException, IOException {

        String language = getLaguage(versionInfo);
        final ModelType modelType = ModelType.getModelType(versionInfo.getModelType());
        ModelInfo modelInfo = versionInfo.getMapping().getModel();
        byte[] xmlArray = null;
        switch (StringUtils.upperCase(StringUtils.trimToEmpty(language))) {
        case BusinessConstants.MATLAB_LANG:
            validateExcel(modelInfo, BusinessConstants.MATLAB_TEMPLATE);
            xmlArray = excelToXmlConverter.excelConvertToXml(modelInfo.getExcel().getDataArray(), excelErrors,
                    BusinessConstants.MATLAB_LANG, modelType);
            break;
        case BusinessConstants.R_LANG:
            validateExcel(modelInfo, BusinessConstants.R_TEMPLATE);
            xmlArray = excelToXmlConverter.excelConvertToXml(modelInfo.getExcel().getDataArray(), excelErrors,
                    BusinessConstants.R_LANG, modelType);
            break;
        case BusinessConstants.EXCEL_LANG:
            validateExcel(modelInfo, BusinessConstants.EXCEL_TEMPLATE);
            xmlArray = excelToXmlConverter.excelConvertToXml(modelInfo.getExcel().getDataArray(), excelErrors,
                    BusinessConstants.EXCEL_LANG, modelType);
            break;
         default :
            LOGGER.error("No language assigned");
            errors.add(new Error("No Language assigned.Language should be R/Matlab/Excel", VALID_EXCEL_CONVERT,
                    BusinessExceptionCodes.BSE000134));
        }
        return xmlArray;
    }

    private void validateExcel(ModelInfo modelInfo, String tempalte) throws BusinessException, IOException {
        InputStream templateStream = ConvertExceltoXml.class.getClassLoader().getResourceAsStream(tempalte);
        excelToXmlConverter.validateExcel(IOUtils.toByteArray(templateStream), modelInfo.getExcel()
                .getDataArray());
        if (templateStream != null) {
            templateStream.close();
        }
    }

    private String getLaguage(VersionInfo versionInfo) {
        String language;
        String languageWithVersion = versionInfo.getModelLibrary().getExecutionLanguage();
        if (StringUtils.contains(languageWithVersion, BusinessConstants.CHAR_HYPHEN)) {
            language = languageWithVersion.substring(0, languageWithVersion.indexOf(BusinessConstants.CHAR_HYPHEN));
        } else {
            language = languageWithVersion;
        }
        return language;
    }

    @Override
    public void execute(Object data) throws BusinessException, SystemException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void rollback(Object data) throws BusinessException, SystemException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isCreated() throws BusinessException, SystemException {
        // TODO Auto-generated method stub
        return false;
    }

}
