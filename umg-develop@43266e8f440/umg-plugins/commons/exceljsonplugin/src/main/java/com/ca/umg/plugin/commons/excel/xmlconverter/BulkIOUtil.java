package com.ca.umg.plugin.commons.excel.xmlconverter;

import static com.ca.framework.core.exception.BusinessException.newBusinessException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.plugin.commons.excel.converter.JsonToExcelPreparator;
import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;
import com.ca.umg.plugin.commons.excel.reader.exception.codes.ExcelPluginExceptionCodes;

public class BulkIOUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BulkIOUtil.class);

	private static final int METADATA_ROW_COUNT = 10;

	public static List<BulkIOFile> getStaticList() throws BusinessException {

		//final File file = new File(BulkIOUtil.class.getClassLoader().getResourceAsStream("files/Bulk_IO_Template.xlsx"));
		//BulkIOUtil.class.getClassLoader().getResourceAsStream(modelTemplatePath)
		final List<BulkIOFile> buildFullList = new ArrayList<BulkIOFile>();
		XSSFWorkbook workbook = null;
		InputStream input = null ;
		try {
			input = BulkIOUtil.class.getClassLoader().getResourceAsStream("locale/Bulk_IO_Template.xlsx");
			workbook = new XSSFWorkbook(input);
			final Sheet inputSheet = workbook.getSheet(ExcelConstants.OUTPUTS);	
			buildIOObjectList(inputSheet, buildFullList);
		} catch (IOException e) {
			newBusinessException(ExcelPluginExceptionCodes.EXPL000032, new String[] { "Issues With Reading the staticbulk io file" });
		} finally{
			try {
				input.close();
			} catch (IOException e) {
				newBusinessException(ExcelPluginExceptionCodes.EXPL000032, new String[] { "Issues With closing the static bulk io file" });;
			}
		}
		return buildFullList;
	}

	public static List<BulkIOFile> getBulkiIOList(Sheet inputSheet) throws BusinessException{
		List<BulkIOFile> buildFullList = new ArrayList<BulkIOFile>();
		buildIOObjectList(inputSheet, buildFullList);
		return buildFullList;
	}

	public static void buildIOObjectList(Sheet inputSheet , List<BulkIOFile> buildFullList) throws BusinessException{
		try {
			for (int j = 1; j <METADATA_ROW_COUNT; j++) {
				Row row = inputSheet.getRow(j);
				BulkIOFile bulkIOFileDTO = new BulkIOFile();
				for(int k =0 ; k < row.getLastCellNum();k++){
					String value;
					switch (k) {
					case 0:
						value = new DataFormatter().formatCellValue(row.getCell(k));
						bulkIOFileDTO.setSequence(Integer.valueOf(value));
						break;
					case 1:
						value = new DataFormatter().formatCellValue(row.getCell(k));
						bulkIOFileDTO.setApiName(value);
						break;
					case 2:
						value = new DataFormatter().formatCellValue(row.getCell(k));
						bulkIOFileDTO.setModelParamName(value);
						break;	
					case 3:
						value = new DataFormatter().formatCellValue(row.getCell(k));
						bulkIOFileDTO.setDescription(value);
						break;

					case 4:
						value = new DataFormatter().formatCellValue(row.getCell(k));
						bulkIOFileDTO.setMandatoryFlag(Boolean.valueOf(value));
						break;
					case 5:
						value = new DataFormatter().formatCellValue(row.getCell(k));
						bulkIOFileDTO.setSyndicate(Boolean.valueOf(value));
						break;
					case 6:
						value = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(row.getCell(k)));
						bulkIOFileDTO.setDataType(value);
						break;
					case 7:
						value = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(row.getCell(k)));
						bulkIOFileDTO.setNativeDataType(value);
						break;
					case 8:
						value = new DataFormatter().formatCellValue(row.getCell(k));
						bulkIOFileDTO.setLength(value);
						break;
					case 9:
						value = new DataFormatter().formatCellValue(row.getCell(k));
						bulkIOFileDTO.setPrecession(value);
						break;
					case 10:
						value = new DataFormatter().formatCellValue(row.getCell(k));
						bulkIOFileDTO.setPattern(value);
						break;
					case 11:
						value = new DataFormatter().formatCellValue(row.getCell(k));
						bulkIOFileDTO.setDimensions(value);
						break;
					case 12:
						value = new DataFormatter().formatCellValue(row.getCell(k));
						bulkIOFileDTO.setDefaultValue(value);
						break;
					default :
						newBusinessException(ExcelPluginExceptionCodes.EXPL000032, new String[] { "Issues With Reading the bulk io file" });
						break;
					}
				}
				buildFullList.add(bulkIOFileDTO);
			}
		} catch (Exception e) {
			newBusinessException(ExcelPluginExceptionCodes.EXPL000032, new String[] { "Issues With Reading the bulk io file" });
			LOGGER.error("Exception: ", e);
		}

	}
	
	public static void findErrorRootCause(List<BulkIOFile> staticList , List<BulkIOFile> currentList , List<String> errorList){
		if(staticList.size() == currentList.size()){
			for(int i=0  ; i< staticList.size(); i ++){
				int row = i + 1;
				BulkIOFile staticDto = staticList.get(i);
				BulkIOFile currentDto = currentList.get(i);

				if(staticDto.getSequence()!= currentDto.getSequence()){
					errorList.add("Incorrect Sequence for <b>"+ currentDto.getApiName() +"</b> parameter in Output definition. Please refer to Bulk_Template for correction.");  

				}
				if(!(staticDto.getApiName().equals(currentDto.getApiName()))){
					errorList.add("Incorrect name for <b>"+ currentDto.getApiName() +"</b> parameter in Output definition. Please refer to Bulk_Template for correction.");  

				}
				if(staticDto.isMandatoryFlag()!= currentDto.isMandatoryFlag()){
					errorList.add("Incorrect mandatory_flag for <b>"+ currentDto.getApiName() +"</b> parameter in Output definition. Please refer to Bulk_Template for correction.");  

				}
				if(! (staticDto.getDataType().equals( currentDto.getDataType()))){
					errorList.add("Incorrect DataType for <b>"+ currentDto.getApiName() +"</b> parameter in Output definition. Please refer to Bulk_Template for correction.");  
				}
				if(!(staticDto.getNativeDataType().equals(currentDto.getNativeDataType()))){
					errorList.add("Incorrect NativeDataType for <b>"+ currentDto.getApiName() +"</b> parameter in Output definition. Please refer to Bulk_Template for correction.");  

				}
				if(!(staticDto.getLength().equals( currentDto.getLength()))){
					errorList.add("Incorrect Length for <b>"+ currentDto.getApiName() +"</b> parameter in Output definition. Please refer to Bulk_Template for correction.");  		
				}
				if(!(staticDto.getDimensions().equals( currentDto.getDimensions()))){
					errorList.add("Incorrect Dimensions for <b>"+ currentDto.getApiName() +"</b> parameter in Output definition. Please refer to Bulk_Template for correction.");  

				}
				
				/*if(!(staticDto.getPattern().equals( currentDto.getPattern()))){
					errorList.add("Invalid bulk IO definition. Refer to output tab. Expected   " +staticDto.getPattern()+ "  in row  " +row+ "and column Pattern Received " +  currentDto.getPattern());  

				}
				if(!(staticDto.getPrecession().equals(currentDto.getPrecession()))){
					errorList.add("Invalid bulk IO definition. Refer to output tab. Expected   " +staticDto.getPrecession()+ "  in row  " +row+ "and column Precession Received " +  currentDto.getPrecession());  

				}
				if(staticDto.isSyndicate()!= currentDto.isSyndicate()){
					errorList.add("Invalid bulk IO definition. Refer to output tab. Expected   " +staticDto.isSyndicate()+ "  in row  " +row+ "and column Syndicate Received " +  currentDto.isSyndicate());  

				}*/
			}
		}
		else{
			errorList.add("Invalid bulk IO definition.Some of the row in current file is missing");
		}
	}

	public static void main (String sd[]){
		List<BulkIOFile> list =  new ArrayList<BulkIOFile>();
		List<BulkIOFile> list2 =  new ArrayList<BulkIOFile>();
		try {
			list = BulkIOUtil.getStaticList();
		} catch (BusinessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<String> errorList = new ArrayList<String>();
		File file = new File("src/main/resources/locale/temp1.xlsx");
		XSSFWorkbook workbook = null;
		InputStream input = null;
		try {
		    input = new FileInputStream(file);
			workbook = new XSSFWorkbook(input);
			//workbook = new HSSFWorkbook(is);
			Sheet inputSheet = workbook.getSheetAt(0);
			list2 = BulkIOUtil.getBulkiIOList(inputSheet);
			if(!list.equals(list2)){
				BulkIOUtil.findErrorRootCause(list, list2,errorList);
				System.out.println(errorList.get(0));
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.error("Exception: ", e);
		}
		finally {
			IOUtils.closeQuietly(input);
        }
	}	
}
