package com.ca.umg.modelet.common;

import static com.ca.umg.modelet.runtime.impl.RRuntime.PATH_SEP;
import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.StringEscapeUtils.escapeJava;

import java.io.File;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.exception.ModeletExceptionCodes;
import com.ca.umg.modelet.runtime.impl.RTextConsole;

@SuppressWarnings("PMD")
public class REngineDelegator {
    private static final int ERROR_MSG_LENGTH = 0;
    private final static String LIB_PATH_COMMAND = ".libPaths";
    private final static String SO_EXT = ".so";
    private final static String PACKAGE_LIB_FOLDER = "libs";

    private static final Logger LOGGER = LoggerFactory.getLogger(REngineDelegator.class);
    final private Rengine rEngine;
    final private ModelKey modelKey;
    final private RTextConsole rTextConsole;
    /* changes for UMG-5015 */
    private String rText;

    public REngineDelegator(final Rengine rEngine, final ModelKey modelKey, final RMainLoopCallbacks rTextConsole) {
        this.rEngine = rEngine;
        this.modelKey = modelKey;
        this.rTextConsole = (RTextConsole) rTextConsole;
        this.rTextConsole.clearCompleteMessageList();
        this.rTextConsole.setForModel(Boolean.FALSE);
    }

    private REXP eval(final String command) {
        final String logText = "R Execution Command :" + command;
        rTextConsole.addRCommandMessage(logText);
        rTextConsole.clearErrorMessage();
        return rEngine.eval(command);
    }

    public REXP executeModel(final String command) throws SystemException {
        REXP result = null;
        try {
            rTextConsole.setForModel(Boolean.TRUE);
            result = eval(command);
            /* changes for UMG-5015 */
            this.rText = rTextConsole.getCompleteMessage();
            final String errorMessage = rTextConsole.getErrorMessage();

            if (modelKey.getCommandsStatus().isUnloadLibraries()) {
                unloadPackages();
                clearLibraryPath();
            }

            if (modelKey.getCommandsStatus().isUnloadModel()) {
                unloadModel();
            }
            if (result == null || errorMessage.length() > ERROR_MSG_LENGTH) {
                LOGGER.error("An error occurred while executing R command {}.", errorMessage);
                rTextConsole.setForModel(Boolean.FALSE);
                SystemException.newSystemException(ModeletExceptionCodes.MOSE000001,
                        new String[] { rTextConsole.getCompleteMessage() });
            }
        } finally {
            rTextConsole.setForModel(Boolean.FALSE);
        }

        return result;
    }

    public void installPackage(final String packagePath, final String installFolder, final String packageName)
            throws SystemException {
        if (!isPackageInstalled(installFolder)) {
            createInstallFolder(installFolder);
            LOGGER.info("Installaing Package {}", packageName);
            eval("install.packages(\"" + escapeJava(packagePath) + "\", lib=\"" + escapeJava(installFolder)
                    + "\",repos=NULL, type=\"source\")");

            final String errorMessage = rTextConsole.getErrorMessage();
            if (errorMessage.length() > ERROR_MSG_LENGTH) {
                LOGGER.error("Installation of  Package {} failed", packageName);
                rTextConsole.addRCommandMessage(errorMessage);
                unloadPackages();
                clearLibraryPath();
                deleteuInstalledPackage(installFolder);
                //UMG-9697
                this.rText = rTextConsole.getCompleteMessage();
                SystemException.newSystemException(ModeletExceptionCodes.MOSE000002,
                        new String[] { "Error occurred while installing package", rTextConsole.getCompleteMessage() });
            }
            LOGGER.info("Installaing Package {} is success", packageName);
        } else {
            LOGGER.info("Package {} is already installed", packageName);
        }
    }

    public void loadPackage(final String installFolder, final String modelLibName) throws SystemException {
        long startTime = System.currentTimeMillis();
        LOGGER.info("Loading Package {}", modelLibName);
        eval("library(\"" + escapeJava(modelLibName) + "\", lib.loc=\"" + escapeJava(installFolder) + "\")");

        final String errorMessage = rTextConsole.getErrorMessage();
        if (errorMessage.length() > ERROR_MSG_LENGTH) {
            LOGGER.error("Loading Package {} is failed, so unloading all loaded packages, ERROR :: {}", modelLibName, errorMessage);

            final String msg = "Package " + modelLibName
                    + " may be dependent on other packages which may not be part of your manifest file, please correct your manifest file.";
            LOGGER.error(msg);
            rTextConsole.addRCommandMessage(errorMessage);
            rTextConsole.addRCommandMessage(msg);
            unloadPackages();
            clearLibraryPath();
            deleteuInstalledPackage(installFolder);
            //UMG-9697
            this.rText = rTextConsole.getCompleteMessage();
            SystemException.newSystemException(ModeletExceptionCodes.MOSE000003,
                    new String[] { "Error occurred while loading package", rTextConsole.getCompleteMessage() + ", " + msg });
        }

        LOGGER.info("Loading Package {} is success in {} ms.", modelLibName, System.currentTimeMillis() - startTime);
    }

    public void addToLibraryPath(final String libraryPath) throws SystemException {
        final StringBuilder sb = new StringBuilder(100);
        final String newLibraryVector = "c(\"" + escapeJava(libraryPath) + "\", ";
        sb.append(LIB_PATH_COMMAND).append("(");
        sb.append(newLibraryVector).append("");
        sb.append(LIB_PATH_COMMAND).append("()))");

        LOGGER.info("Adding to libPath {}", sb.toString());
        eval(sb.toString());

        final String errorMessage = rTextConsole.getErrorMessage();
        if (errorMessage.length() > ERROR_MSG_LENGTH) {
            LOGGER.error("Adding to Library Path is failed, so unloading all loaded packages, Library Path is {}, ERROR :: {}",
                    libraryPath, errorMessage);
            rTextConsole.addRCommandMessage(errorMessage);
            unloadPackages();
            clearLibraryPath();
            //UMG-9697
            this.rText = rTextConsole.getCompleteMessage();
            SystemException.newSystemException(ModeletExceptionCodes.MOSE000002,
                    new String[] { "Adding to library path failed", rTextConsole.getCompleteMessage() });
        }
    }

    private void clearLibraryPath() throws SystemException {
        LOGGER.info("Re-setting the library path");
        eval(LIB_PATH_COMMAND + "(\"\")");

        final String errorMessage = rTextConsole.getErrorMessage();
        if (errorMessage.length() > ERROR_MSG_LENGTH) {
            LOGGER.error("Clearing Library Path is failed, so unloading all loaded packages. {}", errorMessage);
            SystemException.newSystemException(ModeletExceptionCodes.MOSE000004,
                    new String[] { "Clearing library path failed", rTextConsole.getCompleteMessage() });
        }
    }

    private void createInstallFolder(final String installFolderPath) throws SystemException {
        final boolean created = new File(installFolderPath).mkdir();
        if (!created) {
            SystemException.newSystemException(ModeletExceptionCodes.MOSE000005,
                    new String[] { installFolderPath + " does not exist" });
        }
    }

    private boolean isPackageInstalled(final String filePath) {
        final File file = new File(filePath);
        return file.exists();
    }

    private void unloadModel() throws SystemException {
        long startTime = currentTimeMillis();
        eval("detach(\"package:" + modelKey.getModelPackageName() + "\", unload=TRUE)");
        LOGGER.info("Unloading time for model " + modelKey.getModelPackageName() + ":" + (currentTimeMillis() - startTime));

        final String errorMessage = rTextConsole.getErrorMessage();
        if (errorMessage.length() > ERROR_MSG_LENGTH) {
            SystemException.newSystemException(ModeletExceptionCodes.MOSE000006,
                    new String[] {rTextConsole.getCompleteMessage() });
        }
    }

    private void unloadPackages() throws SystemException {
        if (modelKey.getLibraryNames() != null && !modelKey.getLibraryNames().isEmpty()) {
            for (int i = modelKey.getLibraryNames().size() - 1; i >= 0; i--) {
                long startTime = currentTimeMillis();
                eval("detach(\"package:" + modelKey.getLibraryNames().get(i) + "\", unload=TRUE)");
                LOGGER.info("Unloading time for package " + modelKey.getLibraryNames().get(i) + ":"
                        + (currentTimeMillis() - startTime));

                final String errorMessage = rTextConsole.getErrorMessage();
                if (errorMessage.length() > ERROR_MSG_LENGTH) {
                    LOGGER.error("Unloading of Package {} is failed, so unloading all loaded packages. Error :: {}",
                            modelKey.getLibraryNames().get(i), errorMessage);
                    SystemException.newSystemException(ModeletExceptionCodes.MOSE000007,
                            new String[] {rTextConsole.getCompleteMessage() });
                }
            }
        }
    }

    public ModelKey getModelKey() {
        return modelKey;
    }

    public void loadPackagesDynamically(final String installFolder, final String modelLibName) throws SystemException {
        LOGGER.info("Dynamic Loading of Package {}", modelLibName);
        eval("dyn.load" + createDLLPath(installFolder, modelLibName));
        final String errorMessage = rTextConsole.getErrorMessage();
        if (errorMessage.length() > ERROR_MSG_LENGTH) {
            LOGGER.info("Dynamic Loading of Package {} is failed, so unloading all loaded packages", modelLibName);
            unloadPackages();
            clearLibraryPath();
            SystemException.newSystemException(ModeletExceptionCodes.MOSE000008,
                    new String[] {rTextConsole.getCompleteMessage() });
        }
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
}