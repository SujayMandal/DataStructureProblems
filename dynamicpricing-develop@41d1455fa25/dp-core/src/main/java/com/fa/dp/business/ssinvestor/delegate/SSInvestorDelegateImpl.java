package com.fa.dp.business.ssinvestor.delegate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.business.pmi.entity.PmiInsuranceCompany;
import com.fa.dp.business.pmi.entity.PmiInsuranceCompaniesFile;
import com.fa.dp.business.pmi.info.PmiInsuranceCompaniesFileInfo;
import com.fa.dp.business.pmi.info.PmiInsuranceCompanyInfo;
import com.fa.dp.business.ssinvestor.bo.SpclServicingInvestorBO;
import com.fa.dp.business.ssinvestor.entity.SpclServicingInvestor;
import com.fa.dp.business.ssinvestor.entity.SpclServicingInvestorFile;
import com.fa.dp.business.ssinvestor.info.SpclServicingInvestorFileInfo;
import com.fa.dp.business.ssinvestor.info.SpclServicingInvestorInfo;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.business.util.SSInvestorFile;
import com.fa.dp.business.validation.file.util.InputFileValidationUtil;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.business.week0.entity.DynamicPricingFilePrcsStatus;
import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Named
public class SSInvestorDelegateImpl extends AbstractDelegate implements SSInvestorDelegate {

	@Inject
	private SpclServicingInvestorBO spclServicingInvestorBo;
	
	@Inject
	private DPFileProcessBO dpFileProcessBO;

	private static final Logger LOGGER = LoggerFactory.getLogger(SSInvestorDelegateImpl.class);

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public List<SpclServicingInvestorInfo> validateFile(MultipartFile file) throws SystemException, IOException {
		
		LOGGER.info("Checking if week0 file is In progress");
		DynamicPricingFilePrcsStatus dpFilePrcsStatus = dpFileProcessBO
				.findDPProcessStatusByStatus(DPFileProcessStatus.IN_PROGRESS.getFileStatus());
		if (!Objects.isNull(dpFilePrcsStatus)) {
			LOGGER.error("SS Investor file can be uploaded only after the current week 0 file processing is completed");
			throw new SystemException(CoreExceptionCodes.DP034, new Object[] {});
		}

		LOGGER.info("Validating file name of uploaded file");
		InputFileValidationUtil.validateXLSFileName(FilenameUtils.getExtension(file.getOriginalFilename()));

		Sheet datatypeSheet = null;
		// Fetch Investor Matrix sheet from the workbook
		try {
			LOGGER.info("Fetching Investor matrix sheet from the uploaded file");
			datatypeSheet = getInvestorMatrixSheet(file);
		} catch (IOException ioe) {
			LOGGER.error(ioe.getLocalizedMessage(), ioe);
			throw ioe;
		}
		// validate and fetch Special servicing Investors from the sheet
		return getSSInvestorInfos(datatypeSheet);
	}

	private Sheet getInvestorMatrixSheet(MultipartFile file) throws IOException, SystemException {
		Workbook workbook = null;
		Sheet datatypeSheet = null;
		try {
			workbook = new XSSFWorkbook(file.getInputStream());
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				if (StringUtils.equals(workbook.getSheetName(i), SSInvestorFile.INVESTOR_MATRIX.getValue())) {
					datatypeSheet = workbook.getSheetAt(i);
					break;
				}
			}
			if (null == datatypeSheet) {
				LOGGER.error("Investor Matrix sheet is missing in the uploaded file");
				throw new SystemException(CoreExceptionCodes.DP030, new Object[] {});
			}
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			throw e;
		} finally {
			IOUtils.closeQuietly(workbook);
		}
		return datatypeSheet;
	}

	private List<SpclServicingInvestorInfo> getSSInvestorInfos(Sheet datatypeSheet) throws SystemException {
		LOGGER.info("Validating the columns in Investor matrix sheet");
		List<SpclServicingInvestorInfo> ssInvestorInfos = new ArrayList<>();
		int aspsClColNum = -1;
		int investorNameColNum = -1;
		Row headerRow = datatypeSheet.getRow(1);
		DataFormatter df = new DataFormatter();
		for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
			String cellValue = df.formatCellValue(headerRow.getCell(i));
			if (StringUtils.equals(SSInvestorFile.ASPS_CLIENT_ID.getValue(), cellValue)) {
				aspsClColNum = i;
			} else if (StringUtils.equals(SSInvestorFile.INVESTOR_NAME.getValue(), cellValue)) {
				investorNameColNum = i;
			}
		}

		if (aspsClColNum == -1 || investorNameColNum == -1) {
			List<String> missingCols = new ArrayList<>();
			if (aspsClColNum == -1)
				missingCols.add(SSInvestorFile.ASPS_CLIENT_ID.getValue());
			if (investorNameColNum == -1)
				missingCols.add(SSInvestorFile.INVESTOR_NAME.getValue());
			LOGGER.error("Columns Missing in Investor Matrix Sheet: " + missingCols.toString());
			throw new SystemException(CoreExceptionCodes.DP031, new Object[] { String.join(", ", missingCols) });
		}

		SpclServicingInvestorInfo ssInvestorInfo;
		for (int rowIndex = 2; rowIndex <= datatypeSheet.getLastRowNum(); rowIndex++) {
			ssInvestorInfo = new SpclServicingInvestorInfo();
			Row currentRow = datatypeSheet.getRow(rowIndex);
			ssInvestorInfo.setInvestorCode(getCellValue(currentRow, df, aspsClColNum));
			ssInvestorInfo.setInvestorName(getCellValue(currentRow, df, investorNameColNum));
			ssInvestorInfos.add(ssInvestorInfo);
		}
		if (CollectionUtils.isEmpty(ssInvestorInfos)) {
			LOGGER.error("No records found in uploaded file to process");
			throw new SystemException(CoreExceptionCodes.DP020, new Object[] {});
		}

		return ssInvestorInfos;
	}

	private String getCellValue(Row currentRow, DataFormatter df, int index) {
		LOGGER.info("getCellValue() current row : " + currentRow);
		Cell cell = currentRow.getCell(index);
		LOGGER.info("getCellValue() cell : " + cell);
		String result = null;
		if (null != cell) {
			cell.setCellType(CellType.STRING);
			result = df.formatCellValue(cell);
		}
		return result;
	}
	
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public List<SpclServicingInvestorInfo> uploadSSInvestors(String fileName, List<SpclServicingInvestorInfo> ssInvestors) {
		// First make the current active file as inactive in the db
		LOGGER.info("Changing the currently Active file to Inactive state");
		spclServicingInvestorBo.updateActiveToInactive();

		// Save the uploaded file entry in db
		SpclServicingInvestorFile ssInvestorFile = new SpclServicingInvestorFile();
		ssInvestorFile.setUploadedFileName(fileName);
		ssInvestorFile.setActive(true);
		LOGGER.info("Saving the uploaded file " + fileName + " to db");
		ssInvestorFile = spclServicingInvestorBo.saveSSInvFile(ssInvestorFile);
		
//		SpclServicingInvestorFileInfo ssInvFileInfo = convert(ssInvFile, SpclServicingInvestorFileInfo.class);
		SpclServicingInvestorFileInfo ssInvestorFileInfo = new SpclServicingInvestorFileInfo();
		try {
			BeanUtils.copyProperties(ssInvestorFile, ssInvestorFileInfo);
			ssInvestorFileInfo.setCreatedDate(ssInvestorFile.getCreatedDate());
		} catch (BeansException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		
		//Save actual SSInvestor entries to db
		ssInvestors.forEach(c -> c.setSsInvestorFileId(ssInvestorFileInfo));
		LOGGER.info("Saving the actual Special Servicing Investor records to db ");
		List<SpclServicingInvestorInfo> result = new ArrayList<>();
		for(SpclServicingInvestorInfo info : ssInvestors) {
			SpclServicingInvestor ssInvestor = new SpclServicingInvestor();
			ssInvestor = convert(info, SpclServicingInvestor.class);
			ssInvestor = spclServicingInvestorBo.saveSSInvestor(ssInvestor);
			info.setId(ssInvestor.getId());
			result.add(info);
		}
		return result;
	}

	@Override
	public List<SpclServicingInvestorFileInfo> getSsInvestorFiles() {
		List<SpclServicingInvestorFileInfo> result = new ArrayList<>();
		List<SpclServicingInvestorFile> entities;

		entities = spclServicingInvestorBo.getAllSSInvestorFiles();
		if (CollectionUtils.isNotEmpty(entities)) {
			SpclServicingInvestorFileInfo fileInfo;
			result = new ArrayList<>();
			for (SpclServicingInvestorFile entity : entities) {
				try {
					fileInfo = new SpclServicingInvestorFileInfo();
					BeanUtils.copyProperties(fileInfo, entity);
					fileInfo.setCreatedDate(entity.getCreatedDate());
					result.add(fileInfo);
				} catch (BeansException e) {
					LOGGER.error(e.getLocalizedMessage(), e);
				}
			}
			// result = convertToList(entities, SpclServicingInvestorFileInfo.class);
		}
		return result;
	}
	
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public List<PmiInsuranceCompanyInfo> validatePmiCompaniesFile(MultipartFile file) throws SystemException, IOException {

		LOGGER.info("Checking if weekN file is In progress");
		DPWeekNProcessStatus dpWeekNProcessStatus = dpFileProcessBO
				.findWeeknPrcsStatusByStatus(DPFileProcessStatus.IN_PROGRESS.getFileStatus());
		if (!Objects.isNull(dpWeekNProcessStatus)) {
			LOGGER.error("SS Investor file can be uploaded only after the current week N file processing is completed");
			throw new SystemException(CoreExceptionCodes.DP034, new Object[] {});
		}

		LOGGER.info("Validating file type of uploaded file");
		InputFileValidationUtil.validateXLSFileName(FilenameUtils.getExtension(file.getOriginalFilename()));

		// Fetch Investor Matrix sheet from the workbook
		LOGGER.info("Fetching first sheet from the uploaded file");
		Workbook workbook = null;
		Sheet datatypeSheet = null;
		try {
			workbook = new XSSFWorkbook(file.getInputStream());
			datatypeSheet = workbook.getSheetAt(0);
		} catch (IOException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			throw e;
		} finally {
			IOUtils.closeQuietly(workbook);
		}
		// validate and fetch Special servicing Investors from the sheet
		return getPmiCompanyInfos(datatypeSheet);
	}

	private List<PmiInsuranceCompanyInfo> getPmiCompanyInfos(Sheet datatypeSheet) throws SystemException {
		LOGGER.info("Validating the columns in first sheet");
		List<PmiInsuranceCompanyInfo> pmiCompaniesList = new ArrayList<>();
		int insuranceCompColNum = 0;
		int companyCodeColNum = 1;
		DataFormatter df = new DataFormatter();
//		Row headerRow = datatypeSheet.getRow(1);
//		for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
//			String cellValue = df.formatCellValue(headerRow.getCell(i));
//			if (StringUtils.equals(SSInvestorFile.ASPS_CLIENT_ID.getValue(), cellValue)) {
//				insuranceCompColNum = i;
//			} else if (StringUtils.equals(SSInvestorFile.INVESTOR_NAME.getValue(), cellValue)) {
//				companyCodeColNum = i;
//			}
//		}
//
//		if (insuranceCompColNum == -1 || companyCodeColNum == -1) {
//			List<String> missingCols = new ArrayList<>();
//			if (insuranceCompColNum == -1)
//				missingCols.add(SSInvestorFile.ASPS_CLIENT_ID.getValue());
//			if (companyCodeColNum == -1)
//				missingCols.add(SSInvestorFile.INVESTOR_NAME.getValue());
//			LOGGER.error("Columns Missing in Investor Matrix Sheet: " + missingCols.toString());
//			throw new SystemException(CoreExceptionCodes.DP031, new Object[] { String.join(", ", missingCols) });
//		}

		PmiInsuranceCompanyInfo pmiCompaniesInfo;
		for (int rowIndex = 1; rowIndex <= datatypeSheet.getLastRowNum(); rowIndex++) {
			pmiCompaniesInfo = new PmiInsuranceCompanyInfo();
			Row currentRow = datatypeSheet.getRow(rowIndex);
			pmiCompaniesInfo.setInsuranceCompany(getCellValue(currentRow, df, insuranceCompColNum));
			pmiCompaniesInfo.setCompanyCode(getCellValue(currentRow, df, companyCodeColNum));
			pmiCompaniesList.add(pmiCompaniesInfo);
		}
		if (CollectionUtils.isEmpty(pmiCompaniesList)) {
			LOGGER.error("No records found in uploaded file to process");
			throw new SystemException(CoreExceptionCodes.DP020, new Object[] {});
		}
		return pmiCompaniesList;
	}
	
	
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public List<PmiInsuranceCompanyInfo> uploadPmiCompanies(String fileName, List<PmiInsuranceCompanyInfo> pmiCompaniesList) {
		// First make the current active file as inactive in the db
		LOGGER.info("Changing the currently Active file to Inactive state");
		spclServicingInvestorBo.updatePmiActiveToInactive();

		// Save the uploaded file entry in db
		PmiInsuranceCompaniesFile pmiCompaniesFile = new PmiInsuranceCompaniesFile();
		pmiCompaniesFile.setUploadedFileName(fileName);
		pmiCompaniesFile.setActive(true);
		LOGGER.info("Saving the uploaded file " + fileName + " to db");
		pmiCompaniesFile = spclServicingInvestorBo.savePmiCompFile(pmiCompaniesFile);
		
		PmiInsuranceCompaniesFileInfo pmiCompaniesFileInfo = new PmiInsuranceCompaniesFileInfo();
		try {
			BeanUtils.copyProperties(pmiCompaniesFile, pmiCompaniesFileInfo);
		} catch (BeansException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
		}
		
		//Save actual PMI Companies entries to db
		pmiCompaniesList.forEach(c -> c.setPmiCompaniesFileId(pmiCompaniesFileInfo));
		LOGGER.info("Saving the actual Special Servicing Investor records to db ");
		List<PmiInsuranceCompanyInfo> result = new ArrayList<>();
		for(PmiInsuranceCompanyInfo info : pmiCompaniesList) {
			PmiInsuranceCompany pmiCompany = new PmiInsuranceCompany();
			pmiCompany = convert(info, PmiInsuranceCompany.class);
			pmiCompany = spclServicingInvestorBo.savePmiInsuranceCompany(pmiCompany);
			info.setId(pmiCompany.getId());
			result.add(info);
		}
		return result;
	}

}
