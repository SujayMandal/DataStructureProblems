/**
 * 
 */
package com.ca.umg.modelet.common;

import static com.ca.umg.modelet.util.ExcelDatatypes.DOUBLE;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;
import com.ca.framework.core.util.MapCopyUtil;
import com.ca.umg.modelet.constants.ErrorCodes;
import com.ca.umg.modelet.util.ExcelDatatypes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComFailException;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.SafeArray;
import com.jacob.com.Variant;

/**
 * @author kamathan
 *
 */
@SuppressWarnings("PMD")
public class ExcelModel {

	private static final String FIELD_COLLECTION = "collection";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelModel.class);

	private static final String FIELD_MODEL_PARAMETER_NAME = "modelParameterName";

	private static final String API_PARAMETER_NAME = "apiName";

	private static final String FIELD_DATA_TYPE = "dataType";

	private static final String NATIVE_DATA_TYPE = "nativeDataType";

	private static final String FIELD_PRECISION = "precision";

	private static final String FIELD_VALUE = "value";

	private static final String MODEL_LIBRARY = "modelLibrary";

	private static final String OLE_RUN_MACRO = "run";

	private static final String OLE_CELL_VALUE = "Value";

	private static final String OLE_RANGE = "Range";

	private static final String OLE_ITEM = "Item";

	private static final String OLE_DISPLAY_ALERTS = "DisplayAlerts";

	private static final String OLE_ENABLE_EVENTS = "EnableEvents";

	private static final String OLE_OPEN = "Open";

	private static final String OLE_QUIT = "Quit";

	private static final String OLE_WORKBOOKS = "Workbooks";

	private static final String OLE_VISIBLE = "Visible";

	private static final String SHEET_CELL_SEPARATOR = "*";
	
	private static final String REG_EXP_NAMED_PRIMITIVE = "N\\[.+\\*.+\\]";
	
	private static final String SHEET_NAMED_CELL_START = "N[";
	
	private static final String SHEET_NAMED_CELL_END = "]";

	private static final String CELL_SEPARATOR = ":";

	private static final String EXCELAPPLICATION_PROGRAMID = "Excel.Application";

	private static final String OLE_WORKSHEETS = "Worksheets";
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String NATIVE_DATE_FORMAT = "M/d/yyyy";
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String BASE_DATE = "1899-12-30";
	private static final String BASE_DATE_TIME = "1899-12-30 00:00:00";
	private static final int SECONDS_IN_DAY = 86400;
	
	private static final ObjectMapper oMapper = new ObjectMapper();


	private final ModelKey modelKey;

	public ExcelModel(ModelKey modelKey) {
		this.modelKey = modelKey;
	}

	// variant dataType number map
	private static Map<Short, String> dataTypeMap = new HashMap<Short, String>();

	static {
		dataTypeMap.put((short) 0, "Empty");
		dataTypeMap.put((short) 1, "Null");
		dataTypeMap.put((short) 2, "Short");
		dataTypeMap.put((short) 3, "Integer");
		dataTypeMap.put((short) 4, "Float");
		dataTypeMap.put((short) 5, "Double");
		dataTypeMap.put((short) 6, "Currency");
		dataTypeMap.put((short) 7, "Date");
		dataTypeMap.put((short) 8, "String");
		dataTypeMap.put((short) 9, "Dispatch");
		dataTypeMap.put((short) 10, "Error");
		dataTypeMap.put((short) 11, "Boolean");
		dataTypeMap.put((short) 12, "Variant");
		dataTypeMap.put((short) 13, "Object");
		dataTypeMap.put((short) 14, "Decimal");
		dataTypeMap.put((short) 17, "Byte");
		dataTypeMap.put((short) 20, "LongInt");
		dataTypeMap.put((short) 26, "Pointer");
		dataTypeMap.put((short) 4095, "TypeMask");
		dataTypeMap.put((short) 8192, "Array");
		dataTypeMap.put((short) 16384, "Byref");
	}

	@SuppressWarnings("rawtypes")
	public List<FieldInfo> executeModel(final Map<String, FieldInfo> excelModelInput,
			List<FieldInfo> excelModelOutputFields) throws SystemException, BusinessException {
		LOGGER.debug("Executing excel model {}.", modelKey.getModelLibrary());
		long start = System.currentTimeMillis();
		// make a copy of output fields and populate the value
		List<FieldInfo> excelModelOutput = new ArrayList<FieldInfo>(excelModelOutputFields);
		ActiveXComponent objExcel = null;
		Dispatch workbooks = null;
		File excelModelFile = null;
		try {
			LOGGER.error("Initializing ComThread");
			ComThread.InitSTA();
			LOGGER.error("Initialized ComThread");
			
			

			//Local sanpath path dir
			StringBuilder directory = new StringBuilder(modelKey.getTenantCode());
			directory.append(File.separator)
			.append(MODEL_LIBRARY).append(File.separator).append(modelKey.getModelLibrary())
			.append(File.separator).append(modelKey.getUmgName());
			
			
			

			if(StringUtils.isNotBlank(modelKey.getLocalFilePath())) {
				//Local SathPath
				StringBuilder localSanPath = new StringBuilder(modelKey.getLocalFilePath());
				
				//create a localFile name
				StringBuilder fileName = new StringBuilder(localSanPath);
				fileName.append(File.separator).append(directory).append(File.separator)
				.append(modelKey.getJarName());
				excelModelFile = new File(fileName.toString());
				
			//check if the model file exist on local server or not
			if(excelModelFile.exists()) {
				LOGGER.error("Model File Exist on Local Server Path {} ",excelModelFile.getAbsolutePath());
			}
			else {
				StringBuilder remoteFilePath = new StringBuilder(modelKey.getFilePath());
				remoteFilePath.append(File.separator).append(directory).append(File.separator)
				.append(modelKey.getJarName());
				
				StringBuilder localDirectory = new StringBuilder(localSanPath);
				localDirectory.append(File.separator).append(directory);
				File locaDir = new File(localDirectory.toString());
				File remoteFile = new File(remoteFilePath.toString());
				long copyFileStrtTime = System.currentTimeMillis();
				FileUtils.copyFileToDirectory(remoteFile, locaDir);
				LOGGER.error("Time taken to copy the file in Directory {} , {} ms",locaDir,System.currentTimeMillis()-copyFileStrtTime);
			}
			}else {
				LOGGER.error("Local San path is not valid {} or not given in modelet runtime params",modelKey.getLocalFilePath());
			    // build excel model file path
				StringBuilder remoteFilePath = new StringBuilder(modelKey.getFilePath());
				remoteFilePath.append(File.separator).append(directory).append(File.separator)
				.append(modelKey.getJarName());
			    excelModelFile = new File(remoteFilePath.toString());
			}
			
			
			// open excel file application
			objExcel = new ActiveXComponent(EXCELAPPLICATION_PROGRAMID);
			// run excel in background
			objExcel.setProperty(OLE_VISIBLE, new Variant(false));

			// Open Excel file, get the workbooks object required for access:
			workbooks = objExcel.getProperty(OLE_WORKBOOKS).toDispatch();

			// disable calculation
			// xl.setProperty("Calculation", -4135);
     		
 
			// open excel workbook
			long startTime = System.currentTimeMillis();
			workbooks = Dispatch.call(workbooks, OLE_OPEN, excelModelFile.getAbsolutePath()).toDispatch();
			objExcel.setProperty("Calculation", false);
			LOGGER.error("Time taken for opening excel workbook :::: {} ms", System.currentTimeMillis() - startTime);

			Dispatch workSheets = Dispatch.get(workbooks, OLE_WORKSHEETS).toDispatch();

			// populate input cells
			startTime = System.currentTimeMillis();
			Map orderedExcelModelInput = sortInputsBySequenceNumber(excelModelInput);
			populateInputCells(orderedExcelModelInput, workSheets);
			LOGGER.error("Time taken for input population :::: {} ms", System.currentTimeMillis() - startTime);

			long calcStartTime = System.currentTimeMillis();
			// run calculation
			// xl.setProperty("Calculation", -4209);
			objExcel.setProperty("Calculation", true);
			LOGGER.error("Time taken for excel formula calculation :: {}", System.currentTimeMillis() - calcStartTime);
			
			// run macros
			runMacros(objExcel);

			// read output cell
			if (CollectionUtils.isNotEmpty(excelModelOutputFields)) {
				List<String> errorList = new ArrayList<String>();
				startTime = System.currentTimeMillis();
				readOutputCells(excelModelOutputFields, workSheets, errorList);
				LOGGER.error("Time taken for output read :::: {} ms", System.currentTimeMillis() - startTime);
				if (CollectionUtils.isNotEmpty(errorList)) {
					throw new SystemException(FrameworkExceptionCodes.RSE000931, new Object[] { errorList });
				}
			}

		} catch (ComFailException e) { // NOPMD
			LOGGER.error("An error ocurred while executing excel model", e);
			SystemException.newSystemException(ErrorCodes.ME00040, new Object[] {}); // NOPMD
		} 
		catch(IOException exp) {
			LOGGER.error("An error ocurred while copy File to Excel Hosted Server", exp.getLocalizedMessage());
			SystemException.newSystemException(ErrorCodes.ME00040, new Object[] {}); // NOPMD
		}
		finally {
			quitExcel(objExcel, false);
			ComThread.Release();
		}
		LOGGER.info("Time Taken by Excel Model {} ",System.currentTimeMillis()-start);
		return excelModelOutput;
	}

	private void runMacros(ActiveXComponent objExcel) throws BusinessException {
		String currentMacro = null;
		try {
			if (StringUtils.isNotBlank(modelKey.getModelMethod())) {
				String[] macros = StringUtils.split(modelKey.getModelMethod(), FrameworkConstant.COMMA);
				for (String macro : macros) {
					currentMacro = macro;
					LOGGER.info("Executing macro {}.", modelKey.getModelMethod());
					objExcel.setProperty(OLE_VISIBLE, true);
					objExcel.setProperty(OLE_ENABLE_EVENTS, false);
					objExcel.setProperty(OLE_DISPLAY_ALERTS, false);
					Dispatch.call(objExcel, OLE_RUN_MACRO, macro);
					LOGGER.info("Executed macro {} successfully.", modelKey.getModelMethod());
				}
			}
		} catch (ComFailException ex) {// NOPMD
			LOGGER.error("exception while executing macro.");
			throw new BusinessException(ErrorCodes.ME00042, new Object[] { currentMacro });
		}
	}

	/*
	 * Read output cell values from excel model
	 */
	private void readOutputCells(List<FieldInfo> excelModelOutputFields, Dispatch workSheets, List<String> errorList)
			throws BusinessException, SystemException {
		LOGGER.info("Initiated reading output cells from excel model {}.", modelKey.getModelLibrary());

		// sort output cells in sequencial order
		Collections.sort(excelModelOutputFields);

		for (FieldInfo fieldInfo : excelModelOutputFields) {
			// top level object does not contain datatype
			if (StringUtils.equalsIgnoreCase(ExcelDatatypes.OBJECT.getDatatype(), fieldInfo.getDataType())
					&& fieldInfo.isCollection()) {

				List<Map> outputCells = (List<Map>) fieldInfo.getValue();

				Collections.sort(outputCells, new Comparator<Map>() {
					public int compare(Map o1, Map o2) {
						int result = 0;
						if (Integer.parseInt(o1.get("sequence").toString()) > Integer
								.parseInt(o2.get("sequence").toString())) {
							result = 1;
						} else if (Integer.parseInt(o1.get("sequence").toString()) < Integer
								.parseInt(o1.get("sequence").toString())) {
							result = -1;
						}
						return result;
					}
				});
				// read child parameters
				List<List<Map>> values = readCelloutputForObjectArray(workSheets, outputCells, errorList);
				fieldInfo.setValue(values);

			} else if (StringUtils.equalsIgnoreCase(ExcelDatatypes.OBJECT.getDatatype(), fieldInfo.getDataType())) {

				List<Map> outputCells = (List<Map>) fieldInfo.getValue();

				// read child parameters
				readCelloutput(workSheets, outputCells, errorList);

			} else {
				// output cells are defined parameters
				String sheetName = isNameBasedMapping(fieldInfo.getModelParameterName())?
						StringUtils.substringBetween(fieldInfo.getModelParameterName(), SHEET_NAMED_CELL_START, SHEET_CELL_SEPARATOR):
							StringUtils.substringBefore(fieldInfo.getModelParameterName(), SHEET_CELL_SEPARATOR);

				String cellRange = isNameBasedMapping(fieldInfo.getModelParameterName())?
						StringUtils.substringBetween(fieldInfo.getModelParameterName(), SHEET_CELL_SEPARATOR, SHEET_NAMED_CELL_END):
							StringUtils.substringAfter(fieldInfo.getModelParameterName(), SHEET_CELL_SEPARATOR);

				// read values from output
				Dispatch outputWorksheet = findWorksheet(workSheets, sheetName);
				
				Dispatch cell = findCell(outputWorksheet, cellRange, sheetName);
				
				Variant variant = Dispatch.get(cell, OLE_CELL_VALUE);
				Object value = readOutputValueFromSafeArray(variant, fieldInfo.getNativeDataType(),
						fieldInfo.getDataType(), fieldInfo.isCollection(), fieldInfo.getApiName(),
						fieldInfo.getModelParameterName(), fieldInfo.getPrecession(), errorList);
				LOGGER.debug("Read Output cell {} Value : {}.", fieldInfo.getValue(), value);
				fieldInfo.setValue(value);
			}
		}
	}

	/**
	 * Fetches cell values of model output
	 * 
	 * @param workSheets
	 * @param outputCells
	 * @throws SystemException
	 */
	private void readCelloutput(Dispatch workSheets, List<Map> outputCells, List<String> errorList)
			throws SystemException {
		for (Map outputCell : outputCells) {
			String modelParameterName = (String) outputCell.get(FIELD_MODEL_PARAMETER_NAME);
			String dataType = (String) outputCell.get(NATIVE_DATA_TYPE);
			String rADataType = (String) outputCell.get(FIELD_DATA_TYPE);
			int precession = 0;
			if (outputCell.get("precession") instanceof String) {
				String pre = (String) outputCell.get("precession");

				if (pre != null) {
					precession = Integer.valueOf(pre);
				}
			} else {
				if (outputCell.get("precession") != null) {
					precession = (int) outputCell.get("precession");
				}
			}
			boolean collection = (boolean) outputCell.get(FIELD_COLLECTION);
			if (StringUtils.equalsIgnoreCase(ExcelDatatypes.OBJECT.getDatatype(), dataType)) {
				readCelloutput(workSheets, (List<Map>) outputCell.get(FIELD_VALUE), errorList);
			} else {
				String sheetName = isNameBasedMapping(modelParameterName)?
						StringUtils.substringBetween(modelParameterName, SHEET_NAMED_CELL_START, SHEET_CELL_SEPARATOR):
							StringUtils.substringBefore(modelParameterName, SHEET_CELL_SEPARATOR);

				String cellRange = isNameBasedMapping(modelParameterName)?
						StringUtils.substringBetween(modelParameterName, SHEET_CELL_SEPARATOR, SHEET_NAMED_CELL_END):
							StringUtils.substringAfter(modelParameterName, SHEET_CELL_SEPARATOR);

				Dispatch outputWorksheet = findWorksheet(workSheets, sheetName);

				Dispatch cell = findCell(outputWorksheet, cellRange, sheetName);

				Variant variant = Dispatch.get(cell, OLE_CELL_VALUE);
				Object value = readOutputValueFromSafeArray(variant, dataType, rADataType, collection,
						(String) outputCell.get(API_PARAMETER_NAME),
						(String) outputCell.get(FIELD_MODEL_PARAMETER_NAME), precession, errorList);
				LOGGER.debug("Read Output cell {} Value : {}.", modelParameterName, value);
				outputCell.put("value", value);
			}
		}
	}

	/**
	 * Fetches cell values of model output
	 * 
	 * @param workSheets
	 * @param outputCells
	 * @throws SystemException
	 */
	private List<List<Map>> readCelloutputForObjectArray(Dispatch workSheets, List<Map> outputCells,
			List<String> errorList) throws SystemException {
		List<List<Map>> values = new ArrayList<List<Map>>();
		Map<Integer, List<Map>> copyValues = new HashMap<Integer, List<Map>>();
		for (Map outputCell : outputCells) {
			String modelParameterName = (String) outputCell.get(FIELD_MODEL_PARAMETER_NAME);
			String apiName = (String) outputCell.get(API_PARAMETER_NAME);
			String dataType = (String) outputCell.get(NATIVE_DATA_TYPE);
			String rADataType = (String) outputCell.get(FIELD_DATA_TYPE);
			int precession = 0;
			if (outputCell.get("precession") instanceof String) {
				String pre = (String) outputCell.get("precession");

				if (pre != null) {
					precession = Integer.valueOf(pre);
				}
			} else {
				if (outputCell.get("precession") != null) {
					precession = (int) outputCell.get("precession");
				}
			}
			if (StringUtils.equalsIgnoreCase(ExcelDatatypes.OBJECT.getDatatype(), rADataType)) {
				readCelloutput(workSheets, (List<Map>) outputCell.get(FIELD_VALUE), errorList);
			} else {
				String sheetName = isNameBasedMapping(modelParameterName) ? 
						StringUtils.substringBetween(modelParameterName, SHEET_NAMED_CELL_START, SHEET_CELL_SEPARATOR):
							StringUtils.substringBefore(modelParameterName, SHEET_CELL_SEPARATOR);
				String cellRange = isNameBasedMapping(modelParameterName) ?
						StringUtils.substringBetween(modelParameterName, SHEET_CELL_SEPARATOR, SHEET_NAMED_CELL_END):
							StringUtils.substringAfter(modelParameterName, SHEET_CELL_SEPARATOR);
						
				Dispatch outputWorksheet = findWorksheet(workSheets, sheetName);
				
				Dispatch cell = findCell(outputWorksheet, cellRange, sheetName);
				
				Variant variant = Dispatch.get(cell, OLE_CELL_VALUE);
				Object value = readOutputValueFromSafeArray(variant, dataType, rADataType, true, apiName,
						modelParameterName, precession, errorList);
				LOGGER.debug("Read Output cell {} Value : {}.", modelParameterName, value);
				if (value instanceof List) {
					Integer counter = 0;
					List<Object> primitiveValues = (List<Object>) value;
					for (Object obj : primitiveValues) {
						if (copyValues.containsKey(counter)) {
							List<Map> element = copyValues.get(counter);
							setValues(copyValues, outputCell, counter, obj, element);
						} else {
							List<Map> element = new ArrayList<Map>();
							setValues(copyValues, outputCell, counter, obj, element);
						}
						counter = counter + 1;
					}

				}

			}

		}
		Set<Integer> keySet = copyValues.keySet();
		for (Integer key : keySet) {
			values.add(copyValues.get(key));

		}

		return values;
	}

	private void setValues(Map<Integer, List<Map>> copyValues, Map outputCell, Integer counter, Object obj,
			List<Map> element) {
		Map map = MapCopyUtil.deepCopy(outputCell);
		map.put(FIELD_VALUE, obj);
		element.add(map);
		copyValues.put(counter, element);
	}

	/**
	 * Sets input values to excel cells
	 * 
	 * @param excelModelInput
	 * @param workSheets
	 * @throws BusinessException
	 */
	private void populateInputCells(final Map excelModelInput, Dispatch workSheets)
			throws BusinessException, SystemException {
		if (StringUtils.isBlank((String) excelModelInput.get(NATIVE_DATA_TYPE))) {
			for (Object entry : excelModelInput.values()) {
				FieldInfo fieldInfo = (FieldInfo) entry;
				if (fieldInfo.getValue() instanceof List) {
					List<Object> values = (List<Object>) fieldInfo.getValue();
					Map<String, List<Object>> changedValues = new HashMap<String, List<Object>>();
					Map<String, Map<String, Object>> inputCell = new HashMap<String, Map<String, Object>>();
					for (Object value : values) {
						if (StringUtils.equals("object", fieldInfo.getDataType())) {
							readInoutCells(workSheets, changedValues, inputCell, value);
						} else {
							Map<String, Object> map = oMapper.convertValue(fieldInfo, Map.class);
							long startTime = System.currentTimeMillis();
							populateInputValues(map, workSheets);
							LOGGER.error("Time taken for populating {} value :::: {} ms",
									excelModelInput.get("modelParameterName"), System.currentTimeMillis() - startTime);
							break;
						}
					}
					Set<String> keys = inputCell.keySet();
					for (String key : keys) {
						Map<String, Object> aob = inputCell.get(key);
						aob.put("collection", Boolean.TRUE);
						aob.put(FIELD_VALUE, changedValues.get(key));
						populateInputCells(aob, workSheets);
					}
				} else {
					Map<String, Object> map = oMapper.convertValue(fieldInfo, Map.class);
					long startTime = System.currentTimeMillis();
					populateInputValues(map, workSheets);
					LOGGER.error("Time taken for populating {} value :::: {} ms",
							excelModelInput.get("modelParameterName"), System.currentTimeMillis() - startTime);
				}
			}
		} else if (StringUtils.equalsIgnoreCase((String) excelModelInput.get(NATIVE_DATA_TYPE),
				ExcelDatatypes.OBJECT.getDatatype())) {
			List<Map<String, Object>> values = (List<Map<String, Object>>) excelModelInput.get(FIELD_VALUE);
			for (Map<String, Object> value : values) {
				populateInputCells(value, workSheets);
			}
		} else {
			long startTime = System.currentTimeMillis();
			populateInputValues(excelModelInput, workSheets);
			LOGGER.error("Time taken for populating {} value :::: {} ms", excelModelInput.get("modelParameterName"),
					System.currentTimeMillis() - startTime);
		}
	}

	/**
	 * Sorts the excel model inputs based on sequence number.
	 * 
	 * We assume that if there any events written on any cell then those cells will
	 * defined first and affected cells are defined later in IO definition.
	 * 
	 * @param excelModelInput
	 * @return
	 */
	private Map sortInputsBySequenceNumber(final Map excelModelInput) {
		List<Map.Entry<String, FieldInfo>> list = new LinkedList<Map.Entry<String, FieldInfo>>(
				excelModelInput.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, FieldInfo>>() {
			public int compare(Map.Entry<String, FieldInfo> o1, Map.Entry<String, FieldInfo> o2) {
				int result = 0;
				if (Integer.parseInt(o1.getValue().getSequence()) > Integer.parseInt(o2.getValue().getSequence())) {
					result = 1;
				} else if (Integer.parseInt(o1.getValue().getSequence()) < Integer
						.parseInt(o2.getValue().getSequence())) {
					result = -1;
				}
				return result;
			}
		});
		Map orderedExcelModelInput = new LinkedHashMap();
		for (Entry<String, FieldInfo> entry : list) {
			orderedExcelModelInput.put(entry.getKey(), entry.getValue());
		}
		return orderedExcelModelInput;
	}

	private void populateInputValues(final Map excelModelInput, Dispatch workSheets)
			throws BusinessException, SystemException {
		String apiParameterName = (String) excelModelInput.get(API_PARAMETER_NAME);
		String modelParameterName = (String) excelModelInput.get(FIELD_MODEL_PARAMETER_NAME);
		try {
			boolean collection = (boolean) excelModelInput.get(FIELD_COLLECTION);
			Object value = null;
			String sheetName = isNameBasedMapping(modelParameterName) ? 
					StringUtils.substringBetween(modelParameterName, SHEET_NAMED_CELL_START, SHEET_CELL_SEPARATOR):
						StringUtils.substringBefore(modelParameterName, SHEET_CELL_SEPARATOR);
			String cellRange = isNameBasedMapping(modelParameterName) ?
					StringUtils.substringBetween(modelParameterName, SHEET_CELL_SEPARATOR, SHEET_NAMED_CELL_END):
						StringUtils.substringAfter(modelParameterName, SHEET_CELL_SEPARATOR);
					
			Dispatch inputWorksheet = findWorksheet(workSheets, sheetName);
			
			Dispatch cell = findCell(inputWorksheet, cellRange, sheetName);
			
			if (collection) {
				Variant variant = Dispatch.get(cell, OLE_CELL_VALUE);
				// read the existing cell values for given range
				int precession = 0;
				if (excelModelInput.get("precession") instanceof String) {
					String pre = (String) excelModelInput.get("precession");

					if (pre != null) {
						precession = Integer.valueOf(pre);
					}
				} else {
					if (excelModelInput.get("precession") != null) {
						precession = (int) excelModelInput.get("precession");
					}
				}
				
				boolean isVerticalArray = findIfVerticalArray(variant);

				List existingCellArrayValues = (List) readValueFromSafeArray(variant,
						(String) excelModelInput.get(NATIVE_DATA_TYPE), (String) excelModelInput.get(FIELD_DATA_TYPE),
						(boolean) excelModelInput.get(FIELD_COLLECTION), apiParameterName, modelParameterName,
						precession);
				value = buildArrayValues((List) excelModelInput.get(FIELD_VALUE), cellRange, existingCellArrayValues, isVerticalArray);
			} else {
				value = excelModelInput.get(FIELD_VALUE);
			}
			Dispatch.put(cell, OLE_CELL_VALUE, value);
		} catch (ComFailException ex) {// NOPMD
			LOGGER.error("exception while populating input cell " + modelParameterName + ". Cell is locked.");
			throw new BusinessException(ErrorCodes.ME00041, new Object[] { apiParameterName, modelParameterName }); // NOPMD
		}
	}

	private void readInoutCells(Dispatch workSheets, Map<String, List<Object>> changedValues,
			Map<String, Map<String, Object>> inputCell, Object value) throws BusinessException, SystemException {
		if (value instanceof Map) {
			populateInputCells((Map<String, Object>) value, workSheets);
		} else if (value instanceof List) {
			List<Map<String, Object>> aobList = (List<Map<String, Object>>) value;
			for (Map<String, Object> aob : aobList) {
				if (changedValues.get(aob.get(FIELD_MODEL_PARAMETER_NAME)) != null) {
					changedValues.get((String) aob.get(FIELD_MODEL_PARAMETER_NAME)).add(aob.get(FIELD_VALUE));
				} else {
					List<Object> array = new ArrayList<Object>();
					array.add(aob.get(FIELD_VALUE));
					changedValues.put((String) aob.get(FIELD_MODEL_PARAMETER_NAME), array);
					inputCell.put((String) aob.get(FIELD_MODEL_PARAMETER_NAME), aob);
				}
			}
		}
	}

	private Object buildArrayValues(List values, String cellRange, List existingCellArrayValues, Boolean isVerticalArray) {
		Object arrayValues = null;
		if (CollectionUtils.isNotEmpty(values)) {
			if (values.get(0) instanceof List) {
				// Handle 2-D array here
				Object[][] twoDCellValues = new Object[values.size()][];
				for (int i = 0; i < values.size(); i++) {
					List cellValues = (List) values.get(i);
					Object[] oneDCellValues = new Object[cellValues.size()];
					cellValues.toArray(oneDCellValues);
					twoDCellValues[i] = oneDCellValues;
				}
				arrayValues = twoDCellValues;
			} else {
				// set user provided values for the cells
				for (int i = 0; i < values.size(); i++) {
					existingCellArrayValues.set(i, values.get(i));
				}

				if (isVerticalArray) {
					Object[][] twoDCellValues = new Object[existingCellArrayValues.size()][1];
					for (int i = 0; i < existingCellArrayValues.size(); i++) {
						twoDCellValues[i][0] = existingCellArrayValues.get(i);
					}
					arrayValues = twoDCellValues;
				} else {
					Object[] oneDCellValues = new Object[existingCellArrayValues.size()];
					existingCellArrayValues.toArray(oneDCellValues);
					Object[][] twoDCellValues = new Object[1][];
					twoDCellValues[0] = oneDCellValues;
					arrayValues = twoDCellValues;
				}
			}
		}
		return arrayValues;
	}

	/**
	 * Read cell values
	 * 
	 * @param variant
	 * @param datatype
	 * @param isArray
	 * @return
	 * @throws SystemException
	 */
	private Object readValueFromSafeArray(Variant variant, String datatype, String rAdatatype, boolean isArray,
			String apiName, String modelParamName, int precession) throws SystemException {
		List outputList = new LinkedList();
		if (isArray) {
			populateArray(variant, datatype, outputList, apiName, precession, rAdatatype);
		} else {

			readPrimitive(variant, datatype, outputList, apiName, modelParamName, precession, rAdatatype);

		}
		return isArray ? outputList : outputList.get(0);
	}

	private Object readOutputValueFromSafeArray(Variant variant, String datatype, String rAdatatype, boolean isArray,
			String apiName, String modelParamName, int precession, List<String> errorList) throws SystemException {
		List outputList = new LinkedList();
		if (isArray) {
			populateArray(variant, datatype, outputList, apiName, precession, rAdatatype);
		} else {
			if (variant.getvt() != 10 && variant.getvt() != 1) {
				readPrimitive(variant, datatype, outputList, apiName, modelParamName, precession, rAdatatype);
			} else {
				readError(variant, apiName, modelParamName, errorList, outputList);
			}

		}
		return isArray ? outputList : outputList.get(0);
	}

	private void readError(Variant variant, String apiName, String modelParamName, List<String> errorList,
			List outputList) throws SystemException {
		if (variant.getvt() != 1) {
			String error = String.valueOf(variant.getError());
			switch (error) {
			case "-2146826259":
				errorList.add("Erroneous value returned by the model for API Parameter" + apiName + ", cell name "
						+ modelParamName + ".Value received from model is #Name?");
				outputList.add("Erroneous value returned by the model for API Parameter" + apiName + ", cell name "
						+ modelParamName + ".Value received from model is #Name?");
				break;
			case "-2146826273":
				errorList.add("Erroneous value returned by the model for API Parameter " + apiName + ", cell name "
						+ modelParamName + ".Value received from model is #Value!");
				outputList.add("Erroneous value returned by the model for API Parameter " + apiName + ", cell name "
						+ modelParamName + ".Value received from model is #Value!");
				break;
			case "-2146826265":
				errorList.add("Erroneous value returned by the model for API Parameter " + apiName + ", cell name "
						+ modelParamName + ".Value received from model is #Ref!");
				outputList.add("Erroneous value returned by the model for API Parameter " + apiName + ", cell name "
						+ modelParamName + ".Value received from model is #Ref!");
				break;
			case "-2146826281":
				errorList.add("Erroneous value returned by the model for API Parameter " + apiName + ", cell name "
						+ modelParamName + ".Value received from model is #DIV/0!");
				outputList.add("Erroneous value returned by the model for API Parameter " + apiName + ", cell name "
						+ modelParamName + ".Value received from model is #DIV/0!");
				break;
			default:
				errorList.add("Erroneous value returned by the model for API Parameter " + apiName + ", cell name "
						+ modelParamName + " Output cell is not readable");
				outputList.add("Erroneous value returned by the model for API Parameter " + apiName + ", cell name "
						+ modelParamName + " Output cell is not readable");

			}
		} else {
			errorList.add("Erroneous value returned by the model for API Parameter " + apiName + ", cell name "
					+ modelParamName + " Output cell is not readable");
			outputList.add("Erroneous value returned by the model for API Parameter " + apiName + ", cell name "
					+ modelParamName + "Output cell is not readable");

		}
	}

	/**
	 * Reads primitive value from excel cell
	 * 
	 * @param variant
	 * @param datatype
	 * @param outputList
	 * @throws SystemException
	 */

	private void readPrimitive(Variant variant, String datatype, List outputList, String apiName, String modelParamName,
			int precession, String rAdatatype) throws SystemException {
		try {
			if (variant.getvt() == 0) {
				outputList.add(null);
				return;
			}
			switch (ExcelDatatypes.valueOf(StringUtils.upperCase(datatype))) {
			case CURRENCY:
				if (StringUtils.endsWithIgnoreCase(rAdatatype, DOUBLE.getDatatype())) {
					if (precession > 0) {
						long value = variant.getCurrency().longValue();
						BigDecimal a = new BigDecimal(value / 10000.00);
						BigDecimal roundOff = a.setScale(precession, BigDecimal.ROUND_HALF_EVEN);
						outputList.add(roundOff.doubleValue());
					} else {
						long value = variant.getCurrency().longValue();
						outputList.add(value / 10000.00);
					}
				} else {
					long value = variant.getCurrency().longValue();
					outputList.add(value / 10000);
				}
				break;
			case PERCENTAGE:
				if (StringUtils.endsWithIgnoreCase(rAdatatype, DOUBLE.getDatatype())) {
					if (precession > 0) {
						BigDecimal a = new BigDecimal(variant.getDouble());
						BigDecimal roundOff = a.setScale(precession, BigDecimal.ROUND_HALF_EVEN);
						outputList.add(roundOff.doubleValue());
					} else {
						outputList.add(variant.getDouble());
					}
				} else {
					Double value = variant.getDouble();
					outputList.add(value.intValue());
				}
				break;
			case DOUBLE:
				if (precession > 0) {
					BigDecimal a = new BigDecimal(variant.getDouble());
					BigDecimal roundOff = a.setScale(precession, BigDecimal.ROUND_HALF_EVEN);
					outputList.add(roundOff.doubleValue());
				} else {
					outputList.add(variant.getDouble());
				}
				break;
			case LONG:
				outputList.add(variant.getLong());
				break;
			case BOOLEAN:
				outputList.add(variant.getBoolean());
				break;
			case INTEGER:
				try {
					outputList.add(variant.getInt());
				} catch (IllegalStateException e) {
					// Reading output primitive integer as double then type cast
					outputList.add((int) variant.getDouble());
				}
				break;
			case DATE:
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
					Calendar c = Calendar.getInstance();
					c.setTime(sdf.parse(BASE_DATE));
					c.add(Calendar.DATE, (int) variant.getDate());
					outputList.add(sdf.format(c.getTime()));
				} catch (ParseException e) {
					throw new SystemException(ErrorCodes.ME00044,
							new Object[] { datatype, apiName, variant.getDate(), DATE_FORMAT }); // NOPMD
				}
				break;
			case DATETIME:
				try {
					DateTimeFormatter dtf = DateTimeFormat.forPattern(DATE_TIME_FORMAT);
					DateTime dateTime = dtf.parseDateTime(BASE_DATE_TIME);
					dateTime = dateTime.plusDays((int) variant.getDate());
					dateTime = dateTime
							.plusSeconds((int) ((variant.getDate() - (int) variant.getDate()) * SECONDS_IN_DAY));
					outputList.add(dateTime);
				} catch (IllegalArgumentException e) {
					throw new SystemException(ErrorCodes.ME00044,
							new Object[] { datatype, apiName, variant.getDate(), DATE_TIME_FORMAT }); // NOPMD
				}
				break;
			case STRING:
			default:
				try {
					outputList.add(variant.getString());
				} catch (IllegalStateException ex) {// NOPMD
					outputList.add(String.valueOf(variant.getCurrency().longValue() / 10000));
				}
				break;
			}
		} catch (IllegalStateException ex) {// NOPMD
			LOGGER.error("exception while reading modelParameterName " + modelParamName);
			throw new SystemException(ErrorCodes.ME00043,
					new Object[] { apiName, modelParamName, datatype, dataTypeMap.get(variant.getvt()) }); // NOPMD
		}
	}

	/*
	 * Reads and populate excel cell values as array
	 */
	private void populateArray(Variant variant, String datatype, List outputList, String apiName, int precession,
			String rAdatatype) throws SystemException {
		SafeArray safeArray;
		safeArray = variant.toSafeArray();
		// int dim = safeArray.getNumDim();
		int lb1 = safeArray.getLBound(1);
		int lb2 = safeArray.getLBound(2);
		int ub1 = safeArray.getUBound(1);
		int ub2 = safeArray.getUBound(2);

		for (int lineNumber = lb1; lineNumber <= ub1; lineNumber++) {
			// for 2-D arrays the the upper and lower bounds are different
			if (lb1 != ub1 && lb2 != ub2) {
				List internalList = new ArrayList();
				populateArrayValues(safeArray, datatype, internalList, lineNumber, apiName, precession, rAdatatype);
				outputList.add(internalList);
			} else {
				// handle 1-D array
				populateArrayValues(safeArray, datatype, outputList, lineNumber, apiName, precession, rAdatatype);
			}
		}
	}

	/*
	 * Reads and populate excel row values as array depending on datatype
	 */
	private void populateArrayValues(SafeArray safeArray, String datatype, List rowList, int lineNumber, String apiName,
			int precession, String rAdatatype) throws SystemException {
		int lb2 = safeArray.getLBound(2);
		int ub2 = safeArray.getUBound(2);
		for (int columnNumber = lb2; columnNumber <= ub2; columnNumber++) {
			switch (ExcelDatatypes.valueOf(StringUtils.upperCase(datatype))) {
			case CURRENCY:
			case PERCENTAGE:
			case DOUBLE:
				Double dblValue = null;
				if (StringUtils.isNotBlank(safeArray.getString(lineNumber, columnNumber))) {
					dblValue = Double.parseDouble(safeArray.getString(lineNumber, columnNumber));
				}
				if (precession > 0 && dblValue != null) {
					BigDecimal a = new BigDecimal(dblValue);
					BigDecimal roundOff = a.setScale(precession, BigDecimal.ROUND_HALF_EVEN);
					rowList.add(roundOff.doubleValue());
				} else {
					rowList.add(dblValue);
				}

				break;
			case BOOLEAN:
				Boolean boolValue = null;
				if (StringUtils.isNotBlank(safeArray.getString(lineNumber, columnNumber))) {
					boolValue = Boolean.parseBoolean(safeArray.getString(lineNumber, columnNumber));
				}
				rowList.add(boolValue);
				break;
			case INTEGER:
				Integer intValue = null;
				if (StringUtils.isNotBlank(safeArray.getString(lineNumber, columnNumber))) {
					intValue = Integer.parseInt(safeArray.getString(lineNumber, columnNumber));
				}
				rowList.add(intValue);
				break;
			case LONG:
				Long longValue = null;
				if (StringUtils.isNotBlank(safeArray.getString(lineNumber, columnNumber))) {
					longValue = Long.parseLong(safeArray.getString(lineNumber, columnNumber));
				}
				rowList.add(longValue);
				break;
			case DATE:
				try {
					String dateString = null;
					if (StringUtils.isNotBlank(safeArray.getString(lineNumber, columnNumber))) {
						dateString = safeArray.getString(lineNumber, columnNumber);
						SimpleDateFormat nativeDateFormat = new SimpleDateFormat(NATIVE_DATE_FORMAT);
						Date dateValue = nativeDateFormat.parse(dateString);
						SimpleDateFormat requiredDateFormat = new SimpleDateFormat(DATE_FORMAT);
						rowList.add(requiredDateFormat.format(dateValue));
					} else {
						rowList.add(dateString);
					}
				} catch (ParseException e) {
					throw new SystemException(ErrorCodes.ME00044, new Object[] { datatype, apiName,
							safeArray.getString(lineNumber, columnNumber), DATE_FORMAT }); // NOPMD
				}
				break;
			case DATETIME:
				try {
					Double dateTimeValue = null;
					if (StringUtils.isNotBlank(safeArray.getString(lineNumber, columnNumber))) {
						dateTimeValue = Double.parseDouble(safeArray.getString(lineNumber, columnNumber));
						DateTimeFormatter dtf = DateTimeFormat.forPattern(DATE_TIME_FORMAT);
						DateTime dateTime = dtf.parseDateTime(BASE_DATE_TIME);
						dateTime = dateTime.plusDays(dateTimeValue.intValue());
						dateTime = dateTime
								.plusSeconds((int) ((dateTimeValue - dateTimeValue.intValue()) * SECONDS_IN_DAY));
						rowList.add(dateTime);
					} else {
						rowList.add(dateTimeValue);
					}
				} catch (IllegalArgumentException e) {
					throw new SystemException(ErrorCodes.ME00044, new Object[] { datatype, apiName,
							safeArray.getString(lineNumber, columnNumber), DATE_TIME_FORMAT }); // NOPMD
				}
				break;
			case STRING:
			default:
				String val = safeArray.getString(lineNumber, columnNumber);
				if (val.equalsIgnoreCase("")) {
					rowList.add(null);
				} else {
					rowList.add(val);
				}
				break;
			}
		}
	}

	/**
	 * Closes excel application by ignoring alerts,if any.
	 * 
	 * @param axc
	 * @param displayAlerts
	 */
	public static void quitExcel(final ActiveXComponent axc, boolean displayAlerts) {
		if (axc != null) {
			axc.setProperty(OLE_DISPLAY_ALERTS, displayAlerts);
			axc.invoke(OLE_QUIT, new Variant[] {});
		}
	}
	
	/**
	 * determines if Name based mapping
	 */
	public static boolean isNameBasedMapping(String modelParameterName) {
		Pattern primitiveNamedPattern = Pattern.compile(REG_EXP_NAMED_PRIMITIVE);
		Matcher primitiveNamedMatch = primitiveNamedPattern.matcher(modelParameterName);
		if(primitiveNamedMatch.find() && StringUtils.equals(primitiveNamedMatch.group(), modelParameterName)){
			return true;
		}
		return false;
	}
	
	/**
	 * finds a cell in excel
	 * @throws SystemException 
	 */
	public static Dispatch findCell(Dispatch worksheet, String cellRange, String sheetName) throws SystemException {
		Dispatch cell = null;
		try{
			cell = Dispatch
					.invoke(worksheet, OLE_RANGE, Dispatch.Get, new Object[] { cellRange }, new int[1])
					.toDispatch();
		} catch (ComFailException e) { // NOPMD
			LOGGER.error("Field " + cellRange + " is not found in sheet " + sheetName, e);
			SystemException.newSystemException(ErrorCodes.ME00047, new Object[] {cellRange, sheetName}); // NOPMD
		}
		return cell;
	}
	
	/**
	 * finds a worksheet in excel
	 * @throws SystemException 
	 */
	public static Dispatch findWorksheet(Dispatch workSheets, String sheetName) throws SystemException {
		Dispatch worksheet = null;
		try{
			worksheet = Dispatch
					.invoke(workSheets, OLE_ITEM, Dispatch.Get, new Object[] { sheetName }, new int[1])
					.toDispatch();
		} catch (ComFailException e) { // NOPMD
			LOGGER.error("Sheet " + sheetName + " is not found in the model", e);
			SystemException.newSystemException(ErrorCodes.ME00048, new Object[] {sheetName}); // NOPMD
		}
		return worksheet;
	}
	
	/**
	 * finds if a variant is 1D vertical array
	 */
	public static boolean findIfVerticalArray(Variant variant) {
		SafeArray safeArray;
		safeArray = variant.toSafeArray();
		int lbRow = safeArray.getLBound(1);
		int lbCol = safeArray.getLBound(2);
		int ubRow = safeArray.getUBound(1);
		int ubCol = safeArray.getUBound(2);
		if((ubCol == lbCol) && (ubRow > lbRow)){
			return true;
		} else{
			return false;
		}
	}

}