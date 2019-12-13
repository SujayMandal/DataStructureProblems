package com.ca.framework.core.rmodel.dao;

import java.util.List;
import java.util.Map;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.rmodel.info.SupportPackage;
import com.ca.framework.core.rmodel.info.VersionExecInfo;

public interface RModelDAO {

    /**
     * gets the support package list
     * 
     * @param modelName
     * @param majorVersion
     * @param minorVersion
     * @return
     */
    public List<SupportPackage> getSupportPackageList(String modelName, Integer majorVersion, Integer minorVersion,
            final String tenantCode);

    /**
     * gets the model package name
     * 
     * @param modelName
     * @param majorVersion
     * @param minorVersion
     * @return
     */
    public Map<String, String> getModelPackageName(final String modelName, final Integer majorVersion, final Integer minorVersion,
            final String tenantCode);

    /**
     * Returns all the available support packages in the system for the given tenant.
     * 
     * @param tenantCode
     * @return
     * @throws SystemException
     */
    public List<SupportPackage> getAllSupportPackages(final String tenantCode) throws SystemException;

    /**
     * 
     * @param tenantCode
     * @return
     * @throws SystemException
     */
    public Map<String, String> getAllModelPackageNames(final String tenantCode) throws SystemException;

    public  Map<String, Map<String, VersionExecInfo>>  getAllVersionEnvironmentMap(
            String tenantCode) throws SystemException;
    
    public VersionExecInfo getEnvironmentDetails(String tenantCode,
            String versionName,String majorVer,String minorVer) throws SystemException;
    
    public  byte[] getMappingOutput(final String tenantCode,final String versionName,final int majorVersion,final int minorVersion);

}