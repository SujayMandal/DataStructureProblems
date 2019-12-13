package com.ca.umg.plugin.commons.excel.xmlconverter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.UmgModel;

@ContextHierarchy({ @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
public class ExceltoXmlConverterTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExceltoXmlConverterTest.class);
  	
	@Inject
	private MatlabModelExcelReader matlabModelExcelReader;
	
	@Inject
	private RModelExcelReader rModelExcelReader;
	
    @Inject
    private ExceltoXmlConverter exceltoXmlConverter;

    private final String matlabTemplateFile = "Matlab_Template.xlsx";
    private final String fileWithMissingSheet = "Matlab_HAMPV6_Missing_Sheet.xlsx";
    private final String inputExcelFile = "Matlab_HAMPV6_Ver_0.2.xlsx";
    private final String inputExcelFile_sucess = "Matlab_HAMPV6_Ver_0.2_Success.xlsx";
    private final String inputExcelFile_exception = "Matlab_HAMPV6_Ver_0.2_Failure.xlsx";
    private final String rInputExcelFile = "R_testumg.xlsx";
    private final String rInputExcelFileFailure = "R_testumg_failure.xlsx";

    @Before
    public void setup() {
    }

    @Test
    @Ignore
    public void testExcelConvertToXml() {
    	InputStream is = null;
        try {
            is = ExceltoXmlConverterTest.class.getClassLoader().getResourceAsStream(inputExcelFile_sucess);
            Map<String, Object> errors = new HashMap<String, Object>();
            byte[] xmlArray = exceltoXmlConverter.excelConvertToXml(IOUtils.toByteArray(is), errors, "MATLAB", ModelType.ONLINE);
            Assert.assertTrue(errors.size() == 0);
            String s = new String(xmlArray);
            Assert.assertTrue(s != null);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
        	IOUtils.closeQuietly(is);
		}

    }
    

  @Test
    public void testExcelConvertToXmlWithFailures() {
	  InputStream is = null;
        try {
            is = ExceltoXmlConverterTest.class.getClassLoader().getResourceAsStream(inputExcelFile);
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            Map<String, Sheet> sheetsMap = new HashMap<>();
            List<String> validationErrors = new ArrayList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                sheetsMap.put(sheet.getSheetName(), sheet);
            }
            UmgModel model = matlabModelExcelReader.readSheets(sheetsMap, validationErrors);
            Assert.assertTrue(9 == validationErrors.size());
        } catch (BusinessException exp) {
            System.out.println(exp.getLocalizedMessage());
        } catch (Exception e) {
        	LOGGER.error("Exception: ", e);

        }
        finally {
        	IOUtils.closeQuietly(is);
		}
    }
  
  @Test
  public void testRExcelConvertToXmlSuccess() {
	  InputStream is = null;
      try {
          is = ExceltoXmlConverterTest.class.getClassLoader().getResourceAsStream(rInputExcelFile);
          XSSFWorkbook workbook = new XSSFWorkbook(is);
          Map<String, Sheet> sheetsMap = new HashMap<>();
          List<String> validationErrors = new ArrayList<>();
          for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
              Sheet sheet = workbook.getSheetAt(i);
              sheetsMap.put(sheet.getSheetName(), sheet);
          }
          UmgModel model = rModelExcelReader.readSheets(sheetsMap, validationErrors);
            Assert.assertEquals(25, validationErrors.size());
      } catch (BusinessException exp) {
          System.out.println(exp.getLocalizedMessage());
      } catch (Exception e) {
    	  LOGGER.error("Exception: ", e);

      }
      finally {
    	  IOUtils.closeQuietly(is);
		}
  }
  
  @Test
  public void testRExcelConvertToXmlFailure() {
	  InputStream is = null;
      try {
          is = ExceltoXmlConverterTest.class.getClassLoader().getResourceAsStream(rInputExcelFileFailure);
          XSSFWorkbook workbook = new XSSFWorkbook(is);
          Map<String, Sheet> sheetsMap = new HashMap<>();
          List<String> validationErrors = new ArrayList<>();
          for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
              Sheet sheet = workbook.getSheetAt(i);
              sheetsMap.put(sheet.getSheetName(), sheet);
          }
          UmgModel model = rModelExcelReader.readSheets(sheetsMap, validationErrors);
            Assert.assertEquals(50, validationErrors.size());
      } catch (BusinessException exp) {
          System.out.println(exp.getLocalizedMessage());
      } catch (Exception e) {
    	  LOGGER.error("Exception: ", e);

      }
      finally {
    	  IOUtils.closeQuietly(is);
		}
  }

    @Test
    public void testExcelConvertToXmlWithException() {
    	InputStream is = null;
        try {
            is = ExceltoXmlConverterTest.class.getClassLoader().getResourceAsStream(inputExcelFile_exception);
            Map<String,Object> errors = new HashMap<String,Object>();
            exceltoXmlConverter.excelConvertToXml(IOUtils.toByteArray(is), errors, "MATLAB", ModelType.ONLINE);
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            Map<String, Sheet> sheetsMap = new HashMap<>();
            List<String> validationErrors = new ArrayList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                sheetsMap.put(sheet.getSheetName(), sheet);
            }
            UmgModel model = matlabModelExcelReader.readSheets(sheetsMap, validationErrors);
            Assert.assertTrue(9 == validationErrors.size());
        } catch (BusinessException exp) {
            System.out.println(exp.getLocalizedMessage());
        } catch (Exception e) {
        	LOGGER.error("Exception: ", e);

        }
        finally {
        	IOUtils.closeQuietly(is);
		}
    }
    
    @Test
    public void testValidateExcel(){
    	InputStream tempIs = null;
    	InputStream actualIs = null;
    	try {
    		tempIs = ExceltoXmlConverterTest.class.getClassLoader().getResourceAsStream(matlabTemplateFile);
    		actualIs = ExceltoXmlConverterTest.class.getClassLoader().getResourceAsStream(inputExcelFile_sucess);
			byte[] templateByteArray = IOUtils.toByteArray(tempIs);
			byte[] actualByteArray = IOUtils.toByteArray(actualIs);
			exceltoXmlConverter.validateExcel(templateByteArray, actualByteArray);
		} catch (IOException | BusinessException e) {
			LOGGER.error("Exception: ", e);
		}
    	finally {
    		closeResources(tempIs, actualIs);
		}
    }
    
    @Test (expected = BusinessException.class)
    public void testValidateExcelWithMissingSheet() throws BusinessException{
    	InputStream tempIs = null;
    	InputStream actualIs = null;
    	try {
    		tempIs = ExceltoXmlConverterTest.class.getClassLoader().getResourceAsStream(matlabTemplateFile);
    		actualIs = ExceltoXmlConverterTest.class.getClassLoader().getResourceAsStream(fileWithMissingSheet);
			byte[] templateByteArray = IOUtils.toByteArray(tempIs);
			byte[] actualByteArray = IOUtils.toByteArray(actualIs);
			exceltoXmlConverter.validateExcel(templateByteArray, actualByteArray);
		} catch (IOException e) {
			LOGGER.error("IOException: ", e);
		}
    	finally {
    		closeResources(tempIs, actualIs);
		}
    }

	private void closeResources(InputStream tempIs, InputStream actualIs) {
		IOUtils.closeQuietly(tempIs);
		IOUtils.closeQuietly(actualIs);
	}

}
            


