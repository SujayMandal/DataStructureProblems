

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


/**
 * @author nigampra
 * 
 */
@SuppressWarnings("PMD")
public class InsertScriptCreator {	
	

	public static void main(String[] args) {

		StringBuffer insertQuery = new StringBuffer(
				"INSERT INTO `MODEL_EXEC_PACKAGES` (`ID`, `PACKAGE_NAME`, `PACKAGE_VERSION`, `PACKAGE_TYPE`,`PACKAGE_FOLDER`, `COMPILED_OS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`,`EXECUTION_ENVIRONMENT`, `MODEL_EXEC_ENV_NAME`) VALUES (");
		//String basePackagePath = "C:\\Program Files\\R\\R-3.0.1\\library\\";
		//String basePackagePath = "D:\\software\\R-3.0.1\\library\\";
		String basePackagePath = "/usr/lib64/R/library";
		
		StringBuffer insertQueryCollated = new StringBuffer();
		
		File files = new File(basePackagePath);
		int i = 0;
		for (File file : files.listFiles()) {
			if (file.isDirectory()) {
				insertQuery = new StringBuffer(
						"INSERT INTO `MODEL_EXEC_PACKAGES` (`ID`, `PACKAGE_NAME`, `PACKAGE_VERSION`, `PACKAGE_TYPE`,`PACKAGE_FOLDER`, `COMPILED_OS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`,`EXECUTION_ENVIRONMENT`, `MODEL_EXEC_ENV_NAME`) VALUES (");
				
				//Long currentDateTimeMillis = new DateTime().getMillis();
				Date date = new Date();
				Long currentDateTimeMillis = date.getTime();
				Map<String, String> tarDesc = InsertScriptCreator
						.getDescription(file.getAbsolutePath() + "/DESCRIPTION");
				insertQuery
						.append("'")
						.append(UUID.randomUUID())						
						.append("', '")						
						.append(tarDesc.get("package")).append("', '")
						.append(tarDesc.get("version")).append("', 'BASE', '")
						.append(tarDesc.get("package"))
						.append("', 'LINUX', 'SYSTEM', ")
						.append(currentDateTimeMillis).append(", 'SYSTEM', ")
						.append(currentDateTimeMillis)
						.append(",'Linux','R-3.3.2'")
						.append(");");
				System.out.println(insertQuery);
				insertQueryCollated.append(insertQuery);
				i++;
			}
		}
		
		try {
			Files.write(Paths.get("/usr/local/tomcat-UMG/insertscript/insertQuery.txt"), insertQueryCollated.toString().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
		}
	}

	private static Map<String, String> getDescription(String descFilePath) {
		Map<String, String> desc = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File(
				descFilePath)));) {
			String line;
			String value;
			String[] splits;
			String key;
			while ((line = br.readLine()) != null) {
				value = "";
				if (line != null && line.trim() != null && line.contains(":")) {
					splits = line.split(":");
					key = splits[0].toLowerCase(Locale.getDefault()).trim();
					for (int i = 1; i < splits.length; ++i) {
						value = value + splits[i].trim();
					}						
					desc.put(key, value);
				}
			}
		} catch (IOException e) {
		
		}
		return desc;
	}
}