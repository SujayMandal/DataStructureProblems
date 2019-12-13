/**
 * 
 */
package com.fa.dp.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.sop.week0.input.info.DPSopParamEntryInfo;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.weekN.util.SopWeekNFileUtil;
import com.fa.dp.business.test.delegate.TestDelegate;
import com.fa.dp.business.test.info.TestInfo;
import com.fa.dp.business.validation.file.util.InputFileValidationUtil;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.rest.template.RestIntegrationClient;
import com.fa.dp.core.rest.util.SSLCertificateDownloadUtil;
import com.fa.dp.core.systemparam.info.SystemParameterInfo;
import com.fa.dp.core.systemparam.provider.SystemParameterProvider;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.systemparam.util.SystemParameterConstant;
import com.fa.dp.core.util.RAClientConstants;
import com.fa.dp.rest.response.RestResponse;
import com.fa.dp.security.config.ldap.CustomUser;


//@RequestMapping("/dp")
@Controller
@Slf4j
public class HomeController {

	@Inject
	private TestDelegate testDelegate;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	private JdbcTemplate jdbcTemplate;
	
	private static final String CA_AVM = "CA_AVM";

	@Inject
	private SopWeekNFileUtil sopWeekNFileUtil;
	
	@Inject
	private RestIntegrationClient restIntegrationClient;
	
	@Inject
	@Named(value = "rrDataSource")
	private DataSource dataSource;

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@PostConstruct
	public void init() {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setExpandEntityReferences(false);
		try {
			documentBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			log.error(e.getLocalizedMessage(), e);
		}

		XPathFactory xpathFactory = XPathFactory.newInstance();
		xpath = xpathFactory.newXPath();

	}

	@Inject
	private CacheManager cacheManager;
	
	private static final String AVMX_RESPONSE_ZIP = "/avmx/response/reportdata/property/address/zip/text()";

	private static final String AVMX_RESPONSE_FSD = "/avmx/response/reportdata/summary/fsd/text()";

	private static final String AVMX_RESPONSE_ESTIMATED = "/avmx/response/reportdata/summary/estimated/text()";

	private static final String AVMX_RESPONSE_ERROR_MESSAGE = "/avmx/response/responseheader/error/message/text()";

	private static final String AVMX_RESPONSE_TIMESTAMP = "/avmx/response/responseheader/timestamp/text()";

	private static final String AVMX_RESPONSE_ERROR_CODE = "/avmx/response/responseheader/error/code/text()";
	
	private static final String LOAN_NUMBER_HEADER = "Loan number";

	private static final String VALUATION_DATE_HEADER = "Valuation date";
	
	private static final String REGULAR_AVM_HEADER = "Regular avm";
	
	private static final String REGULAR_FSD_HEADER = "Regular fsd";
	
	private static final String REO_AVM_HEADER = "Reo avm";
	
	private static final String REO_FSD_HEADER = "Reo fsd";
	
	private XPath xpath = null;
	
	private DocumentBuilder documentBuilder = null;

	@RequestMapping("/test")
	@ResponseBody
	public String home() {
		return "hello dynamic pricing test api";
	}

	@GetMapping(value = "/updateSystemParameter")
	@ResponseBody
	public String updateSystemParameter(String key, String value) {
		SystemParameterInfo systemParameterInfo = new SystemParameterInfo();
		systemParameterInfo.setKey(key);
		systemParameterInfo.setValue(value);
		if(systemParameterProvider.updateSystemParameter(systemParameterInfo)){
			return "System Parameter " + key + " updated successfully"; 
		} else {
			return "An error occurred while updating system parameter " + key;
		}
	}

	@RequestMapping(value = "/loginPage", method = RequestMethod.GET)
	public ModelAndView getLoginPage(@RequestParam(value = "error", required = false) boolean error) {
		ModelAndView model = new ModelAndView();
		if (error) {
			model.addObject("error", true);
		}
		model.setViewName("login.jsp");
		return model;
	}

	@RequestMapping(value = {"/","/search","/week0","/weekN","/SOPweek0","/SOPweekN","/reports"}, method = RequestMethod.GET)
	public ModelAndView launchIndexPage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView();
		model.setViewName("index.html");
		return model;

	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET )
	@ResponseBody
	public void logoutUser( HttpServletRequest request, HttpServletResponse response) {
		log.info("entered logoutUser of HomeController class");
		try {
			redirectStrategy.sendRedirect((HttpServletRequest) request, (HttpServletResponse) response, "/logout");
		} catch (Exception e) {
			log.error("error occured while redirecting to build-in logout");
		}
	}

	@RequestMapping(value = "/getLoggedInDetails", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public RestResponse<CustomUser> getLoggedInDetails() throws SystemException {
		RestResponse<CustomUser> response = new RestResponse<CustomUser>();
		log.info("entered getLoggedInDetails of LoginAuthenticationRestController class");
		try {
			CustomUser user=(CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if(user !=null)
			{
				response.setResponse(user);
			}else{
				response.setMessage("Error occured in populating username");
			}

		} catch (Exception e) {
			log.error("An error occurred while calling getLoggedInDetails", e);
			response.setSuccess(Boolean.FALSE);
			response.setMessage("User is not authenticated");

		}

		return response;

	}


	@GetMapping("/getAll")
	@ResponseBody
	public RestResponse<List<TestInfo>> create() {
		log.info("in getAll controller");
		RestResponse<List<TestInfo>> response = new RestResponse<List<TestInfo>>();
		try {
			List<TestInfo> testInfos = testDelegate.getAll();
			response.setResponse(testInfos);
			response.setSuccess(true);
		} catch (SystemException e) {
			response.setSuccess(false);
			response.setErrorCode(e.getCode());
			response.setMessage(e.getLocalizedMessage());
		}
		return response;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/caAvmUtil")
	@ResponseBody
	public void callCaAvmUtility(@RequestParam(value = "file") MultipartFile file, HttpServletResponse response) throws SystemException {

		MDC.put(RAClientConstants.PRODUCT_TYPE, DPAConstants.WEEK0);
		MDC.put(RAClientConstants.APP_CODE, "dpa");
		final DPProcessParamEntryInfo dpProcessParamEntryInfo = getWeek0ParamEntryInfo(file);
		List<DPProcessParamInfo> finalList = new ArrayList<>(dpProcessParamEntryInfo.getColumnEntries().size());
		String caUrl = (String) systemParameterProvider.getSystemParamValue(SystemParameterConstant.APP_PARAM_CA_URL);
		String caUserName = (String) systemParameterProvider.getSystemParamValue(SystemParameterConstant.APP_PARAM_CA_USERNAME);
		String caPassword = (String) systemParameterProvider.getSystemParamValue(SystemParameterConstant.APP_PARAM_CA_PASSWORD);

		String requestXMLRegular = (String) cacheManager
				.getAppParamValue(AppParameterConstant.APP_PARAM_CA_REQUEST_XML_REGULAR);
		String requestXMLREO = (String) cacheManager
				.getAppParamValue(AppParameterConstant.APP_PARAM_CA_REQUEST_XML_REO);
		dpProcessParamEntryInfo.getColumnEntries().parallelStream().forEach( i ->{
			String propertyZip = i.getZip();
			String propertyAddress = i.getAddress();
			String valuationDate = i.getAvSetDate();
			String requestXMLRegularFormatted = MessageFormat.format(requestXMLRegular, caUserName, caPassword,
					StringEscapeUtils.escapeXml10(propertyZip),
					StringEscapeUtils.escapeXml10(propertyAddress),
					StringEscapeUtils.escapeXml10(valuationDate));
			String requestXMLREOFormatted = MessageFormat.format(requestXMLREO, caUserName, caPassword,
					StringEscapeUtils.escapeXml10(propertyZip),
					StringEscapeUtils.escapeXml10(propertyAddress),
					StringEscapeUtils.escapeXml10(valuationDate));
			try {
				fetchCollateralAnalyticsRegularAPIResult(i, requestXMLRegularFormatted, caUrl);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				fetchCollateralAnalyticsREOAPIResult(i, requestXMLREOFormatted, caUrl);
			} catch (Exception e) {
				e.printStackTrace();
			}

			finalList.add(i);
		});
		
		createAndDownloadWeek0Excel(finalList, response);

		log.info("Input file validation controller ends");
	}
	
	private void fetchCollateralAnalyticsREOAPIResult(DPProcessParamInfo entry, String xml, String caUrl)
			throws SystemException {

		Document document = extractResult(xml, caUrl);

		if (document != null) {
			try {
				if (xpath.evaluate(AVMX_RESPONSE_ERROR_CODE, document.getDocumentElement()).equals("0")) {
					entry.setTimestampREO(xpath.evaluate(AVMX_RESPONSE_TIMESTAMP, document.getDocumentElement()));
					entry.setMessageREO(xpath.evaluate(AVMX_RESPONSE_ERROR_MESSAGE, document.getDocumentElement()));
					entry.setEstimatedREO(xpath.evaluate(AVMX_RESPONSE_ESTIMATED, document.getDocumentElement()));
					entry.setFsdREO(xpath.evaluate(AVMX_RESPONSE_FSD, document.getDocumentElement()));
					entry.setGeneratedZipREO(xpath.evaluate(AVMX_RESPONSE_ZIP, document.getDocumentElement()));
				}
			} catch (XPathExpressionException e) {
				log.error(e.getLocalizedMessage(), e);
			}
		}
	}
	
	private Document extractResult(String xml, String caUrl)
			throws SystemException, RestClientException {
		Document document = null;
		SSLCertificateDownloadUtil.validateUrl(caUrl, "CA_URL");

		String responseEntity = null;
		try {
			responseEntity = restIntegrationClient.execute(caUrl, xml, null, null, null, MediaType.APPLICATION_XML, String.class);
			System.out.println(responseEntity);
		} catch (SystemException ex) {
			log.error("Error occurred while invoking runtime request", ex);
			throw new SystemException(CoreExceptionCodes.RACLNCOM001, new Object[] {}, ex);
		} catch (Exception ex) {
			log.error("Error occurred while invoking runtime request", ex);
			throw new SystemException(CoreExceptionCodes.RACLNCOM001, new Object[] {}, ex);
		}

		if (responseEntity != null) {
			try {
				document = documentBuilder.parse(new InputSource(new StringReader(responseEntity)));
			} catch (SAXException e) {
				log.error(e.getLocalizedMessage(), e);
			} catch (IOException e) {
				log.error(e.getLocalizedMessage(), e);
			}
		}
		return document;
	}
	
	private void fetchCollateralAnalyticsRegularAPIResult(DPProcessParamInfo entry, String xml, String caUrl)
			throws SystemException {

		Document document = extractResult(xml, caUrl);
		if (document != null) {
			try {
				if (xpath.evaluate(AVMX_RESPONSE_ERROR_CODE, document.getDocumentElement()).equals("0")) {
					entry.setTimestamp(xpath.evaluate(AVMX_RESPONSE_TIMESTAMP, document.getDocumentElement()));
					entry.setMessage(xpath.evaluate(AVMX_RESPONSE_ERROR_MESSAGE, document.getDocumentElement()));
					entry.setEstimated(xpath.evaluate(AVMX_RESPONSE_ESTIMATED, document.getDocumentElement()));
					entry.setFsd(xpath.evaluate(AVMX_RESPONSE_FSD, document.getDocumentElement()));
					entry.setGeneratedZip(xpath.evaluate(AVMX_RESPONSE_ZIP, document.getDocumentElement()));
				}
			} catch (XPathExpressionException e) {
				log.error(e.getLocalizedMessage(), e);
			}
		}

	}

	private DPSopParamEntryInfo getSopopParamEntryInfo(MultipartFile file) {
		Sheet sheet = null;
		DataFormatter df = new DataFormatter();
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream());){
			sheet = workbook.getSheetAt(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		DPSopParamEntryInfo inputFileEntry = populateFields(df, sheet);
		return inputFileEntry;
	}

	private DPProcessParamEntryInfo getWeek0ParamEntryInfo(MultipartFile file) {
		Sheet sheet = null;
		DataFormatter df = new DataFormatter();
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream());){
			sheet = workbook.getSheetAt(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		DPProcessParamEntryInfo inputFileEntry = populateWeek0Fields(df, sheet);
		return inputFileEntry;
	}

	private DPSopParamEntryInfo populateFields(DataFormatter df, Sheet sheet) {
		Row currentRow;
		DPSopParamEntryInfo inputFileEntry = new DPSopParamEntryInfo();
		List<DPSopWeek0ParamInfo> list = new ArrayList<>();

		// validate each row of excel file.
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			currentRow = sheet.getRow(i);

			if(null == currentRow)
				continue;
			//log.info("DPSopWeek0ParamInfo asset number : " + df.formatCellValue(currentRow.getCell(0)));

			DPSopWeek0ParamInfo column = new DPSopWeek0ParamInfo();
			column.setAssetNumber(InputFileValidationUtil.getCellValue(currentRow, df, 0));
			column.setState(InputFileValidationUtil.getCellValue(currentRow, df, 1));
			column.setPropertyType(InputFileValidationUtil.getCellValue(currentRow, df, 2));
			column.setStatus(InputFileValidationUtil.getCellValue(currentRow, df, 3));
			column.setAssetValue(InputFileValidationUtil.getNumericCellValue(currentRow, df, 4));
			column.setAvSetDate(df.formatCellValue(currentRow.getCell(5)));
			column.setReoDate(df.formatCellValue(currentRow.getCell(6)));
			column.setListPrice(InputFileValidationUtil.getCellValue(currentRow, df, 7));
			column.setAssignment(InputFileValidationUtil.getCellValue(currentRow, df, 8));
			column.setEligible(InputFileValidationUtil.getCellValue(currentRow, df, 9));
			//column.setAssignmentDate(InputFileValidationUtil.getCellValue(currentRow, df, 10));
			column.setNotes(InputFileValidationUtil.getCellValue(currentRow, df, 11));
			//column.setClassification(InputFileValidationUtil.getCellValue(currentRow, df, 8));

			list.add(column);
		}

		log.info("DPProcessParamInfo list collected : " + list.size());

		inputFileEntry.setColumnEntries(list);
		inputFileEntry.setColumnCount(list.size());
		return inputFileEntry;
	}

	private DPProcessParamEntryInfo populateWeek0Fields(DataFormatter df, Sheet sheet) {
		Row currentRow;
		DPProcessParamEntryInfo inputFileEntry = new DPProcessParamEntryInfo();
		List<DPProcessParamInfo> list = new ArrayList<>();

		// validate each row of excel file.
		for (int i = 1; i <= sheet.getLastRowNum(); i++) {
			currentRow = sheet.getRow(i);

			if(null == currentRow)
				continue;

			DPProcessParamInfo column = new DPProcessParamInfo();
			column.setAssetNumber(InputFileValidationUtil.getCellValue(currentRow, df, 0));
			column.setAddress(InputFileValidationUtil.getCellValue(currentRow, df, 3));
			column.setZip(InputFileValidationUtil.getCellValue(currentRow, df, 4));
			column.setAvSetDate(LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format
					(currentRow.getCell(2).getDateCellValue())).minusDays(1).format(
							DateTimeFormatter.ofPattern("yyyy-MM-dd")));

			list.add(column);
		}

		log.info("DPProcessParamInfo list collected : " + list.size());

		inputFileEntry.setColumnEntries(list);
		inputFileEntry.setColumnCount(list.size());
		return inputFileEntry;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/classifiSopFile")
	@ResponseBody
	public void classifiSopFile(@RequestParam(value = "file") MultipartFile file,  HttpServletResponse response, @RequestParam(value = "type") String classificationType) throws SystemException {

		MDC.put(RAClientConstants.PRODUCT_TYPE, DPAConstants.SOP_WEEK0);
		MDC.put(RAClientConstants.APP_CODE, "dpa");
		final DPSopParamEntryInfo dpSopParamEntryInfo = getSopopParamEntryInfo(file);
		List<DPSopWeek0ParamInfo> finalList = new ArrayList<>(dpSopParamEntryInfo.getColumnEntries().size());
		if(classificationType.equalsIgnoreCase("NRZ")) {
			dpSopParamEntryInfo.getColumnEntries().parallelStream().forEach(i -> {
				String newClassification = jdbcTemplate.execute(
						"SELECT NRZ_ACQUISITION_DT, NRZ_FLAG FROM SHAHMAYU.REO_PORTFOLIO_MASTER WHERE PROP_TEMP = ? OR LOAN_NUM= ? ORDER BY AS_OF_DT DESC FETCH FIRST 1  ROW ONLY",
						(PreparedStatementCallback<String>) ps -> {
							String newClassification1 = null;
							ps.setString(1, i.getAssetNumber());
							ps.setString(2, i.getAssetNumber());
							ResultSet rs = ps.executeQuery();
							if(null != rs && rs.next()) {
								log.info(i.getAssetNumber() + " have NRZ Flag :" + rs.getString("NRZ_FLAG"));
								if(rs.getDate("NRZ_ACQUISITION_DT") != null)
									newClassification1 = DPProcessParamAttributes.NRZ.getValue();
							}

							return newClassification1;
						});

				if(StringUtils.equals(newClassification, DPProcessParamAttributes.NRZ.getValue())) {
					i.setClassification("NRZ");
				} else {
					i.setClassification("OCN");
				}
				finalList.add(i);
			});
		}
		if(classificationType.equalsIgnoreCase("OCN")) {
			dpSopParamEntryInfo.getColumnEntries().parallelStream().forEach(i -> {
				String newClassification = jdbcTemplate.execute(
						"SELECT PHH_FLAG FROM SHAHMAYU.REO_PORTFOLIO_MASTER WHERE PROP_TEMP = ? OR LOAN_NUM= ? ORDER BY AS_OF_DT DESC FETCH FIRST 1  ROW ONLY",
						(PreparedStatementCallback<String>) ps -> {
							String newClassification1 = null;
							ps.setString(1, i.getAssetNumber());
							ps.setString(2, i.getAssetNumber());
							ResultSet rs = ps.executeQuery();
							if(null != rs && rs.next()) {
								log.info(i.getAssetNumber() + " have PHH Flag :" + rs.getString("PHH_FLAG"));
								if(StringUtils.equalsIgnoreCase(rs.getString("PHH_FLAG"), RAClientConstants.YES))
									newClassification1 = DPProcessParamAttributes.PHH.getValue();
							}

							return newClassification1;
						});

				if(StringUtils.equals(newClassification, DPProcessParamAttributes.PHH.getValue())) {
					i.setClassification("PHH");
				} else {
					i.setClassification("OCN");
				}
				finalList.add(i);
			});
		}
		createAndDownloadSopWeekNExcel(finalList, response);

		log.info("Input file validation controller ends");
	}

	private void createAndDownloadSopWeekNExcel(List<DPSopWeek0ParamInfo> finalList, HttpServletResponse response)
			throws SystemException {

		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet processedSheet = workbook.createSheet(DPAConstants.PROCESSED_RECORDS);

			CellStyle style = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBold(true);
			style.setFont(font);
			int processedRowNum = 0;
			int colNum = 0;

			Row processedRow = processedSheet.createRow(processedRowNum++);
			sopWeekNFileUtil.generateHeaderSopWeek0(style, processedRow, colNum);
			for (int i = 1; i <= processedRow.getPhysicalNumberOfCells(); i++) {
				processedSheet.autoSizeColumn(i);
			}

			for (DPSopWeek0ParamInfo paramObject : finalList) {
				colNum = 0;
				processedRowNum = sopWeekNFileUtil.prepareSopWeek0OutputSheet(processedSheet, processedRowNum, colNum, paramObject);
			}
			// commented auditing as per story DP-603 --> end
			if(processedRowNum > 1 ) {
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					workbook.write(baos);
					byte[] outArray = baos.toByteArray();
					response.setContentLength(outArray.length);
					sopWeekNFileUtil.setResponseHeader(response,"SOP_WEEK0_CLASSIFIED.xlsx");
					response.getOutputStream().write(outArray);
					response.flushBuffer();
				} catch (IOException e) {
					log.error("Unable to write into  file ", e);
				}
			}
		} catch (Exception e) {
			log.error("Unable create excel documents ", e);
			throw new SystemException(CoreExceptionCodes.DPSOPWKN014, e.getLocalizedMessage());
		} finally {
			try {
				response.getOutputStream().close();
			} catch (IOException e) {
				log.error("Unable to close response stream ", e);
			}
		}
	}
	
	private void createAndDownloadWeek0Excel(List<DPProcessParamInfo> finalList, HttpServletResponse response)
			throws SystemException {

		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet processedSheet = workbook.createSheet(CA_AVM);

			CellStyle style = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBold(true);
			style.setFont(font);
			int processedRowNum = 0;
			int colNum = 0;

			Row processedRow = processedSheet.createRow(processedRowNum++);
			generateHeaderCaAvm(style, processedRow, colNum);
			for (int i = 1; i <= processedRow.getPhysicalNumberOfCells(); i++) {
				processedSheet.autoSizeColumn(i);
			}

			for (DPProcessParamInfo paramObject : finalList) {
				colNum = 0;
				processedRowNum = prepareCaAvmOutputSheet(processedSheet, processedRowNum, colNum, paramObject);
			}
			// commented auditing as per story DP-603 --> end
			if(processedRowNum > 1 ) {
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					workbook.write(baos);
					byte[] outArray = baos.toByteArray();
					response.setContentLength(outArray.length);
					sopWeekNFileUtil.setResponseHeader(response,"CA_AVM_Response.xlsx");
					response.getOutputStream().write(outArray);
					response.flushBuffer();
				} catch (IOException e) {
					log.error("Unable to write into  file ", e);
				}
			}
		} catch (Exception e) {
			log.error("Unable create excel documents ", e);
			throw new SystemException(CoreExceptionCodes.DPSOPWKN014, e.getLocalizedMessage());
		} finally {
			try {
				response.getOutputStream().close();
			} catch (IOException e) {
				log.error("Unable to close response stream ", e);
			}
		}
	}
	
	private int prepareCaAvmOutputSheet(XSSFSheet processedSheet, int processedRowNum, int colNum, DPProcessParamInfo paramObject) {
		Row row = processedSheet.createRow(processedRowNum++);

		colNum = prepareCellValue(row, colNum, paramObject.getAssetNumber());
		colNum = prepareCellValue(row, colNum, paramObject.getAvSetDate());
		colNum = prepareCellValue(row, colNum, paramObject.getEstimated());
		colNum = prepareCellValue(row, colNum, paramObject.getFsd());
		colNum = prepareCellValue(row, colNum, paramObject.getEstimatedREO());
		colNum = prepareCellValue(row, colNum, paramObject.getFsdREO());
		return processedRowNum;
	}
	
	private int prepareCellValue(Row row, int colNum, String data) {
		Cell cell;
		cell = row.createCell(colNum++);
		cell.setCellValue(data);
		return colNum;
	}
	
	public void generateHeaderCaAvm(CellStyle style, Row row, int colNum) {
		colNum = prepareCell(style, row, colNum, LOAN_NUMBER_HEADER);

		colNum = prepareCell(style, row, colNum, VALUATION_DATE_HEADER);

		colNum = prepareCell(style, row, colNum, REGULAR_AVM_HEADER);

		colNum = prepareCell(style, row, colNum, REGULAR_FSD_HEADER);

		colNum = prepareCell(style, row, colNum, REO_AVM_HEADER);

		colNum = prepareCell(style, row, colNum, REO_FSD_HEADER);
	}
	
	private int prepareCell(CellStyle style, Row row, int colNum, String data) {
		Cell cell;
		cell = row.createCell(colNum++);
		cell.setCellStyle(style);
		cell.setCellValue(data);
		return colNum;
	}
}
