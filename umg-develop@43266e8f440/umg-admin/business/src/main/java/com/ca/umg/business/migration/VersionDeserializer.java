package com.ca.umg.business.migration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;

import org.apache.commons.io.IOUtils;

import com.ca.framework.core.util.ConversionUtil;
import com.ca.umg.business.migration.info.VersionMigrationWrapper;

@SuppressWarnings("PMD")
public class VersionDeserializer {

	public static final String DEFLATED_PATH = ".\\DeflatedFiles";

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			throw new IllegalArgumentException("Insufficient Number of Arguments");
		} else {
			getVersionContents(args);
		}
	}

	public static void getVersionContents(String[] args) throws Exception {
		String fileName = args[0];
		FileInputStream fis = null;
		ObjectInputStream objectInputStream = null;
		try {
			File serializedFile = new File(fileName);
			fis = new FileInputStream(serializedFile);
			objectInputStream = new ObjectInputStream(fis);
			VersionMigrationWrapper migrationWrapper = (VersionMigrationWrapper) objectInputStream.readObject();
			new File(DEFLATED_PATH).mkdir();
			generateFiles(migrationWrapper);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeResources(fis, objectInputStream);
		}
	}

	private static void closeResources(FileInputStream fis, ObjectInputStream objectInputStream) {
		IOUtils.closeQuietly(fis);
		IOUtils.closeQuietly(objectInputStream);
	}

	private static void generateFiles(VersionMigrationWrapper migrationWrapper) throws Exception {
		writeToFile(migrationWrapper.getModelDocName(), migrationWrapper.getModelDoc());
		writeToFile(migrationWrapper.getModelLibraryJarName(), migrationWrapper.getModelLibraryJar());
		writeToFile(migrationWrapper.getModelXMLName(), migrationWrapper.getModelIODefinition());
		writeToFile(
				migrationWrapper.getVersionMigrationInfo().getModelName() + "_"
						+ migrationWrapper.getVersionMigrationInfo().getModelLibraryName() + ".json",
				ConversionUtil.convertToJsonString(migrationWrapper.getVersionMigrationInfo()).getBytes());
	}

	private static void writeToFile(String fileName, byte[] content) throws Exception {
		if (fileName == null || fileName.isEmpty()) {
			throw new Exception("File is corrupted");
		}
		FileOutputStream fos = null;
		try {
		File newFile = new File(DEFLATED_PATH + "/" + fileName);
		fos = new FileOutputStream(newFile);
		fos.write(content);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(fos != null) {
				fos.flush();
				fos.close();
			}
		}
	}

}
