package com.fa.dp.business.filter.bo;

import com.fa.dp.business.command.dao.CommandDAO;
import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.command.info.CommandProcess;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.ssinvestor.entity.SpclServicingInvestor;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.dao.DPProcessParamsDao;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.report.info.DPWeek0ReportInfo;
import com.fa.dp.business.week0.report.vacant.mapper.DPWeek0VacantReportMapper;
import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.KeyValue;
import com.fa.dp.core.util.RAClientConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.MDC;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Named
@Slf4j
public class DPProcessParamsBOImpl extends AbstractDelegate implements DPProcessParamsBO {

	@Inject
	private DPProcessParamsDao dpProcessParamsDao;

	@Inject
	private CacheManager cacheManager;

	@Inject
	private CommandDAO commandDAO;

	@Inject
	private DPWeek0VacantReportMapper dpWeek0VacantReportMapper;

	private static final String SUCCESS = "SUCCESS";
	private static final String FAIL = "FAIL";
	private static final String BLANK = "";

	private static enum PropertyType {
		CONDO,
		APART,
		TWO,
		THREE,
		FOUR,
		DUPL,
		TRIP,
		QUAD,
		MULTI,
		MANUF,
		MOBIL,
		MODUL,
		SINGLE,
		DETA,
		SF,
		TOWN,
		ATTA,
		ROW
	}

	;

	@Override
	public DPProcessParam saveDPProcessParam(DPProcessParam dpProcessParam) {
		dpProcessParam.setUpdateTimestamp(DateConversionUtil.getCurrentUTCTime().getMillis());
		return dpProcessParamsDao.save(dpProcessParam);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public DPProcessParamInfo saveDPProcessParamInfo(DPProcessParamInfo dpProcessParamInfo) {
		DPProcessParamInfo object = null;
		if (dpProcessParamInfo != null) {
			DPProcessParam dpProcessParam = convert(dpProcessParamInfo, DPProcessParam.class);
			dpProcessParam.setUpdateTimestamp(DateConversionUtil.getCurrentUTCTime().getMillis());
			dpProcessParam = dpProcessParamsDao.save(dpProcessParam);
			object = convert(dpProcessParam, DPProcessParamInfo.class);
		}
		return object;
	}


	@Override
	public DPProcessParam findInWeek0ForAssetNumber(String selrPropIdVcNn) throws SystemException {
		List<DPProcessParam> dpProcessParams = dpProcessParamsDao.findLatestNonDuplicateInWeek0ForGivenAsset(selrPropIdVcNn);
		return !dpProcessParams.isEmpty() ? dpProcessParams.get(0) : null;
	}

	@Override
	public List<DPProcessParam> saveDPProcessParams(List<DPProcessParam> dpProcessParam) {
		return dpProcessParamsDao.saveAll(dpProcessParam);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public List<DPProcessParamInfo> saveDPProcessParamInfos(List<DPProcessParamInfo> dpProcessParamInfoList) {
		List<DPProcessParam> paramObject = convertToList(dpProcessParamInfoList, DPProcessParam.class);
		List<DPProcessParam> list = dpProcessParamsDao.saveAll(paramObject);
		return convertToList(list, DPProcessParamInfo.class);
	}

	@Override
	public List<DPProcessParam> searchByAssetNumber(String assetNumber) {
		return dpProcessParamsDao.findByAssetNumber(assetNumber);
	}

	@Override
	public List<DPProcessParam> findByAssetNumberAndClassification(String assetNumber, String classification) {
		return dpProcessParamsDao.findByAssetNumberAndClassificationOrderByLastModifiedDateDesc(assetNumber, classification);
	}

	public static <E extends Enum<E>> boolean isInEnum(String value, Class<E> enumClass) {
		for (E e : enumClass.getEnumConstants()) {
			if (StringUtils.startsWith(StringUtils.lowerCase(value, Locale.getDefault()), StringUtils.lowerCase(e.name(), Locale.getDefault())))
				return true;
		}
		return false;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
	public DPProcessParamInfo filterOnDuplicates(DPProcessParamInfo columnEntry, List<DPProcessParamInfo> dpProcessParams) {
		DPProcessParamInfo filteredParam = null;
		for (DPProcessParamInfo dpProcessEntry : dpProcessParams) {
			if (DPProcessFilterParams.ELIGIBLE.getValue().equalsIgnoreCase(dpProcessEntry.getEligible())) {
				if (DPProcessFilterParams.ASSIGNMENT_ERROR.getValue().equalsIgnoreCase(dpProcessEntry.getAssignment())) {
					// If there was any technical error, update old record and process new record in normal flow
					updateWeek0OldRecord(dpProcessEntry.getAssetNumber(), DPProcessFilterParams.ELIGIBLE_OUT_OF_SCOPE.getValue(),
							dpProcessEntry.getNotes());
				} else if (!dpProcessEntry.getClassification().equalsIgnoreCase(columnEntry.getClassification())) {
					// If classification has changed, update old record and process new record in normal flow
					updateWeek0OldRecord(dpProcessEntry.getAssetNumber(), DPProcessFilterParams.INELIGIBLE.getValue(),
							String.format(DPProcessFilterParams.NOTES_TRANS.getValue(), dpProcessEntry.getClassification(),
									columnEntry.getClassification()));
					// updateWeekNOldRecord
				} else if (dpProcessEntry.getAssetValue().equalsIgnoreCase(columnEntry.getAssetValue())) {
					// duplicate record is returned back
					filteredParam = setParams(columnEntry, false, false, false);
					String process = null;
					if (DPProcessParamAttributes.OCN.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEK0_OCN.getCommmandProcess();
					else if (DPProcessParamAttributes.PHH.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEK0_PHH.getCommmandProcess();
					else if (DPProcessParamAttributes.NRZ.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEK0_NRZ.getCommmandProcess();
					List<Command> command = commandDAO.findByProcess(process, DPAConstants.DUPLICATE_FILTER);
					CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
					filteredParam.setCommand(commandInfo);
				} else {
					DateTimeFormatter dateFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
					DateTime date = new DateTime();
					updateWeek0OldRecord(dpProcessEntry.getAssetNumber(), DPProcessFilterParams.ELIGIBLE_OUT_OF_SCOPE.getValue(),
							String.format(DPProcessFilterParams.NOTES_REV.getValue(), date.toString(dateFormat)));
				}
				break;
			}
		}
		return filteredParam;
	}

	/*
	 * Update Old record in db to 'Out of Scope' and 'Property Revalued'
	 */
	private void updateWeek0OldRecord(String assetNumber, String eligiblity, String notes) {
		DateTime date = new DateTime();
		// DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-mm-dd hh:mm:ss");
		log.info("Updating the existing Sop week0 record {} to Ineligible/Out of scope",assetNumber);
		dpProcessParamsDao.updateWeek0Record(eligiblity, notes, date.getMillis(), assetNumber, DPProcessFilterParams.ELIGIBLE.getValue());
	}

	@Override
	public KeyValue<List<DPProcessParamInfo>, List<DPProcessParamInfo>> filterOnInvestorCode(List<SpclServicingInvestor> aspsClients,
			DPProcessParamEntryInfo inputParamEntry) {
		List<DPProcessParamInfo> filteredOutParams = new ArrayList<>();
		List<DPProcessParamInfo> successParams = new ArrayList<>();
		List<String> aspsClientIds = convertToList(aspsClients, String.class);
		for (DPProcessParamInfo columnEntry : inputParamEntry.getColumnEntries()) {
			MDC.put(RAClientConstants.LOAN_NUMBER, columnEntry.getAssetNumber());
			if (aspsClientIds.contains(columnEntry.getClientCode())) {
				DPProcessParamInfo filteredParam = setParams(columnEntry, false, false, true);
				String process = null;
				if (DPProcessParamAttributes.OCN.getValue().equals(columnEntry.getClassification()))
					process = CommandProcess.WEEK0_OCN.getCommmandProcess();
				else if (DPProcessParamAttributes.PHH.getValue().equals(columnEntry.getClassification()))
					process = CommandProcess.WEEK0_PHH.getCommmandProcess();
				else if (DPProcessParamAttributes.NRZ.getValue().equals(columnEntry.getClassification()))
					process = CommandProcess.WEEK0_NRZ.getCommmandProcess();
				List<Command> command = commandDAO.findByProcess(process, DPAConstants.INVESTOR_FILTER);
				CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
				filteredParam.setCommand(commandInfo);
				filteredOutParams.add(filteredParam);
			} else {
				successParams.add(columnEntry);
			}
			MDC.remove(RAClientConstants.LOAN_NUMBER);
		}
		return new KeyValue<List<DPProcessParamInfo>, List<DPProcessParamInfo>>(successParams, filteredOutParams);
	}

	@Override
	public KeyValue<List<DPProcessParamInfo>, List<DPProcessParamInfo>> filterOnAssetValue(DPProcessParamEntryInfo inputParamEntry) {
		List<DPProcessParamInfo> filteredOutEntries = new ArrayList<>();
		List<DPProcessParamInfo> successEntries = new ArrayList<>();

		String process = null;
		String maxAssetValue = null;
		String minAssetValue = null;
		for (DPProcessParamInfo columnEntry : inputParamEntry.getColumnEntries()) {
			MDC.put(RAClientConstants.LOAN_NUMBER, columnEntry.getAssetNumber());

			if (DPProcessParamAttributes.OCN.getValue().equals(columnEntry.getClassification())) {
				process = CommandProcess.WEEK0_OCN.getCommmandProcess();
				maxAssetValue = (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_MAXVALUE);
				minAssetValue = (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_OCN_MINVALUE);
			} else if (DPProcessParamAttributes.PHH.getValue().equals(columnEntry.getClassification())) {
				process = CommandProcess.WEEK0_PHH.getCommmandProcess();
				maxAssetValue = (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_MAXVALUE);
				minAssetValue = (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_MINVALUE);
			} else if (DPProcessParamAttributes.NRZ.getValue().equals(columnEntry.getClassification())) {
				process = CommandProcess.WEEK0_NRZ.getCommmandProcess();
				maxAssetValue = (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_MAXVALUE);
				minAssetValue = (String) cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_NRZ_MINVALUE);
			}

			if (Integer.parseInt(columnEntry.getAssetValue()) > Integer.parseInt(maxAssetValue)
					|| Integer.parseInt(columnEntry.getAssetValue()) <= Integer.parseInt(minAssetValue)) {
				DPProcessParamInfo filteredParam = setParams(columnEntry, false, true, false);

				List<Command> command = commandDAO.findByProcess(process, DPAConstants.UNSUPPORTEDASSET_FILTER);
				CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
				filteredParam.setCommand(commandInfo);
				filteredOutEntries.add(filteredParam);
			} else {
				successEntries.add(columnEntry);
			}
			MDC.remove(RAClientConstants.LOAN_NUMBER);
		}
		return new KeyValue<>(successEntries, filteredOutEntries);
	}

	@Override
	public KeyValue<List<DPProcessParamInfo>, List<DPProcessParamInfo>> filterOnPropertyType(DPProcessParamEntryInfo inputParamEntry) {
		List<DPProcessParamInfo> filteredOutEntries = new ArrayList<>();
		List<DPProcessParamInfo> successEntries = new ArrayList<>();

		String process = null;
		for (DPProcessParamInfo columnEntry : inputParamEntry.getColumnEntries()) {
			MDC.put(RAClientConstants.LOAN_NUMBER, columnEntry.getAssetNumber());

			// check for Property Type validity only if RTNG is successful
			if (StringUtils.equalsIgnoreCase(columnEntry.getRtngResponse().getTransactionStatus(), SUCCESS)) {
				if (isInEnum(columnEntry.getPropertyType(), PropertyType.class)) {
					successEntries.add(columnEntry);
				} else {
					DPProcessParamInfo filteredParam = setParams(columnEntry, true, false, false);
					if (DPProcessParamAttributes.OCN.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEK0_OCN.getCommmandProcess();
					else if (DPProcessParamAttributes.PHH.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEK0_PHH.getCommmandProcess();
					else if (DPProcessParamAttributes.NRZ.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEK0_NRZ.getCommmandProcess();
					List<Command> command = commandDAO.findByProcess(process, DPAConstants.UNSUPPORTEDPROP_FILTER);
					CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
					filteredParam.setCommand(commandInfo);
					filteredOutEntries.add(filteredParam);
				}
			}
			MDC.remove(RAClientConstants.LOAN_NUMBER);
		}
		return new KeyValue<List<DPProcessParamInfo>, List<DPProcessParamInfo>>(successEntries, filteredOutEntries);
	}

	/*
	 * Setting fields for ineligible records
	 */
	private static DPProcessParamInfo setParams(DPProcessParamInfo colEntry, boolean isPropertyType, boolean isAssetValue, boolean isInvestorCode) {
		DPProcessParamInfo filteredEntry = colEntry;
		String failureNotes = BLANK;
		String notes = colEntry.getNotes();
		if (StringUtils.isNotEmpty(notes)) {
			int index = notes.indexOf(DPProcessFilterParams.NOTES_PT.getValue());
			if (index > -1) {
				failureNotes = notes.substring(index + 27, notes.length());
			} else {
				failureNotes = notes;
			}
		}
		if (!isPropertyType && !isAssetValue && !isInvestorCode) {
			filteredEntry.setNotes(DPProcessFilterParams.NOTES_DUP.getValue());
		} else if (isInvestorCode) {
			filteredEntry.setNotes(DPProcessFilterParams.NOTES_INV.getValue());
		} else if (isAssetValue && isPropertyType) {
			filteredEntry.setNotes(DPProcessFilterParams.NOTES_PT.getValue() + DPProcessFilterParams.NOTES_AV.getValue() + failureNotes);
			return filteredEntry;
		}
		filteredEntry.setEligible(DPProcessFilterParams.INELIGIBLE.getValue());
		filteredEntry.setWeek0Price(new BigDecimal(colEntry.getListPrice()));
		filteredEntry.setAssignment(BLANK);
		//		DateTimeFormatter dateFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
		DateTime date = new DateTime();
		filteredEntry.setAssignmentDate(DateConversionUtil.getCurrentUTCTime().getMillis());
		filteredEntry.setUpdateTimestamp(date.getMillis());
		if (null == colEntry.getRtngResponse() || StringUtils.equalsIgnoreCase(colEntry.getRtngResponse().getTransactionStatus(), FAIL)) {
			filteredEntry.setState(BLANK);
			filteredEntry.setRtSource(BLANK);
			filteredEntry.setPropertyType(BLANK);
		}
		if (isPropertyType) {
			filteredEntry.setNotes(DPProcessFilterParams.NOTES_PT.getValue() + failureNotes);
		} else if (isAssetValue) {
			filteredEntry.setNotes(DPProcessFilterParams.NOTES_AV.getValue() + failureNotes);
		}
		return filteredEntry;
	}

	/**
	 * @param assetNumber
	 * @return DPProcessParam
	 * @throws SystemException
	 */
	@Override
	public DPProcessParam findOcwenLoanBYAssetNumber(String assetNumber) throws SystemException {
		DPProcessParam dpProcessParam = dpProcessParamsDao.findOcwenLoanBYAssetNumber(assetNumber);
		return dpProcessParam;
	}

	@Override
	public DPProcessParam findOutOfScopeLoanByAssetNumber(String assetNumber) throws SystemException {
		DPProcessParam dpProcessParam = dpProcessParamsDao.findOutOfScopeLoanByAssetNumber(assetNumber);
		return dpProcessParam;
	}

	@Override
	public List<DPProcessParam> findLatestNonDuplicateInWeek0ForGivenAsset(Set<String> assetFromHbz) {
		List<DPProcessParam> infoData = new ArrayList<>();
		try {
			infoData = dpProcessParamsDao.findLatestNonDuplicateInWeek0ForGivenAsset(assetFromHbz);
			//infoData = convertToList(weeknData, DPProcessParamInfo.class);
		} catch (Exception e) {
			log.error("Problem in getting non duplicate in Week0 for given asset numbers. {}.", e);
		}
		return infoData;
	}

	@Override
	public List<DPWeek0ReportInfo> fetchWeek0Report(Long startDate, Long endDate, List<String> clientCode) {
		List<DPProcessParam> reports = dpProcessParamsDao.findWeek0Report(startDate, endDate, clientCode);
		return dpWeek0VacantReportMapper.mapDomainToLinfoList(reports);
	}

}