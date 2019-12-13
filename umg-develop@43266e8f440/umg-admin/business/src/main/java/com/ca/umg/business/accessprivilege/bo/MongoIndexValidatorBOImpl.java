package com.ca.umg.business.accessprivilege.bo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.hazelcaststats.info.IndexInfo;
import com.ca.umg.business.tenant.bo.TenantBO;
import com.ca.umg.business.tenant.entity.Tenant;
import com.ca.umg.business.util.AdminUtil;
import com.mongodb.DB;
import com.mongodb.DBObject;

@Named
public class MongoIndexValidatorBOImpl implements MongoIndexValidatorBO {

	@Inject
	private MongoTemplate mongoTemplate;

	@Inject
	private TenantBO tenantBO;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	private static final String YES = "YES";
	private static final String NO = "NO";
	private static final String UNIQUE = "unique";
	private static final String NAME = "name";
	private static final String KEY = "key";
	private static final String TRANSACTIONID = "transactionId";

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoIndexValidatorBOImpl.class);

	@Override
	public Map<Object, Object> getIndexStatusResult() {

		Map<Object, Object> finalMatch = new HashMap<>();
		DB db = mongoTemplate.getDb();

		try {
			AdminUtil.setAdminAwareTrue();
			List<Tenant> tenantList = tenantBO.listAll();
			List<IndexInfo> masterIndexIOList = getMasterIndexesIO();
			List<IndexInfo> masterIndexMainList = getMasterIndexesMain();
			finalMatch = getIndexStatus(tenantList, masterIndexIOList, masterIndexMainList, db);
		} catch (BusinessException e) {
			LOGGER.error(e.getMessage());
		} catch (SystemException e) {
			LOGGER.error(e.getMessage());
		} finally {
			AdminUtil.setAdminAwareFalse();
		}

		return finalMatch;
	}

	private Map<Object, Object> getIndexStatus(List<Tenant> tenantList, List<IndexInfo> masterIndexIOList,
			List<IndexInfo> masterIndexMainList, DB db) {
		Map<Object, Object> preResponse = new HashMap<>();

		String collectionName = "";
		for (Tenant tenant : tenantList) {
			String tenantCode = tenant.getCode();
			Map<String, String> tempResponse = new LinkedHashMap<>();
			collectionName = FrameworkConstant.MODELINPUT + FrameworkConstant.DOCUMENTS;
			List<DBObject> tenantIndexes = db.getCollection(tenantCode + FrameworkConstant.UNDERSCORE
					+ FrameworkConstant.MODELINPUT + FrameworkConstant.DOCUMENTS).getIndexInfo();
			tempResponse.putAll(getDocAndIndex(tenantCode, collectionName, tenantIndexes, masterIndexIOList));
			preResponse.put(tenantCode, tempResponse);

			tenantIndexes.clear();
			collectionName = FrameworkConstant.MODELOUTPUT + FrameworkConstant.DOCUMENTS;
			tenantIndexes.addAll(db.getCollection(tenantCode + FrameworkConstant.UNDERSCORE
					+ FrameworkConstant.MODELOUTPUT + FrameworkConstant.DOCUMENTS).getIndexInfo());
			tempResponse.putAll(getDocAndIndex(tenantCode, collectionName, tenantIndexes, masterIndexIOList));
			preResponse.put(tenantCode, tempResponse);

			tenantIndexes.clear();
			collectionName = FrameworkConstant.TENANTINPUT + FrameworkConstant.DOCUMENTS;
			tenantIndexes.addAll(db.getCollection(tenantCode + FrameworkConstant.UNDERSCORE
					+ FrameworkConstant.TENANTINPUT + FrameworkConstant.DOCUMENTS).getIndexInfo());
			tempResponse.putAll(getDocAndIndex(tenantCode, collectionName, tenantIndexes, masterIndexIOList));
			preResponse.put(tenantCode, tempResponse);

			tenantIndexes.clear();
			collectionName = FrameworkConstant.TENANTOUTPUT + FrameworkConstant.DOCUMENTS;
			tenantIndexes.addAll(db.getCollection(tenantCode + FrameworkConstant.UNDERSCORE
					+ FrameworkConstant.TENANTOUTPUT + FrameworkConstant.DOCUMENTS).getIndexInfo());
			tempResponse.putAll(getDocAndIndex(tenantCode, collectionName, tenantIndexes, masterIndexIOList));
			preResponse.put(tenantCode, tempResponse);

			tenantIndexes.clear();
			collectionName = tenantCode + FrameworkConstant.DOCUMENTS;
			tenantIndexes.addAll(db.getCollection(tenantCode + FrameworkConstant.DOCUMENTS).getIndexInfo());
			tempResponse.putAll(getDocAndIndex(tenantCode, collectionName, tenantIndexes, masterIndexMainList));
			preResponse.put(tenantCode, tempResponse);
		}
		return preResponse;
	}

	private Map<String, String> getDocAndIndex(String tenantCode, String collectionName, List<DBObject> tenantIndexes,
			List<IndexInfo> masterIndexIOList) {
		Map<String, String> tempMap = new LinkedHashMap<>();
		for (IndexInfo master : masterIndexIOList) {

			String masterObjName = master.getName();
			String masterUniqueVal = master.getUnique() != null ? master.getUnique().toString()
					: Boolean.FALSE.toString().toLowerCase();

			String docname = collectionName.equalsIgnoreCase(tenantCode + FrameworkConstant.DOCUMENTS)
					? FrameworkConstant.DOCUMENTS + BusinessConstants.SLASH + masterObjName
					: collectionName + BusinessConstants.SLASH + masterObjName;

			tempMap.put(docname,
					verifyifIndexExists(tenantIndexes, masterObjName, masterUniqueVal, master.getKey()) ? YES : NO);

		}

		return tempMap;
	}

	private boolean verifyifIndexExists(List<DBObject> tenantIndexes, String masterObjName, String masterUniqueVal,
			Map<String, Object> masterKey) {
		boolean flag = false;
		Map<String, Object> masterKey2 = new HashMap<>();
		masterKey2.put(TRANSACTIONID, -1);
		for (DBObject dbobj : tenantIndexes) {

			String dbobjName = (String) dbobj.get(NAME);
			String dbobjUnique = dbobj.get(UNIQUE) != null ? String.valueOf(dbobj.get(UNIQUE))
					: Boolean.FALSE.toString().toLowerCase();

			Map<String, Object> dObjectMap = getdoubleQuotesRemoved((Map<String, Object>) dbobj.get(KEY));
			if (StringUtils.equalsIgnoreCase(masterObjName, dbobjName)
					&& StringUtils.equalsIgnoreCase(masterUniqueVal, dbobjUnique)
					&& (masterKey.equals(dObjectMap) || masterKey2.equals(dObjectMap))) {
				flag = true;
				break;
			}
		}

		return flag;
	}

	@SuppressWarnings("resource")
	private List<IndexInfo> getMasterIndexesMain() {
		String jsnMstrMainString = "";
		ObjectMapper mapper = new ObjectMapper();
		List<IndexInfo> masterIndexMainList = new ArrayList<>();
		FileReader fr = null;
		try {

			File file = new File(systemParameterProvider.getParameter(SystemConstants.INDEX_FILE_TEMP_PATH)
					+ File.separatorChar + "MasterIndex_MainDoc.json");
			LOGGER.info("Path for MasterIndex_MainDoc.json :" + file.toString());
			fr = new FileReader(file);
			BufferedReader readerMain = new BufferedReader(fr);
			jsnMstrMainString = readerMain.readLine();
			if (!StringUtils.isEmpty(jsnMstrMainString) && !StringUtils.isBlank(jsnMstrMainString)) {
				masterIndexMainList = (List<IndexInfo>) mapper.readValue(jsnMstrMainString,
						new TypeReference<List<IndexInfo>>() {
						});

			}

		} catch (IOException e) {
			LOGGER.info(e.getMessage());
		}

		finally {
			if (fr != null) {
				safeClose(fr);
			}
		}
		return masterIndexMainList;
	}

	private void safeClose(FileReader fr) {
		if (fr != null) {
			try {
				fr.close();
			} catch (IOException e) {
				LOGGER.error("Exception occurred in closing filereader :" + e.getMessage());
			}
		}
	}

	@SuppressWarnings("resource")
	private List<IndexInfo> getMasterIndexesIO() {
		String jsnMstrIOString = "";
		ObjectMapper mapper = new ObjectMapper();
		List<IndexInfo> masterIndexIOList = new ArrayList<>();
		FileReader fr = null;
		try {
			File file = new File(systemParameterProvider.getParameter(SystemConstants.INDEX_FILE_TEMP_PATH)
					+ File.separatorChar + "MasterIndex_IO.json");
			LOGGER.info("Path for MasterIndex_IO.json :" + file.toString());
			fr = new FileReader(file);
			BufferedReader readerIO = new BufferedReader(fr);
			jsnMstrIOString = readerIO.readLine();
			if (!StringUtils.isEmpty(jsnMstrIOString) && !StringUtils.isBlank(jsnMstrIOString)) {
				masterIndexIOList = (List<IndexInfo>) mapper.readValue(jsnMstrIOString,
						new TypeReference<List<IndexInfo>>() {
						});

			}

		} catch (IOException e) {
			LOGGER.info(e.getMessage());
		} finally {
			if (fr != null) {
				safeClose(fr);
			}
		}

		return masterIndexIOList;
	}

	private Map<String, Object> getdoubleQuotesRemoved(Map<String, Object> dboKeyMap) {
		Map<String, Object> dObjectMap = new HashMap<>();
		for (Map.Entry<String, Object> entry : dboKeyMap.entrySet()) {
			String key1 = entry.getKey().replace("\"", "").trim();
			Object v1 = entry.getValue();
			dObjectMap.put(key1, v1);
		}
		return dObjectMap;
	}

}