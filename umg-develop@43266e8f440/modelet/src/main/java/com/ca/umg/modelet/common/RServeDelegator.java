package com.ca.umg.modelet.common;

import static com.ca.umg.modelet.exception.ModeletExceptionCodes.MOSE000001;
import static com.ca.umg.modelet.runtime.impl.RRuntime.PATH_SEP;
import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.StringEscapeUtils.escapeJava;

import java.io.File;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.ca.framework.core.connection.Connector;
import com.ca.framework.core.exception.SystemException;

/**
 * Created by repvenk on 1/10/2017.
 */
public class RServeDelegator {

    private static final int ERROR_MSG_LENGTH = 0;
    private final static String LIB_PATH_COMMAND = ".libPaths";
    private final static String SO_EXT = ".so";
    private final static String PACKAGE_LIB_FOLDER = "libs";

    private static final Logger LOGGER = LoggerFactory.getLogger(REngineDelegator.class);
    final private RConnection rEngine;
    final private ModelKey modelKey;
    /* changes for UMG-5015 */
    private String rText;

    public RServeDelegator(final RConnection rEngine, final ModelKey modelKey) {
        this.rEngine = rEngine;
        this.modelKey = modelKey;
    }

    private REXP eval(final String command) {
        final String logText = "R Execution Command :" + command;
        String finalCommand = "try(" + command + ")";
        REXP evaluatedResp = null;
        try {
            evaluatedResp = rEngine.eval(finalCommand);
        } catch (RserveException e) {
            LOGGER.error("Error occurred while executing R command {}.", finalCommand, e);
        }
        return evaluatedResp;
    }

    public REXP executeModel(final String command) throws SystemException {
        REXP result = null;
        try {
            result = eval(command);
            if (modelKey.getCommandsStatus().isUnloadLibraries()) {
                unloadPackages();
                clearLibraryPath();
            }
            

            if (modelKey.getCommandsStatus().isUnloadModel()) {
                unloadModel();
            }
            if (result == null) {
            	String errorMessage = "-ExecutionCommandUsed-" + command;
                SystemException.newSystemException(MOSE000001, new String[] {errorMessage});
            }
        } finally {
        	terminateRmodelLogging();
        	MDC.remove("tID");
        }

        return result;
    }

    public void installPackage(final String packagePath, final String installFolder, final String packageName,
            Connector connector) throws SystemException {
        if (!isPackageInstalled(installFolder)) {
            createInstallFolder(installFolder);
            LOGGER.info("Installaing Package {}", packageName);

            // if (StringUtils.containsIgnoreCase(System.getProperty("os.name"), ExecutionEnvironment.WINDOWS.getEnvironment())) {
            eval("install.packages(\"" + escapeJava(packagePath) + "\", lib=\"" + escapeJava(installFolder)
                    + "\",repos=NULL, type=\"source\")");
            // } else {
            // boolean installationSuccess = connector
            // .executeCommand("R CMD INSTALL --library=" + escapeJava(installFolder) + " " + escapeJava(packagePath));
            //
            // if (!installationSuccess) {
            // SystemException.newSystemException(MOSE000001, new String[] {});
            // }
            //
            // connector.executeCommand("R CMD check " + escapeJava(installFolder));
            // }

            /*
             * if (errorMessage.length() > ERROR_MSG_LENGTH) { LOGGER.info("Installaing Package {} is failed", packageName);
             * unloadPackages(); clearLibraryPath(); deleteuInstalledPackage(installFolder);
             * SystemException.newSystemException(MOSE000001, new String[] { }); }
             */
            LOGGER.info("Installaing Package {} is success", packageName);
        } else {
            LOGGER.info("Package {} is alredy installed", packageName);
        }
    }

    public void loadPackage(final String installFolder, final String modelLibName, Connector connector) throws SystemException {
        LOGGER.info("Loading Package {}", modelLibName);

        REXP result = eval("library(\"" + escapeJava(modelLibName) + "\", lib.loc=\"" + escapeJava(installFolder) + "\")");

        if ( result != null && result.inherits("try-error")) {
            final String errorMessage;
            try {
                errorMessage = result.asString();
                LOGGER.error("Loading Package {} is failed, so unloading all loaded packages", modelLibName);

                final String msg = "Package " + modelLibName
                        + " may be dependent on other packages which may not be part of your manifest file, please correct your manifest file.";
                LOGGER.error(msg);

                unloadPackages();
                clearLibraryPath();
                deleteuInstalledPackage(installFolder);
                SystemException.newSystemException(MOSE000001, new String[] { errorMessage });
            } catch (REXPMismatchException e) {
                LOGGER.error("Could not convert error message to String");
            }
        }

        LOGGER.info("Loading Package {} is success", modelLibName);
    }

    public void addToLibraryPath(final String libraryPath) throws SystemException {
        final StringBuilder sb = new StringBuilder(100);
        final String newLibraryVector = "c(\"" + escapeJava(libraryPath) + "\", ";
        sb.append(LIB_PATH_COMMAND).append("(");
        sb.append(newLibraryVector).append("");
        sb.append(LIB_PATH_COMMAND).append("()))");

        LOGGER.info("Adding to libPath {}", sb.toString());
        eval(sb.toString());

        /*
         * if (errorMessage.length() > ERROR_MSG_LENGTH) { LOGGER.error(
         * "Adding to Library Path is failed, so unloading all loaded packages, Library Path is {}, ERROR :: {}", libraryPath,
         * errorMessage); unloadPackages(); clearLibraryPath(); SystemException.newSystemException(MOSE000001, new String[] { });
         * }
         */
    }

    private void clearLibraryPath() throws SystemException {
        LOGGER.info("Re-setting the library path");
        LOGGER.error("", eval(LIB_PATH_COMMAND + "(\"\")"));

        /*
         * if (errorMessage.length() > ERROR_MSG_LENGTH) { LOGGER.error(
         * "Clearning Library Path is failed, so unloading all loaded packages. {}", errorMessage);
         * SystemException.newSystemException(MOSE000001, new String[] { }); }
         */
    }

    private void createInstallFolder(final String installFolderPath) throws SystemException {
        final boolean created = new File(installFolderPath).mkdir();
        if (!created) {
            SystemException.newSystemException(MOSE000001, new String[] { installFolderPath + " does not exist" });
        }
    }

    private boolean isPackageInstalled(final String filePath) {
        final File file = new File(filePath);
        return file.exists();
    }

    private void unloadModel() throws SystemException {
        long startTime = currentTimeMillis();
        LOGGER.error("", eval("detach(\"package:" + modelKey.getModelPackageName() + "\", unload=TRUE)"));
        LOGGER.info("Unloading time for model " + modelKey.getModelPackageName() + ":" + (currentTimeMillis() - startTime));
    }

    private void unloadPackages() throws SystemException {
        if (modelKey.getLibraryNames() != null && !modelKey.getLibraryNames().isEmpty()) {
            for (int i = modelKey.getLibraryNames().size() - 1; i >= 0; i--) {
                long startTime = currentTimeMillis();
                LOGGER.error("", eval("detach(\"package:" + modelKey.getLibraryNames().get(i) + "\", unload=TRUE)"));
                LOGGER.info("Unloading time for package " + modelKey.getLibraryNames().get(i) + ":"
                        + (currentTimeMillis() - startTime));
            }
        }
    }

    public ModelKey getModelKey() {
        return modelKey;
    }

    public void loadPackagesDynamically(final String installFolder, final String modelLibName) throws SystemException {
        LOGGER.info("Dynamic Loading of Package {}", modelLibName);
        LOGGER.error("", eval("dyn.load" + createDLLPath(installFolder, modelLibName)));
        LOGGER.info("Dynamic Loading of Package {} is success", modelLibName);
    }

    private String createDLLPath(final String installFolder, final String modelLibName) {
        final StringBuffer sb = new StringBuffer();
        sb.append("(");
        sb.append("\"" + escapeJava(installFolder));
        sb.append(PATH_SEP + modelLibName);
        sb.append(PATH_SEP + PACKAGE_LIB_FOLDER + SO_EXT);
        sb.append(")");
        return sb.toString();
    }

    public void unloadPackagesDynamically() {
        if (modelKey.getLibraryNames() != null && !modelKey.getLibraryNames().isEmpty()) {
            for (int i = modelKey.getLibraryNames().size() - 1; i >= 0; i--) {
                final String packageName = modelKey.getLibraryNames().get(i);
                long startTime = currentTimeMillis();
                eval("dyn.unload" + createDLLPath(packageName, modelKey.getLibInstallPathByPackage().get(packageName)));
                LOGGER.info(
                        "Dyanmic Unloading of package " + packageName + " is done within :" + (currentTimeMillis() - startTime));
            }
        }
    }

    private void deleteuInstalledPackage(final String installFolder) {
        LOGGER.info("Uninstall Package, Package Foolder is : {}", installFolder);
        if (isPackageInstalled(installFolder)) {
            final File file = new File(installFolder);
            final boolean status = file.delete();
            if (status) {
                LOGGER.info("Package is uninstall successfully, Deleted Package Foolder is : {}", installFolder);
            } else {
                LOGGER.error("Package is uninstall failed, Package is not deleted, tried deleted Package Foolder is : {}",
                        installFolder);
            }
            LOGGER.info("Package is uninstall successfully, Package Foolder is : {}", installFolder);
        }
    } 

    public String getrText() {
        return rText;
    }

    public void runGC() {
        LOGGER.error("GC started");
        eval("gc()");
        LOGGER.error("GC ended");
    }

    
    public void initiateRmodelLogging( String file) {
        eval("sink(" + file + ", append=FALSE, split=FALSE)");
 }

 public void terminateRmodelLogging() {
        eval("sink()");
 }

}
