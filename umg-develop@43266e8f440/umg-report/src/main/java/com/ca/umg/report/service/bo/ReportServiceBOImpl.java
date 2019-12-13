package com.ca.umg.report.service.bo;

import static com.ca.framework.core.exception.BusinessException.newBusinessException;
import static com.ca.framework.core.exception.SystemException.newSystemException;
import static com.ca.umg.report.ReportExceptionCodes.REPORT_DOWNLOAD_ERROR;
import static com.ca.umg.report.ReportExceptionCodes.REPORT_STORE_ERROR;
import static com.ca.umg.report.engine.ReportEngineFactory.getReportEngine;
import static com.ca.umg.report.model.ReportExecutedStatus.SUCCESS;
import static com.ca.umg.report.service.ReportService.RA_REPORT_REQUEST_MAPPING;
import static com.ca.umg.report.service.ReportService.RA_REPORT_REQUEST_MAPPING_API;
import static com.ca.umg.report.util.ReportUtil.createAdminErrorMessage;
import static com.ca.umg.report.util.ReportUtil.getFileNameWithoutExt;
import static com.ca.umg.report.util.ReportUtil.getFormattedDate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.report.ReportExceptionCodes;
import com.ca.umg.report.engine.ReportEngine;
import com.ca.umg.report.model.ModelReportStatusInfo;
import com.ca.umg.report.model.ModelReportTemplateInfo;
import com.ca.umg.report.model.ReportExecutedStatus;
import com.ca.umg.report.model.ReportInfo;
import com.ca.umg.report.model.ReportTypes;
import com.ca.umg.report.service.dao.ModelReportStatusDAO;
import com.ca.umg.report.service.dao.ModelReportStatusDefinition;
import com.ca.umg.report.service.dao.ModelReportTemplateDAO2;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

@Named
public class ReportServiceBOImpl extends AbstractDelegate implements ReportServiceBO {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceBOImpl.class);

	private static final String SAN_BASE_PROPERTY = "sanBase";
	private static final String BASE_REPORT_URL_PROPERTY = "baseReportURL";
	private static final String REPOROT_FOLDER = "report";

	private static final String URL_SEPARATOR = "/";

	private static final String FILE_SEPARATOR = File.separator;

	@Inject
	private ModelReportStatusDAO modelReportStatusDAO;

	@Inject
	private ModelReportTemplateDAO2 modelReportTemplateDAO;

	@Inject
	private SystemParameterProvider parameterProvider;

	@Inject
	private UmgFileProxy umgFileProxy;

	@Override
	public void generateReport(final ModelReportStatusInfo reoprtStatusInfo) throws BusinessException, SystemException {
		LOGGER.info("Getting active report template for :" + reoprtStatusInfo.toString());
		final ModelReportTemplateInfo reportTemplate = modelReportTemplateDAO.getActiveReportTemplate(reoprtStatusInfo);
		reoprtStatusInfo.setReportVersion(reportTemplate.getReportVersion());
		reoprtStatusInfo.setReportName(reportTemplate.getName());
		OutputStream outputStream = null;
		LOGGER.info("Checking whethere report is already generated or not for :" + reoprtStatusInfo.toString());
		if (!isReportGeneratedForTransaction(reoprtStatusInfo)) {
			LOGGER.info("Repoort is not generated, hence generating now");
			reoprtStatusInfo.setReportLocation(getReportLocation(reoprtStatusInfo));
			reoprtStatusInfo.setReportFileName(createRreportFileName(reoprtStatusInfo));
			reoprtStatusInfo.setReportUrl(getReportURL(reoprtStatusInfo));
			reportTemplate.setReportJsonString(reoprtStatusInfo.getReportJsonString());
			final ReportEngine reportEngine = getReportEngine(reportTemplate);

			try {
				final File file = new File(reoprtStatusInfo.getReportLocation());
				if (!file.exists()) {
					LOGGER.info("Folder does not exists, hence creating new folders");
					file.mkdirs();
				}

				LOGGER.info("Saving report {} to directory {}, and report name is : {}.", file.getPath(),
						reoprtStatusInfo.getReportFileName());
				outputStream = new FileOutputStream(new File(file, reoprtStatusInfo.getReportFileName()));
				reportEngine.execute(reportTemplate, outputStream);
				reoprtStatusInfo.setReportExecutionstatus(ReportExecutedStatus.SUCCESS.getStatus());
				LOGGER.info("Report is generated successfully");
			} catch (BusinessException | SystemException e) {
				LOGGER.error(e.getMessage());
				LOGGER.error(e.getCode());
				LOGGER.error(e.getLocalizedMessage());
				reoprtStatusInfo.setReportExecutionstatus(ReportExecutedStatus.FAILED.getStatus());
				newBusinessException(e.getCode(),
						new String[] { createAdminErrorMessage(e.getCode(), e.getLocalizedMessage()) });
			} catch (FileNotFoundException fnfe) {
				LOGGER.error(fnfe.getMessage());
				LOGGER.error(fnfe.getLocalizedMessage());
				reoprtStatusInfo.setReportExecutionstatus(ReportExecutedStatus.FAILED.getStatus());
				newBusinessException(REPORT_STORE_ERROR.getErrorCode(), new String[] {
						createAdminErrorMessage(REPORT_STORE_ERROR.getErrorCode(), fnfe.getLocalizedMessage()) });
			} finally {
				LOGGER.info("Saving generated repoort into database, repoort Status is:" + reoprtStatusInfo.toString());
				saveReportStatus(reoprtStatusInfo);

				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException ioe) {
						LOGGER.error(ioe.getMessage());
						LOGGER.error(ioe.getLocalizedMessage());
						newBusinessException(REPORT_STORE_ERROR.getErrorCode(),
								new String[] { createAdminErrorMessage(REPORT_STORE_ERROR.getErrorCode(),
										ioe.getLocalizedMessage()) });
					}
				}
			}
		}
	}

	@Override
	public boolean isReportGeneratedForTransaction(final ModelReportStatusInfo reoprtStatusInfo)
			throws BusinessException, SystemException {
		final List<ModelReportStatusDefinition> reports = modelReportStatusDAO
				.findByUmgTransactionIdAndReportExecutionstatusAndReportTemplateId(
						reoprtStatusInfo.getUmgTransactionId(), SUCCESS.getStatus(),
						reoprtStatusInfo.getReportTemplateId());

		boolean status;
		if (reports != null && reports.size() > 0) {
			LOGGER.info("Report already generated for :" + reoprtStatusInfo.toString());
			status = true;
		} else {
			LOGGER.info("Report is NOT already generated for :" + reoprtStatusInfo.toString());
			status = false;
		}

		return status;
	}

	@Override
	public boolean isReportGeneratedForModel(final ModelReportStatusInfo reoprtStatusInfo)
			throws BusinessException, SystemException {
		final List<ModelReportStatusDefinition> reports = modelReportStatusDAO
				.findByReportExecutionstatusAndReportTemplateId(SUCCESS.getStatus(),
						reoprtStatusInfo.getReportTemplateId());

		boolean status;
		if (reports != null && reports.size() > 0) {
			LOGGER.info("Report is already generated for :" + reoprtStatusInfo.toString());
			status = true;
		} else {
			LOGGER.info("Report is NOT already generated for :" + reoprtStatusInfo.toString());
			status = false;
		}

		return status;
	}

	@Override
	public byte[] downloadReport(final ModelReportStatusInfo reoprtStatusInfo)
			throws BusinessException, SystemException {
		LOGGER.info("Downloading report, Reportr Status info is :" + reoprtStatusInfo.toString());

		byte[] bytes = null;
		LOGGER.info("Getting report location from database");
		final List<ModelReportStatusDefinition> reports = modelReportStatusDAO
				.findByUmgTransactionIdAndReportExecutionstatusAndReportTemplateId(
						reoprtStatusInfo.getUmgTransactionId(), SUCCESS.getStatus(),
						reoprtStatusInfo.getReportTemplateId());
		InputStream inputStream = null;
		try {
			if (reports != null && reports.size() > 0) {
				LOGGER.info("Got report location from database");
				final ModelReportStatusDefinition reportDefinition = reports.get(0);
				final String reportDirPath = reportDefinition.getReportLocation();
				final String reportFileName = reportDefinition.getReportFileName();
				reoprtStatusInfo.setReportFileName(reportDefinition.getReportFileName());

				LOGGER.info("Report URL is: " + reportDefinition.getReportUrl());
				LOGGER.info("Report location is: " + reportDirPath);
				LOGGER.info("Report File Name is: " + reportFileName);

				final File file = new File(reportDirPath);
				if (file.exists() && file.isDirectory()) {
					File fileObj = new File(file, reportFileName);
					if (fileObj.isFile()) {
						LOGGER.info("File is avaiable, sending byte array to repsonse");
						inputStream = new FileInputStream(fileObj);
						if (inputStream != null) {
							bytes = convertStreamToByteArray(inputStream);
						}
					}
				} else {
					LOGGER.error("Report file does not exists in san location, san location is {}", reportDirPath);
				}
			}
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage());
			LOGGER.error(e.getLocalizedMessage());
			newSystemException(REPORT_DOWNLOAD_ERROR.getErrorCode(), new String[] {
					createAdminErrorMessage(REPORT_DOWNLOAD_ERROR.getErrorCode(), e.getLocalizedMessage()) });
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		if (bytes == null) {
			LOGGER.error("Report NOT found in san path, Please generate report below download");
			newSystemException(REPORT_DOWNLOAD_ERROR.getErrorCode(),
					new Object[] { createAdminErrorMessage(REPORT_DOWNLOAD_ERROR.getErrorCode(), "") });
		}

		return bytes;
	}

	@Override
	public ModelReportStatusInfo saveReportStatus(final ModelReportStatusInfo info)
			throws BusinessException, SystemException {
		LOGGER.info("Savinng report status into database, report status info is : " + info.toString());
		ModelReportStatusDefinition savedEntity = null;
		if (info != null) {
			final ModelReportStatusDefinition defination = convert(info, ModelReportStatusDefinition.class);
			savedEntity = modelReportStatusDAO.save(defination);
			LOGGER.info("Saved successfully, report status info is : " + info.toString());
		}
		return convert(savedEntity, ModelReportStatusInfo.class);
	}

	public String getReportURL(final ModelReportStatusInfo info) throws BusinessException, SystemException {
		LOGGER.info("Creating report URL, Report status info is :" + info.toString());

		final String reportURLBase = getProperty(BASE_REPORT_URL_PROPERTY);
		final StringBuilder sb = new StringBuilder();
		sb.append(reportURLBase);
		sb.append(RA_REPORT_REQUEST_MAPPING);
		sb.append(RA_REPORT_REQUEST_MAPPING_API);
		sb.append(URL_SEPARATOR).append(info.getUmgTransactionId());
		sb.append(URL_SEPARATOR).append(getFileNameWithoutExt(info.getReportName()));

		LOGGER.info("Report URL is :" + sb.toString());

		return sb.toString();
	}

	@Override
	public String getReportLocation(final ModelReportStatusInfo info) throws BusinessException, SystemException {
		LOGGER.info("Creating report location, Report status info is :" + info.toString());
		final String sanBase = umgFileProxy.getSanPath(parameterProvider.getParameter(SAN_BASE_PROPERTY));
		final StringBuilder sb = new StringBuilder();
		sb.append(sanBase).append(FILE_SEPARATOR);
		sb.append(info.getTenantId()).append(FILE_SEPARATOR);
		sb.append(REPOROT_FOLDER).append(FILE_SEPARATOR);

		if (info.getModelName() != null && info.getModelName().length() > 0) {
			sb.append(info.getModelName()).append(FILE_SEPARATOR);
		}

		sb.append(info.getUmgTransactionId()).append(FILE_SEPARATOR);

		if (info.getReportName() != null && info.getReportName().length() > 0) {
			sb.append(getFileNameWithoutExt(info.getReportName())).append(FILE_SEPARATOR);
		}

		sb.append(info.getReportVersion());

		LOGGER.info("Report Locaion is :" + sb.toString());

		return sb.toString();
	}

	@Override
	public void storeGeneratedReport(final ModelReportStatusInfo info, final byte[] byteArray)
			throws BusinessException, SystemException {
		writeFileToDirectory(info, byteArray);
	}

	private String getProperty(final String property) {
		return parameterProvider.getParameter(property);
	}

	private void writeFileToDirectory(final ModelReportStatusInfo info, final byte[] byteArray) throws SystemException {
		final File file = new File(info.getReportLocation());
		if (!file.exists()) {
			file.mkdirs();
		}
		Document document = null;
		PdfWriter writer = null;
		try {
			LOGGER.info("Saving report {} to directory {}, and report name is : {}.", file.getPath(),
					info.getReportFileName());
			// outputStream = new FileOutputStream(new File(file,
			// info.getReportFileName()));
			document = new Document();
			writer = PdfWriter.getInstance(document, new FileOutputStream(new File(file, info.getReportFileName())));
			document.open();
			document.add(new Paragraph("UMG transaction report"));

			LOGGER.info("Saved repoort {} successfully.", info.getReportFileName());
		} catch (DocumentException de) {
			throw new SystemException(ReportExceptionCodes.REPORT_STORE_ERROR.getErrorCode(), new Object[] {}, de);
		} catch (IOException e) {
			throw new SystemException(ReportExceptionCodes.REPORT_STORE_ERROR.getErrorCode(), new Object[] {}, e);
		} finally {
			try {
				if (document != null) {
					document.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (Exception e) {
				LOGGER.error(e.getLocalizedMessage());
				LOGGER.error("Exception occured closing Output Stream", e);
			}
		}

		LOGGER.info("Stored generated repoort into san path successfully, repoort Status is:" + info.toString());
	}

	public static byte[] convertStreamToByteArray(InputStream stream) throws SystemException {
		byte[] data = null;
		try {
			data = new byte[stream.available()];
			stream.read(data);
		} catch (IOException ioe) {
			LOGGER.error(ioe.getMessage());
			LOGGER.error(ioe.getLocalizedMessage());
			newSystemException(REPORT_DOWNLOAD_ERROR.getErrorCode(), new String[] {
					createAdminErrorMessage(REPORT_DOWNLOAD_ERROR.getErrorCode(), ioe.getLocalizedMessage()) });
		}
		return data;
	}

	@Override
	public ReportInfo getTransactionReportURL(final ModelReportStatusInfo reoprtStatusInfo)
			throws BusinessException, SystemException {
		LOGGER.info("Getting active report template for :" + reoprtStatusInfo.toString());
		final List<ModelReportStatusDefinition> reports = modelReportStatusDAO
				.findByUmgTransactionIdAndReportExecutionstatusAndReportTemplateId(
						reoprtStatusInfo.getUmgTransactionId(), SUCCESS.getStatus(),
						reoprtStatusInfo.getReportTemplateId());

		String url;
		String status;
		if (reports != null && reports.size() > 0) {
			LOGGER.info("Report Found for Transaction :" + reoprtStatusInfo.toString());
			url = reports.get(0).getReportUrl();
			status = reports.get(0).getReportExecutionstatus();
		} else {
			LOGGER.info("Report NOT found for transacrtion :" + reoprtStatusInfo.toString());
			url = "";
			status = ReportExecutedStatus.FAILED.getStatus();
		}

		final ReportInfo reportInfo = new ReportInfo();
		reportInfo.setReportURL(url);
		reportInfo.setReportExecutionStatus(status);
		reportInfo.setTransactionId(reoprtStatusInfo.getUmgTransactionId());
		reportInfo.setReportName(reoprtStatusInfo.getReportName());

		LOGGER.info(reportInfo.toString());

		return reportInfo;
	}

	private String createRreportFileName(final ModelReportStatusInfo reoprtStatusInfo) {
		final StringBuilder sb = new StringBuilder();
		sb.append(reoprtStatusInfo.getClientTransactionId()).append("_");
		sb.append(getFormattedDate(reoprtStatusInfo.getTransactionCreatedDate())).append("_");
		sb.append(getFileNameWithoutExt(reoprtStatusInfo.getReportName()));
		sb.append(ReportTypes.PDF.getReportFileExt());

		LOGGER.info("Report file name is :" + sb.toString());
		return sb.toString();
	}
}
