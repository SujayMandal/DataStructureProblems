package com.ca.umg.business.tenant.report.model.util;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.mapping.info.MappingDescriptor;
import com.ca.umg.business.mid.extraction.info.DatatypeInfo.Datatype;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.plugin.commons.excel.reader.ReadHeaderSheet;


@SuppressWarnings("PMD")
public class JsonToExcelConverterUtil2 {
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonToExcelConverterUtil2.class);

	private static final String ID = "ID";

	private static final String PIPE = "|";
	private static final String PIPE_REG = "[|]";

	private static final String OBJECT = Datatype.OBJECT.getDatatype();

	private static final String STRING = Datatype.STRING.getDatatype();

	private static final String OBJECT_ARRAY = Datatype.OBJECT.getDatatype() + "|ARRAY";

    private static final String NULL = "null";

	public static Workbook createTenantInputWorkbook(final MappingDescriptor mappingDescription, final Map<String, Object> tenantJson)
			throws BusinessException, SystemException {
		Workbook wb = createWorkbookWithHeadersForInput(mappingDescription);

		for (Object key : tenantJson.keySet()) {
			if (key != null) {
				Sheet sheet = wb.getSheet(key.toString());
				populateSheetData(sheet, tenantJson.get(key));
			}
		}

		Sheet dataSheet = wb.getSheet("Data");
		Sheet headerSheet = wb.getSheet("Header");
		if(dataSheet != null && headerSheet != null ) {
			setTransactionIdInDataSheet(dataSheet, headerSheet, 1);
		}
		return wb;
	}

	private static int buildExcel(final TidParamInfo tidParamInfo, final Row row, final int cellCount) {
		int cllcnt = cellCount;
		if(StringUtils.isNotEmpty(tidParamInfo.getName())){
			tidParamInfo.setApiName(tidParamInfo.getName());
		}
		if (newSheetRequired(tidParamInfo)) {
			row.createCell(cllcnt).setCellValue(tidParamInfo.getApiName() + PIPE + tidParamInfo.getDataTypeStr());
			if (row.getSheet().getWorkbook().getSheet(tidParamInfo.getApiName()) == null) {
				Row childRow = row.getSheet().getWorkbook().createSheet(tidParamInfo.getApiName()).createRow(0);
				childRow.createCell(0).setCellValue("ID" + PIPE + STRING);
				int childCellCount = 1;
				for (TidParamInfo tidParamInf : tidParamInfo.getChildren()) {
					childCellCount = buildExcel(tidParamInf, childRow, childCellCount);
				}
			}
		} else {
			if (tidParamInfo.getDataTypeStr().equals("BOOLEAN")) {
				Cell booleanCell = row.createCell(cllcnt);
				booleanCell.setCellType(CELL_TYPE_BOOLEAN);
				booleanCell.setCellValue(tidParamInfo.getApiName() + PIPE + tidParamInfo.getDataTypeStr());
			} else {
				if(tidParamInfo.isMandatory()){
					row.createCell(cllcnt).setCellValue(tidParamInfo.getApiName() + PIPE + tidParamInfo.getDataTypeStr());
				}else if(!tidParamInfo.isExposedToTenant() ){
					row.createCell(cllcnt).setCellValue(tidParamInfo.getApiName() + PIPE + tidParamInfo.getDataTypeStr());
				} else {
					LOGGER.error("This param "+tidParamInfo.getApiName()+" is exposed to tenant");
					cllcnt--;				
				}
			}
		}
		cllcnt++;
		return cllcnt;
	}

	private static boolean newSheetRequired(final TidParamInfo tidParamInfo) {
		boolean newSheet = false;
		String tidParamDataType = tidParamInfo.getDataTypeStr();
		if ((StringUtils.equalsIgnoreCase(tidParamDataType, OBJECT) || StringUtils.equalsIgnoreCase(tidParamDataType, OBJECT_ARRAY)
				|| StringUtils.startsWithIgnoreCase(tidParamDataType, OBJECT_ARRAY)) && !tidParamInfo.isExposedToTenant()) {
			newSheet = true;
		}
		return newSheet;
	}

	public static void buildHeaderSheet(final Sheet sheet) {
		Row headerRow = sheet.createRow(0);
		headerRow.createCell(0).setCellValue("date");
		headerRow.createCell(1).setCellValue(ReadHeaderSheet.MODELNAME);

		Cell cellMajVer = headerRow.createCell(2);
		cellMajVer.setCellType(Cell.CELL_TYPE_NUMERIC);
		cellMajVer.setCellValue("majorVersion");

		Cell cellMinVer = headerRow.createCell(3);
		cellMinVer.setCellType(Cell.CELL_TYPE_NUMERIC);
		cellMinVer.setCellValue("minorVersion");

	
		headerRow.createCell(4).setCellValue(ReadHeaderSheet.TRANSACTION_TYPE);
		headerRow.createCell(5).setCellValue(ReadHeaderSheet.USER);
		headerRow.createCell(6).setCellValue(ReadHeaderSheet.ADD_ON_VALIDATION);
		
		Cell cellExecutionGroup = headerRow.createCell(7);
		cellExecutionGroup.setCellValue(ReadHeaderSheet.EXECUTION_GROUP);
		
		Cell cellPayloadStorage = headerRow.createCell(8);
		cellPayloadStorage.setCellType(Cell.CELL_TYPE_BOOLEAN);		
		cellPayloadStorage.setCellValue(ReadHeaderSheet.PAYLOAD_STORAGE);
        headerRow.createCell(9).setCellValue(ReadHeaderSheet.TRANSACTION_MODE);
        headerRow.createCell(10).setCellValue(ReadHeaderSheet.TRANSACTIONID);
        Cell cellstoreRLogs = headerRow.createCell(11);
        cellstoreRLogs.setCellType(Cell.CELL_TYPE_BOOLEAN);		
        cellstoreRLogs.setCellValue(ReadHeaderSheet.STORE_RLOGS);
	}

	public static void populateSheetData(final Sheet sheet, final Object obj) {
		Row row = sheet.createRow(sheet.getLastRowNum() + 1);
		if (obj instanceof Map) {
			populateMapData(sheet, obj, row, sheet.getSheetName() + "-" + (sheet.getLastRowNum() + 1));
		} else if (obj instanceof List) {
			populateListData(sheet, (List) obj, sheet.getSheetName() + "-" + (sheet.getLastRowNum() + 1));
		}

	}

	private static void populateSheetData(final Sheet sheet, final Object obj, final String id) {
		if (obj instanceof Map) {
			Row row = sheet.createRow(sheet.getLastRowNum() + 1);
			populateMapData(sheet, obj, row, id);
		} else if (obj instanceof List) {
			populateListData(sheet, (List) obj, id);
		}

	}

	private static void populateListData(final Sheet sheet, final List list, final String id) {
		Row row;
		if (CollectionUtils.isNotEmpty(list)) {
			for (Object ele : list) {
				if (ele instanceof Map) {
					row = sheet.createRow(sheet.getLastRowNum() + 1);
					populateMapData(sheet, ele, row, id);
				} else if (ele instanceof List) {
					populateListData(sheet, (List) ele, id);
				}
			}
		}
	}

	private static void populateMapData(final Sheet sheet, final Object obj, final Row row, final String id) {
		int cellIndex = getColumnIndex(sheet, ID);
		if (cellIndex > -1) {
			if (id != null) {
				row.createCell(cellIndex).setCellValue(id);
			} else {
				row.createCell(cellIndex).setCellValue(sheet.getSheetName() + "-" + (sheet.getLastRowNum() + 1));
			}
		}
		for (Object e : ((Map) obj).entrySet()) {
            Entry x = (Entry) e;
            String keyName = x.getKey().toString();
            cellIndex = getColumnIndex(sheet, keyName);
            if (cellIndex > -1) {
                if (isNewSheetRequired(x.getValue())) {
                    Sheet childSheet = sheet.getWorkbook().getSheet(x.getKey().toString());
                    final String childSheetCellValue = childSheet.getSheetName() + "-" + (childSheet.getLastRowNum() + 1);
                    populateSheetData(childSheet, x.getValue(), childSheetCellValue);
                    row.createCell(cellIndex).setCellValue(childSheetCellValue);
                } else {
                    if (isDataTypeCell(sheet, cellIndex, "BOOLEAN")) {
                        Cell booleanCell = row.createCell(cellIndex);
                        booleanCell.setCellType(CELL_TYPE_BOOLEAN);
                        if (x.getValue() != null) {
                            booleanCell.setCellValue(x.getValue().toString());
                        } else {
                            booleanCell.setCellValue(NULL);
                        }
                    } else {
                        if (isDataTypeCell(sheet, cellIndex, "STRING")) {
                            if (isDataTypeCell(sheet, cellIndex, "ARRAY") && x.getValue() != null && x.getValue() instanceof List) {
                                List<Object> list = (List<Object>) x.getValue();
                                List<Object> newList = new ArrayList<Object>();
                                row.createCell(cellIndex).setCellValue(convertToStringArray(list, newList).toString());
                            } else if (x.getValue() != null) {
                                row.createCell(cellIndex).setCellValue(x.getValue().toString());
                            } else {
                                row.createCell(cellIndex).setCellValue(NULL);
                            }
                        } else {
                            if (x.getValue() != null && !(StringUtils.equalsIgnoreCase(x.getKey().toString() ,"storeRLogs"))) {
                                // The maximum length of cell contents (text) is 32,767 characters
                                if (x.getValue().toString().length() > 32767) {
                                    row.createCell(cellIndex).setCellValue(" ");
                                    LOGGER.error("CELL LENGTH IS MORE THAN 32767 CHAR AND VALUE OF CONTENT IS :"
                                            + x.getValue().toString());
                                } else {
                                	//checking the value is not "null" for integer,bigdecimal,double,biginteger datatype
                                	String datatype = sheet.getRow(sheet.getFirstRowNum()).getCell(cellIndex).getStringCellValue();
                                	if(NULL.equalsIgnoreCase(x.getValue().toString()) && datatype!=null && (datatype.contains("INTEGER")||datatype.contains("DOUBLE")||datatype.contains("BIGINTEGER")||datatype.contains("BIGDECIMAL"))) {
                                        row.createCell(cellIndex).setCellValue(" ");
                                	}
                                	else {
                                        row.createCell(cellIndex).setCellValue(x.getValue().toString());
                                	}
                                }
                            } else {
                            	
                                if(!(StringUtils.equalsIgnoreCase(x.getKey().toString() ,"storeRLogs"))){
                                	//UMG-9707
                                	//check if the value of cell null for integer,bigdecimal,double,biginteger datatype
                                	String datatype = sheet.getRow(sheet.getFirstRowNum()).getCell(cellIndex).getStringCellValue();
                                	if(datatype!=null && (datatype.contains("INTEGER")||datatype.contains("DOUBLE")||datatype.contains("BIGINTEGER")||datatype.contains("BIGDECIMAL"))) {
                                        row.createCell(cellIndex).setCellValue(" ");
                                	}else {
                                    row.createCell(cellIndex).setCellValue(NULL);
                                	}
                            	}
                            }

                        }

                    }
                }
            }
		}
	}

    private static List<Object> convertToStringArray(List<Object> list, List<Object> newList) {
        for (Object object : list) {
            if (object instanceof List) {
                newList.add(convertToStringArray((List<Object>) object, new ArrayList<Object>()));
            } else {
            	if(object != null){
                    newList.add(BusinessConstants.CHAR_DOUBLE_QUOTE + (String) object + BusinessConstants.CHAR_DOUBLE_QUOTE);
                	} else {
                		newList.add(null);
                	}
            }
        }

        return newList;
    }

    private static boolean isDataTypeCell(final Sheet sheet, final int columnIndex, String dataType) {
        if (sheet.getRow(sheet.getFirstRowNum()).getCell(columnIndex).getStringCellValue().contains(dataType)) {
			return true;
		} else {
			return false;
		}
	}

	private static int getColumnIndex(final Sheet sheet, final String key) {
		int index = -1;
		Row row = sheet.getRow(0);
		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			if (isNotEmpty(cell.getStringCellValue())) {
				final String[] words = cell.getStringCellValue().split(PIPE_REG);
				if (words[0].equals(key)) {
					index = cell.getColumnIndex();
					break;
				}
			}
		}
		return index;
	}

	private static boolean isNewSheetRequired(final Object obj) {
		boolean isNewSheetRequired = false;
		if (obj instanceof Map) {
			isNewSheetRequired = true;
		} else if (obj instanceof List) {
			isNewSheetRequired = isObjectList((List) obj);
		}
		return isNewSheetRequired;
	}

	private static boolean isObjectList(final List objList) {
		boolean isObjectList = false;
		if (CollectionUtils.isNotEmpty(objList)) {
			Object listEle = objList.get(0);
			if (listEle instanceof Map) {
				isObjectList = true;
			} else if (listEle instanceof List) {
				isObjectList = isObjectList((List) listEle);
			}
		}
		return isObjectList;
	}

	public static Workbook createTenantOupputWorkbook(final MappingDescriptor mappingDescription, final Map<String, Object> tenantJson)
			throws BusinessException, SystemException {
		Workbook wb = createWorkbookWithHeadersForOutput(mappingDescription);

		for (Object key : tenantJson.keySet()) {
			if (key != null) {
				Sheet sheet = wb.getSheet(key.toString());
				populateSheetData(sheet, tenantJson.get(key));
			}
		}
		return wb;
	}

	private static void setTransactionIdInDataSheet(final Sheet dataSheet, final Sheet headerSheet, final int row) {
		if(headerSheet.getRow(row) != null ){
            final String transactionId = headerSheet.getRow(row).getCell(10).getStringCellValue();
			dataSheet.getRow(row).createCell(0).setCellValue(transactionId);
		}
	}

	public static void main(final String[] args) {
		final String[] words = "abc|as".split(PIPE_REG);

		System.out.println(words.toString());

		System.out.println(words[0].toString());
	}

	private static Workbook createWorkbookWithHeadersForInput(final MappingDescriptor mappingDescription) {
		Workbook wb = new HSSFWorkbook();
		Sheet dataSheet = wb.createSheet("Data");
		Sheet headerSheet = wb.createSheet("Header");
		buildHeaderSheet(headerSheet);
		Row row = dataSheet.createRow(0);	
		row.createCell(0).setCellValue("transactionId|STRING");
		int cellCount = 1;
		for (TidParamInfo tidParamInfo : mappingDescription.getTidTree().getTidInput()) {
			cellCount = buildExcel(tidParamInfo, row, cellCount);
		}
		
		return wb;
	}
	
	public static Workbook createTenantInputWorkbook(final MappingDescriptor mappingDescription, final List<Map<String, Object>> tenantJsons)
			throws BusinessException, SystemException {
		
		Workbook wb = createWorkbookWithHeadersForInput(mappingDescription);

		Sheet dataSheet = wb.getSheet("Data");
		Sheet headerSheet = wb.getSheet("Header");

		int row = 1;
		int actualRowsAdded = 0;
		for (Map<String, Object> tenantJson : tenantJsons) {
			for (Object key : tenantJson.keySet()) {
				if (key != null) {
					Sheet sheet = wb.getSheet(key.toString());
					if (sheet != null ){
					populateSheetData(sheet, tenantJson.get(key));
					}
				}
			}
			if(dataSheet != null && headerSheet != null ) {
				setTransactionIdInDataSheet(dataSheet, headerSheet, row);
			}
			row++;
			actualRowsAdded++;
		}
		
		removeTransactionIdColumnFromHeader(headerSheet, row);
		keepOnlyOneRowInHeader(actualRowsAdded, row, headerSheet);
		return wb;
	}
	
	private static void keepOnlyOneRowInHeader(final int actualRowsAdded, final int rowCount, final Sheet headerSheet) {
		if (actualRowsAdded > 1) {
			for (int rowToBeDeleted = 2; rowToBeDeleted < rowCount; rowToBeDeleted++) {
				if(headerSheet.getRow(rowToBeDeleted) != null){
				headerSheet.removeRow(headerSheet.getRow(rowToBeDeleted));
			}
			}
		}
	}
	
	private static void removeTransactionIdColumnFromHeader(final Sheet headerSheet, final int rowCount) {	
		
		Row sheetRow = headerSheet.getRow(1);	
		if(sheetRow!=null){
			if(sheetRow.getCell(4)!=null){
			Cell transactionType= sheetRow.getCell(4);			
			sheetRow.removeCell(transactionType);	
			}
			if(sheetRow.getCell(5)!=null){
			Cell user= sheetRow.getCell(5);
			sheetRow.removeCell(user);
			}
			if(sheetRow.getCell(6)!=null){
			
			Cell addOnValidation= sheetRow.getCell(6);
			sheetRow.removeCell(addOnValidation);
			}
			if(sheetRow.getCell(7)!=null){
				Cell executionGroup= sheetRow.getCell(7);
				sheetRow.removeCell(executionGroup);				
			}
			if(sheetRow.getCell(8)!=null){
				Cell payloadStorage= sheetRow.getCell(8);
				sheetRow.removeCell(payloadStorage);				
			}
			Row headerRow = headerSheet.getRow(0);	
            if (headerRow.getCell(10) != null) {
                Cell transactionIdCell = headerRow.getCell(10);
				headerRow.removeCell(transactionIdCell);
				if(headerRow.getCell(11) != null){
	        		Cell transactionStoreRLogs = headerRow.getCell(11);
	        		headerRow.removeCell(transactionStoreRLogs);
	        		Cell cellstoreRLogs = headerRow.createCell(10);
	        		cellstoreRLogs.setCellType(Cell.CELL_TYPE_BOOLEAN);		
	        		cellstoreRLogs.setCellValue(ReadHeaderSheet.STORE_RLOGS);
	        	}
			}
            
            if (sheetRow.getCell(10) != null) {
                Cell transactionIdCell = sheetRow.getCell(10);
				sheetRow.removeCell(transactionIdCell);
			}
		}
	}
	

	private static Workbook createWorkbookWithHeadersForOutput(final MappingDescriptor mappingDescription) {
		Workbook wb = new HSSFWorkbook();
		Sheet headerSheet = wb.createSheet("Header");
		Sheet dataSheet = wb.createSheet("Data");
		buildHeaderSheet(headerSheet);
		
		Row row = dataSheet.createRow(0);	
		row.createCell(0).setCellValue("transactionId|STRING");	
		row.createCell(1).setCellValue("umgTransactionId|STRING");
		row.createCell(2).setCellValue("success|BOOLEAN");	
		
		int cellCount = 3;
		for (TidParamInfo tidParamInfo : mappingDescription.getTidTree().getTidOutput()) {
			cellCount = buildExcel(tidParamInfo, row, cellCount);
		}
		return wb;
	}
	
	public static Workbook createTenantOupputWorkbook(final MappingDescriptor mappingDescription, final List<Map<String, Object>> tenantJsons)
			throws BusinessException, SystemException {

		Workbook wb = createWorkbookWithHeadersForOutput(mappingDescription);
		
        Sheet headerSheet = wb.getSheet("Header");
        Row headerRow = headerSheet.getRow(0);
        if (headerRow.getCell(10) != null) {
            Cell transactionIdCell = headerRow.getCell(10);
            headerRow.removeCell(transactionIdCell);
            if(headerRow.getCell(11) != null){
        		Cell transactionStoreRLogs = headerRow.getCell(11);
        		headerRow.removeCell(transactionStoreRLogs);
        		Cell cellstoreRLogs = headerRow.createCell(10);
        		cellstoreRLogs.setCellType(Cell.CELL_TYPE_BOOLEAN);		
        		cellstoreRLogs.setCellValue(ReadHeaderSheet.STORE_RLOGS);
                headerRow.createCell(11).setCellValue(ReadHeaderSheet.MODEL_IDENTIFIER);

        	}else {
                headerRow.createCell(10).setCellValue(ReadHeaderSheet.MODEL_IDENTIFIER);
        	}
        }else {
                headerRow.createCell(12).setCellValue(ReadHeaderSheet.MODEL_IDENTIFIER);
        }

		for (Map<String, Object> tenantJson : tenantJsons) {
			for (Object key : tenantJson.keySet()) {
				if (key != null) {
					Sheet sheet = wb.getSheet(key.toString());
                    if (!(StringUtils.equalsIgnoreCase(key.toString(), "Header") && sheet.getLastRowNum() > 0)) {
                        populateSheetData(sheet, tenantJson.get(key));
                    }
				}
			}
		}
		
        Row sheetRow = headerSheet.getRow(1);
        if (sheetRow.getCell(10) != null) {
            Cell transactionIdCell = sheetRow.getCell(10);
            sheetRow.removeCell(transactionIdCell);
        }
        if (sheetRow.getCell(4) != null) {
            Cell transactionTypeCell = sheetRow.getCell(4);
            sheetRow.removeCell(transactionTypeCell);
        }
        if (sheetRow.getCell(9) != null) {
            Cell transactionModeCell = sheetRow.getCell(9);
            sheetRow.removeCell(transactionModeCell);
        }

		return wb;
	}
}