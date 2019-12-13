package com.fa.dp.business.task;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.command.base.AbstractCommand;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.info.RtngInfo;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.util.TransactionStatus;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.entity.DynamicPricingIntgAudit;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.rest.template.RestIntegrationClient;
import com.fa.dp.core.rest.util.SSLCertificateDownloadUtil;
import com.fa.dp.core.systemparam.provider.SystemParameterProvider;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.systemparam.util.SystemParameterConstant;
import com.fa.dp.core.util.ConversionUtil;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.RAClientConstants;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
@Slf4j
@Named
@Scope("prototype")
@CommandDescription(name = "week0CAIntegration")
public class Week0CAIntegration extends AbstractCommand {

	private static final String CA_EVENT_TYPE = "CA_INTEGRATION";

	private static final Logger LOGGER = LoggerFactory.getLogger(Week0CAIntegration.class);

	private static final String AVMX_RESPONSE_ZIP = "/avmx/response/reportdata/property/address/zip/text()";

	private static final String AVMX_RESPONSE_FSD = "/avmx/response/reportdata/summary/fsd/text()";

	private static final String AVMX_RESPONSE_ESTIMATED = "/avmx/response/reportdata/summary/estimated/text()";

	private static final String AVMX_RESPONSE_ERROR_MESSAGE = "/avmx/response/responseheader/error/message/text()";

	private static final String AVMX_RESPONSE_TIMESTAMP = "/avmx/response/responseheader/timestamp/text()";

	private static final String AVMX_RESPONSE_ERROR_CODE = "/avmx/response/responseheader/error/code/text()";

	private Long start;

	@Inject
	private DPFileProcessBO dpFileProcessBO;

	@Inject
	private CacheManager cacheManager;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	private DocumentBuilder documentBuilder = null;

	private XPath xpath = null;

	@Inject
	private RestIntegrationClient restIntegrationClient;

	@PostConstruct
	public void init() {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setExpandEntityReferences(false);
		try {
			documentBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}

		XPathFactory xpathFactory = XPathFactory.newInstance();
		xpath = xpathFactory.newXPath();

	}

	@Override
	public void execute(Object data) throws SystemException {
		LOGGER.info("Week0CAIntegration -> processTask started.");
		start = DateTime.now().getMillis();
		DPProcessParamEntryInfo infoObject = null;
		if (checkData(data, DPProcessParamEntryInfo.class)){
			infoObject = ((DPProcessParamEntryInfo) data);
			LOGGER.info("CAIntegrationClientImpl -> invokeCollateralAnalyticsAPI started.");
			String caUrl = (String) systemParameterProvider.getSystemParamValue(SystemParameterConstant.APP_PARAM_CA_URL);
			String caUserName = (String) systemParameterProvider.getSystemParamValue(SystemParameterConstant.APP_PARAM_CA_USERNAME);
			String caPassword = (String) systemParameterProvider.getSystemParamValue(SystemParameterConstant.APP_PARAM_CA_PASSWORD);

			String requestXMLRegular = (String) cacheManager
					.getAppParamValue(AppParameterConstant.APP_PARAM_CA_REQUEST_XML_REGULAR);
			String requestXMLREO = (String) cacheManager
					.getAppParamValue(AppParameterConstant.APP_PARAM_CA_REQUEST_XML_REO);

			for (DPProcessParamInfo entry : infoObject.getColumnEntries()) {

				MDC.put(RAClientConstants.LOAN_NUMBER, entry.getAssetNumber());
				
				if (null != entry.getRtngResponse() && CollectionUtils.isNotEmpty(entry.getRtngResponse().getRtngInfos())) {
					String propertyZip = null;
					String propertyAddress = null;
					
					for (int i = entry.getRtngResponse().getRtngInfos().size() - 1; i > 0; i--) {
						if(entry.getRtngResponse().getRtngInfos().get(i).getOrderCreatedDate() == null)
							continue;
						else {
							propertyZip = entry.getRtngResponse().getRtngInfos().get(i).getPropertyZip();
							propertyAddress = entry.getRtngResponse().getRtngInfos().get(i).getPropertyAddress1();
							break;
						}
					}
					//If all the records have OrderCreatedDate as null, then we choose the last record for Zip and Address
					if(propertyZip == null) {
						propertyZip = entry.getRtngResponse().getRtngInfos().get(entry.getRtngResponse().getRtngInfos().size() - 1).getPropertyZip();
						propertyAddress = entry.getRtngResponse().getRtngInfos().get(entry.getRtngResponse().getRtngInfos().size() - 1).getPropertyAddress1();
					}
					String requestXMLRegularFormatted = MessageFormat.format(requestXMLRegular, caUserName, caPassword,
							StringEscapeUtils.escapeXml10(propertyZip),
							StringEscapeUtils.escapeXml10(propertyAddress));
					String requestXMLREOFormatted = MessageFormat.format(requestXMLREO, caUserName, caPassword,
							StringEscapeUtils.escapeXml10(propertyZip),
							StringEscapeUtils.escapeXml10(propertyAddress));

					LOGGER.info("RTNGRespons start : ");
					Long startTimeRTNGRespons = DateTime.now().getMillis();
					if(null != entry.getRtngResponse() && null != entry.getRtngResponse().getRtngInfos()) {
						for(RtngInfo infoRTNG: entry.getRtngResponse().getRtngInfos()) {
							LOGGER.info("property zip : "+ infoRTNG.getPropertyZip()+", property addrerss1 : "+infoRTNG.getPropertyAddress1());
						}
					}
					log.info("Time taken for RTNGRespons : " + (DateTime.now().getMillis() - startTimeRTNGRespons) + "ms");
					LOGGER.info("RTNGRespons end : ");

					LOGGER.info("requestXMLRegular : " + requestXMLRegularFormatted);
					LOGGER.info("requestXMLREO : " + requestXMLREOFormatted);

					DynamicPricingIntgAudit dpIntgAudit = new DynamicPricingIntgAudit();

					Long startTime = DateConversionUtil.getMillisFromUtcToEst(System.currentTimeMillis());

					try {
						fetchCollateralAnalyticsRegularAPIResult(entry, requestXMLRegularFormatted, caUrl);
						fetchCollateralAnalyticsREOAPIResult(entry, requestXMLREOFormatted, caUrl);
						dpIntgAudit.setStatus(TransactionStatus.SUCCESS.getTranStatus());
					} catch (SystemException e) {
						String errorDetail = dpFileProcessBO.saveDPProcessErrorDetail(entry.getId(),
								IntegrationType.CA_INTEGRATION.getIntegrationType(), entry.getErrorDetail(), e);
						entry.setErrorDetail(errorDetail);
						entry.setAssignment(DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue());
						dpIntgAudit.setStatus(TransactionStatus.FAIL.getTranStatus());
						Map<String, String> errorMap = ConversionUtil.convertJson(errorDetail, Map.class);
						dpIntgAudit.setErrorDescription(errorMap.get(IntegrationType.CA_INTEGRATION.getIntegrationType()));
						LOGGER.error(e.getLocalizedMessage(), e);
					}

					Long endTime = DateConversionUtil.getMillisFromUtcToEst(System.currentTimeMillis());

					entry.setStartTime(BigInteger.valueOf(startTime));
					entry.setEndTime(BigInteger.valueOf(endTime));
					LOGGER.info("CAIntegrationClientImpl -> invokeCollateralAnalyticsAPI dp process CA call time taken."
							+ (startTime - endTime));

					DPProcessParam dpProcessParam = new DPProcessParam();
					dpProcessParam.setId(entry.getId());
					dpIntgAudit.setDpProcessParam(dpProcessParam);
					dpIntgAudit.setStartTime(entry.getStartTime());
					dpIntgAudit.setEndTime(entry.getEndTime());
					dpIntgAudit.setEventType(CA_EVENT_TYPE);
					dpFileProcessBO.saveDPProcessIntgAudit(dpIntgAudit);
				}

				LOGGER.info("CAIntegrationClientImpl -> invokeCollateralAnalyticsAPI dp process CA audit entry done.");
				MDC.remove(RAClientConstants.LOAN_NUMBER);
			}
			LOGGER.info("CAIntegrationClientImpl -> invokeCollateralAnalyticsAPI ended.");
		}
		log.info("Time taken for Week0CAIntegration : " + (DateTime.now().getMillis() - start) + "ms");
		LOGGER.info("Week0CAIntegration -> processTask ended.");
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
				LOGGER.error(e.getLocalizedMessage(), e);
			}
		}

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
				LOGGER.error(e.getLocalizedMessage(), e);
			}
		}
	}

	/**
	 * @param xml
	 * @param caUrl
	 * @return
	 * @throws SystemException
	 * @throws RestClientException
	 */
	private Document extractResult(String xml, String caUrl)
			throws SystemException, RestClientException {
		Document document = null;
		SSLCertificateDownloadUtil.validateUrl(caUrl, "CA_URL");

		String responseEntity = null;
		try {
			responseEntity = restIntegrationClient.execute(caUrl, xml, null, null, null, MediaType.APPLICATION_XML, String.class);
		} catch (SystemException ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
			throw new SystemException(CoreExceptionCodes.RACLNCOM001, new Object[] {}, ex);
		} catch (Exception ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
			throw new SystemException(CoreExceptionCodes.RACLNCOM001, new Object[] {}, ex);
		}

		if (responseEntity != null) {
			try {
				document = documentBuilder.parse(new InputSource(new StringReader(responseEntity)));
			} catch (SAXException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			} catch (IOException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			}
		}
		return document;
	}

}