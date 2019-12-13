package com.ca.umg.plugin.commons.excel.xmlconverter;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.plugin.commons.excel.reader.ExcelReadHelper;
import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.Datatype;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.DatatypeValidator;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.ModelMetadata;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.Parameter;
import com.ca.umg.plugin.commons.excel.xmlconverter.entity.PrimitiveDataType;

public abstract class AbstractModelExcelReader {

    @Inject
    private ModelExcelReaderHelper modelExcelReaderHelper;

    @Inject
    private DatatypeValidator datatypeValidator;
    
    @Inject
    ExcelReadHelper excelReadHelper;

    public ModelMetadata readMetadata(Sheet metadataSheet, List<String> errorList) {
        ModelMetadata metaData = new ModelMetadata();
        Row metadataRow = metadataSheet.getRow(1);
        Row headerRow = metadataSheet.getRow(0);
        Cell cellValue = null;
        for (int i = 0; i < 5; i++) {
            cellValue = metadataRow.getCell(i);
            if (datatypeValidator.cellIsNullOrEmpty(cellValue)) {
                String columnName = new DataFormatter().formatCellValue(headerRow.getCell(i));
                errorList.add("No value set for column : " + columnName + " in : metadata sheet");
                continue;
            }
            setMetadata(metaData, metadataRow, i);
        }
        return metaData;
    }

    protected void setMetadata(ModelMetadata metaData, Row metadataRow, int i) {
        switch (i) {
        case 0:
            metaData.setModelName(new DataFormatter().formatCellValue(metadataRow.getCell(0)));
            break;
        case 1:
            metaData.setModelVersion(new DataFormatter().formatCellValue(metadataRow.getCell(1)));
            break;
        case 2:
            metaData.setModelPublisher(new DataFormatter().formatCellValue(metadataRow.getCell(2)));
            break;
        case 3:
            metaData.setModelClass(new DataFormatter().formatCellValue(metadataRow.getCell(3)));
            break;
        case 4:
            metaData.setModelMethod(new DataFormatter().formatCellValue(metadataRow.getCell(4)));
            break;
        }
    }

    public void setPrimeDatatype(Row row, Datatype datatype, PrimitiveDataType primeDataType, String datatypeVal)
            throws BusinessException {
        String datatypeValCopy = StringUtils.trimToEmpty(datatypeVal);
        switch (datatypeValCopy.toLowerCase(Locale.ENGLISH)) {
        case DataTypeMapUtil.DATATYPE_DOUBLE:
            if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(8)))) {
                primeDataType.setLength(new DataFormatter().formatCellValue(row.getCell(8)));
            }
            if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(9)))) {
                primeDataType.setFractionDigits(new DataFormatter().formatCellValue(row.getCell(9)));
            }
            datatype.setDoubletype(primeDataType);
            break;
        case DataTypeMapUtil.DATATYPE_BIGDECIMAL:
            if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(8)))) {
                primeDataType.setLength(new DataFormatter().formatCellValue(row.getCell(8)));
            }
            if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(9)))) {
                primeDataType.setFractionDigits(new DataFormatter().formatCellValue(row.getCell(9)));
            }
            datatype.setBigDecimaltype(primeDataType);
            break;
        case DataTypeMapUtil.DATATYPE_STRING:
            if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(10)))) {
                primeDataType.setPattern(StringUtils.trim(new DataFormatter().formatCellValue(row.getCell(10))));
            }
            if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(8)))) {
                primeDataType.setLength(new DataFormatter().formatCellValue(row.getCell(8)));
            }
            datatype.setStringtype(primeDataType);
            break;
        case DataTypeMapUtil.DATATYPE_INTEGER:
            if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(8)))) {
                primeDataType.setLength(new DataFormatter().formatCellValue(row.getCell(8)));
            }
            datatype.setIntegertype(primeDataType);
            break;
        case DataTypeMapUtil.DATATYPE_LONG:
            if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(8)))) {
                primeDataType.setLength(new DataFormatter().formatCellValue(row.getCell(8)));
            }
            datatype.setLongtype(primeDataType);
            break;
        case DataTypeMapUtil.DATATYPE_BIGINTEGER:
            if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(8)))) {
                primeDataType.setLength(new DataFormatter().formatCellValue(row.getCell(8)));
            }
            datatype.setBigIntegertype(primeDataType);
            break;
        case DataTypeMapUtil.DATATYPE_DATE:
            if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(10)))) {
                primeDataType.setPattern(StringUtils.trim(new DataFormatter().formatCellValue(row.getCell(10))));
            }
            if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(8)))) {
                primeDataType.setLength(new DataFormatter().formatCellValue(row.getCell(8)));
            }
            datatype.setDatetype(primeDataType);
            break;
        case DataTypeMapUtil.DATATYPE_DATETIME:
            if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(8)))) {
                primeDataType.setLength(new DataFormatter().formatCellValue(row.getCell(8)));
            }
            primeDataType.setPattern(StringUtils.trim(new DataFormatter().formatCellValue(row.getCell(10))));
            datatype.setDatetime(primeDataType);
            break;
        case DataTypeMapUtil.DATATYPE_BOOLEAN:
            if (StringUtils.isNotBlank(new DataFormatter().formatCellValue(row.getCell(8)))) {
                primeDataType.setLength(new DataFormatter().formatCellValue(row.getCell(8)));
            }
            datatype.setBooleantype(primeDataType);
            break;

        }
    }

    public void setParameters(Row row, Parameter parameter,String sheetName) {
        parameter.setSequence(StringUtils.trimToEmpty(new DataFormatter().formatCellValue(row.getCell(0))));
        parameter.setApiName(StringUtils.trimToEmpty(new DataFormatter().formatCellValue(row.getCell(1))));
        parameter.setModelParamName(StringUtils.trimToEmpty(new DataFormatter().formatCellValue(row.getCell(2))));
        if (StringUtils.isEmpty(parameter.getModelParamName())) {
            parameter.setModelParamName(parameter.getApiName());
        }
        parameter.setDescription(StringUtils.trimToEmpty(new DataFormatter().formatCellValue(row.getCell(3))));
        String mandatoryStr = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(row.getCell(4)));
        parameter.setMandatory(StringUtils.isBlank(mandatoryStr) ? mandatoryStr : mandatoryStr.toLowerCase());
        String syndicateStr = StringUtils.trimToEmpty(new DataFormatter().formatCellValue(row.getCell(5)));
        parameter.setSyndicate(StringUtils.isBlank(syndicateStr) ? syndicateStr : syndicateStr.toLowerCase());
        parameter.setNativeDataType(StringUtils.trimToEmpty(new DataFormatter().formatCellValue(row.getCell(7)))); 
		List<Object> acceptableValueList = null;        
		String acceptableValue = new DataFormatter().formatCellValue(row.getCell(12));	
		if(StringUtils.equals(ExcelConstants.INPUTS, sheetName) && StringUtils.isNotBlank(acceptableValue)){		
	     	  parameter.setAcceptableValues(acceptableValue);
	    }
		
    }

}