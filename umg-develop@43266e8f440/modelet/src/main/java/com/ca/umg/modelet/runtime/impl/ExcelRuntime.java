/**
 * 
 */
package com.ca.umg.modelet.runtime.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.common.ExcelModel;
import com.ca.umg.modelet.common.HeaderInfo;
import com.ca.umg.modelet.common.ModelKey;
import com.ca.umg.modelet.common.SystemInfo;
import com.ca.umg.modelet.runtime.ModeletRuntime;

/**
 * @author kamathan
 *
 */
public class ExcelRuntime implements ModeletRuntime {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelRuntime.class);

    private final SystemInfo systemInfo;

    public ExcelRuntime(final SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.modelet.runtime.ModeletRuntime#isInitialized()
     */
    @Override
    public boolean isInitialized() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.modelet.runtime.ModeletRuntime#initializeRuntime()
     */
    @Override
    public void initializeRuntime() throws SystemException {
        LOGGER.debug("Excel runtime is initialized by default.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.modelet.runtime.ModeletRuntime#destroyRuntime()
     */
    @Override
    public void destroyRuntime() {
        LOGGER.debug("Destroy runtime is not required for excel model.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.modelet.runtime.ModeletRuntime#getModel(com.ca.umg.modelet.common.HeaderInfo)
     */
    @Override
    public ExcelModel getModel(HeaderInfo headerInfo) throws SystemException {
        ModelKey modelKey = new ModelKey();
        modelKey.setFilePath(systemInfo.getSanPath());
        modelKey.setLocalFilePath(systemInfo.getLocalSanPath());
        modelKey.setModelClass(headerInfo.getModelClass());
        modelKey.setModelLibrary(headerInfo.getModelLibraryName());
        modelKey.setModelMethod(headerInfo.getModelMethod());
        modelKey.setModelName(headerInfo.getModelName());
        modelKey.setModelPackageName(headerInfo.getModelPackageName());
        modelKey.setTenantCode(headerInfo.getTenantCode());
        modelKey.setUmgName(headerInfo.getModelLibraryVersionName());
        modelKey.setJarName(headerInfo.getJarName());
        return new ExcelModel(modelKey);
    }

    @Override
    public void destroyConnection() {
        LOGGER.debug("Destroy runtime is not required for excel model.");

    }

}
