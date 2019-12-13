package com.fa.dp.business.week0.delegate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import com.fa.dp.business.sop.week0.input.mapper.DPSopWeek0ParamMapper;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.util.ObjectUtils;

import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.bo.DPProcessParamsBO;
import com.fa.dp.business.filter.bo.DPProcessWeekNParamsBO;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.rr.migration.RRMigration;
import com.fa.dp.business.sop.week0.bo.DPSopProcessBO;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.week0.input.mapper.DPSOPWeek0DashboardMapper;
import com.fa.dp.business.sop.weekN.bo.DPSopWeekNParamBO;
import com.fa.dp.business.sop.weekN.entity.DPSopWeekNParam;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.sop.weekN.mapper.DPSOPWeekNDashboardMapper;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.bo.DPDashboardParamInfo;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.info.DPWeek0DashboardMapper;
import com.fa.dp.business.week0.info.DPWeek0ToInfoMapper;
import com.fa.dp.business.week0.info.DashboardFilterInfo;
import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;
import com.fa.dp.business.weekn.input.info.DPAssetDetails;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.business.weekn.input.info.DPWeekNDashboardMapper;
import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.util.DateConversionUtil;

@Slf4j
@Named
public class DPFileProcessDelegateImpl extends AbstractDelegate implements DPFileProcessDelegate {
	
	private static final String SOP_WEEK_0 = "SOP Week 0"; 
	private static final String WEEK_0 = "Week 0"; 

	@Inject
	private RRMigration rRMigration;

	@Inject
	private DPFileProcessBO dpFileProcessBO;
	
	@Inject
	private DPSopProcessBO dpSopProcessBO;
	
	@Inject
	private DPSopWeekNParamBO dpSopWeekNParamBO;

	@Inject
	private DPWeek0DashboardMapper dpDashboardMapper;

	@Inject
	private DPWeekNDashboardMapper dpWeekNDashboardMapper;
	
	@Inject
	private DPSOPWeek0DashboardMapper dpSOPWeek0DashboardMapper;
	
	@Inject
	private DPSOPWeekNDashboardMapper dpSOPWeekNDashboardMapper;

	@Inject
	private DPProcessParamsBO dpProcessParamsBo;

	@Inject
	private DPProcessWeekNParamsBO dpProcessWeekNParamsBO;

	@Inject
	private DPWeek0ToInfoMapper week0ToInfoMapper;

	@Inject
	private DPSopProcessBO dpSopWeek0Param;

	@Inject
	private DPSopWeek0ParamMapper dpSopWeek0ParamMapper;

	/**
	 * @return List<DPFileProcessStatusInfo>
	 * @throws SystemException
	 */
	@Override
	public List<DPDashboardParamInfo> getDashboardDetails(String fileType) throws SystemException {
		List<DPDashboardParamInfo> listOfDPDashboardParamInfo = new ArrayList<>();
		List<DPDashboardParamInfo> listOfDashboardParams = new ArrayList<>();
		Long startTime;
		/* To get details for Week Zero tables */
		if (fileType.equalsIgnoreCase(DPAConstants.WEEK0)) {
			log.info("Preparing Dashboard with Week 0 details");
			startTime = DateTime.now().getMillis();
			List<DPProcessParamInfo> listOfDPProcessParamsInfo = dpFileProcessBO.getWeekZeroAllUploadedFiles();
			log.info("Time taken for Week 0 DB call for dashboard : " + (DateTime.now().getMillis() - startTime) + "ms");
			Map map = listOfDPProcessParamsInfo.stream()
					.collect(Collectors.groupingBy(item -> item.getDynamicPricingFilePrcsStatus().getId(), Collectors.counting()));
			listOfDPProcessParamsInfo.stream().forEach(week0Params -> {
				final DPDashboardParamInfo dpDashboardParamInfo = dpDashboardMapper.dpWeekZeroToDashboardInfoMapper(week0Params);
				listOfDPDashboardParamInfo.add(dpDashboardParamInfo);
			});
			startTime = DateTime.now().getMillis();
			listOfDashboardParams = dpFileProcessBO.getDashboardParams(listOfDPDashboardParamInfo, map);
			log.info("Time taken for Week 0 dashboard Params Object Creation: " + (DateTime.now().getMillis() - startTime) + "ms");
			if (listOfDashboardParams.isEmpty()) {
				log.error("Unable create dashboard for Week 0! - listOfDashboardParams empty");
				throw new SystemException(CoreExceptionCodes.DPWKN0101, new Object[] {});
			}
		}
		/* To get details for Week N tables */
		if (fileType.equalsIgnoreCase(DPAConstants.WEEKN)) {
			log.info("Preparing Dashboard with Week N data");
			startTime = DateTime.now().getMillis();
			List<DPProcessWeekNParamInfo> listOfDPProcessParamsInfo = dpFileProcessBO.getWeekNData();
			log.info("Time taken for Week N DB call for dashboard : " + (DateTime.now().getMillis() - startTime) + "ms");
			Map map = listOfDPProcessParamsInfo.stream()
					.collect(Collectors.groupingBy(item -> item.getDpWeekNProcessStatus().getId(), Collectors.counting()));
			listOfDPProcessParamsInfo.stream().forEach(weekNParams -> {
				final DPDashboardParamInfo dpDashboardParamInfo = dpWeekNDashboardMapper.dpWeekNToDashboardInfoMapper(weekNParams);
				listOfDPDashboardParamInfo.add(dpDashboardParamInfo);
			});
			startTime = DateTime.now().getMillis();
			listOfDashboardParams = dpFileProcessBO.getDashboardParams(listOfDPDashboardParamInfo, map);

			log.info("Time taken for Week N dashboard Params Object Creation: " + (DateTime.now().getMillis() - startTime) + "ms");
			if (listOfDashboardParams.isEmpty()) {
				log.error("Unable create dashboard for Week N! !");
				throw new SystemException(CoreExceptionCodes.DPWKN0102, new Object[] {});
			}
		}
		/* To get details for SOP Week 0 tables */
		if (fileType.equalsIgnoreCase(DPAConstants.SOP_WEEK0)) {
			log.info("Preparing Dashboard with SOP Week 0 data");
			startTime = DateTime.now().getMillis();
			List<DPSopWeek0ParamInfo> listOfSOPWeek0ProcessParamsInfo = dpSopProcessBO.getSOPWeek0Data();
			log.info("Time taken for SOP Week 0 DB call for dashboard : " + (DateTime.now().getMillis() - startTime) + "ms");
			Map map = listOfSOPWeek0ProcessParamsInfo.stream()
					.collect(Collectors.groupingBy(item -> item.getSopWeek0ProcessStatus().getId(), Collectors.counting()));
			listOfSOPWeek0ProcessParamsInfo.stream().forEach(sopWeek0Params -> {
				final DPDashboardParamInfo dpDashboardParamInfo = dpSOPWeek0DashboardMapper.dpSOPWeek0DashboardInfoMapper(sopWeek0Params);
				listOfDPDashboardParamInfo.add(dpDashboardParamInfo);
			});
			startTime = DateTime.now().getMillis();
			listOfDashboardParams = dpFileProcessBO.getDashboardParams(listOfDPDashboardParamInfo, map);
			
			log.info("Time taken for SOP Week 0 dashboard Params Object Creation: " + (DateTime.now().getMillis() - startTime) + "ms");
			if (listOfDashboardParams.isEmpty()) {
				log.error("Unable create dashboard for SOP Week 0! !");
				throw new SystemException(CoreExceptionCodes.DPSOPWK0011, new Object[] {});
			}
		}
		/* To get details for SOP Week N tables */
		if (fileType.equalsIgnoreCase(DPAConstants.SOP_WEEKN)) {
			log.info("Preparing Dashboard with SOP Week N data");
			startTime = DateTime.now().getMillis();
			List<DPSopWeekNParamInfo> listOfSOPWeekNProcessParamsInfo = dpSopWeekNParamBO.getSOPWeekNData();
			log.info("Time taken for SOP Week N DB call for dashboard : " + (DateTime.now().getMillis() - startTime) + "ms");
			Map map = listOfSOPWeekNProcessParamsInfo.stream()
					.collect(Collectors.groupingBy(item -> item.getSopWeekNProcessStatus().getId(), Collectors.counting()));
			listOfSOPWeekNProcessParamsInfo.stream().forEach(sopWeekNParams -> {
				final DPDashboardParamInfo dpDashboardParamInfo = dpSOPWeekNDashboardMapper.dpSOPWeekNToDashboardInfoMapper(sopWeekNParams);
				listOfDPDashboardParamInfo.add(dpDashboardParamInfo);
			});
			startTime = DateTime.now().getMillis();
			listOfDashboardParams = dpFileProcessBO.getDashboardParams(listOfDPDashboardParamInfo, map);
			
			log.info("Time taken for SOP Week N dashboard Params Object Creation: " + (DateTime.now().getMillis() - startTime) + "ms");
			if (listOfDashboardParams.isEmpty()) {
				log.error("Unable create dashboard for SOP Week N! !");
				throw new SystemException(CoreExceptionCodes.DPSOPWKN005, new Object[] {});
			}
		}

		listOfDashboardParams = listOfDashboardParams.stream()
				.sorted(Comparator.comparing(DPDashboardParamInfo::getUploadTimeStampInMillis).reversed()).collect(Collectors.toList());

		//        log.info("Sorted List based on getUploadTimeStampInMillis "+listOfDashboardParams.toString());
		return listOfDashboardParams;
	}

    /*@Override
    public List<DPProcessParam> fetchFilesDetailsFromDB(String fileType) throws SystemException {
        List<DPProcessParam> listOfDPProcessParams = new ArrayList<>();
        if (fileType.equalsIgnoreCase(DPAConstants.WEEK0)) {
            listOfDPProcessParams = dpFileProcessBO.fetchFilesDetails();
            if (listOfDPProcessParams.isEmpty()) {
                throw new SystemException(CoreExceptionCodes.DP020);
            }
        }
        return listOfDPProcessParams;
    }*/

	@Override
	public List<DPProcessParam> fetchFilesDetailsById(String fileId, String fileType) throws SystemException {
		List<DPProcessParam> listOfDPProcessParams = new ArrayList<>();
		if (fileType.equalsIgnoreCase(DPAConstants.WEEK0)) {
			listOfDPProcessParams = dpFileProcessBO.findDPProcessParamByProcessID(fileId);
			if (listOfDPProcessParams.isEmpty()) {
				throw new SystemException(CoreExceptionCodes.DP020);
			}
		}
		return listOfDPProcessParams;
	}

	private List<DPProcessWeekNParam> fetchWeekNFilesDetailsById(String Id) throws SystemException {
		List<DPProcessWeekNParam> listOfDPProcessWeekNParam = new ArrayList<>();
		listOfDPProcessWeekNParam = dpFileProcessBO.findDPProcessWeekNParamByProcessID(Id);
		if (listOfDPProcessWeekNParam.isEmpty()) {
			throw new SystemException(CoreExceptionCodes.DP020);
		}
		return listOfDPProcessWeekNParam;
	}

	@Override
	public List<DPProcessWeekNParamInfo> getWeekNAssetDetails(String weekNId, String weekType) throws SystemException {
		return dpFileProcessBO.getWeekNAssetDetails(weekNId, weekType);
	}

	@Override
	public List<DPProcessParamInfo> getAssetDetails(String fileId, String type) throws SystemException {
		return dpFileProcessBO.getAssetDetails(fileId, type);
	}

	@Override
	public String setStatus(DPProcessParamEntryInfo infoObject) throws SystemException {
		List<Command> failedStepCommands = dpFileProcessBO.findfailedStepCommands(infoObject.getDPFileProcessStatusInfo().getId());
		if (failedStepCommands.isEmpty() || failedStepCommands.size() == 0) {
			return DPFileProcessStatus.SUCCESSFUL.getFileStatus();
		} else {
			List<DPProcessParamInfo> dpInfos = dpFileProcessBO.getAssetDetails(infoObject.getDPFileProcessStatusInfo().getId(), DPAConstants.WEEK0);
			if (failedStepCommands.size() == dpInfos.size()) {
				return DPFileProcessStatus.FAILED.getFileStatus();
			} else {
				return DPFileProcessStatus.PARTIAL.getFileStatus();
			}
		}
	}

	@Override
	public String setWeeknStatus(DPProcessWeekNParamEntryInfo dpProcessWeekNParamEntryInfo) throws SystemException {
		List<Command> failedStepCommands = dpFileProcessBO
				.findFailedStepCommandsWeekn(dpProcessWeekNParamEntryInfo.getDpWeeknProcessStatus().getId());
		if (failedStepCommands.isEmpty() || failedStepCommands.size() == 0) {
			return DPFileProcessStatus.SUCCESSFUL.getFileStatus();
		} else {
			List<DPProcessWeekNParamInfo> dpInfos = getWeekNAssetDetails(dpProcessWeekNParamEntryInfo.getDpWeeknProcessStatus().getId(),
					DPAConstants.WEEKN);
			if (failedStepCommands.size() == dpInfos.size()) {
				return DPFileProcessStatus.FAILED.getFileStatus();
			} else {
				return DPFileProcessStatus.PARTIAL.getFileStatus();
			}
		}
	}

	@Override
	public void downloadReports(String fileId, String type, HttpServletResponse httpResponse) throws SystemException {
		List<DPProcessParam> listOfDPProcessParamOCN = new ArrayList<>();
		List<DPProcessParam> listOfDPProcessParamPHH = new ArrayList<>();
		List<DPProcessParam> listOfDPProcessParamNRZ = new ArrayList<>();
		String zipFileName = "";
		List<DPProcessParam> listOfDPProcessParam = fetchFilesDetailsById(fileId, type);

		//DynamicPricingFilePrcsStatus filePrcsStatus = dpFileProcessBO.findDPProcessStatusById(fileId);
		if (CollectionUtils.isNotEmpty(listOfDPProcessParam) && !ObjectUtils.isEmpty(listOfDPProcessParam.get(0).getDynamicPricingFilePrcsStatus())) {
			zipFileName += listOfDPProcessParam.get(0).getDynamicPricingFilePrcsStatus().getSysGnrtdInputFileName();
		} else{
			log.error("Week 0 details are not available for requested fileId - {}" , fileId);
			throw new SystemException(CoreExceptionCodes.DPA0004, fileId);
		}

		listOfDPProcessParam.stream().forEach(info -> {
			log.debug("adding process param for classification : {}, asset number : {}", info.getClassification(), info.getAssetNumber());
			if (StringUtils.equalsIgnoreCase(info.getClassification(), DPProcessParamAttributes.OCN.getValue())) {
				listOfDPProcessParamOCN.add(info);
			}
			if (StringUtils.equalsIgnoreCase(info.getClassification(), DPProcessParamAttributes.PHH.getValue())) {
				listOfDPProcessParamPHH.add(info);
			}
			if (StringUtils.equalsIgnoreCase(info.getClassification(), DPProcessParamAttributes.NRZ.getValue())) {
				listOfDPProcessParamNRZ.add(info);
			}
		});

		log.debug("OCN list count : {}, PHH list count : {}, NRZ list count : {}", listOfDPProcessParamOCN.size(), listOfDPProcessParamPHH.size(),
				listOfDPProcessParamNRZ.size());

		// For OCN, NRZ and PHH File
		if (CollectionUtils.isNotEmpty(listOfDPProcessParamOCN) || CollectionUtils.isNotEmpty(listOfDPProcessParamNRZ) || CollectionUtils
				.isNotEmpty(listOfDPProcessParamPHH)) {
			dpFileProcessBO.generateOutputFile(listOfDPProcessParamOCN, listOfDPProcessParamNRZ, listOfDPProcessParamPHH, httpResponse, zipFileName);

		}
	}

	@Override
	public List<DPDashboardParamInfo> getFilteredDashboardDetails(DashboardFilterInfo dashboardFilterInfo) throws SystemException {
		List<DPDashboardParamInfo> listOfDPDashboardParamInfo = new ArrayList<>();
		List<DPDashboardParamInfo> listOfDashboardParams = new ArrayList<>();

		/* To get details for Week Zero tables */
		if (dashboardFilterInfo.getWeekType().equalsIgnoreCase(DPAConstants.WEEK0)) {
			log.info("Preparing Dashboard with Week 0 details");
			List<DPProcessParamInfo> listOfDPProcessParamsInfo = dpFileProcessBO.getWeekZeroFilteredFiles(dashboardFilterInfo);
			Map map = listOfDPProcessParamsInfo.stream()
					.collect(Collectors.groupingBy(item -> item.getDynamicPricingFilePrcsStatus().getId(), Collectors.counting()));
			listOfDPProcessParamsInfo.stream().forEach(week0Params -> {
				final DPDashboardParamInfo dpDashboardParamInfo = dpDashboardMapper.dpWeekZeroToDashboardInfoMapper(week0Params);
				if (!dpDashboardParamInfo.getStatus().equals(DPFileProcessStatus.DATA_LOAD.getFileStatus()))
					listOfDPDashboardParamInfo.add(dpDashboardParamInfo);
			});
			listOfDashboardParams = dpFileProcessBO.getDashboardParams(listOfDPDashboardParamInfo, map);
			if (listOfDashboardParams.isEmpty()) {
				log.error("Unable create dashboard for Week 0!");
				throw new SystemException(CoreExceptionCodes.DPWKN0101, new Object[] {});
			}
		}
		/* To get details for Week N tables */
		if (dashboardFilterInfo.getWeekType().equalsIgnoreCase(DPAConstants.WEEKN)) {
			log.info("Preparing Dashboard with Week N details");
			List<DPProcessWeekNParamInfo> listOfDPProcessParamsInfo = dpFileProcessBO.getWeekNFilteredData(dashboardFilterInfo);
			Map map = listOfDPProcessParamsInfo.stream()
					.collect(Collectors.groupingBy(item -> item.getDpWeekNProcessStatus().getId(), Collectors.counting()));
			listOfDPProcessParamsInfo.stream().forEach(weekNParams -> {
				final DPDashboardParamInfo dpDashboardParamInfo = dpWeekNDashboardMapper.dpWeekNToDashboardInfoMapper(weekNParams);
				if (!dpDashboardParamInfo.getStatus().equals(DPFileProcessStatus.DATA_LOAD.getFileStatus()))
					listOfDPDashboardParamInfo.add(dpDashboardParamInfo);
			});
			listOfDashboardParams = dpFileProcessBO.getDashboardParams(listOfDPDashboardParamInfo, map);
			if (listOfDashboardParams.isEmpty()) {
				log.error("Unable create dashboard for Week N! !");
				throw new SystemException(CoreExceptionCodes.DPWKN0102, new Object[] {});
			}
		}
		/* To get details for SOP Week 0 tables */
		if (dashboardFilterInfo.getWeekType().equalsIgnoreCase(DPAConstants.SOP_WEEK0)) {
			log.info("Preparing Dashboard with SOP Week 0 details");
			List<DPSopWeek0ParamInfo> listOfDPSOPWeek0ProcessParamsInfo = dpSopProcessBO.getWeekZeroFilteredFiles(dashboardFilterInfo);
			Map map = listOfDPSOPWeek0ProcessParamsInfo.stream()
					.collect(Collectors.groupingBy(item -> item.getSopWeek0ProcessStatus().getId(), Collectors.counting()));
			listOfDPSOPWeek0ProcessParamsInfo.stream().forEach(sopWeek0Params -> {
				final DPDashboardParamInfo dpDashboardParamInfo = dpSOPWeek0DashboardMapper.dpSOPWeek0DashboardInfoMapper(sopWeek0Params);
				if (!dpDashboardParamInfo.getStatus().equals(DPFileProcessStatus.DATA_LOAD.getFileStatus()))
					listOfDPDashboardParamInfo.add(dpDashboardParamInfo);
			});
			listOfDashboardParams = dpFileProcessBO.getDashboardParams(listOfDPDashboardParamInfo, map);
			if (listOfDashboardParams.isEmpty()) {
				log.error("Unable create dashboard for SOP Week 0!");
				throw new SystemException(CoreExceptionCodes.DPSOPWK0011, new Object[] {});
			}
		}
		/* To get details for SOP Week N tables */
		if (dashboardFilterInfo.getWeekType().equalsIgnoreCase(DPAConstants.SOP_WEEKN)) {
			log.info("Preparing Dashboard with SOP Week N details");
			List<DPSopWeekNParamInfo> listOfDPSOPWeekNProcessParamsInfo = dpSopWeekNParamBO.getWeekNFilteredFiles(dashboardFilterInfo);
			Map map = listOfDPSOPWeekNProcessParamsInfo.stream()
					.collect(Collectors.groupingBy(item -> item.getSopWeekNProcessStatus().getId(), Collectors.counting()));
			listOfDPSOPWeekNProcessParamsInfo.stream().forEach(sopWeekNParams -> {
				final DPDashboardParamInfo dpDashboardParamInfo = dpSOPWeekNDashboardMapper.dpSOPWeekNToDashboardInfoMapper(sopWeekNParams);
				if (!dpDashboardParamInfo.getStatus().equals(DPFileProcessStatus.DATA_LOAD.getFileStatus()))
					listOfDPDashboardParamInfo.add(dpDashboardParamInfo);
			});
			listOfDashboardParams = dpFileProcessBO.getDashboardParams(listOfDPDashboardParamInfo, map);
			if (listOfDashboardParams.isEmpty()) {
				log.error("Unable create dashboard for SOP Week N!");
				throw new SystemException(CoreExceptionCodes.DPSOPWKN005, new Object[] {});
			}
		}

		listOfDashboardParams = listOfDashboardParams.stream()
				.sorted(Comparator.comparing(DPDashboardParamInfo::getUploadTimeStampInMillis).reversed()).collect(Collectors.toList());

		return listOfDashboardParams;
	}

	@Override
	public List<DPAssetDetails> searchAssetDetails(String assetNumber, String occupancy) throws SystemException {
		List<DPAssetDetails> dpAssetDetails = new ArrayList<>();
		Long startTime = DateTime.now().getMillis();
		String initialValuation = null;
		String oldAssetNumber = null;
		String propTemp = null;
		Map<String, String> assetMap = new HashMap<String, String>();
		assetMap.put(RRMigration.LOAN_NUM, assetNumber);
		assetMap.put(RRMigration.OLD_RR_LOAN_NUM, oldAssetNumber);
		assetMap.put(RRMigration.PROP_TEMP, propTemp);
		rRMigration.checkForMigration(assetMap);
		if (DPAConstants.VACANT.equals(occupancy)) {
			List<DPProcessParam> dpProcessParams = dpProcessParamsBo.searchByAssetNumber(assetMap.get(RRMigration.LOAN_NUM));
			log.info("Time taken to get Week0 Asset details : " + (DateTime.now().getMillis() - startTime) + "ms");
			if (CollectionUtils.isNotEmpty(dpProcessParams)) {
				for (DPProcessParam dpProcessParam : dpProcessParams) {
					DPAssetDetails dpAssetdetail = new DPAssetDetails();
					dpAssetdetail.setLoanNumber(assetMap.get(RRMigration.LOAN_NUM));
					dpAssetdetail.setOldLoanNumber(assetMap.get(RRMigration.OLD_RR_LOAN_NUM));
					dpAssetdetail.setPropTemp(assetMap.get(RRMigration.PROP_TEMP));
					dpAssetdetail.setClassification(dpProcessParam.getClassification());
					dpAssetdetail.setAssignmentDate(dpProcessParam.getAssignmentDate() != null ?
							DateConversionUtil.getUTCDate(dpProcessParam.getAssignmentDate()).toString(DateConversionUtil.US_DATE_TIME_FORMATTER) :
							null);
					dpAssetdetail.setEligible(dpProcessParam.getEligible());
					dpAssetdetail.setAssignment(dpProcessParam.getAssignment());
					dpAssetdetail.setRecommendedValue(dpProcessParam.getWeek0Price() != null ? dpProcessParam.getWeek0Price().toString() : null);
					dpAssetdetail.setWeek(WEEK_0);
					dpAssetdetail.setNotes(dpProcessParam.getNotes());
					if (initialValuation == null && StringUtils
							.equalsIgnoreCase(dpProcessParam.getEligible(), DPProcessFilterParams.ELIGIBLE.getValue())) {
						initialValuation = dpProcessParam.getAssetValue() != null ? dpProcessParam.getAssetValue().toString() : null;
					}
					dpAssetDetails.add(dpAssetdetail);
				}
				String initVal = initialValuation;
				dpAssetDetails.stream().forEach(dpAssetdetail -> {
					dpAssetdetail.setInitialValuation(initVal);
				});
			}

			startTime = DateTime.now().getMillis();
			List<DPProcessWeekNParam> dpWeeknParams = dpProcessWeekNParamsBO.searchByAssetNumber(assetMap.get(RRMigration.LOAN_NUM));
			log.info("Time taken to get WeekN Asset details : " + (DateTime.now().getMillis() - startTime) + "ms");
			if (CollectionUtils.isNotEmpty(dpWeeknParams)) {
				for (DPProcessWeekNParam dpWeeknParam : dpWeeknParams) {
					if (dpWeeknParam.getDeliveryDate() != null) {
						DPAssetDetails dpAssetdetail = new DPAssetDetails();
						dpAssetdetail.setLoanNumber(assetMap.get(RRMigration.LOAN_NUM));
						dpAssetdetail.setOldLoanNumber(assetMap.get(RRMigration.OLD_RR_LOAN_NUM));
						dpAssetdetail.setPropTemp(assetMap.get(RRMigration.PROP_TEMP));
						dpAssetdetail.setClassification(dpWeeknParam.getClassification());
						dpAssetdetail.setAssignmentDate(
								DateConversionUtil.getUTCDate(dpWeeknParam.getDeliveryDate()).toString(DateConversionUtil.US_DATE_TIME_FORMATTER));
						dpAssetdetail.setEligible(DPProcessParamAttributes.ELIGIBLE.getValue());
						dpAssetdetail.setAssignment(DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue());
						dpAssetdetail.setRecommendedValue(
								dpWeeknParam.getLpDollarAdjustmentRec() != null ? dpWeeknParam.getLpDollarAdjustmentRec().toString() : null);
						dpAssetdetail.setWeek(dpWeeknParam.getModelVersion());
						dpAssetdetail.setNotes(null);
						dpAssetdetail.setInitialValuation(initialValuation);
						dpAssetDetails.add(dpAssetdetail);
					}
				}
			}

		} else if (DPAConstants.OCCUPIED.equals(occupancy)) {
			List<DPSopWeek0Param> dpProcessParams = dpSopProcessBO.searchByAssetNumber(assetMap.get(RRMigration.LOAN_NUM));
			log.info("Time taken to get SOP Week0 Asset details : " + (DateTime.now().getMillis() - startTime) + "ms");
			if (CollectionUtils.isNotEmpty(dpProcessParams)) {
				for (DPSopWeek0Param dpProcessParam : dpProcessParams) {
					DPAssetDetails dpAssetdetail = new DPAssetDetails();
					dpAssetdetail.setLoanNumber(assetMap.get(RRMigration.LOAN_NUM));
					dpAssetdetail.setOldLoanNumber(assetMap.get(RRMigration.OLD_RR_LOAN_NUM));
					dpAssetdetail.setPropTemp(assetMap.get(RRMigration.PROP_TEMP));
					dpAssetdetail.setClassification(dpProcessParam.getClassification());
					dpAssetdetail.setAssignmentDate(dpProcessParam.getAssignmentDate() != null ?
							DateConversionUtil.getUTCDate(dpProcessParam.getAssignmentDate()).toString(DateConversionUtil.US_DATE_TIME_FORMATTER) :
							null);
					dpAssetdetail.setEligible(dpProcessParam.getEligible());
					dpAssetdetail.setAssignment(dpProcessParam.getAssignment());
					dpAssetdetail.setRecommendedValue(dpProcessParam.getListPrice() != null ? dpProcessParam.getListPrice().toString() : null);
					dpAssetdetail.setWeek(SOP_WEEK_0);
					dpAssetdetail.setNotes(dpProcessParam.getNotes());
					if (initialValuation == null && StringUtils
							.equalsIgnoreCase(dpProcessParam.getEligible(), DPProcessFilterParams.ELIGIBLE.getValue())) {
						initialValuation = dpProcessParam.getAssetValue() != null ? dpProcessParam.getAssetValue().toString() : null;
					}
					dpAssetDetails.add(dpAssetdetail);
				}
				String initVal = initialValuation;
				dpAssetDetails.stream().forEach(dpAssetdetail -> {
					dpAssetdetail.setInitialValuation(initVal);
				});
			}

			startTime = DateTime.now().getMillis();
			List<DPSopWeekNParam> dpWeeknParams = dpSopWeekNParamBO.searchByAssetNumber(assetMap.get(RRMigration.LOAN_NUM));
			log.info("Time taken to get WeekN Asset details : " + (DateTime.now().getMillis() - startTime) + "ms");
			if (CollectionUtils.isNotEmpty(dpWeeknParams)) {
				for (DPSopWeekNParam dpWeeknParam : dpWeeknParams) {
					if (dpWeeknParam.getDeliveryDate() != null) {
						DPAssetDetails dpAssetdetail = new DPAssetDetails();
						dpAssetdetail.setLoanNumber(assetMap.get(RRMigration.LOAN_NUM));
						dpAssetdetail.setOldLoanNumber(assetMap.get(RRMigration.OLD_RR_LOAN_NUM));
						dpAssetdetail.setPropTemp(assetMap.get(RRMigration.PROP_TEMP));
						dpAssetdetail.setClassification(dpWeeknParam.getClassification());
						dpAssetdetail.setAssignmentDate(
								DateConversionUtil.getUTCDate(dpWeeknParam.getDeliveryDate()).toString(DateConversionUtil.US_DATE_TIME_FORMATTER));
						dpAssetdetail.setEligible(DPProcessParamAttributes.ELIGIBLE.getValue());
						dpAssetdetail.setAssignment(DPProcessParamAttributes.MODELED_ASSIGNMENT.getValue());
						dpAssetdetail.setRecommendedValue(
								dpWeeknParam.getLpDollarAdjustmentRec() != null ? dpWeeknParam.getLpDollarAdjustmentRec().toString() : null);
						dpAssetdetail.setWeek(dpWeeknParam.getModelVersion());
						dpAssetdetail.setNotes(null);
						dpAssetdetail.setInitialValuation(initialValuation);
						dpAssetDetails.add(dpAssetdetail);
					}
				}
			}

		}
		dpAssetDetails = dpAssetDetails.stream().sorted(Comparator.comparing(DPAssetDetails::getAssignmentDate).reversed())
				.collect(Collectors.toList());
		return dpAssetDetails;
	}

	/**
	 * @param assetNumber
	 * @param occupancy
	 * @return DPAssetDetails
	 * @throws SystemException
	 */
	@Override
	public DPAssetDetails removeLoanFromDP(String assetNumber, String occupancy, String reason) throws SystemException {
		DPAssetDetails dpAssetDetails = null;
		Long startTime = DateTime.now().getMillis();
		if (DPAConstants.VACANT.equals(occupancy)) {
			//Checking if the loan is already excluded
			DPProcessParam dpProcessParam = dpProcessParamsBo.findOutOfScopeLoanByAssetNumber(assetNumber);
			if (!ObjectUtils.isEmpty(dpProcessParam)) {
				dpProcessParam.setNotes(DPAConstants.OUT_OF_SCOPE);
				return week0ToInfoMapper.dpProcessParamToAssetDetailsMapper(dpProcessParam);
			}

			dpProcessParam = dpProcessParamsBo.findOcwenLoanBYAssetNumber(assetNumber);
			log.info("Time taken to get Week0 Asset details : " + (DateTime.now().getMillis() - startTime) + "ms");
			if (!ObjectUtils.isEmpty(dpProcessParam)) {
				dpProcessParam.setNotes(reason);
				dpProcessParam.setEligible(DPAConstants.OUT_OF_SCOPE);
				dpProcessParam.setUpdateTimestamp(DateConversionUtil.getCurrentUTCTime().getMillis());
				dpAssetDetails = week0ToInfoMapper.dpProcessParamToAssetDetailsMapper(dpProcessParam);
				dpProcessParamsBo.saveDPProcessParam(dpProcessParam);
			}

		}
		else if(DPAConstants.OCCUPIED.equals(occupancy)) {
			//Checking if the loan is already excluded
			DPSopWeek0Param dpProcessParam = dpSopWeek0Param.findOutOfScopeLoanByAssetNumber(assetNumber);
			if (!ObjectUtils.isEmpty(dpProcessParam)) {
				dpProcessParam.setNotes(DPAConstants.OUT_OF_SCOPE);
				return dpSopWeek0ParamMapper.dpProcessParamToAssetDetailsMapper(dpProcessParam);
			}

			dpProcessParam = dpSopWeek0Param.findOcwenLoanByAssetNumber(assetNumber);
			log.info("Time taken to get Week0 Asset details : " + (DateTime.now().getMillis() - startTime) + "ms");
			if (!ObjectUtils.isEmpty(dpProcessParam)) {
				dpProcessParam.setNotes(reason);
				dpProcessParam.setEligible(DPAConstants.OUT_OF_SCOPE);
				dpAssetDetails = dpSopWeek0ParamMapper.dpProcessParamToAssetDetailsMapper(dpProcessParam);
				dpSopWeek0Param.saveDPSopWeek0Param(dpProcessParam);
			}

		}
		return dpAssetDetails;
	}

}

