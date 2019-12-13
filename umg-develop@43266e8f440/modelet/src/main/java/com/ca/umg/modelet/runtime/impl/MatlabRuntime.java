package com.ca.umg.modelet.runtime.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.modelet.common.HeaderInfo;
import com.ca.umg.modelet.common.ModelKey;
import com.ca.umg.modelet.common.SystemInfo;
import com.ca.umg.modelet.exception.ModeletExceptionCodes;
import com.ca.umg.modelet.runtime.MatlabCustomClassLoader;
import com.ca.umg.modelet.runtime.ModeletRuntime;
import com.mathworks.toolbox.javabuilder.MWApplication;
import com.mathworks.toolbox.javabuilder.MWMCROption;

public class MatlabRuntime implements ModeletRuntime {

	private static final Logger LOGGER = LoggerFactory.getLogger(MatlabRuntime.class);

	private SystemInfo systemInfo;

	private final UmgFileProxy umgFileProxy;

	private boolean initialized;

	private final Map<ModelKey, ClassLoader> modelHolder = new HashMap<>();
	public MatlabRuntime(final SystemInfo systemInfo, final UmgFileProxy umgFileProxy) {
		this.systemInfo = systemInfo;
		this.umgFileProxy = umgFileProxy;
	}

	@Override
	public void initializeRuntime() throws SystemException {
		LOGGER.info("Initializing MATLAB runtime");
		try {

			if (!MWApplication.isMCRInitialized()) {
				MWApplication.initialize(MWMCROption.logFile(systemInfo.getLogPath()));
				initialized = true;
				LOGGER.info("Successfully initialized MATLAB runtime");
			}
			else {
				LOGGER.info("Failed to initialize MATLAB runtime");

                throw new SystemException(ModeletExceptionCodes.MOSE000100, new String[] { "Matlab", "Failed to initialize" });
			}
		}
		catch (UnsatisfiedLinkError e)	{
			LOGGER.info("Matlab runtime failed with exception " + e.getMessage());
            throw new SystemException(ModeletExceptionCodes.MOSE000100, new String[] { "Matlab", e.getMessage() }, e);
		}

	}

	@Override
	public void destroyRuntime() {
		LOGGER.info("Terminating MATLAB Runtime");
		MWApplication.terminate();
		initialized = false;
	}

	@Override
	public boolean isInitialized() {
		return initialized;
	}

	/*@Override
    public Object getModel(final String modelName) {
        LOGGER.info("Creating model instance for model {}.", modelName);
        return classLoader.getInstance(modelName);
    }*/

	@Override
	public Object getModel(final HeaderInfo headerInfo) throws SystemException {
		ModelKey modelKey = new ModelKey();
		modelKey.setFilePath(systemInfo.getSanPath());
		modelKey.setModelClass(headerInfo.getModelClass());
		modelKey.setModelLibrary(headerInfo.getModelLibraryName());
		modelKey.setModelMethod(headerInfo.getModelMethod());
		modelKey.setModelName(headerInfo.getModelName());
		modelKey.setModelPackageName(headerInfo.getModelPackageName());
		modelKey.setTenantCode(headerInfo.getTenantCode());
		modelKey.setUmgName(headerInfo.getModelLibraryVersionName());
		ClassLoader classLoader = modelHolder.get(modelKey);
		Object modelInstance = null;
		if(null == classLoader) {
			String sanpath = umgFileProxy.getSanPath(systemInfo.getSanPath());
			String filePath = StringUtils.join(sanpath, Character.toString(File.separatorChar), headerInfo.getTenantCode(),
					Character.toString(File.separatorChar), "modelLibrary", Character.toString(File.separatorChar),
					headerInfo.getModelLibraryName(), Character.toString(File.separatorChar),
					headerInfo.getModelLibraryVersionName(), Character.toString(File.separatorChar), headerInfo.getJarName());
			modelKey.setFilePath(filePath);
			classLoader = new MatlabCustomClassLoader(this.getClass().getClassLoader(), systemInfo);
			modelInstance = ((MatlabCustomClassLoader) classLoader).getInstance(modelKey);
			// reset the filepath because sanpath may change in between while loading the jar
			modelKey.setFilePath(systemInfo.getSanPath());
			modelHolder.put(modelKey, classLoader);
		} else {
			modelInstance = ((MatlabCustomClassLoader)modelHolder.get(modelKey)).getInstance(modelKey);
		}
		return modelInstance;
	}

	public SystemInfo getSystemInfo() {
		return systemInfo;
	}

	public void setSystemInfo(final SystemInfo systemInfo) {
		this.systemInfo = systemInfo;
	}

	public void setInitialized(final boolean initialized) {
		this.initialized = initialized;
	}

    @Override
    public void destroyConnection() {
        // TODO Auto-generated method stub

    }

}
