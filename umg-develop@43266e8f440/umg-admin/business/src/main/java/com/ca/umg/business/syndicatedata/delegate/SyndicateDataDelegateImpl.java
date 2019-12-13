/*
 * SyndicateDataDelegateImpl.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.syndicatedata.delegate;

import static com.ca.umg.business.util.SyndicateDataUtil.reportError;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.common.info.PageRecord;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.syndicatedata.bo.SyndicateDataBO;
import com.ca.umg.business.syndicatedata.entity.SyndicateData;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataContainerInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataVersionInfo;
import com.ca.umg.business.syndicatedata.util.SyndicateUtil;
import com.ca.umg.business.tenant.entity.SystemKey;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.validation.AddSyndicateDataVersionValidator;
import com.ca.umg.business.validation.CreateSyndicateDataValidator;
import com.ca.umg.business.validation.UpdateSyndicateDataValidator;
import com.ca.umg.business.validation.UpdateVersionActiveDuration;

/**
 * Syndicate Data Delegate Implementation.
 * 
 * @author mandavak
 **/
@Component
public class SyndicateDataDelegateImpl extends AbstractDelegate implements SyndicateDataDelegate {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyndicateDataDelegateImpl.class);
    @Inject
    private SyndicateDataBO syndicatedataBO;

    // TODO : this has to be autowired
    private final CreateSyndicateDataValidator createContainerValidator = new CreateSyndicateDataValidator();

    private final UpdateSyndicateDataValidator updateContainerValidator = new UpdateSyndicateDataValidator();

    private final AddSyndicateDataVersionValidator createVersionValidator = new AddSyndicateDataVersionValidator();

    private final UpdateVersionActiveDuration updateVersionValidator = new UpdateVersionActiveDuration();

    private static final String CREATE = "create";
    private static final String UPDATE = "update";

    /**
     * It fetches all Versions of a given Container details from SYNDICATE_DATA table.
     * 
     * @param containerName
     * 
     * @return
     * 
     * @throws BusinessException
     * @throws SystemException
     **/
    @Override
    public SyndicateDataVersionInfo listVersions(String containerName) throws BusinessException, SystemException {
        List<SyndicateData> syndicateDataList = syndicatedataBO.findSyndicateContainerVersions(containerName);
        if (CollectionUtils.isEmpty(syndicateDataList)) {
            LOGGER.debug("No syndicate data available for the given container {}", containerName);
            throw new BusinessException(BusinessExceptionCodes.BSE000013, new String[] { containerName });
        }

        List<SyndicateDataInfo> syndDataInfoList = new ArrayList<>();
        List<SyndicateDataInfo> syndicateDataInfoList = convertToList(syndicateDataList, SyndicateDataInfo.class);
        if (CollectionUtils.isNotEmpty(syndicateDataList)) {
            for (SyndicateDataInfo syndicateDataInfo : syndicateDataInfoList) {
                syndicateDataInfo.setCreatedDateTime(AdminUtil.getDateFormatMillisForEst(syndicateDataInfo.getCreatedDate()
                        .getMillis(), null));
                syndicateDataInfo.setLastModifiedDateTime(AdminUtil.getDateFormatMillisForEst(syndicateDataInfo
                        .getLastModifiedDate().getMillis(), null));
                if (syndicateDataInfo.getValidTo() != null) {
                    syndicateDataInfo.setValidToString(AdminUtil.getDateFormatMillisForEst(syndicateDataInfo.getValidTo(), null));
                }
                syndicateDataInfo.setValidFromString(AdminUtil.getDateFormatMillisForEst(syndicateDataInfo.getValidFrom(), null));
                syndDataInfoList.add(syndicateDataInfo);
            }
        }

        SyndicateDataVersionInfo syndicateDataVersionInfo = new SyndicateDataVersionInfo();
        syndicateDataVersionInfo.setVersions(syndDataInfoList);

        return syndicateDataVersionInfo;
    }

    /**
     * Fetches Syndicate Data from SYNDICATE_DATA table, for a given VersionId and ContainerName.
     * 
     * @param versionId
     * @param containerName
     * 
     * @return
     * 
     * @throws BusinessException
     * @throws SystemException
     **/
    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getLookupManageEdit())")
    public SyndicateDataContainerInfo getContainerVersionInformation(Long versionId, String containerName)
            throws BusinessException, SystemException {
        SyndicateData syndicateData = syndicatedataBO.findSyndicateDataByVersionId(versionId, containerName);

        if (syndicateData == null) {
            LOGGER.debug("Version {} is not present for container {}", versionId, containerName);
            throw new BusinessException(BusinessExceptionCodes.BSE000015,
                    new String[] { String.valueOf(versionId), containerName });
        }
        SyndicateDataContainerInfo containerInfo = convert(syndicateData, SyndicateDataContainerInfo.class);

        syndicatedataBO.updateMetadataAndKeyInfo(containerInfo, syndicateData);
        containerInfo.setValidFromString(AdminUtil.getDateFormatMillisForEst(containerInfo.getValidFrom(), null));
        if (containerInfo.getValidTo() != null) {
            containerInfo.setValidToString(AdminUtil.getDateFormatMillisForEst(containerInfo.getValidTo(), null));
        }

        return containerInfo;
    }

    /**
     * Fetches Syndicate Data from SYNDICATE_DATA table, for a given container name;
     * 
     * @param containerName
     * 
     * @return
     * 
     * @throws BusinessException
     * @throws SystemException
     **/
    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getLookupManageAdd())")
    public SyndicateDataContainerInfo getContainerInformation(String containerName) throws BusinessException, SystemException {
        SyndicateData syndicateData = syndicatedataBO.findSyndicateDataContainer(containerName);

        SyndicateDataContainerInfo syndicateDataContainerInfo = convert(syndicateData, SyndicateDataContainerInfo.class);
        if (syndicateData.getValidTo() != null) {
            syndicateDataContainerInfo.setValidToString(AdminUtil.getDateFormatMillisForEst(syndicateData.getValidTo(), null));
        }
        syndicateDataContainerInfo.setValidFromString(AdminUtil.getDateFormatMillisForEst(syndicateData.getValidFrom(), null));

        return syndicatedataBO.getSyndicateDataKeysAndColumnInfo(syndicateDataContainerInfo, syndicateData);
    }

    /**
     * Fetches Syndicate Data from SYNDICATE_DATA table for all containers.
     * 
     * @return
     * 
     * @throws BusinessException
     * @throws SystemException
     **/
    @Override
    public List<SyndicateDataContainerInfo> getContainerInformation() throws BusinessException, SystemException {
        List<SyndicateData> syndicateDataList = syndicatedataBO.getContainers();
        LOGGER.debug("Found {} containers in the system.",
                CollectionUtils.isEmpty(syndicateDataList) ? 0 : syndicateDataList.size());
        if (CollectionUtils.isEmpty(syndicateDataList)) {
            LOGGER.debug("No syndicate data available for the given container.");
            throw new BusinessException(BusinessExceptionCodes.BSE000013,
                    new String[] { "No syndicate data available for the given container." });
        }
        return getContainerInfoList(syndicateDataList);
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getLookupManage())")
    public PageRecord<SyndicateDataContainerInfo> getContainerInformation(SearchOptions pageInfo) throws BusinessException,
            SystemException {
        Page<SyndicateData> page = syndicatedataBO.getContainers(pageInfo);
        List<SyndicateData> syndicateDataList = page.getContent();
        LOGGER.debug("Found {} containers for this page request.", CollectionUtils.isEmpty(syndicateDataList) ? 0
                : syndicateDataList.size());
        if (CollectionUtils.isEmpty(syndicateDataList)) {
            LOGGER.debug("No syndicate data available for this page request.");
            throw new BusinessException(BusinessExceptionCodes.BSE000013,
                    new String[] { "No syndicate data available for this page request." });
        }
        List<SyndicateDataContainerInfo> dataContainerInfosList = getContainerInfoList(syndicateDataList);
        return preparePageRecord(page, dataContainerInfosList);
    }

    private List<SyndicateDataContainerInfo> getContainerInfoList(List<SyndicateData> syndicateDataList) throws BusinessException {
        List<SyndicateDataContainerInfo> containerInfosList = convertToList(syndicateDataList, SyndicateDataContainerInfo.class);
        List<SyndicateDataContainerInfo> dataContainerInfosList = null;
        if (CollectionUtils.isNotEmpty(syndicateDataList)) {
            dataContainerInfosList = new ArrayList<>();
            for (SyndicateDataContainerInfo containerInfo : containerInfosList) {
                containerInfo.setCreatedDateTime(AdminUtil.getDateFormatMillisForEst(containerInfo.getCreatedDate().getMillis(),
                        null));
                containerInfo.setLastModifiedDateTime(AdminUtil.getDateFormatMillisForEst(containerInfo.getLastModifiedDate()
                        .getMillis(), null));
                if (containerInfo.getValidTo() != null) {
                    containerInfo.setValidToString(AdminUtil.getDateFormatMillisForEst(containerInfo.getValidTo(), null));
                }
                containerInfo.setValidFromString(AdminUtil.getDateFormatMillisForEst(containerInfo.getValidFrom(), null));
                dataContainerInfosList.add(containerInfo);
            }
        }
        return dataContainerInfosList;
    }

    private <T, E> PageRecord<T> preparePageRecord(Page<E> page, List<T> infos) {
        PageRecord<T> pageRecord = new PageRecord<>();
        pageRecord.setContent(infos);
        pageRecord.setNumber(page.getNumber());
        pageRecord.setNumberOfElements(page.getNumberOfElements());
        pageRecord.setSize(page.getSize());
        pageRecord.setTotalElements(page.getTotalElements());
        pageRecord.setTotalPages(page.getTotalPages());
        return pageRecord;
    }

    /**
     * Delete particular version data for the syndicate data container.
     * 
     * @param versionId
     * @param containerName
     * 
     * @throws BusinessException
     **/
    @Override
    @Transactional(rollbackFor = { Exception.class })
    @PreAuthorize("hasRole(@accessPrivilege.getLookupManageDelete())")
    public void deleteContainerVersion(Long versionId, String containerName) throws BusinessException, SystemException {
        syndicatedataBO.delete(versionId, containerName);
    }

    /**
     * This method creates a container version. Steps involved are: 1. Creation of new version for a container. 2. If first
     * version is created the a new dynamic table (<container name>_TABLE) to hold the container data is created. 3. Indexes are
     * created on the dynamic table. 4. Version data is inserted into the dynamic table.
     * 
     * @param sContainerInfo
     * @throws BusinessException
     * @throws SystemException
     **/
    @Override
    @Transactional(rollbackFor = { Exception.class })
    @PreAuthorize("hasRole(@accessPrivilege.getLookupAdd())")
    public void createProvider(SyndicateDataContainerInfo sContainerInfo) throws BusinessException, SystemException {
        List<SyndicateData> allSyndicateDatas = syndicatedataBO.getContainers();
        sContainerInfo.setValidFrom(AdminUtil.getMillisFromEstToUtc(sContainerInfo.getValidFromString(), null));
        sContainerInfo.setValidTo(AdminUtil.getMillisFromEstToUtc(sContainerInfo.getValidToString(), null));
        SystemKey systemKey = syndicatedataBO.findByKey(BusinessConstants.COLUMN_IDENTIFIERS);

        // UMG-4459 start
        formatMetadata(sContainerInfo.getMetaData());
        // UMG-4459 end
        reportError(createContainerValidator.validateForCreate(sContainerInfo, allSyndicateDatas, systemKey), CREATE);

        SyndicateData syndicateData = convert(sContainerInfo, SyndicateData.class);
        syndicatedataBO.createSyndicateDataVersion(syndicateData);
        boolean isTableCreated = Boolean.FALSE;
        try {
            syndicatedataBO.createSyndicateDataTable(syndicateData.getTableName(), sContainerInfo.getMetaData());
            isTableCreated = Boolean.TRUE;
            syndicatedataBO.createSyndicateDataKeyDefs(syndicateData.getTableName(), sContainerInfo.getKeyDefinitions());
            syndicatedataBO.insertSyndicateData(sContainerInfo, syndicateData);
        } catch (DataAccessException | SystemException dae) {
            if (isTableCreated) {
                syndicatedataBO.dropSyndicateDataContainer(sContainerInfo.getContainerName());
            }
            LOGGER.error("Error occurred during creation of syndicate data container : " + dae.getLocalizedMessage());
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000028,
                    new String[] { "INSERT", syndicateData.getTableName() });
        }
    }

    // UMG-4459 start
    private void formatMetadata(List<SyndicateDataColumnInfo> metaData) {
        if (CollectionUtils.isNotEmpty(metaData)) {
            for (SyndicateDataColumnInfo syndicateDataColumnInfo : metaData) {
                String field = syndicateDataColumnInfo.getField();
                String displayname = syndicateDataColumnInfo.getDisplayName();
                syndicateDataColumnInfo.setField(SyndicateUtil.formatSyndicateColumnName(field));
                syndicateDataColumnInfo.setDisplayName(SyndicateUtil.formatSyndicateColumnName(displayname));
            }
        }
    }

    // UMG-4459 end
    /**
     * @author mandavak
     * 
     *         Update container information.
     * 
     * @param sContainerInfo
     * @throws BusinessException
     * @throws SystemException
     */
    @Override
    public void updateProvider(SyndicateDataContainerInfo sContainerInfo) throws BusinessException, SystemException {
        SystemKey systemKey = syndicatedataBO.findByKey(BusinessConstants.COLUMN_IDENTIFIERS);
        // UMG-4459 start
        formatMetadata(sContainerInfo.getMetaData());
        // UMG-4459 end
        reportError(updateContainerValidator.validateForUpdate(sContainerInfo, systemKey), UPDATE);
        syndicatedataBO.updateContainerInfor(sContainerInfo);
    }

    /**
     * Create a new version for the Syndicate Data provider.
     * 
     * @param sContainerInfo
     * @throws BusinessException
     * @throws SystemException
     */
    @Override
    public void createProviderVersion(SyndicateDataContainerInfo sContainerInfo) throws BusinessException, SystemException {
        sContainerInfo.setId(null);
        sContainerInfo.setVersionId(null);
        List<SyndicateData> previousVersions = syndicatedataBO.findSyndicateContainerVersions(sContainerInfo.getContainerName());
        sContainerInfo.setValidFrom(AdminUtil.getMillisFromEstToUtc(sContainerInfo.getValidFromString(), null));
        sContainerInfo.setValidTo(AdminUtil.getMillisFromEstToUtc(sContainerInfo.getValidToString(), null));
        // UMG-4459 start
        formatMetadata(sContainerInfo.getMetaData());
        // UMG-4459 end
        reportError(createVersionValidator.validateAddNewVersion(sContainerInfo, previousVersions), CREATE);
        SyndicateData syndicateData = convert(sContainerInfo, SyndicateData.class);

        // Have to check if no changes and then delete. Seems to be an unnecessary performance
        syndicatedataBO.createSyndicateDataVersion(syndicateData);
        syndicatedataBO.deleteSyndicateDataKeyDefs(sContainerInfo.getContainerName());
        syndicatedataBO.createSyndicateDataKeyDefs(syndicateData.getTableName(), sContainerInfo.getKeyDefinitions());
        try {
            syndicatedataBO.insertSyndicateData(sContainerInfo, syndicateData);
        } catch (DataAccessException dae) {
            BusinessException.raiseBusinessException(BusinessExceptionCodes.BSE000028,
                    new String[] { "INSERT", syndicateData.getTableName() });
        }

    }

    /**
     * Updates the Syndicate Data provider. Only valid from and valid to are the fields eligible for update
     * 
     * @param sContainerInfo
     * @throws BusinessException
     * @throws SystemException
     */
    @Override
    public void updateProviderVersion(SyndicateDataContainerInfo sContainerInfo) throws BusinessException, SystemException {
        sContainerInfo.setValidFrom(AdminUtil.getMillisFromEstToUtc(sContainerInfo.getValidFromString(), null));
        sContainerInfo.setValidTo(AdminUtil.getMillisFromEstToUtc(sContainerInfo.getValidToString(), null));
        sContainerInfo.setOldValidFrom(AdminUtil.getMillisFromEstToUtc(sContainerInfo.getOldValidFromStr(), null));
        sContainerInfo.setOldValidTo(AdminUtil.getMillisFromEstToUtc(sContainerInfo.getOldValidToStr(), null));
        List<SyndicateData> previousVersions = getPreviousVersions(sContainerInfo);
        reportError(updateVersionValidator.validateForUpdate(sContainerInfo, previousVersions), UPDATE);
        SyndicateData syndicateData = convert(sContainerInfo, SyndicateData.class);
        // Have to check if no changes and then delete. Seems to be an unnecessary performance
        syndicatedataBO.deleteSyndicateDataKeyDefs(sContainerInfo.getContainerName());
        syndicatedataBO.createSyndicateDataKeyDefs(AdminUtil.generateSyndDataTableName(syndicateData.getContainerName()),
                sContainerInfo.getKeyDefinitions());
        syndicatedataBO.updateSyndicateDataVersion(syndicateData);
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getLookupManageDataDownload())")
    public List<String> downloadSyndTableData(String providerName, Long versionId) throws BusinessException, SystemException {
        return syndicatedataBO.getSyndicateTableData(providerName, versionId);
    }

    private List<SyndicateData> getPreviousVersions(SyndicateDataContainerInfo sContainerInfo) throws BusinessException,
            SystemException {
        return ObjectUtils.defaultIfNull(
                syndicatedataBO.findPreviousVersion(
                        sContainerInfo.getContainerName(),
                        ObjectUtils.defaultIfNull(sContainerInfo.getVersionId(),
                                findMaxSyndDataVersion(sContainerInfo.getContainerName()))), new ArrayList<SyndicateData>());
    }

    private Long findMaxSyndDataVersion(String providerName) throws SystemException, BusinessException {
        SyndicateData syndicateData = syndicatedataBO.findProviderMaxVersion(providerName);
        return ObjectUtils.defaultIfNull(syndicateData.getVersionId(), 0l);
    }

    @Override
    @PreAuthorize("hasRole(@accessPrivilege.getLookupManageDefinitionDownload())")
    public String downloadSyndContainerDefinition(String providerName) throws BusinessException, SystemException {
        return syndicatedataBO.getSyndicateContainerDefinition(providerName);
    }

}