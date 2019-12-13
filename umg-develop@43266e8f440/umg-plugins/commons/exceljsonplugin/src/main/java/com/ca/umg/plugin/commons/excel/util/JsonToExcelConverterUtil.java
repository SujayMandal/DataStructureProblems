package com.ca.umg.plugin.commons.excel.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.umg.plugin.commons.excel.converter.TransactionElement;
import com.ca.umg.plugin.commons.excel.reader.ReadHeaderSheet;

public class JsonToExcelConverterUtil {
	private static final String ID = "Id";
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonToExcelConverterUtil.class);
	public static final String SUCCESS_COUNT = "successCount";
	public static final String TOTAL_COUNT = "totalCount";
	public static final String FAILURE_COUNT = "failureCount";
	public static final String NULL = "null";
	public static final String NOT_PICKED_COUNT="notPickedCount";
	public static final String STORE_RLOG="storeRLogs";
	public static final String MODEL_IDENTIFIER="modelIdentifier";
     
	public static void prepareWorkbook(final Workbook wb, final TransactionElement obj, final boolean combine) {	
		Iterator<Object> response = ((Map) obj.getResponse()).entrySet().iterator();
		while (response.hasNext()) {
			Object e = response.next();
			Entry x = (Entry) e;
			Sheet sheet = wb.getSheet(x.getKey().toString());
			if (sheet == null) {
				sheet = wb.createSheet(x.getKey().toString());
				populateSheetHeader(sheet, x.getValue(), combine);
			}
			if (StringUtils.equalsIgnoreCase(sheet.getSheetName(), "header")) {
				LOGGER.error("header data is :" + x.getValue());
				if (sheet.getRow(1) == null) {
					populateSheetData(sheet, x.getValue(), obj.getIndex(), combine);
				} else {
					Row row = sheet.getRow(1);
					int cellIndex = 0;
					Iterator<Object> objectIterator = ((Map) x.getValue()).entrySet().iterator();
					while (objectIterator.hasNext()) {
						Object f = objectIterator.next();
						Entry y = (Entry) f;
						if (y.getValue() != null) {
							String keyName = getKeyName(y);
							if (StringUtils.equalsIgnoreCase(keyName, SUCCESS_COUNT)
									|| StringUtils.equalsIgnoreCase(keyName, FAILURE_COUNT)
									|| StringUtils.equalsIgnoreCase(keyName, TOTAL_COUNT)
									|| StringUtils.equalsIgnoreCase(keyName, NOT_PICKED_COUNT) 
									||  StringUtils.equalsIgnoreCase(keyName, STORE_RLOG)) {
								cellIndex = getColumnIndex(sheet, keyName);
								row.createCell(cellIndex).setCellValue(y.getValue().toString());
							}
						}
					}
				}

			} else {
				populateSheetData(sheet, x.getValue(), obj.getIndex(), combine);
			}

		}
		// }
	}

	public static void populateSheetData(final Sheet sheet, final Object obj, final int index, final boolean combine) {
		if (obj instanceof Map) {
			if (StringUtils.equals(sheet.getSheetName(), FrameworkConstant.FAILURE_TERMINATED)) {
				Row row = sheet.createRow(sheet.getLastRowNum() + 1);
				populateMapData(sheet, obj, row, null, combine);
			} else {
				Row row = sheet.createRow(index);
				populateMapData(sheet, obj, row, null, combine);

			}
		} else if (obj instanceof List) {
			populateListData(sheet, (List) obj, null, combine);
		}

	}

	private static void populateSheetData(final Sheet sheet, final Object obj, final String id, final boolean combine) {
		if (obj instanceof Map) {
			Row row = sheet.createRow(sheet.getLastRowNum() + 1);
			populateMapData(sheet, obj, row, id, combine);
		} else if (obj instanceof List) {
			populateListData(sheet, (List) obj, id, combine);
		}

	}

	private static void populateSheetData(final Sheet sheet, final Object obj, final String id, final boolean combine,
			final boolean forModel) {
		if (obj instanceof Map) {
			Row row = sheet.createRow(sheet.getLastRowNum() + 1);
			populateMapData(sheet, obj, row, id, combine, true);
		} else if (obj instanceof List) {
			populateListData(sheet, (List) obj, id, combine);
		}

	}

	public static void populateSheetData(final Sheet sheet, final Object obj, final int index, final boolean combine,
			final boolean forModel) {
		if (obj instanceof Map) {
			Row row = sheet.createRow(sheet.getLastRowNum() + 1);
			populateMapData(sheet, obj, row, null, combine, true);
		} else if (obj instanceof List) {
			populateListData(sheet, (List) obj, null, combine);
		}

	}

	private static void populateListData(final Sheet sheet, final List list, final String id, final boolean combine) {
		Row row;
		if (CollectionUtils.isNotEmpty(list)) {
			for (Object ele : list) {
				if (ele instanceof Map) {
					row = sheet.createRow(sheet.getLastRowNum() + 1);
					populateMapData(sheet, ele, row, id, combine);
				} else if (ele instanceof List) {
					populateListData(sheet, (List) ele, id, combine);
				}
			}
		}
	}

	private static void populateMapData(final Sheet sheet, final Object obj, final Row row,
			final String id, final boolean combine) {
		int cellIndex = 0;	
		if(!(StringUtils.equals("header", sheet.getSheetName()) || StringUtils.equals("data", sheet.getSheetName())||StringUtils.equals(FrameworkConstant.FAILURE_TERMINATED, sheet.getSheetName()))){
			cellIndex = getColumnIndex(sheet, ID);
			if (id != null){
				row.createCell(cellIndex).setCellValue(id);	
			} else {
				row.createCell(cellIndex).setCellValue(sheet.getSheetName() + "-" + row.getRowNum());
			}
		}
		Iterator<Object> objectIterator = ((Map) obj).entrySet().iterator();
		while (objectIterator.hasNext()) {
			Object e = objectIterator.next();
			Entry x = (Entry) e;
			String keyName = getKeyName(x);
			cellIndex = getColumnIndex(sheet, keyName);
			if (isNewSheetRequired(x.getValue())) {
				Sheet childSheet = sheet.getWorkbook().getSheet(x.getKey().toString());
				if (childSheet == null) {
					childSheet = sheet.getWorkbook().createSheet(x.getKey().toString());
					populateSheetHeader(childSheet, x.getValue(), combine);
				}				
				if(StringUtils.equals(FrameworkConstant.FAILURE_TERMINATED,childSheet.getSheetName())){
					final String childSheetCellValue = childSheet.getSheetName() + "-" + (childSheet.getLastRowNum() + 1);
					populateSheetData(childSheet, x.getValue(), childSheetCellValue, combine);
					row.createCell(cellIndex).setCellValue(childSheetCellValue);					
				}
				else{
					final String childSheetCellValue = childSheet.getSheetName() + "-" + ((sheet.getLastRowNum()-1) + 1);
					populateSheetData(childSheet, x.getValue(), childSheetCellValue, combine);
					row.createCell(cellIndex).setCellValue(childSheetCellValue);
				}

			} else if(x.getValue() != null) {
				row.createCell(cellIndex).setCellValue(
						x.getValue().toString());
			} else {
				row.createCell(cellIndex).setCellValue(NULL);
			}
		}
	}

	private static String getKeyName(final Entry x) {
		String keyName = "";
		if (isNewSheetRequired(x.getValue())) {
			if (x.getValue() instanceof List) {
				keyName = x.getKey().toString() + "|OBJECT|ARRAY";
			} else {
				keyName = x.getKey().toString() + "|OBJECT";
			}
		} else {
			keyName = x.getKey().toString();
		}
		return keyName;
	}

	private static int getColumnIndex(final Sheet sheet, final String key) {
		int index = -1;
		Row row = sheet.getRow(0);
		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			if (StringUtils.isNotEmpty(cell.getStringCellValue()) && cell.getStringCellValue().equals(key)) {
				index = cell.getColumnIndex();
				break;
			}
		}
		if (index == -1) {
			row.createCell(row.getLastCellNum()).setCellValue(key);
			index = row.getLastCellNum() - 1;
		}
		return index;
	}

	private static int getColumnIndex(final Sheet sheet, final String key, final boolean forModel) {
		int index = -1;
		Row row = sheet.getRow(0);
		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			if (StringUtils.isNotEmpty(cell.getStringCellValue()) && cell.getStringCellValue().equals(key)) {
				index = cell.getColumnIndex();
				break;
			}
		}
		if (index == -1) {
			row.createCell(row.getLastCellNum() + 1).setCellValue(key);
			index = row.getLastCellNum() - 1;
		}
		return index;
	}

	public static void populateSheetHeader(final Sheet sheet, final Object obj, final boolean combine) {
		Row row = sheet.createRow(sheet.getLastRowNum());
		int colNum = 0;
		Sheet newSheet;
		if (!(StringUtils.equals("header", sheet.getSheetName()) || StringUtils.equals("data", sheet.getSheetName())
				|| StringUtils.equals(FrameworkConstant.FAILURE_TERMINATED, sheet.getSheetName()))) {
			row.createCell(colNum++).setCellValue(ID);
		}
		if(StringUtils.equals("header", sheet.getSheetName()) ){
			row.createCell(0).setCellValue("date");
			row.createCell(1).setCellValue(ReadHeaderSheet.MODELNAME);
			row.createCell(2).setCellValue(ReadHeaderSheet.MAJOR_VERSION);
			row.createCell(3).setCellValue(ReadHeaderSheet.MINOR_VERSION);
			row.createCell(4).setCellValue(ReadHeaderSheet.TRANSACTION_TYPE);
			row.createCell(5).setCellValue(ReadHeaderSheet.USER);
			row.createCell(6).setCellValue(ReadHeaderSheet.ADD_ON_VALIDATION);
			row.createCell(7).setCellValue(ReadHeaderSheet.PAYLOAD_STORAGE);
			row.createCell(8).setCellValue(ReadHeaderSheet.EXECUTION_GROUP);
			row.createCell(9).setCellValue(ReadHeaderSheet.TRANSACTION_MODE);
			row.createCell(10).setCellValue(SUCCESS_COUNT);			
			row.createCell(11).setCellValue(FAILURE_COUNT);
			row.createCell(12).setCellValue(TOTAL_COUNT);	
			row.createCell(13).setCellValue(NOT_PICKED_COUNT);
			row.createCell(14).setCellValue(STORE_RLOG);
			row.createCell(15).setCellValue(MODEL_IDENTIFIER);
		}else if(obj instanceof Map) {
			Iterator<Object> objectIterator = ((Map) obj).entrySet().iterator();
			while (objectIterator.hasNext()) {
				Object e = objectIterator.next();
				Entry x = (Entry) e;
				String keyName = getKeyName(x);				
				row.createCell(colNum++).setCellValue(keyName);
				if (isNewSheetRequired(x.getValue())) {
					final String newSheetName = x.getKey().toString();
					newSheet = sheet.getWorkbook().getSheet(newSheetName);
					if (newSheet == null) {
						newSheet = sheet.getWorkbook().createSheet(newSheetName);
						populateSheetHeader(newSheet, x.getValue(), combine);
					}
				}
			}
		}


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

	public static Object parseJsonString(final String jsonString)
			throws JsonParseException, JsonProcessingException, IOException {
		Object jsonObj = null;
		JsonFactory factory = new JsonFactory();
		ObjectMapper om = new ObjectMapper();
		factory.setCodec(om);
		JsonParser parser = factory.createJsonParser(jsonString);
		JsonNode topNode = parser.readValueAsTree();
		if (topNode != null) {
			if (topNode.isObject()) {
				jsonObj = parseJsonMap(topNode);
			} else if (topNode.isArray()) {
				jsonObj = parseJsonList(topNode);
			} else {
				String value = topNode.asText();
				jsonObj = value;
			}
		}
		return jsonObj;
	}

	// Parses a json list.
	private static List<Object> parseJsonList(final JsonNode parentNode) {
		List<Object> jsonArray = new ArrayList<>();
		try {
			for (Iterator<JsonNode> elements = parentNode.getElements(); elements.hasNext();) {
				JsonNode node = elements.next();
				if (node == null) {
					continue;
				}
				if (node.isArray()) {
					List<Object> childList = parseJsonList(node);
					jsonArray.add(childList);
				} else if (node.isObject()) {
					Map<String, Object> childMap = parseJsonMap(node);
					jsonArray.add(childMap);
				} else {
					String value = node.asText();
					jsonArray.add(value);
				}
			}
		} catch (Exception e) {

		}
		return jsonArray;
	}

	// Parse a json map
	private static Map<String, Object> parseJsonMap(final JsonNode parentNode) {
		Map<String, Object> jsonObject = new LinkedHashMap<>();
		try {
			Iterator<String> fieldNames = parentNode.getFieldNames();
			while (fieldNames.hasNext()) {
				String name = fieldNames.next();
				JsonNode node = parentNode.get(name);
				if (node == null) {
					continue;
				}
				if (node.isArray()) {
					List<Object> childList = parseJsonList(node);
					jsonObject.put(name, childList);
				} else if (node.isObject()) {
					Map<String, Object> childMap = parseJsonMap(node);
					jsonObject.put(name, childMap);
				} else {
					String value = node.asText();
					jsonObject.put(name, value);
				}
			}
		} catch (Exception e) {
		}
		return jsonObject;
	}

	private static String getValidationErrors(final Entry entry, String errorMsg) {
		int count = 1;
		Iterator<Object> objectIterator = ((Map) entry.getValue()).entrySet().iterator();
		while (objectIterator.hasNext()) {
			Object obj = objectIterator.next();
			Entry errorEntry = (Entry) obj;
			if (errorEntry.getValue() instanceof Map) {
				errorMsg = getValidationErrors(errorEntry, errorMsg + errorEntry.getKey() + "|");
			} else {
				errorMsg = errorMsg + errorEntry.getKey() + ":" + errorEntry.getValue() + "  ";

			}

		}

		return errorMsg;
	}

	public static void populateSheetHeader(final Sheet sheet, final Object obj, final boolean combine,
			final boolean froModel) {
		Row row = sheet.createRow(sheet.getLastRowNum());
		int colNum = 0;
		Sheet newSheet;
		if (!(StringUtils.equals("header", sheet.getSheetName()) || StringUtils.equals("data", sheet.getSheetName()))) {
			row.createCell(colNum++).setCellValue(ID);
		}

		if (obj instanceof Map) {
			Iterator<Object> objectIterator = ((Map) obj).entrySet().iterator();
			while (objectIterator.hasNext()) {
				Object e = objectIterator.next();
				Entry x = (Entry) e;
				String keyName = getKeyName(x);
				row.createCell(colNum++).setCellValue(keyName);
				if (isNewSheetRequired(x.getValue())) {
					final String newSheetName = x.getKey().toString();
					newSheet = sheet.getWorkbook().getSheet(newSheetName);
					if (newSheet == null) {
						newSheet = sheet.getWorkbook().createSheet(newSheetName);
						populateSheetHeader(newSheet, x.getValue(), combine);
					}
				}
			}
		}

	}

	private static void populateMapData(final Sheet sheet, final Object obj, final Row row, final String id,
			final boolean combine, final boolean forModel) {
		int cellIndex = getColumnIndex(sheet, ID, true);
		if (id != null) {
			row.createCell(cellIndex).setCellValue(id);

		} else {
			row.createCell(cellIndex).setCellValue(sheet.getSheetName() + "-" + row.getRowNum());

		}
		Iterator<Object> objectIterator = ((Map) obj).entrySet().iterator();
		while (objectIterator.hasNext()) {
			Object e = objectIterator.next();
			Entry x = (Entry) e;
			String keyName = getKeyName(x);
			cellIndex = getColumnIndex(sheet, keyName, true);
			if (isNewSheetRequired(x.getValue())) {
				Sheet childSheet = sheet.getWorkbook().getSheet(x.getKey().toString());
				if (childSheet == null) {
					childSheet = sheet.getWorkbook().createSheet(x.getKey().toString());
					populateSheetHeader(childSheet, x.getValue(), combine);
				}
				final String childSheetCellValue = childSheet.getSheetName() + "-"
						+ (childSheet.getLastRowNum() + 1) + "-" + cellIndex;
				populateSheetData(childSheet, x.getValue(), childSheetCellValue, combine, true);
				row.createCell(cellIndex).setCellValue(childSheetCellValue);
			} else if (x.getValue() != null) {
				row.createCell(cellIndex).setCellValue(x.getValue().toString());
			} else {
				row.createCell(cellIndex).setCellValue(NULL);
			}
		}
	}

	/*	private List<String> getHeaderMap(){
		List<String> headerList = new LinkedList<String>();
		headerList.add(0, element);
		return headerList;
	}
	 */
}