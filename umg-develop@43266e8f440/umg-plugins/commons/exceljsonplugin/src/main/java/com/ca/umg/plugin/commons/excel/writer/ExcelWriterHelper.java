package com.ca.umg.plugin.commons.excel.writer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.ca.umg.plugin.commons.excel.reader.constants.ExcelConstants;

public class ExcelWriterHelper {
	
	public static void generateNewCorrelationId(Sheet dataSheet) {
		short lastCellNum = -1;
		for (int i = 0; i <= dataSheet.getLastRowNum(); i++) {
			Row rowData = dataSheet.getRow(i);
			if (lastCellNum == -1) {
				lastCellNum = rowData.getLastCellNum();
			}
			Cell cellData = rowData.createCell(lastCellNum);
			if (i == 0) {
				cellData.setCellValue(ExcelConstants.CORR_ID_HEADER);
			} else {
				cellData.setCellValue(ExcelConstants.ROW_NO + i);
			}
		}
	}

	/*public static void fillMissingCorrelationIds(Sheet dataSheet, ExcelDetails xlValDetails) {
		List<Integer> missingIdList = xlValDetails.getMissingList();
		Integer counter = 1;
		for (Integer rownum : missingIdList) {
			Row row = dataSheet.getRow(rownum);
			Cell cell = row.getCell(xlValDetails.getCorrelationColumnIndex());
			if (cell != null) {
				cell.setCellValue(ExcelConstants.MISS_CORR_ID + counter++);
			} else {
				cell = row.createCell(xlValDetails.getCorrelationColumnIndex());
				cell.setCellValue(ExcelConstants.MISS_CORR_ID + counter++);
			}
		}
	}

	public static void rectifyDuplicates(Sheet dataSheet, ExcelDetails xlValDetails) {
		Map<String, List<Integer>> dupsMap = xlValDetails.getDuplicateMap();
		Integer counter = 1;
		Iterator<String> dupsItr = dupsMap.keySet().iterator();
		while (dupsItr.hasNext()) {
			String cellValue = dupsItr.next();
			List<Integer> dupsRowList = dupsMap.get(cellValue);
			if (dupsRowList.size() > 1) {
				for (int i = 0; i < dupsRowList.size(); i++) {
					if (i == 0) {
						continue;
					}
					Row row = dataSheet.getRow(dupsRowList.get(i));
					Cell cell = row.getCell(xlValDetails.getCorrelationColumnIndex());
					String existingDupsValue = null;
					cell.setCellType(Cell.CELL_TYPE_STRING);
					existingDupsValue = cell.getStringCellValue();
					cell.setCellValue(ExcelConstants.DUPS_CORR_ID + existingDupsValue + ExcelConstants.CORR_ID_HYPHEN + counter++);
				}
			}
		}
	}*/

}
