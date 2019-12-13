package com.ca.umg.business.transaction.mongo.dao;

import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.BsonDocument;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.transaction.info.TransactionFilterForApi;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.transaction.mongo.info.TransactionDocumentForApi;
import com.mongodb.AggregationOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoExecutionTimeoutException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.util.JSON;

/**
 * @author basanaga
 *
 */

@SuppressWarnings("PMD")
public class MongoTransactionDAOImpl implements MongoTransactionDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoTransactionDAOImpl.class);

	// private static final String TXN_DASHBOARD_COUNT_FIELDS_ID =
	// "{_id:0,transactionId:1}";

	private static final int DEFAULT_RECORD_LIMIT = 50000;

	@Value("${default.record.limit:500000}")
	private int defaultLimit;

	private MongoTemplate mongoTemplate;
	private MongoDatabase mongoDb;

	// private static final int COUNT_LIMIT = 2000;
	public static final Long MAX_TIME_ADVANCED_MS = 30000l;
	public static final Long MAX_TIME_PRIMARY_MS = 10000l;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	public MongoTransactionDAOImpl() {
		super();
	}

	public MongoTransactionDAOImpl(MongoDatabase mongoDb, MongoTemplate mongoTemplate) {
		this.mongoDb = mongoDb;
		this.mongoTemplate = mongoTemplate;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ca.umg.business.transaction.dao.TransactionDocumentDAO#
	 * getTenantAndModelIO(java.lang.String)
	 */
	@Override
	public TransactionDocument getTenantAndModelIO(String txnId) throws SystemException {
		TransactionDocument txnDocument = null;
		String sanBase = systemParameterProvider.getParameter(SystemConstants.SAN_BASE);
		try {
			Query searchtxnIdQuery = new Query(Criteria.where(BusinessConstants.TRANSACTION_ID).is(txnId));
			txnDocument = mongoTemplate.findOne(searchtxnIdQuery, TransactionDocument.class,
					RequestContext.getRequestContext().getTenantCode() + FrameworkConstant.DOCUMENTS);

			Query searchtxnIdQueryForTI = new Query(Criteria.where(BusinessConstants.TRANSACTION_ID).is(txnId));

			Map<String, Object> txnTIPayload = mongoTemplate.findOne(searchtxnIdQueryForTI, Map.class,
					RequestContext.getRequestContext().getTenantCode() + FrameworkConstant.UNDERSCORE
							+ FrameworkConstant.TENANTINPUT + FrameworkConstant.DOCUMENTS);
			Map<String, Object> tempTenantInput = new HashMap<String, Object>();
			if (txnTIPayload != null && txnTIPayload.get("tenantInput") != null) {
				tempTenantInput = (Map<String, Object>) txnTIPayload.get("tenantInput");
			} else if (txnTIPayload != null && txnTIPayload.get(BusinessConstants.ALTERNATE_STORAGE) != null
					&& Boolean.valueOf((boolean) txnTIPayload.get(BusinessConstants.ALTERNATE_STORAGE))) {
				byte[] tenantInputFileBytes = getFile(sanBase, txnDocument, BusinessConstants.TENANT_INPUT);
				if (tenantInputFileBytes != null && tenantInputFileBytes.length > 0) {
					tempTenantInput = ConversionUtil.convertJson(tenantInputFileBytes, Map.class);
				}
			}
			if (((Map<String, Object>) tempTenantInput.get(BusinessConstants.HEADER)) != null)
				((Map<String, Object>) tempTenantInput.get(BusinessConstants.HEADER))
						.remove(BusinessConstants.BATCH_ID);
			txnDocument.setTenantInput(tempTenantInput);
			Query searchtxnIdQueryForTO = new Query(Criteria.where(BusinessConstants.TRANSACTION_ID).is(txnId));

			Map<String, Object> txnTOPayload = mongoTemplate.findOne(searchtxnIdQueryForTO, Map.class,
					RequestContext.getRequestContext().getTenantCode() + FrameworkConstant.UNDERSCORE
							+ FrameworkConstant.TENANTOUTPUT + FrameworkConstant.DOCUMENTS);
			if (txnTOPayload != null && txnTOPayload.get("tenantOutput") != null) {
				txnDocument.setTenantOutput((Map<String, Object>) txnTOPayload.get("tenantOutput"));
			} else if (txnTOPayload != null && txnTOPayload.get(BusinessConstants.ALTERNATE_STORAGE) != null
					&& Boolean.valueOf((boolean) txnTOPayload.get(BusinessConstants.ALTERNATE_STORAGE))) {
				byte[] tenantOutputFileBytes = getFile(sanBase, txnDocument, BusinessConstants.TENANT_OUTPUT);
				if (tenantOutputFileBytes != null && tenantOutputFileBytes.length > 0) {
					txnDocument.setTenantOutput(ConversionUtil.convertJson(tenantOutputFileBytes, Map.class));
				}
			}

			Query searchtxnIdQueryForMI = new Query(Criteria.where(BusinessConstants.TRANSACTION_ID).is(txnId));
			Map<String, Object> txnMIPayload = mongoTemplate.findOne(searchtxnIdQueryForMI, Map.class,
					RequestContext.getRequestContext().getTenantCode() + FrameworkConstant.UNDERSCORE
							+ FrameworkConstant.MODELINPUT + FrameworkConstant.DOCUMENTS);

			if (txnMIPayload != null && txnMIPayload.get("modelInput") != null) {
				txnDocument.setModelInput((Map<String, Object>) txnMIPayload.get("modelInput"));
			} else if (txnMIPayload != null && txnMIPayload.get(BusinessConstants.ALTERNATE_STORAGE) != null
					&& Boolean.valueOf((boolean) txnMIPayload.get(BusinessConstants.ALTERNATE_STORAGE))) {
				byte[] modelInputFileBytes = getFile(sanBase, txnDocument, BusinessConstants.MODEL_INPUT);
				if (modelInputFileBytes != null && modelInputFileBytes.length > 0) {
					txnDocument.setModelInput(ConversionUtil.convertJson(modelInputFileBytes, Map.class));
				}
			}

			Query searchtxnIdQueryForMO = new Query(Criteria.where(BusinessConstants.TRANSACTION_ID).is(txnId));
			Map<String, Object> txnMOPayload = mongoTemplate.findOne(searchtxnIdQueryForMO, Map.class,
					RequestContext.getRequestContext().getTenantCode() + FrameworkConstant.UNDERSCORE
							+ FrameworkConstant.MODELOUTPUT + FrameworkConstant.DOCUMENTS);
			if (txnMOPayload != null && txnMOPayload.get("modelOutput") != null) {
				txnDocument.setModelOutput((Map<String, Object>) txnMOPayload.get("modelOutput"));
			} else if (txnMOPayload != null && txnMOPayload.get(BusinessConstants.ALTERNATE_STORAGE) != null
					&& Boolean.valueOf((boolean) txnMOPayload.get(BusinessConstants.ALTERNATE_STORAGE))) {
				byte[] modelOutputFileBytes = getFile(sanBase, txnDocument, BusinessConstants.MODEL_OUTPUT);
				if (modelOutputFileBytes != null && modelOutputFileBytes.length > 0) {
					txnDocument.setModelInput(ConversionUtil.convertJson(modelOutputFileBytes, Map.class));
				}
			}

		} catch (Exception ex) {// NOPMD
			SystemException.newSystemException(FrameworkExceptionCodes.BSE000125,
					new Object[] { txnId, ex.getMessage() });
		}
		return txnDocument;
	}

	@Override
	@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.AvoidDeeplyNestedIfStmts", "PMD.NPathComplexity" })
	public Page<TransactionDocument> searchDefaultTransactions(Integer pageSize)
			throws SystemException, BusinessException {
		Page<TransactionDocument> page = null;
		Pageable pageable = null;
		Long count = null;
		long otherStartTime;
		long otherEndTime;
		List<TransactionDocument> transactionDocs = new ArrayList<TransactionDocument>();

		DBCursor cursor = null;
		DBCollection collectionFromTemplate = getDbCollectionFromTempate();
		otherStartTime = System.currentTimeMillis();
		DBObject dbObj = (DBObject) JSON.parse("{}");
		// cursor = fetchFromDbDefault(collectionFromTemplate, dbObj);
		cursor = fetchFromDbDefault(collectionFromTemplate, dbObj, pageSize);
		if (cursor != null) {
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				TransactionDocument td = mongoTemplate.getConverter().read(TransactionDocument.class, obj);
				if (td.getRunAsOfDate() != null) {
					transactionDocs.add(td);
				}
			}
		}
		otherEndTime = System.currentTimeMillis();
		LOGGER.info(
				"???????????????????????????? TIME TAKEN FOR FETCHING RECORDS FROM MONGO IS ????????????????????????? : "
						+ (otherEndTime - otherStartTime) + " ms");
		LOGGER.info("Size of documents: " + transactionDocs.size());
		pageable = new PageRequest(1, transactionDocs.isEmpty() == true ? 1 : transactionDocs.size());
		page = new PageImpl<TransactionDocument>(transactionDocs, pageable,
				transactionDocs.isEmpty() ? 1 : transactionDocs.size());
		return page;
	}

	@Override
	@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.AvoidDeeplyNestedIfStmts", "PMD.NPathComplexity" })
	public Page<TransactionDocument> searchTransactions(KeyValuePair<String, String> query, int pageNumber,
			int pageSize, String sortOn, boolean sortDesc, List<String> criteiraFields, final boolean emptySreach,
			final Boolean isApiSearch, TransactionFilterForApi transactionFilterForApi)
			throws SystemException, BusinessException {
		Page<TransactionDocument> page = null;
		Pageable pageable = null;
		Long count = null;
		long otherStartTime = System.currentTimeMillis();
		String mergeCollectionName = RequestContext.getRequestContext().getTenantCode() + FrameworkConstant.UNDERSCORE
				+ FrameworkConstant.TENANTOUTPUT + FrameworkConstant.DOCUMENTS;
		Map<String, Object> tempObject;
		try {
			List<TransactionDocument> transactionDocs = new ArrayList<TransactionDocument>();
			if (query.getKey() != null && StringUtils.isNotBlank(query.getKey())) {
				long startTime = System.currentTimeMillis();
				pageable = new PageRequest(
						pageNumber == BusinessConstants.NUMBER_ZERO ? BusinessConstants.NUMBER_ZERO : pageNumber - 1,
						pageSize, new Sort(sortDesc ? Direction.DESC : Direction.ASC, sortOn));
				LOGGER.info("Pagination details {}", pageable);
				if (StringUtils.isNotBlank(query.getValue())) {
					LOGGER.info("Fetching record based on advanced search criteria.");
					LOGGER.error("Query for advanced search criteria used for count is : " + query.getKey());
					int recordLimit = StringUtils
							.isNotBlank(systemParameterProvider.getParameter(SystemConstants.RECORD_LIMIT))
									? Integer.parseInt(
											systemParameterProvider.getParameter(SystemConstants.RECORD_LIMIT))
									: DEFAULT_RECORD_LIMIT;
					Long timeout = StringUtils.isNotBlank(
							systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_ADVANCED_SEARCH))
									? Long.parseLong(systemParameterProvider
											.getParameter(BusinessConstants.MAX_WAIT_TIME_ADVANCED_SEARCH))
									: MAX_TIME_ADVANCED_MS;
					LOGGER.info("Timeout for mongo is (Adavnce Search):" + (timeout / 1000.0) + " seconods");
					Long startCount = System.currentTimeMillis();
					MongoCollection mongoCollection = getMongoCollection();
					CountOptions countOption = new CountOptions().maxTime(timeout, TimeUnit.MILLISECONDS);
					otherStartTime = System.currentTimeMillis();
					count = mongoCollection.count(BsonDocument.parse(query.getKey()), countOption);
					long otherEndTime = System.currentTimeMillis();
					LOGGER.info("Time taken for counting is (Advance Search):"
							+ ((otherEndTime - otherStartTime) / 1000.0) + " seconods");

					LOGGER.info("Found {} records for advanced search count query {} in {} ms.", count, query.getKey(),
							System.currentTimeMillis() - startCount);
					if (count > BusinessConstants.NUMBER_ZERO) {
						if (emptySreach || (!emptySreach && count <= Integer.valueOf(
								systemParameterProvider.getParameter(BusinessConstants.MAX_DISPLAY_RECORDS_SIZE)))) {
							if (recordLimit < count) {
								BusinessException.newBusinessException(BusinessExceptionCodes.BSE000701,
										new Object[] {});
							}
							LOGGER.error("Query for advanced search criteria value is : " + query.getValue());
							DBObject dbObj = (DBObject) JSON.parse(query.getValue());
							DBObject projectionObj = getProjectionObject();
							DBCursor cursor = null;
							DBCollection collectionFromTemplate = getDbCollectionFromTempate();
							otherStartTime = System.currentTimeMillis();
							cursor = fetchFromDb(collectionFromTemplate, dbObj, projectionObj, pageNumber, pageSize,
									sortOn, sortDesc, timeout);
							otherEndTime = System.currentTimeMillis();
							LOGGER.info("Time taken for fetching records is  (Advanced Search):"
									+ ((otherEndTime - otherStartTime) / 1000.0) + " seconods");
							if (cursor != null) {
								while (cursor.hasNext()) {
									DBObject obj = cursor.next();
									TransactionDocument td = mongoTemplate.getConverter()
											.read(TransactionDocument.class, obj);
									transactionDocs.add(td);
								}
							}
							LOGGER.info("Found {} records based on advanced search criteria query {} in {} ms.",
									transactionDocs == null ? 0 : transactionDocs.size(), dbObj,
									System.currentTimeMillis() - startTime);
						}
					}
				} else {
					LOGGER.info("Fetching records based on basic search criteria.");
					/*
					 * MongoCollection mongoCollection = getMongoCollection(); if (mongoCollection
					 * != null) { Long count = mongoCollection.count(); }
					 */

					Long timeout = StringUtils.isNotBlank(
							systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
									? Long.parseLong(systemParameterProvider
											.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
									: MAX_TIME_PRIMARY_MS;
					MongoCollection mongoCollection = getMongoCollection();

					LOGGER.error("Query for basic search criteria is : " + query.getKey());
					LOGGER.info("Timeout for mongo is (Basic Search):" + (timeout / 1000.0) + " seconods");
					CountOptions countOption = new CountOptions().maxTime(timeout, TimeUnit.MILLISECONDS);
					otherStartTime = System.currentTimeMillis();
					count = mongoCollection.count(BsonDocument.parse(query.getKey()), countOption);
					long otherEndTime = System.currentTimeMillis();
					LOGGER.info("Time taken for counting is (Basic Search): "
							+ ((otherEndTime - otherStartTime) / 1000.0) + " seconods");

					if ((emptySreach || (!emptySreach && count <= Integer.valueOf(
							systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))))
							|| isApiSearch) {
						DBObject dbObj = (DBObject) JSON.parse(query.getKey());

						DBObject projectionObj = getProjectionObject();
						// added if check for umg-4849
						if (isApiSearch) {
							projectionObj = setProjectionForApi(projectionObj, transactionFilterForApi);
						}

						Cursor cursor = null;
						DBCollection collectionFromTemplate = getDbCollectionFromTempate();
						if (collectionFromTemplate != null) {

							if (transactionFilterForApi != null && transactionFilterForApi.getIncludeTntOutput()) {
								otherStartTime = System.currentTimeMillis();
								cursor = fetchFromDbByAggregation(collectionFromTemplate, dbObj, projectionObj,
										pageNumber, pageSize, sortOn, sortDesc, timeout, mergeCollectionName);
								otherEndTime = System.currentTimeMillis();
								LOGGER.info("Time taken for fectching records is  (Basic Search):"
										+ ((otherEndTime - otherStartTime) / 1000.0) + " seconods");

								if (cursor != null) {
									while (cursor.hasNext()) {
										DBObject obj = cursor.next();
										TransactionDocument td = mongoTemplate.getConverter()
												.read(TransactionDocument.class, obj);
										tempObject = (Map<String, Object>) ((Map<String, Object>) obj
												.get(mergeCollectionName)).get("tenantOutput");
										td.setTenantOutput(tempObject);
										transactionDocs.add(td);
									}
								}
							} else {
								otherStartTime = System.currentTimeMillis();
								cursor = fetchFromDb(collectionFromTemplate, dbObj, projectionObj, pageNumber, pageSize,
										sortOn, sortDesc, timeout);
								otherEndTime = System.currentTimeMillis();
								LOGGER.info("Time taken for fectching records is  (Basic Search):"
										+ ((otherEndTime - otherStartTime) / 1000.0) + " seconods");
								if (cursor != null) {
									while (cursor.hasNext()) {
										DBObject obj = cursor.next();
										TransactionDocument td = mongoTemplate.getConverter()
												.read(TransactionDocument.class, obj);
										transactionDocs.add(td);
									}
								}
							}
						}
						LOGGER.info("Found {} records for query {} in {} ms.",
								transactionDocs == null ? 0 : transactionDocs.size(), dbObj,
								System.currentTimeMillis() - startTime);

					}
				}
				pageable = new PageRequest(pageNumber, pageSize,
						new Sort(sortDesc ? Direction.DESC : Direction.ASC, sortOn));
				// page = new PageImpl<TransactionDocument>(transactionDocs, pageable,
				// transactionDocs.size());
				page = new PageImpl<TransactionDocument>(transactionDocs, pageable, count);
			}
		} catch (MongoExecutionTimeoutException | MongoTimeoutException ex) {
			long otherEndTime = System.currentTimeMillis();
			LOGGER.info("Mongo Time Out : " + ((otherEndTime - otherStartTime) / 1000.0) + " seconods");
			SystemException.newSystemException(BusinessExceptionCodes.BSE000702, new Object[] {});
		}
		return page;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	/**
	 * used for getting the db collection from mongo template for find query
	 *
	 * @return
	 */
	private DBCollection getDbCollectionFromTempate() {
		DBCollection collectionFrmTemplate = null;
		DB db = this.mongoTemplate.getDb();
		collectionFrmTemplate = db
				.getCollection(RequestContext.getRequestContext().getTenantCode() + FrameworkConstant.DOCUMENTS);
		return collectionFrmTemplate;
	}

	/**
	 * used for getting the db collection from mongo template for specific tenant
	 * document
	 *
	 * @return
	 */
	private DBCollection getDbCollectionForTenant(String tenantCode) {
		DBCollection collectionFrmTemplate = null;
		DB db = this.mongoTemplate.getDb();
		collectionFrmTemplate = db.getCollection(StringUtils.trim(tenantCode) + FrameworkConstant.DOCUMENTS);
		return collectionFrmTemplate;
	}

	/**
	 * used for getting the raw mongo collection for count query
	 *
	 * @return
	 */
	private MongoCollection getMongoCollection() {
		MongoCollection mongoCollection = null;
		mongoCollection = this.mongoDb
				.getCollection(RequestContext.getRequestContext().getTenantCode() + FrameworkConstant.DOCUMENTS);
		return mongoCollection;
	}

	/**
	 * used for creating the raw mongo collection
	 *
	 * @return
	 */
	public void createMongoCollection(String collectionName) {
		mongoDb.createCollection(collectionName + FrameworkConstant.DOCUMENTS);

		mongoDb.createCollection(collectionName + FrameworkConstant.UNDERSCORE + FrameworkConstant.TENANTINPUT
				+ FrameworkConstant.DOCUMENTS);
		mongoDb.createCollection(collectionName + FrameworkConstant.UNDERSCORE + FrameworkConstant.MODELINPUT
				+ FrameworkConstant.DOCUMENTS);
		mongoDb.createCollection(collectionName + FrameworkConstant.UNDERSCORE + FrameworkConstant.TENANTOUTPUT
				+ FrameworkConstant.DOCUMENTS);
		mongoDb.createCollection(collectionName + FrameworkConstant.UNDERSCORE + FrameworkConstant.MODELOUTPUT
				+ FrameworkConstant.DOCUMENTS);

		ListIndexesIterable<Document> existingIndexes = mongoDb
				.getCollection(RequestContext.getRequestContext().getTenantCode() + FrameworkConstant.DOCUMENTS)
				.listIndexes();
		for (Document index : existingIndexes) {
			// System.out.println(document.getKey() + " ---- " +
			// document.getValue());
			IndexOptions indexOptions = new IndexOptions();
			String name = index.getString("name");
			if (name.equals("_id_")) {
				continue;
			}
			indexOptions.name(name);
			Document document = (Document) index.get("key");
			indexOptions.background(true);
			if (index.get("unique") != null) {
				Boolean unique = index.getBoolean("unique");
				indexOptions.unique(unique);
			}
			mongoDb.getCollection(collectionName + FrameworkConstant.DOCUMENTS).createIndex(document, indexOptions);
		}

		IndexOptions indexOptions = new IndexOptions();
		indexOptions.unique(true);
		indexOptions.name("transactionId_-1");
		mongoDb.getCollection(collectionName + FrameworkConstant.UNDERSCORE + FrameworkConstant.TENANTINPUT
				+ FrameworkConstant.DOCUMENTS).createIndex(new BasicDBObject("transactionId", -1.0), indexOptions);
		mongoDb.getCollection(collectionName + FrameworkConstant.UNDERSCORE + FrameworkConstant.TENANTOUTPUT
				+ FrameworkConstant.DOCUMENTS).createIndex(new BasicDBObject("transactionId", -1.0), indexOptions);
		mongoDb.getCollection(collectionName + FrameworkConstant.UNDERSCORE + FrameworkConstant.MODELINPUT
				+ FrameworkConstant.DOCUMENTS).createIndex(new BasicDBObject("transactionId", -1.0), indexOptions);
		mongoDb.getCollection(collectionName + FrameworkConstant.UNDERSCORE + FrameworkConstant.MODELOUTPUT
				+ FrameworkConstant.DOCUMENTS).createIndex(new BasicDBObject("transactionId", -1.0), indexOptions);
	}

	/**
	 * used to drop the raw mongo collection
	 *
	 * @return
	 */
	public void dropMongoCollection(String collectionName) {
		mongoDb.getCollection(collectionName).drop();
	}

	private DBObject getProjectionObject() {
		DBObject projectionObj = new BasicDBObject("_id", 0);
		projectionObj.put("tenantOutput", 0);
		projectionObj.put("tenantInput", 0);
		projectionObj.put("modelOutput", 0);
		projectionObj.put("modelInput", 0);
		return projectionObj;
	}

	private DBCursor fetchFromDb(DBCollection collectionFromTemplate, DBObject dbObj, DBObject projectionObj,
			int pageNumber, int pageSize, String sortOn, boolean sortDesc, Long timeout) {
		DBCursor cursor = collectionFromTemplate.find(dbObj, projectionObj).limit(pageSize)
				.skip((pageNumber == BusinessConstants.NUMBER_ZERO ? BusinessConstants.NUMBER_ZERO : pageNumber - 1)
						* pageSize)
				.maxTime(timeout, TimeUnit.MILLISECONDS).sort(new BasicDBObject(sortOn,
						sortDesc ? BusinessConstants.NEGETIVE_NUMBER_ONE : BusinessConstants.NUMBER_ONE));
		return cursor;
	}

	private Cursor fetchFromDbByAggregation(DBCollection collectionFromTemplate, DBObject dbObj, DBObject projectionObj,
			int pageNumber, int pageSize, String sortOn, boolean sortDesc, Long timeout, String mergeCollectionName) {
		BasicDBObject matchObj = new BasicDBObject("$match", dbObj);
		BasicDBObject lookup = new BasicDBObject("$lookup",
				new BasicDBObject("from", mergeCollectionName).append("localField", "transactionId")
						.append("foreignField", "transactionId").append("as", mergeCollectionName));
		BasicDBObject unwind = new BasicDBObject("$unwind", "$" + mergeCollectionName);
		BasicDBObject limit = new BasicDBObject("$limit", pageSize);
		BasicDBObject skip = new BasicDBObject("$skip",
				(pageNumber == BusinessConstants.NUMBER_ZERO ? BusinessConstants.NUMBER_ZERO : pageNumber - 1)
						* pageSize);
		BasicDBObject sort = new BasicDBObject("$sort", new BasicDBObject(sortOn,
				sortDesc ? BusinessConstants.NEGETIVE_NUMBER_ONE : BusinessConstants.NUMBER_ONE));
		List<DBObject> pipelines = new ArrayList();
		pipelines.add(matchObj);
		pipelines.add(lookup);
		pipelines.add(unwind);
		pipelines.add(limit);
		pipelines.add(skip);
		pipelines.add(sort);
		AggregationOptions aggregationOptions = AggregationOptions.builder().maxTime(timeout, TimeUnit.MILLISECONDS)
				.build();

		Cursor cursor = collectionFromTemplate.aggregate(pipelines, aggregationOptions);

		return cursor;
	}

	private DBCursor fetchFromDbDefault(DBCollection collectionFromTemplate, DBObject dbObj, Integer pageSize) {
		DBObject dbSortObj = (DBObject) JSON.parse("{_id:-1}");
		DBCursor cursor = collectionFromTemplate.find(dbObj, getProjectionObject()).sort(dbSortObj).limit(pageSize);
		return cursor;
	}

	@Override
	public List<String> fetchDistinctVersionName() {
		DBCollection collectionFromTemplate = getDbCollectionFromTempate();
		DBObject o1 = new BasicDBObject(BusinessConstants.VERSION_NAME,
				new BasicDBObject("$ne", new Object[] { "", null }));
		long startTime = System.currentTimeMillis();
		List<String> versionNameList = collectionFromTemplate.distinct("versionName", o1);
		long endTime = System.currentTimeMillis();
		LOGGER.info("Time taken for fetching model name from mongo is : " + (endTime - startTime) + " ms");
		return versionNameList;
	}

	@Override
	public Long fetchTransactionInDays(Long runAsOfDate) {
		DBCollection collectionFromTemplate = getDbCollectionFromTempate();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(BusinessConstants.IS_TEST, false);
		map.put(BusinessConstants.RUN_AS_OF_DATE, new BasicDBObject("$gte", runAsOfDate));
		BasicDBObject query = new BasicDBObject(map);
		long startTime = System.currentTimeMillis();
		long transactionCountInDays = collectionFromTemplate.find(query).count();
		long endTime = System.currentTimeMillis();
		LOGGER.info(
				" Time taken for fetching total transation in days from mongo is  : " + (endTime - startTime) + " ms");
		return transactionCountInDays;
	}

	@Override
	public Cursor getSuccessFailTransactionCount(Long startRunAsOfDate, Long endRunAsOfDate, String tenantCode) {
		DBCollection collectionFromTemplate = getDbCollectionForTenant(tenantCode);

		Map<String, Object> matchQuery = new HashMap<String, Object>();
		matchQuery.put(BusinessConstants.IS_TEST, false);
		matchQuery.put(BusinessConstants.RUN_AS_OF_DATE,
				new BasicDBObject("$gte", startRunAsOfDate).append("$lte", endRunAsOfDate));
		DBObject match = new BasicDBObject("$match", matchQuery);

		Map<String, Object> groupMap = new HashMap<String, Object>();
		groupMap.put(BusinessConstants.GROUP_ID, "$versionName");

		/*
		 * // Count Success Count DBObject successEq = new BasicDBObject("$eq", new
		 * Object[] { "$status", "Success" }); DBObject successCond = new
		 * BasicDBObject("$cond", new Object[] { successEq, 1, 0 }); myCount: { $sum: 1
		 * } groupMap.put(BusinessConstants.SUCCESS_COUNT, new BasicDBObject("$sum",
		 * successCond));
		 *
		 * // Count Fail Count DBObject errorEq = new BasicDBObject("$eq", new Object[]
		 * { "$status", "Error" }); DBObject errorCond = new BasicDBObject("$cond", new
		 * Object[] { errorEq, 1, 0 });
		 */

		// total Count
		groupMap.put(BusinessConstants.TRANSACTION_COUNT, new BasicDBObject("$sum", 1));

		DBObject group = new BasicDBObject("$group", groupMap);

		Long timeout = StringUtils
				.isNotBlank(systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
						? Long.parseLong(
								systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
						: MAX_TIME_PRIMARY_MS;

		LOGGER.info("Timeout for mongo is (Basic Search):" + (timeout / 1000.0) + " seconods");

		AggregationOptions aggregationOptions = AggregationOptions.builder().maxTime(timeout, TimeUnit.MILLISECONDS)
				.build();

		long startTime = System.currentTimeMillis();
		List<? extends DBObject> pipeline = Arrays.asList(match, group);
		Cursor cursor = collectionFromTemplate.aggregate(pipeline, aggregationOptions);
		long endTime = System.currentTimeMillis();
		LOGGER.info(" Time taken for fetching getSuccessFailTransactionCount  for Tenant {}  from mongo is  : {} ms",
				tenantCode, (endTime - startTime));
		return cursor;
	}

	@Override
	public Cursor getStatusMetricsTransactionCount(Long startRunAsOfDate, Long endRunAsOfDate, String tenantCode) {
		DBCollection collectionFromTemplate = getDbCollectionForTenant(tenantCode);

		Map<String, Object> matchQuery = new HashMap<String, Object>();
		matchQuery.put(BusinessConstants.IS_TEST, false);
		matchQuery.put(BusinessConstants.RUN_AS_OF_DATE,
				new BasicDBObject("$gte", startRunAsOfDate).append("$lte", endRunAsOfDate));
		DBObject match = new BasicDBObject("$match", matchQuery);

		Map<String, Object> groupMap = new HashMap<String, Object>();
		groupMap.put(BusinessConstants.GROUP_ID, "");

		// Count Success Count
		DBObject successEq = new BasicDBObject("$eq", new Object[] { "$status", "Success" });
		DBObject successCond = new BasicDBObject("$cond", new Object[] { successEq, 1, 0 });
		groupMap.put(BusinessConstants.SUCCESS, new BasicDBObject("$sum", successCond));

		// RSE[8041,8042,.....] MODEL FAILURES Count
		Object[] subSet = new Object[] { "$errorCode" };
		Object[] set = new Object[] { "RSE008041", "RSE008042", "RSE008043", "RSE008044", "RSE008045", "RSE008046",
				"RSE008047", "RSE008048" };
		DBObject modelError = new BasicDBObject("$setIsSubset", new Object[] { subSet, set });

		DBObject errorCondition = new BasicDBObject("$cond", new Object[] { modelError, 1, 0 });

		groupMap.put(BusinessConstants.MODEL_FAILURES, new BasicDBObject("$sum", errorCondition));

		// RSE[....,.....] OTHER FAILURES Count
		Object[] failSubSet = new Object[] { "$errorCode" };
		Object[] failSet = new Object[] { "RSE000002", "RSE000003", "RSE000100", "RSE000101", "RSE000102", "RSE000103",
				"RSE000104", "RSE000401", "RSE000501", "RSE000502", "RSE000508", "RSE000509", "RSE000510", "RSE000511",
				"RSE000800", "RSE000801", "RSE000802", "RSE000803", "RSE000804", "RSE000805", "RSE000806", "RSE000807",
				"RSE000808", "RSE000809", "RSE000812", "RSE000813", "RSE000816", "RSE000817", "RSE000818", "RSE000819",
				"RSE000821", "RSE000822", "RSE000830", "RSE000831", "RSE000832", "RSE000833", "RSE000835", "RSE000905",
				"RSE000930" };
		DBObject otherError = new BasicDBObject("$setIsSubset", new Object[] { failSubSet, failSet });

		DBObject failcond = new BasicDBObject("$cond", new Object[] { otherError, 1, 0 });

		groupMap.put(BusinessConstants.OTHER_FAILURES, new BasicDBObject("$sum", failcond));

		// RVE INPUT VALIDATION FAILURES Count
		DBObject errorRVE = new BasicDBObject("$substr", new Object[] { "$errorCode", 0, 3 });
		DBObject eqCond = new BasicDBObject("$eq", new Object[] { errorRVE, "RVE" });

		DBObject inputValFlr = new BasicDBObject("$cond", new Object[] { eqCond, 1, 0 });
		groupMap.put(BusinessConstants.INPUT_VALIDATION_FAILURES, new BasicDBObject("$sum", inputValFlr));

		// RVE OUT VALIDATION FAILURES Count
		DBObject errorRMV = new BasicDBObject("$substr", new Object[] { "$errorCode", 0, 3 });
		DBObject rmvEqCond = new BasicDBObject("$eq", new Object[] { errorRMV, "RMV" });

		DBObject outputValFlr = new BasicDBObject("$cond", new Object[] { rmvEqCond, 1, 0 });
		groupMap.put(BusinessConstants.OUTPUT_VALIDATION_FAILURES, new BasicDBObject("$sum", outputValFlr));

		DBObject group = new BasicDBObject("$group", groupMap);

		Long timeout = StringUtils
				.isNotBlank(systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
						? Long.parseLong(
								systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
						: MAX_TIME_PRIMARY_MS;

		LOGGER.info("Timeout for mongo is (Basic Search):" + (timeout / 1000.0) + " seconods");

		AggregationOptions aggregationOptions = AggregationOptions.builder().maxTime(timeout, TimeUnit.MILLISECONDS)
				.build();

		long startTime = System.currentTimeMillis();
		List<? extends DBObject> pipeline = Arrays.asList(match, group);
		Cursor cursor = collectionFromTemplate.aggregate(pipeline, aggregationOptions);
		long endTime = System.currentTimeMillis();
		LOGGER.info(" Time taken for fetching getStatusMetricsTransactionCount for Tenant {}  from mongo is  : {} ms",
				tenantCode, (endTime - startTime));
		return cursor;
	}

	@Override
	public Cursor getUsageDynamicsDetails(Long startRunAsOfDate, Long endRunAsOfDate, String tenantCode) {
		DBCollection collectionFromTemplate = getDbCollectionForTenant(tenantCode);

		Map<String, Object> matchQuery = new HashMap<String, Object>();
		matchQuery.put(BusinessConstants.IS_TEST, false);
		matchQuery.put(BusinessConstants.RUN_AS_OF_DATE,
				new BasicDBObject("$gte", startRunAsOfDate).append("$lte", endRunAsOfDate));
		DBObject match = new BasicDBObject("$match", matchQuery);

		Map<String, String> groupByCriterion = new HashMap<String, String>();
		groupByCriterion.put(BusinessConstants.MODEL_NAME, "$versionName");
		groupByCriterion.put(BusinessConstants.MAJOR_VERSION, "$majorVersion");
		groupByCriterion.put(BusinessConstants.MINOR_VERSION, "$minorVersion");

		Map<String, Object> groupMap = new HashMap<String, Object>();
		groupMap.put(BusinessConstants.GROUP_ID, groupByCriterion);

		// Count Success Count
		DBObject successEq = new BasicDBObject("$eq", new Object[] { "$status", "Success" });
		DBObject successCond = new BasicDBObject("$cond", new Object[] { successEq, 1, 0 });
		groupMap.put(BusinessConstants.SUCCESS, new BasicDBObject("$sum", successCond));

		groupMap.put(BusinessConstants.TOTAL, new BasicDBObject("$sum", 1));

		// RSE[8041,8042,.....] MODEL FAILURES Count
		Object[] subSet = new Object[] { "$errorCode" };
		Object[] set = new Object[] { "RSE008041", "RSE008042", "RSE008043", "RSE008044", "RSE008045", "RSE008046",
				"RSE008047", "RSE008048" };
		DBObject modelError = new BasicDBObject("$setIsSubset", new Object[] { subSet, set });

		DBObject errorCondition = new BasicDBObject("$cond", new Object[] { modelError, 1, 0 });

		// RSE[....,.....] OTHER FAILURES Count
		Object[] failSubSet = new Object[] { "$errorCode" };
		Object[] failSet = new Object[] { "RSE000002", "RSE000003", "RSE000100", "RSE000101", "RSE000102", "RSE000103",
				"RSE000104", "RSE000401", "RSE000501", "RSE000502", "RSE000508", "RSE000509", "RSE000510", "RSE000511",
				"RSE000800", "RSE000801", "RSE000802", "RSE000803", "RSE000804", "RSE000805", "RSE000806", "RSE000807",
				"RSE000808", "RSE000809", "RSE000812", "RSE000813", "RSE000816", "RSE000817", "RSE000818", "RSE000819",
				"RSE000821", "RSE000822", "RSE000830", "RSE000831", "RSE000832", "RSE000833", "RSE000835", "RSE000905",
				"RSE000930" };
		DBObject otherError = new BasicDBObject("$setIsSubset", new Object[] { failSubSet, failSet });

		DBObject failcond = new BasicDBObject("$cond", new Object[] { otherError, 1, 0 });

		groupMap.put(BusinessConstants.OTHER_FAILURES, new BasicDBObject("$sum", failcond));

		groupMap.put(BusinessConstants.MODEL_FAILURES, new BasicDBObject("$sum", errorCondition));

		// RVE INPUT VALIDATION FAILURES Count
		DBObject errorRVE = new BasicDBObject("$substr", new Object[] { "$errorCode", 0, 3 });
		DBObject eqCond = new BasicDBObject("$eq", new Object[] { errorRVE, "RVE" });

		DBObject inputValFlr = new BasicDBObject("$cond", new Object[] { eqCond, 1, 0 });
		groupMap.put(BusinessConstants.INPUT_VALIDATION_FAILURES, new BasicDBObject("$sum", inputValFlr));

		// RVE OUT VALIDATION FAILURES Count
		DBObject errorRMV = new BasicDBObject("$substr", new Object[] { "$errorCode", 0, 3 });
		DBObject rmvEqCond = new BasicDBObject("$eq", new Object[] { errorRMV, "RMV" });

		DBObject outputValFlr = new BasicDBObject("$cond", new Object[] { rmvEqCond, 1, 0 });
		groupMap.put(BusinessConstants.OUTPUT_VALIDATION_FAILURES, new BasicDBObject("$sum", outputValFlr));

		/*// MODEL RESPONSE TIME
		DBObject subTime = new BasicDBObject("$subtract", new Object[] { "$runtimeCallEnd", "$runtimeCallStart" });
		DBObject ifNull = new BasicDBObject("$ifNull", new Object[] { "$runtimeCallEnd", "true" });
		DBObject eqCon = new BasicDBObject("$eq", new Object[] { ifNull, "true" });
		DBObject modelTime = new BasicDBObject("$cond", new Object[] { eqCon, new Long(0), subTime });
		groupMap.put(BusinessConstants.MODEL_RESPONSE_TIME, new BasicDBObject("$push", modelTime));
		
		// END TO END TIME
		DBObject endsubTime = new BasicDBObject("$subtract", new Object[] { "$modelCallEnd", "$modelCallStart" });
		DBObject endtimeifNull = new BasicDBObject("$ifNull", new Object[] { "$modelCallEnd", "true" });
		DBObject runtimeEndeqCon = new BasicDBObject("$eq", new Object[] { endtimeifNull, "true" });
		DBObject entToendTime = new BasicDBObject("$cond", new Object[] { runtimeEndeqCon, new Long(0), endsubTime });
		groupMap.put(BusinessConstants.END_TO_END_TIME, new BasicDBObject("$push", entToendTime));
		
		DBObject totalSubTime = new BasicDBObject("$subtract", new Object[] { "$runtimeCallEnd", "$runtimeCallStart" });
		groupMap.put(BusinessConstants.MODEL_UTILISATION, new BasicDBObject("$sum", totalSubTime));
		*/
		DBObject group = new BasicDBObject("$group", groupMap);

		Long timeout = StringUtils
				.isNotBlank(systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
						? Long.parseLong(
								systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
						: MAX_TIME_PRIMARY_MS;

		LOGGER.info("Timeout for mongo is (Basic Search):" + (timeout / 1000.0) + " seconods");

		AggregationOptions aggregationOptions = AggregationOptions.builder().maxTime(timeout, TimeUnit.MILLISECONDS)
				.build();

		long startTime = System.currentTimeMillis();
		List<? extends DBObject> pipeline = Arrays.asList(match, group);
		Cursor cursor = collectionFromTemplate.aggregate(pipeline, aggregationOptions);
		long endTime = System.currentTimeMillis();
		LOGGER.info(" Time taken for fetching usage dynamics  for Tenant {}  from mongo is  : {} ms", tenantCode,
				(endTime - startTime));
		return cursor;
	}

	@Override
	public Cursor getSelectedUsageDynamicsDetails(Long startRunAsOfDate, Long endRunAsOfDate, String tenantCode,
			String groupBy, String selectionType) {
		DBCollection collectionFromTemplate = getDbCollectionForTenant(tenantCode);

		// match
		Map<String, Object> matchQuery = new HashMap<String, Object>();
		matchQuery.put(BusinessConstants.IS_TEST, false);
		matchQuery.put(BusinessConstants.RUN_AS_OF_DATE,
				new BasicDBObject("$gte", startRunAsOfDate).append("$lte", endRunAsOfDate));
		DBObject match = new BasicDBObject("$match", matchQuery);

		// group criterion
		Map<String, String> groupByCriterion = new HashMap<String, String>();

		// project
		// project to make the pipeline of filter data
		Map<String, Object> projectQuery = new HashMap<String, Object>();
		projectQuery.put(BusinessConstants.RUN_AS_OF_DATE,
				new BasicDBObject("$add", new Object[] { new Date(0), "$runAsOfDate" }));
		projectQuery.put(BusinessConstants.VERSION_NAME, 1);
		projectQuery.put(BusinessConstants.ERROR_CODE, 1);
		projectQuery.put(BusinessConstants.STATUS, 1);

		DBObject project = new BasicDBObject("$project", projectQuery);

		Map<String, Object> projectMonth = new HashMap<String, Object>();
		projectMonth.put(BusinessConstants.VERSION_NAME, 1);
		projectMonth.put(BusinessConstants.ERROR_CODE, 1);
		projectMonth.put(BusinessConstants.STATUS, 1);
		projectMonth.put(BusinessConstants.RUN_AS_OF_DATE, 1);

		projectMonth.put(BusinessConstants.YEAR, new BasicDBObject("$year", "$runAsOfDate"));
		groupByCriterion.put(BusinessConstants.YEAR, "$year");
		if (StringUtils.equalsIgnoreCase(BusinessConstants.MONTH, groupBy)) {
			projectMonth.put(BusinessConstants.MONTH, new BasicDBObject("$month", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.MONTH, "$month");
		} else if (StringUtils.equalsIgnoreCase(BusinessConstants.DAY, groupBy)) {
			projectMonth.put(BusinessConstants.MONTH, new BasicDBObject("$month", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.MONTH, "$month");
			projectMonth.put(BusinessConstants.DAY, new BasicDBObject("$dayOfMonth", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.DAY, "$day");
		} else {
			projectMonth.put(BusinessConstants.MONTH, new BasicDBObject("$month", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.MONTH, "$month");
			projectMonth.put(BusinessConstants.DAY, new BasicDBObject("$dayOfMonth", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.DAY, "$day");
			projectMonth.put(BusinessConstants.HOUR, new BasicDBObject("$hour", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.HOUR, "$hour");
		}

		DBObject projectPipeline = new BasicDBObject("$project", projectMonth);

		// Group By
		groupByCriterion.put(BusinessConstants.MODEL_NAME, "$versionName");

		Map<String, Object> groupMap = new HashMap<String, Object>();
		groupMap.put(BusinessConstants.GROUP_ID, groupByCriterion);

		// Count Success Count
		DBObject successEq = new BasicDBObject("$eq", new Object[] { "$status", "Success" });
		DBObject successCond = new BasicDBObject("$cond", new Object[] { successEq, 1, 0 });
		groupMap.put(BusinessConstants.SUCCESS, new BasicDBObject("$sum", successCond));

		groupMap.put(BusinessConstants.TOTAL, new BasicDBObject("$sum", 1));

		DBObject group = new BasicDBObject("$group", groupMap);

		Long timeout = StringUtils
				.isNotBlank(systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
						? Long.parseLong(
								systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
						: MAX_TIME_PRIMARY_MS;

		LOGGER.info("Timeout for mongo is (Basic Search):" + (timeout / 1000.0) + " seconods");

		AggregationOptions aggregationOptions = AggregationOptions.builder().maxTime(timeout, TimeUnit.MILLISECONDS)
				.build();

		long startTime = System.currentTimeMillis();
		List<? extends DBObject> pipeline = Arrays.asList(match, project, projectPipeline, group);
		Cursor cursor = collectionFromTemplate.aggregate(pipeline, aggregationOptions);
		long endTime = System.currentTimeMillis();
		LOGGER.info(" Time taken for fetching usage dynamics  for Tenant {}  from mongo is  : {} ms", tenantCode,
				(endTime - startTime));
		return cursor;
	}

	@Override
	public Cursor getUsageDynamicsGrid(Long startRunAsOfDate, Long endRunAsOfDate, String tenantCode) {
		DBCollection collectionFromTemplate = getDbCollectionForTenant(tenantCode);

		// match
		Map<String, Object> matchQuery = new HashMap<String, Object>();
		matchQuery.put(BusinessConstants.IS_TEST, false);
		matchQuery.put(BusinessConstants.RUN_AS_OF_DATE,
				new BasicDBObject("$gte", startRunAsOfDate).append("$lte", endRunAsOfDate));
		DBObject match = new BasicDBObject("$match", matchQuery);

		// group criterion
		Map<String, String> groupByCriterion = new HashMap<String, String>();

		/*// project
		// project to make the pipeline of filter data
		Map<String, Object> projectQuery = new HashMap<String, Object>();
		projectQuery.put(BusinessConstants.VERSION_NAME, 1);
		projectQuery.put(BusinessConstants.RUNTIME_CALL_START, 1);
		projectQuery.put(BusinessConstants.RUNTIME_CALL_END, 1);
		projectQuery.put(BusinessConstants.ERROR_CODE, 1);
		projectQuery.put(BusinessConstants.MODEL_CALL_START, 1);
		projectQuery.put(BusinessConstants.MODEL_CALL_END, 1);
		projectQuery.put(BusinessConstants.MINOR_VERSION, 1);
		projectQuery.put(BusinessConstants.MAJOR_VERSION, 1);
		projectQuery.put(BusinessConstants.STATUS, 1);
		
		DBObject project = new BasicDBObject("$project", projectQuery);
		
		Map<String, Object> projectMonth = new HashMap<String, Object>();
		projectMonth.put(BusinessConstants.VERSION_NAME, 1);
		projectMonth.put(BusinessConstants.RUNTIME_CALL_START, 1);
		projectMonth.put(BusinessConstants.RUNTIME_CALL_END, 1);
		projectMonth.put(BusinessConstants.ERROR_CODE, 1);
		projectMonth.put(BusinessConstants.MODEL_CALL_START, 1);
		projectMonth.put(BusinessConstants.MODEL_CALL_END, 1);
		projectMonth.put(BusinessConstants.MINOR_VERSION, 1);
		projectMonth.put(BusinessConstants.MAJOR_VERSION, 1);
		projectMonth.put(BusinessConstants.STATUS, 1);
		projectMonth.put(BusinessConstants.RUN_AS_OF_DATE, 1);
		
		
		DBObject projectPipeline = new BasicDBObject("$project", projectMonth);
		
		*/
		// Group By
		groupByCriterion.put(BusinessConstants.MODEL_NAME, "$versionName");
		groupByCriterion.put(BusinessConstants.MAJOR_VERSION, "$majorVersion");
		groupByCriterion.put(BusinessConstants.MINOR_VERSION, "$minorVersion");

		Map<String, Object> groupMap = new HashMap<String, Object>();
		groupMap.put(BusinessConstants.GROUP_ID, groupByCriterion);

		// Count Success Count
		DBObject successEq = new BasicDBObject("$eq", new Object[] { "$status", "Success" });
		DBObject successCond = new BasicDBObject("$cond", new Object[] { successEq, 1, 0 });
		groupMap.put(BusinessConstants.SUCCESS, new BasicDBObject("$sum", successCond));

		groupMap.put(BusinessConstants.TOTAL, new BasicDBObject("$sum", 1));

		// RSE[8041,8042,.....] MODEL FAILURES Count
		Object[] subSet = new Object[] { "$errorCode" };
		Object[] set = new Object[] { "RSE008041", "RSE008042", "RSE008043", "RSE008044", "RSE008045", "RSE008046",
				"RSE008047", "RSE008048" };
		DBObject modelError = new BasicDBObject("$setIsSubset", new Object[] { subSet, set });

		DBObject errorCondition = new BasicDBObject("$cond", new Object[] { modelError, 1, 0 });

		// RSE[....,.....] OTHER FAILURES Count
		Object[] failSubSet = new Object[] { "$errorCode" };
		Object[] failSet = new Object[] { "RSE000002", "RSE000003", "RSE000100", "RSE000101", "RSE000102", "RSE000103",
				"RSE000104", "RSE000401", "RSE000501", "RSE000502", "RSE000508", "RSE000509", "RSE000510", "RSE000511",
				"RSE000800", "RSE000801", "RSE000802", "RSE000803", "RSE000804", "RSE000805", "RSE000806", "RSE000807",
				"RSE000808", "RSE000809", "RSE000812", "RSE000813", "RSE000816", "RSE000817", "RSE000818", "RSE000819",
				"RSE000821", "RSE000822", "RSE000830", "RSE000831", "RSE000832", "RSE000833", "RSE000835", "RSE000905",
				"RSE000930" };
		DBObject otherError = new BasicDBObject("$setIsSubset", new Object[] { failSubSet, failSet });

		DBObject failcond = new BasicDBObject("$cond", new Object[] { otherError, 1, 0 });

		groupMap.put(BusinessConstants.OTHER_FAILURES, new BasicDBObject("$sum", failcond));

		groupMap.put(BusinessConstants.MODEL_FAILURES, new BasicDBObject("$sum", errorCondition));

		// RVE INPUT VALIDATION FAILURES Count
		DBObject errorRVE = new BasicDBObject("$substr", new Object[] { "$errorCode", 0, 3 });
		DBObject eqCond = new BasicDBObject("$eq", new Object[] { errorRVE, "RVE" });

		DBObject inputValFlr = new BasicDBObject("$cond", new Object[] { eqCond, 1, 0 });
		groupMap.put(BusinessConstants.INPUT_VALIDATION_FAILURES, new BasicDBObject("$sum", inputValFlr));

		// RVE OUT VALIDATION FAILURES Count
		DBObject errorRMV = new BasicDBObject("$substr", new Object[] { "$errorCode", 0, 3 });
		DBObject rmvEqCond = new BasicDBObject("$eq", new Object[] { errorRMV, "RMV" });

		DBObject outputValFlr = new BasicDBObject("$cond", new Object[] { rmvEqCond, 1, 0 });
		groupMap.put(BusinessConstants.OUTPUT_VALIDATION_FAILURES, new BasicDBObject("$sum", outputValFlr));

		// MODEL RESPONSE TIME
		DBObject subTime = new BasicDBObject("$subtract", new Object[] { "$modelCallEnd", "$modelCallStart" });
		DBObject ifNull = new BasicDBObject("$ifNull", new Object[] { "$modelCallEnd", "true" });
		DBObject eqCon = new BasicDBObject("$eq", new Object[] { ifNull, "true" });
		DBObject modelTime = new BasicDBObject("$cond", new Object[] { eqCon, new Long(0), subTime });
		groupMap.put(BusinessConstants.MODEL_RESPONSE_TIME, new BasicDBObject("$avg", modelTime));

		// END TO END TIME
		DBObject endsubTime = new BasicDBObject("$subtract", new Object[] { "$runtimeCallEnd", "$runtimeCallStart" });
		DBObject endtimeifNull = new BasicDBObject("$ifNull", new Object[] { "$runtimeCallEnd", "true" });
		DBObject runtimeEndeqCon = new BasicDBObject("$eq", new Object[] { endtimeifNull, "true" });
		DBObject entToendTime = new BasicDBObject("$cond", new Object[] { runtimeEndeqCon, new Long(0), endsubTime });
		groupMap.put(BusinessConstants.END_TO_END_TIME, new BasicDBObject("$avg", entToendTime));

		DBObject totalSubTime = new BasicDBObject("$subtract", new Object[] { "$modelCallEnd", "$modelCallStart" });
		groupMap.put(BusinessConstants.MODEL_UTILISATION, new BasicDBObject("$sum", totalSubTime));

		DBObject group = new BasicDBObject("$group", groupMap);

		Long timeout = StringUtils
				.isNotBlank(systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
						? Long.parseLong(
								systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
						: MAX_TIME_PRIMARY_MS;

		LOGGER.info("Timeout for mongo is (Basic Search):" + (timeout / 1000.0) + " seconods");

		AggregationOptions aggregationOptions = AggregationOptions.builder().maxTime(timeout, TimeUnit.MILLISECONDS)
				.build();

		long startTime = System.currentTimeMillis();
		List<? extends DBObject> pipeline = Arrays.asList(match, group);
		Cursor cursor = collectionFromTemplate.aggregate(pipeline, aggregationOptions);
		long endTime = System.currentTimeMillis();
		LOGGER.info(" Time taken for fetching All usage dynamics  for Tenant {}  from mongo is  : {} ms", tenantCode,
				(endTime - startTime));
		return cursor;
	}

	@Override
	public Cursor getUsageDynamicsDetails(Long startRunAsOfDate, Long endRunAsOfDate, String tenantCode, String groupBy,
			String selectionType) {
		DBCollection collectionFromTemplate = getDbCollectionForTenant(tenantCode);

		// match
		Map<String, Object> matchQuery = new HashMap<String, Object>();
		matchQuery.put(BusinessConstants.IS_TEST, false);
		matchQuery.put(BusinessConstants.RUN_AS_OF_DATE,
				new BasicDBObject("$gte", startRunAsOfDate).append("$lte", endRunAsOfDate));
		DBObject match = new BasicDBObject("$match", matchQuery);

		// group criterion
		Map<String, String> groupByCriterion = new HashMap<String, String>();

		// project
		// project to make the pipeline of filter data
		Map<String, Object> projectQuery = new HashMap<String, Object>();
		projectQuery.put(BusinessConstants.RUN_AS_OF_DATE,
				new BasicDBObject("$add", new Object[] { new Date(0), "$runAsOfDate" }));
		projectQuery.put(BusinessConstants.VERSION_NAME, 1);
		projectQuery.put(BusinessConstants.RUNTIME_CALL_START, 1);
		projectQuery.put(BusinessConstants.RUNTIME_CALL_END, 1);
		projectQuery.put(BusinessConstants.ERROR_CODE, 1);
		projectQuery.put(BusinessConstants.MODEL_CALL_START, 1);
		projectQuery.put(BusinessConstants.MODEL_CALL_END, 1);
		projectQuery.put(BusinessConstants.STATUS, 1);

		DBObject project = new BasicDBObject("$project", projectQuery);

		Map<String, Object> projectMonth = new HashMap<String, Object>();
		projectMonth.put(BusinessConstants.VERSION_NAME, 1);
		projectMonth.put(BusinessConstants.RUNTIME_CALL_START, 1);
		projectMonth.put(BusinessConstants.RUNTIME_CALL_END, 1);
		projectMonth.put(BusinessConstants.ERROR_CODE, 1);
		projectMonth.put(BusinessConstants.MODEL_CALL_START, 1);
		projectMonth.put(BusinessConstants.MODEL_CALL_END, 1);
		projectMonth.put(BusinessConstants.STATUS, 1);
		projectMonth.put(BusinessConstants.RUN_AS_OF_DATE, 1);

		projectMonth.put(BusinessConstants.YEAR, new BasicDBObject("$year", "$runAsOfDate"));
		groupByCriterion.put(BusinessConstants.YEAR, "$year");
		if (StringUtils.equalsIgnoreCase(BusinessConstants.MONTH, groupBy)) {
			projectMonth.put(BusinessConstants.MONTH, new BasicDBObject("$month", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.MONTH, "$month");
		} else if (StringUtils.equalsIgnoreCase(BusinessConstants.DAY, groupBy)) {
			projectMonth.put(BusinessConstants.MONTH, new BasicDBObject("$month", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.MONTH, "$month");
			projectMonth.put(BusinessConstants.DAY, new BasicDBObject("$dayOfMonth", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.DAY, "$day");
		} else {
			projectMonth.put(BusinessConstants.MONTH, new BasicDBObject("$month", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.MONTH, "$month");
			projectMonth.put(BusinessConstants.DAY, new BasicDBObject("$dayOfMonth", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.DAY, "$day");
			projectMonth.put(BusinessConstants.HOUR, new BasicDBObject("$hour", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.HOUR, "$hour");
		}

		DBObject projectPipeline = new BasicDBObject("$project", projectMonth);

		// Group By
		groupByCriterion.put(BusinessConstants.MODEL_NAME, "$versionName");

		Map<String, Object> groupMap = new HashMap<String, Object>();
		groupMap.put(BusinessConstants.GROUP_ID, groupByCriterion);

		// Count Success Count
		DBObject successEq = new BasicDBObject("$eq", new Object[] { "$status", "Success" });
		DBObject successCond = new BasicDBObject("$cond", new Object[] { successEq, 1, 0 });
		groupMap.put(BusinessConstants.SUCCESS, new BasicDBObject("$sum", successCond));

		groupMap.put(BusinessConstants.TOTAL, new BasicDBObject("$sum", 1));

		// RSE[8041,8042,.....] MODEL FAILURES Count
		Object[] subSet = new Object[] { "$errorCode" };
		Object[] set = new Object[] { "RSE008041", "RSE008042", "RSE008043", "RSE008044", "RSE008045", "RSE008046",
				"RSE008047", "RSE008048" };
		DBObject modelError = new BasicDBObject("$setIsSubset", new Object[] { subSet, set });

		DBObject errorCondition = new BasicDBObject("$cond", new Object[] { modelError, 1, 0 });

		// RSE[....,.....] OTHER FAILURES Count
		Object[] failSubSet = new Object[] { "$errorCode" };
		Object[] failSet = new Object[] { "RSE000002", "RSE000003", "RSE000100", "RSE000101", "RSE000102", "RSE000103",
				"RSE000104", "RSE000401", "RSE000501", "RSE000502", "RSE000508", "RSE000509", "RSE000510", "RSE000511",
				"RSE000800", "RSE000801", "RSE000802", "RSE000803", "RSE000804", "RSE000805", "RSE000806", "RSE000807",
				"RSE000808", "RSE000809", "RSE000812", "RSE000813", "RSE000816", "RSE000817", "RSE000818", "RSE000819",
				"RSE000821", "RSE000822", "RSE000830", "RSE000831", "RSE000832", "RSE000833", "RSE000835", "RSE000905",
				"RSE000930" };
		DBObject otherError = new BasicDBObject("$setIsSubset", new Object[] { failSubSet, failSet });

		DBObject failcond = new BasicDBObject("$cond", new Object[] { otherError, 1, 0 });

		groupMap.put(BusinessConstants.OTHER_FAILURES, new BasicDBObject("$sum", failcond));

		groupMap.put(BusinessConstants.MODEL_FAILURES, new BasicDBObject("$sum", errorCondition));

		// RVE INPUT VALIDATION FAILURES Count
		DBObject errorRVE = new BasicDBObject("$substr", new Object[] { "$errorCode", 0, 3 });
		DBObject eqCond = new BasicDBObject("$eq", new Object[] { errorRVE, "RVE" });

		DBObject inputValFlr = new BasicDBObject("$cond", new Object[] { eqCond, 1, 0 });
		groupMap.put(BusinessConstants.INPUT_VALIDATION_FAILURES, new BasicDBObject("$sum", inputValFlr));

		// RVE OUT VALIDATION FAILURES Count
		DBObject errorRMV = new BasicDBObject("$substr", new Object[] { "$errorCode", 0, 3 });
		DBObject rmvEqCond = new BasicDBObject("$eq", new Object[] { errorRMV, "RMV" });

		DBObject outputValFlr = new BasicDBObject("$cond", new Object[] { rmvEqCond, 1, 0 });
		groupMap.put(BusinessConstants.OUTPUT_VALIDATION_FAILURES, new BasicDBObject("$sum", outputValFlr));

		/*// MODEL RESPONSE TIME
		DBObject subTime = new BasicDBObject("$subtract", new Object[] { "$modelCallEnd", "$modelCallStart" });
		DBObject ifNull = new BasicDBObject("$ifNull", new Object[] { "$modelCallEnd", "true" });
		DBObject eqCon = new BasicDBObject("$eq", new Object[] { ifNull, "true" });
		DBObject modelTime = new BasicDBObject("$cond", new Object[] { eqCon, new Long(0), subTime });
		groupMap.put(BusinessConstants.MODEL_RESPONSE_TIME, new BasicDBObject("$addToSet", modelTime));
		*/
		/*// END TO END TIME
		DBObject endsubTime = new BasicDBObject("$subtract", new Object[] { "$runtimeCallEnd", "$runtimeCallStart" });
		DBObject endtimeifNull = new BasicDBObject("$ifNull", new Object[] { "$runtimeCallEnd", "true" });
		DBObject runtimeEndeqCon = new BasicDBObject("$eq", new Object[] { endtimeifNull, "true" });
		DBObject entToendTime = new BasicDBObject("$cond", new Object[] { runtimeEndeqCon, new Long(0), endsubTime });
		groupMap.put(BusinessConstants.END_TO_END_TIME, new BasicDBObject("$addToSet", entToendTime));
		
		DBObject totalSubTime = new BasicDBObject("$subtract", new Object[] { "$modelCallEnd", "$modelCallStart" });
		groupMap.put(BusinessConstants.MODEL_UTILISATION, new BasicDBObject("$sum", totalSubTime));
		*/
		DBObject group = new BasicDBObject("$group", groupMap);

		/*//sort by created Date
		Map<String, Object> sortBy = new HashMap<String, Object>();
		sortBy.put(BusinessConstants.CREATED_BY,BusinessConstants.NUMBER_ONE);
		DBObject sort = new BasicDBObject("$sort", sortBy);*/

		/*//we set this limit bcz array size with 16 mb is not supported in mongo
		//limit the endToendTime,modelResponseTime array size to 50000
		Map<String, Object> arrayMap = new HashMap<String, Object>();
		LOGGER.info("Default Size: {}",defaultLimit);
		DBObject e2EArray=new BasicDBObject("$slice",new Object[] {"$endToEndTime",defaultLimit==0?DEFAULT_RECORD_LIMIT:defaultLimit});
		DBObject modelRespArray=new BasicDBObject("$slice",new Object[] {"$modelResponseTime",defaultLimit==0?DEFAULT_RECORD_LIMIT:defaultLimit});
		arrayMap.put(BusinessConstants.END_TO_END_TIME, e2EArray);
		arrayMap.put(BusinessConstants.MODEL_RESPONSE_TIME, modelRespArray);
		arrayMap.put(BusinessConstants.SUCCESS,1);
		arrayMap.put(BusinessConstants.TOTAL,1);
		arrayMap.put(BusinessConstants.OTHER_FAILURES,1);
		arrayMap.put(BusinessConstants.MODEL_FAILURES,1);
		arrayMap.put(BusinessConstants.INPUT_VALIDATION_FAILURES,1);
		arrayMap.put(BusinessConstants.OUTPUT_VALIDATION_FAILURES,1);
		arrayMap.put(BusinessConstants.MODEL_UTILISATION,1);
		DBObject arrayProject = new BasicDBObject("$project", arrayMap);
		*/

		Long timeout = StringUtils
				.isNotBlank(systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
						? Long.parseLong(
								systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
						: MAX_TIME_PRIMARY_MS;

		LOGGER.info("Timeout for mongo is (Basic Search):" + (timeout / 1000.0) + " seconods");

		AggregationOptions aggregationOptions = AggregationOptions.builder().maxTime(timeout, TimeUnit.MILLISECONDS)
				.build();

		long startTime = System.currentTimeMillis();
		List<? extends DBObject> pipeline = Arrays.asList(match, project, projectPipeline, group);
		Cursor cursor = collectionFromTemplate.aggregate(pipeline, aggregationOptions);
		long endTime = System.currentTimeMillis();
		LOGGER.info(" Time taken for fetching usage dynamics  for Tenant {}  from mongo is  : {} ms", tenantCode,
				(endTime - startTime));
		return cursor;
	}

	@Override
	public Cursor getAllTntUsageDynamicsDetails(Long startRunAsOfDate, Long endRunAsOfDate, String tenantCode,
			String groupBy, String selectionType) {
		DBCollection collectionFromTemplate = getDbCollectionForTenant(tenantCode);

		// match
		Map<String, Object> matchQuery = new HashMap<String, Object>();
		matchQuery.put(BusinessConstants.IS_TEST, false);
		matchQuery.put(BusinessConstants.RUN_AS_OF_DATE,
				new BasicDBObject("$gte", startRunAsOfDate).append("$lte", endRunAsOfDate));
		DBObject match = new BasicDBObject("$match", matchQuery);

		// group criterion
		Map<String, String> groupByCriterion = new HashMap<String, String>();

		// project
		// project to make the pipeline of filter data
		Map<String, Object> projectQuery = new HashMap<String, Object>();
		projectQuery.put(BusinessConstants.RUN_AS_OF_DATE,
				new BasicDBObject("$add", new Object[] { new Date(0), "$runAsOfDate" }));
		/*projectQuery.put(BusinessConstants.VERSION_NAME, 1);
		projectQuery.put(BusinessConstants.RUNTIME_CALL_START, 1);
		projectQuery.put(BusinessConstants.RUNTIME_CALL_END, 1);*/
		projectQuery.put(BusinessConstants.ERROR_CODE, 1);
		/*projectQuery.put(BusinessConstants.MODEL_CALL_START, 1);
		projectQuery.put(BusinessConstants.MODEL_CALL_END, 1);
		projectQuery.put(BusinessConstants.MINOR_VERSION, 1);
		projectQuery.put(BusinessConstants.MAJOR_VERSION, 1);*/
		projectQuery.put(BusinessConstants.STATUS, 1);

		DBObject project = new BasicDBObject("$project", projectQuery);

		Map<String, Object> projectMonth = new HashMap<String, Object>();
		/*projectMonth.put(BusinessConstants.VERSION_NAME, 1);
		projectMonth.put(BusinessConstants.RUNTIME_CALL_START, 1);
		projectMonth.put(BusinessConstants.RUNTIME_CALL_END, 1);*/
		projectMonth.put(BusinessConstants.ERROR_CODE, 1);
		/*projectMonth.put(BusinessConstants.MODEL_CALL_START, 1);
		projectMonth.put(BusinessConstants.MODEL_CALL_END, 1);
		projectMonth.put(BusinessConstants.MINOR_VERSION, 1);
		projectMonth.put(BusinessConstants.MAJOR_VERSION, 1);*/
		projectMonth.put(BusinessConstants.STATUS, 1);
		projectMonth.put(BusinessConstants.RUN_AS_OF_DATE, 1);

		projectMonth.put(BusinessConstants.YEAR, new BasicDBObject("$year", "$runAsOfDate"));
		groupByCriterion.put(BusinessConstants.YEAR, "$year");
		if (StringUtils.equalsIgnoreCase(BusinessConstants.MONTH, groupBy)) {
			projectMonth.put(BusinessConstants.MONTH, new BasicDBObject("$month", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.MONTH, "$month");
		} else if (StringUtils.equalsIgnoreCase(BusinessConstants.DAY, groupBy)) {
			projectMonth.put(BusinessConstants.MONTH, new BasicDBObject("$month", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.MONTH, "$month");
			projectMonth.put(BusinessConstants.DAY, new BasicDBObject("$dayOfMonth", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.DAY, "$day");
		} else {
			projectMonth.put(BusinessConstants.MONTH, new BasicDBObject("$month", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.MONTH, "$month");
			projectMonth.put(BusinessConstants.DAY, new BasicDBObject("$dayOfMonth", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.DAY, "$day");
			projectMonth.put(BusinessConstants.HOUR, new BasicDBObject("$hour", "$runAsOfDate"));
			groupByCriterion.put(BusinessConstants.HOUR, "$hour");
		}

		DBObject projectPipeline = new BasicDBObject("$project", projectMonth);

		/*// Group By
		if(!StringUtils.equalsIgnoreCase(selectionType, BusinessConstants.ALL)) {
		groupByCriterion.put(BusinessConstants.MODEL_NAME, "$versionName");
		groupByCriterion.put(BusinessConstants.MAJOR_VERSION, "$majorVersion");
		groupByCriterion.put(BusinessConstants.MINOR_VERSION, "$minorVersion");
		}*/

		Map<String, Object> groupMap = new HashMap<String, Object>();
		groupMap.put(BusinessConstants.GROUP_ID, groupByCriterion);

		// Count Success Count
		DBObject successEq = new BasicDBObject("$eq", new Object[] { "$status", "Success" });
		DBObject successCond = new BasicDBObject("$cond", new Object[] { successEq, 1, 0 });
		groupMap.put(BusinessConstants.SUCCESS, new BasicDBObject("$sum", successCond));

		groupMap.put(BusinessConstants.TOTAL, new BasicDBObject("$sum", 1));

		// RSE[8041,8042,.....] MODEL FAILURES Count
		Object[] subSet = new Object[] { "$errorCode" };
		Object[] set = new Object[] { "RSE008041", "RSE008042", "RSE008043", "RSE008044", "RSE008045", "RSE008046",
				"RSE008047", "RSE008048" };
		DBObject modelError = new BasicDBObject("$setIsSubset", new Object[] { subSet, set });

		DBObject errorCondition = new BasicDBObject("$cond", new Object[] { modelError, 1, 0 });

		// RSE[....,.....] OTHER FAILURES Count
		Object[] failSubSet = new Object[] { "$errorCode" };
		Object[] failSet = new Object[] { "RSE000002", "RSE000003", "RSE000100", "RSE000101", "RSE000102", "RSE000103",
				"RSE000104", "RSE000401", "RSE000501", "RSE000502", "RSE000508", "RSE000509", "RSE000510", "RSE000511",
				"RSE000800", "RSE000801", "RSE000802", "RSE000803", "RSE000804", "RSE000805", "RSE000806", "RSE000807",
				"RSE000808", "RSE000809", "RSE000812", "RSE000813", "RSE000816", "RSE000817", "RSE000818", "RSE000819",
				"RSE000821", "RSE000822", "RSE000830", "RSE000831", "RSE000832", "RSE000833", "RSE000835", "RSE000905",
				"RSE000930" };
		DBObject otherError = new BasicDBObject("$setIsSubset", new Object[] { failSubSet, failSet });

		DBObject failcond = new BasicDBObject("$cond", new Object[] { otherError, 1, 0 });

		groupMap.put(BusinessConstants.OTHER_FAILURES, new BasicDBObject("$sum", failcond));

		groupMap.put(BusinessConstants.MODEL_FAILURES, new BasicDBObject("$sum", errorCondition));

		// RVE INPUT VALIDATION FAILURES Count
		DBObject errorRVE = new BasicDBObject("$substr", new Object[] { "$errorCode", 0, 3 });
		DBObject eqCond = new BasicDBObject("$eq", new Object[] { errorRVE, "RVE" });

		DBObject inputValFlr = new BasicDBObject("$cond", new Object[] { eqCond, 1, 0 });
		groupMap.put(BusinessConstants.INPUT_VALIDATION_FAILURES, new BasicDBObject("$sum", inputValFlr));

		// RVE OUT VALIDATION FAILURES Count
		DBObject errorRMV = new BasicDBObject("$substr", new Object[] { "$errorCode", 0, 3 });
		DBObject rmvEqCond = new BasicDBObject("$eq", new Object[] { errorRMV, "RMV" });

		DBObject outputValFlr = new BasicDBObject("$cond", new Object[] { rmvEqCond, 1, 0 });
		groupMap.put(BusinessConstants.OUTPUT_VALIDATION_FAILURES, new BasicDBObject("$sum", outputValFlr));

		/*// MODEL RESPONSE TIME
		DBObject subTime = new BasicDBObject("$subtract", new Object[] { "$modelCallEnd", "$modelCallStart" });
		DBObject ifNull = new BasicDBObject("$ifNull", new Object[] { "$modelCallEnd", "true" });
		DBObject eqCon = new BasicDBObject("$eq", new Object[] { ifNull, "true" });
		DBObject modelTime = new BasicDBObject("$cond", new Object[] { eqCon, new Long(0), subTime });
		groupMap.put(BusinessConstants.MODEL_RESPONSE_TIME, new BasicDBObject("$push", modelTime));
		
		// END TO END TIME
		DBObject endsubTime = new BasicDBObject("$subtract", new Object[] { "$runtimeCallEnd", "$runtimeCallStart" });
		DBObject endtimeifNull = new BasicDBObject("$ifNull", new Object[] { "$runtimeCallEnd", "true" });
		DBObject runtimeEndeqCon = new BasicDBObject("$eq", new Object[] { endtimeifNull, "true" });
		DBObject entToendTime = new BasicDBObject("$cond", new Object[] { runtimeEndeqCon, new Long(0), endsubTime });
		groupMap.put(BusinessConstants.END_TO_END_TIME, new BasicDBObject("$push", entToendTime));
		
		DBObject totalSubTime = new BasicDBObject("$subtract", new Object[] { "$modelCallEnd", "$modelCallStart" });
		groupMap.put(BusinessConstants.MODEL_UTILISATION, new BasicDBObject("$sum", totalSubTime));
		*/
		DBObject group = new BasicDBObject("$group", groupMap);

		Long timeout = StringUtils
				.isNotBlank(systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
						? Long.parseLong(
								systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
						: MAX_TIME_PRIMARY_MS;

		LOGGER.info("Timeout for mongo is (Basic Search):" + (timeout / 1000.0) + " seconods");

		AggregationOptions aggregationOptions = AggregationOptions.builder().maxTime(timeout, TimeUnit.MILLISECONDS)
				.build();

		long startTime = System.currentTimeMillis();
		List<? extends DBObject> pipeline = Arrays.asList(match, project, projectPipeline, group);
		Cursor cursor = collectionFromTemplate.aggregate(pipeline, aggregationOptions);
		long endTime = System.currentTimeMillis();
		LOGGER.info(" Time taken for fetching All usage dynamics  for Tenant {}  from mongo is  : {} ms", tenantCode,
				(endTime - startTime));
		return cursor;
	}

	@Override
	public Cursor fetchTransactionCount(Long runAsOfDate) {
		DBCollection collectionFromTemplate = getDbCollectionFromTempate();
		Map<String, Object> matchQuery = new HashMap<String, Object>();
		matchQuery.put(BusinessConstants.IS_TEST, false);
		matchQuery.put(BusinessConstants.RUN_AS_OF_DATE, new BasicDBObject("$gte", runAsOfDate));
		DBObject match = new BasicDBObject("$match", matchQuery);

		Map<String, Object> projectQuery = new HashMap<String, Object>();
		projectQuery.put(BusinessConstants.VERSION_NAME, 1);
		projectQuery.put(BusinessConstants.RUN_AS_OF_DATE,
				new BasicDBObject("$add", new Object[] { new Date(0), "$runAsOfDate" }));
		DBObject project = new BasicDBObject("$project", projectQuery);

		Map<String, Object> projectMonth = new HashMap<String, Object>();
		projectMonth.put(BusinessConstants.MONTH, new BasicDBObject("$month", "$runAsOfDate"));
		projectMonth.put(BusinessConstants.VERSION_NAME, 1);
		projectMonth.put(BusinessConstants.RUN_AS_OF_DATE, 1);
		DBObject projectMon = new BasicDBObject("$project", projectMonth);

		Map<String, Object> groupBy = new HashMap<String, Object>();
		groupBy.put(BusinessConstants.INTERVAL, "$month");
		groupBy.put(BusinessConstants.VERSION_NAME, "$versionName");

		Map<String, Object> groupMap = new HashMap<String, Object>();
		groupMap.put(BusinessConstants.GROUP_ID, groupBy);
		groupMap.put(BusinessConstants.TRANSACTION_COUNT, new BasicDBObject("$sum", 1));
		DBObject group = new BasicDBObject("$group", groupMap);

		Long timeout = StringUtils
				.isNotBlank(systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
						? Long.parseLong(
								systemParameterProvider.getParameter(BusinessConstants.MAX_WAIT_TIME_PRIMARY_SEARCH))
						: MAX_TIME_PRIMARY_MS;

		LOGGER.info("Timeout for mongo is (Basic Search):" + (timeout / 1000.0) + " seconods");

		AggregationOptions aggregationOptions = AggregationOptions.builder().maxTime(timeout, TimeUnit.MILLISECONDS)
				.build();

		long startTime = System.currentTimeMillis();
		List<? extends DBObject> pipeline = Arrays.asList(match, project, projectMon, group);
		Cursor cursor = collectionFromTemplate.aggregate(pipeline, aggregationOptions);
		long endTime = System.currentTimeMillis();
		LOGGER.info(" Time taken for fetching total transation group by version name  from mongo is  : "
				+ (endTime - startTime) + " ms");
		return cursor;
	}

	// added for umg-4849
	private static DBObject getProjectionObjectForApi() {
		DBObject projectionObj = new BasicDBObject();
		Field fld[] = TransactionDocumentForApi.class.getDeclaredFields();
		for (Field x : fld) {
			projectionObj.put(x.getName(), 1);
		}
		return projectionObj;
	}

	// added for umg-4849
	private DBObject setProjectionForApi(DBObject projectionObj, TransactionFilterForApi transactionFilterForApi) {
		if (transactionFilterForApi != null
				&& (transactionFilterForApi.getIncludeTntOutput() || transactionFilterForApi.getIncludeTntInput())) {
			Boolean projectionObjModified = Boolean.FALSE;
			if (transactionFilterForApi.getIncludeTntOutput()) {
				if (CollectionUtils.isNotEmpty(transactionFilterForApi.getPayloadOutputFields())) {
					projectionObj = getProjectionObjectForApi();
					setPayloadFieldsToProjection(projectionObj, transactionFilterForApi.getPayloadOutputFields());
					projectionObjModified = Boolean.TRUE;
				} else {
					projectionObj.removeField("tenantOutput");
				}
			}

			if (transactionFilterForApi.getIncludeTntInput()) {
				if (CollectionUtils.isNotEmpty(transactionFilterForApi.getPayloadInputFields())) {
					if (CollectionUtils.isEmpty(transactionFilterForApi.getPayloadOutputFields())) {
						projectionObj = getProjectionObjectForApi();
						setPayloadFieldsToProjection(projectionObj, transactionFilterForApi.getPayloadInputFields());
						projectionObj.put("tenantOutput", 1);
						projectionObjModified = Boolean.TRUE;
					} else {
						setPayloadFieldsToProjection(projectionObj, transactionFilterForApi.getPayloadInputFields());
					}
				} else {
					if (CollectionUtils.isEmpty(transactionFilterForApi.getPayloadOutputFields())) {
						projectionObj.removeField("tenantInput");
					} else {
						projectionObj.put("tenantInput", 1);
					}
				}
			}

			if (!transactionFilterForApi.getIncludeTntInput() && projectionObjModified) {
				projectionObj.removeField("tenantInput");
			}

			if (!transactionFilterForApi.getIncludeTntOutput() && projectionObjModified) {
				projectionObj.removeField("tenantOutput");
			}
		}

		return projectionObj;
	}

	// added for umg-4849
	private void setPayloadFieldsToProjection(DBObject projectionObj, List<String> payloadFields) {
		for (String payloadField : payloadFields) {
			projectionObj.put(payloadField, 1);
		}
	}

	private static byte[] getFile(String sanBase, TransactionDocument txnDocument, String fileType)
			throws SystemException, IOException {
		String fileName = txnDocument.getTransactionId() + PoolConstants.ENV_SEPERATOR + fileType
				+ BusinessConstants.JSON_EXTENSION;
		byte[] fileBytes = null;
		File file = null;
		if (StringUtils.isNotEmpty(sanBase)) {
			StringBuilder buffer = new StringBuilder(sanBase);
			buffer.append(File.separatorChar).append(getRequestContext().getTenantCode()).append(File.separatorChar)
					.append(BusinessConstants.DOCUMENTS_FOLDER).append(File.separatorChar).append(fileName);
			String absoluteFileName = buffer.toString();
			file = new File(absoluteFileName);
		} else {
			LOGGER.error("San base not available");
		}
		if (file != null) {
			fileBytes = Files.readAllBytes(file.toPath());
		}
		return fileBytes;
	}

}
