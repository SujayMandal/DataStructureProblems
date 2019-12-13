/**
 * 
 */
package com.ca.umg.business.model.bo;

import java.util.List;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.model.info.ModelArtifact;

/**
 * @author kamathan
 *
 */
public interface ModelArtifactBO {

	/**
	 * Saves the given model jar to the system defined location.
	 * 
	 * @param modelArtifact
	 * @throws SystemException
	 * @throws BusinessException
	 */
	void storeModelJar(ModelArtifact modelArtifact) throws SystemException, BusinessException;

	/**
	 * Saves the given artifacts to the system defined location.
	 * 
	 * @param artifacts
	 * @throws SystemException
	 * @throws BusinessException
	 */
	void storeArtifacts(ModelArtifact[] artifacts) throws SystemException, BusinessException;

	/**
	 * @param modelArtifact
	 * @param isLibrary
	 * @throws SystemException
	 * @throws BusinessException
	 */
	void deleteModelArtifact(ModelArtifact modelArtifact, boolean isLibrary) throws SystemException, BusinessException;

	/**
	 * Returns the artifacts from the system defined location for the given model.
	 * 
	 * @param modelUmgName
	 * @param modleGroup
	 * @param isLibrary
	 * 
	 * @return
	 * @throws SystemException
	 * @throws BusinessException
	 */
	List<ModelArtifact> fetchArtifacts(String modelName, String modelUmgName, boolean isLibrary)
			throws SystemException, BusinessException;

	void storeSupportPackage(ModelArtifact modelArtifact, String environment, String envrionmentVersion,
			String executionEnvironment) throws SystemException, BusinessException;

	String getLargeFilesPackage() throws SystemException, BusinessException;

	/**
	 * Saves the given model definitional file to the system defined location.
	 * 
	 * @param artifacts
	 * @throws SystemException
	 * @throws BusinessException
	 */
	void storeModelDefArtifacts(ModelArtifact[] artifacts) throws SystemException, BusinessException;
}
