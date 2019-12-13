package com.ca.umg.business.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nigampra
 * 
 */
@SuppressWarnings("PMD")
public class InsertScriptCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsertScriptCreator.class);

	public static void main(String[] args) {

		StringBuffer insertQuery = new StringBuffer(
				"INSERT INTO `MODEL_EXEC_PACKAGES` (`ID`, `TENANT_ID`, `MODEL_EXEC_ENV_ID`, `PACKAGE_NAME`, `PACKAGE_VERSION`, `PACKAGE_TYPE`,`PACKAGE_FOLDER`, `COMPILED_OS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES (");
		//change to the base path of your R installation
		String basePackagePath = "C:\\Program Files\\R\\R-3.0.1\\library\\";
		//base path for unix in denver QE
		//String basePackagePath = "/usr/lib64/R/library";
		
		StringBuffer insertQueryCollated = new StringBuffer();
		
		File files = new File(basePackagePath);
		int i = 0;
		for (File file : files.listFiles()) {
			if (file.isDirectory()) {
				insertQuery = new StringBuffer(
						"INSERT INTO `MODEL_EXEC_PACKAGES` (`ID`, `TENANT_ID`, `MODEL_EXEC_ENV_ID`, `PACKAGE_NAME`, `PACKAGE_VERSION`, `PACKAGE_TYPE`,`PACKAGE_FOLDER`, `COMPILED_OS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES (");
				
				Date date = new Date();
				Long currentDateTimeMillis = date.getTime();
				Properties tarDesc = InsertScriptCreator
						.getDescription(file.getAbsolutePath() + "/DESCRIPTION");
				insertQuery
						.append("'")
						.append(UUID.randomUUID())
						.append(i)
						.append("', ")
						.append("'localhost', 'F6ACD2A3-D78D-4E36-A2D8-7420F7CF7537', '")
						.append(tarDesc.get("Package")).append("', '")
						.append(tarDesc.get("Version")).append("', 'BASE', '")
						.append(tarDesc.get("Package"))
						.append("', 'LINUX', 'SYSTEM', ")
						.append(currentDateTimeMillis).append(", 'SYSTEM', ")
						.append(currentDateTimeMillis).append(");");
				System.out.println(insertQuery);
				insertQueryCollated.append(insertQuery);
				i++;
			}
		}
		
		try {
			//change to where you want to write the file with insert scripts
			Files.write(Paths.get("/usr/local/tomcat-UMG/insertscript/insertQuery.txt"), insertQueryCollated.toString().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
            LOGGER.error("IOException: ", e);
		}
	}

	private static Properties getDescription(String descFilePath) {
		Properties properties = null;
		try (InputStream is = new FileInputStream(new File(descFilePath))) {
			properties = new Properties();
			properties.load(is);
		} catch (IOException e) {
            LOGGER.error("IOException: ", e);
		}
		return properties;
	}
}