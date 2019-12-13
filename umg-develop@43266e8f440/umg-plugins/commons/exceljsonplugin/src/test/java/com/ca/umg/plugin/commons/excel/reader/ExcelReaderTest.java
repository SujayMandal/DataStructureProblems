package com.ca.umg.plugin.commons.excel.reader;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.plugin.commons.excel.model.ExcelData;


@ContextHierarchy({ @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
public class ExcelReaderTest {

	@Inject
	ExcelReader excelReader;
	
	
	
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelReaderTest.class);

	private final String excelFile = "com/ca/umg/plugin/commons/excel/reader/INPUT_EXCEL.xls";
	private final String excelFileWithCorrId = "com/ca/umg/plugin/commons/excel/reader/INPUT_EXCEL_WITH_CORRID.xls";
	private final String excelTemplateFile = "com/ca/umg/plugin/commons/excel/reader/INPUT_TEMPLATE.xls";

	@Before
	public void setup() {
	}

	@Test
	public void testParseExcel() {
		InputStream inputStream = null;
		try {
			inputStream = this.getClass().getClassLoader()
					.getResourceAsStream(excelFile);
			List<Map<String, Object>> resultMap = excelReader.parseExcel(
					inputStream, "INPUT_EXCEL.xls");
			assertEquals(2, resultMap.size());
		} catch (BusinessException e) {
            LOGGER.error("message :", e);
			e.printStackTrace();
		}
		finally {
			 IOUtils.closeQuietly(inputStream);
		}
	}

	@Test
	public void testParseExcelAddCorrelationID() throws IOException, SystemException, InvalidFormatException {
		InputStream inputStream =null;
		InputStream fis = null;
		try {			
		    fis = this.getClass().getClassLoader().getResourceAsStream(excelTemplateFile);
			Workbook wb =WorkbookFactory.create(fis); 
		    inputStream = this.getClass().getClassLoader().getResourceAsStream(excelFile);
			ExcelData excelData = excelReader.parseXLData(inputStream, "INPUT_EXCEL.xls");
			assertEquals(2, excelData.getExcelData().size());
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		finally {
			if(fis !=null) {
				fis.close();
			}
			if(inputStream !=null) {
				inputStream.close();
			}
		}
	}
	
	@Test
	public void testParseExcelExistingCorrelationID() throws IOException, InvalidFormatException, SystemException {
		InputStream inputStream =null;
		InputStream fis = null;
		try {
			fis = this.getClass().getClassLoader().getResourceAsStream(excelTemplateFile);
			Workbook wb =WorkbookFactory.create(fis); 
		    inputStream = this.getClass().getClassLoader().getResourceAsStream(excelFileWithCorrId);
			ExcelData excelData = excelReader.parseXLData(inputStream, "MODIFIED_EXCEL.xls");
			assertEquals(2, excelData.getExcelData().size());
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		finally {
			if(fis !=null) {
				fis.close();
			}
			if(inputStream !=null) {
				inputStream.close();
			}
		}
	}

}
