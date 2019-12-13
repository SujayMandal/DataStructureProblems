package com.ca.umg.plugin.commons.excel.converter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.umg.plugin.commons.excel.util.JsonToExcelConverterUtil;

public class JsonToExcelPreparator implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonToExcelPreparator.class);
	
	private final JsonToExcelBatchContainer jsonToExcelBatchContainer;

	public JsonToExcelPreparator(
			final JsonToExcelBatchContainer jsonToExcelBatchContainer) {
		this.jsonToExcelBatchContainer = jsonToExcelBatchContainer;
	}

	@Override
	public void run() {
		if (jsonToExcelBatchContainer.isWriteOutputToFile()) {
			Workbook wb = new HSSFWorkbook();
			File file = new File(jsonToExcelBatchContainer.getLocation());
			file.delete();
			fillWorkBook(wb);
			try {
				file.createNewFile();
				FileOutputStream fileOut = new FileOutputStream(file);
				wb.write(fileOut);
				fileOut.close();
			} catch (Exception e) {
				LOGGER.error("Exception: ", e);
			}
		} else {
			Workbook wb = null;
			if(jsonToExcelBatchContainer.getExtension().equals("xlsx")){
				wb = new XSSFWorkbook();
			}else{
				wb = new HSSFWorkbook();
			}
			try {
				fillWorkBook(wb);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				wb.write(baos);
				jsonToExcelBatchContainer.setBatchOutput(baos);
			} catch (Exception e) {
				LOGGER.error("Exception: ", e);
			}
		}
	}

	private void fillWorkBook(final Workbook wb) {
		while (true) {
			if (jsonToExcelBatchContainer.isEmptyQueue()
					&& jsonToExcelBatchContainer.isFinished()) {
				break;
			}
			if (!jsonToExcelBatchContainer.isEmptyQueue()) {
				TransactionElement ele = jsonToExcelBatchContainer
						.getTransactionElementQueue().poll();
				JsonToExcelConverterUtil.prepareWorkbook(wb, ele, true);
			}
		}
	}

}