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
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.ExcelArrayOfObjectDetails;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.ModelIO;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.ModelMetadata;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.ObjectDataType;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.Parameter;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.PrimitiveDataType;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.Properties;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.UmgModel;

@Named
public class ExcelModelExcelReader extends AbstractModelExcelReader implements ModelExcelReader {

    @Inject
    private ExcelModelHelper excelModelExcelReaderHelper;

    @Inject
    private DefaultValueValidator defaultValueValidator;
    
    @Inject
    private AcceptableValueValidator acceptableValueValidator;
    
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelModelExcelReader.class);
    private static final String STR_OBJECT = "object";
    // private static final String FILE_ERROR_MASSEGE = "Provided output file for bulk processes is not currect";

    private ModelType modelType;

    public void setModelType(final ModelType modelType) {
        this.modelType = modelType;
    }

    @Override
    public ModelMetadata readMetadata(Sheet metadataSheet, List<String> errorList) {
        ModelMetadata metaData = new ModelMetadata();
        Row metadataRow = metadataSheet.getRow(1);
        Row headerRow = metadataSheet.getRow(0);
        Cell cellValue = null;
        for (int i = 0; i < 5; i++) {
            cellValue = metadataRow.getCell(i);
            if (i!= 4 && DatatypeValidator.cellIsNullOrEmpty(cellValue)) {
                String columnName = new DataFormatter().formatCellValue(headerRow.getCell(i));
                errorList.add("No value set for column : " + columnName + " in : metadata sheet");
                continue;
            }
            setMetadata(metaData, metadataRow, i);
        }
        return metaData;
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

        Map<String, Integer> parameterSequenceMap = new HashMap<String, Integer>();
        Set<String> modelParamNameSet	= new HashSet<String>();
        Set<String> objectNameSet	= new HashSet<String>();
        ExcelArrayOfObjectDetails excelArrayOfObjectDetails = new ExcelArrayOfObjectDetails();
        excelArrayOfObjectDetails.setArrayOfObjectFlag(Boolean.FALSE);
        excelArrayOfObjectDetails.setObjectApiName(null);
        excelArrayOfObjectDetails.setObjectLength(null);
        excelArrayOfObjectDetails.setMemberArrayDirection(null);
        
        for (int i = 1; i <= inputSheet.getLastRowNum(); i++) {
            Row row = inputSheet.getRow(i);
            if (!excelModelExcelReaderHelper.rowEmpty(row)) {
                errorList.addAll(validateRowData(row, inputSheet.getRow(0), sheetName, parameterSequenceMap, modelParamNameSet, objectNameSet, excelArrayOfObjectDetails));
            }
        }
        if (ExcelConstants.OUTPUTS.equalsIgnoreCase(sheetName) && modelType == ModelType.BULK) {
            validateBulkIOFile(inputSheet, errorList);
        }

        if (CollectionUtils.isEmpty(errorList)) {
            createModelIo(inputSheet, errorList, sheetName, modelIpOrOp);
        }

        return modelIpOrOp;
    }

    // checks the validity of row
    private List<String> validateRowData(Row dataRow, Row headerRow, String sheetName,
            Map<String, Integer> parameterSequenceMap, Set<String> modelParamNameSet, Set<String> objectNameSet, ExcelArrayOfObjectDetails excelArrayOfObjectDetails) {
        List<String> errorList = new ArrayList<>();
        String nativeDataType = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(dataRow.getCell(7)));
        String dataType = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(dataRow.getCell(6)));
        for (int i = 0; i < 13; i++) {
            Cell cellValue = dataRow.getCell(i);
            String apiName = new DataFormatter().formatCellValue(dataRow.getCell(1));
            String defaultValue = null;
            if(ExcelConstants.OUTPUTS.equals(sheetName)){
            	defaultValue = new DataFormatter().formatCellValue(dataRow.getCell(12));
            } else {
            	defaultValue = new DataFormatter().formatCellValue(dataRow.getCell(13));
            }
            String columnName = new DataFormatter().formatCellValue(headerRow.getCell(i));
            String modelParamName = new DataFormatter().formatCellValue(dataRow.getCell(2));
            String length = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(dataRow.getCell(8)));
            String pattern = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(dataRow.getCell(10)));
            switch (i) {
            case 0:
                excelModelExcelReaderHelper.validateSequenceColumn(cellValue, columnName, apiName, errorList, sheetName,
                        parameterSequenceMap, dataType);
                break;
            case 1:
                excelModelExcelReaderHelper.validateSpclCharsInName(cellValue, columnName, apiName, dataType, objectNameSet, errorList, sheetName);
                break;
            case 2:
            	excelModelExcelReaderHelper.validateModelParamName(cellValue, columnName, apiName, errorList, sheetName, dataType, modelParamNameSet);
            	break;
            case 3:
                if (DatatypeValidator.cellIsNullOrEmpty(cellValue)) {
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
            	excelModelExcelReaderHelper.validateDataType(cellValue, columnName, apiName, errorList, sheetName);
                break;
            case 7:
                excelModelExcelReaderHelper.validateNativeDataType(cellValue, columnName, apiName, errorList, sheetName, dataType, pattern);
                break;
            case 9:
                excelModelExcelReaderHelper.validatePrecision(cellValue, columnName, apiName, errorList, sheetName, dataType, nativeDataType);
                break;
            case 11:
            	excelModelExcelReaderHelper.validateModelParamNameDimension(cellValue, columnName, apiName, errorList, sheetName,
                        dataType, modelParamName, length, excelArrayOfObjectDetails);
                excelModelExcelReaderHelper.validateDimensionsColumn(cellValue, columnName, apiName, errorList, sheetName,
                        defaultValue, dataType);
                excelModelExcelReaderHelper.validateLengthColumn(cellValue, apiName, errorList, sheetName, defaultValue, dataType,
                        length);
                break;
            default:
                break;
            }
        }

        excelModelExcelReaderHelper.validateDateFormat(headerRow, dataRow, errorList, sheetName);
        Cell dataTypeCellValue = getDatatypeCellValue(dataRow);
        if(errorList.isEmpty()){
        	acceptableValueValidator.validateAcceptableValues(dataRow, headerRow, sheetName, errorList,
        			dataTypeCellValue);
        	defaultValueValidator.validateMandateAndDefaultValues(dataRow, headerRow, sheetName, errorList, dataTypeCellValue);
        }
        excelModelExcelReaderHelper.validateLengthAndPrecsn(dataRow, headerRow, sheetName, errorList);
        if(errorList.isEmpty() && DataTypeMapUtil.DATATYPE_DOUBLE.equalsIgnoreCase(dataType) && (DataTypeMapUtil.DATATYPE_CURRENCY.equalsIgnoreCase(nativeDataType) || DataTypeMapUtil.DATATYPE_PERCENTAGE.equalsIgnoreCase(nativeDataType))){
        	excelModelExcelReaderHelper.validateValuesPrecision(dataRow, errorList, sheetName);
        }

        return errorList;
    }

    private Cell getDatatypeCellValue(Row dataRow) {
        Cell dataTypeCellValue = null;
        Cell nativeDataTypeCell = dataRow.getCell(7);
        String nativeDataTypeCellValue = new DataFormatter().formatCellValue(nativeDataTypeCell);
        if (ExcelModelHelper.nativeDataTypeEnum.isValid(nativeDataTypeCellValue)) {
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
            if (!excelModelExcelReaderHelper.rowEmpty(row)) {
                Parameter parameter = new Parameter();
                String nativedataTypeCellVal = new DataFormatter().formatCellValue(row.getCell(7));
                switch (nativedataTypeCellVal.toLowerCase(Locale.ENGLISH)) {
                case DataTypeMapUtil.DATATYPE_OBJECT:
                    if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(11)))) {
                        setArrayValues(row, parameter, objectParams, paramList, errorList, sheetName, inputSheet.getRow(i + 1));
                    } else {
                        setObjectValues(row, parameter, objectParams, paramList, errorList, sheetName, inputSheet.getRow(i + 1));
                    }

                    break;
                case DataTypeMapUtil.DATATYPE_BOOLEAN:
                    setValuesForPrimitives(row, parameter, sheetName, objectParams, paramList);
                    break;
                case DataTypeMapUtil.DATATYPE_INTEGER:
                    setValuesForPrimitives(row, parameter, sheetName, objectParams, paramList);
                    break;
                case DataTypeMapUtil.DATATYPE_DOUBLE:
                    setValuesForPrimitives(row, parameter,sheetName, objectParams, paramList);
                    break;
                case DataTypeMapUtil.DATATYPE_STRING:
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

    private void setArrayValues(Row row, Parameter parameter, Map<String, Parameter> objectParams, List<Parameter> paramList,
            List<String> errorList, String sheetName, Row nextRow) throws BusinessException {
        setParameters(row, parameter,sheetName);
        Datatype datatype = new Datatype();
        ArrayDataType arrayDatatype = new ArrayDataType();
        if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(13)))) {
            arrayDatatype.setDefaultValue(new DataFormatter().formatCellValue(row.getCell(13)));
        }
        arrayDatatype.setLength(new DataFormatter().formatCellValue(row.getCell(8)));
        PrimitiveDataType primeDataType = new PrimitiveDataType();
        String datatypeVal = new DataFormatter().formatCellValue(row.getCell(6));
        if (STR_OBJECT.equals(datatypeVal)) {
            setObjectDataType(objectParams, row, parameter, datatype, arrayDatatype);

        } else {
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
        if (excelModelExcelReaderHelper.objectHasChild(nextRow, new DataFormatter().formatCellValue(row.getCell(1)), sheetName,
                errorList)) {
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

    private void setValuesForPrimitives(Row row, Parameter parameter, String sheetName, Map<String, Parameter> objectParams,
            List<Parameter> paramList) throws BusinessException {
        if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(11)))) {
            setArrayValues(row, parameter, objectParams, paramList, null, sheetName, null);
        } else {
            setParameters(row, parameter,sheetName);
            Datatype datatype = new Datatype();
            PrimitiveDataType primeDataType = new PrimitiveDataType();
            String datatypeVal = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(row.getCell(7)));
            if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(13)))) {
            	if(StringUtils.equalsIgnoreCase(datatypeVal, DataTypeMapUtil.DATATYPE_DATE) || StringUtils.equalsIgnoreCase(datatypeVal, DataTypeMapUtil.DATATYPE_DATETIME)) {
            		primeDataType.setDefaultValue(StringUtils.trimToEmpty(StringUtils.remove(new DataFormatter().formatCellValue(row.getCell(13)), '"')));
            	} else {
            		primeDataType.setDefaultValue(StringUtils.trimToEmpty(new DataFormatter().formatCellValue(row.getCell(13))));
            	}
            }
            if (datatypeVal.equals(DataTypeMapUtil.DATATYPE_DOUBLE) || datatypeVal.equals(DataTypeMapUtil.DATATYPE_STRING)
                    || datatypeVal.equals(DataTypeMapUtil.DATATYPE_BOOLEAN)
                    || datatypeVal.equals(DataTypeMapUtil.DATATYPE_INTEGER) || datatypeVal.equals(DataTypeMapUtil.DATATYPE_DATE)
                    || datatypeVal.equals(DataTypeMapUtil.DATATYPE_DATETIME) || datatypeVal.equals(DataTypeMapUtil.DATATYPE_CURRENCY)
                    || datatypeVal.equals(DataTypeMapUtil.DATATYPE_PERCENTAGE)) {
                datatypeVal = new DataFormatter().formatCellValue(row.getCell(6));
            } else if (ExcelConstants.FACTOR.equals(datatypeVal)) {
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

    private boolean validateBulkIOFile(Sheet outputSheet, List<String> errorList) throws BusinessException {
        List<BulkIOFile> staticBulkList = new ArrayList<BulkIOFile>();
        List<BulkIOFile> currentBulkList = new ArrayList<BulkIOFile>();
        staticBulkList = BulkIOUtil.getStaticList();
        currentBulkList = BulkIOUtil.getBulkiIOList(outputSheet);

        if (staticBulkList.equals(currentBulkList)) {
            return true;
        } else {
            BulkIOUtil.findErrorRootCause(staticBulkList, currentBulkList, errorList);
        }

        return false;
    }

}
