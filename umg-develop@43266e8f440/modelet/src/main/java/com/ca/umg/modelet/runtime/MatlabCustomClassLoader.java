package com.ca.umg.modelet.runtime;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.modelet.common.MatlabModel;
import com.ca.umg.modelet.common.ModelKey;
import com.ca.umg.modelet.common.SystemInfo;
import com.ca.umg.modelet.constants.ErrorCodes;
import com.mathworks.toolbox.javabuilder.MWComponentOptions;
import com.mathworks.toolbox.javabuilder.MWCtfExtractLocation;
import com.mathworks.toolbox.javabuilder.MWCtfStreamSource;

@SuppressWarnings("PMD")
public class MatlabCustomClassLoader extends ClassLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatlabCustomClassLoader.class);

    @Inject
    private UmgFileProxy umgFileProxy;

    final private Map<String, Class<Object>> classesMap = new ConcurrentHashMap<>();
    final private Map<String, Object> instancesMap = new ConcurrentHashMap<>();
    final private Map<ModelKey, Object> models = new HashMap<>();

    private final SystemInfo systemInfo;

    public MatlabCustomClassLoader(final ClassLoader classLoader, SystemInfo systemInfo) {
        super(classLoader);
        this.systemInfo = systemInfo;
    }

    public void loadJarURLClassLoader(final String filePath) throws SystemException {
        // URLClassLoader classLoader = new URLClassLoader(new URL)
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void loadJar(final String filePath) throws SystemException {
        JarFile jarFile =  null;
    	try {
            File file = new File(umgFileProxy.getSanPath(systemInfo.getSanPath()));
            if (!file.exists()) {
                LOGGER.error("San Base path not available");
                throw new SystemException(ErrorCodes.ME0011, new String[] { "" });
            }
            file = new File(filePath);
            if (!file.exists()) {
                LOGGER.error("File not found in the given location - " + filePath);
                throw new SystemException(ErrorCodes.ME0012, new String[] { filePath });
            }
            jarFile = new JarFile(file);
            JarEntry jarEntry = null;
            final Enumeration<JarEntry> enumeration = jarFile.entries();
            String packageName = null;
            String className = null;
            final String modelName = getModelName(filePath);
            final MWComponentOptions mwCompOpts = new MWComponentOptions();
            LOGGER.info("Loading jar for model {}", modelName);
            while (enumeration.hasMoreElements()) {
                jarEntry = enumeration.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    LOGGER.info("Loading class {}", jarEntry.getName());
                    packageName = getPackageName(jarEntry.getName());
                    className = getClassName(jarEntry.getName());
                    final InputStream inputStream = jarFile.getInputStream(jarEntry);
                    final byte[] classByte = getClassBytes(inputStream);
                    if (packageName != null && getPackage(packageName) == null) {
                        definePackage(packageName, "", "", "", "", "", "", null);
                    }
                    final Class result = defineClass((packageName != null ? packageName + "." : "") + className, classByte, 0,
                            classByte.length, null);
                    classesMap.put(getAbsoluteClassName(packageName, className), result);
                } else if (jarEntry.getName().endsWith(".ctf")) {
                    LOGGER.info("Loading ctf file {}", jarEntry.getName());
                    final InputStream ctfStream = jarFile.getInputStream(jarEntry);
                    InputStream ctfInStream = new BufferedInputStream(new ByteArrayInputStream(IOUtils.toByteArray(ctfStream)));
                    mwCompOpts.setCtfSource(new MWCtfStreamSource(ctfInStream));
                }
            }
            jarFile.close();
            mwCompOpts.setExtractLocation(new MWCtfExtractLocation(systemInfo.getWorkspacePath()));
            try {
                final Object modelInstance = classesMap.get(modelName + "." + modelName).getConstructor(MWComponentOptions.class)
                        .newInstance(mwCompOpts);
                instancesMap.put(modelName, new MatlabModel(modelInstance, modelName));
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException
                    | InvocationTargetException | NoSuchMethodException e) {
                throw new SystemException(ErrorCodes.ME0002, new String[] { "" }, e);
            }
        } catch (IOException e) {
            throw new SystemException(ErrorCodes.ME0001, new String[] { "" }, e);
        }
        finally{
        	releaseJar(jarFile);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void loadJar(ModelKey modelKey) throws SystemException {
        InputStream ctfInStream = null;
        JarFile jarFile = null;
        InputStream ctfStream = null;
        InputStream inputStream = null;
        try {
            jarFile = new JarFile(new File(modelKey.getFilePath()));
            JarEntry jarEntry = null;
            final Enumeration<JarEntry> enumeration = jarFile.entries();
            String packageName = null;
            String className = null;
            LOGGER.info("Loading jar for model {}", modelKey.toString());
            while (enumeration.hasMoreElements()) {
                jarEntry = enumeration.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    LOGGER.info("Loading class {}", jarEntry.getName());
                    packageName = getPackageName(jarEntry.getName());
                    className = getClassName(jarEntry.getName());
                    inputStream = jarFile.getInputStream(jarEntry);
                    final byte[] classByte = getClassBytes(inputStream);
                    if (packageName != null && getPackage(packageName) == null) {
                        definePackage(packageName, "", "", "", "", "", "", null);
                    }
                    final Class result = defineClass((packageName != null ? packageName + "." : "") + className, classByte, 0,
                            classByte.length, null);
                    classesMap.put(getAbsoluteClassName(packageName, className), result);
                } else if (jarEntry.getName().endsWith(".ctf")) {
                    LOGGER.info("Loading ctf file {}", jarEntry.getName());
                    ctfStream = jarFile.getInputStream(jarEntry);
                    ctfInStream = new BufferedInputStream(new ByteArrayInputStream(IOUtils.toByteArray(ctfStream)));
                }
            }
            jarFile.close();
            final MWComponentOptions mwCompOpts = new MWComponentOptions();
            mwCompOpts.setCtfSource(new MWCtfStreamSource(ctfInStream));
            mwCompOpts.setExtractLocation(new MWCtfExtractLocation(workSpacePath(modelKey)));
            try {
                validateClassMap(modelKey);
                final Object modelInstance = classesMap.get(modelKey.getModelClass()).getConstructor(MWComponentOptions.class)
                        .newInstance(mwCompOpts);
                // reset the filepath because sanpath may change in between while loading the jar
                modelKey.setFilePath(systemInfo.getSanPath());
                models.put(modelKey, new MatlabModel(modelInstance, modelKey.getModelMethod()));
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException
                    | InvocationTargetException | NoSuchMethodException e) {
                throw new SystemException(ErrorCodes.ME0002,
                        new String[] { modelKey.getModelClass(), modelKey.getModelMethod() }, e);
            }
        } catch (IOException e) {
            throw new SystemException(ErrorCodes.ME0001, new String[] { modelKey.getFilePath() }, e);
        } finally {
            release(ctfInStream);
            release(ctfStream);
            release(inputStream);
            releaseJar(jarFile);
        }
    }

    private void releaseJar(JarFile jarFile) {
        if (jarFile != null) {
            try {
                jarFile.close();
                jarFile = null; // NOPMD
            } catch (IOException e) {
                jarFile = null; // NOPMD
            }
        }

    }

    private void release(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
                inputStream = null;// NOPMD
            } catch (IOException e) {
                inputStream = null;// NOPMD
            }
        }
    }

    private String getPackageName(final String jarFileClassName) {
        String packageName = null;
        if (jarFileClassName.indexOf('/') != -1) {
            packageName = jarFileClassName.substring(0, jarFileClassName.lastIndexOf('/')).replaceAll("\\/", "\\.");
        }
        return packageName;
    }

    private String getClassName(final String jarFileClassName) {
        String className = null;
        if (jarFileClassName.indexOf('/') != -1) {
            className = jarFileClassName.substring(jarFileClassName.lastIndexOf('/') + 1, jarFileClassName.lastIndexOf('.'));
        } else {
            className = jarFileClassName.substring(0, jarFileClassName.indexOf('.'));
        }
        return className;
    }

    private String getAbsoluteClassName(final String packageName, final String className) {
        String absClassName;
        if (packageName == null) {
            absClassName = className;
        } else {
            absClassName = packageName + "." + className;
        }
        return absClassName;
    }

    private String getModelName(final String filePath) {
        return filePath.substring(filePath.lastIndexOf(System.getProperty("file.separator")) + 1, filePath.lastIndexOf('.'));
    }

    public byte[] getClassBytes(final InputStream inputStream) throws SystemException {
        byte[] classBytes = null;
        try {
            classBytes = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new SystemException("", new String[] { "" }, e);
        }
        return classBytes;
    }

    public void loadJars(final String... jarpaths) throws SystemException {
        for (String path : jarpaths) {
            loadJar(path);
        }
    }

    public void loadJarsDirectory(final String directoryPath) throws SystemException {
        final File directory = new File(directoryPath);
        File[] files = null;
        if (ArrayUtils.isNotEmpty(directory.listFiles())) {
            files = directory.listFiles();
            for (File file : files) {
                if (file.getName().endsWith(".jar")) {
                    loadJars(file.getAbsolutePath());
                }
            }
        }
        directory.listFiles();
    }

    public Object getInstance(final String modelName) {
        return instancesMap.get(modelName);
    }

    public Object getInstance(ModelKey modelKey) throws SystemException {
        if (!models.containsKey(modelKey)) {
            try {
                loadJar(modelKey);
            } catch (Exception e) {// NOPMD
                LOGGER.error("Exception occured loading required jar", e);
                SystemException.newSystemException(ErrorCodes.ME0005,
                        new String[] { modelKey.getFilePath(), modelKey.getModelClass(), modelKey.getModelMethod() }, e);
            }
        }
        return models.get(modelKey);
    }

    public Map<String, Class<Object>> getClassesTable() {
        return classesMap;
    }

    public Map<String, Object> getInstancesTable() {
        return instancesMap;
    }

    private String workSpacePath(ModelKey modelKey) {
        return StringUtils.join(systemInfo.getWorkspacePath(), Character.toString(File.separatorChar), modelKey.getTenantCode(),
                Character.toString(File.separatorChar), modelKey.getModelLibrary(), Character.toString(File.separatorChar),
                modelKey.getUmgName());
    }

    private void validateClassMap(ModelKey modelKey) throws SystemException {
        if (!classesMap.containsKey(modelKey.getModelClass())) {
            SystemException.newSystemException(ErrorCodes.ME0006, new String[] { modelKey.getModelClass() });
        }
    }

}