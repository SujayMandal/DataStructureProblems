package com.fa.dp.business.filter.bo;

import com.fa.dp.business.command.dao.CommandDAO;
import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.command.info.CommandProcess;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.info.HubzuInfo;
import com.fa.dp.business.info.SSPMIInfo;
import com.fa.dp.business.pmi.entity.PmiInsuranceCompany;
import com.fa.dp.business.ssinvestor.bo.SpclServicingInvestorBO;
import com.fa.dp.business.ssinvestor.entity.SpclServicingInvestor;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.weekn.dao.DPProcessWeekNParamsDao;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.business.weekn.input.info.DPWeekNProcessStatusInfo;
import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.KeyValue;
import com.fa.dp.core.util.RAClientConstants;
import com.fa.dp.core.util.RAClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named
@Slf4j
public class DPProcessWeekNParamsBOImpl extends AbstractDelegate implements DPProcessWeekNParamsBO {

	@Value("${weekN.excluded.state}")
	private String[] excludedStates;

	@Inject
	private DPProcessWeekNParamsDao dpProcessWeekNParamsDao;

	@Inject
	private SpclServicingInvestorBO spclServicingInvestorBo;

	@Inject
	private CommandDAO commandDAO;

	@Inject
	private DPFileProcessBO dpFileProcessBO;

	@Override
	public DPProcessWeekNParam saveDPProcessWeekNParam(DPProcessWeekNParam dpProcessParam) {
		return dpProcessWeekNParamsDao.save(dpProcessParam);
	}

	@Override
	public DPProcessWeekNParamInfo saveDPProcessWeekNParamInfo(DPProcessWeekNParamInfo dpProcessParamInfo) {
		if (null != dpProcessParamInfo) {
			DPProcessWeekNParam entity = convert(dpProcessParamInfo, DPProcessWeekNParam.class);
			entity = dpProcessWeekNParamsDao.save(entity);
			dpProcessParamInfo = convert(entity, DPProcessWeekNParamInfo.class);
		}
		return dpProcessParamInfo;
	}

	@Override
	public List<DPProcessWeekNParam> saveDPProcessParams(List<DPProcessWeekNParam> dpProcessParam) {
		return dpProcessWeekNParamsDao.saveAll(dpProcessParam);
	}

	@Override
	public List<DPProcessWeekNParamInfo> saveDPProcessParamInfos(List<DPProcessWeekNParamInfo> dpProcessParamInfoList) {
		List<DPProcessWeekNParam> entityList = new ArrayList<>();
		for (DPProcessWeekNParamInfo dpProcessWeekNParamInfo : dpProcessParamInfoList) {
			DPWeekNProcessStatus dpWeeknPrcsStatus = convert(dpProcessWeekNParamInfo.getDpWeekNProcessStatus(), DPWeekNProcessStatus.class);
			DPProcessWeekNParam dpProcessWeekNParam = convert(dpProcessWeekNParamInfo, DPProcessWeekNParam.class);
			dpProcessWeekNParam.setDpWeekNProcessStatus(dpWeeknPrcsStatus);
			entityList.add(dpProcessWeekNParam);
		}
		entityList = dpProcessWeekNParamsDao.saveAll(entityList);
		List<DPProcessWeekNParamInfo> columnEntries = new ArrayList<>();
		for (DPProcessWeekNParam dpProcessWeekNParam : entityList) {
			DPWeekNProcessStatusInfo dpWeeknProcessStatusInfo = convert(dpProcessWeekNParam.getDpWeekNProcessStatus(),
					DPWeekNProcessStatusInfo.class);
			DPProcessWeekNParamInfo dInfo = convert(dpProcessWeekNParam, DPProcessWeekNParamInfo.class);
			dInfo.setDpWeekNProcessStatus(dpWeeknProcessStatusInfo);
			columnEntries.add(dInfo);
		}
		return columnEntries;
	}

	@Override
	public List<DPProcessWeekNParamInfo> fetchRecommendationList(DPProcessWeekNParamEntryInfo infoObject, String classification) {

		List<DPProcessWeekNParam> weekNParams = dpProcessWeekNParamsDao
				.findWeekNParamByDelioveryDate(DateConversionUtil.getCurrentUTCTime().getMillis(), DPProcessParamAttributes.ELIGIBLE.getValue(),
						classification);

		List<DPProcessWeekNParamInfo> infoList = new ArrayList<>();
		for (DPProcessWeekNParam dpProcessWeekNParam : weekNParams) {
			DPWeekNProcessStatusInfo dpWeeknProcessStatusInfo = convert(dpProcessWeekNParam.getDpWeekNProcessStatus(),
					DPWeekNProcessStatusInfo.class);
			DPProcessWeekNParamInfo dInfo = convert(dpProcessWeekNParam, DPProcessWeekNParamInfo.class);
			dInfo.setDpWeekNProcessStatus(dpWeeknProcessStatusInfo);
			infoList.add(dInfo);
		}
		return infoList;
	}

	@Override
	public List<DPProcessWeekNParamInfo> fetchExclusionList(DPProcessWeekNParamEntryInfo infoObject, String classification) {
		List<DPProcessWeekNParam> weekNParams = dpProcessWeekNParamsDao
				.findWeekNParamByDelioveryDate(DateConversionUtil.getCurrentUTCTime().getMillis(), DPProcessParamAttributes.INELIGIBLE.getValue(),
						classification);
		List<DPProcessWeekNParamInfo> infoList = new ArrayList<>();
		for (DPProcessWeekNParam dpProcessWeekNParam : weekNParams) {
			DPWeekNProcessStatusInfo dpWeeknProcessStatusInfo = convert(dpProcessWeekNParam.getDpWeekNProcessStatus(),
					DPWeekNProcessStatusInfo.class);
			DPProcessWeekNParamInfo dInfo = convert(dpProcessWeekNParam, DPProcessWeekNParamInfo.class);
			dInfo.setDpWeekNProcessStatus(dpWeeknProcessStatusInfo);
			infoList.add(dInfo);
		}
		return infoList;
	}

	@Override
	public List<DPProcessWeekNParamInfo> fetchPast12List(DPProcessWeekNParamEntryInfo infoObject, String classification) {
		List<DPProcessWeekNParam> weekNParams = dpProcessWeekNParamsDao.findPast12CycleList("End of 12 Listing Cycles", classification);
		List<DPProcessWeekNParamInfo> infoList = new ArrayList<>();
		for (DPProcessWeekNParam dpProcessWeekNParam : weekNParams) {
			DPWeekNProcessStatusInfo dpWeeknProcessStatusInfo = convert(dpProcessWeekNParam.getDpWeekNProcessStatus(),
					DPWeekNProcessStatusInfo.class);
			DPProcessWeekNParamInfo dInfo = convert(dpProcessWeekNParam, DPProcessWeekNParamInfo.class);
			dInfo.setDpWeekNProcessStatus(dpWeeknProcessStatusInfo);
			infoList.add(dInfo);
		}
		return infoList;
	}

	/*@Override
	public KeyValue<List<DPProcessWeekNParamInfo>, List<DPProcessWeekNParamInfo>> filterRecords(
			List<DPProcessWeekNParamInfo> columnEntries) {
		log.debug("Enter DPProcessWeekNParamsBOImpl :: method filterRecords");
		List<DPProcessWeekNParamInfo> filteredOutEntries = new ArrayList<>();
		List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();

		for (DPProcessWeekNParamInfo columnEntry : columnEntries) {
			if (null == columnEntry.getCommand()) {
				MDC.put(RAClientConstants.LOAN_NUMBER, columnEntry.getAssetNumber());
				// check for Hubzu db response from ss and pmi success/failure
				if (null != columnEntry.getSsPmiHubzuResponse()) {
					// TODO story required for hubzu call failure or Error handling
					// where to add in success or failure entries
					if (StringUtils.equalsIgnoreCase(columnEntry.getSsPmiHubzuResponse().getTransactionStatus(), FAIL)) {
						columnEntry.setAssignment(DPProcessFilterParams.ASSIGNMENT_ERROR.getValue());
						log.debug("Hubzu Call FAIL for ss privateMortgageInsurance query Need To handle");
					} else {
						HubzuInfo hubzuInfo = columnEntry.getSsPmiHubzuResponse().getHubzuInfos().get(0);
						// check for clntCodeVc in spclInvestorcode Table
						// TODO need to check for same loan number in ss investor db
						// as in hubzu data
						if (StringUtils.isBlank(hubzuInfo.getClntCodeVc())
								|| spclServicingInvestorBo.isInvestorCodeExist(hubzuInfo.getClntCodeVc())) {
							columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
							columnEntry.setExclusionReason(DPProcessFilterParams.SPECIAL_SERVICE.getValue());
							String process = null;
							if (DPProcessParamAttributes.OCN.getValue().equals(columnEntry.getClassification()))
								process = CommandProcess.WEEKN_OCN.getCommmandProcess();
							else if (DPProcessParamAttributes.NRZ.getValue().equals(columnEntry.getClassification()))
								process = CommandProcess.WEEKN_NRZ.getCommmandProcess();
							List<Command> command = commandDAO.findByProcess(process, DPAConstants.SS_AND_PMI);
							CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
							columnEntry.setCommand(commandInfo);
							filteredOutEntries.add(columnEntry);
						}
						// check for isSpclHndlPropVc
						else if (StringUtils.equalsIgnoreCase(hubzuInfo.getIsSpclHndlPropVc(), YES)) {
							columnEntry.setExclusionReason(DPProcessFilterParams.PMI.getValue());
							columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
							String process = null;
							if (DPProcessParamAttributes.OCN.getValue().equals(columnEntry.getClassification()))
								process = CommandProcess.WEEKN_OCN.getCommmandProcess();
							else if (DPProcessParamAttributes.NRZ.getValue().equals(columnEntry.getClassification()))
								process = CommandProcess.WEEKN_NRZ.getCommmandProcess();
							List<Command> command = commandDAO.findByProcess(process, DPAConstants.SS_AND_PMI);
							CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
							columnEntry.setCommand(commandInfo);
							filteredOutEntries.add(columnEntry);
						} else if (!filteredOutEntries.contains(columnEntry) && !successEntries.contains(columnEntry)) {
							successEntries.add(columnEntry);
						}
					}
				}
				MDC.remove(RAClientConstants.LOAN_NUMBER);
			}
		}
		log.debug("Exit DPProcessWeekNParamsBOImpl :: method filterRecords");
		return new KeyValue<>(successEntries, filteredOutEntries);

	}*/

	@Override
	public KeyValue<DPProcessWeekNParamEntryInfo, List<SSPMIInfo>> filterOnPMIFlag(DPProcessWeekNParamEntryInfo infoObject,
			List<SSPMIInfo> ssPmiInfos) {
		List<SSPMIInfo> successPmiInfos = new ArrayList<>();
		List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();
		List<DPProcessWeekNParamInfo> filteredOutEntries = new ArrayList<>();

		for (DPProcessWeekNParamInfo columnEntry : infoObject.getColumnEntries()) {
			if (null == columnEntry.getCommand()) {
				if (columnEntry.getPrivateMortgageInsurance().equals(RAClientConstants.YES)) {
					columnEntry.setExclusionReason(DPProcessFilterParams.PMI.getValue());
					columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					String process = null;
					if (DPProcessParamAttributes.OCN.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEKN_OCN.getCommmandProcess();
					else if (DPProcessParamAttributes.NRZ.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEKN_NRZ.getCommmandProcess();
					List<Command> command = commandDAO.findByProcess(process, DPAConstants.SS_AND_PMI);
					CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
					columnEntry.setCommand(commandInfo);
					columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					filteredOutEntries.add(columnEntry);
				} else {
					columnEntry.setPrivateMortgageInsurance(RAClientConstants.NO);
					successEntries.add(columnEntry);
				}
			}
		}
		if (!infoObject.isFetchProcess()) {
			// setting the successful records after filtering, back to input object
			infoObject.setColumnEntries(successEntries);

			// save ineligible records to DP_WEEKN_PARAMS table
			if (CollectionUtils.isNotEmpty(filteredOutEntries)) {
				log.info("Saving all ineligible AssetValue entries to db..");
				saveDPProcessParamInfos(filteredOutEntries);
			}
		}
		return new KeyValue<>(infoObject, successPmiInfos);
	}

	@Override
	public KeyValue<DPProcessWeekNParamEntryInfo, List<SSPMIInfo>> filterOnInscComp(DPProcessWeekNParamEntryInfo infoObject,
			List<SSPMIInfo> ssPmiInfos) {
		List<SSPMIInfo> successPmiInfos = new ArrayList<>();
		List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();
		List<DPProcessWeekNParamInfo> filteredOutEntries = new ArrayList<>();
		List<PmiInsuranceCompany> pmiInscCompanies = spclServicingInvestorBo.findPmiCompsByActiveTrue();
		List<String> pmiInscCompIds = convertToList(pmiInscCompanies, String.class);
		boolean found;
		for (DPProcessWeekNParamInfo columnEntry : infoObject.getColumnEntries()) {
			if (null == columnEntry.getCommand()) {
				found = false;
				for (SSPMIInfo pmiInfo : ssPmiInfos) {
					if (null != columnEntry.getOldAssetNumber() && columnEntry.getOldAssetNumber().equals(pmiInfo.getAssetNumber())) {
						found = true;
						if (pmiInscCompIds.contains(pmiInfo.getInsuranceId())) {
							columnEntry.setExclusionReason(DPProcessFilterParams.PMI.getValue());
							columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
							String process = null;
							if (DPProcessParamAttributes.OCN.getValue().equals(columnEntry.getClassification()))
								process = CommandProcess.WEEKN_OCN.getCommmandProcess();
							else if (DPProcessParamAttributes.NRZ.getValue().equals(columnEntry.getClassification()))
								process = CommandProcess.WEEKN_NRZ.getCommmandProcess();
							List<Command> command = commandDAO.findByProcess(process, DPAConstants.SS_AND_PMI);
							CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
							columnEntry.setCommand(commandInfo);
							columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
							columnEntry.setPrivateMortgageInsurance(pmiInfo.getInsuranceId());
							filteredOutEntries.add(columnEntry);
							break;
						} else {
							columnEntry.setPrivateMortgageInsurance(RAClientConstants.NO);
							successEntries.add(columnEntry);
							successPmiInfos.add(pmiInfo);
						}
					} else if (columnEntry.getAssetNumber().equals(pmiInfo.getAssetNumber())) {
						found = true;
						if (pmiInscCompIds.contains(pmiInfo.getInsuranceId())) {
							columnEntry.setExclusionReason(DPProcessFilterParams.PMI.getValue());
							columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
							String process = null;
							if (DPProcessParamAttributes.OCN.getValue().equals(columnEntry.getClassification()))
								process = CommandProcess.WEEKN_OCN.getCommmandProcess();
							else if (DPProcessParamAttributes.NRZ.getValue().equals(columnEntry.getClassification()))
								process = CommandProcess.WEEKN_NRZ.getCommmandProcess();
							List<Command> command = commandDAO.findByProcess(process, DPAConstants.SS_AND_PMI);
							CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
							columnEntry.setCommand(commandInfo);
							columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
							columnEntry.setPrivateMortgageInsurance(pmiInfo.getInsuranceId());
							filteredOutEntries.add(columnEntry);
							break;
						} else {
							columnEntry.setPrivateMortgageInsurance(RAClientConstants.NO);
							successEntries.add(columnEntry);
							successPmiInfos.add(pmiInfo);
						}
					}
				}
				if (!found) {
					successEntries.add(columnEntry);
					SSPMIInfo notFoundInfo = new SSPMIInfo();
					notFoundInfo.setAssetNumber(columnEntry.getAssetNumber());
					successPmiInfos.add(notFoundInfo);
				}
			}
		}
		if (!infoObject.isFetchProcess()) {
			// setting the successful records after filtering, back to input object
			infoObject.setColumnEntries(successEntries);

			// save ineligible records to DP_WEEKN_PARAMS table
			if (CollectionUtils.isNotEmpty(filteredOutEntries)) {
				log.info("Saving all ineligible AssetValue entries to db..");
				saveDPProcessParamInfos(filteredOutEntries);
			}
		}
		return new KeyValue<>(infoObject, successPmiInfos);
	}

	@Override
	public KeyValue<DPProcessWeekNParamEntryInfo, List<SSPMIInfo>> filterOnSpclServicing(DPProcessWeekNParamEntryInfo infoObject,
			List<SSPMIInfo> ssPmiInfos) {
		List<SSPMIInfo> successPmiInfos = new ArrayList<>();
		List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();
		List<DPProcessWeekNParamInfo> filteredOutEntries = new ArrayList<>();
		List<SpclServicingInvestor> aspsClients = spclServicingInvestorBo.findByActiveTrue();
		List<String> aspsClientIds = convertToList(aspsClients, String.class);
		boolean found;
		for (DPProcessWeekNParamInfo columnEntry : infoObject.getColumnEntries()) {
			if (null == columnEntry.getCommand()) {
				found = false;
				//in case of reprocess, consider user entered client code
				if (!StringUtils.isEmpty(columnEntry.getClientCode())) {
					if (aspsClientIds.contains(columnEntry.getClientCode())) {
						columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
						columnEntry.setExclusionReason(DPProcessFilterParams.SPECIAL_SERVICE.getValue());
						String process = null;
						if (DPProcessParamAttributes.OCN.getValue().equals(columnEntry.getClassification()))
							process = CommandProcess.WEEKN_OCN.getCommmandProcess();
						else if (DPProcessParamAttributes.NRZ.getValue().equals(columnEntry.getClassification()))
							process = CommandProcess.WEEKN_NRZ.getCommmandProcess();
						List<Command> command = commandDAO.findByProcess(process, DPAConstants.SS_AND_PMI);
						CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
						columnEntry.setCommand(commandInfo);
						columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
						filteredOutEntries.add(columnEntry);
						continue;
					}
				}
				for (SSPMIInfo pmiInfo : ssPmiInfos) {
					if (columnEntry.getAssetNumber().equals(pmiInfo.getAssetNumber())) {
						found = true;
						columnEntry.setClientCode(pmiInfo.getClientCode());
						if (StringUtils.isBlank(pmiInfo.getClientCode()) || aspsClientIds.contains(pmiInfo.getClientCode())) {
							columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
							columnEntry.setExclusionReason(DPProcessFilterParams.SPECIAL_SERVICE.getValue());
							String process = null;
							if (DPProcessParamAttributes.OCN.getValue().equals(columnEntry.getClassification()))
								process = CommandProcess.WEEKN_OCN.getCommmandProcess();
							else if (DPProcessParamAttributes.NRZ.getValue().equals(columnEntry.getClassification()))
								process = CommandProcess.WEEKN_NRZ.getCommmandProcess();
							List<Command> command = commandDAO.findByProcess(process, DPAConstants.SS_AND_PMI);
							CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
							columnEntry.setCommand(commandInfo);
							columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
							filteredOutEntries.add(columnEntry);
							break;
						} else {
							successEntries.add(columnEntry);
							successPmiInfos.add(pmiInfo);
							break;
						}
					}
				}
				if (!found) {
					List<Command> command = commandDAO.findByProcess(CommandProcess.WEEKN.getCommmandProcess(), DPAConstants.DATA_FETCH_FAILURE);
					CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
					columnEntry.setCommand(commandInfo);
					columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					columnEntry.setEligible(DPProcessFilterParams.ELIGIBLE.getValue());
					columnEntry.setExclusionReason(DPProcessFilterParams.SS_PMI_EXCLUSION.getValue());
					if (!infoObject.isFetchProcess())
						saveDPProcessWeekNParamInfo(columnEntry);
				}
			}
		}
		if (!infoObject.isFetchProcess()) {
			// setting the successful records after filtering, back to input object
			infoObject.setColumnEntries(successEntries);

			// save ineligible records to DP_WEEKN_PARAMS table
			if (CollectionUtils.isNotEmpty(filteredOutEntries)) {
				log.info("Saving all ineligible AssetValue entries to db..");
				saveDPProcessParamInfos(filteredOutEntries);
			}
		}
		return new KeyValue<>(infoObject, successPmiInfos);
	}

	@Override
	public List<DPProcessWeekNParam> findByAssetNumberAndClassification(String assetNumber, String classification) {
		return dpProcessWeekNParamsDao.findByAssetNumberAndClassificationOrderByLastModifiedDateDesc(assetNumber, classification);
	}

	@Override
	public KeyValue<List<DPProcessWeekNParamInfo>, List<DPProcessWeekNParamInfo>> filterRecordsOnZipCodeAndState(
			List<DPProcessWeekNParamInfo> columnEntries) {
		log.debug("Enter DPProcessWeekNParamsBOImpl :: method filterRecordsOnZipCodeAndState");

		List<DPProcessWeekNParamInfo> filteredOutEntries = new ArrayList<>();
		List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();

		for (DPProcessWeekNParamInfo columnEntry : columnEntries) {
			if (null == columnEntry.getCommand()) {
				MDC.put(RAClientConstants.LOAN_NUMBER, columnEntry.getAssetNumber());
				// check for state and zipcode
				if (Arrays.asList(excludedStates).contains(columnEntry.getState())) {
					columnEntry.setExclusionReason(DPProcessFilterParams.STATE_LAW.getValue().replace("#", "State :" + columnEntry.getState()));
					columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					String process = null;
					if (DPProcessParamAttributes.OCN.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEKN_OCN.getCommmandProcess();
					else if (DPProcessParamAttributes.NRZ.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEKN_NRZ.getCommmandProcess();
					else if (DPProcessParamAttributes.PHH.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEKN_PHH.getCommmandProcess();
					List<Command> command = commandDAO.findByProcess(process, DPAConstants.UNSUPPORTED_STATE_OR_ZIP);
					CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
					columnEntry.setCommand(commandInfo);
					columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					filteredOutEntries.add(columnEntry);
				} else if (!filteredOutEntries.contains(columnEntry) && !successEntries.contains(columnEntry)) {
					successEntries.add(columnEntry);
				}
				MDC.remove(RAClientConstants.LOAN_NUMBER);
			}
		}
		log.debug("Exit DPProcessWeekNParamsBOImpl :: method filterRecordsOnZipCodeAndState");
		return new KeyValue<>(successEntries, filteredOutEntries);

	}

	@Override
	public KeyValue<List<DPProcessWeekNParamInfo>, List<DPProcessWeekNParamInfo>> filterRecordsOnSop(List<DPProcessWeekNParamInfo> columnEntries) {
		log.debug("Enter DPProcessWeekNParamsBOImpl :: method filterRecordsOnSop");

		List<DPProcessWeekNParamInfo> filteredOutEntries = new ArrayList<>();
		List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();

		for (DPProcessWeekNParamInfo columnEntry : columnEntries) {
			if (null == columnEntry.getCommand()) {
				MDC.put(RAClientConstants.LOAN_NUMBER, columnEntry.getAssetNumber());

				// check for SOP_PROGRAM_STATUS column
				if (StringUtils.equalsIgnoreCase(columnEntry.getSellerOccupiedProperty(), RAClientConstants.YES)) {
					columnEntry.setExclusionReason(DPProcessFilterParams.SOP_EXCLUSION_REASON.getValue());
					columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					String process = null;
					if (DPProcessParamAttributes.OCN.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEKN_OCN.getCommmandProcess();
					else if (DPProcessParamAttributes.NRZ.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEKN_NRZ.getCommmandProcess();
					else if (DPProcessParamAttributes.PHH.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEKN_PHH.getCommmandProcess();
					List<Command> command = commandDAO.findByProcess(process, DPAConstants.SOP);
					CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
					columnEntry.setCommand(commandInfo);
					columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					filteredOutEntries.add(columnEntry);
				} else if (!filteredOutEntries.contains(columnEntry) && !successEntries.contains(columnEntry)) {
					successEntries.add(columnEntry);
				}
				MDC.remove(RAClientConstants.LOAN_NUMBER);
			}
		}
		log.debug("Exit DPProcessWeekNParamsBOImpl :: method filterRecordsOnSop");
		return new KeyValue<>(successEntries, filteredOutEntries);

	}

	@Override
	public List<DPProcessWeekNParam> findByDeliveryDate(String deliveryDate) {
		return dpProcessWeekNParamsDao.findByDeliveryDate(deliveryDate);
	}

	@Override
	public List<DPProcessWeekNParam> searchByAssetNumber(String assetNumber) {
		return dpProcessWeekNParamsDao.findByAssetNumberAndDeliveryDateNotNull(assetNumber);
	}

	@Override
	public List<DPProcessWeekNParam> searchByAssetDeliveryNull(String assetNumber) {
		return dpProcessWeekNParamsDao.findByAssetNumberAndDeliveryDateNullAndModelVersionNotNull(assetNumber);
	}

	@Override
	public KeyValue<List<DPProcessWeekNParamInfo>, List<DPProcessWeekNParamInfo>> filterRecordsOnAssignment(
			List<DPProcessWeekNParamInfo> columnEntries, boolean isFetchProcess) {
		log.debug("Enter DPProcessWeekNParamsBOImpl :: method filterRecordsOnAssignment");

		List<DPProcessWeekNParamInfo> filteredOutEntries = new ArrayList<>();
		List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();

		for (DPProcessWeekNParamInfo columnEntry : columnEntries) {
			if (null == columnEntry.getCommand()) {
				MDC.put(RAClientConstants.LOAN_NUMBER, columnEntry.getAssetNumber());

				List<DPProcessParam> dpProcessParams = dpFileProcessBO.findLatestNonDuplicateInWeek0ForGivenAsset(columnEntry.getAssetNumber());
				DPProcessParam dpProcessParam;
				DateTime assignmentDate;

				if (dpProcessParams.isEmpty()) {
					log.info("There is no Week0 entry for loan no: " + columnEntry.getAssetNumber());
					dpProcessParam = null;
				} else {
					dpProcessParam = dpProcessParams.get(0);
					log.info("Week0 entry for loan: " + columnEntry.getAssetNumber() + " Assignment : '" + dpProcessParam.getAssignment()
							+ "' Assignment Date : " + dpProcessParam.getAssignmentDate());
				}

				if (dpProcessParam != null && dpProcessParam.getAssignmentDate() != null) {
					DateTimeParser[] dateParsers = { DateTimeFormat.forPattern("MM/dd/yyyy").getParser(),
							DateTimeFormat.forPattern("M/d/yy").getParser(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").getParser() };
					DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(null, dateParsers).toFormatter();
					//					assignmentDate = formatter.parseDateTime(dpProcessParam.getAssignmentDate());
					assignmentDate = DateConversionUtil.getEstDate(dpProcessParam.getAssignmentDate());
					List<HubzuInfo> updatedHubzuInfoList = new ArrayList<>();
					for (HubzuInfo hubzuInfo : columnEntry.getHubzuDBResponse().getHubzuInfos()) {
						if (hubzuInfo.getCurrentListStrtDate() == null)
							continue;
						DateTime listStrtDt = DateConversionUtil.getEstDate(formatter.parseDateTime(hubzuInfo.getCurrentListStrtDate()).getMillis());
						if (listStrtDt.isBefore(assignmentDate))
							continue;
						else
							updatedHubzuInfoList.add(hubzuInfo);
					}
					columnEntry.getHubzuDBResponse().setHubzuInfos(updatedHubzuInfoList);
					//if all listings are before assignment date, put that columnEntry in assignment filter with a different exclusion reason
					if (updatedHubzuInfoList.isEmpty()) {
						columnEntry.setExclusionReason(DPProcessFilterParams.ASSIGNMENT_DATE_EXCLUSION.getValue());
						columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
						String process;
						if (columnEntry.getClassification().equals(DPProcessParamAttributes.OCN.getValue()))
							process = CommandProcess.WEEKN_OCN.getCommmandProcess();
						else if (columnEntry.getClassification().equals(DPProcessParamAttributes.NRZ.getValue()))
							process = CommandProcess.WEEKN_NRZ.getCommmandProcess();
						else
							process = CommandProcess.WEEKN_PHH.getCommmandProcess();

						List<Command> command = commandDAO.findByProcess(process, DPAConstants.ASSIGNMNT_FILTER);
						CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
						columnEntry.setCommand(commandInfo);
						columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
						filteredOutEntries.add(columnEntry);
						continue;
					}
				}

				if (columnEntry.getClassification().equals(DPProcessParamAttributes.OCN.getValue())) {
					if (dpProcessParam == null || StringUtils
							.equalsIgnoreCase(dpProcessParam.getAssignment(), DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue()) || !StringUtils
							.equalsIgnoreCase(dpProcessParam.getEligible(), DPProcessParamAttributes.ELIGIBLE.getValue())) {
						columnEntry.setExclusionReason(DPProcessFilterParams.WEEK_ZERO_NOT_RUN.getValue());
						columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
						String process = CommandProcess.WEEKN_OCN.getCommmandProcess();
						List<Command> command = commandDAO.findByProcess(process, DPAConstants.ASSIGNMNT_FILTER);
						CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
						columnEntry.setCommand(commandInfo);
						columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
						filteredOutEntries.add(columnEntry);
					} else if (StringUtils
							.equalsIgnoreCase(dpProcessParam.getAssignment(), DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue())) {
						columnEntry.setExclusionReason(DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue());
						columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
						String process = CommandProcess.WEEKN_OCN.getCommmandProcess();
						List<Command> command = commandDAO.findByProcess(process, DPAConstants.ASSIGNMNT_FILTER);
						CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
						columnEntry.setCommand(commandInfo);
						columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
						filteredOutEntries.add(columnEntry);
					} else if (StringUtils.equalsIgnoreCase(dpProcessParam.getAssignment(), DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue())) {
						successEntries.add(columnEntry);
					} else {
						//For any other assignment statuses
						log.debug("No Success or Failure condition Matches for given LoanNumber=" + columnEntry.getAssetNumber() + " have Assignment="
								+ dpProcessParam.getAssignment() + " and classification=" + dpProcessParam.getClassification());
						columnEntry.setExclusionReason(DPProcessFilterParams.WEEK_ZERO_NOT_RUN.getValue());
						columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
						String process = CommandProcess.WEEKN_OCN.getCommmandProcess();
						List<Command> command = commandDAO.findByProcess(process, DPAConstants.ASSIGNMNT_FILTER);
						CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
						columnEntry.setCommand(commandInfo);
						columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
						filteredOutEntries.add(columnEntry);
					}
				} else if (columnEntry.getClassification().equals(DPProcessParamAttributes.PHH.getValue())) {
					if (dpProcessParam == null || StringUtils
							.equalsIgnoreCase(dpProcessParam.getAssignment(), DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue()) || !StringUtils
							.equalsIgnoreCase(dpProcessParam.getEligible(), DPProcessParamAttributes.ELIGIBLE.getValue())) {
						columnEntry.setExclusionReason(DPProcessFilterParams.WEEK_ZERO_NOT_RUN.getValue());
						columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
						String process = CommandProcess.WEEKN_PHH.getCommmandProcess();
						List<Command> command = commandDAO.findByProcess(process, DPAConstants.ASSIGNMNT_FILTER);
						CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
						columnEntry.setCommand(commandInfo);
						columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
						filteredOutEntries.add(columnEntry);
					} else if (StringUtils
							.equalsIgnoreCase(dpProcessParam.getAssignment(), DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue())) {
						columnEntry.setExclusionReason(DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue());
						columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
						String process = CommandProcess.WEEKN_PHH.getCommmandProcess();
						List<Command> command = commandDAO.findByProcess(process, DPAConstants.ASSIGNMNT_FILTER);
						CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
						columnEntry.setCommand(commandInfo);
						columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
						filteredOutEntries.add(columnEntry);
					} else if (StringUtils.equalsIgnoreCase(dpProcessParam.getAssignment(), DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue())) {
						successEntries.add(columnEntry);
					} else {
						//For any other assignment statuses
						log.debug("No Success or Failure condition Matches for given LoanNumber=" + columnEntry.getAssetNumber() + " have Assignment="
								+ dpProcessParam.getAssignment() + " and classification=" + dpProcessParam.getClassification());
						columnEntry.setExclusionReason(DPProcessFilterParams.WEEK_ZERO_NOT_RUN.getValue());
						columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
						String process = CommandProcess.WEEKN_PHH.getCommmandProcess();
						List<Command> command = commandDAO.findByProcess(process, DPAConstants.ASSIGNMNT_FILTER);
						CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
						columnEntry.setCommand(commandInfo);
						columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
						filteredOutEntries.add(columnEntry);
					}
				} else if (columnEntry.getClassification().equals(DPProcessParamAttributes.NRZ.getValue())) {
					successEntries.add(columnEntry);
				}
			}
			MDC.remove(RAClientConstants.LOAN_NUMBER);
		}
		log.debug("Exit DPProcessWeekNParamsBOImpl :: method filterRecordsOnAssignment");
		return new KeyValue<>(successEntries, filteredOutEntries);
	}

	/**
	 * Filter PMI property as per the story #328
	 *
	 * @param infoObject
	 * @return DPProcessWeekNParamEntryInfo
	 */
	@Override
	public DPProcessWeekNParamEntryInfo filterOnNewPMIFlag(DPProcessWeekNParamEntryInfo infoObject) throws SystemException {
		List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();
		List<DPProcessWeekNParamInfo> filteredOutEntries = new ArrayList<>();

		for (DPProcessWeekNParamInfo columnEntry : infoObject.getColumnEntries()) {
			if (null == columnEntry.getCommand()) {
				if (columnEntry.getPrivateMortgageInsurance().equals(RAClientConstants.YES)) {
					columnEntry.setExclusionReason(DPProcessFilterParams.PMI.getValue());
					columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					String process = null;
					if (DPProcessParamAttributes.OCN.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEKN_OCN.getCommmandProcess();
					else if (DPProcessParamAttributes.NRZ.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEKN_NRZ.getCommmandProcess();
					else if (DPProcessParamAttributes.PHH.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEKN_PHH.getCommmandProcess();
					List<Command> command = commandDAO.findByProcess(process, DPAConstants.SS_AND_PMI);
					CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
					columnEntry.setCommand(commandInfo);
					columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					filteredOutEntries.add(columnEntry);
				} else {
					successEntries.add(columnEntry);

				}
			}
		}
		if (!infoObject.isFetchProcess()) {
			// setting the successful records after filtering, back to input object
			infoObject.setColumnEntries(successEntries);

			// save ineligible records to DP_WEEKN_PARAMS table
			if (CollectionUtils.isNotEmpty(filteredOutEntries)) {
				log.info("Saving all ineligible AssetValue entries to db..");
				saveDPProcessParamInfos(filteredOutEntries);
			}
		}
		return infoObject;
	}

	@Override
	public DPProcessWeekNParamEntryInfo filterOnZeroPMIFlag(DPProcessWeekNParamEntryInfo infoObject) throws SystemException {
		List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();
		List<DPProcessWeekNParamInfo> filteredOutEntries = new ArrayList<>();

		List<PmiInsuranceCompany> pmiInscCompanies = spclServicingInvestorBo.findPmiCompsByActiveTrue();
		List<String> pmiInscCompIds = convertToList(pmiInscCompanies, String.class);
		log.debug("filterOnZeroPMIFlag() pmiInscCompIds - > {}", pmiInscCompIds.toString());

		for (DPProcessWeekNParamInfo columnEntry : infoObject.getColumnEntries()) {
			if (null == columnEntry.getCommand()) {
				log.debug("Check for private mortgage insurance : {} and pmi company insurance id : {}", columnEntry.getPrivateMortgageInsurance(),
						columnEntry.getPmiCompanyInsuranceId());
				if (StringUtils.equals(columnEntry.getPrivateMortgageInsurance(), RAClientConstants.NO) && pmiInscCompIds
						.contains(columnEntry.getPmiCompanyInsuranceId())) {
					columnEntry.setExclusionReason(DPProcessFilterParams.PMI.getValue());
					columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					String process = null;
					if (DPProcessParamAttributes.OCN.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEKN_OCN.getCommmandProcess();
					else if (DPProcessParamAttributes.NRZ.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEKN_NRZ.getCommmandProcess();
					else if (DPProcessParamAttributes.PHH.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEKN_PHH.getCommmandProcess();
					List<Command> command = commandDAO.findByProcess(process, DPAConstants.SS_AND_PMI);
					CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
					columnEntry.setCommand(commandInfo);
					columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					filteredOutEntries.add(columnEntry);
				} else {
					successEntries.add(columnEntry);
				}
			}
		}

		log.debug("filterOnZeroPMIFlag() successEntries size : {}, filteredOutEntries size : {} - > ", successEntries.size(),
				filteredOutEntries.size());

		if (!infoObject.isFetchProcess()) {
			// setting the successful records after filtering, back to input object
			infoObject.setColumnEntries(successEntries);

			// save ineligible records to DP_WEEKN_PARAMS table
			if (CollectionUtils.isNotEmpty(filteredOutEntries)) {
				log.info("Saving all ineligible AssetValue entries to db..");
				saveDPProcessParamInfos(filteredOutEntries);
			}
		}
		return infoObject;
	}

	/**
	 * Filter SS property as per the story #328
	 *
	 * @param infoObject
	 * @return DPProcessWeekNParamEntryInfo
	 */
	@Override
	public DPProcessWeekNParamEntryInfo filterOnNewSpclServicing(DPProcessWeekNParamEntryInfo infoObject) {
		List<DPProcessWeekNParamInfo> successEntries = new ArrayList<>();
		List<DPProcessWeekNParamInfo> filteredOutEntries = new ArrayList<>();
		for (DPProcessWeekNParamInfo columnEntry : infoObject.getColumnEntries()) {
			if (null == columnEntry.getCommand()) {
				if (columnEntry.getSpecialServicingFlag().equalsIgnoreCase(RAClientConstants.YES)) {
					columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					columnEntry.setExclusionReason(DPProcessFilterParams.SPECIAL_SERVICE.getValue());
					String process = null;
					if (DPProcessParamAttributes.OCN.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEKN_OCN.getCommmandProcess();
					else if (DPProcessParamAttributes.NRZ.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEKN_NRZ.getCommmandProcess();
					else if (DPProcessParamAttributes.PHH.getValue().equals(columnEntry.getClassification()))
						process = CommandProcess.WEEKN_PHH.getCommmandProcess();
					List<Command> command = commandDAO.findByProcess(process, DPAConstants.SS_AND_PMI);
					CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
					columnEntry.setCommand(commandInfo);
					columnEntry.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
					filteredOutEntries.add(columnEntry);
				} else
					successEntries.add(columnEntry);

			}
		}
		if (!infoObject.isFetchProcess()) {
			// setting the successful records after filtering, back to input object
			infoObject.setColumnEntries(successEntries);

			// save ineligible records to DP_WEEKN_PARAMS table
			if (CollectionUtils.isNotEmpty(filteredOutEntries)) {
				log.info("Saving all ineligible AssetValue entries to db..");
				saveDPProcessParamInfos(filteredOutEntries);
			}
		}
		return infoObject;
	}

	@Override
	public DPProcessWeekNParamInfo checkReduction(String selrPropId, String oldLoanNumber, LocalDate currentListEndDate) {
		log.debug("selrPropId : {}, oldLoanNumber : {}, list end date : {}", selrPropId, oldLoanNumber, currentListEndDate);
		DPProcessWeekNParamInfo weeknParamInfo = null;

		//long start = previousListEndDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;
		long end = currentListEndDate.atStartOfDay(ZoneId.of(RAClientUtil.EST_TIME_ZONE)).toEpochSecond() * 1000;
		//end += 86399999L;
		DPProcessWeekNParam data = null;
		try {
			List<DPProcessWeekNParam> dataList = dpProcessWeekNParamsDao.findByRbidPropIdAndDeliveryDate(selrPropId, oldLoanNumber, end);
			if(CollectionUtils.isNotEmpty(dataList)) {
				if(dataList.size() > 1) {
					data = dataList.stream().filter(d->d.getCommand() == null).collect(Collectors.toList()).get(0);
				}
				if(data == null) {
					data = dataList.get(0);
				}
			}
			weeknParamInfo = convert(data, DPProcessWeekNParamInfo.class);
		} catch (Exception e) {
			log.error("Problem in fetching data. {}", e);
		}

		//DPProcessWeekNParam data = dpProcessWeekNParamsDao.findByRbidPropIdAndDeliveryDate(rbidPropIdVcNn, previousListEndDate, currentListStrtDate);
		return weeknParamInfo;
	}

	@Override
	public String getLatestWekNRunDate() {
		DPProcessWeekNParam data = dpProcessWeekNParamsDao.findFirstByOrderByLastModifiedDateDesc();
		return data != null ? DateConversionUtil.EST_DATE_TIME_FORMATTER.print(data.getLastModifiedDate()) : null;
	}

	@Override
	public void filterQAReportAssignment(List<DPProcessWeekNParamInfo> columnEntries) {

		log.info("filterQAReportAssignment() started.");

		Map<String, DPProcessParam> dpProcessParamsMap = dpFileProcessBO.findLatestNonDuplicateInWeek0ForGivenAssetList(columnEntries.stream().map(a->a.getOldAssetNumber()).collect(Collectors.toList()));

		for (DPProcessWeekNParamInfo columnEntry : columnEntries) {
			MDC.put(RAClientConstants.LOAN_NUMBER, columnEntry.getAssetNumber());

			//List<DPProcessParam> dpProcessParams = dpFileProcessBO.findLatestNonDuplicateInWeek0ForGivenAssetList(columnEntry.getOldAssetNumber());
			DPProcessParam dpProcessParam = dpProcessParamsMap.get(columnEntry.getOldAssetNumber());;
			DateTime assignmentDate;

			if (dpProcessParam == null) {
				log.info("There is no Week0 entry for loan no: " + columnEntry.getAssetNumber());
			} else {
				log.info("Week0 entry for loan: " + columnEntry.getAssetNumber() + " Assignment : '" + dpProcessParam.getAssignment()
						+ "' Assignment Date : " + dpProcessParam.getAssignmentDate());
			}

			if (dpProcessParam != null && dpProcessParam.getAssignmentDate() != null) {
				DateTimeParser[] dateParsers = { DateTimeFormat.forPattern("MM/dd/yyyy").getParser(), DateTimeFormat.forPattern("M/d/yy").getParser(),
						DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").getParser() };
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append(null, dateParsers).toFormatter();
				//					assignmentDate = formatter.parseDateTime(dpProcessParam.getAssignmentDate());
				assignmentDate = DateConversionUtil.getEstDate(dpProcessParam.getAssignmentDate());
				List<HubzuInfo> updatedHubzuInfoList = new ArrayList<>();
				for (HubzuInfo hubzuInfo : columnEntry.getHubzuDBResponse().getHubzuInfos()) {
					if (StringUtils.isBlank(hubzuInfo.getCurrentListStrtDate())) {
						continue;
					}
					DateTime listStrtDt = DateConversionUtil.getEstDate(formatter.parseDateTime(hubzuInfo.getCurrentListStrtDate()).getMillis());
					if (listStrtDt.isBefore(assignmentDate)) {
						continue;
					} else {
						updatedHubzuInfoList.add(hubzuInfo);
					}
				}
				columnEntry.getHubzuDBResponse().setHubzuInfos(updatedHubzuInfoList);
				//if all listings are before assignment date, put that columnEntry in assignment filter with a different exclusion reason
				if (updatedHubzuInfoList.isEmpty()) {
					columnEntry.setExclusionReason(DPProcessFilterParams.ASSIGNMENT_DATE_EXCLUSION.getValue());
					columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
					continue;
				}
			}

			if (StringUtils
					.equalsAny(columnEntry.getClassification(), DPProcessParamAttributes.OCN.getValue(), DPProcessParamAttributes.PHH.getValue())) {
				if (dpProcessParam == null || StringUtils
						.equalsIgnoreCase(dpProcessParam.getAssignment(), DPProcessParamAttributes.ERROR_ASSIGNMENT.getValue())) {
					columnEntry.setExclusionReason(DPProcessFilterParams.WEEK_ZERO_NOT_RUN.getValue());
					columnEntry.setEligible(DPProcessParamAttributes.INELIGIBLE.getValue());
				} else if (StringUtils.equalsIgnoreCase(dpProcessParam.getAssignment(), DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue())) {
					columnEntry.setExclusionReason(DPProcessParamAttributes.BENCHMARK_ASSIGNMENT.getValue());
				} else if (StringUtils.equalsIgnoreCase(dpProcessParam.getAssignment(), DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue())) {
				} else {
					//For any other assignment statuses
					log.debug("No Success or Failure condition Matches for given LoanNumber=" + columnEntry.getAssetNumber() + " have Assignment="
							+ dpProcessParam.getAssignment() + " and classification=" + dpProcessParam.getClassification());
					columnEntry.setExclusionReason(DPProcessFilterParams.WEEK_ZERO_NOT_RUN.getValue());
				}
			}
		}
		MDC.remove(RAClientConstants.LOAN_NUMBER);
	}

	@Override
	public void filterQAReportState(List<DPProcessWeekNParamInfo> columnEntries) {
		for (DPProcessWeekNParamInfo columnEntry : columnEntries) {
			MDC.put(RAClientConstants.LOAN_NUMBER, columnEntry.getAssetNumber());
			// check for state
			if (Arrays.asList(excludedStates).contains(columnEntry.getState())) {
				columnEntry.setExclusionReason(DPProcessFilterParams.STATE_LAW.getValue().replace("#", "State :" + columnEntry.getState()));
			}
			MDC.remove(RAClientConstants.LOAN_NUMBER);
		}
	}

	@Override
	public void filterQAReportSSPmiFlag(DPProcessWeekNParamEntryInfo infoObject) {
		List<PmiInsuranceCompany> pmiInscCompanies = spclServicingInvestorBo.findPmiInsCompsByActiveTrue();
		List<String> pmiInscCompIds = pmiInscCompanies.stream().map(a -> new String[] { a.getInsuranceCompany(), a.getCompanyCode() })
				.flatMap(a -> Arrays.stream(a)).distinct().collect(Collectors.toList());
		log.info("filterOnZeroPMIFlag() pmiInscCompIds - > {}", pmiInscCompIds.toString());
		//dpProcessWeekNFilterDelegate.filterSSPmi(infoObject);
		for (DPProcessWeekNParamInfo columnEntry : infoObject.getColumnEntries()) {
			if (StringUtils.equals(columnEntry.getPrivateMortgageInsurance(), RAClientConstants.YES)) {
				columnEntry.setExclusionReason(DPProcessFilterParams.PMI.getValue());
			} else if (StringUtils.equals(columnEntry.getPrivateMortgageInsurance(), RAClientConstants.NO) && pmiInscCompIds
					.contains(columnEntry.getPmiCompanyInsuranceId())) {
				columnEntry.setExclusionReason(DPProcessFilterParams.PMI.getValue());
			}
			if (StringUtils.equalsIgnoreCase(columnEntry.getSpecialServicingFlag(), RAClientConstants.YES)) {
				columnEntry.setExclusionReason(DPProcessFilterParams.SPECIAL_SERVICE.getValue());
			}
		}
	}

}