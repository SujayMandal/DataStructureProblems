/*
\ * SyndicateDataBOImpl.java
 *
 * -----------------------------------------------------------
 * Copyright 2012 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.syndicatedata.bo;

import static org.springframework.data.jpa.domain.Specifications.where;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.common.info.PagingInfo;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.syndicatedata.dao.SyndicateDataDAO;
import com.ca.umg.business.syndicatedata.dao.SyndicateVersionDataDAO;
import com.ca.umg.business.syndicatedata.daohelper.SyndicateVersionDataHelper;
import com.ca.umg.business.syndicatedata.entity.SyndicateData;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataKeyColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataKeyInfo;
import com.ca.umg.business.syndicatedata.specification.SyndicateDataSpecification;
import com.ca.umg.business.syndicatedata.util.DataTypes;
import com.ca.umg.business.syndicatedata.util.SyndicateUtil;
import com.ca.umg.business.tenant.dao.SystemKeyDAO;
import com.ca.umg.business.tenant.entity.SystemKey;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.util.SyndicateDataUtil;

/**
 * Syndicate Data DAO class( that is repository class for SyndicateData bean)
 * implementation.
 * 
 * @author mandavak
 * 
 */

@Service
public class SyndicateDataBOImpl implements SyndicateDataBO {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SyndicateDataBOImpl.class);

	@Inject
	private SyndicateDataDAO syndicatedataDAO;

	@Inject
	private SyndicateVersionDataDAO versionDataDAO;

	@Inject
	private SyndicateVersionDataHelper sDataHelper;

	@Inject
	private SyndicateDataBODeleteHelper deleteHelper;

	@Inject
	private SystemKeyDAO systemKeyDAO;

	/**
	 * List all versions of a given container
	 * 
	 * @param containerName
	 * @return List
	 * @throws BusinessException
	 */
	@Override
	public List<SyndicateData> findSyndicateContainerVersions(
			String containerName) throws BusinessException {

		if (StringUtils.isEmpty(containerName)) {
			LOGGER.debug("container name {} is empty", containerName);
			throw new BusinessException(BusinessExceptionCodes.BSE000012,
					new String[] { "Container Name is empty or null" });
		}

        List<SyndicateData> syndDataList = syndicatedataDAO.findByContainerNameOrderByVersionIdDesc(containerName);
        if (syndDataList != null) {
        LOGGER.info("No of versions for the container :" + containerName + " are :" + syndDataList.size());
        }
        return syndDataList;
	}

	/**
	 * 
	 * Find's Syndicated data with maximum or minimum version of all containers.
	 * 
	 * @param containerName
	 * @return syndicateData
	 */

	@Override
	public SyndicateData findSyndicateDataContainer(String containerName)
			throws BusinessException, SystemException {

		if (StringUtils.isEmpty(containerName)) {
			LOGGER.debug("BSE000012 container id {} is empty or null",
					containerName);
			throw new BusinessException(BusinessExceptionCodes.BSE000012,
					new String[] { "Container Name is empty or null." });
		}

		List<SyndicateData> syndicateDataList = syndicatedataDAO
				.getMinMaxVerContainer(containerName);

		// based of flag set keydefinition

		if (CollectionUtils.isEmpty(syndicateDataList)) {
			LOGGER.debug("SyndicateDataList is empty for a given container {}",
					containerName);
			throw new BusinessException(BusinessExceptionCodes.BSE000018,
					new String[] { containerName });
		}

		SyndicateData syndicateData = null;
		if (CollectionUtils.isNotEmpty(syndicateDataList)) {
			for (SyndicateData temp : syndicateDataList) {
				if (syndicateData == null) {
					syndicateData = temp;
				} else {
					syndicateData.setValidTo(temp.getValidTo());
				}
			}
		}
		return syndicateData;

	}

	/**
	 * Fetche's Syndicate Data for a given versionId and containerName.
	 * 
	 * @param versionId
	 * @param containerName
	 * 
	 * @return syndicateData
	 * 
	 */
	@Override
	public SyndicateData findSyndicateDataByVersionId(Long versionId,
			String containerName) {
		return syndicatedataDAO.findByVersionIdAndContainerName(versionId,
				containerName);
	}

	/**
	 * fetches Syndicate Data which is either maximum version or Minimum version
	 * of all containers
	 * 
	 * @return List
	 * @throws SystemException
	 */
	@Override
	public List<SyndicateData> getContainers() throws SystemException {
		return syndicatedataDAO.getMinVersionOfEachContainer();
	}

	@Override
	public Page<SyndicateData> getContainers(SearchOptions pageInfo)
			throws BusinessException, SystemException {
		Long fromDate = null;
		Long tillDate = null;
		if (pageInfo.getFromDate() != null && !pageInfo.getFromDate().isEmpty()) {
			fromDate = AdminUtil.getMillisFromEstToUtc(pageInfo.getFromDate(),
					BusinessConstants.LIST_SEARCH_DATE_FORMAT);
		}
		if (pageInfo.getToDate() != null && !pageInfo.getToDate().isEmpty()) {
			tillDate = AdminUtil.getMillisFromEstToUtc(pageInfo.getToDate(),
					BusinessConstants.LIST_SEARCH_DATE_FORMAT);
		}
		Pageable pageRequest = getPagingInformation(pageInfo,
				pageInfo.getSortColumn());
		Specification<SyndicateData> withContainerName = SyndicateDataSpecification
				.withContainerNameLike(pageInfo.getSearchText());
		Specification<SyndicateData> withVersionName = SyndicateDataSpecification
				.withVersionNameLike(pageInfo.getSearchText());
		Specification<SyndicateData> withVersionDesc = SyndicateDataSpecification
				.withVersionDescLike(pageInfo.getSearchText());
		Specification<SyndicateData> withCreatedBy = SyndicateDataSpecification
				.withCreatedByLike(pageInfo.getSearchText());
		Specification<SyndicateData> withUpdatedBy = SyndicateDataSpecification
				.withUpdatedByLike(pageInfo.getSearchText());
		Specification<SyndicateData> withCreatedDateFrom = SyndicateDataSpecification
				.withCreatedDateFrom(fromDate);
		Specification<SyndicateData> withCreatedDateTill = SyndicateDataSpecification
				.withCreatedDateTill(tillDate);
		/*Specification<SyndicateData> withVersionId = SyndicateDataSpecification
				.withVersionId(1L);*/
		return syndicatedataDAO.findAll(
                where(withCreatedDateFrom)
						.and(withCreatedDateTill)
						.and(where(withContainerName).or(withVersionName)
								.or(withVersionDesc).or(withCreatedBy)
								.or(withUpdatedBy)), pageRequest);
	}

	/**
	 * Prepares paging request
	 * 
	 * @param pagingInfo
	 * @param sortColumn
	 * @return
	 */
	private Pageable getPagingInformation(PagingInfo pagingInfo,
			String sortColumn) {
		Direction direction = pagingInfo.isDescending() ? Sort.Direction.DESC
				: Sort.Direction.ASC;
		String newSortColumn = sortColumn;
		Order[] sortOrders = null;
		Order order = null;
		if (StringUtils.isBlank(newSortColumn)) {
			order = new Order(direction, "containerName").ignoreCase();
			sortOrders = new Order[] { order };
		} else {
			order = new Order(direction, newSortColumn).ignoreCase();
			sortOrders = new Order[] { order };
		}

		Sort sort = new Sort(sortOrders);
		return new PageRequest(pagingInfo.getPage() == 0 ? 0
				: pagingInfo.getPage() - 1, pagingInfo.getPageSize(), sort);
	}

	/**
	 * Retrieves indexes information for a given database table.
	 */
	@Override
	public Map<String, List<String>> getTableKeys(String tableName)
			throws BusinessException, SystemException {
		if (StringUtils.isEmpty(tableName)) {
			LOGGER.debug("TableName Empty {} is empty or null", tableName);
			throw new BusinessException(BusinessExceptionCodes.BSE000019,
					new String[] { "TableName Empty is empty or null." });
		}
		return versionDataDAO.getTableKeys(tableName);
	}

	/**
	 * Retrieves column information for a given database table.
	 */
	@Override
	public List<SyndicateDataColumnInfo> getTableColumnInfo(String tableName)
			throws BusinessException, SystemException {

		if (StringUtils.isEmpty(tableName)) {
			LOGGER.debug("TableName Empty {} is empty or null", tableName);
			throw new BusinessException(BusinessExceptionCodes.BSE000019,
					new String[] { "TableName Empty is empty or null." });
		}
		return versionDataDAO.getTableColumnInfo(tableName);
	}

	/**
	 * Delete a version of a syndicate data container.
	 * 
	 * @param versionId
	 * @param containerName
	 * @throws BusinessException
	 * @throws SystemException
	 */
	@Override
	public void delete(Long versionId, String containerName)
			throws BusinessException, SystemException {
		deleteHelper.delete(containerName, versionId);
	}

	/**
	 * create syndicate data dynamic table with columns from list of
	 * {@code SyndicateDataColumnInfo}
	 */
	@Override
	public void createSyndicateDataTable(String containerName,
			List<SyndicateDataColumnInfo> dataColumnInfos) {
		String createTabQuery = sDataHelper.createTableQuery(containerName,
				dataColumnInfos);
		versionDataDAO.executeQuery(createTabQuery);
		LOGGER.debug("CreateTableQuery [{}] executed successfully.",
				createTabQuery);
	}

	/**
	 * Creates a new version
	 */
	@Override
    public SyndicateData createSyndicateDataVersion(final SyndicateData syndicateData) {
        SyndicateData syndicateDataMax = ObjectUtils.defaultIfNull(
                syndicatedataDAO.findProviderMaxVersion(syndicateData.getContainerName()), new SyndicateData());
        Long versionId = syndicateDataMax.getVersionId();
        LOGGER.debug("versionId {} find as maximum Version Id", versionId);
        Long minuteMillis = BusinessConstants.DOUBLE_SIX_THOUSAND;
        if (syndicateDataMax.getId() != null) {
            syndicateDataMax.setValidTo(syndicateData.getValidFrom() - minuteMillis);
            LOGGER.debug("updating the syndicate version :{}  with valid to : {}", syndicateDataMax.getVersionId(),
                    syndicateDataMax.getValidTo());
            syndicatedataDAO.save(syndicateDataMax);
            LOGGER.debug("updation is done for the versionName : {} and versionId : {}", syndicateDataMax.getVersionName(),
                    syndicateDataMax.getVersionId());
        }
        if (versionId == null) {
            versionId = 1l;
        } else {
            versionId = versionId + 1l;
        }
        syndicateData.setVersionId(versionId);
        LOGGER.debug("new Version id is :" + syndicateData.getVersionId());
        syndicateData.setTableName(AdminUtil.generateSyndDataTableName(syndicateData.getContainerName()));
        LOGGER.debug("{} container creating", syndicateData.getContainerName());
        return syndicatedataDAO.save(syndicateData);
    }

	/**
	 * Creates indexes on the dynamic table.
	 */
	@Override
	public void createSyndicateDataKeyDefs(String tableName,
			List<SyndicateDataKeyInfo> dataKeyInfos) throws BusinessException,
			SystemException {
		if (CollectionUtils.isEmpty(dataKeyInfos)) {
			LOGGER.info("There are no keys defined for the given table {}",
					tableName);
			return;
		}
		StringBuilder indexesQuery = null;
		for (SyndicateDataKeyInfo keyDef : dataKeyInfos) {
			indexesQuery = new StringBuilder(BusinessConstants.NUMBER_FIFTY);
			indexesQuery
					.append("CREATE INDEX ")
					.append(tableName.toUpperCase(Locale.getDefault()))
					.append('_')
					.append(keyDef.getKeyName()
							.toUpperCase(Locale.getDefault())).append(" ON ")
					.append(tableName).append('(');
			for (SyndicateDataKeyColumnInfo keyColumnInfo : keyDef
					.getsColumnInfos()) {
				if (keyColumnInfo.isStatus()) {
					indexesQuery.append(keyColumnInfo.getColumnName()).append(
							',');
				}
			}
			indexesQuery.setCharAt(indexesQuery.length() - 1, ' ');
			indexesQuery.append(");");
			versionDataDAO.executeQuery(indexesQuery.toString());
			LOGGER.info("Index created successfully: {}",
					indexesQuery.toString());
		}
	}

	@Override
	public void deleteSyndicateDataKeyDefs(String containerName)
			throws BusinessException, SystemException {
		String tableName = AdminUtil.generateSyndDataTableName(containerName);
		Map<String, List<String>> indexes = versionDataDAO
				.getTableKeys(tableName);
		List<String> dropIndexQueries = sDataHelper.dropIndexesQueries(
				tableName, indexes.keySet());
		for (String query : dropIndexQueries) {
			versionDataDAO.executeQuery(query);
			LOGGER.debug("{} query executed successfully", query);
		}
	}

	/**
	 * Insert data into dynamic table.
	 */
	@Override
	public void insertSyndicateData(SyndicateDataContainerInfo containerInfo,
			SyndicateData syndicateData) throws BusinessException,
			SystemException {
		String[] queries = sDataHelper.insertDataStatements(containerInfo,
				syndicateData);
		int totalQueries = queries.length;
		int inputLimit = BusinessConstants.NUMBER_FIVE_THOUSAND;
		int startIndex = BusinessConstants.NUMBER_ZERO;
		int endIndex = BusinessConstants.NUMBER_ZERO;
		while (endIndex < totalQueries) {
			endIndex = Math.min(startIndex + inputLimit, totalQueries);
			versionDataDAO.insertData(ArrayUtils.subarray(queries, startIndex,
					endIndex));
			LOGGER.debug("[{}] quries executed successfully.",
					StringUtils.join(queries, BusinessConstants.CHAR_PIPE));
			startIndex = endIndex;
		}
	}

	/**
	 * 
	 * update container information.
	 * 
	 * @param sContainerInfo
	 * @throws BusinessException
	 * @throws SystemException
	 */
	@Override
	public void updateContainerInfor(SyndicateDataContainerInfo sContainerInfo)
			throws BusinessException, SystemException {
		SyndicateData syndicateData = syndicatedataDAO
				.findFirstProviderVersion(sContainerInfo.getContainerName());
		if (syndicateData == null) {
			LOGGER.debug("SyndicateDataList is empty for a given container {}",
					sContainerInfo.getContainerName());
			throw new BusinessException(BusinessExceptionCodes.BSE000018,
					new String[] { sContainerInfo.getContainerName() });
		}
		String tableName = syndicateData.getTableName();

		createSyndicateDataKeyDefs(tableName,
				sContainerInfo.getKeyDefinitions());

		syndicateData.setDescription(sContainerInfo.getDescription());
		syndicatedataDAO.save(syndicateData);
	}

	/**
	 * Updates the Syndicate Data provider. Only valid from and valid to are the
	 * fields eligible for update
	 * 
	 * @param sContainerInfo
	 * @throws BusinessException
	 * @throws SystemException
	 */
	@Override
	public SyndicateData updateSyndicateDataVersion(SyndicateData syndicateData)
			throws BusinessException, SystemException {
		SyndicateData syndicateDataDB = findSyndicateDataByVersionId(
				syndicateData.getVersionId(), syndicateData.getContainerName());
		syndicateDataDB.setDescription(syndicateData.getDescription());
		syndicateDataDB.setValidFrom(syndicateData.getValidFrom());
		syndicateDataDB.setValidTo(syndicateData.getValidTo());
		syndicateDataDB.setVersionDescription(syndicateData
				.getVersionDescription());
		Pageable top = new PageRequest(0, 1);
		List<SyndicateData> syndicateDataPrevVers = syndicatedataDAO
				.findByContainerNameAndVersionIdLessThanOrderByVersionIdDesc(
						syndicateData.getContainerName(),
						syndicateData.getVersionId(), top);
		List<SyndicateData> syndicateDataNextVers = syndicatedataDAO
				.findByContainerNameAndVersionIdGreaterThanOrderByVersionIdAsc(
						syndicateData.getContainerName(),
						syndicateData.getVersionId(), top);
		SyndicateData syndicateDataPrev = null;
		SyndicateData syndicateDataNext = null;
		Long minuteMillis = BusinessConstants.DOUBLE_SIX_THOUSAND;
		if (CollectionUtils.isNotEmpty(syndicateDataPrevVers)) {
			syndicateDataPrev = syndicateDataPrevVers.get(0);
			syndicateDataPrev.setValidTo(syndicateDataDB.getValidFrom()
					- minuteMillis);
			syndicatedataDAO.save(syndicateDataPrev);
			LOGGER.debug("Previous version({}) saved successfully.",
					syndicateDataPrev.getVersionId());
		}
		if (CollectionUtils.isNotEmpty(syndicateDataNextVers)) {
			syndicateDataNext = syndicateDataNextVers.get(0);
			syndicateDataNext.setValidFrom(syndicateDataDB.getValidTo()
					+ minuteMillis);
			syndicatedataDAO.save(syndicateDataNext);
			LOGGER.debug("Next version({}) saved successfully.",
					syndicateDataNext.getVersionId());
		}

		syndicatedataDAO.save(syndicateDataDB);
		LOGGER.debug("Syndicate Data ({}) saved successfully.",
				syndicateDataDB.getContainerName());
		return syndicateDataDB;
	}

	/**
	 * 
	 * Fetches Table data if key exist.
	 * 
	 * @param syndicateData
	 * @param fetchColumn
	 * @return
	 * @throws BusinessException
	 * @throws SystemException
	 */
	@Override
	public SyndicateDataContainerInfo getSyndicateDataKeysAndColumnInfo(
			SyndicateDataContainerInfo syndicateDataContainerInfo,
			SyndicateData syndicateData) throws BusinessException,
			SystemException {
		String tableName = syndicateData.getTableName();
		Map<String, List<String>> tableKeys = getTableKeys(tableName);
		List<SyndicateDataColumnInfo> columnInfos = getTableColumnInfo(tableName);
		syndicateDataContainerInfo.setKeyDefinitions(SyndicateDataUtil
				.getSyndicateDatakeyInfoList(columnInfos, tableKeys));
		syndicateDataContainerInfo.setMetaData(columnInfos);
		return syndicateDataContainerInfo;
	}

	/**
	 * This method responsible for updating SyndicateDataVersionInfo with key,
	 * meta data and Version.
	 * 
	 * @param containerInfo
	 * @param syndicateData
	 * @return
	 */
	@Override
	public void updateMetadataAndKeyInfo(
			SyndicateDataContainerInfo containerInfo,
			SyndicateData syndicateData) throws BusinessException,
			SystemException {
		String tablename = syndicateData.getTableName();
		containerInfo.setMetaData(this.getTableColumnInfo(tablename));
		List<SyndicateDataColumnInfo> columnInfos = versionDataDAO
				.getTableColumnInfo(tablename);
		Map<String, List<String>> keysMap = this.getTableKeys(tablename);
		containerInfo.setKeyDefinitions(SyndicateDataUtil
				.getSyndicateDatakeyInfoList(columnInfos, keysMap));
		// Add version data
		List<Map<String, Object>> dataMap = versionDataDAO.getData(tablename,
				syndicateData.getVersionId());
		containerInfo.setSyndicateVersionData(SyndicateDataUtil
				.getConvertedData(dataMap));
		if (CollectionUtils.isNotEmpty(containerInfo.getSyndicateVersionData())) {
			containerInfo.setTotalRows(Long.valueOf(containerInfo
					.getSyndicateVersionData().size()));
		}
	}

	@Override
	public void dropSyndicateDataContainer(String containerName) {
		versionDataDAO.dropTable(AdminUtil
				.generateSyndDataTableName(containerName));
		LOGGER.debug("{} container table dropped successfully.", containerName);
	}

	@Override
	public List<SyndicateData> findPreviousVersion(String providerName,
			Long versionId) {
		return syndicatedataDAO
				.findByContainerNameAndVersionIdLessThanOrderByVersionIdDesc(
						providerName, versionId);
	}

	@Override
	public SyndicateData findProviderMaxVersion(String containerName) {
		return ObjectUtils.defaultIfNull(
				syndicatedataDAO.findProviderMaxVersion(containerName),
				new SyndicateData());
	}

	@Override
	public SystemKey findByKey(String key) {
		return systemKeyDAO.findByKey(key);
	}

	@Override
	public String getSyndicateContainerDefinition(String providerName) throws BusinessException, SystemException {
		String tableName = AdminUtil.generateSyndDataTableName(providerName);
		List<SyndicateDataColumnInfo> dataColumnInfos = getTableColumnInfo(tableName);
		int arraySize = dataColumnInfos.size();
		String[] columnNames = new String[arraySize];
		String[] columnDesc = new String[arraySize];
		String[] columnType = new String[arraySize];
		String[] columnSize = new String[arraySize];
		String[] isNullable = new String[arraySize];
		for (int i = 0; i < arraySize; i++) {
			columnNames[i] = dataColumnInfos.get(i).getDisplayName();
            columnNames[i] = SyndicateUtil.formatSyndicateColumnName(columnNames[i]);
			columnDesc[i] = BusinessConstants.CHAR_DOUBLE_QUOTE + dataColumnInfos.get(i).getDescription() + BusinessConstants.CHAR_DOUBLE_QUOTE;
			columnType[i] = dataColumnInfos.get(i).getColumnType();
			columnSize[i] = String.valueOf(dataColumnInfos.get(i).getColumnSize());
			if (columnType[i].equalsIgnoreCase(DataTypes.DOUBLE.toString())) {
				columnSize[i] = dataColumnInfos.get(i).getColumnSize() + BusinessConstants.CHAR_PIPE + dataColumnInfos.get(i).getPrecision();
			} else if (columnType[i].equalsIgnoreCase(DataTypes.DATE.toString()) || columnType[i].equalsIgnoreCase(DataTypes.BOOLEAN.toString())
					|| columnType[i].equalsIgnoreCase(DataTypes.INTEGER.toString())) {
				columnSize[i] = BusinessConstants.EMPTY_STRING;
			}
			isNullable[i] = dataColumnInfos.get(i).isMandatory() ? BusinessConstants.NO : BusinessConstants.YES;
		}
		Map<String, List<String>> keysList = versionDataDAO.getTableKeys(tableName);
		StringBuffer stringBuff = new StringBuffer();
		buildTableInfo(columnNames, columnDesc, columnType, columnSize, isNullable, stringBuff);
		buildKeyInfo(keysList, stringBuff);
		return stringBuff.toString();
	}

	private void buildKeyInfo(Map<String, List<String>> keysList,
			StringBuffer stringBuff) {
		for (String key : keysList.keySet()) {
			String keysCsv = StringUtils.join(keysList.get(key).toArray(),
					BusinessConstants.CHAR_COMMA);
			stringBuff.append(key).append(BusinessConstants.CHAR_COMMA)
					.append(keysCsv).append(BusinessConstants.CHAR_NEWLINE);
		}
	}

    private void buildTableInfo(String[] columnNames, String[] columnDesc, String[] columnType, String[] columnSize,// NOPMD
            String[] isNullable, StringBuffer stringBuff) {// NOPMD
		createRows(stringBuff, columnNames);
		createRows(stringBuff, columnDesc);
		createRows(stringBuff, columnType);
		createRows(stringBuff, columnSize);
		createRows(stringBuff, isNullable);
	}

	public void createRows(StringBuffer stringBuff, String[] data) {
		stringBuff.append(StringUtils.join(data, BusinessConstants.CHAR_COMMA))
				.append(BusinessConstants.CHAR_NEWLINE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ca.umg.business.syndicatedata.bo.SyndicateDataBO#getSyndicateTableData
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public List<String> getSyndicateTableData(String providerName,
			Long versionId) throws SystemException, BusinessException {
		List<String> list = new ArrayList<String>();
		String tableName = AdminUtil.generateSyndDataTableName(providerName);
		List<SyndicateDataColumnInfo> dataColumnInfos = getTableColumnInfo(tableName);
		List<String> columnWitDatetype = new ArrayList<String>();
		if (!CollectionUtils.isEmpty(dataColumnInfos)) {
			String[] columnNames = new String[dataColumnInfos.size()];
			int colCount = dataColumnInfos.size();
			for (int i = 0; i < colCount; i++) {
				columnNames[i] = dataColumnInfos.get(i).getDisplayName();
                columnNames[i] = SyndicateUtil.formatSyndicateColumnName(columnNames[i]);
				if (StringUtils.equals(dataColumnInfos.get(i).getColumnType(),
						"DATE")) {
					columnWitDatetype.add(columnNames[i]);
				}
			}
			List<Map<String, Object>> syndDataList = versionDataDAO.getData(
					tableName, versionId);
			list.add(StringUtils
					.join(columnNames, BusinessConstants.CHAR_COMMA)
					+ BusinessConstants.CHAR_NEWLINE);
			for (Map<String, Object> syndDataMap : syndDataList) {
				int i = 0;
				Object[] syndDataArr = new Object[columnNames.length];
				for (String columnName : columnNames) {
					Object columnData = syndDataMap.get(columnName);
					if (columnData instanceof String
							&& ((String) columnData)
									.contains(BusinessConstants.CHAR_COMMA)) {
						syndDataArr[i] = BusinessConstants.CHAR_DOUBLE_QUOTE
								+ columnData
								+ BusinessConstants.CHAR_DOUBLE_QUOTE;
					} else {
						syndDataArr[i] = columnData;
					}
					checkForDate(columnWitDatetype, columnData, i, syndDataArr,
							columnName);
					i++;
				}
				list.add(StringUtils.join(syndDataArr,
						BusinessConstants.CHAR_COMMA)
						+ BusinessConstants.CHAR_NEWLINE);
			}

		}
		return list;
	}


	private void checkForDate(List<String> columnWitDatetype,
			Object columnData, int i, Object[] syndDataArr, String columnName)
			throws BusinessException {
		if (columnWitDatetype.contains(columnName)) {
			SimpleDateFormat originalFormat = new SimpleDateFormat(
					"yyyy-MM-dd", Locale.getDefault());
			SimpleDateFormat changedFormat = new SimpleDateFormat(
					"dd-MMM-yyyy", Locale.getDefault());
			try {
				syndDataArr[i] = changedFormat
						.format(originalFormat.parse(originalFormat
								.format(columnData))).toUpperCase();
			} catch (ParseException e) {
				BusinessException.raiseBusinessException(
						BusinessExceptionCodes.BSE000124, new Object[] {
								columnData, e.getMessage() });
			}
		}
	}
}