package com.ca.umg.business.util;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.joda.time.DateTime;

/**
 * @author nigampra
 *
 */
@SuppressWarnings("PMD")
public class InsertScriptCreatorOnLinux {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsertScriptCreatorOnLinux.class);

        public static void main(String[] args) {

                StringBuffer insertQuery = new StringBuffer(
                                "INSERT INTO `MODEL_EXEC_PACKAGES` (`ID`, `PACKAGE_NAME`, `PACKAGE_VERSION`, `PACKAGE_TYPE`,`PACKAGE_FOLDER`, `COMPILED_OS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `MODEL_EXEC_ENV_NAME`) VALUES (");
                //String basePackagePath = "C:\\Program Files\\R\\R-3.0.1\\library\\";
                //change to r installion on your local machine for generating insert scripts on local
                String basePackagePath = "D:\\software\\R-3.0.1\\library\\";
                //R installation path of aws-qe. uncomment below to run on aws-qe for insert script generation
                //String basePackagePath = "/usr/lib64/R/library";

                StringBuffer insertQueryCollated = new StringBuffer();

                File files = new File(basePackagePath);
                int i = 0;
                for (File file : files.listFiles()) {
                        if (file.isDirectory()) {
                                insertQuery = new StringBuffer(
                                                "INSERT INTO `MODEL_EXEC_PACKAGES` (`ID`, `PACKAGE_NAME`, `PACKAGE_VERSION`, `PACKAGE_TYPE`,`PACKAGE_FOLDER`, `COMPILED_OS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`, `MODEL_EXEC_ENV_NAME`) VALUES (");

                                //Long currentDateTimeMillis = new DateTime().getMillis();
                                Date date = new Date();
                                Long currentDateTimeMillis = date.getTime();
                                Map<String, String> tarDesc = InsertScriptCreatorOnLinux
                                                .getDescription(file.getAbsolutePath() + "/DESCRIPTION");
                                insertQuery
                                                .append("'")
                                                .append(UUID.randomUUID())
                                                //.append(i)
                                                .append("', '")
                                                //.append("'localhost', 'F6ACD2A3-D78D-4E36-A2D8-7420F7CF7537', '")
                                                .append(tarDesc.get("package")).append("', '")
                                                .append(tarDesc.get("version")).append("', 'BASE', '")
                                                .append(tarDesc.get("package"))
                                                .append("', 'LINUX', 'SYSTEM', ")
                                                .append(currentDateTimeMillis).append(", 'SYSTEM', ")
                                                .append(currentDateTimeMillis)
                                                .append(", 'R-3.1.2'")
                                                .append(");");
                                System.out.println(insertQuery);
                                insertQueryCollated.append(insertQuery);
                                i++;
                        }
                }

                try {
                		//chnage to the path where you want to write the output
                        Files.write(Paths.get("/opt/umg/r-bas-lib-insert-script-generator/insertQuery.txt"), insertQueryCollated.toString().getBytes());
                } catch (IOException e) {
            // TODO Auto-generated catch block
            LOGGER.error("IOException: ", e);
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
            LOGGER.error("IOException: ", e);
                }
                return desc;
        }
}
