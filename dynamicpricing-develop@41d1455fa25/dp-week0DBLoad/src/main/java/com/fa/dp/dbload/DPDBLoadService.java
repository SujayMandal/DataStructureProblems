package com.fa.dp.dbload;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.UUID;

public class DPDBLoadService {

	private static final Logger LOGGER = LogManager.getLogger(DPDBLoadService.class);

	public static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
			  .appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
			  .appendOptional(DateTimeFormatter.ofPattern("MM/d/yyyy"))
			  .appendOptional(DateTimeFormatter.ofPattern("M/dd/yyyy"))
			  .appendOptional(DateTimeFormatter.ofPattern("M/d/yyyy"))
			  .appendOptional(DateTimeFormatter.ofPattern("M/d/yy"))
			  .appendOptional(DateTimeFormatter.ofPattern("MM/d/yy"))
			  .appendOptional(DateTimeFormatter.ofPattern("M/dd/yy"))
			  .toFormatter()
			  .withZone(ZoneId.of("UTC"));


	private static final String DP_PROCESS_INSERT_QUERY = "INSERT INTO `DYNAMIC_PRICING_FILE_PRCS_STATUS` (`ID`, `INPUT_FILE_NAME`, `SYS_GNRTD_INPUT_FILE_NAME`, `STATUS`, `PROCESS`, `UPLOAD_TIMESTAMP`, `CREATED_BY`, `CREATED_ON`) VALUES (";

	private static final String DP_WEEK0_INSERT_QUERY = "INSERT INTO `DP_WEEK0_PARAMS` (`ID`, `DYNAMIC_PRICING_FILE_ID`, `UPLOAD_FLAG`, `ASSET_NUMBER`, `CLIENT_CODE`, `STATUS`, `ASSET_VALUE`, `AV_SET_DATE`, `LIST_PRICE`, `CLASSIFICATION`, `ELIGIBLE`, `ASSIGNMENT`, `WEEK_0_PRICE`, `STATE`, `RT_SOURCE`, `NOTES`, `PROPERTY_TYPE`, `ASSIGNMENT_DATE`, `ENSEMBLE`, `NOTES_RA`, `PR_MODE`, `UPDATE_TIMESTAMP`, `CREATED_BY`, `CREATED_ON`) VALUES ";

	private static final String DP_WEEKNPRCSSTATUS_INSERT_QUERY = "INSERT INTO `DP_WEEKN_PRCS_STATUS` (`ID`, `INPUT_FILE_NAME`, `SYS_GNRTD_INPUT_FILE_NAME`, `STATUS`, `PROCESS`, `CREATED_BY`, `CREATED_ON`) VALUES (";

	private static final String DP_WEEKN_PARAMS_INSERT_QUERY = "INSERT INTO `DP_WEEKN_PARAMS` (`ID`, `ASSET_NUMBER`, `DP_WEEKN_PRCS_STATUS_ID`, `ELIGIBLE`,  `MOST_RECENT_LIST_END_DATE`, `MOST_RECENT_LIST_STATUS`, `MOST_RECENT_PROPERTY_STATUS`, `MOST_RECENT_LIST_PRICE`, `LP_PERCENT_ADJUSTMENT_REC`, `LP_DOLLAR_ADJUSTMENT_REC`, `MODEL_VERSION`, `DELIVERY_DATE`, `CLASSIFICATION`, `CREATED_BY`, `CREATED_ON`) VALUES ";

	private static final String DP_WEEKN_AUDIT_INSERT_QUERY = "INSERT INTO `DP_WEEKN_AUDIT_REPORTS` (`ID`, `RUN_DATE`, `DELIVERY_DATE`, `LOAN_NUMBER`, `CLASSIFICATION`, `ACTION`, `PERMANENT_EXCLUSION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ";

	private static final String DP_WEEKN_PARAMS_UPDATE_QUERY = "UPDATE `DP_WEEKN_PARAMS` SET `OLD_ASSET_NUMBER` = '?', `ASSET_NUMBER` = '?', `PROP_TEMP` = '?' WHERE  `ASSET_NUMBER` = '?' OR `OLD_ASSET_NUMBER` = '?';";

	private static final String DP_WEEKN_PARAMS_ORIGINAL_UPDATE_QUERY = "UPDATE `DP_WEEKN_PARAMS_ORIGINAL` SET `OLD_ASSET_NUMBER` = '?', `ASSET_NUMBER` = '?', `PROP_TEMP` = '?' WHERE  `ASSET_NUMBER` = '?' OR `OLD_ASSET_NUMBER` = '?';";

	private static final String DP_WEEK0_PARAMS_UPDATE_QUERY = "UPDATE `DP_WEEK0_PARAMS` SET `OLD_ASSET_NUMBER` = '?', `ASSET_NUMBER` = '?', `PROP_TEMP` = '?' WHERE  `ASSET_NUMBER` = '?' OR `OLD_ASSET_NUMBER` = '?';";

	private static final String DP_WEEK0_PARAMS_ORIGINAL_UPDATE_QUERY = "UPDATE `DP_WEEK0_PARAMS_ORIGINAL` SET `OLD_ASSET_NUMBER` = '?', `ASSET_NUMBER` = '?', `PROP_TEMP` = '?' WHERE  `ASSET_NUMBER` = '?' OR `OLD_ASSET_NUMBER` = '?';";

	private static final String DP_WEEKN_AUDIT_REPORTS_UPDATE_QUERY = "UPDATE `DP_WEEKN_AUDIT_REPORTS` SET `OLD_LOAN_NUMBER` = '?', `LOAN_NUMBER` = '?', `PROP_TEMP` = '?' WHERE  `LOAN_NUMBER` = '?' OR `OLD_LOAN_NUMBER` = '?';";

	private static final String DP_WEEKN_PARAMS_UPDATE_ROLLBACK_QUERY = "UPDATE `DP_WEEKN_PARAMS` SET `ASSET_NUMBER` = `OLD_ASSET_NUMBER`, `OLD_ASSET_NUMBER` = NULL, `PROP_TEMP` = NULL WHERE  `OLD_ASSET_NUMBER` = '?';";

	private static final String DP_WEEKN_PARAMS_ORIGINAL_UPDATE_ROLLBACK_QUERY = "UPDATE `DP_WEEKN_PARAMS_ORIGINAL` SET `ASSET_NUMBER` = `OLD_ASSET_NUMBER`, `OLD_ASSET_NUMBER` = NULL, `PROP_TEMP` = NULL WHERE  `OLD_ASSET_NUMBER` = '?';";

	private static final String DP_WEEK0_PARAMS_UPDATE_ROLLBACK_QUERY = "UPDATE `DP_WEEK0_PARAMS` SET `ASSET_NUMBER` = `OLD_ASSET_NUMBER`, `OLD_ASSET_NUMBER` = NULL, `PROP_TEMP` = NULL WHERE  `OLD_ASSET_NUMBER` = '?';";

	private static final String DP_WEEK0_PARAMS_ORIGINAL_UPDATE_ROLLBACK_QUERY = "UPDATE `DP_WEEK0_PARAMS_ORIGINAL` SET `ASSET_NUMBER` = `OLD_ASSET_NUMBER`, `OLD_ASSET_NUMBER` = NULL, `PROP_TEMP` = NULL WHERE  `OLD_ASSET_NUMBER` = '?';";

	private static final String DP_WEEKN_AUDIT_REPORTS_UPDATE_ROLLBACK_QUERY = "UPDATE `DP_WEEKN_AUDIT_REPORTS` SET `LOAN_NUMBER` = `OLD_LOAN_NUMBER`, `OLD_LOAN_NUMBER` = NULL, `PROP_TEMP` = NULL WHERE  `OLD_LOAN_NUMBER` = '?';";

	private static final String SOP_WEEK0_PROCESS_INSERT_QUERY = "INSERT INTO `DP_SOP_WEEK0_PRCS_STATUS` (`ID`, `INPUT_FILE_NAME`, `SYS_GNRTD_INPUT_FILE_NAME`, `STATUS`, `CREATED_BY`, `CREATED_ON`) VALUES (";

	private static final String SOP_WEEK0_INSERT_QUERY = "INSERT INTO `DP_SOP_WEEK0_PARAMS` (`ID`, `DP_SOP_WEEK0_FILE_ID`, `UPLOAD_FLAG`, `ASSET_NUMBER`, `STATUS`, `ASSET_VALUE`, `AV_SET_DATE`, `LIST_PRICE`, `CLASSIFICATION`, `ELIGIBLE`, `ASSIGNMENT`,  `STATE`,  `NOTES`, `PROPERTY_TYPE`, `ASSIGNMENT_DATE`, `REO_DATE`, `CREATED_BY`, `CREATED_ON`) VALUES ";

	private static final String SOP_WEEKN_PROCESS_INSERT_QUERY = "INSERT INTO `DP_SOP_WEEKN_PRCS_STATUS` (`ID`, `INPUT_FILE_NAME`, `SYS_GNRTD_INPUT_FILE_NAME`, `STATUS`, `CREATED_BY`, `CREATED_ON`) VALUES (";

	private static final String SOP_WEEKN_PARAMS_INSERT_QUERY = "INSERT INTO `DP_SOP_WEEKN_PARAMS` (`ID`, `ASSET_NUMBER`, `DP_SOP_WEEKN_FILE_ID`, `ELIGIBLE`,  `MOST_RECENT_LIST_END_DATE`, `MOST_RECENT_LIST_STATUS`, `MOST_RECENT_PROPERTY_STATUS`, `MOST_RECENT_LIST_PRICE`, `LP_PERCENT_ADJUSTMENT_REC`, `LP_DOLLAR_ADJUSTMENT_REC`, `MODEL_VERSION`, `DELIVERY_DATE`, `CLASSIFICATION`, `CREATED_BY`, `CREATED_ON`) VALUES ";


	private static File outputFile;

	public static void uploadWeek0DBCSV(File file) throws Exception {

		System.out.println("Processing ...");
		Workbook workbook = null;
		Sheet datatypeSheet = null;
		try {
			workbook = new XSSFWorkbook(new BufferedInputStream(new FileInputStream(file)));
			datatypeSheet = workbook.getSheetAt(0);
			DataFormatter df = new DataFormatter();
			Row currentRow;

			UUID processId = UUID.randomUUID();

			generateProcessStatusFile(outputFile, processId.toString(), file.getName());

			int count = 0;

			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(outputFile, true));

				bw.newLine();

				bw.write("-- dp_week0_params DML Query.");

				bw.newLine();

				for (int i = 1; i <= datatypeSheet.getLastRowNum(); i++) {
					currentRow = datatypeSheet.getRow(i);

					if (!df.formatCellValue(currentRow.getCell(6)).equalsIgnoreCase("OCN")
							  && !df.formatCellValue(currentRow.getCell(6)).equalsIgnoreCase("NRZ")) {
						continue;
					}

					UUID processParamId = UUID.randomUUID();

					if (count % 20 == 0) {
						bw.write(DP_WEEK0_INSERT_QUERY);
					}
					bw.write("('" + processParamId.toString() + "', ");
					bw.write("'" + processId.toString() + "', ");
					bw.write("'WEEK0DB_INITIAL', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(0))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(1))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(2))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(3))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(4))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(5))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(6))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(7))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(18))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(19))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(8))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(9))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(10))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(11))) + "', ");
//					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(16))) + "', ");

					LocalDate localDate = LocalDate.parse(StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(16))), DATE_TIME_FORMATTER);
					bw.write("'" + localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() + "', ");


					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(12))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(23))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(24))) + "', ");
					Date date = new Date();
					bw.write("'" + date.getTime() + "', ");
					bw.write("'SYSTEM', ");
					bw.write("UNIX_TIMESTAMP() * 1000) ");
					count++;
					if (count % 20 == 0 || i == datatypeSheet.getLastRowNum()) {
						bw.write(";");
					} else {
						bw.write(",");
					}
					bw.newLine();

				}
			} catch (IOException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			} finally {
				flushStream(bw);
			}

			System.out.println("Number of Queries generated : " + count);

		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} finally {
			if (null != workbook) {
				workbook.close();
			}
		}
	}

	private static void generateProcessStatusFile(File file2, String processId, String fileName) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file2, false));

			bw.newLine();

			bw.write("-- dynamic_pricing_file_prcs_status DML Query.");

			bw.newLine();

			bw.write(DP_PROCESS_INSERT_QUERY);
			bw.write("'" + processId + "', ");
			bw.write("'" + StringEscapeUtils.escapeSql(fileName) + "', ");
			bw.write("'', ");
			bw.write("'DATA_LOAD', ");
			bw.write("'Vacant Week 0', ");
			bw.write("UNIX_TIMESTAMP() * 1000, ");
			bw.write("'SYSTEM', ");
			bw.write("UNIX_TIMESTAMP() * 1000); ");

			bw.newLine();

		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} finally {
			flushStream(bw);
		}
	}

	/**
	 * @param bw
	 */
	private static void flushStream(BufferedWriter bw) {
		if (null != bw) {
			try {
				bw.flush();
			} catch (IOException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			}
		}
	}

	public static void uploadWeekNDBCSV(File file) throws Exception {
		System.out.println("Processing ...");
		Workbook workbook = null;
		Sheet datatypeSheet = null;
		try {
			workbook = new XSSFWorkbook(new BufferedInputStream(new FileInputStream(file)));
			datatypeSheet = workbook.getSheetAt(0);
			DataFormatter df = new DataFormatter();
			Row currentRow;

			UUID processId = UUID.randomUUID();

			generateWeeknPrcsStatusFile(outputFile, processId.toString(), file.getName());

			int count = 0;

			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(outputFile, true));

				bw.newLine();

				bw.write("-- dp_weekn_params DML Query.");

				bw.newLine();

				for (int i = 1; i <= datatypeSheet.getLastRowNum(); i++) {
					currentRow = datatypeSheet.getRow(i);

					if (!df.formatCellValue(currentRow.getCell(0)).equalsIgnoreCase("OCN")
							  && !df.formatCellValue(currentRow.getCell(0)).equalsIgnoreCase("NRZ")) {
						continue;
					}

					UUID processParamId = UUID.randomUUID();

					if (count % 20 == 0) {
						bw.write(DP_WEEKN_PARAMS_INSERT_QUERY);
					}
					bw.write("('" + processParamId.toString() + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(1))) + "', ");
					bw.write("'" + processId.toString() + "', ");
					bw.write("'Eligible', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(2))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(3))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(4))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(5))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(6))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(7))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(8))) + "', ");

					if (!StringUtils.isEmpty(df.formatCellValue(currentRow.getCell(9)))) {
						LocalDate localDate = LocalDate.parse(
								  StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(9))), DATE_TIME_FORMATTER);
						bw.write("'" + localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() + "', ");
					} else {
						bw.write("" + null + ", ");
					}

					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(0))) + "', ");

					bw.write("'SYSTEM', ");
					bw.write("UNIX_TIMESTAMP() * 1000) ");
					count++;
					if (count % 20 == 0 || i == datatypeSheet.getLastRowNum()) {
						bw.write(";");
					} else {
						bw.write(",");
					}
					bw.newLine();
				}
			} catch (IOException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			} finally {
				flushStream(bw);
			}

			System.out.println("Number of Queries generated : " + count);
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} finally {
			if (null != workbook) {
				workbook.close();
			}
		}
	}

	public static void uploadWeekNAuditCSV(File file) throws Exception {
		System.out.println("Processing ...");
		Workbook workbook = null;
		Sheet datatypeSheet = null;
		try {
			workbook = new XSSFWorkbook(new BufferedInputStream(new FileInputStream(file)));
			datatypeSheet = workbook.getSheetAt(0);
			DataFormatter df = new DataFormatter();
			Row currentRow;

			int count = 0;

			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(outputFile, true));

				bw.newLine();

				bw.write("-- dp_weekn_audit DML Query.");

				bw.newLine();

				for (int i = 1; i <= datatypeSheet.getLastRowNum(); i++) {
					currentRow = datatypeSheet.getRow(i);

					if (count % 20 == 0) {
						bw.write(DP_WEEKN_AUDIT_INSERT_QUERY);
					}
					bw.write("(UUID(), ");
					bw.write("'" + StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(1)))) + "', ");
					if (StringUtils.equalsIgnoreCase(StringUtils.trim(df.formatCellValue(currentRow.getCell(2))), "NULL")) {
						bw.write("" + null + ", ");
					} else {
						bw.write("'" + StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(2)))) + "', ");
					}
					bw.write("'" + StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(3)))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(4)))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(5)))) + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(StringUtils.equalsIgnoreCase(StringUtils.trim(df.formatCellValue(currentRow.getCell(6))), "N") ? "0" : "1") + "', ");
					bw.write("'" + StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(7)))) + "', ");
					bw.write("UNIX_TIMESTAMP() * 1000, ");
					bw.write("'" + StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(9)))) + "', ");
					bw.write("UNIX_TIMESTAMP() * 1000)");
					count++;
					if (count % 20 == 0 || i == datatypeSheet.getLastRowNum()) {
						bw.write(";");
					} else {
						bw.write(",");
					}
					bw.newLine();
				}
			} catch (IOException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			} finally {
				flushStream(bw);
			}

			System.out.println("Number of Queries generated : " + count);
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} finally {
			if (null != workbook) {
				workbook.close();
			}
		}
	}

	public static void uploadEqMigrationCSV(File file) throws Exception {
		System.out.println("Processing ...");
		Workbook workbook = null;
		Sheet datatypeSheet = null;
		try {
			workbook = new XSSFWorkbook(new BufferedInputStream(new FileInputStream(file)));
			datatypeSheet = workbook.getSheetAt(0);
			DataFormatter df = new DataFormatter();
			Row currentRow;

			int count = 0;

			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(outputFile, true));

				bw.newLine();

				bw.write("-- dp_eq_migration DML Query.");

				bw.newLine();

				for (int i = 1; i <= datatypeSheet.getLastRowNum(); i++) {
					currentRow = datatypeSheet.getRow(i);
					bw.write(StringUtils.replaceOnce(StringUtils.replaceOnce(StringUtils.replaceOnce(
							  StringUtils.replaceOnce(StringUtils.replaceOnce(DP_WEEKN_PARAMS_UPDATE_QUERY, "?",
										 StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))), "?",
										 StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(1))))), "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(2))))), "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))), "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))));
					bw.newLine();
					bw.write(StringUtils.replaceOnce(StringUtils.replaceOnce(StringUtils.replaceOnce(
							  StringUtils.replaceOnce(StringUtils.replaceOnce(DP_WEEKN_PARAMS_ORIGINAL_UPDATE_QUERY, "?",
										 StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))), "?",
										 StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(1))))), "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(2))))), "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))), "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))));
					bw.newLine();
					bw.write(StringUtils.replaceOnce(StringUtils.replaceOnce(StringUtils.replaceOnce(
							  StringUtils.replaceOnce(StringUtils.replaceOnce(DP_WEEK0_PARAMS_UPDATE_QUERY, "?",
										 StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))), "?",
										 StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(1))))), "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(2))))), "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))), "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))));
					bw.newLine();
					bw.write(StringUtils.replaceOnce(StringUtils.replaceOnce(StringUtils.replaceOnce(
							  StringUtils.replaceOnce(StringUtils.replaceOnce(DP_WEEK0_PARAMS_ORIGINAL_UPDATE_QUERY, "?",
										 StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))), "?",
										 StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(1))))), "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(2))))), "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))), "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))));
					bw.newLine();
					bw.write(StringUtils.replaceOnce(StringUtils.replaceOnce(StringUtils.replaceOnce(
							  StringUtils.replaceOnce(StringUtils.replaceOnce(DP_WEEKN_AUDIT_REPORTS_UPDATE_QUERY, "?",
										 StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))), "?",
										 StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(1))))), "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(2))))), "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))), "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))));
					bw.newLine();
				}
			} catch (IOException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			} finally {
				flushStream(bw);
			}

		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} finally {
			if (null != workbook) {
				workbook.close();
			}
		}
	}

	public static void uploadEqMigrationRollbackCSV(File file) throws Exception {
		System.out.println("Processing ...");
		Workbook workbook = null;
		Sheet datatypeSheet = null;
		try {
			workbook = new XSSFWorkbook(new BufferedInputStream(new FileInputStream(file)));
			datatypeSheet = workbook.getSheetAt(0);
			DataFormatter df = new DataFormatter();
			Row currentRow;

			int count = 0;

			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(outputFile, true));

				bw.newLine();

				bw.write("-- dp_eq_migration_rollback DML Query.");

				bw.newLine();

				for (int i = 1; i <= datatypeSheet.getLastRowNum(); i++) {
					currentRow = datatypeSheet.getRow(i);
					bw.write(StringUtils.replaceOnce(DP_WEEKN_PARAMS_UPDATE_ROLLBACK_QUERY, "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))));
					bw.newLine();
					bw.write(StringUtils.replaceOnce(DP_WEEKN_PARAMS_ORIGINAL_UPDATE_ROLLBACK_QUERY, "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))));
					bw.newLine();
					bw.write(StringUtils.replaceOnce(DP_WEEK0_PARAMS_UPDATE_ROLLBACK_QUERY, "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))));
					bw.newLine();
					bw.write(StringUtils.replaceOnce(DP_WEEK0_PARAMS_ORIGINAL_UPDATE_ROLLBACK_QUERY, "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))));
					bw.newLine();
					bw.write(StringUtils.replaceOnce(DP_WEEKN_AUDIT_REPORTS_UPDATE_ROLLBACK_QUERY, "?",
							  StringEscapeUtils.escapeSql(StringUtils.trim(df.formatCellValue(currentRow.getCell(0))))));
					bw.newLine();
					count++;
				}
			} catch (IOException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			} finally {
				flushStream(bw);
			}

		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} finally {
			if (null != workbook) {
				workbook.close();
			}
		}
	}

	private static void generateWeeknPrcsStatusFile(File file2, String processId, String fileName) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file2, false));

			bw.newLine();

			bw.write("-- dp_weekn_prcs_status DML Query.");

			bw.newLine();

			bw.write(DP_WEEKNPRCSSTATUS_INSERT_QUERY);
			bw.write("'" + processId + "', ");
			bw.write("'" + StringEscapeUtils.escapeSql(fileName) + "', ");
			bw.write("'', ");
			bw.write("'DATA_LOAD', ");
			bw.write("'Vacant Week N', ");
			bw.write("'SYSTEM', ");
			bw.write("UNIX_TIMESTAMP() * 1000); ");

			bw.newLine();

		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} finally {
			flushStream(bw);
		}
	}

	public static void uploadSopWeek0DBCSV(File file) throws Exception {

		System.out.println("Processing ...");
		Workbook workbook = null;
		Sheet datatypeSheet = null;
		try {
			workbook = new XSSFWorkbook(new BufferedInputStream(new FileInputStream(file)));
			datatypeSheet = workbook.getSheetAt(0);
			DataFormatter df = new DataFormatter();
			Row currentRow;

			UUID processId = UUID.randomUUID();

			generateSopWeek0ProcessStatusFile(outputFile, processId.toString(), file.getName());

			int count = 0;

			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(outputFile, true));

				bw.newLine();

				bw.write("-- dp_sop_week0_params DML Query.");

				bw.newLine();

				for (int i = 1; i <= datatypeSheet.getLastRowNum(); i++) {
					currentRow = datatypeSheet.getRow(i);

					if (!df.formatCellValue(currentRow.getCell(5)).equalsIgnoreCase("OCN")
							  && !df.formatCellValue(currentRow.getCell(5)).equalsIgnoreCase("NRZ")
							  && !df.formatCellValue(currentRow.getCell(5)).equalsIgnoreCase("PHH")) {
						continue;
					}

					UUID processParamId = UUID.randomUUID();

					if (count % 20 == 0) {
						bw.write(SOP_WEEK0_INSERT_QUERY);
					}
					bw.write("('" + processParamId.toString() + "', ");
					bw.write("'" + processId.toString() + "', ");
					bw.write("'SOP_WEEK0DB_INITIAL', ");
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(0))) + "', "); //Asset Number
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(1))) + "', "); // `STATUS`
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(2))) + "', "); //`ASSET_VALUE`
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(3))) + "', "); //`AV_SET_DATE`
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(4))) + "', "); //`LIST_PRICE`
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(5))) + "', "); //`CLASSIFICATION`
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(6))) + "', "); //`ELIGIBLE`
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(7))) + "', "); //`ASSIGNMENT`
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(8))) + "', "); //`STATE`
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(9))) + "', "); //`NOTES`
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(10))) + "', "); //`PROPERTY_TYPE`
					LocalDate localDate = LocalDate.parse(StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(11))), DATE_TIME_FORMATTER);
					bw.write("'" + localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() + "', ");  // `ASSIGNMENT_DATE`
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(12))) + "', ");  // `REO_DATE`
					bw.write("'SYSTEM', "); //`CREATED_BY`
					bw.write("UNIX_TIMESTAMP() * 1000) "); //`CREATED_ON`
					count++;
					if (count % 20 == 0 || i == datatypeSheet.getLastRowNum()) {
						bw.write(";");
					} else {
						bw.write(",");
					}
					bw.newLine();

				}
			} catch (IOException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			} finally {
				flushStream(bw);
			}

			System.out.println("Number of Queries generated : " + count);

		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} finally {
			if (null != workbook) {
				workbook.close();
			}
		}
	}

	private static void generateSopWeek0ProcessStatusFile(File file2, String processId, String fileName) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file2, false));

			bw.newLine();

			bw.write("-- dp_sop_week0_prcs_status DML Query.");

			bw.newLine();

			bw.write(SOP_WEEK0_PROCESS_INSERT_QUERY);
			bw.write("'" + processId + "', ");
			bw.write("'" + StringEscapeUtils.escapeSql(fileName) + "', ");
			bw.write("'', ");
			bw.write("'DATA_LOAD', ");
			bw.write("'SYSTEM', ");
			bw.write("UNIX_TIMESTAMP() * 1000); ");

			bw.newLine();

		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} finally {
			flushStream(bw);
		}
	}

	public static void uploadSopWeekNDBCSV(File file) throws Exception {

		System.out.println("Processing ...");
		Workbook workbook = null;
		Sheet datatypeSheet = null;
		try {
			workbook = new XSSFWorkbook(new BufferedInputStream(new FileInputStream(file)));
			datatypeSheet = workbook.getSheetAt(0);
			DataFormatter df = new DataFormatter();
			Row currentRow;

			UUID processId = UUID.randomUUID();

			generateSopWeekNProcessStatusFile(outputFile, processId.toString(), file.getName());

			int count = 0;

			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(outputFile, true));

				bw.newLine();

				bw.write("-- dp_sop_weekn_params DML Query.");

				bw.newLine();

				for (int i = 1; i <= datatypeSheet.getLastRowNum(); i++) {
					currentRow = datatypeSheet.getRow(i);

					if (!df.formatCellValue(currentRow.getCell(0)).equalsIgnoreCase("OCN")
							&& !df.formatCellValue(currentRow.getCell(0)).equalsIgnoreCase("NRZ")
							&& !df.formatCellValue(currentRow.getCell(0)).equalsIgnoreCase("PHH")) {
						continue;
					}

					UUID processParamId = UUID.randomUUID();

					if (count % 20 == 0) {
						bw.write(SOP_WEEKN_PARAMS_INSERT_QUERY);
					}
					bw.write("('" + processParamId.toString() + "', "); //ID
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(1))) + "', "); //Asset Number
					bw.write("'" + processId.toString() + "', ");  //DP_SOP_WEEKN_FILE_ID
					bw.write("'Eligible', "); //`ELIGIBLE`
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(2))) + "', "); //MOST_RECENT_LIST_END_DATE
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(3))) + "', "); //MOST_RECENT_LIST_STATUS
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(4))) + "', "); //MOST_RECENT_PROPERTY_STATUS
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(5))) + "', "); //MOST_RECENT_LIST_PRICE
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(6))) + "', "); //LP_PERCENT_ADJUSTMENT_REC
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(7))) + "', "); //LP_DOLLAR_ADJUSTMENT_REC
					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(8))) + "', "); //MODEL_VERSION

					if (!StringUtils.isEmpty(df.formatCellValue(currentRow.getCell(9)))) {
						LocalDate localDate = LocalDate.parse(
								StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(9))), DATE_TIME_FORMATTER);
						bw.write("'" + localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() + "', "); //DELIVERY_DATE
					} else {
						bw.write("" + null + ", "); //DELIVERY_DATE
					}

					bw.write("'" + StringEscapeUtils.escapeSql(df.formatCellValue(currentRow.getCell(0))) + "', "); //CLASSIFICATION

					bw.write("'SYSTEM', ");
					bw.write("UNIX_TIMESTAMP() * 1000) ");
					count++;
					if (count % 20 == 0 || i == datatypeSheet.getLastRowNum()) {
						bw.write(";");
					} else {
						bw.write(",");
					}
					bw.newLine();

				}
			} catch (IOException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			} finally {
				flushStream(bw);
			}

			System.out.println("Number of Queries generated : " + count);

		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} finally {
			if (null != workbook) {
				workbook.close();
			}
		}
	}

	/**
	 *
	 * @param file2
	 * @param processId
	 * @param fileName
	 */
	private static void generateSopWeekNProcessStatusFile(File file2, String processId, String fileName) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(file2, false));

			bw.newLine();

			bw.write("-- dp_sop_week0_prcs_status DML Query.");

			bw.newLine();

			bw.write(SOP_WEEKN_PROCESS_INSERT_QUERY);
			bw.write("'" + processId + "', ");
			bw.write("'" + StringEscapeUtils.escapeSql(fileName) + "', ");
			bw.write("'', ");
			bw.write("'DATA_LOAD', ");
			bw.write("'SYSTEM', ");
			bw.write("UNIX_TIMESTAMP() * 1000); ");

			bw.newLine();

		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		} finally {
			flushStream(bw);
		}
	}

	public static void main(String args[]) {

		String inputFileName = System.getProperty("input.file.location");

		if (null == inputFileName) {
			System.out.println("input.file.location argument is missing.");
			System.exit(1);
		}

		File file = new File(inputFileName);

		String outputFileName = file.getParent();

		outputFile = new File(outputFileName);

		if (outputFile.exists() && outputFile.isDirectory()) {

			String inputFileOriginalName = file.getName().substring(0, file.getName().lastIndexOf("."));

			outputFile = new File(outputFileName + File.separator + inputFileOriginalName + "_DML.sql");

			try {
				if (System.getProperty("db.load.process").equals("Week0")) {
					uploadWeek0DBCSV(file);
				} else if (System.getProperty("db.load.process").equals("WeekN")) {
					uploadWeekNDBCSV(file);
				} else if (System.getProperty("db.load.process").equals("WeekNAudit")) {
					uploadWeekNAuditCSV(file);
				} else if (System.getProperty("db.load.process").equals("EqMigration")) {
					uploadEqMigrationCSV(file);
				} else if (System.getProperty("db.load.process").equals("EqMigrationRollback")) {
					uploadEqMigrationRollbackCSV(file);
				} else if (System.getProperty("db.load.process").equals("SopWeek0")) {
					uploadSopWeek0DBCSV(file);
				} else if (System.getProperty("db.load.process").equals("SopWeekN")) {
					uploadSopWeekNDBCSV(file);
				} else if (System.getProperty("db.load.process") == null) {
					System.out.println("db.load.process argument is missing.");
					System.exit(1);
				}
			} catch (Exception e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			} catch (Throwable e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			}
		}
	}
}
