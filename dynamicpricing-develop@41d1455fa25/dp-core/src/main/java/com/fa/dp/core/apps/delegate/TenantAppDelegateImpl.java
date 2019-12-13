package com.fa.dp.core.apps.delegate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.core.apps.bo.TenantAppBO;
import com.fa.dp.core.apps.domain.AdGroupAppMapping;
import com.fa.dp.core.apps.domain.TenantApp;
import com.fa.dp.core.apps.domain.TenantAppParam;
import com.fa.dp.core.apps.info.AdGroupAppMappingInfo;
import com.fa.dp.core.apps.info.RATenantAppParamsRequest;
import com.fa.dp.core.apps.info.TenantAppInfo;
import com.fa.dp.core.apps.info.TenantAppParamInfo;
import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.systemparam.adapter.ClassificationAdapter;
import com.fa.dp.core.systemparam.util.AppType;
import com.fa.dp.core.systemparam.util.ClassificationType;
import com.fa.dp.core.util.DateConversionUtil;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

@Named
public class TenantAppDelegateImpl extends AbstractDelegate implements TenantAppDelegate {

	private static final Logger LOGGER = LoggerFactory.getLogger(TenantAppDelegateImpl.class);
	
	private static final String NA = "NA";

	@Inject
	private TenantAppBO tenantAppBO;
	
	@Autowired
	private ApplicationContext applicationContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fa.ra.client.core.apps.delegate.TenantAppDelegate#
	 * getTenantAppsByADGroupsSortedByPriorty(java.util.List)
	 */
	@Override
	public List<TenantAppInfo> getTenantAppsByADGroupsSortedByPriorty(List<String> adGroups) throws SystemException {
		List<TenantApp> tenantApps = tenantAppBO.getTenantAppsByADGroupsSortedByPriorty(adGroups);
		return convertToList(tenantApps, TenantAppInfo.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fa.ra.client.core.apps.delegate.TenantAppDelegate#getAllAppParams()
	 */
	@Override
	public List<TenantAppParamInfo> getAllAppParams() {
		List<TenantAppParam> appParams = tenantAppBO.getAllAppParams();
		return convertToList(appParams, TenantAppParamInfo.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fa.ra.client.core.apps.delegate.TenantAppDelegate#getAllTenantApps()
	 */
	@Override
	public List<TenantAppInfo> getAllTenantApps() throws SystemException {
		List<TenantApp> tenantApps = tenantAppBO.getAllTenantApps();
		return convertToList(tenantApps, TenantAppInfo.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fa.ra.client.core.apps.delegate.TenantAppDelegate#
	 * getAllAdGroupAppMappings()
	 */
	@Override
	public List<AdGroupAppMappingInfo> getAllAdGroupAppMappings() throws SystemException {
		List<AdGroupAppMapping> adGroupAppMappings = tenantAppBO.getAllAdGroupAppMapping();
		return convertToList(adGroupAppMappings, AdGroupAppMappingInfo.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fa.ra.client.core.apps.delegate.TenantAppDelegate#
	 * updateTenantAppParams(com.fa.ra.client.core.apps.info.
	 * RATenantAppParamsRequest)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public RATenantAppParamsRequest updateTenantAppParams(RATenantAppParamsRequest request, String updatedBy)
			throws SystemException, IOException {
		String code = AppType.DPA.getAppCode();
		List<TenantAppParam> liAppParams = tenantAppBO.getTenantAppParams(code, request.getType());
		if (CollectionUtils.isEmpty(liAppParams)) {
			LOGGER.info("App Params are not created for " + code.toUpperCase());
			throw new SystemException(CoreExceptionCodes.SYSPAR002, new Object[] { code.toUpperCase() });
		}
		TenantApp teApp = tenantAppBO.getTenantApp(code);
		LOGGER.info("TenantApp for given code :" + teApp.getName());
		// create the mapping editable parameter as key and value as given in
		// AppParameterConstant
		
		ClassificationAdapter classificationAdapter = (ClassificationAdapter) applicationContext.getBean(ClassificationType.getBeanType(request.getType()));
		Map<String, String> params = classificationAdapter.appParamsToJsonConverter(request);
		List<TenantAppParam> list = new ArrayList<>(params.entrySet().size());
		for(Entry<String, String> entry: params.entrySet()) {
			TenantAppParam teAppParam = new TenantAppParam();
			teAppParam.setAttrKey(entry.getKey());
			teAppParam.setAttrValue(entry.getValue());
			teAppParam.setTenantApp(teApp);
			list.add(teAppParam);
		}

		tenantAppBO.updateAppParams(list, updatedBy);
		request.setLastUpdatedBy(updatedBy);
		DateTime date = new DateTime();
		request.setLastUpdatedOn(DateConversionUtil.convertUtcToEstTimeZone(date).toString());
		return request;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fa.ra.client.core.apps.delegate.TenantAppDelegate#getTenantAppParam(
	 * java.lang.Long)
	 */
	@Override
	public RATenantAppParamsRequest getTenantAppParam(String type) throws SystemException, IOException {
		RATenantAppParamsRequest req = null;
		String code = AppType.DPA.getAppCode();
		List<TenantAppParam> appParams = tenantAppBO.getTenantAppParams(code, type);
		if (CollectionUtils.isEmpty(appParams)) {
			LOGGER.error("No Record Found for App Param " + code);
			throw new SystemException(CoreExceptionCodes.SYSPAR002, new Object[] { code.toUpperCase() });
		} else {
			// convert the key value parameter to form parameter for ui
			req = listToObjectConverter(appParams, code, type);
		}
		return req;
	}

	/**
	 * @param appParams
	 * @param code
	 * @param type
	 * @return
	 * @throws IOException
	 * @throws SystemException
	 */
	private RATenantAppParamsRequest listToObjectConverter(List<TenantAppParam> appParams, String code, String type)
			throws IOException, SystemException {
		LOGGER.info("Enter:: TenantAppDelegateImpl ::listToObjectConverter method");
		LOGGER.info("code : type ==>> " + code + " : " + type);
		RATenantAppParamsRequest req = null;

		/*JSONObject obj = new JSONObject();
		ClassificationAdapter classificationAdapter = (ClassificationAdapter) applicationContext.getBean(ClassificationType.getBeanType(type));
		for (TenantAppParam param : appParams) {
			try {
				obj.put(param.getAttrKey(), param.getAttrValue());
			} catch (org.springframework.boot.configurationprocessor.json.JSONException e) {
				LOGGER.error("problem with the JSON API ", e);
			}
		}
		req = classificationAdapter.jsonToAppParamConverter(obj);
		TenantAppParam param = appParams.get(0);
		req.setLastUpdatedBy(Objects.isNull(param.getLastModifiedBy())?NA:param.getLastModifiedBy());
		req.setLastUpdatedOn(Objects.isNull(param.getLastModifiedDate())?NA:DateConversionUtil.convertUtcToEstTimeZone(param.getLastModifiedDate()).toString());
		req.setType(type);*/
		LOGGER.info("Exit:: TenantAppDelegateImpl ::listToObjectConverter method");
		return req;
	}

}