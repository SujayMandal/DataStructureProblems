package com.fa.dp.business.task.sop.weekn.task.executor.phh;

import com.fa.dp.business.command.annotation.CommandDescription;
import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.sop.weekN.input.info.DPSopWeekNParamEntryInfo;
import com.fa.dp.business.task.sop.weekn.base.AbstractSOPWeekNRAIntegrarion;
import com.fa.dp.core.cache.CacheManager;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.systemparam.util.AppParameterConstant;
import com.fa.dp.core.util.RAClientConstants;
import org.slf4j.MDC;

import javax.inject.Inject;
import javax.inject.Named;

@Named
@CommandDescription(name = DPAConstants.PHH_SOPWEEKN_RAINTEGRARION)
public class PhhSOPWeekNRAIntegrarion extends AbstractSOPWeekNRAIntegrarion {

	@Inject
	private CacheManager cacheManager;

	@Override
	public void execute(Object data) throws SystemException {
		MDC.put(RAClientConstants.COMMAND_PROCES, this.getClass().getAnnotation(CommandDescription.class).name());

		String tenantCode = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_WEEKN_TENANT_CODE));
		String modelName = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_SOP_WEEKN_MODEL_NAME));
		String modelMajorVersion = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_SOP_WEEKN_MAJOR_VERSION));
		String modelMinorVersion = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_SOP_WEEKN_MINOR_VERSION));
		String authToken = String.valueOf(cacheManager.getAppParamValue(AppParameterConstant.APP_PARAM_PHH_WEEKN_AUTH_TOKEN));

		super.executeRACall((DPSopWeekNParamEntryInfo) data, tenantCode, modelName, modelMajorVersion, modelMinorVersion, authToken);
	}
}
