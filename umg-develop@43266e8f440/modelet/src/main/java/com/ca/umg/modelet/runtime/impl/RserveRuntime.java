package com.ca.umg.modelet.runtime.impl;

import static com.ca.umg.modelet.exception.ModeletExceptionCodes.MOSE000004;
import static java.io.File.separatorChar;
import static java.lang.System.currentTimeMillis;
import static org.rosuda.JRI.Rengine.versionCheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.connection.Connector;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.publishing.status.constants.PublishingStatus;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.modelet.common.HeaderInfo;
import com.ca.umg.modelet.common.LibraryInfo;
import com.ca.umg.modelet.common.ModelKey;
import com.ca.umg.modelet.common.RServeDelegator;
import com.ca.umg.modelet.common.RServeModel;
import com.ca.umg.modelet.common.SystemInfo;
import com.ca.umg.modelet.runtime.ModeletRuntime;

/**
 * Created by repvenk on 1/10/2017.
 */
public class RserveRuntime implements ModeletRuntime {

    private static final int WAITING_TIME = 5000;
	private static final Logger LOGGER = LoggerFactory.getLogger(RserveRuntime.class);
    private boolean initialized;
    private RConnection rConnection;

    private SystemInfo systemInfo;

    private UmgFileProxy umgFileProxy;

    private Connector connector;
    
    private CacheRegistry cacheRegistry;

    private Map<String, ModelKey> modelKeyMap = new HashMap<String, ModelKey>();

    private final RMainLoopCallbacks rTextConsole = new RTextConsole();

    public static final String PATH_SEP = String.valueOf(separatorChar);
    public static final String INSTALL_FOLDER_NAME = "_install";
    public static final String SUPPORT_PACKAGE_FOLDER = "supportpackage";

    private int pid = 0;

    private int parentProcessId = 0;
    
    public RserveRuntime(final SystemInfo systemInfo, final UmgFileProxy umgFileProxy, Connector connector, CacheRegistry cacheRegistry) {
        this.systemInfo = systemInfo;
        this.umgFileProxy = umgFileProxy;
        this.connector = connector;
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
            throw new SystemException(MOSE000004, new String[] { "R Runtime Initialization Failed", e.getMessage() }, e);
        }
    }

    private void versionMismatchCheck() throws SystemException {
        if (!versionCheck()) {
            LOGGER.info("R Version Mismatch - Java files don't match library version.");
            LOGGER.info("Failed to initialize R runtime");
            throw new SystemException(MOSE000004, new String[] { "R", "R Runtime Version Mismatch" });
        }
    }

    private void initializeRengine() throws SystemException {
        // if (!StringUtils.containsIgnoreCase(System.getProperty("os.name"), ExecutionEnvironment.WINDOWS.getEnvironment())) {
        // initialized = connector.executeCommand("nohup R CMD Rserve --RS-port " + systemInfo.getrServePort() + " --vanilla &");
        // }
        // if (!isInitialized()) {
        // LOGGER.error("R Serve process invocation failed");
        // }
        // initialized = false;
        LOGGER.info("Creating R Rengine");
        try {
            /*
             * RConnection rServeInitializer = new RConnection(); rServeInitializer.eval("library(\"Rserve\")");
             * rServeInitializer.eval("Rserve(args=\"--no-save\", port=" + rServePort + ")"); rServeInitializer.close();
             */
        	LOGGER.info("Rserve port : ", systemInfo.getrServePort());
            rConnection = new RConnection("localhost", systemInfo.getrServePort());
            
            pid = rConnection.eval("Sys.getpid()").asInteger();
            LOGGER.error("R Serve connection established in port {} with process id {} : ", systemInfo.getrServePort(), pid);

            // rConnection.eval("sink(file = \"/usr/local/umg-logs/rserve.out\", type=c(\"output\", \"message\"), append = TRUE,
            // split=FALSE)");
            // LOGGER.error("R Serve evaluation done for rServeLogFile.");
            // rConnection.eval(".libPaths(c(\"/usr/lib64/R/library/rJava/jri\", \"/usr/lib64/R/lib\",
            // \"/usr/lib/jvm/java-1.7.0-openjdk-1.7.0.85.x86_64/jre/lib/amd64/server\", .libPaths()))");
            // LOGGER.error("R Serve evaluation done for library Paths.");

            // rConnection.eval("library(\"rJava\")");
            
            if(systemInfo.isUnix()){
            setParentProcessId(pid);
            pollingForRServeProcess(pid);
            }
            
            LOGGER.error("R Serve evaluation done for rJava.");
        } catch (RserveException | REXPMismatchException e) {
            throw new SystemException(MOSE000004, new String[] { "R Runtime Initialization Failed" });
        }
        initialized = true;
        LOGGER.info("Successfully Initialized R Runtime");
    }

    @Override
    public void destroyRuntime() {
        LOGGER.info("Terminating R Runtime");
        if (rConnection != null && rConnection.isConnected()) {
            // rConnection.eval("sink()");
            try {
                rConnection.shutdown();
            } catch (RserveException e) {
                LOGGER.error(e.getMessage(), e);
            }
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
        final RServeDelegator rEngineInvoker = new RServeDelegator(rConnection, modelKey);
        LOGGER.info("REngineDelegator is created");

        final MemoryFoot mfBeforeInstallAndLoadLibraries = new MemoryFoot();
        final long startInstallAndLoadLibraries = currentTimeMillis();
        final List<RPackageStats> packageStatsList = new ArrayList<>();
        if(headerInfo.isStoreRLogs()  || (headerInfo.getTransactionCriteria().getTransactionRequestType() != null && StringUtils.equalsIgnoreCase(headerInfo.getTransactionCriteria().getTransactionRequestType(), "test"))){
        String fileLocation = createLogFileLocation(systemInfo, headerInfo);
        if(systemInfo.isUnix()){
        	rEngineInvoker.initiateRmodelLogging(fileLocation);
        }
        }
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
        LOGGER.info(
                "Total time taken to Insatll and Load Libraries :" + (endInstallAndLoadLibraries - startInstallAndLoadLibraries));

        mfBeforeInstallAndLoadLibraries.printMemoryFootPrint("Memory FooT Print Before Installing and Loading Libraries :",
                LOGGER);
        mfAfterInstallAndLoadLibraries.printMemoryFootPrint("Memory FooT Print After Installing and Loading Libraries :", LOGGER);

        LOGGER.info("Start Time of Install and Load {} Package :" + startInstallAndLoadModel, headerInfo.getModelPackageName());
        LOGGER.info("End Time of Install and Load {} Package :" + endinstallAndLoadModel, headerInfo.getModelPackageName());
        LOGGER.info("Total time taken to Insatll and Load {} Package :" + (endinstallAndLoadModel - startInstallAndLoadModel),
                headerInfo.getModelPackageName());

        mfBeforeInstallAndLoadModel.printMemoryFootPrint("Memory FooT Print Before Installing and Loading Model :", LOGGER);
        mfAfterInstallAndLoadModel.printMemoryFootPrint("Memory FooT Print After Installing and Loading Model :", LOGGER);

        LOGGER.info("Package Name" + "," + "Size" + "," + "Install Time" + "," + "Load Time" + "," + "Add Lib Time" + ","
                + "Total Time ");
        for (RPackageStats stats : packageStatsList) {
            LOGGER.info(stats.toString());
        }

        return new RServeModel(rEngineInvoker);
    }

    private void installAndLoadModel(final HeaderInfo headerInfo, final RServeDelegator rEngineInvoker,
            final List<RPackageStats> packageStatsList, final ModelKey modelKey) throws SystemException {
        // System.exit(0);
        if (!modelKey.getCommandsStatus().isLoadedModel()) {
            final RPackageStats packageStats = new RPackageStats();
            packageStats.setPackageName(headerInfo.getModelPackageName());
            packageStats.setModel(true);
            packageStats.setJarName(headerInfo.getJarName());
            packageStats.setAddLibPathTime(0l);

            long startTime = currentTimeMillis();
            final String modelPath = getModelPath(headerInfo);
            LOGGER.info("Model Path :" + modelPath);
            final String installFolder = modelPath + INSTALL_FOLDER_NAME;
            rEngineInvoker.installPackage(modelPath, installFolder, headerInfo.getModelPackageName(), connector);
            long endTime = currentTimeMillis();

            packageStats.setInstallTime(endTime - startTime);

            startTime = endTime;

            rEngineInvoker.loadPackage(installFolder, headerInfo.getModelPackageName(), connector);
            modelKey.getCommandsStatus().setLoadedModel(true);
            endTime = currentTimeMillis();
            packageStats.setLoadTime(endTime - startTime);
            packageStatsList.add(packageStats);
        }
        if(headerInfo.getTransactionCriteria().getClientID() != null && ! StringUtils.isEmpty(headerInfo.getTransactionCriteria().getClientID())){
            cacheRegistry.getTopic(PublishingStatus.LOAD_MODEL).publish(headerInfo.getTransactionCriteria().getClientID()+ "@" + PublishingStatus.LOAD_MODEL);
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

    private void installAndLoadLibraries(final HeaderInfo headerInfo, final RServeDelegator rEngineInvoker,
    		final List<RPackageStats> packageStatsList, final ModelKey modelKey) throws SystemException {
    	String packagePath = null;
    	LOGGER.info("Installaing Libraries");
    	LOGGER.info("No of Required Libraries to run a model :" + headerInfo.getLibraries().size());
    	LOGGER.info("Loading Required Libraries");
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
    			rEngineInvoker.installPackage(packagePath, installFolderPath, libraryInfo.getPackageFolder(), connector);
    			long endTime = currentTimeMillis();
    			packageStats.setInstallTime(endTime - startTime);

    			startTime = endTime;
    			rEngineInvoker.loadPackage(installFolderPath, libraryInfo.getPackageFolder(), connector);
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
    	
    	if(headerInfo.getTransactionCriteria().getClientID() != null && ! StringUtils.isEmpty(headerInfo.getTransactionCriteria().getClientID())){
    		cacheRegistry.getTopic(PublishingStatus.LOAD_LIB).publish(headerInfo.getTransactionCriteria().getClientID() + "@" + PublishingStatus.LOAD_LIB);
    	}
    }

    private String getCompletePackagePath(final HeaderInfo headerInfo, final LibraryInfo libraryInfo) throws SystemException {
        String execEnv = headerInfo.getTransactionCriteria().getExecutionEnvironment();
        if (StringUtils.equalsIgnoreCase(SystemConstants.WINDOWS_OS, execEnv)) {
            execEnv = SystemConstants.WINDOWS_OS;
        } else {
            execEnv = SystemConstants.LINUX_OS;
        }
        String sanBase = systemInfo.getSanPath();
        // umgFileProxy.getSanPath(systemInfo.getSanPath())
        final String packagePath = StringUtils.join(sanBase, PATH_SEP, SUPPORT_PACKAGE_FOLDER, PATH_SEP, libraryInfo.getExecEnv(),
                PATH_SEP, libraryInfo.getEnvVersion(), PATH_SEP, execEnv, PATH_SEP, libraryInfo.getPackageFolder(), PATH_SEP);
        return StringUtils.join(packagePath, libraryInfo.getPackageName());
    }

    public void clearModeletKeyMap() {
        modelKeyMap.clear();
    }

    /*
     * private String getInstallFolderPath(final HeaderInfo headerInfo, final LibraryInfo libraryInfo) throws SystemException {
     * final String completePackagePath = getCompletePackagePath(headerInfo, libraryInfo); return
     * StringUtils.join(completePackagePath, INSTALL_FOLDER_NAME); }
     */
    public void runGC() throws BusinessException, SystemException {
        final ModelKey modelKey = null;
        final RServeDelegator rEngineInvoker = new RServeDelegator(rConnection, modelKey);
        rEngineInvoker.runGC();
    }

    @Override
    public void destroyConnection() {
        LOGGER.info("Terminating R serve connection for pid {}.", pid);
        if (rConnection != null && rConnection.isConnected()) {
            try {

                // rConnection.shutdown();
                // rConnection.serverShutdown();
                // rConnection.voidEval("q()");
                rConnection.voidEvalDetach("q()");
                killParentRServeProcess();
            } catch (RserveException e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                rConnection.close();
            }
        }
        initialized = false;
    }
    
    /**
     * Method to set Parent process Id for R Serve unix child process
     * @param childProcessId
     */
    public void setParentProcessId(int childProcessId) {
		BufferedReader reader = null;
		try {
			Process process = Runtime.getRuntime().exec("ps -o ppid " + childProcessId);
			if (process != null) {
				int exitStatus = process.waitFor();
				if (exitStatus == 0) {
					reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
					// Header line , so skipping
					String line = reader.readLine();
					line = reader.readLine();
					if (StringUtils.isNotEmpty(line)) {
						LOGGER.info("Parent Process Id  is " + Integer.parseInt(line.trim()));
						parentProcessId = Integer.parseInt(line.trim());
					}
				} else {
					LOGGER.info("Exit status is 1.Error while getting Parent Process Id for child process Id "
							+ childProcessId);
				}
			}
		} catch (IOException e) {
			LOGGER.error("Error while getting parent process id " + e);
		} catch (InterruptedException e) {
			LOGGER.error("Error while waiting for getting  parent process id " + e);
		} catch (NumberFormatException e) {
			LOGGER.error("Got parent process id not in number" + e);
		} catch (Exception e) {
			LOGGER.error("Error while setting  parent process id" + e);
		} finally {
			if(reader != null){
			IOUtils.closeQuietly(reader);
			}
		}
	}
    
    
	/**
	 * Method to check R serve child unix process is running or suspended
	 * 
	 * @param pid
	 * @param executorService
	 * @return
	 */
	public void pollingForRServeProcess(final int pid) {

		ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						ProcessStatus ps = checkingProcessStatus(pid);
						if (ProcessStatus.SUSPENDED == ps || ProcessStatus.EXCEPTION == ps
								|| ProcessStatus.NOT_STARTED == ps) {
							LOGGER.info("Process status for child process " + ps);
							if (ProcessStatus.SUSPENDED == ps) {
								//kill parent  process and modelet
								killParentRServeProcess();
								LOGGER.info("gracefully exiting java application");
                                System.exit(-1);
							}
							break;
						}
						// sleep for 5 seconds
						Thread.sleep(WAITING_TIME);
					} catch (InterruptedException e) {
						LOGGER.error("Interrupted while polling using pid  : " + pid + " " + e);
						break;
					} catch (Exception e) {
						LOGGER.error("Error while polling using pid  : " + pid + " " + e);
						break;
					}

				}
			}
		});

		executorService.shutdown();

	}

	/**
	 * Method to kill parent R serve unix process
	 */
	public void killParentRServeProcess() {
		try {
			if (parentProcessId != 0) {
				Process process = Runtime.getRuntime().exec("kill -9 " + parentProcessId);
				LOGGER.info("killing parent process " + process);
				if (process != null) {
					int exitStatus = process.waitFor();
					if (exitStatus == 0) {
						LOGGER.info("Parent process having process id : " + parentProcessId + "is killed");
					} else {
						LOGGER.info("Exit status is 1.Process  having process id : " + parentProcessId + "is not killed");
					}

				}
			} else {
				LOGGER.info("Parent process Id does not exist for killing it");
			}
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted while killing parent  process having pid  : " + pid + " " + e);
		} catch (Exception e) {
			LOGGER.error("Error while killing parent  process having pid  : " + pid + " " + e);
		}
	}

	public enum ProcessStatus {
		NOT_STARTED, RUNNING, SUSPENDED, EXCEPTION;
	}

	private ProcessStatus checkingProcessStatus(int pid) {

		try {
			Process process = Runtime.getRuntime().exec("ps -p " + pid);
			if (process == null) {
				LOGGER.info("Child Process is not started ");
				return ProcessStatus.NOT_STARTED;
			} else {
				int exitStatus = process.waitFor();
				if (exitStatus == 0) {
					return ProcessStatus.RUNNING;
				} else {
					LOGGER.info(" Process details suspended  ");
					return ProcessStatus.SUSPENDED;
				}
			}
		} catch (IOException e) {
			LOGGER.error("Error while executing command to get process using pid  : " + pid + " " + e);
			return ProcessStatus.EXCEPTION;
		} catch (Exception e) {
			LOGGER.error("Error while executing command to get process using pid  : " + pid + " " + e);
			return ProcessStatus.EXCEPTION;
		}

	}
	 private String createLogFileLocation(SystemInfo systemInfo,HeaderInfo headerInfo){
	    	
	    	String location = systemInfo.getSanPath() + "/" + headerInfo.getTenantCode();
	    	String fileName = MDC.get("tID");
	    	File file = new File(location + "/"  +"rLog" +  "/" + fileName + ".txt");
	    	return "\"" + file.getAbsolutePath() + "\"";
	    }
}