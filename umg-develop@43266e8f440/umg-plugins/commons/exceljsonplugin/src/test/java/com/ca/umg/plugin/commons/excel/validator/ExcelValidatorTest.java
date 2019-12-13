package com.ca.umg.plugin.commons.excel.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Test;

import com.ca.framework.core.exception.BusinessException;

public class ExcelValidatorTest {
	
	private final String templateExcel = "com/ca/umg/plugin/commons/excel/validator/TemplateExcel.xls";
	
	private final String templateExcel1 = "com/ca/umg/plugin/commons/excel/validator/Template1.xls";
	
	private final String missingColumnIssueExcelX = "com/ca/umg/plugin/commons/excel/validator/MissingColumnIssue.xlsx";
	
	private final String missingSheetExcel = "com/ca/umg/plugin/commons/excel/validator/MissingSheet.xls";
	
	private final String missingColumnInASheetExcel = "com/ca/umg/plugin/commons/excel/validator/MissingColumn.xls";
	
	@Test
	public void testMissingSheet() throws InvalidFormatException, IOException, BusinessException {
		InputStream templateExcelStream=null;
		InputStream missingSheetExcelStream = null;
		try {
			templateExcelStream = this.getClass().getClassLoader().getResourceAsStream(templateExcel);
	    	Workbook templateWorkBook = WorkbookFactory.create(templateExcelStream);
	        missingSheetExcelStream = this.getClass().getClassLoader().getResourceAsStream(missingSheetExcel);
	    	Workbook excelWorkBook = WorkbookFactory.create(missingSheetExcelStream);
	    	ExcelValidator xlValidator = new ExcelValidator();
	    	List<String> validationMessages = xlValidator.validateExcel(excelWorkBook, templateWorkBook);
	    	assertNotNull(validationMessages);
	    	assertEquals(2, validationMessages.size());
	    	assertTrue(validationMessages.get(0).contains("The number of the sheets in the excel is not equal to the sheets in the template"));
	    	assertEquals("Sheet : aqmkNPV is not found as per the template", validationMessages.get(1));
		}  finally {
			if(missingSheetExcelStream != null)
				IOUtils.closeQuietly(missingSheetExcelStream);
			if(templateExcelStream != null)
			    IOUtils.closeQuietly(templateExcelStream);
		}
		
	}
	
	@Test
	public void testMissingColumn() throws InvalidFormatException, IOException, BusinessException {
		InputStream templateExcelStream = null;
		InputStream missingSheetExcelStream = null;
		try {
	    templateExcelStream = this.getClass().getClassLoader().getResourceAsStream(templateExcel);
    	Workbook templateWorkBook = WorkbookFactory.create(templateExcelStream);
        missingSheetExcelStream = this.getClass().getClassLoader().getResourceAsStream(missingColumnInASheetExcel);
    	Workbook excelWorkBook = WorkbookFactory.create(missingSheetExcelStream);
    	ExcelValidator xlValidator = new ExcelValidator();
    	List<String> validationMessages = xlValidator.validateExcel(excelWorkBook, templateWorkBook);
    	assertNotNull(validationMessages);
    	assertEquals(1, validationMessages.size());
    	assertEquals("Column Header : additionalInterestStip|DOUBLE is not found in the given excel", validationMessages.get(0));
	}  finally {
		if(missingSheetExcelStream != null)
			IOUtils.closeQuietly(missingSheetExcelStream);
		if(templateExcelStream != null)
		    IOUtils.closeQuietly(templateExcelStream);
	}
	}
	
	
	@Test (expected = BusinessException.class)
	public void testEmptyWorkBooks() throws BusinessException {
		ExcelValidator xlValidator = new ExcelValidator();
    	xlValidator.validateExcel(null, null);
	}
	
	@Test
	public void testMissingColumnIssue() throws InvalidFormatException, IOException, BusinessException {
		InputStream templateExcelStream = null;
		InputStream missingSheetExcelStream = null;
		try {
	    templateExcelStream = this.getClass().getClassLoader().getResourceAsStream(templateExcel1);
    	Workbook templateWorkBook = WorkbookFactory.create(templateExcelStream);
        missingSheetExcelStream = this.getClass().getClassLoader().getResourceAsStream(missingColumnIssueExcelX);
    	Workbook excelWorkBook = WorkbookFactory.create(missingSheetExcelStream);
    	ExcelValidator xlValidator = new ExcelValidator();
    	List<String> validationMessages = xlValidator.validateExcel(excelWorkBook, templateWorkBook);
    	assertNotNull(validationMessages);
    	assertEquals(1, validationMessages.size());
    	assertEquals("Column Header : ExcelCorrelationID|STRING is not found in the given excel", validationMessages.get(0));
	}  finally {
		if(missingSheetExcelStream != null)
			IOUtils.closeQuietly(missingSheetExcelStream);
		if(templateExcelStream != null)
		    IOUtils.closeQuietly(templateExcelStream);
	}
	}

}
