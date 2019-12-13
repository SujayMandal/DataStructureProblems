package com.ca.umg.plugin.commons.excel.xmlconverter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;
import com.ca.umg.plugin.commons.excel.reader.exception.codes.ExcelPluginExceptionCodes;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.UmgModel;

@Named
public class ExceltoXmlConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceltoXmlConverter.class);

    private static final int METADATA_SHEET_ROWS = 2;

    private static final int MINIMUM_REQUIRED_SHEETS = 3;

    private static final String NAME = "name";

    private static final String API_NAME = "apiName";

    private static final String MODEL_PARAMETER_NAME = "modelParameterName";

    @Inject
    private ModelExcelReaderFactory modelExcelReaderFactory;

    public byte[] excelConvertToXml(byte[] excelBytArray, Map<String, Object> errors, String language,
            final ModelType modelType) {
        byte[] xmlArray = null;
        ModelExcelReader excelReader = null;
        UmgModel model = null;
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(excelBytArray);
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            Map<String, Sheet> sheetsMap = new HashMap<>();
            List<String> validationErrors = new ArrayList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                sheetsMap.put(sheet.getSheetName(), sheet);
            }

            excelReader = modelExcelReaderFactory.getReader(language);
            excelReader.setModelType(modelType);
            if (excelReader != null) {
                model = excelReader.readSheets(sheetsMap, validationErrors);
            } else {
                BusinessException.newBusinessException(ExcelPluginExceptionCodes.EXPL000031, new Object[] {});
            }

            if (CollectionUtils.isNotEmpty(validationErrors)) {
                errors.put(ExcelPluginExceptionCodes.EXPL000025, validationErrors);
            } else {
                JAXBContext context = JAXBContext.newInstance(UmgModel.class);
                Marshaller m = context.createMarshaller();
                // for pretty-print XML in JAXB
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                m.marshal(model, os);
                xmlArray = os.toByteArray();
                os.close();
            }
        } catch (BusinessException exp) {
            errors.put(exp.getCode(), exp.getLocalizedMessage());
        } catch (JAXBException | IOException exp) {
            errors.put(ExcelPluginExceptionCodes.EXPL000025, exp.getMessage());
        }
        return xmlArray;
    }

    /**
     * Validates if the excel has metadata,Inputs and Outputs
     * 
     * @param sheetsMap
     * @return
     * @throws BusinessException
     */
    public void validateExcel(byte[] templateByteArray, byte[] actualByteArray) throws BusinessException {
        if (templateByteArray == null || actualByteArray == null) {
            BusinessException.newBusinessException(ExcelPluginExceptionCodes.EXPL000022, new Object[] {});
        }
        try {
            validateSheets(new XSSFWorkbook(new ByteArrayInputStream(actualByteArray)),
                    new XSSFWorkbook(new ByteArrayInputStream(templateByteArray)));
        } catch (IOException e) {
            LOGGER.error("Unable to parse excel files. " + e);
            BusinessException.newBusinessException(ExcelPluginExceptionCodes.EXPL000002, new Object[] {});
        }

    }

    private void validateSheets(Workbook actualExcelBook, Workbook templateExcelBook) throws BusinessException {
        if (actualExcelBook.getNumberOfSheets() < MINIMUM_REQUIRED_SHEETS) {
            BusinessException.newBusinessException(ExcelPluginExceptionCodes.EXPL000027, new Object[] {});
        }
        Map<String, Sheet> actualExcelSheetMap = getAllSheetsFromWorkbook(actualExcelBook);
        Map<String, Sheet> templateExcelSheetMap = getAllSheetsFromWorkbook(templateExcelBook);
        validateSheetNames(actualExcelSheetMap, templateExcelSheetMap);
    }

    private void validateSheetNames(Map<String, Sheet> actualExcelSheetMap, Map<String, Sheet> templateExcelSheetMap)
            throws BusinessException {
        List<String> expectedTabs = new ArrayList<>();
        expectedTabs.add("metadata");
        expectedTabs.add(ExcelConstants.INPUTS);
        expectedTabs.add(ExcelConstants.OUTPUTS);

        for (String tab : expectedTabs) {
            if (actualExcelSheetMap.containsKey(tab)) {
                validateSheetColumns(actualExcelSheetMap.get(tab), templateExcelSheetMap.get(tab));
                if (tab.equalsIgnoreCase("metadata")
                        && actualExcelSheetMap.get(tab).getPhysicalNumberOfRows() != METADATA_SHEET_ROWS) {
                    BusinessException.newBusinessException(ExcelPluginExceptionCodes.EXPL000030, new Object[] {});
                }
            } else {
                LOGGER.error("Sheet : " + tab + " is not found as per the template");
                BusinessException.newBusinessException(ExcelPluginExceptionCodes.EXPL000028, new Object[] { tab });
            }
        }
    }

    private void validateSheetColumns(Sheet actualSheet, Sheet templateSheet) throws BusinessException {
        Row actualRow = actualSheet.getRow(0);
        Row templateRow = templateSheet.getRow(0);

        for (int i = 0; i < templateRow.getLastCellNum(); i++) {
            String templateCellValue = new DataFormatter().formatCellValue(templateRow.getCell(i)).trim();
            String actualCellValue = new DataFormatter().formatCellValue(actualRow.getCell(i)).trim();

            if (!StringUtils.equals(templateCellValue, actualCellValue)) {
                if (StringUtils.equals(templateCellValue, API_NAME)
                        || StringUtils.equals(templateCellValue, MODEL_PARAMETER_NAME)) {
                    LOGGER.error(
                            "Column Header : " + templateCellValue + " is not found in Sheet : " + actualSheet.getSheetName());
                    BusinessException.newBusinessException(ExcelPluginExceptionCodes.EXPL000033,
                            new Object[] { MODEL_PARAMETER_NAME, NAME, API_NAME });
                } else {
                    LOGGER.error(
                            "Column Header : " + templateCellValue + " is not found in Sheet : " + actualSheet.getSheetName());
                    BusinessException.newBusinessException(ExcelPluginExceptionCodes.EXPL000029,
                            new Object[] { templateCellValue, actualSheet.getSheetName(), i });
                }
            }
        }
    }

    private static Map<String, Sheet> getAllSheetsFromWorkbook(Workbook workBook) {
        Map<String, Sheet> sheetsMap = new HashMap<>();
        for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
            Sheet sheet = workBook.getSheetAt(i);
            sheetsMap.put(sheet.getSheetName(), sheet);
        }
        return sheetsMap;
    }

}
