package com.fa.dp.business.rr.rtng.dao;

import javax.inject.Inject;

import com.fa.dp.business.info.Response;
import com.fa.dp.business.info.RrResponse;
import com.fa.dp.business.util.IntegrationType;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.validator.dao.DynamicPricingIntgAuditDao;
import com.fa.dp.business.week0.entity.DPProcessParam;
import com.fa.dp.business.week0.entity.DynamicPricingIntgAudit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

public abstract class AbstractDBClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDBClient.class);

	@Inject
	private DynamicPricingIntgAuditDao dynamicPricingIntgAuditDao;

	public abstract Response execute(DPProcessParamInfo dpInfo);

	public void insertRrRtngResponse(Response response,DPProcessParamInfo dpInfo) {
		LOGGER.debug("save the " + response.getClass().getName() + " audit data");
		try {
			DynamicPricingIntgAudit dynamicPricingIntgAudit = new DynamicPricingIntgAudit();
			dynamicPricingIntgAudit.setEventType(response instanceof RrResponse ?IntegrationType.RR_INTEGRATION.getIntegrationType():IntegrationType.RTNG_INTEGRATION.getIntegrationType());
			dynamicPricingIntgAudit.setStatus(response.getTransactionStatus());
			dynamicPricingIntgAudit.setErrorDescription(response.getErrorMsg());
			dynamicPricingIntgAudit.setStartTime(dpInfo.getStartTime());
			dynamicPricingIntgAudit.setEndTime(dpInfo.getEndTime());
			DPProcessParam dpProcessParam = new DPProcessParam();
			dpProcessParam.setId(dpInfo.getId());
			dynamicPricingIntgAudit.setDpProcessParam(dpProcessParam);
			dynamicPricingIntgAuditDao.save(dynamicPricingIntgAudit);
		} catch (DataAccessException e) {
			LOGGER.error("Error while saving data for class " + response.getClass().getName());
		}

	}
}
