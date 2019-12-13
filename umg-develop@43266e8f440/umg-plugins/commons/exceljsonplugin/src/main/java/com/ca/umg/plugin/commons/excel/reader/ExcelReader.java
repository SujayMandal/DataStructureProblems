/**
 * 
 */
package com.ca.umg.plugin.commons.excel.reader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.plugin.commons.excel.model.ExcelData;
import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;
import com.ca.umg.plugin.commons.excel.reader.exception.codes.ExcelPluginExceptionCodes;
import com.ca.umg.plugin.commons.excel.reader.exception.codes.ExceptionCodeParameters;
import com.ca.umg.plugin.commons.excel.validator.ExcelValidator;

/**
 * @author raddibas
 * 
 */
@Named
public class ExcelReader {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelReader.class);

    @Inject
    private ExcelReadHelper excelReadHelper;

    @Inject
    ReadDataSheet rdDataSheet;

    @Inject
    ReadSheet rdSheet;

    @Inject
    ReadHeaderSheet rdHeaderSheet;
    
    @Inject
    ExceptionCodeParameters exceptionCodeParameters;
    
    @Inject
    ExcelValidator excelValidator;
   

    /**
     * Parse the provided the input excel and return the list of Map of Json Object
     * 
     * @param inputExcelStream
     * @param fileName
     * @return
     * @throws BusinessException
     */
    public List<Map<String, Object>> parseExcel(InputStream inputExcelStream, String fileName) throws BusinessException {
        Map<String, Object> otherSheetsAsMap = new HashMap<>();
        List<Map<String, Object>> dataSheetAsList = new ArrayList<>();
        List<Map<String, Object>> returnList = null;
        Map<String, Object> headerMap = new HashMap<>();
        Map<String, Sheet> sheetsMap = new HashMap<>();
        exceptionCodeParameters.setExceptionCodeParams(null);
        try {
            sheetsMap = getAllSheetsFromExcel(inputExcelStream, fileName);
            if (validExcel(sheetsMap)) {
            	for (String sheetName : sheetsMap.keySet()) {
            		if (StringUtils.equalsIgnoreCase(sheetName, "Data")) {
            			dataSheetAsList = rdDataSheet.readSheet(sheetsMap.get(sheetName));
            		} else if (StringUtils.equalsIgnoreCase(sheetName, "Header")) {
            			headerMap = rdHeaderSheet.readSheet(sheetsMap.get(sheetName));
            		} else {
            			otherSheetsAsMap.put(sheetName, rdSheet.readSheet(sheetsMap.get(sheetName)));
            		}
            	}

                if (dataSheetAsList.isEmpty()) {
                    BusinessException.raiseBusinessException(ExcelPluginExceptionCodes.EXPL000003,null);
                } else if (headerMap.isEmpty()) {
                    BusinessException.raiseBusinessException(ExcelPluginExceptionCodes.EXPL000004,null);
                }

                returnList = excelReadHelper.resolveObjectReference(dataSheetAsList, otherSheetsAsMap, headerMap);
            }
        } catch (BusinessException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            throw new BusinessException(e.getCode(), exceptionCodeParameters.getExceptionCodeParamsAsArray());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            throw new BusinessException(ExcelPluginExceptionCodes.EXPL000002, null);
        }
        return returnList;
    }
    
    public ExcelData parseXLData(InputStream inputExcelStream, String fileName) throws BusinessException {
    	ExcelData excelData = new ExcelData();
    	try{
	    	try {    		
	    		ByteArrayInputStream originalBais = new ByteArrayInputStream(IOUtils.toByteArray(inputExcelStream));
	    		Workbook actualExcelBook = readExcel(originalBais, fileName);    		
	    		Map<String,Object> headerDetails = getHeaderDetails(actualExcelBook); 
	    		excelData.setHeaderDetails(headerDetails);    		
	    		excelData.setModifiedExcel(modifyExcelForCorrelation(actualExcelBook));
	    		ByteArrayInputStream bais = new ByteArrayInputStream(excelData.getModifiedExcel());
	    		List<Map<String, Object>> parsedData = parseExcel(bais, fileName);
	    		//updateParsedDataForCorrelationId(parsedData);
	    		excelData.setExcelData(parsedData);
	    		bais.close();
	        	
	        } catch (BusinessException e) {
	    		LOGGER.error(e.getLocalizedMessage(), e);
	    		excelData.setModifiedExcel(getErrorXL(e,fileName));
	    	} catch (Exception e) {
	    		LOGGER.error(e.getLocalizedMessage(), e);
	    		throw new BusinessException(ExcelPluginExceptionCodes.EXPL000021, null);
	    	} finally {
	    		if(inputExcelStream != null){
						inputExcelStream.close();
	    		}   		
	    		
	    	}
    	}catch (IOException e) {
			throw new BusinessException(ExcelPluginExceptionCodes.EXPL000002, null);
		}
    	return excelData;
    }
    
    private byte[] generateErrorXL(List<String> validationMessages,String fileName) throws BusinessException {   	
    	Workbook wb = null;
		if(fileName.endsWith(".xls")){
			wb = new HSSFWorkbook();
		}else{
			wb = new XSSFWorkbook();
		}
    	Sheet sheet = wb.createSheet(ExcelConstants.EXCEL_VALIDATION_ERROR_SHEET);
    	Row row = sheet.createRow(0);
		row.createCell(0).setCellValue(ExcelConstants.EXCEL_VALIDATION_ERROR_SHEET);
		for (int i = 1; i <= validationMessages.size(); i++) {
			row = sheet.createRow(i);
			row.createCell(0).setCellValue(validationMessages.get(i-1));
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			wb.write(baos);
		} catch (IOException e) {
			BusinessException.newBusinessException(ExcelPluginExceptionCodes.EXPL000021, new Object[]{});
		}
		return baos.toByteArray();
	}

	private Workbook readExcel(InputStream excelInputStream, String fileName) throws BusinessException {
    	Workbook workBook = null;
		try {
			if(fileName.endsWith(".xls")){
				workBook = new HSSFWorkbook(excelInputStream);
			}else{
				workBook = new XSSFWorkbook(excelInputStream);
			}
		} catch (IOException | IllegalArgumentException e) {
			LOGGER.error("Error while converting the input stream to workbook", e);
			BusinessException.newBusinessException(ExcelPluginExceptionCodes.EXPL000002, new Object[]{});
		}
		return workBook;
	}

	private byte[] getErrorXL(BusinessException exception,String fileName) throws BusinessException {
		Workbook wb = null;
		if(fileName.endsWith(".xls")){
			wb = new HSSFWorkbook();
		}else{
			wb = new XSSFWorkbook();
		}
    	Sheet sheet = wb.createSheet(ExcelConstants.EXCEL_ERROR_SHEET);
    	Row row = sheet.createRow(0);
		row.createCell(0).setCellValue(ExcelConstants.EXCEL_ERROR_SHEET);
		row = sheet.createRow(1);
		row.createCell(0).setCellValue(exception.getCode() + ":" + exception.getLocalizedMessage());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			wb.write(baos);
		} catch (IOException e) {
			BusinessException.newBusinessException(ExcelPluginExceptionCodes.EXPL000021, new Object[]{});
		}
		return baos.toByteArray();
    }
    
    private void updateParsedDataForCorrelationId(List<Map<String, Object>> parsedData) {
    	for (Map<String, Object> requestData : parsedData) {
    		Map dataMap = (Map) requestData.get("data");
    		Map headerMap = (Map) requestData.get("header");
    		//headerMap.put(ExcelConstants.CORRELATION_ID, (String) dataMap.remove(ExcelConstants.CORRELATION_ID));
		}
	}

	private byte[] modifyExcelForCorrelation(Workbook workBook) throws BusinessException {
    	/*Sheet dataSheet = getDataSheet(workBook);
    	ExcelDetailsParser excelDetailsParser = new ExcelDetailsParser();
    	ExcelDetails xlValDetails = excelDetailsParser.validateExcelForCorrelationId(dataSheet);
    	if (!xlValDetails.hasCorrelationId()) {
    		ExcelWriterHelper.generateNewCorrelationId(dataSheet);
    	}
    	if (xlValDetails.hasMissingCorrelationIds()) {
    		ExcelWriterHelper.fillMissingCorrelationIds(dataSheet, xlValDetails);
    	}
        xlValDetails = excelDetailsParser.duplicateCheck(dataSheet, xlValDetails);
    	if (xlValDetails.hasDuplicates()) {
    		ExcelWriterHelper.rectifyDuplicates(dataSheet, xlValDetails);
    	}
*/
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	try {
    		workBook.write(bos);
    	} catch (Exception excp) {
    		BusinessException.newBusinessException(ExcelPluginExceptionCodes.EXPL000021, new Object[]{});
    	}
		return bos.toByteArray();
	}
    
    private Sheet getDataSheet(Workbook workBook) {
    	Sheet dataSheet = null;
    	for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
            Sheet sheet = workBook.getSheetAt(i);
            if (sheet.getSheetName().equalsIgnoreCase("Data")) {
            	dataSheet = sheet;
            	break;
            }
        }
    	return dataSheet;
    }

    /**
     * Returns the map of all the sheets from input excel
     * 
     * @param inputExcelStream
     * @param fileName
     * @return
     * @throws BusinessException
     */
    private Map<String, Sheet> getAllSheetsFromExcel(InputStream inputExcelStream, String fileName) throws BusinessException {
        Workbook workBook = null;
        Map<String, Sheet> sheetsMap = null;
        try {
            if (FilenameUtils.getExtension(fileName).equalsIgnoreCase("xlsx")) {
                workBook = new XSSFWorkbook(inputExcelStream);
            } else {
                workBook = new HSSFWorkbook(inputExcelStream);
            }

            sheetsMap = new HashMap<>();
            for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
                Sheet sheet = workBook.getSheetAt(i);
                sheetsMap.put(sheet.getSheetName(), sheet);
            }
        } catch (IOException e) {
        	exceptionCodeParameters.setExceptionCodeParams(null);
            LOGGER.error("Error while converting the input stream to workbook", e);
            throw new BusinessException(ExcelPluginExceptionCodes.EXPL000019, null);
        }
        return sheetsMap;
    }

    /**
     * Validates if the input excel has data and header sheet
     * 
     * @param sheetsMap
     * @return
     * @throws BusinessException
     */
    private boolean validExcel(Map<String, Sheet> sheetsMap) throws BusinessException {
        boolean validExcel = false;
        exceptionCodeParameters.setExceptionCodeParams(null);
        if (!sheetsMap.containsKey("Data")) {
            throw new BusinessException(ExcelPluginExceptionCodes.EXPL000006, null);
        } else if (!sheetsMap.containsKey("Header")) {
            throw new BusinessException(ExcelPluginExceptionCodes.EXPL000007, null);
        } else {
            validExcel = true;
        }
        return validExcel;
    }    
    

	private Map<String, Object> getHeaderDetails(Workbook workBook)
			throws BusinessException {
        Map<String, Object> headerMap = null;
        try {
            for (int i = 0; i < workBook.getNumberOfSheets(); i++) {
                Sheet sheet = workBook.getSheetAt(i);
                if (StringUtils.equalsIgnoreCase(sheet.getSheetName(), "Header")) {
                    headerMap = rdHeaderSheet.readSheet(sheet);
                }
            }
            if (headerMap == null) {
                throw new BusinessException(ExcelPluginExceptionCodes.EXPL000007, null);
            }
            if (headerMap.isEmpty()) {
                throw new BusinessException(ExcelPluginExceptionCodes.EXPL000004, null);
            }
        } catch (BusinessException ex) {
            throw new BusinessException(ex.getCode(), exceptionCodeParameters.getExceptionCodeParamsAsArray());
		}
		return headerMap;
	}


    
}
