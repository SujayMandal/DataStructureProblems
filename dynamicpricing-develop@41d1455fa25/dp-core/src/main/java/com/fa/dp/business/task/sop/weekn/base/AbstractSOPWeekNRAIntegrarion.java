package com.fa.dp.business.task.sop.weekn.base;

import com.fa.dp.business.command.Command;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.sop.week0.delegate.DPSopProcessFilterDelegate;
import com.fa.dp.business.sop.weekN.delegate.DPSopWeekNParamDelegate;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamInfo;
import com.fa.dp.business.task.sop.weekn.filters.SOPWeekNRAIntegrarion;
import com.fa.dp.business.task.sop.weekn.ra.input.DpSopWeekNRAModelPreparator;
import com.fa.dp.business.util.ThreadPoolExecutorUtil;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.rest.RAClient;
import com.fa.dp.core.util.DateConversionUtil;
import com.fa.dp.core.util.KeyValue;
import com.fa.dp.core.util.RAClientConstants;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
@Named
public abstract class AbstractSOPWeekNRAIntegrarion implements Command, SOPWeekNRAIntegrarion {

	@Inject
	private DPSopProcessFilterDelegate dpSopProcessFilterDelegate;

	@Inject
	private DPSopWeekNParamDelegate sopWeekNParamDelegate;

	@Inject
	private DpSopWeekNRAModelPreparator sopWeekNRAModelPreparator;

	@Inject
	private RAClient raClient;

	@Value("${WEEKN_CONCURRENT_RACALL_POOL_SIZE}")
	private int concurrentRaCallPoolSize;

	private ExecutorService executorService;

	@PostConstruct
	public void initializeTemplate() {
		executorService = ThreadPoolExecutorUtil.getFixedSizeThreadPool(concurrentRaCallPoolSize);
	}

	@PreDestroy
	public void destroy() {
		if(executorService != null) {
			executorService.shutdown();
		}
	}

	@Override
	public void executeRACall(DPSopWeekNParamEntryInfo paramEntryInfo, String tenantCode, String modelName, String modelMajorVersion,
			String modelMinorVersion, String authToken) throws SystemException {
		List<DPSopWeekNParamInfo> successEntries = new ArrayList<>();

		List<Future<KeyValue<Map, DPSopWeekNParamInfo>>> futureList = new ArrayList<>();

		if(CollectionUtils.isNotEmpty(paramEntryInfo.getColumnEntries())){
			paramEntryInfo.getColumnEntries().forEach(paramEntry -> {
				Map<String, Object> raRequest = null;
				try {
					raRequest = sopWeekNRAModelPreparator.prepareSopWeekNRAMapping(paramEntry, modelName, modelMajorVersion, modelMinorVersion);
				} catch (Exception e) {
					log.error("sop weekn ra input prepare failed", e);
					setModelFailure(paramEntry);
				}
				if(null != raRequest && !raRequest.isEmpty()) {
					Future<KeyValue<Map, DPSopWeekNParamInfo>> future = executorService.submit(executeDPAModel(tenantCode, modelName,
							StringUtils.join(new String[] {modelMajorVersion, modelMinorVersion}, RAClientConstants.CHAR_DOT), authToken, raRequest,
							paramEntry));
					futureList.add(future);
				}
			});
		}

		futureList.stream().forEach(future -> {
			KeyValue<Map, DPSopWeekNParamInfo> keyValuePair = null;
			try {
				keyValuePair = future.get();

				DPSopWeekNParamInfo dPProcessParamInfo = keyValuePair.getValue();
				if(keyValuePair.getKey() != null) {
					sopWeekNParamDelegate.populateSopWeekNOutputParam(dPProcessParamInfo, keyValuePair.getKey());
				}
				if(StringUtils.isEmpty(dPProcessParamInfo.getFailedStepCommandName())) {
					successEntries.add(dPProcessParamInfo);
				}
			} catch (InterruptedException | ExecutionException e) {
				log.error("Execution exception occured while invoking RA for sop weekn", e);
			} catch (SystemException e) {
				log.error("System exception occured while invoking RA for sop weekn", e);
			}
		});
		paramEntryInfo.setColumnEntries(successEntries);
	}

	private Callable<KeyValue<Map, DPSopWeekNParamInfo>> executeDPAModel(String tenantCode, String modelName, String modelVersion, String authToken,
			Map<String, Object> raRequest, DPSopWeekNParamInfo info) {
		final Map<String, String> mdcContext = MDC.getCopyOfContextMap();
		return DelegatingSecurityContextCallable.create(() -> {
			if(mdcContext != null)
				MDC.setContextMap(mdcContext);
			MDC.put(RAClientConstants.LOAN_NUMBER, info.getAssetNumber());
			Map response = null;
			try {
				response = raClient.executeSopWeekNDPAModel(tenantCode, modelName, modelVersion, authToken, raRequest);
			} catch (SystemException se) {
				log.error("Sop Weekn ra call failed", se);
				setModelFailure(info);
			}
			MDC.remove(RAClientConstants.LOAN_NUMBER);
			return new KeyValue<>(response, info);
		}, SecurityContextHolder.getContext());
	}
	
	private void setModelFailure(DPSopWeekNParamInfo info){
		info.setDeliveryDate(DateConversionUtil.getCurrentEstDate().getMillis());
		info.setEligible(DPProcessFilterParams.INELIGIBLE.getValue());
		info.setAssignment(DPProcessFilterParams.ASSIGNMENT_ERROR.getValue());
		info.setExclusionReason(DPProcessFilterParams.RA_FAIL_EXCLUSION.getValue());
		info.setFailedStepCommandName(MDC.get(RAClientConstants.COMMAND_PROCES));
		try {
			sopWeekNParamDelegate.saveSopWeekNParamInfo(info);
		} catch (SystemException e) {
			log.error("sop weekn saving failure", e);
		}
	}

}
