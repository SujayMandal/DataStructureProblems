package com.fa.dp.business.rr.rtng.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.core.util.DateConversionUtil;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fa.dp.business.command.dao.CommandDAO;
import com.fa.dp.business.command.entity.Command;
import com.fa.dp.business.command.info.CommandInfo;
import com.fa.dp.business.command.info.CommandProcess;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.constant.DPProcessParamAttributes;
import com.fa.dp.business.filter.bo.DPProcessParamsBO;
import com.fa.dp.business.filter.constant.DPProcessFilterParams;
import com.fa.dp.business.info.RTNGResponse;
import com.fa.dp.business.info.Response;
import com.fa.dp.business.info.RrResponse;
import com.fa.dp.business.info.RtngInfo;
import com.fa.dp.business.util.ThreadPoolExecutorUtil;
import com.fa.dp.business.util.TransactionStatus;
import com.fa.dp.business.validation.input.info.DPProcessParamEntryInfo;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.util.KeyValue;
import com.fa.dp.core.util.RAClientConstants;

@Named
public class RrRtngAggregator extends AbstractDelegate {

	private static final Logger LOGGER = LoggerFactory.getLogger(RrRtngAggregator.class);

	@Inject
	private RrRtngMapper rrRtngMapper;

	@Inject
	private CommandDAO commandDAO;

	@Inject
	private DPProcessParamsBO dpProcessParamsBo;

	private static final String BLANK = "";

	private static final String PROPERTY_CONDITION = "C4-Below Average";

	private ExecutorService executorService;

	private ExecutorService rRexecutorService;

	@Value("${WEEK0_CONCURRENT_DBCALL_POOL_SIZE}")
	private int concurrentDbCallPoolSize;

	@PostConstruct
	public void initializeTemplate() {
		executorService = ThreadPoolExecutorUtil.getFixedSizeThreadPool(concurrentDbCallPoolSize);
		rRexecutorService = ThreadPoolExecutorUtil.getFixedSizeThreadPool(concurrentDbCallPoolSize);
	}

	@PreDestroy
	public void destroy() {
		if (executorService != null) {
			executorService.shutdown();
		}
		if (rRexecutorService != null) {
			rRexecutorService.shutdown();
		}
	}

	public DPProcessParamEntryInfo prepareRARespose(DPProcessParamEntryInfo dp) throws SystemException {
		Long startTime = System.currentTimeMillis();
		LOGGER.info("Enter RrRtngAggregator :: method prepareRARespose");
		List<Future<KeyValue<List<Response>, DPProcessParamInfo>>> futureList = new ArrayList<>();

		try {
			List<DPProcessParamInfo> list = dp.getColumnEntries();
			List<DPProcessParamInfo> successEntries = new ArrayList<>();
			list.forEach(dPProcessParamInfo -> {
				Future<KeyValue<List<Response>, DPProcessParamInfo>> future = executorService
						.submit(fetchRRAndRTNGResponse(dPProcessParamInfo, rRexecutorService));
				futureList.add(future);
			});

			for (Future<KeyValue<List<Response>, DPProcessParamInfo>> keyValueFuture : futureList) {
				KeyValue<List<Response>, DPProcessParamInfo> keyValuePair = keyValueFuture.get();
				DPProcessParamInfo dPProcessParamInfo = keyValuePair.getValue();
				createRARespose(keyValuePair.getKey(), dPProcessParamInfo);

				// setting fields specific to failure
				/*if (TransactionStatus.FAIL.getTranStatus().equals(dPProcessParamInfo.getRtngResponse().getTransactionStatus())) {
					dPProcessParamInfo.setNotes(DPProcessFilterParams.NOTES_RRTNG.getValue());
					dPProcessParamInfo.setState(BLANK);
					dPProcessParamInfo.setRtSource(BLANK);
					dPProcessParamInfo.setPropertyType(BLANK);
					if (!dp.isReprocess() || StringUtils.isBlank(dPProcessParamInfo.getDpProcessParamOriginal().getPropertyType()))
						dPProcessParamInfo.getDpProcessParamOriginal().setPropertyType(BLANK);
				} else if (TransactionStatus.FAIL.getTranStatus().equals(dPProcessParamInfo.getRrResponse().getTransactionStatus())
						  && TransactionStatus.SUCCESS.getTranStatus().equals(dPProcessParamInfo.getRtngResponse().getTransactionStatus())) {
					dPProcessParamInfo.setNotes(DPProcessFilterParams.NOTES_RR.getValue());
				} else if (TransactionStatus.SUCCESS.getTranStatus().equals(dPProcessParamInfo.getRrResponse().getTransactionStatus())
						  && TransactionStatus.FAIL.getTranStatus().equals(dPProcessParamInfo.getRtngResponse().getTransactionStatus())) {
					dPProcessParamInfo.setNotes(DPProcessFilterParams.NOTES_RTNG.getValue());
					dPProcessParamInfo.setState(BLANK);
					dPProcessParamInfo.setRtSource(BLANK);
					dPProcessParamInfo.setPropertyType(BLANK);
					if (!dp.isReprocess() || StringUtils.isBlank(dPProcessParamInfo.getDpProcessParamOriginal().getPropertyType()))
						dPProcessParamInfo.getDpProcessParamOriginal().setPropertyType(BLANK);
				}*/
				if (TransactionStatus.FAIL.getTranStatus().equals(dPProcessParamInfo.getRtngResponse().getTransactionStatus())) {
					dPProcessParamInfo.setNotes(DPProcessFilterParams.NOTES_RRTNG.getValue());
					dPProcessParamInfo.setState(BLANK);
					dPProcessParamInfo.setRtSource(BLANK);
					dPProcessParamInfo.setPropertyType(BLANK);
				}

				if (TransactionStatus.SUCCESS.getTranStatus().equals(dPProcessParamInfo.getRtngResponse().getTransactionStatus())) {
					String propertyType = null;
					String state = null;
					for (int i = dPProcessParamInfo.getRtngResponse().getRtngInfos().size() - 1; i > 0; i--) {
						if (dPProcessParamInfo.getRtngResponse().getRtngInfos().get(i).getOrderCreatedDate() == null)
							continue;
						else {
							state = dPProcessParamInfo.getRtngResponse().getRtngInfos().get(i).getPropertyState();
							propertyType = dPProcessParamInfo.getRtngResponse().getRtngInfos().get(i).getPropertyType();
							break;
						}
					}
					// If all the records have OrderCreatedDate as null, then we choose the last
					// record for State and propertyType
					if (propertyType == null) {
						propertyType = dPProcessParamInfo.getRtngResponse().getRtngInfos()
								.get(dPProcessParamInfo.getRtngResponse().getRtngInfos().size() - 1).getPropertyType();
						state = dPProcessParamInfo.getRtngResponse().getRtngInfos()
								.get(dPProcessParamInfo.getRtngResponse().getRtngInfos().size() - 1).getPropertyState();
					}
					dPProcessParamInfo.setRtSource(DPProcessFilterParams.RTSOURCE_RTNG.getValue());
					dPProcessParamInfo.setState(state);
					boolean override = false;
					if (StringUtils.isNotBlank(dPProcessParamInfo.getPropertyType()))
						override = true;
					for (RtngInfo rtngInfo : dPProcessParamInfo.getRtngResponse().getRtngInfos()) {
						if (StringUtils.isBlank(rtngInfo.getCurrentReviewHigh())) {
							rtngInfo.setCurrentReviewHigh(rtngInfo.getReviewMidValue());
						}
						if (StringUtils.isBlank(rtngInfo.getCurrentReviewLow())) {
							rtngInfo.setCurrentReviewLow(rtngInfo.getReviewMidValue());
						}
						if (StringUtils.isBlank(rtngInfo.getAsIsHigh())) {
							rtngInfo.setAsIsHigh(rtngInfo.getCurrentReviewHigh());
						}
						if (StringUtils.isBlank(rtngInfo.getAsIsLow())) {
							rtngInfo.setAsIsLow(rtngInfo.getCurrentReviewLow());
						}
						if(StringUtils.isBlank(rtngInfo.getPropertyCondition())) {
							rtngInfo.setPropertyCondition(PROPERTY_CONDITION);
						}
						if (override) {
							rtngInfo.setPropertyType(dPProcessParamInfo.getPropertyType());
						}
					}
					if (!override) {
						dPProcessParamInfo.setPropertyType(propertyType);
					}
				}

				// setting fields common for any failure
				if (TransactionStatus.FAIL.getTranStatus().equals(dPProcessParamInfo.getRtngResponse().getTransactionStatus())) {
					dPProcessParamInfo.setAssignment(DPProcessFilterParams.ASSIGNMENT_ERROR.getValue());
					dPProcessParamInfo.setWeek0Price(new BigDecimal(dPProcessParamInfo.getListPrice()));
					dPProcessParamInfo.setAssignmentDate(DateConversionUtil.getCurrentUTCTime().getMillis());
					dPProcessParamInfo.setEligible(DPProcessFilterParams.ELIGIBLE.getValue());
					String process = null;
					if (DPProcessParamAttributes.OCN.getValue().equals(dPProcessParamInfo.getClassification()))
						process = CommandProcess.WEEK0_OCN.getCommmandProcess();
					else if (DPProcessParamAttributes.PHH.getValue().equals(dPProcessParamInfo.getClassification()))
						process = CommandProcess.WEEK0_PHH.getCommmandProcess();
					else if (DPProcessParamAttributes.NRZ.getValue().equals(dPProcessParamInfo.getClassification()))
						process = CommandProcess.WEEK0_NRZ.getCommmandProcess();
					List<Command> command = commandDAO.findByProcess(process,
							DPAConstants.FAILED_REAL_RESOL_REAL_FILTER);
					CommandInfo commandInfo = convert(command.get(0), CommandInfo.class);
					dPProcessParamInfo.setCommand(commandInfo);
					DPProcessParam dpProcessParam = new DPProcessParam();
					dpProcessParam = convert(dPProcessParamInfo, DPProcessParam.class);
					dpProcessParamsBo.saveDPProcessParam(dpProcessParam);
				} else {
					successEntries.add(dPProcessParamInfo);
				}
			}
			LOGGER.info("Time taken for all weekNRRRTNGDBCall records : " + (System.currentTimeMillis() - startTime));
			dp.setColumnEntries(successEntries);
		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			SystemException.newSystemException(CoreExceptionCodes.RACLNCOM001, new Object[] { e.getMessage() });
		}
		LOGGER.info("Exit RrRtngAggregator :: method prepareRARespose");
		return dp;
	}

	private Callable<KeyValue<List<Response>, DPProcessParamInfo>> fetchRRAndRTNGResponse(
			DPProcessParamInfo dPProcessParamInfo, ExecutorService rRExeService) {
		final Map<String, String> mdcContext = MDC.getCopyOfContextMap();
		return DelegatingSecurityContextCallable.create(() -> {
			if (mdcContext != null)
				MDC.setContextMap(mdcContext);
			MDC.put(RAClientConstants.LOAN_NUMBER, dPProcessParamInfo.getAssetNumber());
			List<Response> response = rrRtngMapper.fetchRRAndRTNGResponse(dPProcessParamInfo, rRExeService);
			MDC.remove(RAClientConstants.LOAN_NUMBER);
			return new KeyValue<List<Response>,DPProcessParamInfo>(response, dPProcessParamInfo);
		}, SecurityContextHolder.getContext());
	}

	private DPProcessParamInfo createRARespose(List<Response> infoList, DPProcessParamInfo dPProcessParamInfo) {
		LOGGER.info("Enter RrRtngAggregator :: method createRARespose");
		RrResponse rrResponse = null;
		RTNGResponse rtngResponse = null;
		for (Response response : infoList) {
			if (response instanceof RTNGResponse) {
				rtngResponse = (RTNGResponse) response;
				dPProcessParamInfo.setRtngResponse(rtngResponse);
				if(null != rtngResponse && null != rtngResponse.getRtngInfos()) {
					LOGGER.info("RrRtngAggregator -> RTNGResponse start : ");
					for(RtngInfo infoRTNG: rtngResponse.getRtngInfos()) {
						LOGGER.info("property type : " + infoRTNG.getPropertyType());
					}
					LOGGER.info("RrRtngAggregator -> RTNGResponse end : ");
				} else {
					LOGGER.info("RrRtngAggregator -> RTNGResponse empty.");
				}
			} else if (response instanceof RrResponse) {
				rrResponse = (RrResponse) response;
				dPProcessParamInfo.setRrResponse(rrResponse);
			}
		}
		LOGGER.info("Exit RrRtngAggregator :: method createRARespose");
		return dPProcessParamInfo;

	}
}