package com.ca.umg.plugin.commons.excel.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.plugin.commons.excel.reader.exception.codes.ExcelPluginExceptionCodes;

@Named
public class ExcelValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelValidator.class);

    public List<String> validateExcel(Workbook actualExcelBook, Workbook templateExcelBook) throws BusinessException {
        if (actualExcelBook == null || templateExcelBook == null) {
            BusinessException.newBusinessException(ExcelPluginExceptionCodes.EXPL000022, new Object[] {});
        }
        List<String> validationMessages = new ArrayList<String>();
        validateSheets(actualExcelBook, templateExcelBook, validationMessages);
        return validationMessages;
    }

    private void validateSheets(Workbook actualExcelBook, Workbook templateExcelBook, List<String> validationMessages) {
        if (actualExcelBook.getNumberOfSheets() != templateExcelBook.getNumberOfSheets()) {
            validationMessages
                    .add("The number of the sheets in the excel is not equal to the sheets in the template.Numbers of sheets in template and actual excel files are "
                            + templateExcelBook.getNumberOfSheets() + " and " + actualExcelBook.getNumberOfSheets()
                            + " respectively.");
            if (LOGGER.isInfoEnabled()) {
                for (int i = 0; i < actualExcelBook.getNumberOfSheets(); i++) {
                    Sheet sheet = actualExcelBook.getSheetAt(i);
                    LOGGER.info("Actual Excel work book sheet " + sheet.getSheetName());
                }

                for (int i = 0; i < templateExcelBook.getNumberOfSheets(); i++) {
                    Sheet sheet = templateExcelBook.getSheetAt(i);
                    LOGGER.info("Template Excel work book sheet " + sheet.getSheetName());
                }
            }
        }
        Map<String, Sheet> actualExcelSheetMap = getAllSheetsFromWorkbook(actualExcelBook);
        Map<String, Sheet> templateExcelSheetMap = getAllSheetsFromWorkbook(templateExcelBook);
        validateSheetNames(actualExcelSheetMap, templateExcelSheetMap, validationMessages);
    }

    private void validateSheetNames(Map<String, Sheet> actualExcelSheetMap, Map<String, Sheet> templateExcelSheetMap,
            List<String> validationMessages) {
        Iterator<String> templateExcelSheetItr = templateExcelSheetMap.keySet().iterator();
        while (templateExcelSheetItr.hasNext()) {
            String templateSheetName = templateExcelSheetItr.next();
            Iterator<String> actualExcelSheetItr = actualExcelSheetMap.keySet().iterator();
            boolean sheetFound = false;
            Sheet foundSheet = null;
            while (actualExcelSheetItr.hasNext()) {
                String actualSheetName = actualExcelSheetItr.next();
                if (StringUtils.equalsIgnoreCase(templateSheetName, actualSheetName)) {
                    sheetFound = true;
                    foundSheet = actualExcelSheetMap.get(actualSheetName);
                    break;
                }
            }
            if (!sheetFound) {
                validationMessages.add("Sheet : " + templateSheetName + " is not found as per the template");
            } else {
                validateSheetColumns(foundSheet, templateExcelSheetMap.get(templateSheetName), validationMessages);
            }
        }
    }

    private void validateSheetColumns(Sheet actualSheet, Sheet templateSheet, List<String> validationMessages) {
        Row actualRow = actualSheet.getRow(0);
        Row templateRow = templateSheet.getRow(0);
        Iterator<Cell> templateCellItr = templateRow.cellIterator();
        while (templateCellItr.hasNext()) {
            Cell templateCell = templateCellItr.next();
            templateCell.setCellType(Cell.CELL_TYPE_STRING);
            String templateCellValue = templateCell.getStringCellValue().trim();
            Iterator<Cell> actualCellItr = actualRow.cellIterator();
            boolean foundColumn = false;
            while (actualCellItr.hasNext()) {
                Cell actualCell = actualCellItr.next();
                actualCell.setCellType(Cell.CELL_TYPE_STRING);
                String actualCellValue = actualCell.getStringCellValue().trim();
                if (StringUtils.equalsIgnoreCase(actualCellValue, templateCellValue)) {
                    foundColumn = true;
                    break;
                }
            }
            if (!foundColumn) {
                validationMessages.add("Column Header : " + templateCellValue + " is not found in the given excel");
            }
        }
    }

    private Map<String, Sheet> getAllSheetsFromWorkbook(Workbook workBook) {
        Map<String, Sheet> sheetsMap = new HashMap<>();
        for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
            Sheet sheet = workBook.getSheetAt(i);
            sheetsMap.put(sheet.getSheetName(), sheet);
        }
        return sheetsMap;
    }

    // private void validateLinks(Workbook actualExcelBook, List<String> validationMessages) {
    // Map<String, Sheet> actualExcelSheetMap = getAllSheetsFromWorkbook(actualExcelBook);
    // Iterator<String> actualExcelSheetItr = actualExcelSheetMap.keySet().iterator();
    // while (actualExcelSheetItr.hasNext()) {
    // String actualSheetName = actualExcelSheetItr.next();
    // if (!StringUtils.containsIgnoreCase(actualSheetName, "header")) {
    // Sheet sheet = actualExcelSheetMap.get(actualSheetName);
    // Row headerRow = sheet.getRow(0);
    // Iterator<Cell> cellItr = headerRow.cellIterator();
    // while (cellItr.hasNext()) {
    // Cell cell = cellItr.next();
    // cell.setCellType(Cell.CELL_TYPE_STRING);
    // String cellValue = cell.getStringCellValue().trim();
    // if (StringUtils.containsIgnoreCase(cellValue, "Object")) {
    // String derivedSheetName = cellValue.substring(0, cellValue.indexOf("|"));
    // if (!actualExcelSheetMap.containsKey(derivedSheetName)) {
    // validationMessages.add("The sheet : " + derivedSheetName + " is not present in the excel. The reference is found in " +
    // actualSheetName);
    // }
    //
    // }
    // }
    // }
    // }
    // }

}