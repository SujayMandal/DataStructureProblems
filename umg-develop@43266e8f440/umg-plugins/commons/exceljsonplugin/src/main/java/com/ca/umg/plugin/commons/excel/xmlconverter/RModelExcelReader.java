/**
 * 
 */
package com.ca.umg.plugin.commons.excel.xmlconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;
import com.ca.umg.plugin.commons.excel.reader.exception.codes.ExcelPluginExceptionCodes;
import com.ca.umg.plugin.commons.excel.xmlconverter.ModelExcelReaderHelper.nativeDataTypeEnum;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.AcceptableValueValidator;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.ArrayDataType;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.Datatype;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.DefaultValueValidator;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.Dimension;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.ModelIO;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.ObjectDataType;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.Parameter;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.PrimitiveDataType;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.Properties;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.UmgModel;

@Named
public class RModelExcelReader extends AbstractModelExcelReader implements ModelExcelReader {

    @Inject
    private ModelExcelReaderHelper modelExcelReaderHelper;
    
    @Inject
    private DefaultValueValidator defaultValueValidator;
    
    @Inject
    private AcceptableValueValidator acceptableValueValidator;
    

    private static final Logger LOGGER = LoggerFactory.getLogger(RModelExcelReader.class);
    private static final String STR_OBJECT = "object";
    private static final String TWO = "2";
    private static final String MATRIX = "matrix";
    //private static final String FILE_ERROR_MASSEGE = "Provided output file for bulk processes is not currect";
    
    private ModelType modelType;
    
    public void setModelType(final ModelType modelType) {
    	this.modelType = modelType;
    }
    
    public UmgModel readSheets(Map<String, Sheet> sheets, List<String> errorList) throws BusinessException {
        UmgModel mModel = new UmgModel();
        mModel.setXmlns("http://com.ca.umg.matlab.io/xml/ns/umg-model-io");
        LOGGER.debug("Parsing metadata sheet");
        mModel.setMetadata(readMetadata(sheets.get("metadata"), errorList));
        LOGGER.debug("Parsing Inputs sheet and Errors of metadata are :" + errorList);
        mModel.setModelInput(readIOs(sheets.get(ExcelConstants.INPUTS), errorList, ExcelConstants.INPUTS));
        LOGGER.debug("Parsing Outputs sheet and errors of metadata and Inputs sheets are :" + errorList);
        mModel.setModelOutput(readIOs(sheets.get(ExcelConstants.OUTPUTS), errorList, ExcelConstants.OUTPUTS));
        LOGGER.debug("Parsing of Outputs sheet is completed.Error List " + errorList);
        return mModel;
    }

    public ModelIO readIOs(Sheet inputSheet, List<String> errorList, String sheetName) throws BusinessException {
        ModelIO modelIpOrOp = new ModelIO();
        Map<String,Integer> parameterSequenceMap = new HashMap<String,Integer>(); 
        Set<String> objectNameSet	= new HashSet<String>();
        for (int i = 1; i <= inputSheet.getLastRowNum(); i++) {
            Row row = inputSheet.getRow(i);
            if (!modelExcelReaderHelper.rowEmpty(row)) {
                errorList.addAll(validateRowData(row, inputSheet.getRow(0), sheetName, parameterSequenceMap, objectNameSet));
            }
        }
        if(sheetName.equalsIgnoreCase(ExcelConstants.OUTPUTS) && modelType == ModelType.BULK){
            validateBulkIOFile(inputSheet , errorList);
        }
        
        if (CollectionUtils.isEmpty(errorList)) {
            createModelIo(inputSheet, errorList, sheetName, modelIpOrOp);
        }
        
        return modelIpOrOp;
    }

    // checks the validity of row
    private List<String> validateRowData(Row dataRow, Row headerRow, String sheetName, Map<String,Integer> parameterSequenceMap, Set<String> objectNameSet) {
        List<String> errorList = new ArrayList<>();
        // checks if the first 6 fields have any value or not
        for (int i = 0; i < 13; i++) {
            Cell cellValue = dataRow.getCell(i);
            String columnName = new DataFormatter().formatCellValue(headerRow.getCell(i));
            String apiName = new DataFormatter().formatCellValue(dataRow.getCell(1));
            String dataType = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(dataRow.getCell(6)));
            String defaultValue = null;
            if(ExcelConstants.OUTPUTS.equals(sheetName)){
            	defaultValue = new DataFormatter().formatCellValue(dataRow.getCell(12));
            } else {
            	defaultValue = new DataFormatter().formatCellValue(dataRow.getCell(13));
            }
            String length = new DataFormatter().formatCellValue(dataRow.getCell(8));
            String dimension = new DataFormatter().formatCellValue(dataRow.getCell(11));
            switch (i) {
            case 0:
                modelExcelReaderHelper.validateSequenceColumn(cellValue, columnName, apiName, errorList, sheetName,
                		parameterSequenceMap, dataType);
                break;
            case 1:
                modelExcelReaderHelper.validateSpclCharsInName(cellValue, columnName, apiName, dataType, objectNameSet, errorList, sheetName);
                break;
            case 3:
            	if (defaultValueValidator.cellIsNullOrEmpty(cellValue)) {
            		errorList.add("No value set to column : " + columnName + " for variable name : <b>" + apiName + "</b> in : "
            				+ sheetName + " sheet");
            	}
            	break;
            case 4:
            	defaultValueValidator.validateForMandateAndSeq(cellValue, columnName, apiName, errorList, sheetName);
                break;
            case 5:
            	defaultValueValidator.validateForMandateAndSeq(cellValue, columnName, apiName, errorList, sheetName);
                break;
            case 7:
                modelExcelReaderHelper.validateNativeDataType(cellValue, columnName, apiName, errorList, sheetName);
                if(!errorList.isEmpty() && StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(new DataFormatter().formatCellValue(cellValue)), MATRIX) 
                		&& !StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(dimension), TWO)) {
                	errorList.add("Dimension incorrect for <b>" + apiName + "</b> in " + sheetName + " definition. Matrix native datatype requires"
                			+ " Dimension to be 2.");
                }
                break;
            case 11:
            	modelExcelReaderHelper.validateDimensionsColumn(cellValue, columnName, apiName, errorList, sheetName, defaultValue, dataType);
            	modelExcelReaderHelper.validateLengthColumn(cellValue, apiName, errorList, sheetName, defaultValue, dataType, length);
            	break;
            default:
                break;
            }
        }

        modelExcelReaderHelper.validateDateFormat(headerRow, dataRow, errorList, sheetName);
        modelExcelReaderHelper.checkNativeAndUmgDataTypeAssociation(dataRow, errorList, sheetName);
        Cell dataTypeCellValue = getDatatypeCellValue(dataRow);
    	acceptableValueValidator.validateAcceptableValues(dataRow, headerRow, sheetName, errorList,
    			dataTypeCellValue);
        defaultValueValidator.validateMandateAndDefaultValues(dataRow, headerRow, sheetName, errorList, dataTypeCellValue);
        modelExcelReaderHelper.validateLengthAndPrecsn(dataRow, headerRow, sheetName, errorList);

        return errorList;
    }

    private Cell getDatatypeCellValue(Row dataRow) {
        Cell dataTypeCellValue = null;
        Cell nativeDataTypeCell = dataRow.getCell(7);
        String nativeDataTypeCellValue = new DataFormatter().formatCellValue(nativeDataTypeCell);
        if (nativeDataTypeEnum.isValid(nativeDataTypeCellValue)) {
            dataTypeCellValue = dataRow.getCell(6);
        } else {
            dataTypeCellValue = nativeDataTypeCell;
        }
        return dataTypeCellValue;
    }

    private void createModelIo(Sheet inputSheet, List<String> errorList, String sheetName, ModelIO modelIpOrOp)
            throws BusinessException {
        Map<String, Parameter> objectParams = new HashMap<String, Parameter>();
        List<Parameter> paramList = new ArrayList<Parameter>();
        for (int i = 1; i <= inputSheet.getLastRowNum(); i++) {
            Row row = inputSheet.getRow(i);
            if (!modelExcelReaderHelper.rowEmpty(row)) {
                Parameter parameter = new Parameter();
                String nativedataTypeCellVal = new DataFormatter().formatCellValue(row.getCell(7));
                switch (nativedataTypeCellVal.toLowerCase(Locale.ENGLISH)) {
                case DataTypeMapUtil.DATATYPE_MATRIX:
                    setArrayValues(row, parameter, objectParams, paramList,errorList, sheetName, null);
                    // setObjectValues(row, parameter, objectParams, paramList, errorList, sheetName, inputSheet.getRow(i + 1));
                    break;
                case DataTypeMapUtil.DATATYPE_VECTOR:
                    setObjectValues(row, parameter, objectParams, paramList, errorList, sheetName, inputSheet.getRow(i + 1));
                    break;
                case DataTypeMapUtil.DATATYPE_FACTOR:
                	if(StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(11)))){
                		setArrayValues(row, parameter, objectParams, paramList,errorList, sheetName, null);
                	}else{
                		 setValuesForPrimitives(row, parameter, sheetName,objectParams, paramList);
                		
                	}
                    break;
                case DataTypeMapUtil.DATATYPE_DATAFRAME:
                	if(StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(11)))){
                		setArrayValues(row, parameter, objectParams, paramList, errorList, sheetName, inputSheet.getRow(i + 1));
                	}else{
                		 setObjectValues(row, parameter, objectParams, paramList, errorList, sheetName, inputSheet.getRow(i + 1));
                	}
                    break;
                case DataTypeMapUtil.DATATYPE_LIST:
                    if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(11)))) {
                        setArrayValues(row, parameter, objectParams, paramList, errorList, sheetName, inputSheet.getRow(i + 1));
                    } else {
                        setObjectValues(row, parameter, objectParams, paramList, errorList, sheetName, inputSheet.getRow(i + 1));
                    }

                    break;
                case DataTypeMapUtil.DATATYPE_NUMERIC:
                    setValuesForPrimitives(row, parameter, sheetName, objectParams, paramList);
                    break;
                case DataTypeMapUtil.DATATYPE_CHARACTER:
                    setValuesForPrimitives(row, parameter, sheetName, objectParams, paramList);
                    break;
                default:
                    setValuesForPrimitives(row, parameter, sheetName, objectParams, paramList);
                    break;
                }
            }
        }
        modelIpOrOp.setParameter(paramList);
    }

    private void setArrayValues(Row row, Parameter parameter, Map<String, Parameter> objectParams, List<Parameter> paramList,List<String> errorList, String sheetName, Row nextRow)
            throws BusinessException {    	
        setParameters(row, parameter, sheetName);
        Datatype datatype = new Datatype();
        ArrayDataType arrayDatatype = new ArrayDataType();
        if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(13)))) {
            arrayDatatype.setDefaultValue(new DataFormatter().formatCellValue(row.getCell(13)));
        }
        arrayDatatype.setLength(new DataFormatter().formatCellValue(row.getCell(8)));
        PrimitiveDataType primeDataType = new PrimitiveDataType();
        String datatypeVal = new DataFormatter().formatCellValue(row.getCell(6));
        if(STR_OBJECT.equals(datatypeVal)){
        	setObjectDataType(objectParams, row, parameter, datatype, arrayDatatype);      
        
       
        }else{
        	 setPrimeDatatype(row, datatype, primeDataType, datatypeVal);
        }
        arrayDatatype.setType(datatype);
        Dimension dim = new Dimension();
        String dims = new DataFormatter().formatCellValue(row.getCell(11));
        Integer[] dimensions = null;
        if (org.apache.commons.lang.StringUtils.isNotBlank(dims)) {
            String[] dimsArr = dims.split(ExcelConstants.CORR_ID_COMMA);
            dimensions = new Integer[dimsArr.length];
            for (int j = 0; j < dimensions.length; j++) {
                dimensions[j] = Integer.parseInt(dimsArr[j]);
            }
            dim.setDim(Arrays.asList(dimensions));
        }
        arrayDatatype.setDimension(dim);
        arrayDatatype.setLength(new DataFormatter().formatCellValue(row.getCell(8)));
        Datatype arrayType = new Datatype();
        arrayType.setArrayDatatype(arrayDatatype);
        
        
           
        parameter.setDatatype(arrayType);
        String name = parameter.getApiName();
        int index = name.lastIndexOf(ExcelConstants.CORR_ID_DOT);
        if (index > 0) {
            String obj = name.substring(0, index);
            String paramName = name.substring(index + 1, name.length());
            parameter.setApiName(paramName);          
            Parameter p = objectParams.get(obj);          
           
            getObjectProperies(parameter, obj, p);
        } else {
            paramList.add(parameter);
        }
    }

    private void setObjectValues(Row row, Parameter parameter, Map<String, Parameter> objectParams, List<Parameter> paramList,
            List<String> errorList, String sheetName, Row nextRow) throws BusinessException {
        if (modelExcelReaderHelper.objectHasChild(nextRow, new DataFormatter().formatCellValue(row.getCell(1)), sheetName,
                errorList)) {        	
            setParameters(row, parameter, sheetName);
            Datatype datatype = new Datatype();
            ObjectDataType objDataType = new ObjectDataType();
            Properties p = new Properties();
            List<Parameter> propparamsList = new ArrayList<Parameter>();
            p.setParameter(propparamsList);
            objDataType.setProperties(p);
            datatype.setObject(objDataType);
            parameter.setDatatype(datatype);
            String name = parameter.getApiName();
            int index = parameter.getApiName().lastIndexOf(".");
            if (index > 0) {
                String obj = name.substring(0, index);
                String paramName = name.substring(index + 1, name.length());
                parameter.setApiName(paramName);              
                Parameter pa = objectParams.get(obj);
                if (pa != null) {                   
                    Parameter childParam = objectParams.get(obj);
                    getObjectProperies(parameter, obj, childParam);
                } else {
                    throw new BusinessException(ExcelPluginExceptionCodes.EXPL000026, new Object[] { obj });
                }
            } else {
                paramList.add(parameter);
            }
            objectParams.put(new DataFormatter().formatCellValue(row.getCell(1)), parameter);
        }
    }

    private void setValuesForPrimitives(Row row, Parameter parameter, String sheetName,  Map<String, Parameter> objectParams,
            List<Parameter> paramList) throws BusinessException {
        if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(11)))) {
            setArrayValues(row, parameter, objectParams, paramList,null, sheetName, null);
        } else {         	
            setParameters(row, parameter, sheetName);           
            Datatype datatype = new Datatype();
            PrimitiveDataType primeDataType = new PrimitiveDataType();
            if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(13)))) {
                primeDataType.setDefaultValue(StringUtils.trimToEmpty(new DataFormatter().formatCellValue(row.getCell(13))));
            }
            String datatypeVal = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(row.getCell(7)));
            if (datatypeVal.equals(DataTypeMapUtil.DATATYPE_NUMERIC) || datatypeVal.equals(DataTypeMapUtil.DATATYPE_CHARACTER)
                    || datatypeVal.equals(DataTypeMapUtil.DATATYPE_LOGICAL) || datatypeVal.equals(DataTypeMapUtil.DATATYPE_INTEGER)) {
                datatypeVal = new DataFormatter().formatCellValue(row.getCell(6));
            }else if(datatypeVal.equals("factor")){
            	   datatypeVal = new DataFormatter().formatCellValue(row.getCell(6));
            	
            }
            setPrimeDatatype(row, datatype, primeDataType, datatypeVal);

            parameter.setDatatype(datatype);
            String name = parameter.getApiName();
            int index = name.lastIndexOf(ExcelConstants.CORR_ID_DOT);
            if (index > 0) {
                String obj = name.substring(0, index);
                String paramName = name.substring(index + 1, name.length());
                parameter.setApiName(paramName);             
                Parameter p = objectParams.get(obj);
                if (p != null) {                    
                    Parameter childParam = objectParams.get(obj);
                    getObjectProperies(parameter, obj, childParam);                    
                } else {
                    throw new BusinessException(ExcelPluginExceptionCodes.EXPL000026, new Object[] { obj });
                }
            } else {
                paramList.add(parameter);
            }
        }
    }
    
    private void setObjectDataType(Map<String, Parameter> objectParams, Row row, Parameter parameter, Datatype datatype,
            ArrayDataType arrayDatatype) {
        ObjectDataType objDataType = new ObjectDataType();
        Properties p = new Properties();
        List<Parameter> propparamsList = new ArrayList<Parameter>();
        p.setParameter(propparamsList);
        objDataType.setProperties(p);
        datatype.setObject(objDataType);
        
        arrayDatatype.setType(datatype);
        objectParams.put(new DataFormatter().formatCellValue(row.getCell(1)), parameter);
    }
    
    private void getObjectProperies(Parameter parameter, String obj, Parameter childParam) throws BusinessException {
        if (childParam != null) {
            Properties props = null;
            if (childParam.getDatatype().getObject() == null) {
                props = childParam.getDatatype().getArrayDatatype().getType().getObject().getProperties();
            } else {
                props = childParam.getDatatype().getObject().getProperties();
            }
            props.getParameter().add(parameter);
        } else {
            throw new BusinessException(ExcelPluginExceptionCodes.EXPL000026, new Object[] { obj });
        }
    }
    
    private boolean validateBulkIOFile(Sheet outputSheet ,  List<String> errorList) throws BusinessException{
    	List<BulkIOFile> staticBulkList = new ArrayList<BulkIOFile>();
    	List<BulkIOFile> currentBulkList = new ArrayList<BulkIOFile>();
    	staticBulkList = BulkIOUtil.getStaticList();
    	currentBulkList = BulkIOUtil.getBulkiIOList(outputSheet);
    	
    	if(staticBulkList.equals(currentBulkList)){
    		return true;
    	} else {
    		BulkIOUtil.findErrorRootCause(staticBulkList, currentBulkList,errorList);
    	}
    	
    	return false;
    }

}
