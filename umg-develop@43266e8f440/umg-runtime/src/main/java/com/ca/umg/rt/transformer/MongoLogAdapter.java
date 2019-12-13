package com.ca.umg.rt.transformer;

import static com.ca.umg.rt.util.ME2WaitingTimeUtil.getMe2WaitingTime;
import static com.ca.umg.rt.util.ME2WaitingTimeUtil.getModelExecutionTime;
import static com.ca.umg.rt.util.ME2WaitingTimeUtil.getModeletExecution;
import static com.ca.umg.rt.util.MessageVariables.ME2_RESPONSE;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHeaders;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.logging.appender.AppenderConstants;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.task.AbstractCallableTask;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.framework.core.util.TransactionDocumentPayload;
import com.ca.framework.core.util.TransactionIOPayload;
import com.ca.pool.Channel;
import com.ca.pool.TransactionMode;
import com.ca.pool.model.ExecutionEnvironment;
import com.ca.pool.model.TransactionCriteria;
import com.ca.umg.me2.util.ModelExecResponse;
import com.ca.umg.rt.batching.delegate.BatchingDelegate;
import com.ca.umg.rt.batching.entity.BatchRuntimeTransactionMapping;
import com.ca.umg.rt.batching.entity.BatchTransaction;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.core.flow.dao.MongoTransactionLogDAO;
import com.ca.umg.rt.core.flow.dao.TransactionLogDAO;
import com.ca.umg.rt.core.flow.entity.TransactionLog;
import com.ca.umg.rt.flows.container.EnvironmentVariables;
import com.ca.umg.rt.util.IOTransformerUtil;
import com.ca.umg.rt.util.MessageVariables;
import com.ca.umg.rt.util.TransactionPayload;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Created by repvenk on 2/29/2016.
 */
@SuppressWarnings("PMD")
public class MongoLogAdapter {

	private static final String JSON = ".json";

	private static final String SAN_PATH = "SAN_PATH";
	private static final String STATUS = "status";
	private static final String IO_TIMESTAMP = "IO_TIMESTAMP";

	private TransactionLogDAO transactionLogDAO;

	private MongoTransactionLogDAO mongoTransactionLogDAO;

	private MoveFileAdapter moveFileAdapter;

	private BatchingDelegate batchingDelegate;

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoLogAdapter.class);

	private ExecutorService executorService;

	public void init() {
		executorService = Executors.newCachedThreadPool();
	}

	public void destroy() {
		executorService.shutdownNow();
	}

	private class CallableLogRequestTask extends AbstractCallableTask<Void> {

		private final Map<String, Object> payload;
		private final Map<String, Object> tenantRequest;
		private final Map<String, Object> headers;
		private final Map<String, Object> modelRequest;
		private final String transactionId;
		private final boolean error;
		private final Map<String, Object> tenantResponse;
		private final String transactionMode;

		public CallableLogRequestTask(String tenantCode, Map<String, Object> payload, Map<String, Object> tenantRequest,
				Map<String, Object> headers, Map<String, Object> modelRequest, String transactionId, boolean error,
				Map<String, Object> tenantResponse, String transactionMode) {
			super(tenantCode);
			this.payload = payload;
			this.tenantRequest = tenantRequest;
			this.headers = headers;
			this.modelRequest = modelRequest;
			this.transactionId = transactionId;
			this.error = error;
			this.tenantResponse = tenantResponse;
			this.transactionMode = transactionMode;
		}

		@Override
		public Void call() throws SystemException {
			setupBeforeExecution();
			try {
				logInputTransactionToMongo(payload, tenantRequest, headers, modelRequest, transactionId, error,
						tenantResponse, transactionMode);
			} catch (Exception e) { // NOPMD
				LOGGER.error("Error occured while saving tenantRequest", e);
			}
			payload.remove(MessageVariables.TENANT_REQUEST);
			payload.remove(MessageVariables.RESULT);
			payload.remove(MessageVariables.MODEL_REQUEST);
			return null;
		}

		private void setupBeforeExecution() {
			setRequestContext();
		}
	}

	private class CallableLogResponseTask extends AbstractCallableTask<Void> {

		private final Map<String, Object> payload;
		private final Map<String, Object> headers;
		private final Map<String, Object> tenantResponse;
		private final Map<String, Object> modelResponse;
		private final String transactionId;
		private final String transactionMode;

		public CallableLogResponseTask(String tenantCode, Map<String, Object> payload, Map<String, Object> headers,
				Map<String, Object> tenantResponse, Map<String, Object> modelResponse, String transactionId,
				String transactionMode) {
			super(tenantCode);
			this.payload = payload;
			this.headers = headers;
			this.tenantResponse = tenantResponse;
			this.modelResponse = modelResponse;
			this.transactionId = transactionId;
			this.transactionMode = transactionMode;
		}

		@Override
		public Void call() throws SystemException {
			setupBeforeExecution();
			try {
				logOutputTransactionToMongo(payload, headers, tenantResponse, modelResponse, transactionId,
						transactionMode);
			} catch (Exception e) { // NOPMD
				LOGGER.error("Error occured while saving tenantResponse", e);
			}
			payload.remove(ME2_RESPONSE);
			payload.remove(MessageVariables.TENANT_RESPONSE);
			return null;
		}

		private void setupBeforeExecution() {
			setRequestContext();
		}
	}

	/**
	 * @param message
	 */
	public void log(Message<?> message) {
		Properties properties = new Properties();
		properties.put(RequestContext.TENANT_CODE, message.getHeaders().get("tenantCode"));
		RequestContext requestContext = new RequestContext(properties);
		MDC.put(AppenderConstants.MDC_TENANT_CODE, requestContext.getTenantCode());
		Map<String, Object> payload = (Map<String, Object>) message.getPayload();
		String transactionId = (String) (message.getHeaders().get(MessageVariables.UMG_TRANSACTION_ID) != null
				? message.getHeaders().get(MessageVariables.UMG_TRANSACTION_ID)
				: message.getHeaders().get(MessageVariables.MESSAGE_ID));
		String transactionMode = MessageVariables.TRAN_ONLINE;
		boolean fileHandle = Boolean.FALSE;
		String channel = (String) message.getHeaders().get(MessageVariables.CHANNEL);

		Map<String, Object> payloadRequest = (Map<String, Object>) message.getPayload();
		Map<String, Object> tenantRequestHeader = null;
		if (payloadRequest != null) {
			tenantRequestHeader = (Map<String, Object>) payloadRequest.get("tenantRequestHeader");
		}
		if (StringUtils.isNotBlank((String) message.getHeaders().get(MessageVariables.FILE_NAME_HEADER))
				|| tenantRequestHeader != null && tenantRequestHeader.get(MessageVariables.FILE_NAME) != null) {
			transactionMode = MessageVariables.TRAN_BULK;
			fileHandle = Boolean.TRUE;
		} else if (tenantRequestHeader != null && tenantRequestHeader.get(MessageVariables.TRAN_MODE) != null
				&& TransactionMode.BULK.getMode()
						.equals((String) tenantRequestHeader.get(MessageVariables.TRAN_MODE))) {
			transactionMode = MessageVariables.TRAN_BULK;

		} else if (tenantRequestHeader != null && tenantRequestHeader.get("batchId") != null) {
			transactionMode = TransactionMode.BATCH.getMode();

		}

		try {
			switch ((String) message.getHeaders().get("MONGO_ACTION")) {
			case "TENANT_AND_MODEL_REQUEST_SAVE":
				/*
				 * This case initiated once mapping input transformation is complete. model
				 * result from MappingInputTransformer, model request from ModelInputTransformer
				 * and request from the gateway will be removed once they are persisted into
				 * Mongo
				 */
				handleRequest(message, payload, transactionId, transactionMode, fileHandle);
				break;
			case "TENANT_AND_MODEL_RESPONSE_SAVE":
				/*
				 * This case initiated once model output transformation is complete. me2Response
				 * from Me2Executor, tenant response from ModelOutputTransformer will be removed
				 * once they are persisted into Mongo
				 */
				handleResponse(message, payload, transactionId, transactionMode, fileHandle, channel);
				break;
			case "ERROR":
				handleError(message, payload, transactionId, transactionMode, channel);
				break;
			default:
				LOGGER.error(
						"No switch case action available for " + (String) message.getHeaders().get("MONGO_ACTION"));
				break;
			}
		} catch (Exception e) { // NOPMD
			LOGGER.error("Logging exception", e);
		}
	}

	private void handleResponse(Message<?> message, Map<String, Object> payload, String transactionId,
			String transactionMode, boolean fileHandle, String channel) {
		Map<String, Object> tenantResponse = (Map<String, Object>) payload.get(MessageVariables.TENANT_RESPONSE);
		Map<String, Object> modelResponse = ((ModelExecResponse<Map<String, Object>>) payload.get(ME2_RESPONSE))
				.getResponse();

		try {
			logOutputTransactionToMySQL(message.getHeaders(), payload, modelResponse, transactionId, tenantResponse,
					Boolean.FALSE, transactionMode);
		} catch (Exception e) { // NOPMD
			LOGGER.error("Exception occurred while saving response to MySql", e);
		}
		try {
			// save tenant-response with all data to output folder,
			// dont save model-output and save tenant-response with only metadata to mongo
			if (StringUtils.isNotBlank((String) message.getHeaders().get(MessageVariables.FILE_NAME_HEADER))) {
				// set modelIdentifier in tenant response and model response for TO and MO
				if (null != payload && null != payload.get(MessageVariables.TENANT_REQUEST_HEADER) 
						&& null != tenantResponse && null !=tenantResponse.get(MessageVariables.HEADER)) {
					String modelIdentifier = (String) ((Map<String, Object>) payload
							.get(MessageVariables.TENANT_REQUEST_HEADER)).get(EnvironmentVariables.MODEL_CHECKSUM);
					((Map<String, Object>)tenantResponse.get(MessageVariables.HEADER)).put(EnvironmentVariables.MODEL_CHECKSUM, modelIdentifier);
				}
				getMoveFileAdapter().delInprogsFileAndWriteResponse(
						(String) message.getHeaders().get(MessageVariables.FILE_NAME_HEADER),
						(String) message.getHeaders().get(SAN_PATH), tenantResponse, Boolean.TRUE, channel);
				updateProcessForBulk(message, transactionId, tenantResponse, transactionMode);
			} else if (TransactionMode.BULK.getMode().equals(transactionMode)) {
				updateProcessForBulkHttp(message, transactionId, tenantResponse, transactionMode);
			}

		} catch (SystemException | BusinessException e) {
			LOGGER.error("Error occured in MongoLogAdapter::log method of switch case-TENANT_AND_MODEL_RESPONSE_SAVE "
					+ "during the saving of tenant response " + e.getCode() + " " + e.getLocalizedMessage(), e);
		}
		try {
			if (fileHandle) {
				executorService.submit(new CallableLogResponseTask(MDC.get(AppenderConstants.MDC_TENANT_CODE), payload,
						message.getHeaders(), tenantResponse, modelResponse, transactionId, transactionMode));
				/*
				 * logOutputTransactionToMongo(payload, message.getHeaders(), tenantResponse,
				 * null, transactionId, transactionMode);
				 */
			} else {
				executorService.submit(new CallableLogResponseTask(MDC.get(AppenderConstants.MDC_TENANT_CODE), payload,
						message.getHeaders(), tenantResponse, modelResponse, transactionId, transactionMode));
				/*
				 * logOutputTransactionToMongo(payload, message.getHeaders(), tenantResponse,
				 * modelResponse, transactionId, transactionMode);
				 */
			}
		} catch (Exception e) { // NOPMD
			LOGGER.error("Exception occurred while saving response to Mongo", e);
		}
		/*
		 * payload.remove(MessageVariables.ME2_RESPONSE);
		 * payload.remove(MessageVariables.TENANT_RESPONSE);
		 */
	}

	private void updateProcessForBulk(Message<?> message, String transactionId, Map<String, Object> tenantResponse,
			String transactionMode) throws SystemException, BusinessException {
		Map<String, Object> tntResponseOnlyMetadata;
		tntResponseOnlyMetadata = getTntResponseMetadata(tenantResponse);
		if (tntResponseOnlyMetadata != null) {
			tenantResponse.put(MessageVariables.DATA, tntResponseOnlyMetadata);
		} else {
			tenantResponse.put(MessageVariables.DATA, new HashMap<String, Object>());
		}
		createTranForBulk(message, transactionId, TransactionStatus.PROCESSED.getStatus(), tntResponseOnlyMetadata,
				transactionMode);
	}

	private void updateProcessForBulkHttp(Message<?> message, String transactionId, Map<String, Object> tenantResponse,
			String transactionMode) throws SystemException, BusinessException {
		Map<String, Object> tntResponseOnlyMetadata = null;
		if (tenantResponse != null && tenantResponse.get(MessageVariables.DATA) != null
				&& ((Map<String, Object>) tenantResponse.get(MessageVariables.DATA))
						.get(MessageVariables.OUTPUT_FOLDER) != null) {
			Map<String, Object> tntResponsedata = (Map<String, Object>) tenantResponse.get(MessageVariables.DATA);
			Map<String, Object> tntExecMetaOutput = (Map<String, Object>) ((Map<String, Object>) tntResponsedata
					.get(MessageVariables.OUTPUT_FOLDER)).get("execmeta_output");
			if (MapUtils.isNotEmpty(tntExecMetaOutput)) {
				tntResponseOnlyMetadata = (Map<String, Object>) tntExecMetaOutput.get("metadata");
			}
		}
		createTranForBulk(message, transactionId, TransactionStatus.PROCESSED.getStatus(), tntResponseOnlyMetadata,
				transactionMode);
	}

	private void handleRequest(Message<?> message, Map<String, Object> payload, String transactionId,
			String transactionMode, boolean fileHandle) {

		Long runAsOfDate = (Long) ((Map<String, Object>) payload.get("request")).get("TESTDATE_MILLIS");
		final ModelExecResponse<Map<String, Object>> me2Response = (ModelExecResponse<Map<String, Object>>) payload
				.get(ME2_RESPONSE);
		Map<String, Object> modelRequest = (Map<String, Object>) payload.get(MessageVariables.MODEL_REQUEST);
		try {
			logInputTransactionToMySQL(message.getHeaders(), transactionId,
					(Map<String, Object>) payload.get(MessageVariables.TENANT_REQUEST),
					(Map<String, Object>) payload.get(MessageVariables.TENANT_RESPONSE), runAsOfDate, Boolean.FALSE,
					transactionMode, me2Response, modelRequest);
		} catch (Exception e) { // NOPMD
			LOGGER.error("Exception occurred while saving request to MySql", e);
		}

		Map<String, Object> tenantRequest = (Map<String, Object>) payload.get(MessageVariables.TENANT_REQUEST);

		// remove data part of tenant-request and save to mongo, dont save model-request
		if (StringUtils.isNotBlank((String) message.getHeaders().get(MessageVariables.FILE_NAME_HEADER))) {
			tenantRequest.put(MessageVariables.DATA, new HashMap<String, Object>());
		}
		try {
			if (fileHandle) {
				executorService.submit(
						new CallableLogRequestTask(MDC.get(AppenderConstants.MDC_TENANT_CODE), payload, tenantRequest,
								message.getHeaders(), null, transactionId, Boolean.FALSE, null, transactionMode));
				/*
				 * logInputTransactionToMongo(payload, tenantRequest, message.getHeaders(),
				 * null, transactionId, Boolean.FALSE, null, transactionMode);
				 */
			} else {
				executorService.submit(new CallableLogRequestTask(MDC.get(AppenderConstants.MDC_TENANT_CODE), payload,
						tenantRequest, message.getHeaders(), modelRequest, transactionId, Boolean.FALSE, null,
						transactionMode));
				/*
				 * logInputTransactionToMongo(payload, tenantRequest, message.getHeaders(),
				 * modelRequest, transactionId, Boolean.FALSE, null, transactionMode);
				 */
			}
		} catch (Exception e) { // NOPMD
			LOGGER.error("Exception occurred while saving request to Mongo", e);
		}
		/*
		 * payload.remove(MessageVariables.TENANT_REQUEST);
		 * payload.remove(MessageVariables.RESULT);
		 * payload.remove(MessageVariables.MODEL_REQUEST);
		 */
	}

	private void handleError(Message<?> message, Map<String, Object> payload, String transactionId,
			String transactionMode, String channel) {
		Map<String, Object> tntResponseOnlyMetadataErr = null;
		Map<String, Object> tenantRequestError = (Map<String, Object>) payload.get(MessageVariables.TENANT_REQUEST);
		Map<String, Object> modelRequestError = (Map<String, Object>) payload.get(MessageVariables.MODEL_REQUEST);
		final ModelExecResponse<Map<String, Object>> me2Response = (ModelExecResponse<Map<String, Object>>) payload
				.get(ME2_RESPONSE);
		
		         // set modelIdentifier in tenant request header in case of any error this will used to the modelIdentifier in TO and MO while logging the error in mongo
				if (null != payload && null != payload.get(MessageVariables.TENANT_REQUEST_HEADER) && null != payload.get(MessageVariables.MODEL_REQUEST_STRING)) {
					
					String headerInfo = (String) (payload
							.get(MessageVariables.MODEL_REQUEST_STRING));
					try {
						Map<String,Object> headerInfoMap =  ConversionUtil.convertJson(headerInfo, Map.class);
					    ((Map<String, Object>)payload.get(MessageVariables.TENANT_REQUEST_HEADER)).put(EnvironmentVariables.MODEL_CHECKSUM,((Map<String,Object>) headerInfoMap.get(MessageVariables.HEADER_INFO)).get(EnvironmentVariables.MODEL_CHECKSUM));
					} catch (SystemException e) {
						LOGGER.error("Error occured in MongoLogAdapter "
								+ "during convertion of headerInfo " + e.getCode() + " "
								+ e.getLocalizedMessage(), e);
					}
					
				}
		if (!StringUtils.equals((String) message.getHeaders().get("INSERT_REQUEST"), "SUCCESS")) {
			logInputTransactionToMySQL(message.getHeaders(), transactionId,
					(Map<String, Object>) payload.get(MessageVariables.TENANT_REQUEST),
					(Map<String, Object>) payload.get(MessageVariables.TENANT_RESPONSE),
					(Long) ((Map<String, Object>) payload.get("request")).get("TESTDATE_MILLIS"), Boolean.TRUE,
					transactionMode, me2Response, modelRequestError);

			// error in model request transformer remove tenant request's data and dont save
			// model request
			if (StringUtils.isNotBlank((String) message.getHeaders().get(MessageVariables.FILE_NAME_HEADER))) {
				if (tenantRequestError != null) {
					tenantRequestError.put(MessageVariables.DATA, new HashMap<String, Object>());
				}
				// transactionMode = MessageVariables.TRAN_BULK;
				executorService.submit(new CallableLogRequestTask(MDC.get(AppenderConstants.MDC_TENANT_CODE), payload,
						tenantRequestError, message.getHeaders(), null, transactionId, Boolean.TRUE,
						(Map<String, Object>) payload.get(MessageVariables.TENANT_RESPONSE), transactionMode));
				/*
				 * logInputTransactionToMongo(payload, tenantRequestError, message.getHeaders(),
				 * null, transactionId, Boolean.TRUE, (Map<String,
				 * Object>)payload.get(MessageVariables.TENANT_RESPONSE), transactionMode);
				 */
			} else {
				executorService.submit(new CallableLogRequestTask(MDC.get(AppenderConstants.MDC_TENANT_CODE), payload,
						tenantRequestError, message.getHeaders(), modelRequestError, transactionId, Boolean.TRUE,
						(Map<String, Object>) payload.get(MessageVariables.TENANT_RESPONSE), transactionMode));
				/*
				 * logInputTransactionToMongo(payload, tenantRequestError, message.getHeaders(),
				 * modelRequestError, transactionId, Boolean.TRUE, (Map<String,
				 * Object>)payload.get(MessageVariables.TENANT_RESPONSE), transactionMode);
				 */
			}
		} else if (StringUtils.equals((String) message.getHeaders().get("INSERT_REQUEST"), "SUCCESS")
				&& !StringUtils.equals((String) message.getHeaders().get("INSERT_RESPONSE"), "SUCCESS")) {
			handleErrorForBulk(message, payload, transactionId, transactionMode, channel);
		}
		try {
			if (StringUtils.isNotBlank((String) message.getHeaders().get(MessageVariables.FILE_NAME_HEADER))) {
				getMoveFileAdapter().delInprogsFileAndWriteResponse(
						(String) message.getHeaders().get(MessageVariables.FILE_NAME_HEADER),
						(String) message.getHeaders().get(MessageVariables.SAN_PATH),
						(Map<String, Object>) payload.get(MessageVariables.TENANT_RESPONSE), Boolean.FALSE, channel);
				createTranForBulk(message, transactionId, TransactionStatus.ERROR.getStatus(),
						tntResponseOnlyMetadataErr, transactionMode);
			} else if (TransactionMode.BULK.getMode().equals(transactionMode)) {
				Map<String, Object> tenantResponse = (Map<String, Object>) payload
						.get(MessageVariables.TENANT_RESPONSE);
				tntResponseOnlyMetadataErr = getTntResponseMetadata(tenantResponse);
				if (tntResponseOnlyMetadataErr != null) {
					tenantResponse.put(MessageVariables.DATA, tntResponseOnlyMetadataErr);
				}
				createTranForBulk(message, transactionId, TransactionStatus.ERROR.getStatus(),
						tntResponseOnlyMetadataErr, transactionMode);

			}
		} catch (SystemException | BusinessException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Error occured in MongoLogAdapter::log method of switch case-Error "
					+ "during the deletion of file from inprogress and writing response " + e.getCode() + " "
					+ e.getLocalizedMessage(), e);
		}
	}

	private void handleErrorForBulk(Message<?> message, Map<String, Object> payload, String transactionId,
			String transactionMode, String channel) {
		Map<String, Object> tntResponseError = (Map<String, Object>) payload.get(MessageVariables.TENANT_RESPONSE);
		Map<String, Object> modelResponseError = payload.get(ME2_RESPONSE) != null
				? ((ModelExecResponse<Map<String, Object>>) payload.get(ME2_RESPONSE)).getResponse()
				: null;

		logOutputTransactionToMySQL(message.getHeaders(), payload, modelResponseError, transactionId,
				(Map<String, Object>) payload.get(MessageVariables.TENANT_RESPONSE), Boolean.TRUE, transactionMode);

		// error in model response transformation/modelet error store model-response
		// and tenant-response as file, tenant-response with only header and metadata to
		// mongo
		if (StringUtils.isNotBlank((String) message.getHeaders().get(MessageVariables.FILE_NAME_HEADER))
				|| transactionMode.equals(TransactionMode.BULK.getMode())) {
			Map<String, Object> tntResponseOnlyMetadataErr = getTntResponseMetadata(tntResponseError);
			if (tntResponseOnlyMetadataErr != null) {
				tntResponseError.put(MessageVariables.DATA, tntResponseOnlyMetadataErr);
			}
			try {
				if (modelResponseError != null && !modelResponseError.isEmpty() && StringUtils
						.isNotBlank((String) message.getHeaders().get(MessageVariables.FILE_NAME_HEADER))) {
					getMoveFileAdapter().createResponseFile(
							(String) message.getHeaders().get(MessageVariables.FILE_NAME_HEADER),
							(String) message.getHeaders().get(MessageVariables.SAN_PATH), modelResponseError,
							"ErrorModelResponse", channel);
				}
			} catch (SystemException e) {
				LOGGER.error("Error occured in MongoLogAdapter::log method of switch case-Error "
						+ "during the saving of tenant response " + e.getCode() + " " + e.getLocalizedMessage());
			}
			// transactionMode = MessageVariables.TRAN_BULK;
			executorService.submit(new CallableLogResponseTask(MDC.get(AppenderConstants.MDC_TENANT_CODE), payload,
					message.getHeaders(), tntResponseError, modelResponseError, transactionId, transactionMode));
			/*
			 * logOutputTransactionToMongo(payload, message.getHeaders(), tntResponseError,
			 * null, transactionId, transactionMode);
			 */
		} else {
			executorService.submit(new CallableLogResponseTask(MDC.get(AppenderConstants.MDC_TENANT_CODE), payload,
					message.getHeaders(), tntResponseError, modelResponseError, transactionId, transactionMode));
			/*
			 * logOutputTransactionToMongo(payload, message.getHeaders(), tntResponseError,
			 * modelResponseError, transactionId, transactionMode);
			 */
		}
	}

	private Map<String, Object> getTntResponseMetadata(Map<String, Object> tenantResponse) {
		Map<String, Object> tntResponseOnlyMetadata = null;
		if (tenantResponse != null && tenantResponse.get(MessageVariables.DATA) != null
				&& ((Map<String, Object>) tenantResponse.get(MessageVariables.DATA)).get("output") != null) {
			Map<String, Object> tntResponsedata = (Map<String, Object>) tenantResponse.get(MessageVariables.DATA);
			Map<String, Object> tntExecMetaOutput = (Map<String, Object>) ((Map<String, Object>) tntResponsedata
					.get("output")).get("execmeta_output");
			if (MapUtils.isNotEmpty(tntExecMetaOutput)) {
				tntResponseOnlyMetadata = (Map<String, Object>) tntExecMetaOutput.get("metadata");
				tenantResponse.put(MessageVariables.DATA, tntResponseOnlyMetadata);
			}
		}
		return tntResponseOnlyMetadata;
	}

	private Map<String, Object> getHeaderOnly(Map<String, Object> requestOrResponse) {
		Map<String, Object> headers = new HashMap<String, Object>();
		if (requestOrResponse != null) {
			if (requestOrResponse.get(MessageVariables.HEADER) != null) {
				headers.put(MessageVariables.HEADER, requestOrResponse.get(MessageVariables.HEADER));
			} else {
				headers.put(MessageVariables.HEADER, requestOrResponse.get(MessageVariables.HEADER_INFO));
			}
		}
		headers.put(MessageVariables.DATA, new HashMap());
		return headers;
	}

	private void createTranForBulk(Message message, String umgTransactionId, String status,
			Map<String, Object> tntResponseOnlyMetadata, String transactionMode)
			throws SystemException, BusinessException {
		MessageHeaders messageHeaders = message.getHeaders();
		Map<String, Object> payload = (Map<String, Object>) message.getPayload();
		Map<String, Object> tenantReqHdr = (Map<String, Object>) payload.get(MessageVariables.TENANT_REQUEST_HEADER);
		Integer tenantTranCount = (Integer) tenantReqHdr.get(MessageVariables.TENANT_TRAN_COUNT);
		String fileName = (String) messageHeaders.get(MessageVariables.FILE_NAME_HEADER);
		if (StringUtils.isNotEmpty(fileName) || TransactionMode.BULK.getMode().equals(transactionMode)) {
			BatchTransaction batchTransaction = batchingDelegate
					.getBatch((String) messageHeaders.get(MessageVariables.BATCH_ID));
			if (batchTransaction != null) {
				if (tntResponseOnlyMetadata != null) {
					// Map<String,Object> metaData =
					// ((Map<String,Object>)tntResponseOnlyMetadata.get("metadata"));
					Integer success_ct = 0;
					Integer failure_ct = 0;
					if (tntResponseOnlyMetadata.get("success_ct") != null) {
						if (tntResponseOnlyMetadata.get("success_ct") instanceof Double) {
							success_ct = ((Double) tntResponseOnlyMetadata.get("success_ct")).intValue();
						} else if (tntResponseOnlyMetadata.get("success_ct") instanceof Integer) {
							success_ct = (Integer) tntResponseOnlyMetadata.get("success_ct");
						}
					}
					if (tntResponseOnlyMetadata.get("failure_ct") != null) {
						if (tntResponseOnlyMetadata.get("failure_ct") instanceof Double) {
							failure_ct = ((Double) tntResponseOnlyMetadata.get("failure_ct")).intValue();
						} else if (tntResponseOnlyMetadata.get("failure_ct") instanceof Integer) {
							failure_ct = (Integer) tntResponseOnlyMetadata.get("failure_ct");
						}
					}
					LOGGER.error("batchingDelegate is===" + batchingDelegate + " tenantTranCount ===" + tenantTranCount
							+ " success_ct===" + success_ct + " failure_ct===" + failure_ct + " status===" + status);

					batchingDelegate.updateBatch(batchTransaction.getId(), tenantTranCount, success_ct, failure_ct,
							status);
				} else {
					batchingDelegate.updateBatch(batchTransaction.getId(), 0, 0, 0, status);
				}
				if (fileName != null) {
					String outputFileName = null;
					if (StringUtils.equals(status, TransactionStatus.PROCESSED.getStatus())) {
						outputFileName = getOutputFileName(fileName);
					} else {
						outputFileName = getOutputErrorFileName(fileName);
					}
					batchingDelegate.updateBatchOutputFile(batchTransaction.getId(), outputFileName);
				}
				createEntryInBatchTranMapping(batchTransaction.getId(), umgTransactionId);
			}
		}
	}

	private String getOutputErrorFileName(String fileName) {
		String fileNameWithoutExtn = StringUtils.substringBeforeLast(fileName, FrameworkConstant.DOT);
		StringBuffer stringBuffer = new StringBuffer(fileNameWithoutExtn);
		stringBuffer.append(FrameworkConstant.HYPHEN).append(FrameworkConstant.ERROR).append(FrameworkConstant.DOT)
				.append(StringUtils.substringAfterLast(fileName, FrameworkConstant.DOT));
		return stringBuffer.toString();
	}

	private String getOutputFileName(String fileName) {
		String fileNameWithoutExtn = StringUtils.substringBeforeLast(fileName, FrameworkConstant.DOT);
		StringBuffer stringBuffer = new StringBuffer(fileNameWithoutExtn);
		stringBuffer.append(FrameworkConstant.HYPHEN).append(FrameworkConstant.OUTPUT).append(FrameworkConstant.DOT)
				.append(StringUtils.substringAfterLast(fileName, FrameworkConstant.DOT));
		return stringBuffer.toString();
	}

	private void createEntryInBatchTranMapping(String batchId, String umgTransactionId)
			throws SystemException, BusinessException {
		BatchRuntimeTransactionMapping runtimeTransactionMapping = new BatchRuntimeTransactionMapping();
		runtimeTransactionMapping.setBatchId(batchId);
		runtimeTransactionMapping.setTransactionId(umgTransactionId);
		runtimeTransactionMapping.setStatus(MessageVariables.FAILURE);
		runtimeTransactionMapping.setError("No Error!");
		batchingDelegate.addBatchTransactionMapping(runtimeTransactionMapping, Boolean.TRUE);
	}

	public Integer logInputTransactionToMySQL(Map<String, Object> headers, String transactionId,
			Map<String, Object> tenantRequest, Map<String, Object> tenantResponse, Long runAsOfDate, boolean error,
			String transactionMode, ModelExecResponse<Map<String, Object>> me2Response,
			Map<String, Object> modelRequest) {
		TransactionLog transactionLog = null;
		Integer result = null;
		transactionLog = new TransactionLog();
		transactionLog.setId(transactionId);
		transactionLog.setTransactionMode(transactionMode);
		String clientTransactionId = headers.get(MessageVariables.TRANSACTION_ID) != null
				? (String) headers.get(MessageVariables.TRANSACTION_ID)
				: "";
		transactionLog.setTransactionId(clientTransactionId);
		transactionLog.setLibraryName((String) headers.get(MessageVariables.MODEL_LIBRARY_VERSION_NAME));
		transactionLog.setModelName((String) headers.get(MessageVariables.MODEL_NAME));
		transactionLog.setMajorVersion((Integer) headers.get(MessageVariables.MAJOR_VERSION));
		transactionLog.setMinorVersion((Integer) headers.get(MessageVariables.MINOR_VERSION));
		transactionLog.setStatus(
				headers.get(STATUS) != null ? (String) headers.get(STATUS) : TransactionStatus.QUEUED.getStatus());
		transactionLog.setTenantInput(RuntimeConstants.STR_EMPTY);
		transactionLog.setTenantOutput(RuntimeConstants.STR_EMPTY);
		transactionLog.setModelInput(RuntimeConstants.STR_EMPTY);
		transactionLog.setModelOutput(RuntimeConstants.STR_EMPTY);
		transactionLog.setRunAsOfDate(runAsOfDate);
		transactionLog.setRuntimeStart((Long) headers.get(MessageVariables.RNTM_CALL_START));
		transactionLog.setIsTest((Integer) headers.get(MessageVariables.TEST));
		Map<String, Object> tenantRequestHdr = (Map<String, Object>) tenantRequest.get(MessageVariables.HEADER);
		setEnvironmentInfo(transactionLog, modelRequest);

		/*
		 * transactionLog.setCpuUsage((Double) headers.get(MessageVariables.CPU_USAGE));
		 * transactionLog.setFreeMemorey((String)
		 * headers.get(MessageVariables.FREE_MEMOREY));
		 */ // added to fix UMG-4500 Additional variables in Transaction header
			// transactionLog.setCreatedBy(RequestContext.getRequestContext().getTenantCode());
		if (tenantRequest != null) {

			if (tenantRequestHdr != null) {
				if (StringUtils.isNotBlank((String) tenantRequestHdr.get(MessageVariables.TRANSACTION_TYPE))) { // UMG-4611
																												// audhyabh
					String tranType = ((String) tenantRequestHdr.get(MessageVariables.TRANSACTION_TYPE)).trim();
					transactionLog.setIsTest(StringUtils.equalsIgnoreCase(tranType, MessageVariables.TEST) ? 1 : 0);
				}
				if (StringUtils.isNotBlank((String) tenantRequestHdr.get(MessageVariables.USER))) {
					transactionLog.setCreatedBy((String) tenantRequestHdr.get(MessageVariables.USER));
				}
				IOTransformerUtil.setAddOnValidations(transactionLog, tenantRequestHdr);

			}
		}

		if (StringUtils.isBlank(transactionLog.getCreatedBy())) {
			transactionLog.setCreatedBy(RequestContext.getRequestContext().getTenantCode());
		}

		if (error) {
			String errorCode = (String) ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER))
					.get(MessageVariables.ERROR_CODE);
			transactionLog.setErrorCode(errorCode);
			String errorDescription = (String) ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER))
					.get(MessageVariables.ERROR_MESSAGE);
			if (errorDescription != null) {
				transactionLog.setErrorDescription(errorDescription.getBytes());
			} else {
				transactionLog.setErrorDescription(null);
			}
		}
		if (me2Response != null) {
			transactionLog
					.setIpAndPort(String.valueOf(me2Response.getMemberHost() + ":" + me2Response.getMemberPort()));
			transactionLog.setPoolName(me2Response.getPoolName());
		}
		if (transactionLogDAO.checkTransactionExists(transactionId) == NumberUtils.INTEGER_ZERO) {
			try {
				result = transactionLogDAO.insertTransactionRequest(transactionLog);
			} catch (DuplicateKeyException e) {
				LOGGER.debug("Insertion done from transactionResponse");
			} catch (DataAccessException e) {
				LOGGER.error("Exception occured while inserting request details", e);
			}
		} else {
			result = transactionLogDAO.updateModelExecEnvName(transactionLog);

		}
		return result;
	}

	public Integer logOutputTransactionToMySQL(Map<String, Object> headers, Map<String, Object> payload,
			Map<String, Object> modelResponse, String transactionId, Map<String, Object> tenantResponse, boolean error,
			String transactionMode) {
		TransactionLog transactionLog = null;
		Integer result = null;
		transactionLog = new TransactionLog();
		transactionLog.setId(transactionId);
		transactionLog.setTransactionMode(transactionMode);
		String clientTransactionId = headers.get(MessageVariables.TRANSACTION_ID) != null
				? (String) headers.get(MessageVariables.TRANSACTION_ID)
				: "";
		transactionLog.setTransactionId(clientTransactionId);
		transactionLog.setLibraryName((String) headers.get(MessageVariables.MODEL_LIBRARY_VERSION_NAME));
		transactionLog.setModelName((String) headers.get(MessageVariables.MODEL_NAME));
		transactionLog.setMajorVersion((Integer) headers.get(MessageVariables.MAJOR_VERSION));
		transactionLog.setMinorVersion((Integer) headers.get(MessageVariables.MINOR_VERSION));
		transactionLog.setStatus(
				headers.get(STATUS) != null ? (String) headers.get(STATUS) : TransactionStatus.QUEUED.getStatus());
		transactionLog.setTenantInput(RuntimeConstants.STR_EMPTY);
		transactionLog.setTenantOutput(RuntimeConstants.STR_EMPTY);
		transactionLog.setModelInput(RuntimeConstants.STR_EMPTY);
		transactionLog.setModelOutput(RuntimeConstants.STR_EMPTY);
		transactionLog.setRuntimeEnd((Long) headers.get(MessageVariables.RNTM_CALL_END));
		transactionLog.setModelCallStart((Long) headers.get(MessageVariables.MODEL_CALL_START));
		transactionLog.setModelCallEnd((Long) headers.get(MessageVariables.MODEL_CALL_END));
		transactionLog.setIsTest((Integer) headers.get(MessageVariables.TEST));
		transactionLog.setModelExecutionTime(getModelExecutionTime(payload, headers));
		transactionLog.setModeletExecutionTime(getModeletExecution(payload, headers));
		transactionLog.setMe2WaitingTime(getMe2WaitingTime(payload, headers));
		if (modelResponse != null) {
			transactionLog.setCpuUsage((Double) modelResponse.get(MessageVariables.CPU_USAGE));
			transactionLog.setFreeMemory((String) modelResponse.get(MessageVariables.FREE_MEMORY));
			transactionLog.setCpuUsageAtStart((Double) modelResponse.get(MessageVariables.CPU_USAGE_AT_START));
			transactionLog.setFreeMemoryAtStart((String) modelResponse.get(MessageVariables.FREE_MEMORY_AT_START));
			transactionLog.setNoOfAttempts((int) modelResponse.get(MessageVariables.NO_OF_ATTEMPTS));
		}
		if (payload.get(MessageVariables.TENANT_REQUEST) != null
				&& ((Map<String, Object>) payload.get(MessageVariables.TENANT_REQUEST))
						.get(MessageVariables.HEADER) != null) {
			Map<String, Object> tntRqstHeader = (Map<String, Object>) ((Map<String, Object>) payload
					.get(MessageVariables.TENANT_REQUEST)).get(MessageVariables.HEADER);
			if (tntRqstHeader.get(MessageVariables.USER) != null) {
				transactionLog.setCreatedBy((String) tntRqstHeader.get(MessageVariables.USER));
			}
			IOTransformerUtil.setAddOnValidations(transactionLog, tntRqstHeader);
		}

		if (StringUtils.isBlank(transactionLog.getCreatedBy())) {
			transactionLog.setCreatedBy(RequestContext.getRequestContext().getTenantCode());
		}

		if (error) {
			String errorCode = (String) ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER))
					.get(MessageVariables.ERROR_CODE);
			transactionLog.setErrorCode(errorCode);
			String errorDescription = (String) ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER))
					.get(MessageVariables.ERROR_MESSAGE);
			if (errorDescription != null) {
				if (errorDescription.contains("Number of attempts")) {
					errorDescription = errorDescription.trim();
					int noOfAttempts = Integer.valueOf(
							errorDescription.substring(errorDescription.length() - 1, errorDescription.length()));
					transactionLog.setNoOfAttempts(noOfAttempts);
				}
				transactionLog.setErrorDescription(errorDescription.getBytes());
			} else {
				transactionLog.setErrorDescription(null);
			}
		}
		setModeletInfoForSql(transactionLog, payload);
		if (transactionLogDAO.checkTransactionExists(transactionId) > NumberUtils.INTEGER_ZERO) {
			transactionLogDAO.updateTransactionResponse(transactionLog);
		} else {
			try {
				result = transactionLogDAO.insertTransactionResponse(transactionLog);
			} catch (DuplicateKeyException e) {
				transactionLogDAO.updateTransactionResponse(transactionLog);
			} catch (DataAccessException e) {
				LOGGER.error("Exception occured while inserting/updating response details", e);
			}
		}
		return result;
	}

	private void logInputTransactionToMongo(Map<String, Object> payload, Map<String, Object> tenantRequest,
			Map<String, Object> headers, Map<String, Object> modelRequest, String transactionId, boolean error,
			Map<String, Object> tenantResponse, String transactionMode) throws SystemException {
		TransactionDocumentPayload transactionDocumentPayload = new TransactionDocumentPayload();
		TransactionIOPayload txnTIPayload = new TransactionIOPayload();
		TransactionIOPayload txnMIPayload = new TransactionIOPayload();
		TransactionIOPayload txnTOPayload = new TransactionIOPayload();
		TransactionIOPayload txnMOPayload = new TransactionIOPayload();
		txnTIPayload.setTransactionId(transactionId);
		txnMIPayload.setTransactionId(transactionId);
		txnTOPayload.setTransactionId(transactionId);
		txnMOPayload.setTransactionId(transactionId);
		transactionDocumentPayload.setTxnTIPayload(txnTIPayload);
		transactionDocumentPayload.setTxnTOPayload(txnTOPayload);
		transactionDocumentPayload.setTxnMOPayload(txnMOPayload);
		transactionDocumentPayload.setTxnMIPayload(txnMIPayload);

		TransactionPayload transactionPayload = new TransactionPayload();

		// added to fix UMG-4500 Additional variables in Transaction header
		// UMG-

		transactionPayload.setTest((Boolean) ((Integer) headers.get(MessageVariables.TEST) == 1 ? true : false));
		transactionPayload.setCreatedBy(headers.get("tenantCode") != null ? (String) headers.get("tenantCode")
				: RequestContext.getRequestContext().getTenantCode());
		// added this to fix umg-4251 to set versionCreationTest flag to true
		// if it is test transaction during version creation else the flag will be false
		removeChannelFromHeader(tenantRequest);
		setTransactionDocForIp(tenantRequest, headers, modelRequest, transactionDocumentPayload, transactionPayload);
		transactionPayload.setTransactionId(transactionId);
		transactionPayload.setTenantId(RequestContext.getRequestContext().getTenantCode());
		String clientTransactionId = headers.get(MessageVariables.TRANSACTION_ID) != null
				? (String) headers.get(MessageVariables.TRANSACTION_ID)
				: "";
		transactionPayload.setClientTransactionID(clientTransactionId);
		transactionPayload.setStatus(
				headers.get(STATUS) != null ? (String) headers.get(STATUS) : TransactionStatus.QUEUED.getStatus());
		transactionPayload.setLibraryName((String) headers.get(MessageVariables.MODEL_LIBRARY_VERSION_NAME));
		transactionPayload.setVersionName((String) headers.get(MessageVariables.MODEL_NAME));
		transactionPayload.setMajorVersion((Integer) headers.get(MessageVariables.MAJOR_VERSION));
		transactionPayload.setMinorVersion((Integer) headers.get(MessageVariables.MINOR_VERSION));
		LOGGER.error(
				"runAsOfDate is ==" + (Long) ((Map<String, Object>) payload.get("request")).get("TESTDATE_MILLIS"));
		transactionPayload.setRunAsOfDate((Long) ((Map<String, Object>) payload.get("request")).get("TESTDATE_MILLIS"));
		transactionPayload.setCreatedDate(System.currentTimeMillis());
		transactionPayload.setRuntimeCallStart((Long) headers.get(MessageVariables.RNTM_CALL_START));
		transactionPayload.setTransactionMode(transactionMode);

		/*
		 * transactionDocumentPayload.setCpuUsage((Double)
		 * headers.get(MessageVariables.CPU_USAGE));
		 * transactionDocumentPayload.setFreeMemorey((String)
		 * headers.get(MessageVariables.FREE_MEMOREY));
		 */
		// UMG-9466
		Map<String, Object> modelReq = (Map<String, Object>) payload.get(MessageVariables.MODEL_REQUEST);
		setEnvironmentInfo(transactionPayload, modelReq);
		if (payload.get("environment") != null) {
			payload.remove("environnment");
		}
		if (error) {
			txnTOPayload.setTxnIOPayload(tenantResponse);
			transactionDocumentPayload.setTxnTOPayload(txnTOPayload);
			transactionPayload.setErrorCode((String) ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER))
					.get(MessageVariables.ERROR_CODE));
			transactionPayload
					.setErrorDescription((String) ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER))
							.get(MessageVariables.ERROR_MESSAGE));
		}

		transactionPayload.setChannel(StringUtils.isNotBlank((String) headers.get(MessageVariables.CHANNEL))
				? (String) headers.get(MessageVariables.CHANNEL)
				: MessageVariables.ChannelType.HTTP.getChannel());
		if (headers.containsKey(MessageVariables.METRICS)) {
			transactionPayload.setMetricData((Map<String, Long>) headers.get(MessageVariables.METRICS));
		}
		if (transactionPayload.getErrorCode() != null
				&& TransactionMode.BULK.getMode().equalsIgnoreCase(transactionPayload.getTransactionMode())
				&& Channel.HTTP.getChannel().equals(transactionPayload.getChannel())
				&& transactionDocumentPayload.getTxnTOPayload().getTxnIOPayload() != null) {
			moveFileAdapter.saveInBulkHttpFolder(
					(String) headers.get(MessageVariables.TRANSACTION_ID) + PoolConstants.ENV_SEPERATOR
							+ (String) headers.get(IO_TIMESTAMP) + PoolConstants.ENV_SEPERATOR
							+ RuntimeConstants.TENANT_OUTPUT + JSON,
					(String) headers.get(SAN_PATH), tenantResponse, MessageVariables.OUTPUT_FOLDER);
			transactionDocumentPayload.getTxnTOPayload().setTxnIOPayload(null);
		}

		// Removing model checksum from transaction tenant input.
		if (null != transactionDocumentPayload && null != transactionDocumentPayload.getTxnTIPayload()
				&& null != transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload()) {
			Map<String, Object> tenantIpRequestHdr = (Map<String, Object>) transactionDocumentPayload.getTxnTIPayload()
					.getTxnIOPayload().get(MessageVariables.HEADER);
			tenantIpRequestHdr.remove(EnvironmentVariables.MODEL_CHECKSUM);
		}

		mongoTransactionLogDAO.upsertRequestTransactionLogToMongo(transactionPayload, transactionDocumentPayload);
	}

	private void logOutputTransactionToMongo(Map<String, Object> payload, Map<String, Object> headers,
			Map<String, Object> tenantResponse, Map<String, Object> modelResponse, String transactionId,
			String transactionMode) throws JsonParseException, JsonMappingException, IOException, SystemException {

		// set modelIdentifier in tenant response and model response for TO and MO
		if (null != payload && null != payload.get(MessageVariables.TENANT_REQUEST_HEADER)) {
			String modelIdentifier = (String) ((Map<String, Object>) payload
					.get(MessageVariables.TENANT_REQUEST_HEADER)).get(EnvironmentVariables.MODEL_CHECKSUM);
			if(null != tenantResponse && null !=tenantResponse.get(MessageVariables.HEADER)) {
				((Map<String, Object>)tenantResponse.get(MessageVariables.HEADER)).put(EnvironmentVariables.MODEL_CHECKSUM, modelIdentifier);

			}
		    if(null != modelResponse  && null !=modelResponse.get(MessageVariables.RESPONSE_HEADER_INFO)) {
				((Map<String, Object>)modelResponse.get(MessageVariables.RESPONSE_HEADER_INFO)).put(EnvironmentVariables.MODEL_CHECKSUM, modelIdentifier);
		    }		
		}

		TransactionDocumentPayload transactionDocumentPayload = new TransactionDocumentPayload();
		TransactionPayload transactionPayload = new TransactionPayload();
		transactionPayload.setTransactionId(transactionId);

		TransactionIOPayload txnTOPayload = new TransactionIOPayload();
		txnTOPayload.setTransactionId(transactionId);
		transactionDocumentPayload.setTxnTOPayload(txnTOPayload);

		TransactionIOPayload txnMOPayload = new TransactionIOPayload();
		txnMOPayload.setTransactionId(transactionId);
		transactionDocumentPayload.setTxnMOPayload(txnMOPayload);

		transactionPayload.setTenantId(RequestContext.getRequestContext().getTenantCode());
		String clientTransactionId = headers.get(MessageVariables.TRANSACTION_ID) != null
				? (String) headers.get(MessageVariables.TRANSACTION_ID)
				: "";
		transactionPayload.setClientTransactionID(clientTransactionId);

		LOGGER.error("Response status is :" + headers.get(STATUS));
		transactionPayload.setStatus(
				headers.get(STATUS) != null ? (String) headers.get(STATUS) : TransactionStatus.QUEUED.getStatus());
		String errorCode = (String) ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER))
				.get(MessageVariables.ERROR_CODE);
		transactionPayload.setErrorCode(errorCode);
		String errorDescription = (String) ((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER))
				.get(MessageVariables.ERROR_MESSAGE);
		if (errorDescription != null) {
			transactionPayload.setErrorDescription(errorDescription);
			if (errorDescription.contains("Number of attempts")) {
				errorDescription = errorDescription.trim();
				int noOfAttempts = Integer
						.valueOf(errorDescription.substring(errorDescription.length() - 1, errorDescription.length()));
				transactionPayload.setNoOfAttempts(noOfAttempts);
			}

		} else {
			transactionPayload.setErrorDescription(null);
		}
		Map<String, Object> tenantResponseHdr = (Map<String, Object>) tenantResponse.get(MessageVariables.HEADER);
		if ((!(Boolean) tenantResponseHdr.get(MessageVariables.SUCCESS))
				&& (!(tenantResponseHdr.get(MessageVariables.ERROR_CODE) != null
						&& ((String) tenantResponseHdr.get(MessageVariables.ERROR_CODE))
								.startsWith(RuntimeConstants.RVE_EXCEPTION)))) {
			((Map<String, Object>) tenantResponse.get(MessageVariables.HEADER)).put(MessageVariables.ERROR_MESSAGE,
					RuntimeConstants.GENERIC_ERROR_MESSAGE);
		}
		Map<String, Object> tenantRequestHeader = (Map<String, Object>) payload
				.get(MessageVariables.TENANT_REQUEST_HEADER);
		if (tenantRequestHeader != null) {
			if (tenantRequestHeader.containsKey(MessageVariables.STORE_RLOGS)) {
				Boolean storeRLogs = false;
				Object storeR = tenantRequestHeader.get(MessageVariables.STORE_RLOGS);
				if (storeR instanceof Boolean) {
					storeRLogs = (Boolean) tenantRequestHeader.get(MessageVariables.STORE_RLOGS);
				} else {
					if (storeR instanceof String && StringUtils.equalsIgnoreCase(storeR.toString(), "true")) {
						storeRLogs = true;
					}
				}

				if (!tenantResponseHdr.containsKey(MessageVariables.STORE_RLOGS)) {
					tenantResponseHdr.put(MessageVariables.STORE_RLOGS, storeR);
				}

				transactionPayload.setStoreRLogs(storeRLogs);
			} else {
				transactionPayload.setStoreRLogs(false);
			}
			setTransactionDocForOp(headers, tenantResponse, modelResponse, transactionDocumentPayload, errorCode,
					tenantRequestHeader, payload);
		}

		transactionPayload.setRuntimeCallEnd((Long) headers.get(MessageVariables.RNTM_CALL_END));
		transactionPayload.setModelCallStart((Long) headers.get(MessageVariables.MODEL_CALL_START));
		transactionPayload.setModelCallEnd((Long) headers.get(MessageVariables.MODEL_CALL_END));
		transactionPayload.setModelExecutionTime(getModelExecutionTime(payload, headers));
		transactionPayload.setModeletExecutionTime(getModeletExecution(payload, headers));
		transactionPayload.setMe2WaitingTime(getMe2WaitingTime(payload, headers));
		transactionPayload.setTransactionMode(transactionMode);
		if (tenantResponseHdr.get("payloadStorage") != null) {
			transactionPayload.setPayloadStorage((Boolean) tenantResponseHdr.get("payloadStorage"));
		}
		if (modelResponse != null) {
			transactionPayload.setCpuUsage((Double) modelResponse.get(MessageVariables.CPU_USAGE));
			transactionPayload.setFreeMemory((String) modelResponse.get(MessageVariables.FREE_MEMORY));
			transactionPayload.setCpuUsageAtStart((Double) modelResponse.get(MessageVariables.CPU_USAGE_AT_START));
			transactionPayload.setFreeMemoryAtStart((String) modelResponse.get(MessageVariables.FREE_MEMORY_AT_START));
			transactionPayload.setNoOfAttempts((int) modelResponse.get(MessageVariables.NO_OF_ATTEMPTS));
		}

		setModeletInfo(transactionPayload, payload);
		if (headers.containsKey(MessageVariables.METRICS)) {
			transactionPayload.setMetricData((Map<String, Long>) headers.get(MessageVariables.METRICS));
		}
		mongoTransactionLogDAO.upsertResponseTransactionLogToMongo(transactionPayload, transactionDocumentPayload);
	}

	private void setTransactionDocForOp(Map<String, Object> headers, Map<String, Object> tenantResponse,
			Map<String, Object> modelResponse, TransactionDocumentPayload transactionDocumentPayload, String errorCode,
			Map<String, Object> tenantRequestHeader, Map<String, Object> payload) throws SystemException,
			org.codehaus.jackson.JsonParseException, org.codehaus.jackson.map.JsonMappingException, IOException {
		if (tenantRequestHeader.get(MessageVariables.PAYLOAD_STORAGE) == null
				|| (Boolean) tenantRequestHeader.get(MessageVariables.PAYLOAD_STORAGE)) {
			if (TransactionMode.BULK.getMode().equals(tenantRequestHeader.get(MessageVariables.TRAN_MODE))
					&& headers.get(MessageVariables.FILE_NAME_HEADER) == null) {
				moveFileAdapter.saveInBulkHttpFolder(
						tenantRequestHeader.get(MessageVariables.TRANSACTION_ID) + PoolConstants.ENV_SEPERATOR
								+ (String) headers.get(IO_TIMESTAMP) + PoolConstants.ENV_SEPERATOR
								+ RuntimeConstants.TENANT_OUTPUT + JSON,
						(String) headers.get(SAN_PATH), tenantResponse, MessageVariables.OUTPUT_FOLDER);
				if (errorCode == null) {
					moveFileAdapter.deleteInBulkHttpFolder(
							tenantRequestHeader.get(MessageVariables.TRANSACTION_ID) + PoolConstants.ENV_SEPERATOR
									+ (String) headers.get(IO_TIMESTAMP) + PoolConstants.ENV_SEPERATOR
									+ RuntimeConstants.MODEL_INPUT + JSON,
							(String) headers.get(SAN_PATH), MessageVariables.ARCHIVE_FOLDER);
				} else {
					moveFileAdapter.saveInBulkHttpFolder(
							tenantRequestHeader.get(MessageVariables.TRANSACTION_ID) + PoolConstants.ENV_SEPERATOR
									+ (String) headers.get(IO_TIMESTAMP) + PoolConstants.ENV_SEPERATOR
									+ RuntimeConstants.MODEL_OUTPUT + JSON,
							(String) headers.get(SAN_PATH), modelResponse, MessageVariables.OUTPUT_FOLDER);
				}
			} else {
				transactionDocumentPayload.getTxnMOPayload().setTxnIOPayload(modelResponse);
				transactionDocumentPayload.getTxnTOPayload().setTxnIOPayload(tenantResponse);
				/*
				 * if(errorCode != null &&
				 * payload.get(MessageVariables.MODEL_REQUEST_STRING)!=null){ ObjectMapper
				 * mapper = new ObjectMapper();
				 * transactionDocumentPayload.getTxnMIPayload().setTxnIOPayload(mapper.readValue
				 * ((String)payload.get( MessageVariables.MODEL_REQUEST_STRING), Map.class));
				 * LOGGER.error("modelInput is :"
				 * +transactionDocumentPayload.getTxnMIPayload().getTxnIOPayload()); }
				 */
			}
		} else if (tenantRequestHeader.get(MessageVariables.PAYLOAD_STORAGE) == null
				|| !(Boolean) tenantRequestHeader.get(MessageVariables.PAYLOAD_STORAGE)) {
			if (errorCode != null
					&& TransactionMode.BULK.getMode().equals(tenantRequestHeader.get(MessageVariables.TRAN_MODE))
					&& headers.get(MessageVariables.FILE_NAME_HEADER) == null) {
				moveFileAdapter.saveInBulkHttpFolder(
						tenantRequestHeader.get(MessageVariables.TRANSACTION_ID) + PoolConstants.ENV_SEPERATOR
								+ (String) headers.get(IO_TIMESTAMP) + PoolConstants.ENV_SEPERATOR
								+ RuntimeConstants.TENANT_OUTPUT + JSON,
						(String) headers.get(SAN_PATH), tenantResponse, MessageVariables.OUTPUT_FOLDER);
				moveFileAdapter.saveInBulkHttpFolder(
						tenantRequestHeader.get(MessageVariables.TRANSACTION_ID) + PoolConstants.ENV_SEPERATOR
								+ (String) headers.get(IO_TIMESTAMP) + PoolConstants.ENV_SEPERATOR
								+ RuntimeConstants.MODEL_OUTPUT + JSON,
						(String) headers.get(SAN_PATH), modelResponse, MessageVariables.OUTPUT_FOLDER);
			} else if (errorCode == null
					&& TransactionMode.BULK.getMode().equals(tenantRequestHeader.get(MessageVariables.TRAN_MODE))
					&& headers.get(MessageVariables.FILE_NAME_HEADER) == null) {
				getTntResponseMetadata(tenantResponse);
				moveFileAdapter.saveInBulkHttpFolder(
						tenantRequestHeader.get(MessageVariables.TRANSACTION_ID) + PoolConstants.ENV_SEPERATOR
								+ (String) headers.get(IO_TIMESTAMP) + PoolConstants.ENV_SEPERATOR
								+ RuntimeConstants.TENANT_OUTPUT + JSON,
						(String) headers.get(SAN_PATH), tenantResponse, MessageVariables.OUTPUT_FOLDER);
				tenantRequestHeader.put(MessageVariables.DATA, new HashMap<>());
				if (transactionDocumentPayload != null && transactionDocumentPayload.getTxnTIPayload() != null) {
					transactionDocumentPayload.getTxnTIPayload().setTxnIOPayload(tenantRequestHeader);
				}
				moveFileAdapter.saveInBulkHttpFolder(
						tenantRequestHeader.get(MessageVariables.TRANSACTION_ID) + PoolConstants.ENV_SEPERATOR
								+ (String) headers.get(IO_TIMESTAMP) + PoolConstants.ENV_SEPERATOR
								+ RuntimeConstants.TENANT_INPUT + JSON,
						(String) headers.get(SAN_PATH), tenantRequestHeader, MessageVariables.ARCHIVE_FOLDER);
				moveFileAdapter.deleteInBulkHttpFolder(
						tenantRequestHeader.get(MessageVariables.TRANSACTION_ID) + PoolConstants.ENV_SEPERATOR
								+ (String) headers.get(IO_TIMESTAMP) + PoolConstants.ENV_SEPERATOR
								+ RuntimeConstants.MODEL_INPUT + JSON,
						(String) headers.get(SAN_PATH), MessageVariables.ARCHIVE_FOLDER);
			} else {
				if (errorCode != null) {
					if (TransactionMode.BULK.getMode().equals(tenantRequestHeader.get(MessageVariables.TRAN_MODE))
							&& headers.get(MessageVariables.FILE_NAME_HEADER) != null) {
						transactionDocumentPayload.getTxnMOPayload().setTxnIOPayload(getHeaderOnly(modelResponse));
						transactionDocumentPayload.getTxnTOPayload().setTxnIOPayload(getHeaderOnly(tenantResponse));
					} else {
						transactionDocumentPayload.getTxnMOPayload().setTxnIOPayload(modelResponse);
						transactionDocumentPayload.getTxnTOPayload().setTxnIOPayload(tenantResponse);
					}
				} else {
					modelResponse.put("payload", new HashMap<>());
					transactionDocumentPayload.getTxnMOPayload().setTxnIOPayload(modelResponse);
					transactionDocumentPayload.getTxnTOPayload().setTxnIOPayload(getHeaderOnly(tenantResponse));

					// when payload is false, adding header to tenantInput
					Map<String, Object> header = new HashMap<String, Object>();
					Set<String> keySet = tenantRequestHeader.keySet();
					for (String key : keySet) {
						if (!StringUtils.equals(key, MessageVariables.DATA)) {
							header.put(key, tenantRequestHeader.get(key));
						}
					}
					TransactionIOPayload txnTIPayload = new TransactionIOPayload();
					txnTIPayload.setTransactionId((String) tenantRequestHeader.get(MessageVariables.TRANSACTION_ID));
					Map<String, Object> tenantIP = new HashMap<String, Object>();
					tenantIP.put("header", header);
					tenantIP.put(MessageVariables.DATA, new HashMap<>());
					txnTIPayload.setTxnIOPayload(tenantIP);
					transactionDocumentPayload.setTxnTIPayload(txnTIPayload);
				}
			}
		}
	}

	private void setTransactionDocForIp(Map<String, Object> tenantRequest, Map<String, Object> headers,
			Map<String, Object> modelRequest, TransactionDocumentPayload transactionDocumentPayload,
			TransactionPayload transactionPayload) throws SystemException {
		if (tenantRequest != null) {
			Map<String, Object> tenantRequestHdr = (Map<String, Object>) tenantRequest.get(MessageVariables.HEADER);
			if (tenantRequestHdr != null) {
				tenantRequestHdr.remove(MessageVariables.VERSION_CREATION_TEST);
				tenantRequest.put(MessageVariables.HEADER, tenantRequestHdr);

				if (tenantRequestHdr.containsKey(MessageVariables.STORE_RLOGS)) {
					Boolean storeRLogs = false;
					Object storeR = tenantRequestHdr.get(MessageVariables.STORE_RLOGS);
					if (storeR instanceof Boolean) {
						storeRLogs = (Boolean) tenantRequestHdr.get(MessageVariables.STORE_RLOGS);
					} else {
						if (storeR instanceof String && StringUtils.equalsIgnoreCase(storeR.toString(), "true")) {
							storeRLogs = true;
						}
					}
					transactionPayload.setStoreRLogs(storeRLogs);
				} else {
					transactionPayload.setStoreRLogs(false);
				}
				// added to fix UMG-4500 Additional variables in Transaction header
				if (StringUtils.isNotBlank((String) tenantRequestHdr.get(MessageVariables.TRANSACTION_TYPE))) { // UMG-4611
																												// audhyabh
					String tranType = ((String) tenantRequestHdr.get(MessageVariables.TRANSACTION_TYPE)).trim();
					transactionPayload
							.setTest(StringUtils.equalsIgnoreCase(tranType, MessageVariables.TEST) ? Boolean.TRUE
									: Boolean.FALSE);
				}
				if (StringUtils.isNotBlank((String) tenantRequestHdr.get(MessageVariables.USER))) {
					transactionPayload.setCreatedBy((String) tenantRequestHdr.get(MessageVariables.USER));
				}
				// UMG-4697
				if (StringUtils.isNotBlank((String) tenantRequestHdr.get("executionGroup"))) {
					transactionPayload.setExecutionGroup((String) tenantRequestHdr.get("executionGroup"));
				} else {
					transactionPayload.setExecutionGroup(MessageVariables.DEFAULT_EXECUTION_GROUP);
				}
				if (tenantRequestHdr.get(MessageVariables.PAYLOAD_STORAGE) != null) {
					transactionPayload
							.setPayloadStorage((Boolean) tenantRequestHdr.get(MessageVariables.PAYLOAD_STORAGE));
				}
				if (tenantRequestHdr != null && tenantRequestHdr.get(MessageVariables.FILE_NAME) == null
						&& headers != null && headers.get(MessageVariables.FILE_NAME_HEADER) != null) {
					tenantRequestHdr.put(MessageVariables.FILE_NAME, headers.get(MessageVariables.FILE_NAME_HEADER));
				}
				if (TransactionMode.BULK.getMode().equals(tenantRequestHdr.get(MessageVariables.TRAN_MODE))
						&& headers.get(MessageVariables.FILE_NAME_HEADER) == null) {
					moveFileAdapter.saveInBulkHttpFolder(
							tenantRequestHdr.get(MessageVariables.TRANSACTION_ID) + PoolConstants.ENV_SEPERATOR
									+ (String) headers.get(IO_TIMESTAMP) + PoolConstants.ENV_SEPERATOR
									+ RuntimeConstants.TENANT_INPUT + JSON,
							(String) headers.get(SAN_PATH), tenantRequest, MessageVariables.ARCHIVE_FOLDER);
					moveFileAdapter.saveInBulkHttpFolder(
							tenantRequestHdr.get(MessageVariables.TRANSACTION_ID) + PoolConstants.ENV_SEPERATOR
									+ (String) headers.get(IO_TIMESTAMP) + PoolConstants.ENV_SEPERATOR
									+ RuntimeConstants.MODEL_INPUT + JSON,
							(String) headers.get(SAN_PATH), modelRequest, MessageVariables.ARCHIVE_FOLDER);
					transactionPayload.setBulkOnlineTimeStamp((String) headers.get(IO_TIMESTAMP));
				} else {
					if (transactionDocumentPayload.getTxnMIPayload() != null) {
						transactionDocumentPayload.getTxnMIPayload().setTxnIOPayload(modelRequest);
					}
					if (transactionDocumentPayload.getTxnTIPayload() != null) {
						transactionDocumentPayload.getTxnTIPayload().setTxnIOPayload(tenantRequest);
					}
				}
			}
		}

	}

	private void setEnvironmentInfo(final TransactionPayload transactionPayload,
			final Map<String, Object> modelRequest) {
		TransactionCriteria transactionCriteria = null;
		if (modelRequest != null && modelRequest.get("headerInfo") != null) {
			final Map<Object, Object> headerInfo = (Map<Object, Object>) modelRequest.get("headerInfo");
			if (headerInfo != null && headerInfo.get("transactionCriteria") != null) {
				transactionCriteria = (TransactionCriteria) headerInfo.get("transactionCriteria");
			}
		}

		if (transactionCriteria != null) {
			transactionPayload.setModellingEnv(transactionCriteria.getExecutionLanguage() + RuntimeConstants.CHAR_HYPHEN
					+ transactionCriteria.getExecutionLanguageVersion());
			transactionPayload.setExecEnv(ExecutionEnvironment
					.getEnvironment(transactionCriteria.getExecutionEnvironment()).getEnvironment());
		}
	}

	private void setEnvironmentInfo(final TransactionLog transactionLog, final Map<String, Object> modelRequest) {
		TransactionCriteria transactionCriteria = null;
		if (modelRequest != null && modelRequest.get("headerInfo") != null) {
			final Map<Object, Object> headerInfo = (Map<Object, Object>) modelRequest.get("headerInfo");
			if (headerInfo != null && headerInfo.get("transactionCriteria") != null) {
				transactionCriteria = (TransactionCriteria) headerInfo.get("transactionCriteria");
			}
		}

		if (transactionCriteria != null) {
			transactionLog.setModelExecEnvName(transactionCriteria.getExecutionLanguage() + RuntimeConstants.CHAR_HYPHEN
					+ transactionCriteria.getExecutionLanguageVersion());

		}
	}

	private void setModeletInfo(final TransactionPayload transactionPayload, final Map<String, Object> payload) {
		if (payload != null) {
			final ModelExecResponse<Map<String, Object>> me2Response = (ModelExecResponse<Map<String, Object>>) payload
					.get(ME2_RESPONSE);
			if (me2Response != null) {
				transactionPayload.setModeletServerHost(me2Response.getHost());
				transactionPayload.setModeletServerPort(me2Response.getPort());
				transactionPayload.setModeletServerMemberHost(me2Response.getMemberHost());
				transactionPayload.setModeletServerMemberPort(me2Response.getMemberPort());
				transactionPayload.setModeletPoolName(me2Response.getPoolName());
				transactionPayload.setModeletPoolCriteria(me2Response.getPoolCriteria());
				transactionPayload.setModeletServerType(me2Response.getServerType());
				transactionPayload.setModeletServerContextPath(me2Response.getContextPath());
				transactionPayload.setrServePort(me2Response.getrServePort());
			}
		}
	}

	public TransactionLogDAO getTransactionLogDAO() {
		return transactionLogDAO;
	}

	public void setTransactionLogDAO(TransactionLogDAO transactionLogDAO) {
		this.transactionLogDAO = transactionLogDAO;
	}

	public MongoTransactionLogDAO getMongoTransactionLogDAO() {
		return mongoTransactionLogDAO;
	}

	public void setMongoTransactionLogDAO(MongoTransactionLogDAO mongoTransactionLogDAO) {
		this.mongoTransactionLogDAO = mongoTransactionLogDAO;
	}

	public MoveFileAdapter getMoveFileAdapter() {
		return moveFileAdapter;
	}

	public void setMoveFileAdapter(MoveFileAdapter moveFileAdapter) {
		this.moveFileAdapter = moveFileAdapter;
	}

	public BatchingDelegate getBatchingDelegate() {
		return batchingDelegate;
	}

	public void setBatchingDelegate(BatchingDelegate batchingDelegate) {
		this.batchingDelegate = batchingDelegate;
	}

	private void setModeletInfoForSql(TransactionLog transactionLog, final Map<String, Object> payload) {
		if (payload != null) {
			final ModelExecResponse<Map<String, Object>> me2Response = (ModelExecResponse<Map<String, Object>>) payload
					.get(ME2_RESPONSE);
			if (me2Response != null) {
				transactionLog.setIpAndPort(String.valueOf(me2Response.getHost() + ":" + me2Response.getPort()));
				transactionLog.setPoolName(me2Response.getPoolName());
				transactionLog.setrServePort(me2Response.getrServePort());
			}
		}
	}

	private void removeChannelFromHeader(Map<String, Object> request) {
		if (!request.isEmpty()) {
			Object requestHeader = request.get(MessageVariables.HEADER);
			if (requestHeader instanceof Map) {
				Map<String, Object> requestMap = (Map<String, Object>) requestHeader;
				if (requestMap.containsKey(MessageVariables.CHANNEL)) {
					requestMap.remove(MessageVariables.CHANNEL);
				}
			}
		}
	}

}
