/**
 *
 */
package com.fa.dp.core.rest;

import com.fa.dp.business.validation.input.info.DPProcessParamInfo;
import com.fa.dp.business.weekn.input.info.DPProcessWeekNParamInfo;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.rest.info.TenantIODefinition;
import com.fa.dp.core.transaction.domain.Transaction;

import java.util.List;
import java.util.Map;

/**
 *
 *
 */
public interface RAClient {

	/**
	 * @param modelName
	 * @param modelVersion
	 * @param status
	 * @param transactionId
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws SystemException
	 */
	public List<Transaction> fetchRATransaction(String modelName, String modelVersion, String status, String transactionId, String fromDate,
			String toDate) throws SystemException;

	/**
	 * Fetches model definition for the given model name and version
	 *
	 * @param modelName
	 * @param modelVersion
	 * @return TenantIODefinition
	 * @throws SystemException
	 */
	public TenantIODefinition getModelDefinition(String modelName, String modelVersion) throws SystemException;

	/**
	 * @param raRequest
	 * @return Map
	 * @throws SystemException
	 */
	public Map executeModel(String modelName, String modelVersion, Map<String, Object> raRequest) throws SystemException;

	/**
	 * @param tenantCode
	 * @param modelName
	 * @param modelVersion
	 * @param authToken
	 * @param raRequest
	 * @return
	 * @throws SystemException
	 */
	public Map executeWeek0DPAModel(String tenantCode, String modelName, String modelVersion, String authToken, Map<String, Object> raRequest,
			DPProcessParamInfo info) throws SystemException;

	public Map executeWeekNDPAModel(String tenantCode, String modelName, String modelVersion, String authToken, Map<String, Object> raRequest,
			DPProcessWeekNParamInfo dpProcessWeekNParamInfo) throws SystemException;

	Map executeSopWeekNDPAModel(String tenantCode, String modelName, String modelVersion, String authToken, Map<String, Object> raRequest)
			throws SystemException;

	public List<Transaction> fetchRATransaction(String modelName, String transactionType, String fromDate, String endDate);

}
