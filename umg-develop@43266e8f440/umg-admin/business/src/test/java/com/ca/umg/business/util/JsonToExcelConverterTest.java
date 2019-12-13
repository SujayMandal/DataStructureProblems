package com.ca.umg.business.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.umg.plugin.commons.excel.util.JsonToExcelConverterUtil;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class JsonToExcelConverterTest {
	private String inputJsonString;
	private String path = "com/ca/umg/business/util";
	private String input = "BATCH_OUTPUT_JSON.json";
	private String output = "BATCH_OUTPUT_EXCEL.xls";

	@Before
	public void init() throws Exception {
		URL url = this.getClass().getClassLoader().getResource(path + "/" + input);
		BufferedReader br = null;
		FileReader fr = null;
		try {
			fr = new FileReader(url.getPath());
			br = new BufferedReader(fr);
			StringBuilder outputBuilder = new StringBuilder();
			String outputJson;
			while ((outputJson = br.readLine()) != null) {
				outputBuilder.append(outputJson);
			}
			inputJsonString = outputBuilder.toString();
		} finally {
			IOUtils.closeQuietly(br);
			IOUtils.closeQuietly(fr);
		}
	}

	@Test
	public void test() throws Exception {
		InputStream in = null;
		FileOutputStream fileOut = null;
		try {
			Object jsonObj = JsonToExcelConverterUtil.parseJsonString(inputJsonString);
			URL url = this.getClass().getClassLoader().getResource(path + "/" + input);
			File file = new File(url.getPath());
			file.delete();
			Workbook wb;
			if (file.exists()) {
				in = new FileInputStream(file);
				wb = new HSSFWorkbook(in);
			} else {
				wb = new HSSFWorkbook();
			}
			// printJson("",wb,jsonObj);
			// wb.createSheet("Data");
			file.createNewFile();
			fileOut = new FileOutputStream(file);
			wb.write(fileOut);
			fileOut.close();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(fileOut);
		}

	}
}
