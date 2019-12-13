/**
 * 
 */
package com.ca.umg.plugin.commons.excel.xmlconverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.bo.ModelType;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;
import com.ca.umg.plugin.commons.excel.reader.exception.codes.ExcelPluginExceptionCodes;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.AcceptableValueValidator;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.ArrayDataType;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.Datatype;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.DatatypeValidator;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.DefaultValueValidator;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.Dimension;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.UmgModel;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.ModelIO;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.ObjectDataType;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.Parameter;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.PrimitiveDataType;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.Properties;

@Named
public class MatlabModelExcelReader extends AbstractModelExcelReader implements ModelExcelReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatlabModelExcelReader.class);
    private static final String STR_OBJECT = "object";
    @Inject
    private DefaultValueValidator defaultValueValidator;
    
    @Inject
    private AcceptableValueValidator acceptableValueValidator;

    @Inject
    private ModelExcelReaderHelper modelExcelReaderHelper;
    public void setModelType(final ModelType modelType) {
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

    private ModelIO readIOs(Sheet inputSheet, List<String> errorList, String sheetName) throws BusinessException {
        ModelIO modelIpOrOp = new ModelIO();
        Map<String, Parameter> objectParams = new HashMap<String, Parameter>();
        Map<String,Integer> parameterSequenceMap = new HashMap<String,Integer>(); 
        Set<String> objectNameSet	= new HashSet<String>();
        List<Parameter> paramList = new ArrayList<Parameter>();       
        for (int i = 1; i <= inputSheet.getLastRowNum(); i++) {
            Row row = inputSheet.getRow(i);
            if (!modelExcelReaderHelper.rowEmpty(row)) {
                errorList.addAll(validateRowData(row, inputSheet.getRow(0), sheetName, parameterSequenceMap, objectNameSet));
                Parameter parameter = new Parameter();             
                if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(11)))) {
                    setParameters(row, parameter,sheetName);
                    Datatype datatype = new Datatype();
                    ArrayDataType arrayDatatype = new ArrayDataType();
                    if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(13)))) {
                        arrayDatatype.setDefaultValue(new DataFormatter().formatCellValue(row.getCell(13)));
                    }
                    PrimitiveDataType primeDataType = new PrimitiveDataType();
                    String datatypeVal = new DataFormatter().formatCellValue(row.getCell(6));

                    arrayDatatype.setType(datatype);
                    Dimension dim = new Dimension();
                    String dims = new DataFormatter().formatCellValue(row.getCell(11));
                    String[] dimsArr = dims.split(ExcelConstants.CORR_ID_COMMA);
                    Integer[] dimensions = new Integer[dimsArr.length];
                    for (int j = 0; j < dimensions.length; j++) {
                        dimensions[j] = Integer.parseInt(dimsArr[j]);
                    }
                    dim.setDim(Arrays.asList(dimensions));
                    arrayDatatype.setDimension(dim);
                    Datatype arrayType = new Datatype();
                    if (STR_OBJECT.equals(datatypeVal)) {
                        setObjectDataType(objectParams, row, parameter, datatype, arrayDatatype);
                    } else {
                        setPrimeDatatype(row, datatype, primeDataType, datatypeVal);

                    }
                    arrayDatatype.setType(datatype);
                    arrayType.setArrayDatatype(arrayDatatype);
                    parameter.setDatatype(arrayType);

                    String name = parameter.getApiName();
                    int index = name.lastIndexOf(ExcelConstants.CORR_ID_DOT);
                    if (index > 0) {
                        String obj = name.substring(0, index);
                        String paramName = name.substring(index + 1, name.length());
                        parameter.setApiName(paramName);                      
                        Parameter childParam = objectParams.get(obj);
                        getObjectProperies(parameter, obj, childParam);
                    } else {
                        paramList.add(parameter);
                    }

                } else if (STR_OBJECT.equals(new DataFormatter().formatCellValue(row.getCell(6)))
                        && modelExcelReaderHelper.objectHasChild(inputSheet.getRow(i + 1),
                                new DataFormatter().formatCellValue(row.getCell(1)), sheetName, errorList)) {
                    setParameters(row, parameter,sheetName);
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
                        Parameter childParam = objectParams.get(obj);
                        getObjectProperies(parameter, obj, childParam);
                    } else {
                        paramList.add(parameter);
                    }
                    objectParams.put(new DataFormatter().formatCellValue(row.getCell(1)), parameter);
                } else {
                    setParameters(row, parameter, sheetName);
                    Datatype datatype = new Datatype();
                    PrimitiveDataType primeDataType = new PrimitiveDataType();
                    if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(13)))) {
                        primeDataType.setDefaultValue(new DataFormatter().formatCellValue(row.getCell(13)));
                    }
                    String datatypeVal = new DataFormatter().formatCellValue(row.getCell(6));
                    setPrimeDatatype(row, datatype, primeDataType, datatypeVal);
                    parameter.setDatatype(datatype);
                    String name = parameter.getApiName();
                    int index = name.lastIndexOf(ExcelConstants.CORR_ID_DOT);
                    if (index > 0) {
                        String obj = name.substring(0, index);
                        String paramName = name.substring(index + 1, name.length());
                        parameter.setApiName(paramName);                      
                        Parameter childParam = objectParams.get(obj);
                        getObjectProperies(parameter, obj, childParam);
                    } else {
                        paramList.add(parameter);
                    }

                }
            }
        }
        modelIpOrOp.setParameter(paramList);
        return modelIpOrOp;

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

    private void setObjectDataType(Map<String, Parameter> objectParams, Row row, Parameter parameter, Datatype datatype,
            ArrayDataType arrayDatatype) {
        ObjectDataType objDataType = new ObjectDataType();
        Properties p = new Properties();
        List<Parameter> propparamsList = new ArrayList<Parameter>();
        p.setParameter(propparamsList);
        objDataType.setProperties(p);
        datatype.setObject(objDataType);
        arrayDatatype.setType(datatype);
        arrayDatatype.setLength(new DataFormatter().formatCellValue(row.getCell(8)));
        objectParams.put(new DataFormatter().formatCellValue(row.getCell(1)), parameter);
    }

    // checks the validity of row
    private List<String> validateRowData(Row dataRow, Row headerRow, String sheetName, Map<String,Integer> parameterSequenceNumbers, Set<String> objectNameSet) {
        List<String> errorList = new ArrayList<>();
        // checks if the first 5 fields have any value or not
        Cell dataTypeCell = dataRow.getCell(6);
        Cell nativeDataTypeCell = dataRow.getCell(7);
        String apiName = new DataFormatter().formatCellValue(dataRow.getCell(1));
        String dataType = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(dataRow.getCell(6)));
        String defaultValue = null;
        if(ExcelConstants.OUTPUTS.equals(sheetName)){
        	defaultValue = new DataFormatter().formatCellValue(dataRow.getCell(12));
        } else {
        	defaultValue = new DataFormatter().formatCellValue(dataRow.getCell(13));
        }
        String length = new DataFormatter().formatCellValue(dataRow.getCell(8));
        for (int i = 0; i < 13; i++) {
            Cell cellValue = dataRow.getCell(i);
            String columnName = new DataFormatter().formatCellValue(headerRow.getCell(i));
            switch (i) {
            case 0:
                modelExcelReaderHelper.validateSequenceColumn(cellValue, columnName, apiName, errorList, sheetName,
                        parameterSequenceNumbers, dataType);
                break;
            case 1:
                modelExcelReaderHelper.validateSpclCharsInName(cellValue, columnName, apiName, dataType, objectNameSet, errorList, sheetName);
                break;
            case 3:
            	if (defaultValueValidator.cellIsNullOrEmpty(cellValue)) {
                    errorList.add("No value set to column : " + columnName + " for variable name : " + apiName + " in : "
                            + sheetName + " sheet");
                }
            	break;
            case 4:
            	defaultValueValidator.validateForMandateAndSeq(cellValue, columnName, apiName, errorList, sheetName);
                break;
            case 5:
            	defaultValueValidator.validateForMandateAndSeq(cellValue, columnName, apiName, errorList, sheetName);
                break;
            case 6:
                modelExcelReaderHelper.validateDataType(cellValue, columnName, apiName, errorList, sheetName);
                break;
            case 11:
            	modelExcelReaderHelper.validateDimensionsColumn(cellValue, columnName, apiName, errorList, sheetName, defaultValue,dataType);
            	modelExcelReaderHelper.validateLengthColumn(cellValue, apiName, errorList, sheetName, defaultValue, dataType, length);
            	break;
            default:
                break;
            }
        }

        Cell dataTypeCellValue = dataRow.getCell(6);
        acceptableValueValidator.validateAcceptableValues(dataRow, headerRow, sheetName, errorList,
    			dataTypeCellValue);
        defaultValueValidator.validateMandateAndDefaultValues(dataRow, headerRow, sheetName, errorList, dataTypeCellValue);
        // modelExcelReaderHelper.validateDefaultValueIfInteger (dataRow, headerRow, sheetName, errorList, dataTypeCellValue);
        modelExcelReaderHelper.validateLengthAndPrecsn(dataRow, headerRow, sheetName, errorList);
        modelExcelReaderHelper.validateDataTypesForMatlab(dataRow, headerRow, sheetName, errorList, dataTypeCell, nativeDataTypeCell);

        return errorList;
    }
}
