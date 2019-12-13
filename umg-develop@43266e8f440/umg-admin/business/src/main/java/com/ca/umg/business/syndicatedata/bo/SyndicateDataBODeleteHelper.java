package com.ca.umg.business.syndicatedata.bo;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.syndicatedata.dao.SyndicateDataDAO;
import com.ca.umg.business.syndicatedata.dao.SyndicateVersionDataDAO;
import com.ca.umg.business.syndicatedata.entity.SyndicateData;

@Service
public class SyndicateDataBODeleteHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyndicateDataBODeleteHelper.class);

    @Inject
    private SyndicateVersionDataDAO versionDataDAO;

    @Inject
    private SyndicateDataDAO syndicatedataDAO;
    
    @Inject
    private SystemParameterProvider systemParameterProvider;

    public void delete(String containerName, Long versionId) throws BusinessException, SystemException {
        try {
            if (isEmpty(containerName) || versionId == null) {
                LOGGER.error("container name({}) or version id({}) is empty or null", containerName, versionId);
                throw new BusinessException(BusinessExceptionCodes.BSE000014, new String[] {});
            }
            List<SyndicateData> allVersions = getAllVersions(containerName);
            SyndicateData versionToBeDeleted = getVersionToBeDeleted(allVersions, versionId);
            if (versionToBeDeleted != null) {
                this.deleteVersion(versionToBeDeleted, allVersions, versionId);
            } else {
                LOGGER.error("provided container name({}) or version id({}) not available in database.", containerName,
                        String.valueOf(versionId));
                throw new BusinessException(BusinessExceptionCodes.BSE000036, new String[] { containerName,
                        String.valueOf(versionId) });
            }
        } catch (DataAccessException e) {
            LOGGER.error("Exception({}) in deleting container version", e);
            throw new SystemException(BusinessExceptionCodes.BSE000023, new String[] { "" }, e);
        }
    }

    /**
     * 
     * @param versionToBeDeleted
     * @param allVersions
     * @param versionId
     * @throws BusinessException
     * @throws SystemException
     */
    private void deleteVersion(SyndicateData versionToBeDeleted, List<SyndicateData> allVersions, Long versionId)
            throws BusinessException, SystemException {
        Long validFrom = versionToBeDeleted.getValidFrom();
        if (validFrom != null && isActiveFromDateLessThanCurrentDateTime(validFrom)) {
            LOGGER.error("'Active From'({}) date time should be greater than current date time.", new Date(validFrom).toString());
            throw new BusinessException(BusinessExceptionCodes.BSE000021, new String[] { "" });
        }

        SyndicateData nextVersion = getNextVersionIfVersionToBeDeletedIsNotFirstVersion(allVersions, versionToBeDeleted);
        if (nextVersion != null) {
            fillGapToVersionBeforeDeletedVersion(versionToBeDeleted, nextVersion);
        }

        String syndicateDataTableName = getSyndicateDataTableName(versionToBeDeleted);
        StringBuffer syndDataTableWithSchema = null;
        if (systemParameterProvider.getParameter(SystemConstants.UMG_ADMIN_SCHEMA) != null) {
        	syndDataTableWithSchema = new StringBuffer(systemParameterProvider.getParameter(SystemConstants.UMG_ADMIN_SCHEMA));
        	syndDataTableWithSchema.append(BusinessConstants.DOT).append(syndicateDataTableName);
        } else {
        	syndDataTableWithSchema = new StringBuffer(syndicateDataTableName);
        }
        deleteData(syndDataTableWithSchema.toString(), versionId);
        if (isNoOtherVersionExists(allVersions)) {
            dropDataTable(syndDataTableWithSchema.toString());
            LOGGER.debug("{} table dropped successfully", syndicateDataTableName);
        }

        deleteContainer(versionToBeDeleted);
        LOGGER.debug("{} version dropped successfully", versionToBeDeleted);
    }

    private void dropDataTable(String tableName) {
        versionDataDAO.dropTable(tableName);
    }

    private boolean isNoOtherVersionExists(List<SyndicateData> allVersions) {
        return allVersions.size() == 1;
    }

    private SyndicateData getNextVersionIfVersionToBeDeletedIsNotFirstVersion(List<SyndicateData> allVersions,
            SyndicateData versionToBeDeleted) {
        SyndicateData syndicateData = null;
        int indexOfVersionToBeDeleted = allVersions.indexOf(versionToBeDeleted);
        if (isNotFirstOrLast(allVersions, indexOfVersionToBeDeleted)) {
            syndicateData = getNextElement(allVersions, indexOfVersionToBeDeleted);
        }
        return syndicateData;
    }

    private SyndicateData getNextElement(List<SyndicateData> allVersions, int indexOfVersionToBeDeleted) {
        return allVersions.get(indexOfVersionToBeDeleted + 1);
    }

    private boolean isNotFirstOrLast(List<SyndicateData> allVersions, int indexOfVersionToBeDeleted) {
        return indexOfVersionToBeDeleted != 0 && indexOfVersionToBeDeleted != (allVersions.size() - 1);
    }

    private String getSyndicateDataTableName(SyndicateData versionToBeDeleted) {
        return versionToBeDeleted.getTableName();
    }

    private SyndicateData getVersionToBeDeleted(List<SyndicateData> allVersions, Long versionId) {
        SyndicateData syndicateDataTemp = null;
        for (SyndicateData syndicateData : allVersions) {
            if (versionId.equals(syndicateData.getVersionId())) {
                syndicateDataTemp = syndicateData;
                break;
            }
        }
        return syndicateDataTemp;
    }

    private void fillGapToVersionBeforeDeletedVersion(SyndicateData versionToBeDeleted, SyndicateData nextVersion) {
        nextVersion.setValidTo(versionToBeDeleted.getValidTo());
        syndicatedataDAO.save(nextVersion);
    }

    private void deleteData(String tableName, Long versionId) {
        versionDataDAO.deleteVersionData(tableName, versionId);
    }

    private void deleteContainer(SyndicateData syndicateData) {
        syndicatedataDAO.delete(syndicateData);
    }

    private List<SyndicateData> getAllVersions(String containerName) {
        return syndicatedataDAO.findByContainerNameOrderByVersionIdDesc(containerName);
    }

    protected boolean isActiveFromDateLessThanCurrentDateTime(Long activeFrom) {
        return activeFrom.compareTo(new DateTime().getMillis()) < 0;
    }
}
