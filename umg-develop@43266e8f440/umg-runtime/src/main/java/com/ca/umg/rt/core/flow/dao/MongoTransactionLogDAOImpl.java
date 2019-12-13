package com.ca.umg.rt.core.flow.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bson.BsonSerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.ca.framework.core.batch.TransactionStatus;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.framework.core.util.TransactionDocumentPayload;
import com.ca.pool.TransactionMode;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;
import com.ca.umg.rt.flows.container.EnvironmentVariables;
import com.ca.umg.rt.transformer.MoveFileAdapter;
import com.ca.umg.rt.util.MessageVariables;
import com.ca.umg.rt.util.TransactionPayload;
import com.mongodb.DuplicateKeyException;

@SuppressWarnings("PMD")
@Named
public class MongoTransactionLogDAOImpl implements MongoTransactionLogDAO {


	private static final Logger LOGGER = LoggerFactory.getLogger(MongoTransactionLogDAOImpl.class);

	@Inject
	private MongoTemplate mongoTemplate;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	@Inject
	private MoveFileAdapter moveFileAdapter;

	private static final String EXCEEDS_MAX_DOC_SIZE = "larger than MaxDocumentSize";
	private static final String STATUS = "status";
	private static final String TRANSACTION_ID = "transactionId";
	private static final String JSON = ".json";
	@Override
	public void insertTransactionLogToMongo(final TransactionPayload transactionPayload, TransactionDocumentPayload transactionDocumentPayload) {
		LOGGER.error("Inserting to mongo for transaction : {}", transactionPayload.getTransactionId());
		String tenantCode = RequestContext.getRequestContext().getTenantCode();
		if(transactionDocumentPayload.getTxnTIPayload()!=null){
			Map<String,Object> tenantInput = new HashMap<String,Object>();
			tenantInput.put(TRANSACTION_ID, transactionDocumentPayload.getTxnTIPayload().getTransactionId());
			transactionDocumentPayload=removeAdditionalHeaderParam(transactionDocumentPayload);
			tenantInput.put("tenantInput", transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload());
			mongoTemplate.insert(tenantInput, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.TENANTINPUT+FrameworkConstant.DOCUMENTS);
		}
		if(transactionDocumentPayload.getTxnTOPayload()!=null){
			Map<String,Object> tenantOutput = new HashMap<String,Object>();
			tenantOutput.put(TRANSACTION_ID, transactionDocumentPayload.getTxnTOPayload().getTransactionId());
			tenantOutput.put("tenantOutput", transactionDocumentPayload.getTxnTOPayload().getTxnIOPayload());
			mongoTemplate.insert(tenantOutput, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.TENANTOUTPUT+FrameworkConstant.DOCUMENTS);
		}
		if(transactionDocumentPayload.getTxnMIPayload()!=null){
			Map<String,Object> modelInput = new HashMap<String,Object>();
			modelInput.put(TRANSACTION_ID, transactionDocumentPayload.getTxnMIPayload().getTransactionId());
			modelInput.put("modelInput", transactionDocumentPayload.getTxnMIPayload().getTxnIOPayload());
			mongoTemplate.insert(modelInput, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.MODELINPUT+FrameworkConstant.DOCUMENTS);
		}
		if(transactionDocumentPayload.getTxnMOPayload()!=null){
			Map<String,Object> modelOutput = new HashMap<String,Object>();
			modelOutput.put(TRANSACTION_ID, transactionDocumentPayload.getTxnMOPayload().getTransactionId());
			modelOutput.put("modelInput", transactionDocumentPayload.getTxnMOPayload().getTxnIOPayload());
			mongoTemplate.insert(modelOutput, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.MODELOUTPUT+FrameworkConstant.DOCUMENTS);        	
		}    

		mongoTemplate.insert(transactionPayload, tenantCode + FrameworkConstant.DOCUMENTS);     

	}

	@Override
	public void upsertRequestTransactionLogToMongo(final TransactionPayload transactionPayload, TransactionDocumentPayload transactionDocumentPayload) throws SystemException {
		LOGGER.info("Txn Status for txn Id :" + transactionPayload.getTransactionId() + " is :"
				+ transactionPayload.getStatus());
		String tenantCode = RequestContext.getRequestContext().getTenantCode();
		LOGGER.error("runAsOfDate is :"+transactionPayload.getRunAsOfDate());		
		Query query = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
		Update update = new Update()
		.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())
		.set("clientTransactionID", transactionPayload.getClientTransactionID())
		.set("tenantId", transactionPayload.getTenantId())		
		.set("libraryName", transactionPayload.getLibraryName())
		.set("versionName", transactionPayload.getVersionName())
		.set("majorVersion", transactionPayload.getMajorVersion())
		.set("minorVersion", transactionPayload.getMinorVersion())
		.set("runAsOfDate", transactionPayload.getRunAsOfDate())
		.set("test", transactionPayload.getTest())
		.set("createdBy", transactionPayload.getCreatedBy())
		.set("createdDate", transactionPayload.getCreatedDate())
		.set("runtimeCallStart", transactionPayload.getRuntimeCallStart())
		.set("executionGroup", transactionPayload.getExecutionGroup())
		.set("cpuUsage" , transactionPayload.getCpuUsage())
		.set("freeMemory", transactionPayload.getFreeMemory())
		.set("cpuUsageAtStart" , transactionPayload.getCpuUsageAtStart())
		.set("freeMemoryAtStart", transactionPayload.getFreeMemoryAtStart())
		.set("bulkOnlineTimeStamp", transactionPayload.getBulkOnlineTimeStamp())
		.set("channel", transactionPayload.getChannel())
		.set("storeRLogs", transactionPayload.isStoreRLogs());

		if(transactionPayload.getExecEnv() != null) {
			update.set("execEnv", transactionPayload.getExecEnv());
		}
		if(transactionPayload.getModellingEnv() != null) {
			update.set("modellingEnv", transactionPayload.getModellingEnv());
		}
		setCorrectStatus(transactionPayload, tenantCode, update);
		if(transactionPayload.getMetricData() != null) {
			update.set("metrics", transactionPayload.getMetricData());
		}
		if(transactionPayload.getPayloadStorage() != null) {
			update.set(MessageVariables.PAYLOAD_STORAGE, transactionPayload.getPayloadStorage());
		}		

		if (transactionPayload.getTransactionMode() != null) {
			update.set("transactionMode", transactionPayload.getTransactionMode());
		}		
		try{
			mongoTemplate.upsert(query, update, tenantCode + FrameworkConstant.DOCUMENTS);
			LOGGER.info("Updated status:"+update.getUpdateObject().get("status"));
		} catch (DuplicateKeyException dke) {// NOPMD
			LOGGER.error("GOT Duplicate Key error while updating tenantRequest, hence updating collection");
			try {
				setCorrectStatus(transactionPayload, tenantCode, update);
				mongoTemplate.updateFirst(query, update, tenantCode + "_documents");
			} catch (Exception e) {//NOPMD
				LOGGER.error("GOT error while updating updating collection", e);
			}
		} catch (org.springframework.dao.DuplicateKeyException dke) {
			LOGGER.error("GOT Duplicate Key error while updating tenantRequest, hence updating collection");
			try {
				setCorrectStatus(transactionPayload, tenantCode, update);
				mongoTemplate.updateFirst(query, update, tenantCode + "_documents");
			} catch (Exception e) {//NOPMD
				LOGGER.error("GOT error while updating updating collection", e);
			}    	  
		}	

		if(transactionDocumentPayload.getTxnTIPayload()!=null && transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload()!=null){

//						FileInputStream fis = null;
//						String input = null;
//						File file = new File("C:\\Users\\mandasuj\\Desktop\\Mysql.zip");
//						try {
//							fis = new FileInputStream(file);
//						} catch (FileNotFoundException e) {
//						}
//						try {
//							input = new String(IOUtils.toByteArray(fis));
//						} catch (IOException e) {
//						}

			Query queryForTIPayload = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
			transactionDocumentPayload=removeAdditionalHeaderParam(transactionDocumentPayload);
			// Removing model checksum from transaction tenant input.
			if (null != transactionDocumentPayload && null != transactionDocumentPayload.getTxnTIPayload()
					&& null != transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload()) {
				Map<String, Object> tenantIpRequestHdr = (Map<String, Object>) transactionDocumentPayload.getTxnTIPayload()
						.getTxnIOPayload().get(MessageVariables.HEADER);
				tenantIpRequestHdr.remove(EnvironmentVariables.MODEL_CHECKSUM);
			}
			Update updateForTIPayload = new Update()				
			.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())
//								.set(MessageVariables.TENANT_INPUT, input);
			.set(MessageVariables.TENANT_INPUT, transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload());	
			try{
				mongoTemplate.upsert(queryForTIPayload, updateForTIPayload, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.TENANTINPUT+FrameworkConstant.DOCUMENTS);
			}catch(BsonSerializationException ex){
				if(StringUtils.contains(ex.getMessage(), EXCEEDS_MAX_DOC_SIZE)){
					updateForTIPayload.set(MessageVariables.TENANT_INPUT, null).set(MessageVariables.ALTERNATE_STORAGE, Boolean.TRUE);
					mongoTemplate.upsert(queryForTIPayload, updateForTIPayload, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.TENANTINPUT+FrameworkConstant.DOCUMENTS);
					Map<String, Object> tenantInputDoc = new HashMap<String,Object>();
					tenantInputDoc.put(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId());
					tenantInputDoc.put(MessageVariables.TENANT_INPUT, transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload());
					moveFileAdapter.deleteFromFolder(transactionPayload.getTransactionId()+ PoolConstants.ENV_SEPERATOR + RuntimeConstants.TENANT_INPUT + JSON,systemParameterProvider
							.getParameter(SystemConstants.SAN_BASE), MessageVariables.DOCUMENTS_FOLDER);
					moveFileAdapter.saveInFolder(transactionPayload.getTransactionId()+ PoolConstants.ENV_SEPERATOR + RuntimeConstants.TENANT_INPUT + JSON,systemParameterProvider
							.getParameter(SystemConstants.SAN_BASE), tenantInputDoc, MessageVariables.DOCUMENTS_FOLDER);
				} else {
					Map<String,Object> errorMsg = new HashMap<String,Object>();	
					errorMsg.put("error", ex.getMessage());
					transactionDocumentPayload.getTxnTIPayload().setTxnIOPayload(errorMsg);
					Query queryForTIErrPayload = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
					Update updateForTIErrPayload = new Update()				
					.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())			
					.set("tenantInput", transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload());	
					mongoTemplate.upsert(queryForTIErrPayload, updateForTIErrPayload, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.TENANTINPUT+FrameworkConstant.DOCUMENTS);
				}
			}
		}

		if(transactionDocumentPayload.getTxnTOPayload()!=null && transactionDocumentPayload.getTxnTOPayload().getTxnIOPayload()!=null){
			Query queryForTOPayload = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
			Update updateForTOPayload = new Update()				
			.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())			
			.set(MessageVariables.TENANT_OUTPUT, transactionDocumentPayload.getTxnTOPayload().getTxnIOPayload());
			try{
				mongoTemplate.upsert(queryForTOPayload, updateForTOPayload, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.TENANTOUTPUT+FrameworkConstant.DOCUMENTS);	
			}catch(BsonSerializationException ex){
				if(StringUtils.contains(ex.getMessage(), EXCEEDS_MAX_DOC_SIZE)){
					updateForTOPayload.set(MessageVariables.TENANT_OUTPUT, null).set(MessageVariables.ALTERNATE_STORAGE, Boolean.TRUE);
					mongoTemplate.upsert(queryForTOPayload, updateForTOPayload, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.TENANTOUTPUT+FrameworkConstant.DOCUMENTS);	
					Map<String, Object> tenantOutputDoc = new HashMap<String,Object>();
					tenantOutputDoc.put(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId());
					tenantOutputDoc.put(MessageVariables.TENANT_OUTPUT, transactionDocumentPayload.getTxnTOPayload().getTxnIOPayload());
					moveFileAdapter.deleteFromFolder(transactionPayload.getTransactionId()+ PoolConstants.ENV_SEPERATOR + RuntimeConstants.TENANT_OUTPUT + JSON,systemParameterProvider
							.getParameter(SystemConstants.SAN_BASE), MessageVariables.DOCUMENTS_FOLDER);
					moveFileAdapter.saveInFolder(transactionPayload.getTransactionId()+ PoolConstants.ENV_SEPERATOR + RuntimeConstants.TENANT_OUTPUT + JSON,systemParameterProvider
							.getParameter(SystemConstants.SAN_BASE), tenantOutputDoc, MessageVariables.DOCUMENTS_FOLDER);
				} else {
					Map<String,Object> errorMsg = new HashMap<String,Object>();	
					errorMsg.put("error", ex.getMessage());
					transactionDocumentPayload.getTxnTOPayload().setTxnIOPayload(errorMsg);
					Query queryForTOErrPayload = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
					Update updateForTOErrPayload = new Update()				
					.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())			
					.set("tenantOutput", transactionDocumentPayload.getTxnTOPayload().getTxnIOPayload());
					mongoTemplate.upsert(queryForTOErrPayload, updateForTOErrPayload, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.TENANTOUTPUT+FrameworkConstant.DOCUMENTS);
				}
			}
		}

		if(transactionDocumentPayload.getTxnMIPayload()!=null && transactionDocumentPayload.getTxnMIPayload().getTxnIOPayload()!=null ){
			Query queryForMIPayload = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
			Update updateForMIPayload = new Update()				
			.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())			
			.set(MessageVariables.MODEL_INPUT, transactionDocumentPayload.getTxnMIPayload().getTxnIOPayload());
			try{
				mongoTemplate.upsert(queryForMIPayload, updateForMIPayload, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.MODELINPUT+FrameworkConstant.DOCUMENTS);
			}catch(BsonSerializationException ex){
				if(StringUtils.contains(ex.getMessage(), EXCEEDS_MAX_DOC_SIZE)){
					updateForMIPayload.set(MessageVariables.MODEL_INPUT, null).set(MessageVariables.ALTERNATE_STORAGE, Boolean.TRUE);
					mongoTemplate.upsert(queryForMIPayload, updateForMIPayload, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.MODELINPUT+FrameworkConstant.DOCUMENTS);
					Map<String, Object> modelInputDoc = new HashMap<String,Object>();
					modelInputDoc.put(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId());
					modelInputDoc.put(MessageVariables.MODEL_INPUT, transactionDocumentPayload.getTxnMIPayload().getTxnIOPayload());
					moveFileAdapter.deleteFromFolder(transactionPayload.getTransactionId()+ PoolConstants.ENV_SEPERATOR + RuntimeConstants.MODEL_INPUT + JSON,systemParameterProvider
							.getParameter(SystemConstants.SAN_BASE), MessageVariables.DOCUMENTS_FOLDER);
					moveFileAdapter.saveInFolder(transactionPayload.getTransactionId()+ PoolConstants.ENV_SEPERATOR + RuntimeConstants.MODEL_INPUT + JSON,systemParameterProvider
							.getParameter(SystemConstants.SAN_BASE), modelInputDoc, MessageVariables.DOCUMENTS_FOLDER);
				} else {
					Query queryForMIErrorPayload = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
					Map<String,Object> errorMsg = new HashMap<String,Object>();	
					errorMsg.put("error", ex.getMessage());					
					transactionDocumentPayload.getTxnMIPayload().setTxnIOPayload(errorMsg);
					Update updateForMIErrorPayload = new Update()				
					.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())			
					.set("modelInput", transactionDocumentPayload.getTxnMIPayload().getTxnIOPayload());
					mongoTemplate.upsert(queryForMIErrorPayload, updateForMIErrorPayload, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.MODELINPUT+FrameworkConstant.DOCUMENTS);
				}
			}
		}

		if(transactionDocumentPayload.getTxnMOPayload()!=null && transactionDocumentPayload.getTxnMOPayload().getTxnIOPayload()!=null){

			Query queryForMOPayload = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
			Update updateForMOPayload = new Update()				
			.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())			
			.set(MessageVariables.MODEL_OUTPUT, transactionDocumentPayload.getTxnMOPayload().getTxnIOPayload());
			try{
				mongoTemplate.upsert(queryForMOPayload, updateForMOPayload, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.MODELOUTPUT+FrameworkConstant.DOCUMENTS);
			}catch(BsonSerializationException ex){
				if(StringUtils.contains(ex.getMessage(), EXCEEDS_MAX_DOC_SIZE)){
					updateForMOPayload.set(MessageVariables.MODEL_OUTPUT, null).set(MessageVariables.ALTERNATE_STORAGE, Boolean.TRUE);
					mongoTemplate.upsert(queryForMOPayload, updateForMOPayload, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.MODELOUTPUT+FrameworkConstant.DOCUMENTS);
					Map<String, Object>	modelOutputDoc = new HashMap<String,Object>();
					modelOutputDoc.put(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId());
					modelOutputDoc.put(MessageVariables.MODEL_OUTPUT, transactionDocumentPayload.getTxnMOPayload().getTxnIOPayload());
					moveFileAdapter.deleteFromFolder(transactionPayload.getTransactionId()+ PoolConstants.ENV_SEPERATOR + RuntimeConstants.MODEL_OUTPUT + JSON,systemParameterProvider
							.getParameter(SystemConstants.SAN_BASE), MessageVariables.DOCUMENTS_FOLDER);
					moveFileAdapter.saveInFolder(transactionPayload.getTransactionId()+ PoolConstants.ENV_SEPERATOR + RuntimeConstants.MODEL_OUTPUT + JSON,systemParameterProvider
							.getParameter(SystemConstants.SAN_BASE), modelOutputDoc, MessageVariables.DOCUMENTS_FOLDER);
				} else {
					Map<String,Object> errorMsg = new HashMap<String,Object>();	
					errorMsg.put("error", ex.getMessage());
					transactionDocumentPayload.getTxnMIPayload().setTxnIOPayload(errorMsg);
					Query queryForMOErrPayload = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
					Update updateForMOErrPayload = new Update()				
					.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())			
					.set("modelOutput", transactionDocumentPayload.getTxnMOPayload().getTxnIOPayload());
					mongoTemplate.upsert(queryForMOErrPayload, updateForMOErrPayload, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.MODELOUTPUT+FrameworkConstant.DOCUMENTS);
				}
			}

		}
	}

	private void setCorrectStatus(final TransactionPayload transactionPayload, String tenantCode, Update update) {
		if (StringUtils.equalsIgnoreCase("ERROR", transactionPayload.getStatus())) {
			update.set("status", transactionPayload.getStatus())			
			.set("errorCode", transactionPayload.getErrorCode())
			.set("errorDescription", transactionPayload.getErrorDescription());
		} else {
			Query queryForStatus = new Query(
					Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
			TransactionPayload status = mongoTemplate.findOne(queryForStatus, TransactionPayload.class,
					tenantCode + FrameworkConstant.DOCUMENTS);
			LOGGER.info("Transaction status for txn ID :" + transactionPayload.getTransactionId() + " status :"
					+ transactionPayload.getStatus());
			if (status != null) {
				LOGGER.info("Inside mongo status for txn Id :" + transactionPayload.getTransactionId() + " status  :"
						+ status.getStatus());
				if (!(status != null && (StringUtils.equals(TransactionStatus.IN_EXECUTION.getStatus(), status.getStatus())
						|| StringUtils.equals(TransactionStatus.SUCCESS.getStatus(), status.getStatus()) ||StringUtils.equals(TransactionStatus.ERROR.getStatus(), status.getStatus())))) {
					LOGGER.info("Transaction status not In Execution and Success");
					update.set(STATUS, status.getStatus());
				}
			}else{
				TransactionPayload recheckStatus = mongoTemplate.findOne(queryForStatus, TransactionPayload.class,
						tenantCode + FrameworkConstant.DOCUMENTS);
				if(recheckStatus ==null)
				update.set(STATUS, transactionPayload.getStatus());            	
			}        

		}
	}

	@Override
	public void upsertResponseTransactionLogToMongo(final TransactionPayload transactionPayload,  TransactionDocumentPayload transactionDocumentPayload) throws SystemException {
		String tenantCode = RequestContext.getRequestContext().getTenantCode();
		Query query = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
		LOGGER.info("Txn Status for txn Id :" + transactionPayload.getTransactionId() + " status :"
				+ transactionPayload.getStatus());
		Update update = new Update().set(STATUS, transactionPayload.getStatus());	
		update.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())
		.set("runtimeCallEnd", transactionPayload.getRuntimeCallEnd());
		if(transactionPayload.getModelCallEnd() != null) {
			update.set("modelCallStart", transactionPayload.getModelCallStart());
		}
		if(transactionPayload.getModelCallEnd() != null) {
			update.set("modelCallEnd", transactionPayload.getModelCallEnd());
		}

		if (transactionPayload.getTransactionMode() != null) {
			update.set("transactionMode", transactionPayload.getTransactionMode());
		}

		if(transactionPayload.getModelExecutionTime() != NumberUtils.LONG_ZERO) {
			update.set("modelExecutionTime", transactionPayload.getModelExecutionTime());
		}
		if(transactionPayload.getModeletExecutionTime() != NumberUtils.LONG_ZERO) {
			update.set("modeletExecutionTime", transactionPayload.getModeletExecutionTime());
		}
		if(transactionPayload.getMe2WaitingTime() != NumberUtils.LONG_ZERO) {
			update.set("me2WaitingTime", transactionPayload.getMe2WaitingTime());
		}

		if(transactionPayload.getModeletServerHost() != null) {
			update.set("modeletServerHost", transactionPayload.getModeletServerHost());
		}
		if(transactionPayload.getModeletServerPort() != null) {
			update.set("modeletServerPort", transactionPayload.getModeletServerPort());
		}
		if(transactionPayload.getModeletServerMemberHost() != null) {
			update.set("modeletServerMemberHost", transactionPayload.getModeletServerMemberHost());
		}
		if(transactionPayload.getModeletServerMemberPort() != null) {
			update.set("modeletServerMemberPort", transactionPayload.getModeletServerMemberPort());
		}
		if(transactionPayload.getModeletPoolName() != null) {
			update.set("modeletPoolName", transactionPayload.getModeletPoolName());
		}
		if(transactionPayload.getModeletPoolCriteria() != null) {
			update.set("modeletPoolCriteria", transactionPayload.getModeletPoolCriteria());
		}
		if(transactionPayload.getModeletServerType() != null) {
			update.set("modeletServerType", transactionPayload.getModeletServerType());
		}
		if(transactionPayload.getModeletServerContextPath() != null) {
			update.set("modeletServerContextPath", transactionPayload.getModeletServerContextPath());
		}
		if (StringUtils.equalsIgnoreCase("ERROR", transactionPayload.getStatus())) {
			update.set("errorCode", transactionPayload.getErrorCode())
			.set("errorDescription", transactionPayload.getErrorDescription());
		}
		if(transactionPayload.getMetricData() != null) {
			update.set("metrics", transactionPayload.getMetricData());
		}
		if(transactionPayload.getCpuUsage() != -1.0){
			update.set("cpuUsage" , transactionPayload.getCpuUsage());
		}
		if(transactionPayload.getFreeMemory() != null){
			update.set("freeMemory", transactionPayload.getFreeMemory());
		}
		if(transactionPayload.getFreeMemoryAtStart() != null){
			update.set("freeMemoryAtStart" , transactionPayload.getFreeMemoryAtStart());
		}
		if(transactionPayload.getCpuUsageAtStart() != -1.0){
			update.set("cpuUsageAtStart", transactionPayload.getCpuUsageAtStart());
		}
		if(transactionPayload.getNoOfAttempts() != -1){
			update.set("noOfAttempts", transactionPayload.getNoOfAttempts());
		}
		if(transactionPayload.getrServePort() != 0){
			update.set("rServePort", transactionPayload.getrServePort());
		}
		if(transactionPayload != null){
			update.set("storeRLogs", transactionPayload.isStoreRLogs());
		}
		try {
			mongoTemplate.upsert(query, update, tenantCode + FrameworkConstant.DOCUMENTS);
		} catch (DuplicateKeyException dke) {// NOPMD
			LOGGER.error("GOT Duplicate Key error while updating tenantResponse, hence updating collection");
			try {
				mongoTemplate.updateFirst(query, update, tenantCode + FrameworkConstant.DOCUMENTS);
			} catch (Exception e) {// NOPMD
				LOGGER.error("GOT error while updating updating collection", e);
			}
		} catch (org.springframework.dao.DuplicateKeyException dke) {
			LOGGER.error("GOT Duplicate Key error while updating tenantResponse, hence updating collection");
			try {
				mongoTemplate.updateFirst(query, update, tenantCode + "_documents");
			} catch (Exception e) {// NOPMD
				LOGGER.error("GOT error while updating updating collection", e);
			}
		}
		Query searchtxnIdQueryForMI = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
		Map<String,Object> txnMIPayload = mongoTemplate.findOne(searchtxnIdQueryForMI, Map.class, RequestContext.getRequestContext()
				.getTenantCode() + FrameworkConstant.UNDERSCORE+FrameworkConstant.MODELINPUT+FrameworkConstant.DOCUMENTS); 
		if(txnMIPayload != null && txnMIPayload.get("modelInput")==null && txnMIPayload.get(MessageVariables.ALTERNATE_STORAGE) != null
				&& Boolean.valueOf((boolean) txnMIPayload.get(MessageVariables.ALTERNATE_STORAGE))){
			byte[] modelInputFileBytes = null;
			try {
				modelInputFileBytes = Files.readAllBytes(moveFileAdapter.getFileObjectForFolder(systemParameterProvider
						.getParameter(SystemConstants.SAN_BASE), transactionPayload.getTransactionId()+ PoolConstants.ENV_SEPERATOR + RuntimeConstants.MODEL_INPUT + JSON, MessageVariables.DOCUMENTS_FOLDER).toPath());
			} catch (IOException e) {
				SystemException.newSystemException(FrameworkExceptionCodes.BSE000125, new Object[] { transactionPayload.getTransactionId(), e.getMessage() });
			}
			if (modelInputFileBytes != null && modelInputFileBytes.length > 0) {
				txnMIPayload.put(MessageVariables.MODEL_INPUT, ConversionUtil.convertJson(modelInputFileBytes, Map.class));
			}
		}
		if(txnMIPayload!=null && txnMIPayload.get("modelInput")!=null && transactionPayload.getPayloadStorage()!=null && !transactionPayload.getPayloadStorage()){
			Query queryForMI = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
			LOGGER.info("Txn Status for txn Id :" + transactionPayload.getTransactionId() + " status :"
					+ transactionPayload.getStatus());
			Map<String,Object> modelInput = (Map<String, Object>)txnMIPayload.get(MessageVariables.MODEL_INPUT);
			modelInput.remove("payload");
			Update updateForMI = new Update()
			.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())
			.set(MessageVariables.MODEL_INPUT,modelInput);                
			try{
				mongoTemplate.upsert(queryForMI, updateForMI, tenantCode +FrameworkConstant.UNDERSCORE+FrameworkConstant.MODELINPUT+ FrameworkConstant.DOCUMENTS);  
			}catch(BsonSerializationException ex){  
				if(StringUtils.contains(ex.getMessage(), EXCEEDS_MAX_DOC_SIZE)){
					updateForMI.set(MessageVariables.MODEL_INPUT, null).set(MessageVariables.ALTERNATE_STORAGE, Boolean.TRUE);
					mongoTemplate.upsert(queryForMI, updateForMI, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.MODELINPUT+FrameworkConstant.DOCUMENTS);
					Map<String, Object> modelInputDoc = new HashMap<String,Object>();
					modelInputDoc.put(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId());
					modelInputDoc.put(MessageVariables.MODEL_INPUT, modelInput);
					moveFileAdapter.deleteFromFolder(transactionPayload.getTransactionId()+ PoolConstants.ENV_SEPERATOR + RuntimeConstants.MODEL_INPUT + JSON,systemParameterProvider
							.getParameter(SystemConstants.SAN_BASE), MessageVariables.DOCUMENTS_FOLDER);
					moveFileAdapter.saveInFolder(transactionPayload.getTransactionId()+ PoolConstants.ENV_SEPERATOR + RuntimeConstants.MODEL_INPUT + JSON,systemParameterProvider
							.getParameter(SystemConstants.SAN_BASE), modelInputDoc, MessageVariables.DOCUMENTS_FOLDER);
				} else {
					Map<String,Object> errorMsg = new HashMap<String,Object>();	
					errorMsg.put("error", ex.getMessage());
					transactionDocumentPayload.getTxnMIPayload().setTxnIOPayload(errorMsg);
					Query queryForMIError = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
					Update updateForMIErrPayload = new Update()				
					.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())			
					.set("modelInput",transactionDocumentPayload.getTxnMIPayload());
					mongoTemplate.upsert(queryForMIError, updateForMIErrPayload, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.MODELINPUT+FrameworkConstant.DOCUMENTS);
				}
			}
		}  

		if(transactionDocumentPayload !=null && transactionDocumentPayload.getTxnMOPayload()!=null && transactionDocumentPayload.getTxnMOPayload().getTxnIOPayload()!=null){
			Query queryForMO = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
			LOGGER.info("Txn Status for txn Id :" + transactionPayload.getTransactionId() + " status :"
					+ transactionPayload.getStatus());
			Update updateForMO = new Update()
			.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())
			.set(MessageVariables.MODEL_OUTPUT,transactionDocumentPayload.getTxnMOPayload().getTxnIOPayload());  
			try{
				mongoTemplate.upsert(queryForMO, updateForMO, tenantCode +FrameworkConstant.UNDERSCORE+FrameworkConstant.MODELOUTPUT+ FrameworkConstant.DOCUMENTS);
			}catch(BsonSerializationException ex){
				if(StringUtils.contains(ex.getMessage(), EXCEEDS_MAX_DOC_SIZE)){
					updateForMO.set(MessageVariables.MODEL_OUTPUT, null).set(MessageVariables.ALTERNATE_STORAGE, Boolean.TRUE);
					mongoTemplate.upsert(queryForMO, updateForMO, tenantCode +FrameworkConstant.UNDERSCORE+FrameworkConstant.MODELOUTPUT+ FrameworkConstant.DOCUMENTS);
					Map<String, Object> modelOutputDoc = new HashMap<String,Object>();
					modelOutputDoc.put(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId());
					modelOutputDoc.put(MessageVariables.MODEL_OUTPUT, transactionDocumentPayload.getTxnMOPayload().getTxnIOPayload());
					moveFileAdapter.deleteFromFolder(transactionPayload.getTransactionId()+ PoolConstants.ENV_SEPERATOR + RuntimeConstants.MODEL_OUTPUT + JSON,systemParameterProvider
							.getParameter(SystemConstants.SAN_BASE), MessageVariables.DOCUMENTS_FOLDER);
					moveFileAdapter.saveInFolder(transactionPayload.getTransactionId()+ PoolConstants.ENV_SEPERATOR + RuntimeConstants.MODEL_OUTPUT + JSON,systemParameterProvider
							.getParameter(SystemConstants.SAN_BASE), modelOutputDoc, MessageVariables.DOCUMENTS_FOLDER);
				} else {
					Map<String,Object> errorMsg = new HashMap<String,Object>();	
					errorMsg.put("error", ex.getMessage());
					transactionDocumentPayload.getTxnMOPayload().setTxnIOPayload(errorMsg);
					Query queryForMOErrPayload = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
					Update updateForMOErrPayload = new Update()				
					.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())			
					.set("modelOutput", transactionDocumentPayload.getTxnMOPayload().getTxnIOPayload());	
					mongoTemplate.upsert(queryForMOErrPayload, updateForMOErrPayload, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.MODELOUTPUT+FrameworkConstant.DOCUMENTS);
				}
			}
		}
		if(transactionDocumentPayload !=null && transactionDocumentPayload.getTxnTIPayload() !=null && transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload()!=null){
			Query queryForTI = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
			LOGGER.info("Txn Status for txn Id :" + transactionPayload.getTransactionId() + " status :"
					+ transactionPayload.getStatus());
			transactionDocumentPayload=removeAdditionalHeaderParam(transactionDocumentPayload);
			// Removing model checksum from transaction tenant input.
						if (null != transactionDocumentPayload && null != transactionDocumentPayload.getTxnTIPayload()
								&& null != transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload()) {
							Map<String, Object> tenantIpRequestHdr = (Map<String, Object>) transactionDocumentPayload.getTxnTIPayload()
									.getTxnIOPayload().get(MessageVariables.HEADER);
							tenantIpRequestHdr.remove(EnvironmentVariables.MODEL_CHECKSUM);
						}
			Update updateForTI = new Update()
			.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())
			.set(MessageVariables.TENANT_INPUT,transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload());    
			try{
				mongoTemplate.upsert(queryForTI, updateForTI, tenantCode +FrameworkConstant.UNDERSCORE+FrameworkConstant.TENANTINPUT+ FrameworkConstant.DOCUMENTS);
			}catch(BsonSerializationException ex){
				if(StringUtils.contains(ex.getMessage(), EXCEEDS_MAX_DOC_SIZE)){
					updateForTI.set(MessageVariables.TENANT_INPUT, null).set(MessageVariables.ALTERNATE_STORAGE, Boolean.TRUE);
					mongoTemplate.upsert(queryForTI, updateForTI, tenantCode +FrameworkConstant.UNDERSCORE+FrameworkConstant.TENANTINPUT+ FrameworkConstant.DOCUMENTS);
					Map<String, Object> tenantInputDoc = new HashMap<String,Object>();
					tenantInputDoc.put(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId());
					tenantInputDoc.put(MessageVariables.TENANT_INPUT, transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload());
					moveFileAdapter.deleteFromFolder(transactionPayload.getTransactionId()+ PoolConstants.ENV_SEPERATOR + RuntimeConstants.TENANT_INPUT + JSON,systemParameterProvider
							.getParameter(SystemConstants.SAN_BASE), MessageVariables.DOCUMENTS_FOLDER);
					moveFileAdapter.saveInFolder(transactionPayload.getTransactionId()+ PoolConstants.ENV_SEPERATOR + RuntimeConstants.TENANT_INPUT + JSON,systemParameterProvider
							.getParameter(SystemConstants.SAN_BASE), tenantInputDoc, MessageVariables.DOCUMENTS_FOLDER);
				} else {
					Map<String,Object> errorMsg = new HashMap<String,Object>();	
					errorMsg.put("error", ex.getMessage());
					transactionDocumentPayload.getTxnTIPayload().setTxnIOPayload(errorMsg);
					Query queryForTIErrPayload = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
					Update updateForTIErrPayload = new Update()				
					.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())			
					.set(MessageVariables.TENANT_INPUT, transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload());	
					mongoTemplate.upsert(queryForTIErrPayload, updateForTIErrPayload, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.TENANTINPUT+FrameworkConstant.DOCUMENTS);
				}
			}
		}
		if(transactionDocumentPayload !=null && transactionDocumentPayload.getTxnTOPayload()!=null && transactionDocumentPayload.getTxnTOPayload().getTxnIOPayload()!=null){
			Query queryForTO = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
			LOGGER.info("Txn Status for txn Id :" + transactionPayload.getTransactionId() + " status :"
					+ transactionPayload.getStatus());
			Update updateForTO = new Update()
			.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())
			.set(MessageVariables.TENANT_OUTPUT,transactionDocumentPayload.getTxnTOPayload().getTxnIOPayload());     		
			try{
				mongoTemplate.upsert(queryForTO, updateForTO, tenantCode +FrameworkConstant.UNDERSCORE+FrameworkConstant.TENANTOUTPUT+ FrameworkConstant.DOCUMENTS);
			}catch(BsonSerializationException ex){
				if(StringUtils.contains(ex.getMessage(), EXCEEDS_MAX_DOC_SIZE)){
					updateForTO.set(MessageVariables.TENANT_OUTPUT, null).set(MessageVariables.ALTERNATE_STORAGE, Boolean.TRUE);
					mongoTemplate.upsert(queryForTO, updateForTO, tenantCode +FrameworkConstant.UNDERSCORE+FrameworkConstant.TENANTOUTPUT+ FrameworkConstant.DOCUMENTS);
					Map<String, Object> tenantOutputDoc = new HashMap<String,Object>();
					tenantOutputDoc.put(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId());
					tenantOutputDoc.put(MessageVariables.TENANT_OUTPUT, transactionDocumentPayload.getTxnTOPayload().getTxnIOPayload());
					moveFileAdapter.deleteFromFolder(transactionPayload.getTransactionId()+ PoolConstants.ENV_SEPERATOR + RuntimeConstants.TENANT_OUTPUT + JSON,systemParameterProvider
							.getParameter(SystemConstants.SAN_BASE), MessageVariables.DOCUMENTS_FOLDER);
					moveFileAdapter.saveInFolder(transactionPayload.getTransactionId()+ PoolConstants.ENV_SEPERATOR + RuntimeConstants.TENANT_OUTPUT + JSON,systemParameterProvider
							.getParameter(SystemConstants.SAN_BASE), tenantOutputDoc, MessageVariables.DOCUMENTS_FOLDER);
				} else {
					Map<String,Object> errorMsg = new HashMap<String,Object>();	
					errorMsg.put("error", ex.getMessage());
					transactionDocumentPayload.getTxnTOPayload().setTxnIOPayload(errorMsg);
					Query queryForTOErrPayload = new Query(Criteria.where(MessageVariables.TRANSACTION_ID).is(transactionPayload.getTransactionId()));
					Update updateForTOErrPayload = new Update()				
					.set(MessageVariables.TRANSACTION_ID, transactionPayload.getTransactionId())			
					.set(MessageVariables.TENANT_OUTPUT, transactionDocumentPayload.getTxnTOPayload().getTxnIOPayload());
					mongoTemplate.upsert(queryForTOErrPayload, updateForTOErrPayload, tenantCode + FrameworkConstant.UNDERSCORE+FrameworkConstant.TENANTOUTPUT+FrameworkConstant.DOCUMENTS);
				}
			}
		}
	}

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	public void setMongoTemplate(final MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	 
	private TransactionDocumentPayload removeAdditionalHeaderParam(TransactionDocumentPayload transactionDocumentPayload)
	{
		((Map<String,Object>) transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload().get(MessageVariables.HEADER)).remove(MessageVariables.CLIENT_ID);
		((Map<String,Object>) transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload().get(MessageVariables.HEADER)).remove(MessageVariables.CHANNEL);
		String txnType=(String)((Map<String,Object>) transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload().get(MessageVariables.HEADER)).get(MessageVariables.TRAN_MODE);
		if(txnType==null) {
			txnType=TransactionMode.ONLINE.getMode();
		}
		switch(txnType)
		{
			case  MessageVariables.TRAN_BULK:
				((Map<String,Object>) transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload().get(MessageVariables.HEADER)).remove(MessageVariables.BATCH_ID);
				break;
			case  MessageVariables.TRAN_BATCH:
				((Map<String,Object>) transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload().get(MessageVariables.HEADER)).remove(MessageVariables.FILE_NAME);
				break;
			default: // MessageVariables.TRAN_ONLINE
				((Map<String,Object>) transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload().get(MessageVariables.HEADER)).remove(MessageVariables.BATCH_ID);
				((Map<String,Object>) transactionDocumentPayload.getTxnTIPayload().getTxnIOPayload().get(MessageVariables.HEADER)).remove(MessageVariables.FILE_NAME);
				break;
		}
		return transactionDocumentPayload;
	}

}
