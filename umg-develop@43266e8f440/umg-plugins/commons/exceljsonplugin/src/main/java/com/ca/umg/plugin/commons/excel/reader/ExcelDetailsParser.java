package com.ca.umg.plugin.commons.excel.reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;

public class ExcelDetailsParser {
	
	public ExcelDetails validateExcelForCorrelationId(Sheet dataSheet) {
		ExcelDetails excelDetails = new ExcelDetails();
		for (int rowCtr = 0; rowCtr <= dataSheet.getLastRowNum(); rowCtr++) {
			Row rowData = dataSheet.getRow(rowCtr);
			short startCtr = rowData.getFirstCellNum();
			short endCtr = rowData.getLastCellNum();
			if (rowCtr == 0) {
				checkForCorrelationHeader(rowData, startCtr, endCtr, excelDetails);
			} else {
				checkForMissingCorrelationId(rowData, excelDetails, rowCtr);
                // Commented below to check duplicates after writting missing correlation ids
                // checkForDuplicates(rowData, excelDetails, rowCtr);
			}
			if (!excelDetails.hasCorrelationId()) {
				break;
			}
		}
		return excelDetails;
	}

    // written below to check duplicates after writting missing correlation ids
    public ExcelDetails duplicateCheck(Sheet dataSheet, ExcelDetails excelDetails) {
        for (int rowCtr = 0; rowCtr <= dataSheet.getLastRowNum(); rowCtr++) {
            Row rowData = dataSheet.getRow(rowCtr);
            if (rowData != null) {
                checkForDuplicates(rowData, excelDetails, rowCtr);
            }
        }
        return excelDetails;
    }

	private void checkForDuplicates(Row rowData, ExcelDetails excelDetails, int rowCtr) {
		Cell cell = rowData.getCell(excelDetails.getCorrelationColumnIndex());
		String cellValue = null;
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cellValue = cell.getStringCellValue().trim();
		}
		if (!StringUtils.isEmpty(cellValue)) {
			Map<String, List<Integer>> dupsMap = excelDetails.getDuplicateMap();
			if (dupsMap.containsKey(cellValue)) {
				dupsMap.get(cellValue).add(rowCtr);
				excelDetails.setHasDuplicates(true);
			} else {
				List<Integer> dupsRowList = new ArrayList<Integer>();
				dupsRowList.add(rowCtr);
				dupsMap.put(cellValue, dupsRowList);
			}
		}
	}

	private void checkForMissingCorrelationId(Row rowData, ExcelDetails excelDetails, Integer rowCtr) {
		Cell cell = rowData.getCell(excelDetails.getCorrelationColumnIndex());
		String cellValue = null;
		if (cell != null) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cellValue = cell.getStringCellValue().trim();
		}
		if (StringUtils.isEmpty(cellValue)) {
			excelDetails.setHasMissingCorrelationIds(true);
			excelDetails.getMissingList().add(rowCtr);
		}
	}

	private void checkForCorrelationHeader(Row rowData, short startCtr, short endCtr, ExcelDetails excelValidationDetails) {
		for(short ctr = startCtr; ctr < endCtr; ctr++) {
			Cell cell = rowData.getCell(ctr);
			if (cell.getStringCellValue().equalsIgnoreCase(ExcelConstants.CORR_ID_HEADER)) {
				excelValidationDetails.setHasCorrelationId(true);
				excelValidationDetails.setCorrelationColumnIndex(ctr);
				break;
			}
		}
	}

}
