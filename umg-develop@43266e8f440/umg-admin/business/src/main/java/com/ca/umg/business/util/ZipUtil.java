package com.ca.umg.business.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.migration.audit.info.MigrationAuditInfo;
import com.ca.umg.business.migration.info.VersionMigrationInfo;
import com.ca.umg.business.migration.info.VersionMigrationWrapper;

@SuppressWarnings("PMD")
public final class ZipUtil {

    private static final String MODELNAMETYPE_DELIMITER = "%%";
    private static final String VERSION_AUDIT_INFO = "VersionAuditInfo.json";
    private static final String VERSION_INFO = "VersionInfo.json";
    private static final String MODEL_DEFINITION = "_MODEL_DEFINITION";
    private static final String MODEL_EXCEL_DEFINITION = "_MODEL_EXCEL_DEFINITION";
    private static final String MODEL_LIB = "_MODEL_LIBRARY";
    private static final String MODEL_DOC = "_MODEL_DOC";
    private static final String TEMP_FOLDER = "TEMP";
    private static final String DESCRIPTION_FILE = "DESCRIPTION";
    private static final Logger LOGGER = LoggerFactory.getLogger(ZipUtil.class);

    private ZipUtil() {
    }

    public static boolean deleteFile(String zipName) {
        return new File(zipName).delete();
    }

    public static byte[] readZipFile(String zipName) throws SystemException, IOException {
        byte[] zipArray = null;
        FileInputStream zfis = null;
        try {
            zfis = new FileInputStream(zipName);
            zipArray = IOUtils.toByteArray(zfis);
        }
        finally {
            IOUtils.closeQuietly(zfis);
        }
        return zipArray;
    }

    public static void createZipFile(ZipInputStream zipInputStream, String zipName) throws FileNotFoundException, IOException { 
        int size;
        byte[] buffer = new byte[BusinessConstants.TWO_KB];
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try { //NOPMD
            fos = new FileOutputStream(zipName);
            bos =  new BufferedOutputStream(fos, buffer.length);
            while ((size = zipInputStream.read(buffer, 0, buffer.length)) != -1) { //NOPMD
                bos.write(buffer, 0, size);
            }
            bos.flush();
        }
        finally {
        	closeResources(fos, bos);
        }
    }

    public static void createZipFile(InputStream inputStream, String zipName) throws FileNotFoundException, IOException { 
        int size;
        byte[] buffer = new byte[BusinessConstants.TWO_KB];
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try { //NOPMD
            fos = new FileOutputStream(zipName);
            bos =  new BufferedOutputStream(fos, buffer.length);
            while ((size = inputStream.read(buffer, 0, buffer.length)) != -1) { //NOPMD
                bos.write(buffer, 0, size);
            }
            bos.flush();
        }
        finally {
        	closeResources(fos, bos);
        }
    }

    public static void createWrapperZipFile(String fileName, VersionMigrationWrapper versionMigrationWrapper) throws FileNotFoundException, IOException, SystemException {
        File zippedFile = new File(fileName);
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zippedFile));
            // Add Model Doc
            zos.putNextEntry(new ZipEntry(versionMigrationWrapper.getModelDocName() + MODEL_DOC));
            zos.write(versionMigrationWrapper.getModelDoc());
            zos.closeEntry();
            
            // Add Model Library
            zos.putNextEntry(new ZipEntry(versionMigrationWrapper.getModelLibraryJarName() + MODEL_LIB));
            zos.write(versionMigrationWrapper.getModelLibraryJar());
            zos.closeEntry();
            
            // Add Model Definition XML
            zos.putNextEntry(new ZipEntry(versionMigrationWrapper.getModelXMLName() + MODELNAMETYPE_DELIMITER + versionMigrationWrapper.getModelDefinitionType() + MODEL_DEFINITION));
            zos.write(versionMigrationWrapper.getModelIODefinition());
            zos.closeEntry();
            
            // Add Model Definition EXCEL
            if (StringUtils.isNotBlank(versionMigrationWrapper.getModelExcelName())) {
                zos.putNextEntry(new ZipEntry(versionMigrationWrapper.getModelExcelName() + MODEL_EXCEL_DEFINITION));
                 zos.write(versionMigrationWrapper.getModelExcelDefinition());
                 zos.closeEntry();
            }
            
            // Add Version JSON
            zos.putNextEntry(new ZipEntry(VERSION_INFO));
            zos.write(ConversionUtil.convertToJsonString(versionMigrationWrapper.getVersionMigrationInfo()).getBytes());
            zos.closeEntry();
            
            // Add Migration Audit JSON
            zos.putNextEntry(new ZipEntry(VERSION_AUDIT_INFO));
            zos.write(ConversionUtil.convertToJsonString(versionMigrationWrapper.getMigrationAuditInfo()).getBytes());
            zos.closeEntry();
            
            zos.flush();
        }
        finally {
            IOUtils.closeQuietly(zos);
        }
    }
    
    public static VersionMigrationWrapper readWrapperZipFile(ZipInputStream zis) throws FileNotFoundException, IOException, SystemException {
        VersionMigrationWrapper versionMigrationWrapper = new VersionMigrationWrapper();
        try {
            ZipEntry entry = null;
            while ((entry = zis.getNextEntry()) != null) {// NOPMD
                String name = entry.getName().toLowerCase(Locale.getDefault());
                if (name.endsWith(MODEL_DOC.toLowerCase(Locale.getDefault()))) {
                    versionMigrationWrapper.setModelDocName(name.substring(0, name.lastIndexOf(MODEL_DOC.toLowerCase(Locale.getDefault()))));
                    versionMigrationWrapper.setModelDoc(IOUtils.toByteArray(zis));
                } else if (name.endsWith(MODEL_LIB.toLowerCase(Locale.getDefault()))) {
                    versionMigrationWrapper.setModelLibraryJarName(name.substring(0, name.lastIndexOf(MODEL_LIB.toLowerCase(Locale.getDefault()))));
                    versionMigrationWrapper.setModelLibraryJar(IOUtils.toByteArray(zis));
                } else if (name.endsWith(MODEL_DEFINITION.toLowerCase(Locale.getDefault()))) {
                    versionMigrationWrapper.setModelIODefinition(IOUtils.toByteArray(zis));
                    versionMigrationWrapper.setModelXMLName(name.substring(0, name.indexOf(MODELNAMETYPE_DELIMITER)));
                    versionMigrationWrapper.setModelDefinitionType(name.substring(name.indexOf(MODELNAMETYPE_DELIMITER)+2, name.indexOf(MODEL_DEFINITION.toLowerCase(Locale.getDefault()))));
                } else if (name.endsWith(MODEL_EXCEL_DEFINITION.toLowerCase(Locale.getDefault()))) {
                    versionMigrationWrapper.setModelExcelDefinition(IOUtils.toByteArray(zis));
                    versionMigrationWrapper.setModelExcelName(name.substring(0, name.lastIndexOf(MODEL_EXCEL_DEFINITION.toLowerCase(Locale.getDefault()))));
                } else if (name.equalsIgnoreCase(VERSION_INFO)) {
                    VersionMigrationInfo versionMigrationInfo = ConversionUtil.convertJson(IOUtils.toByteArray(zis), VersionMigrationInfo.class);
                    versionMigrationWrapper.setVersionMigrationInfo(versionMigrationInfo);
                } else if (name.equalsIgnoreCase(VERSION_AUDIT_INFO)) {
                    MigrationAuditInfo  migrationAuditInfo  = ConversionUtil.convertJson(IOUtils.toByteArray(zis), MigrationAuditInfo.class);
                    versionMigrationWrapper.setMigrationAuditInfo(migrationAuditInfo);
                }
            }
        } finally {
            IOUtils.closeQuietly(zis);
        }
        return versionMigrationWrapper;
    }

    public static byte[] prepareZipData(ZipInputStream zipInputStream, String zipName) throws IOException, SystemException {
        ZipUtil.createZipFile(zipInputStream, zipName);
        LOGGER.info("File created with name : {}", zipName);
        byte[] zipArray = ZipUtil.readZipFile(zipName);
        boolean fileDeleted = ZipUtil.deleteFile(zipName);
        if (fileDeleted) {
            LOGGER.info("File deleted with name : {}", zipName);
        } else {
            LOGGER.info("File could not be deleted : {}", zipName);
        }
        return zipArray;
    }

    public static byte[] prepareZipData(InputStream zipInputStream, String zipName) throws IOException, SystemException {
        ZipUtil.createZipFile(zipInputStream, zipName);
        LOGGER.info("File created with name : {}", zipName);
        byte[] zipArray = ZipUtil.readZipFile(zipName);
        boolean fileDeleted = ZipUtil.deleteFile(zipName);
        if (fileDeleted) {
            LOGGER.info("File deleted with name : {}", zipName);
        } else {
            LOGGER.info("File could not be deleted : {}", zipName);
        }
        return zipArray;
    }
    
    public static Properties getTarDescription(InputStream tarInputStream)
            throws SystemException, BusinessException {
        Properties properties = null;
        File baseDir = null;
        String basePath = null;
        InputStream is = null;
        TarArchiveInputStream tarIn = null;
        try {
            GzipCompressorInputStream gzIn = new GzipCompressorInputStream(tarInputStream);
             tarIn = new TarArchiveInputStream(gzIn);
            String tempFolder = TEMP_FOLDER + new Random().nextLong() + System.currentTimeMillis();
            baseDir = new File(tempFolder);
            baseDir.mkdirs();
            basePath = baseDir.getAbsolutePath();
            String descFile = null;
            descFile = ZipUtil.unZipSupportPackage(tarIn, basePath);
            if (descFile != null) {
                StringBuffer descriptionPath = new StringBuffer(basePath).append(File.separatorChar).append(descFile);
                File descriptionFile = new File(descriptionPath.toString());
                is = new FileInputStream(descriptionFile);
                properties = new Properties();
                properties.load(is);
            }
        } catch (IOException ie) {
            BusinessException.newBusinessException(BusinessExceptionCodes.BSE000653, new Object[] {});
        } finally {
            try {
                IOUtils.closeQuietly(is);
                if (baseDir != null) 
                	FileUtils.deleteDirectory(baseDir);
            } catch (IOException e) {
                LOGGER.error("Unable to delete : " + basePath);
            }
            if (tarIn != null) {
                IOUtils.closeQuietly(tarIn);
            }
        }

    return properties;
   }
    
      
    private static String unZipSupportPackage(TarArchiveInputStream tarInputStream, String basePath)
            throws BusinessException {
        TarArchiveEntry entry = null;
        boolean descriptionFileFound = false;
        String descriptionFile = null;
        FileOutputStream fos = null;
        BufferedOutputStream dest = null;
         try {
            while ((entry = (TarArchiveEntry) tarInputStream.getNextEntry()) != null) {
                    if (entry.getName().contains(BusinessConstants.SLASH)) {
                        String directories[] = entry.getName().split(BusinessConstants.SLASH);                  
                        int i = 0;
                        for (String dir : directories) {
                            File newDir = new File(basePath + File.separatorChar + dir);
                            if (i < (directories.length - 1)) {
                                if (!newDir.exists()) {
                                    newDir.mkdirs();
                                }
                                i++;

                            }
                        }
                }
                 
                File f = new File(basePath + File.separatorChar + entry.getName());
                if (entry.isDirectory()) {
                    f.mkdirs();
                } else {
                    int count;
                    byte data[] = new byte[BusinessConstants.TWO_KB];
                     fos = new FileOutputStream(f);
                     dest = new BufferedOutputStream(fos, BusinessConstants.TWO_KB);
                    while ((count = tarInputStream.read(data, 0, BusinessConstants.TWO_KB)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.close();
                    String directories[] = entry.getName().split(BusinessConstants.SLASH);                    
                    if (directories!=null && directories.length==2 && entry.getName().lastIndexOf(DESCRIPTION_FILE) != -1 && (entry.getName().length()-entry.getName().lastIndexOf(DESCRIPTION_FILE))==11) {
                        descriptionFileFound = true;
                        descriptionFile = entry.getName();
                        break;
                    }
                }
            }
        } catch (IOException e) {
        	LOGGER.error("error while getting pcakge from tar. Exception is", e);
            BusinessException.newBusinessException(BusinessExceptionCodes.BSE000653, new Object[] {});
        }
         finally {
        	 closeResources(fos, dest);
 		}


        if (!descriptionFileFound) {
            BusinessException.newBusinessException(BusinessExceptionCodes.BSE000654, new Object[] {});
        }

        return descriptionFile;
    }
    private static String unZipSupportPackage(ZipInputStream zipInputStream, String basePath)
            throws BusinessException {
    	ZipEntry entry = null;
        boolean descriptionFileFound = false;
        String descriptionFile = null;
        FileOutputStream fos = null;
        BufferedOutputStream dest = null;
         try {
            while ((entry = (ZipEntry) zipInputStream.getNextEntry()) != null) {
                    if (entry.getName().contains(BusinessConstants.SLASH)) {
                        String directories[] = entry.getName().split(BusinessConstants.SLASH);                  
                        int i = 0;
                        for (String dir : directories) {
                            File newDir = new File(basePath + File.separatorChar + dir);
                            if (i < (directories.length - 1)) {
                                if (!newDir.exists()) {
                                    newDir.mkdirs();
                                }
                                i++;

                            }
                        }
                }
                 
                File f = new File(basePath + File.separatorChar + entry.getName());
                if (entry.isDirectory()) {
                    f.mkdirs();
                } else {
                    int count;
                    byte data[] = new byte[BusinessConstants.TWO_KB];
                     fos = new FileOutputStream(f);
                     dest = new BufferedOutputStream(fos, BusinessConstants.TWO_KB);
                    while ((count = zipInputStream.read(data, 0, BusinessConstants.TWO_KB)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.close();
                    String directories[] = entry.getName().split(BusinessConstants.SLASH);                    
                    if (directories!=null && directories.length==2 && entry.getName().lastIndexOf(DESCRIPTION_FILE) != -1 && (entry.getName().length()-entry.getName().lastIndexOf(DESCRIPTION_FILE))==11) {
                        descriptionFileFound = true;
                        descriptionFile = entry.getName();
                        break;
                    }
                }
            }
        } catch (IOException e) {
        	LOGGER.error("error while getting pcakge from tar. Exception is", e);
            BusinessException.newBusinessException(BusinessExceptionCodes.BSE000653, new Object[] {});
        } finally {
        	closeResources(fos, dest);
		}

        if (!descriptionFileFound) {
            BusinessException.newBusinessException(BusinessExceptionCodes.BSE000654, new Object[] {});
        }

        return descriptionFile;
    }

	private static void closeResources(FileOutputStream fos, BufferedOutputStream dest) {
		IOUtils.closeQuietly(fos);
		IOUtils.closeQuietly(dest);
	}
    
    
    public static void main(String[] args) throws Exception {
		final InputStream tarInputStream =  new FileInputStream("D:\\Work\\modelet\\HubzuMVP\\extlibs\\Rcpp_0.11.3.tar.gz");
		final Properties properties = getTarDescription(tarInputStream);
		System.out.println(properties);
	}
    public static Properties getZipDescription(final InputStream zipInputStream) throws SystemException, BusinessException {
        Properties properties = null;
        String basePath = null;
        InputStream is = null;
        ZipInputStream zipIn = new ZipInputStream(zipInputStream);
        File baseDir = null;
        String tempFolder = TEMP_FOLDER + new Random().nextLong() + System.currentTimeMillis();
    	
    	  String descFile = null;
        try {
            baseDir = new File(tempFolder);
            baseDir.mkdirs();
            basePath = baseDir.getAbsolutePath();
        	descFile = ZipUtil.unZipSupportPackage(zipIn, basePath);
                    if (descFile != null) {
                        StringBuilder descriptionPath = new StringBuilder(basePath).append(File.separatorChar).append(descFile);
                        File descriptionFile = new File(descriptionPath.toString());
                        is = new FileInputStream(descriptionFile);
                        properties = new Properties();
                        properties.load(is);
                    }                	
                
              
                	
        } catch (IOException ie) {
        	LOGGER.error("Error : ", ie);
            BusinessException.newBusinessException(BusinessExceptionCodes.BSE000653, new Object[] {});
        } finally {
            try {
                IOUtils.closeQuietly(is);
                if (baseDir != null) 
                	FileUtils.deleteDirectory(baseDir);
            } catch (IOException e) {
                LOGGER.error("Unable to delete : " + basePath);
                LOGGER.error("Exception : ",e);
            }
            if (zipIn != null) {
                IOUtils.closeQuietly(zipIn);
            }
        }

  

    return properties;
   }
    
    
    private static File file(final File root, final ZipEntry entry) throws IOException {

    	    final File file = new File(root, entry.getName());

    	    File parent = file;
    	    if (!entry.isDirectory()) {
    	        final String name = entry.getName();
    	        final int index = name.lastIndexOf('/');
    	        if (index != -1) {
    	            parent = new File(root, name.substring(0, index));
    	        }
    	    }
    	    if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
    	        throw new IOException(
    	            "failed to create a directory: " + parent.getPath());
    	    }

    	    return file;
    	}
}
