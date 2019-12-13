
/*
 * RRuntime.java
 * Author: Manasi Seshadri (manasi.seshadri@altisource.com)
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.modelet.runtime.impl;

import static java.io.File.separatorChar;
import static java.lang.System.currentTimeMillis;
import static org.rosuda.JRI.Rengine.versionCheck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.publishing.status.constants.PublishingStatus;
import com.ca.framework.core.util.CheckSumUtil;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.pool.model.TransactionCriteria;
import com.ca.umg.modelet.common.HeaderInfo;
import com.ca.umg.modelet.common.LibraryInfo;
import com.ca.umg.modelet.common.ModelKey;
import com.ca.umg.modelet.common.REngineDelegator;
import com.ca.umg.modelet.common.RModel;
import com.ca.umg.modelet.common.SystemInfo;
import com.ca.umg.modelet.constants.ErrorCodes;
import com.ca.umg.modelet.exception.ModeletExceptionCodes;
import com.ca.umg.modelet.runtime.ModeletRuntime;
import com.ca.umg.modelet.util.Modeletutil;

/**
 * ModeletRuntime implementation for R
 **/

@SuppressWarnings("PMD")
public class RRuntime implements ModeletRuntime {
    private static final Logger LOGGER = LoggerFactory.getLogger(RRuntime.class);

	private static final String checkSumGenerationAlgo = "SHA256";
    private boolean initialized;
    private Rengine rEngine;

    private SystemInfo systemInfo;

    private UmgFileProxy umgFileProxy;
    
    private CacheRegistry cacheRegistry;

    private Map<String, ModelKey> modelKeyMap = new HashMap<String, ModelKey>();
    
    private static final String TID = "tID";

    private final RMainLoopCallbacks rTextConsole = new RTextConsole();
    public static final String PATH_SEP = String.valueOf(separatorChar);
    public static final String INSTALL_FOLDER_NAME = "_install";
    public static final String SUPPORT_PACKAGE_FOLDER = "supportpackage";

    public RRuntime(final SystemInfo systemInfo, final UmgFileProxy umgFileProxy, CacheRegistry cacheRegistry) {
        this.systemInfo = systemInfo;
        this.umgFileProxy = umgFileProxy;
        this.cacheRegistry = cacheRegistry;
    }

    @Override
    public void initializeRuntime() throws SystemException {
        LOGGER.info("Initializing R runtime");
        try {
            versionMismatchCheck();
            initializeRengine();
        } catch (final UnsatisfiedLinkError e) {
            LOGGER.info("R Runtime Failed With Exception " + e.getMessage());
            throw new SystemException(ModeletExceptionCodes.MOSE000100,
                    new String[] { "R Runtime Initialization Failed", e.getMessage() }, e);
        }
    }

    private void versionMismatchCheck() throws SystemException {
        if (!versionCheck()) {
            LOGGER.info("R Version Mismatch - Java files don't match library version.");
            LOGGER.info("Failed to initialize R runtime");
			throw new SystemException(ModeletExceptionCodes.MOSE000100,
					new String[] { "R", "R Runtime Version Mismatch" });
        }
    }

    private void initializeRengine() throws SystemException {
        LOGGER.info("Creating R Rengine");
        final String[] roptions = new String[1];
        roptions[0] = "--vanilla";
        if (rEngine == null) {
            rEngine = new Rengine(roptions, false, rTextConsole);
            if (rEngine == null) {
                initialized = false;
				throw new SystemException(ModeletExceptionCodes.MOSE000100,
						new String[] { "R Runtime Initialization Failed" });
            }
        }
        initialized = true;
        LOGGER.info("Successfully Initialized R Runtime");
    }

    @Override
    public void destroyRuntime() {
        LOGGER.info("Terminating R Runtime");
        if (rEngine != null) {
            rEngine.end();
            rEngine.interrupt();
            rEngine = null;
        }
        initialized = false;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public Object getModel(final HeaderInfo headerInfo) throws SystemException {
		LOGGER.info("Creating R Execution Model");
        long start = System.currentTimeMillis();
        final ModelKey modelKey = createModelKey(headerInfo);
        LOGGER.info("Create model key: " + (System.currentTimeMillis() - start));
        final REngineDelegator rEngineInvoker = new REngineDelegator(rEngine, modelKey, rTextConsole);
		LOGGER.info("REngineDelegator is created");

        final MemoryFoot mfBeforeInstallAndLoadLibraries = new MemoryFoot();
        final long startInstallAndLoadLibraries = currentTimeMillis();
        final List<RPackageStats> packageStatsList = new ArrayList<>();
        try {
        installAndLoadLibraries(headerInfo, rEngineInvoker, packageStatsList, modelKey);
        final long endInstallAndLoadLibraries = currentTimeMillis();
        final MemoryFoot mfAfterInstallAndLoadLibraries = new MemoryFoot();

        final long startInstallAndLoadModel = currentTimeMillis();
        final MemoryFoot mfBeforeInstallAndLoadModel = new MemoryFoot();
		
		installAndLoadModel(headerInfo, rEngineInvoker, packageStatsList, modelKey);
		
        final long endinstallAndLoadModel = currentTimeMillis();
        final MemoryFoot mfAfterInstallAndLoadModel = new MemoryFoot();

		LOGGER.info("Memroy And Time Statistics for Innstalling and Loading Libraries");
		LOGGER.info("Start Time of Install and Load Libraries :" + startInstallAndLoadLibraries);
		LOGGER.info("End Time of Install and Load Libraries :" + endInstallAndLoadLibraries);
		LOGGER.info("Total time taken to Insatll and Load Libraries :"
				+ (endInstallAndLoadLibraries - startInstallAndLoadLibraries));
        
		mfBeforeInstallAndLoadLibraries
				.printMemoryFootPrint("Memory FooT Print Before Installing and Loading Libraries :", LOGGER);
		mfAfterInstallAndLoadLibraries
				.printMemoryFootPrint("Memory FooT Print After Installing and Loading Libraries :", LOGGER);

		LOGGER.info("Start Time of Install and Load {} Package :" + startInstallAndLoadModel,
                headerInfo.getModelPackageName());
		LOGGER.info("End Time of Install and Load {} Package :" + endinstallAndLoadModel,
				headerInfo.getModelPackageName());
		LOGGER.info("Total time taken to Insatll and Load {} Package :"
				+ (endinstallAndLoadModel - startInstallAndLoadModel), headerInfo.getModelPackageName());

		mfBeforeInstallAndLoadModel.printMemoryFootPrint("Memory FooT Print Before Installing and Loading Model :",
				LOGGER);
		mfAfterInstallAndLoadModel.printMemoryFootPrint("Memory FooT Print After Installing and Loading Model :",
				LOGGER);

		LOGGER.info("Package Name" + "," + "Size" + "," + "Install Time" + "," + "Load Time" + "," + "Add Lib Time"
				+ "," + "Total Time ");
        for (RPackageStats stats : packageStatsList) {
            LOGGER.info(stats.toString());
        }
        } catch (SystemException sys) {
            if(ErrorCodes.ME00045.equals(sys.getCode())) {
            	LOGGER.info("RRuntime :: method getModel :: start Loading the Model ");
        installAndLoadModel(headerInfo, rEngineInvoker, packageStatsList, modelKey);
    			LOGGER.info("RRuntime :: method getModel::End Loading the Model ");
            }else {
            	boolean rlogFlag = (headerInfo.isStoreRLogs()  || (headerInfo.getTransactionCriteria().getTransactionRequestType() != null && StringUtils.equalsIgnoreCase(headerInfo.getTransactionCriteria().getTransactionRequestType(), "test")));
            	//R logs will written only in following condition- installPackage,loadPackage,addToLibraryPath
            	if(rlogFlag && ("MOSE000002".equalsIgnoreCase(sys.getCode())||"MOSE000003".equalsIgnoreCase(sys.getCode()))){
        	        try {
						FileUtils.writeStringToFile(Modeletutil.createLogFileLocation(systemInfo, headerInfo,MDC.get(TID)),rEngineInvoker.getrText());
						MDC.remove(TID);
					} catch (IOException e) {
						 LOGGER.error("ISSUE IN WRITING RJAVA LOG TO FILE");
					} 
        	 }
            	throw sys;
            }
          }
        return new RModel(rEngineInvoker,systemInfo);
    }

    private void installAndLoadModel(final HeaderInfo headerInfo, final REngineDelegator rEngineInvoker,
            final List<RPackageStats> packageStatsList, final ModelKey modelKey) throws SystemException {
        // System.exit(0);
		if (!modelKey.getCommandsStatus().isLoadedModel() || !isLodedSameModel(headerInfo)) {
            final RPackageStats packageStats = new RPackageStats();
			try {
				String modelCheckSum = CheckSumUtil.getCheckSumValue(CheckSumUtil.readZipFile(modelKey.getFilePath()),
						checkSumGenerationAlgo);
				TransactionCriteria transactionCriteria = headerInfo.getTransactionCriteria();
				systemInfo.setModelIdentifier(modelCheckSum);
				systemInfo.setModelName(transactionCriteria.getModelName());
				systemInfo.setModelVersion(transactionCriteria.getModelVersion());
				if (StringUtils.isNotBlank(headerInfo.getModelIdentifier()) && !isLodedSameModel(headerInfo)) {
					LOGGER.info("Generated CheckSum %s and Header CheckSum %s are not Same", modelCheckSum,
							headerInfo.getModelIdentifier());
					throw new SystemException(ErrorCodes.ME00045,
							new Object[] { systemInfo.getModelName(), systemInfo.getModelVersion(),
									systemInfo.getModelIdentifier(), transactionCriteria.getModelName(),
									transactionCriteria.getModelVersion(), headerInfo.getModelIdentifier() });
				}
			} catch (IOException exe) {
				LOGGER.error(exe.getLocalizedMessage(), exe);
				throw new SystemException(ErrorCodes.ME00046, new Object[] { modelKey.getFilePath() });
			}
            packageStats.setPackageName(headerInfo.getModelPackageName());
            packageStats.setModel(true);
            packageStats.setJarName(headerInfo.getJarName());
            packageStats.setAddLibPathTime(0l);

            long startTime = currentTimeMillis();
            final String modelPath = getModelPath(headerInfo);
            LOGGER.info("Model Path :" + modelPath);
            final String installFolder = modelPath + INSTALL_FOLDER_NAME;
            rEngineInvoker.installPackage(modelPath, installFolder, headerInfo.getModelPackageName());
            long endTime = currentTimeMillis();

            packageStats.setInstallTime(endTime - startTime);

            startTime = endTime;

            rEngineInvoker.loadPackage(installFolder, headerInfo.getModelPackageName());
            modelKey.getCommandsStatus().setLoadedModel(true);
            endTime = currentTimeMillis();
            packageStats.setLoadTime(endTime - startTime);
            packageStatsList.add(packageStats);
        }
		if (headerInfo.getTransactionCriteria().getClientID() != null
				&& !StringUtils.isEmpty(headerInfo.getTransactionCriteria().getClientID())) {
			cacheRegistry.getTopic(PublishingStatus.LOAD_MODEL)
					.publish(headerInfo.getTransactionCriteria().getClientID() + "@" + PublishingStatus.LOAD_MODEL);
           }
    }

    private String getModelPath(final HeaderInfo headerInfo) throws SystemException {
        String sanBase = systemInfo.getSanPath();
        // umgFileProxy.getSanPath(systemInfo.getSanPath())
        return StringUtils.join(sanBase, PATH_SEP, headerInfo.getTenantCode(), PATH_SEP, "modelLibrary", PATH_SEP,
                headerInfo.getModelLibraryName(), PATH_SEP, headerInfo.getModelLibraryVersionName(), PATH_SEP,
                headerInfo.getJarName());

    }

    private ModelKey createModelKey(final HeaderInfo headerInfo) throws SystemException {
        final String key = headerInfo.getModelLibraryVersionName();
        long start = System.currentTimeMillis();
        ModelKey modelKey = modelKeyMap.get(key);
        LOGGER.info("modelKeyMap.get(key): " + (System.currentTimeMillis() - start));

        if (modelKey == null) {
            modelKey = new ModelKey();

            modelKey.getCommandsStatus().setLoadLibraries(true);
            modelKey.getCommandsStatus().setLoadedLibraries(false);
            modelKey.getCommandsStatus().setUnloadLibraries(false);

            modelKey.getCommandsStatus().setLoadModel(true);
            modelKey.getCommandsStatus().setLoadedModel(false);
            modelKey.getCommandsStatus().setUnloadModel(false);

            start = System.currentTimeMillis();
            modelKeyMap.put(key, modelKey);
            LOGGER.info("modelKeyMap.get(key): " + (System.currentTimeMillis() - start));
        }
        modelKey.setFilePath(getModelPath(headerInfo));
        modelKey.setModelClass(headerInfo.getModelClass());
        modelKey.setModelLibrary(headerInfo.getModelLibraryName());
        modelKey.setModelMethod(headerInfo.getModelMethod());
        modelKey.setModelName(headerInfo.getModelName());
        modelKey.setTenantCode(headerInfo.getTenantCode());
        modelKey.setUmgName(headerInfo.getModelLibraryVersionName());
        modelKey.setLibraryNames(new ArrayList<String>());
        modelKey.setModelPackageName(headerInfo.getModelPackageName());
        modelKey.setLibInstallPathByPackage(new HashMap<String, String>());
        return modelKey;
    }

    private void installAndLoadLibraries(final HeaderInfo headerInfo, final REngineDelegator rEngineInvoker,
    		final List<RPackageStats> packageStatsList, final ModelKey modelKey) throws SystemException {
    	String packagePath = null;
    	LOGGER.info("Installaing Libraries");
    	LOGGER.info("No of Required Libraries to run a model :" + headerInfo.getLibraries().size());
    	Collections.sort(headerInfo.getLibraries());
    	for (LibraryInfo libraryInfo : headerInfo.getLibraries()) {
    		if (libraryInfo.getPackageType().equalsIgnoreCase("ADDON")) {
    			final RPackageStats packageStats = new RPackageStats();
    			packageStats.setPackageName(libraryInfo.getPackageFolder());
    			packageStats.setModel(false);
    			packageStats.setJarName(libraryInfo.getPackageName());
    			packageStats.setExecutionTime(0l);
    			packageStats.setSize("0KB");
    			packagePath = getCompletePackagePath(headerInfo, libraryInfo);

    			final String installFolderPath = packagePath + INSTALL_FOLDER_NAME;
    			LOGGER.info("Support Package Path:" + packagePath);

    			long startTime = currentTimeMillis();
    			rEngineInvoker.installPackage(packagePath, installFolderPath, libraryInfo.getPackageFolder());
    			long endTime = currentTimeMillis();
    			packageStats.setInstallTime(endTime - startTime);

    			startTime = endTime;
    			rEngineInvoker.loadPackage(installFolderPath, libraryInfo.getPackageFolder());
    			endTime = currentTimeMillis();
    			packageStats.setLoadTime(endTime - startTime);

    			startTime = endTime;
    			rEngineInvoker.addToLibraryPath(installFolderPath);
    			endTime = currentTimeMillis();
    			packageStats.setAddLibPathTime(endTime - startTime);

    			rEngineInvoker.getModelKey().getLibraryNames().add(libraryInfo.getPackageFolder());
    			rEngineInvoker.getModelKey().getLibInstallPathByPackage().put(libraryInfo.getPackageFolder(),
    					installFolderPath);
    			packageStatsList.add(packageStats);
    		} else {

    			LOGGER.info(libraryInfo.getPackageName() + " is already loaded");
    		}
    	}

    	modelKey.getCommandsStatus().setLoadedLibraries(true);

		if (headerInfo.getTransactionCriteria().getClientID() != null
				&& !StringUtils.isEmpty(headerInfo.getTransactionCriteria().getClientID())) {
			cacheRegistry.getTopic(PublishingStatus.LOAD_LIB)
					.publish(headerInfo.getTransactionCriteria().getClientID() + "@" + PublishingStatus.LOAD_LIB);
    	}
    }

	private String getCompletePackagePath(final HeaderInfo headerInfo, final LibraryInfo libraryInfo)
			throws SystemException {
        String execEnv = headerInfo.getTransactionCriteria().getExecutionEnvironment();
        if (StringUtils.equalsIgnoreCase(SystemConstants.WINDOWS_OS, execEnv)) {
            execEnv = SystemConstants.WINDOWS_OS;
        } else {
            execEnv = SystemConstants.LINUX_OS;
        }
        String sanBase = systemInfo.getSanPath();
        // umgFileProxy.getSanPath(systemInfo.getSanPath())
		final String packagePath = StringUtils.join(sanBase, PATH_SEP, SUPPORT_PACKAGE_FOLDER, PATH_SEP,
				libraryInfo.getExecEnv(), PATH_SEP, libraryInfo.getEnvVersion(), PATH_SEP, execEnv, PATH_SEP,
				libraryInfo.getPackageFolder(), PATH_SEP);
        return StringUtils.join(packagePath, libraryInfo.getPackageName());
    }

	private Boolean isLodedSameModel(final HeaderInfo headerInfo) {
		Boolean isLodedSameModel = true;
		TransactionCriteria transactionCriteria = headerInfo.getTransactionCriteria();
		if (StringUtils.isBlank(systemInfo.getModelName()) || StringUtils.isBlank(systemInfo.getModelVersion())
				|| StringUtils.isBlank(systemInfo.getModelIdentifier()) || transactionCriteria == null
				|| !(StringUtils.equals(systemInfo.getModelName(), transactionCriteria.getModelName()))
				|| !(StringUtils.equals(systemInfo.getModelVersion(), transactionCriteria.getModelVersion()))
				|| !(StringUtils.equals(systemInfo.getModelIdentifier(), headerInfo.getModelIdentifier()))) {
			isLodedSameModel = false;
		}
		return isLodedSameModel;
	}

    public void clearModeletKeyMap() {
        modelKeyMap.clear();
    }

    /*
	 * private String getInstallFolderPath(final HeaderInfo headerInfo, final
	 * LibraryInfo libraryInfo) throws SystemException { final String
	 * completePackagePath = getCompletePackagePath(headerInfo, libraryInfo); return
     * StringUtils.join(completePackagePath, INSTALL_FOLDER_NAME); }
     */
    public void runGC() throws BusinessException, SystemException {
        final ModelKey modelKey = null;
        final REngineDelegator rEngineInvoker = new REngineDelegator(rEngine, modelKey, rTextConsole);
        rEngineInvoker.runGC();
    }

    @Override
    public void destroyConnection() {
        // TODO Auto-generated method stub

    }
}