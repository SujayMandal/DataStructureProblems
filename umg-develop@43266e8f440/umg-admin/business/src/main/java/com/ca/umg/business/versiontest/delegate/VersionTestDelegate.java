package com.ca.umg.business.versiontest.delegate;

import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mapping.info.VersionTestContainer;
import com.ca.umg.business.version.info.VersionAPIContainer;
import com.ca.umg.business.version.info.VersionAPIInfo;

/**
 * @author basanaga
 * 
 */
public interface VersionTestDelegate {

    /**
     * This method is used to get Version Container Info data to display in test bed based on Transaction ID
     * 
     * @param txnId
     * @return List of TidIoDefinitions
     * @throws SystemException
     * @throws BusinessException
     */
    VersionTestContainer getVersionTestContainer(String txnId) throws SystemException, BusinessException;

    /**
     * This method is used to get Version Container Info data to display in test bed based on tenant Input data
     * 
     * @param tenantInput
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    VersionTestContainer getVersionTestContainerFromFile(byte[] tenantInput) throws SystemException, BusinessException;

    /**
     * This method is used to get the API details for the given version.
     * 
     * @param txnId
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    VersionAPIContainer getVersionAPI(String versionId) throws SystemException, BusinessException;

    /**
     * Returns the version API details of the given tenant model name, major version and minor version.
     * 
     * @param name
     * @param majorVersion
     * @param minorVersion
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    VersionAPIInfo getVersionDetails(String name, Integer majorVersion, Integer minorVersion) throws BusinessException,
            SystemException;

    /**
     * Returns the list of version API details for the given tenant model name
     * 
     * @param name
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    List<VersionAPIInfo> getVersionDetails(String name) throws BusinessException, SystemException;

    /**
     * Returns all the list of version API details
     * 
     * @return
     * @throws BusinessException
     * @throws SystemException
     */
    List<VersionAPIInfo> getAllVersionDetails() throws BusinessException, SystemException;

    String createZip(List<Map<String, Object>> jsonList, boolean downloadSingleFile) throws BusinessException, SystemException;

    void getZipFile(ZipOutputStream zos, String fileName) throws BusinessException, SystemException;

    
    /**
     * This method would create sample test input data for a given version.
     * @param tidName
     * @param modelName
     * @param majorVersion
     * @param minorVersion
     * @param runAsDate
     * @param isTestForVerCreation -- this param is added for umg-4020 
     * 				to add clientTransactionId as Publishing-Test in case of sample test transaction during version creation  
     * @return
     * @throws SystemException
     * @throws BusinessException
     */
    byte[] getSampleTenantInput(String tidName, String modelName, int majorVersion, int minorVersion, 
    		String runAsDate, Boolean isTestForVerCreation, Boolean isOpValidation,Boolean isAcceptableValuesValidation,Boolean saveRLogs)
            throws SystemException, BusinessException;
}
