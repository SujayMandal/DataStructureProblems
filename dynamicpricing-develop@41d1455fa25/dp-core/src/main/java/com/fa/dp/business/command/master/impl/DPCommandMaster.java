package com.fa.dp.business.command.master.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.util.ObjectUtils;

import com.fa.dp.business.classificationExecutor.classificationFactory.SopWeek0Factory;
import com.fa.dp.business.command.Command;
import com.fa.dp.business.command.CommandPreparator;
import com.fa.dp.business.command.bo.CommandBO;
import com.fa.dp.business.command.executor.CommandExecutor;
import com.fa.dp.business.command.info.CommandProcess;
import com.fa.dp.business.command.master.CommandMaster;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.sop.validator.delegate.DPSopFileDelegate;
import com.fa.dp.business.sop.week0.bo.DPSopProcessBO;
import com.fa.dp.business.sop.week0.entity.DPSopWeek0ProcessStatus;
import com.fa.dp.business.sop.week0.input.info.DPSopParamEntryInfo;
import com.fa.dp.business.sop.week0.input.info.DPSopWeek0ParamInfo;
import com.fa.dp.business.sop.week0.input.mapper.DPSopWeek0ProcessStatusMapper;
import com.fa.dp.business.sop.weekN.bo.DPSopWeekNParamBO;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.mapper.DPSopWeekNProcessStatusMapper;
import com.fa.dp.business.util.DPFileProcessStatus;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.bo.DPFileProcessBO;
import com.fa.dp.business.week0.delegate.DPFileProcessDelegate;
import com.fa.dp.business.week0.entity.DynamicPricingFilePrcsStatus;
import com.fa.dp.business.weekn.entity.DPWeekNProcessStatus;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamEntryInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.business.BusinessException;

/**
 * @author misprakh
 */
@Slf4j
@Named("dpCommandMaster")
public class DPCommandMaster extends AbstractDelegate implements CommandMaster {

	private static final String WEEKN = "WEEKN";

	private static final String SOP_WEEKN = "SOP_WEEKN";

	private Map<String, List<Command>> commandProcessMap = new HashMap<>();

	@Inject
	private CommandPreparator commandPreparator;

	@Inject
	private CommandExecutor commandExecutor;

	@Inject
	private CommandBO commandBO;

	@Inject
	private DPFileProcessBO dpFileProcessBO;

	@Inject
	private DPFileProcessDelegate dpFileProcessDelegate;

	@Inject
	private SopWeek0Factory sopWeek0Factory;

	@Inject
	private DPSopWeek0ProcessStatusMapper sopWeek0Mapper;

	@Inject
	private DPSopProcessBO dpSopProcessBO;

	@Inject
	private DPSopFileDelegate dpSopFileDelegate;
	
	@Inject
	private DPSopWeekNProcessStatusMapper dpSopWeekNProcessStatusMapper;
	
	@Inject
	private DPSopWeekNParamBO dpSopWeekNParamBO;

	@PostConstruct
	private void init() {

		Arrays.stream(CommandProcess.values()).forEach(process -> {
			try {
				List<String> commandSequence = commandBO.getAllCommandsByExecutionSequenceForProcess(process.getCommmandProcess());
				if (CollectionUtils.isNotEmpty(commandSequence)) {
					List<Command> execCommands = new LinkedList<>();
					for (String commandName : commandSequence) {
						Command command = commandPreparator.prepareCommand(commandName);
						execCommands.add(command);
					}
					commandProcessMap.put(process.getCommmandProcess(), Collections.unmodifiableList(execCommands));
				}
			} catch (SystemException e) {
				log.error("Problem in creating command process for command {}, Error : [}", process.getCommmandProcess(), e);
				throw new BeanCreationException("Command creation failed.", e);
			}
		});
		commandProcessMap = Collections.unmodifiableMap(commandProcessMap);
	}

	@Override
	public void prepareWeek0(DPProcessParamEntryInfo dpProcessParamEntryInfo) throws SystemException {
		createExecuteFlows(dpProcessParamEntryInfo, CommandProcess.WEEK0);
		Map<String, List<DPProcessParamInfo>> classifiedColumnEntriesMap = new HashMap<>();
		List<DPProcessParamInfo> nrzColumnList = new ArrayList<>();
		List<DPProcessParamInfo> ocnColumnList = new ArrayList<>();
		List<DPProcessParamInfo> phhColumnList = new ArrayList<>();

		dpProcessParamEntryInfo.getColumnEntries().forEach(a -> {
			if (StringUtils.equals(a.getClassification(), DPAConstants.OCN))
				ocnColumnList.add(a);
			if (StringUtils.equals(a.getClassification(), DPAConstants.NRZ))
				nrzColumnList.add(a);
			if (StringUtils.equals(a.getClassification(), DPAConstants.PHH))
				phhColumnList.add(a);

		});
		classifiedColumnEntriesMap.put(DPProcessParamAttributes.OCN.getValue(), ocnColumnList);
		classifiedColumnEntriesMap.put(DPProcessParamAttributes.PHH.getValue(), phhColumnList);
		classifiedColumnEntriesMap.put(DPProcessParamAttributes.NRZ.getValue(), nrzColumnList);


/*		List<DPProcessParamInfo> classifiedColumnListOCN = dpProcessParamEntryInfo.getColumnEntries().stream()
				  .filter(item -> StringUtils.equalsIgnoreCase(item.getClassification(), DPProcessParamAttributes.OCN.getValue()))
				  .collect(Collectors.toList());
		classifiedColumnEntriesMap.put(DPProcessParamAttributes.OCN.getValue(), classifiedColumnListOCN);

		List<DPProcessParamInfo> classifiedColumnListPHH = dpProcessParamEntryInfo.getColumnEntries().stream()
				  .filter(item -> StringUtils.equalsIgnoreCase(item.getClassification(), DPProcessParamAttributes.PHH.getValue()))
				  .collect(Collectors.toList());
		classifiedColumnEntriesMap.put(DPProcessParamAttributes.PHH.getValue(), classifiedColumnListPHH);

		List<DPProcessParamInfo> classifiedColumnListNRZ = dpProcessParamEntryInfo.getColumnEntries().stream()
				  .filter(item -> StringUtils.equalsIgnoreCase(item.getClassification(), DPProcessParamAttributes.NRZ.getValue()))
				  .collect(Collectors.toList());
		classifiedColumnEntriesMap.put(DPProcessParamAttributes.NRZ.getValue(), classifiedColumnListNRZ);*/

		if (MapUtils.isNotEmpty(classifiedColumnEntriesMap))
			dpProcessParamEntryInfo.setClassifiedColumnEntries(classifiedColumnEntriesMap);

		if (dpProcessParamEntryInfo != null && dpProcessParamEntryInfo.getClassifiedColumnEntries() != null
				&& dpProcessParamEntryInfo.getClassifiedColumnEntries().get(DPProcessParamAttributes.OCN.getValue()) != null) {
			List<DPProcessParamInfo> classifiedColumnList = dpProcessParamEntryInfo.getClassifiedColumnEntries()
					.get(DPProcessParamAttributes.OCN.getValue());
			dpProcessParamEntryInfo.setColumnEntries(classifiedColumnList);
			filterOCNWeek0(dpProcessParamEntryInfo);
		}
		if (dpProcessParamEntryInfo != null && dpProcessParamEntryInfo.getClassifiedColumnEntries() != null
				&& dpProcessParamEntryInfo.getClassifiedColumnEntries().get(DPProcessParamAttributes.PHH.getValue()) != null) {
			List<DPProcessParamInfo> classifiedColumnList = dpProcessParamEntryInfo.getClassifiedColumnEntries()
					.get(DPProcessParamAttributes.PHH.getValue());
			dpProcessParamEntryInfo.setColumnEntries(classifiedColumnList);
			filterPHHWeek0(dpProcessParamEntryInfo);
		}
		if (dpProcessParamEntryInfo != null && dpProcessParamEntryInfo.getClassifiedColumnEntries() != null
				&& dpProcessParamEntryInfo.getClassifiedColumnEntries().get(DPProcessParamAttributes.NRZ.getValue()) != null) {
			List<DPProcessParamInfo> classifiedColumnList = dpProcessParamEntryInfo.getClassifiedColumnEntries()
					.get(DPProcessParamAttributes.NRZ.getValue());
			dpProcessParamEntryInfo.setColumnEntries(classifiedColumnList);
			filterNRZWeek0(dpProcessParamEntryInfo);
		}
		if (null == dpProcessParamEntryInfo.getClassifiedColumnEntries()) {
			if (!dpProcessParamEntryInfo.isReprocess()) {
				dpProcessParamEntryInfo.getDPFileProcessStatusInfo().setStatus(DPFileProcessStatus.FAILED.getFileStatus());
			} else {
				dpProcessParamEntryInfo.getDPFileProcessStatusInfo().setStatus(dpFileProcessDelegate.setStatus(dpProcessParamEntryInfo));
			}
			DynamicPricingFilePrcsStatus processStatus = convert(dpProcessParamEntryInfo.getDPFileProcessStatusInfo(),
					DynamicPricingFilePrcsStatus.class);
			dpFileProcessBO.saveDPProcessStatus(processStatus);
		}
		DynamicPricingFilePrcsStatus processStatus = dpFileProcessBO
				.findDPProcessStatusById(dpProcessParamEntryInfo.getDPFileProcessStatusInfo().getId());
		if (processStatus != null && StringUtils.equals(processStatus.getStatus(), DPFileProcessStatus.IN_PROGRESS.getFileStatus())) {
			processStatus.setStatus(dpFileProcessDelegate.setStatus(dpProcessParamEntryInfo));
			processStatus.setOcnOutputFileName(dpProcessParamEntryInfo.getDPFileProcessStatusInfo().getOcnOutputFileName());
			processStatus.setNrzOutputFileName(dpProcessParamEntryInfo.getDPFileProcessStatusInfo().getNrzOutputFileName());
			processStatus.setPhhOutputFileName(dpProcessParamEntryInfo.getDPFileProcessStatusInfo().getPhhOutputFileName());
			dpFileProcessBO.saveDPProcessStatus(processStatus);
		}
	}

	@Override
	public DPProcessWeekNParamEntryInfo prepareWeekN(DPProcessWeekNParamEntryInfo dpProcessWeekNParamEntryInfo) throws SystemException {

		createExecuteFlows(dpProcessWeekNParamEntryInfo, CommandProcess.WEEKN);
		List<DPProcessWeekNParamInfo> ocnColumnList = new ArrayList<>();
		List<DPProcessWeekNParamInfo> nrzColumnList = new ArrayList<>();
		List<DPProcessWeekNParamInfo> phhColumnList = new ArrayList<>();
		List<DPProcessWeekNParamInfo> failedList = new ArrayList<>();
		List<DPProcessWeekNParamInfo> ocnFilteredColumnEntries = new ArrayList<>();
		List<DPProcessWeekNParamInfo> nrzFilteredColumnEntries = new ArrayList<>();
		List<DPProcessWeekNParamInfo> phhFilteredColumnEntries = new ArrayList<>();

		if (!ObjectUtils.isEmpty(dpProcessWeekNParamEntryInfo.getColumnEntries())) {
			for (DPProcessWeekNParamInfo dpProcessWeeknParam : dpProcessWeekNParamEntryInfo.getColumnEntries()) {
				if (dpProcessWeeknParam.getCommand() == null && dpProcessWeeknParam.getClassification() != null) {
					if (dpProcessWeeknParam.getClassification().equals(DPProcessParamAttributes.OCN.getValue())) {
						ocnColumnList.add(dpProcessWeeknParam);
					} else if (dpProcessWeeknParam.getClassification().equals(DPProcessParamAttributes.NRZ.getValue())) {
						nrzColumnList.add(dpProcessWeeknParam);
					} else {
						phhColumnList.add(dpProcessWeeknParam);
					}
				} else {
					failedList.add(dpProcessWeeknParam);
				}
			}
		}

		if (!ocnColumnList.isEmpty()) {
			dpProcessWeekNParamEntryInfo.setColumnEntries(ocnColumnList);
			filterOCNWeekN(dpProcessWeekNParamEntryInfo);
			ocnFilteredColumnEntries = dpProcessWeekNParamEntryInfo.getColumnEntries();
		}

		if (!nrzColumnList.isEmpty()) {
			dpProcessWeekNParamEntryInfo.setColumnEntries(nrzColumnList);
			filterNRZWeekN(dpProcessWeekNParamEntryInfo);
			nrzFilteredColumnEntries = dpProcessWeekNParamEntryInfo.getColumnEntries();
		}
		if (!phhColumnList.isEmpty()) {
			dpProcessWeekNParamEntryInfo.setColumnEntries(phhColumnList);
			filterPHHWeekN(dpProcessWeekNParamEntryInfo);
			phhFilteredColumnEntries = dpProcessWeekNParamEntryInfo.getColumnEntries();
		}
		dpProcessWeekNParamEntryInfo.setColumnEntries(new ArrayList<>());
		if (CollectionUtils.isNotEmpty(ocnFilteredColumnEntries))
			dpProcessWeekNParamEntryInfo.setColumnEntries(ocnFilteredColumnEntries);

		if (CollectionUtils.isNotEmpty(nrzFilteredColumnEntries))
			dpProcessWeekNParamEntryInfo.getColumnEntries().addAll(nrzFilteredColumnEntries);

		if (CollectionUtils.isNotEmpty(phhFilteredColumnEntries))
			dpProcessWeekNParamEntryInfo.getColumnEntries().addAll(phhFilteredColumnEntries);

		if (!failedList.isEmpty()) {
			dpProcessWeekNParamEntryInfo.getColumnEntries().addAll(failedList);
		}

		if (!dpProcessWeekNParamEntryInfo.isFetchProcess()) {
			DPWeekNProcessStatus dpweeknProcess = dpFileProcessBO
					.findDPWeekNProcessById(dpProcessWeekNParamEntryInfo.getDpWeeknProcessStatus().getId());
			if (dpweeknProcess != null && dpweeknProcess.getStatus().equals(DPFileProcessStatus.IN_PROGRESS.getFileStatus())) {
				dpweeknProcess.setOcnOutputFileName(dpProcessWeekNParamEntryInfo.getDpWeeknProcessStatus().getOcnOutputFileName());
				dpweeknProcess.setNrzOutputFileName(dpProcessWeekNParamEntryInfo.getDpWeeknProcessStatus().getNrzOutputFileName());
				dpweeknProcess.setPhhOutputFileName(dpProcessWeekNParamEntryInfo.getDpWeeknProcessStatus().getPhhOutputFileName());
				dpFileProcessBO.saveDPProcessWeekNStatus(dpweeknProcess);
			}
		}
		return dpProcessWeekNParamEntryInfo;
	}

	/**
	 * This method would take the responsibility of preparing SOP Week0 input for filtering.
	 *
	 * @param dpSopParamEntryInfo
	 * @return
	 * @throws SystemException
	 */
	@Override
	public void prepareSopWeek0(DPSopParamEntryInfo dpSopParamEntryInfo) throws SystemException {
		// Check for SOP  Week 0 Migrated Loans
		dpSopProcessBO.checkSopWeek0Migration(dpSopParamEntryInfo);
		Map<String, List<DPSopWeek0ParamInfo>> classifiedColumnEntriesMap = new HashMap<>();
		List<DPSopWeek0ParamInfo> nrzColumnList = new ArrayList<>();
		List<DPSopWeek0ParamInfo> ocnColumnList = new ArrayList<>();
		List<DPSopWeek0ParamInfo> phhColumnList = new ArrayList<>();
		dpSopParamEntryInfo.getColumnEntries().forEach(a -> {
			if (StringUtils.equals(a.getClassification(), DPAConstants.OCN))
				ocnColumnList.add(a);
			if (StringUtils.equals(a.getClassification(), DPAConstants.NRZ))
				nrzColumnList.add(a);
			if (StringUtils.equals(a.getClassification(), DPAConstants.PHH))
				phhColumnList.add(a);
		});
		classifiedColumnEntriesMap.put(DPAConstants.OCN, ocnColumnList);
		classifiedColumnEntriesMap.put(DPAConstants.PHH, phhColumnList);
		classifiedColumnEntriesMap.put(DPAConstants.NRZ, nrzColumnList);

		if (MapUtils.isNotEmpty(classifiedColumnEntriesMap))
			dpSopParamEntryInfo.setClassifiedColumnEntries(classifiedColumnEntriesMap);

		if (dpSopParamEntryInfo != null && dpSopParamEntryInfo.getColumnEntries() != null) {
			if (dpSopParamEntryInfo.getClassifiedColumnEntries().get(DPAConstants.OCN) != null) {
				List<DPSopWeek0ParamInfo> classifiedColumnList = dpSopParamEntryInfo.getClassifiedColumnEntries().get(DPAConstants.OCN);
				dpSopParamEntryInfo.setColumnEntries(classifiedColumnList);
				filterOCNSopWeek0(dpSopParamEntryInfo);
			}
			if (dpSopParamEntryInfo.getClassifiedColumnEntries().get(DPAConstants.NRZ) != null) {
				List<DPSopWeek0ParamInfo> classifiedColumnList = dpSopParamEntryInfo.getClassifiedColumnEntries().get(DPAConstants.NRZ);
				dpSopParamEntryInfo.setColumnEntries(classifiedColumnList);
				filterNRZSopWeek0(dpSopParamEntryInfo);
			}
			if (dpSopParamEntryInfo.getClassifiedColumnEntries().get(DPAConstants.PHH) != null) {
				List<DPSopWeek0ParamInfo> classifiedColumnList = dpSopParamEntryInfo.getClassifiedColumnEntries().get(DPAConstants.PHH);
				dpSopParamEntryInfo.setColumnEntries(classifiedColumnList);
				filterPHHSopWeek0(dpSopParamEntryInfo);
			}
		}
		DPSopWeek0ProcessStatus processStatus = dpSopProcessBO
				.findDpSopWeek0ProcessStatusById(dpSopParamEntryInfo.getDpSopWeek0ProcessStatusInfo().getId());
		if (processStatus != null) {
			processStatus.setStatus(dpSopFileDelegate.setStatus(dpSopParamEntryInfo));
			dpSopProcessBO.saveDPSopProcessStatus(processStatus);
		}
	}

	private void filterOCNWeekN(DPProcessWeekNParamEntryInfo dpProcessWeekNParamEntryInfo) throws SystemException {
		createExecuteFlows(dpProcessWeekNParamEntryInfo, CommandProcess.WEEKN_OCN);
	}

	private void filterOCNSopWeekN(DPSopWeekNParamEntryInfo weekNParamEntryInfo) throws SystemException {
		DPSopWeekNParamEntryInfo paramEntryInfo = new DPSopWeekNParamEntryInfo();
		if (!ObjectUtils.isEmpty(weekNParamEntryInfo) && CollectionUtils.isNotEmpty(weekNParamEntryInfo.getColumnEntries())) {
			paramEntryInfo.setFetchProcess(weekNParamEntryInfo.isFetchProcess());
			paramEntryInfo.setReprocess(weekNParamEntryInfo.isReprocess());
			paramEntryInfo.setColumnEntries(weekNParamEntryInfo.getColumnEntries().stream()
					.filter(a -> StringUtils.equals(DPProcessParamAttributes.OCN.getValue(), a.getClassification())).collect(Collectors.toList()));
		}
		createExecuteFlows(paramEntryInfo, CommandProcess.SOP_WEEKN_OCN);
	}

	private void filterPHHSopWeekN(DPSopWeekNParamEntryInfo weekNParamEntryInfo) throws SystemException {
		DPSopWeekNParamEntryInfo paramEntryInfo = new DPSopWeekNParamEntryInfo();
		if (!ObjectUtils.isEmpty(weekNParamEntryInfo) && CollectionUtils.isNotEmpty(weekNParamEntryInfo.getColumnEntries())) {
			paramEntryInfo.setFetchProcess(weekNParamEntryInfo.isFetchProcess());
			paramEntryInfo.setReprocess(weekNParamEntryInfo.isReprocess());
			paramEntryInfo.setColumnEntries(weekNParamEntryInfo.getColumnEntries().stream()
					.filter(a -> StringUtils.equals(DPProcessParamAttributes.PHH.getValue(), a.getClassification())).collect(Collectors.toList()));
		}
		createExecuteFlows(paramEntryInfo, CommandProcess.SOP_WEEKN_PHH);
	}

	private void filterNRZSopWeekN(DPSopWeekNParamEntryInfo weekNParamEntryInfo) throws SystemException {
		DPSopWeekNParamEntryInfo paramEntryInfo = new DPSopWeekNParamEntryInfo();
		if (!ObjectUtils.isEmpty(weekNParamEntryInfo) && CollectionUtils.isNotEmpty(weekNParamEntryInfo.getColumnEntries())) {
			paramEntryInfo.setFetchProcess(weekNParamEntryInfo.isFetchProcess());
			paramEntryInfo.setReprocess(weekNParamEntryInfo.isReprocess());
			paramEntryInfo.setColumnEntries(weekNParamEntryInfo.getColumnEntries().stream()
					.filter(a -> StringUtils.equals(DPProcessParamAttributes.NRZ.getValue(), a.getClassification())).collect(Collectors.toList()));
		}
		createExecuteFlows(paramEntryInfo, CommandProcess.SOP_WEEKN_NRZ);
	}

	private void filterNRZWeekN(DPProcessWeekNParamEntryInfo dpProcessWeekNParamEntryInfo) throws SystemException {
		createExecuteFlows(dpProcessWeekNParamEntryInfo, CommandProcess.WEEKN_NRZ);
	}

	private void filterPHHWeekN(DPProcessWeekNParamEntryInfo dpProcessWeekNParamEntryInfo) throws SystemException {
		createExecuteFlows(dpProcessWeekNParamEntryInfo, CommandProcess.WEEKN_PHH);
	}

	private void filterOCNWeek0(DPProcessParamEntryInfo dpProcessParamEntryInfo) throws SystemException {
		createExecuteFlows(dpProcessParamEntryInfo, CommandProcess.WEEK0_OCN);
	}

	private void filterPHHWeek0(DPProcessParamEntryInfo dpProcessParamEntryInfo) throws SystemException {
		createExecuteFlows(dpProcessParamEntryInfo, CommandProcess.WEEK0_PHH);
	}

	private void filterNRZWeek0(DPProcessParamEntryInfo dpProcessParamEntryInfo) throws SystemException {
		createExecuteFlows(dpProcessParamEntryInfo, CommandProcess.WEEK0_NRZ);
	}

	/**
	 * Filter SOP Week 0 OCN Properties
	 *
	 * @param dpSopParamEntryInfo
	 * @throws SystemException
	 */
	private void filterOCNSopWeek0(DPSopParamEntryInfo dpSopParamEntryInfo) throws SystemException {
		createSopExecuteFlows(dpSopParamEntryInfo, CommandProcess.SOP_WEEK0_OCN);
	}

	/**
	 * Filter SOP Week 0 NRZ Properties
	 *
	 * @param dpSopParamEntryInfo
	 * @throws SystemException
	 */
	private void filterNRZSopWeek0(DPSopParamEntryInfo dpSopParamEntryInfo) throws SystemException {
		createSopExecuteFlows(dpSopParamEntryInfo, CommandProcess.SOP_WEEK0_NRZ);
	}

	/**
	 * Filter SOP Week 0 PHH Properties
	 *
	 * @param dpSopParamEntryInfo
	 * @throws SystemException
	 */
	private void filterPHHSopWeek0(DPSopParamEntryInfo dpSopParamEntryInfo) throws SystemException {
		createSopExecuteFlows(dpSopParamEntryInfo, CommandProcess.SOP_WEEK0_PHH);
	}

	private void createExecuteFlows(Object processInfo, CommandProcess process) throws SystemException {
		log.debug(String.format("DP {} process started", process.getCommmandProcess()));
		commandExecutor.execute(commandProcessMap.get(process.getCommmandProcess()), processInfo);
		log.debug(String.format("DP {} process completed", process.getCommmandProcess()));
	}

	@Override
	public void filterQaReport(DPProcessWeekNParamEntryInfo weeknParamEntryInfo, Boolean sopStatus) {
		try {
			createExecuteFlowsQAReport(weeknParamEntryInfo, sopStatus ? CommandProcess.SOP_QA_REPORT : CommandProcess.QA_REPORT);
		} catch (SystemException e) {
			log.error("Problem in runing qa report command. {}", e);
		}
	}

	@Override
	public void filterQaReportForSOP(DPSopWeekNParamEntryInfo sopWeeknParamEntryInfo, Boolean sopStatus) {
		try {
			createExecuteFlowsQAReportForSOP(sopWeeknParamEntryInfo, sopStatus ? CommandProcess.SOP_QA_REPORT : CommandProcess.QA_REPORT);
		} catch (SystemException e) {
			log.error("Problem in runing qa report command. {}", e);
		}
	}

	@Override
	public void prepareSopWeekN(DPSopWeekNParamEntryInfo sopWeekNParamEntryInfo) throws BusinessException, SystemException {
		String status = DPFileProcessStatus.IN_PROGRESS.getFileStatus();
		try {
			dpSopFileDelegate.updateSopWeeknRunningStatus(sopWeekNParamEntryInfo.getDpSopWeekNProcessStatus(), status);
			createExecuteFlows(sopWeekNParamEntryInfo, CommandProcess.SOP_WEEKN);
			filterOCNSopWeekN(sopWeekNParamEntryInfo);
			filterNRZSopWeekN(sopWeekNParamEntryInfo);
			filterPHHSopWeekN(sopWeekNParamEntryInfo);

		} catch (BusinessException be) {
			log.error("prepareSopWeekN failed with reason.", be);
			status = DPFileProcessStatus.ERROR.getFileStatus();
			throw be;
		}  catch (Exception e) {
			log.error("prepareSopWeekN failed with reason.", e);
			status = DPFileProcessStatus.ERROR.getFileStatus();
		}
	}

	private void createExecuteFlowsQAReport(DPProcessWeekNParamEntryInfo paramEntryInfo, CommandProcess process) throws SystemException {
		log.debug(String.format("DP {} process started", process.getCommmandProcess()));
		commandExecutor.execute(commandProcessMap.get(process.getCommmandProcess()), paramEntryInfo);
		log.debug(String.format("DP {} process completed", process.getCommmandProcess()));
	}


	private void createExecuteFlowsQAReportForSOP(DPSopWeekNParamEntryInfo paramEntryInfo, CommandProcess process) throws SystemException {
		log.debug(String.format("DP {} process started", process.getCommmandProcess()));
		commandExecutor.execute(commandProcessMap.get(process.getCommmandProcess()), paramEntryInfo);
		log.debug(String.format("DP {} process completed", process.getCommmandProcess()));
	}

	private void createSopExecuteFlows(DPSopParamEntryInfo dpSopParamEntryInfo, CommandProcess sopWeek0) throws SystemException {
		log.debug(String.format("DP {} process started", sopWeek0.getCommmandProcess()));
		commandExecutor.execute(commandProcessMap.get(sopWeek0.getCommmandProcess()), dpSopParamEntryInfo);
		log.debug(String.format("DP {} process completed", sopWeek0.getCommmandProcess()));
	}

}