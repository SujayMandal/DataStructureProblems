package com.ca.umg.business.version.command.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.version.command.annotation.CommandDescription;
import com.ca.umg.business.version.command.base.AbstractCommand;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.business.version.info.VersionInfo;

@Named
@Scope("prototype")
@CommandDescription(name = "convertExcelToXml")
public class ConvertExceltoXml extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertExceltoXml.class);
    private static final String CONVERT_EXCEL_TO_XML = "convertExcelToXml";

    @Inject
    private ValidExcelConverter validExcelConverter;

    @Override
    public void execute(Object data) throws BusinessException, SystemException {
        ModelInfo modelInfo = null;
        List<Error> errors = new ArrayList<Error>();
        Map<String, Object> excelErrors = new HashMap<String, Object>();
        boolean breakExecution = Boolean.FALSE;
        VersionInfo versionInfo = null;
        byte[] xmlArray = null;
        try {
            if (checkData(errors, data, CONVERT_EXCEL_TO_XML, VersionInfo.class)) {
                versionInfo = ((VersionInfo) data);
                modelInfo = versionInfo.getMapping().getModel();
                if (modelInfo.getExcel() != null) {
                    
                    xmlArray = validExcelConverter.convertValidExcel(versionInfo, excelErrors, errors);

                    if (xmlArray == null) {
                        breakExecution = Boolean.TRUE;
                    }

                    if (MapUtils.isNotEmpty(excelErrors)) {
                        setErrors(errors, excelErrors);                      
                        breakExecution = Boolean.TRUE;
                        
                    } else {
                        ModelArtifact modelArtifact = new ModelArtifact();
                        modelArtifact.setContentType("text/xml");
                        modelArtifact.setDataArray(xmlArray);
                        modelArtifact.setName(modelInfo.getIoDefExcelName().replace(".xlsx", ".xml"));
                        modelArtifact.setModelName(modelInfo.getIoDefExcelName().replace(".xlsx", ".xml"));
                        modelInfo.setIoDefinitionName(modelInfo.getIoDefExcelName().replace(".xlsx", ".xml"));
                        modelInfo.setIoDefExcelName(modelInfo.getIoDefExcelName());
                        modelInfo.setXml(modelArtifact);
                        setExecuted(Boolean.TRUE);
                    }

                }

            } else {
                breakExecution = Boolean.TRUE;
            }
        } catch (BusinessException ex) { // NOPMD
            breakExecution = Boolean.TRUE;
            LOGGER.error("error in CreateModel::execute method : ", ex);
            errors.add(new Error(ex.getLocalizedMessage(), CONVERT_EXCEL_TO_XML, BusinessExceptionCodes.BSE000134));
        } catch (Exception ex) { // NOPMD
            breakExecution = Boolean.TRUE;
            LOGGER.error("error in CreateModel::execute method : ", ex);
            errors.add(new Error(ex.getMessage(), CONVERT_EXCEL_TO_XML, BusinessExceptionCodes.BSE000134));
        }
        getErrorController().setErrors(errors);
        getErrorController().setExecutionBreak(breakExecution);
    }

    private void setErrors(List<Error> errors, Map<String, Object> excelErrors) {
        Set<Entry<String, Object>> excelErrorsSet = excelErrors.entrySet();
        for (Entry<String, Object> excelError : excelErrorsSet) {
            if (excelError.getValue() instanceof List) {
                Object excelErrorsList = excelError.getValue();
                for (String str : (List<String>) excelErrorsList) {
                    errors.add(new Error(str, CONVERT_EXCEL_TO_XML, excelError.getKey()));
                }
                
            }else{
            errors.add(new Error((String)excelError.getValue(), CONVERT_EXCEL_TO_XML, excelError.getKey()));
            }
        }
    }

    @Override
    public void rollback(Object data) throws BusinessException, SystemException {
        LOGGER.info("Rollback called for " + CONVERT_EXCEL_TO_XML + ".But No Action required");
    }

    @Override
    public boolean isCreated() throws BusinessException, SystemException {
        return true;
    }

}
