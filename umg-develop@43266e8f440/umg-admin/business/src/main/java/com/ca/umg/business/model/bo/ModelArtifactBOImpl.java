package com.ca.umg.business.model.bo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.model.info.ModelArtifact;
import com.ca.umg.business.util.AdminUtil;

@Named
public class ModelArtifactBOImpl implements ModelArtifactBO {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModelArtifactBOImpl.class);

	@Inject
	private UmgFileProxy umgFileProxy;

	@Inject
	private SystemParameterProvider systemParameterProvider;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ca.umg.business.model.bo.ModelArtifactBO#storeModelJar(com.ca.umg
	 * .business.model.info.ModelArtifact)
	 */
	@Override
	public void storeModelJar(ModelArtifact modelArtifact) throws SystemException, BusinessException {
		LOGGER.info("Saving jar for model group {}.", modelArtifact.getModelName());
		File file = createFile(modelArtifact, BusinessConstants.MODEL_LIBRARY_PARENT_FOLDER);
		writeFileToDirectory(file, modelArtifact);
	}

	/**
	 * Writes the given {@link ModelArtifact} to the system defined san location.
	 * 
	 * @param file
	 * @param modelArtifact
	 * @throws SystemException
	 */
	private void writeFileToDirectory(File file, ModelArtifact modelArtifact) throws SystemException {
		OutputStream outputStream = null;
		// create directory for model if it does not exist
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			LOGGER.info("Saving artifact {} to directory {}.", modelArtifact.getName(), file.getPath());
			outputStream = new FileOutputStream(new File(file, modelArtifact.getName()));
			outputStream.write(modelArtifact.getDataArray());
			outputStream.flush();
			outputStream.close();
			LOGGER.info("Saved model artifact {} successfully.", modelArtifact.getName());
		} catch (IOException e) {
			throw new SystemException(BusinessExceptionCodes.BSE000005, new Object[] {}, e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					LOGGER.error("Exception occured closing Output Stream", e);
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ca.umg.business.model.bo.ModelArtifactBO#storeArtifacts(com.ca.umg
	 * .business.model.info.ModelArtifact[])
	 */
	@Override
	public void storeArtifacts(ModelArtifact[] artifacts) throws SystemException, BusinessException {
		LOGGER.info("Received {} model artifacts for saving.", artifacts != null ? artifacts.length : 0);
		// prepare model directory path
		String modelDirPath = AdminUtil.getSanBasePath(
				umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE)));

		StringBuffer filePathBfr = null;
		File file = null;

		for (ModelArtifact artifact : artifacts) {
			filePathBfr = new StringBuffer(modelDirPath);
			filePathBfr.append(File.separatorChar).append(BusinessConstants.MODEL_PARENT_FOLDER)
					.append(File.separatorChar).append(artifact.getModelName()).append(File.separatorChar)
					.append(artifact.getUmgName());
			file = new File(filePathBfr.toString());
			writeFileToDirectory(file, artifact);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ca.umg.business.model.bo.ModelArtifactBO#fetchArtifacts(java.lang
	 * .String, int, java.lang.String)
	 */
	@Override
	public List<ModelArtifact> fetchArtifacts(String modelName, String modelUmgName, boolean isLibrary)
			throws SystemException, BusinessException {
		LOGGER.info("Fetching artifacts for model {} {}.", modelName, modelUmgName);
		List<ModelArtifact> modelArtifacts = null;
		ModelArtifact modelArtifact = null;
		String modelBaseDir = AdminUtil.getSanBasePath(
				umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE)));
		StringBuffer finalPath = new StringBuffer(modelBaseDir).append(File.separatorChar);

		if (isLibrary) {
			finalPath.append(BusinessConstants.MODEL_LIBRARY_PARENT_FOLDER);
		} else {
			finalPath.append(BusinessConstants.MODEL_PARENT_FOLDER);
		}

		finalPath.append(File.separatorChar).append(modelName).append(File.separatorChar).append(modelUmgName);
		String[] modelArtifactNames = null;
		LOGGER.info("Looking for model artifact in directory {}.", finalPath.toString());

		File file = new File(finalPath.toString());
		if (file.exists() && file.isDirectory()) {
			modelArtifacts = new ArrayList<ModelArtifact>();
			// fetch all the child file or directory names
			modelArtifactNames = file.list();
			for (String modelArtifactName : modelArtifactNames) {
				File fileObj = new File(file, modelArtifactName);
				if (fileObj.isFile()) {
					modelArtifact = buildModelArtifacts(modelName, modelUmgName, new File(file, modelArtifactName));
					modelArtifact.setName(modelArtifactName);
					modelArtifact.setAbsolutePath(finalPath.toString() + File.separator);
					if (modelArtifact != null) {
						modelArtifacts.add(modelArtifact);
					}
				}
			}

		}
		LOGGER.info("Found {} artifacts for model {} {}.", modelArtifacts == null ? 0 : modelArtifacts.size(),
				modelName, modelUmgName);
		return modelArtifacts;
	}

	private ModelArtifact buildModelArtifacts(String modelName, String umgName, File modelPath) throws SystemException {
		InputStream inputStream = null;
		ModelArtifact modelArtifact = null;
		try {
			// if directory exists in the path ignore it, we are
			// interested only in files present in the directory
			if (!modelPath.isDirectory()) {
				inputStream = new FileInputStream(modelPath);
				modelArtifact = new ModelArtifact();
				modelArtifact.setModelName(modelName);
				modelArtifact.setUmgName(umgName);
				if (inputStream != null) {
					modelArtifact.setDataArray(AdminUtil.convertStreamToByteArray(inputStream));
				}
				inputStream.close();
			}
		} catch (FileNotFoundException exp) {
			SystemException.newSystemException(BusinessExceptionCodes.BSE000010,
					new Object[] { String.format("File %s not found.", modelPath.getName()) });
		} catch (IOException exp) {
			SystemException.newSystemException(BusinessExceptionCodes.BSE000010,
					new Object[] { "An error occurred while reading file %s.", modelPath.getName() });
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					LOGGER.error("Exception occured while closing Input Stream", e);
				}
			}
		}
		return modelArtifact;
	}

	@Override
	public void deleteModelArtifact(ModelArtifact modelArtifact, boolean isLibrary)
			throws SystemException, BusinessException {
		LOGGER.info("Deleting artifact {}.", modelArtifact.getName());
		StringBuffer modelArtifactPath = new StringBuffer(AdminUtil.getSanBasePath(
				umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE))))
						.append(File.separatorChar);
		if (isLibrary) {
			modelArtifactPath.append(BusinessConstants.MODEL_LIBRARY_PARENT_FOLDER);
		} else {
			modelArtifactPath.append(BusinessConstants.MODEL_PARENT_FOLDER);
		}

		String modelRootDirectory = modelArtifactPath.append(File.separatorChar).append(modelArtifact.getModelName())
				.toString();

		modelArtifactPath.append(File.separatorChar).append(modelArtifact.getUmgName());

		File file = new File(modelArtifactPath.toString());
		if (file.exists()) {
			deleteArtifacts(file);
			// delete root directory of the model if there are no subdirectory
			// exists
			deleteModelRoot(modelRootDirectory);
			LOGGER.info("Deleted artifact {} successfully.", modelArtifact.getName());
		}

	}

	private void deleteModelRoot(String modelRootFirectory) throws SystemException {
		File file = new File(modelRootFirectory);
		if (file.exists() && file.list().length == BusinessConstants.NUMBER_ZERO) {
			delete(file);
		}
	}

	/**
	 * 
	 * @param file
	 * @throws SystemException
	 */
	private void deleteArtifacts(File file) throws SystemException {
		if (file.isDirectory()) {
			if (file.list().length == BusinessConstants.NUMBER_ZERO) {
				delete(file);
			} else {
				String files[] = file.list();
				for (String childFile : files) {
					deleteArtifacts(new File(file, childFile));
				}
				if (file.list().length == BusinessConstants.NUMBER_ZERO) {
					delete(file);
				}
			}
		} else {
			delete(file);
		}
	}

	private void delete(File file) throws SystemException {
		if (file.delete()) {
			LOGGER.info("Deleted artifact {} successfully.", file.getName());
		} else {
			SystemException.newSystemException(BusinessExceptionCodes.BSE000022,
					new Object[] { file.getName(), "Could not delete file" });
		}
	}

	@Override
	public void storeSupportPackage(ModelArtifact modelArtifact, String environment, String envrionmentVersion,
			String executionEnvironment) throws SystemException, BusinessException {
		LOGGER.info("Saving support package {}.", modelArtifact.getModelName());
		File file = createFile(modelArtifact, BusinessConstants.SUPPORT_PACKAGE_PARENT_FOLDER, environment,
				envrionmentVersion, executionEnvironment);
		writeFileToDirectory(file, modelArtifact);
	}

	@Override
	public String getLargeFilesPackage() throws SystemException, BusinessException {
		LOGGER.info("Fetching files from package {}.");
		StringBuffer pendingfilesPath = new StringBuffer(
				umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE)));
		pendingfilesPath.append(File.separatorChar)
				.append(systemParameterProvider.getParameter(BusinessConstants.LARGE_FILE_FOLDER));
		return pendingfilesPath.toString();

	}

	private File createFile(ModelArtifact modelArtifact, String parentFolder) throws SystemException {
		StringBuffer filePathBfr = new StringBuffer(AdminUtil.getSanBasePath(
				umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE))));
		filePathBfr.append(File.separatorChar).append(parentFolder).append(File.separatorChar)
				.append(modelArtifact.getModelName()).append(File.separatorChar).append(modelArtifact.getUmgName());
		return new File(filePathBfr.toString());
	}

	@SuppressWarnings("PMD.UseObjectForClearerAPI")
	private File createFile(ModelArtifact modelArtifact, String parentFolder, String environment,
			String envrionmentVersion, String executionEnvironment) throws SystemException {
		StringBuffer filePathBfr = new StringBuffer(
				umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE)));
		filePathBfr.append(File.separatorChar).append(parentFolder).append(File.separatorChar).append(environment)
				.append(File.separatorChar).append(envrionmentVersion).append(File.separatorChar)
				.append(executionEnvironment).append(File.separatorChar).append(modelArtifact.getModelName());
		return new File(filePathBfr.toString());
	}

	@Override
	public void storeModelDefArtifacts(ModelArtifact[] artifacts) throws SystemException, BusinessException {

		LOGGER.info("Received {} model artifacts for saving.", artifacts != null ? artifacts.length : 0);
		// prepare model directory path
		String modelDirPath = AdminUtil.getSanBasePath(
				umgFileProxy.getSanPath(systemParameterProvider.getParameter(SystemConstants.SAN_BASE)));

		StringBuffer filePathBfr = null;
		File file = null;
		for (ModelArtifact artifact : artifacts) {
			filePathBfr = new StringBuffer(modelDirPath);
			filePathBfr.append(File.separatorChar).append(BusinessConstants.MODEL_PARENT_FOLDER)
					.append(File.separatorChar).append(artifact.getModelName()).append(File.separatorChar)
					.append(artifact.getUmgName()).append(File.separatorChar).append(BusinessConstants.IO_DEFN);
			file = new File(filePathBfr.toString());
			writeFileToDirectory(file, artifact);
		}

	}

}