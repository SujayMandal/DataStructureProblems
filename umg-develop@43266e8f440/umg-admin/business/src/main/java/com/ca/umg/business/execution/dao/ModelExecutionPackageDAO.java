/**
 * 
 */
package com.ca.umg.business.execution.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.ca.umg.business.execution.entity.ModelExecutionPackage;

/**
 * @author kamathan
 *
 */
@SuppressWarnings("PMD")
public interface ModelExecutionPackageDAO extends JpaRepository<ModelExecutionPackage, String>,
        JpaSpecificationExecutor<ModelExecutionPackage> {

	List<ModelExecutionPackage> findByPackageType(String packagetype);
	
	ModelExecutionPackage findByPackageFolderAndPackageVersionAndExecEnvAndModelExecEnvName(String packageFolder, String packageVersion,String execEnvString,String modelExecEnvName);

	ModelExecutionPackage findByModelExecEnvNameAndPackageFolderAndPackageVersionAndExecEnv(String modelExecEnvName, String packageFolder,
            String packageVersion,String execEnv);

    List<ModelExecutionPackage> findByModelExecEnvNameAndPackageFolderAndPackageType(String modelExecEnvName,
            String packageFolder, String packageType);

    @Query("SELECT DISTINCT modelExePack.packageFolder FROM #{#entityName} modelExePack where modelExePack.modelExecEnvName = ?1 AND modelExePack.packageType=?2")
    List<String> findAllPackageFoldersByModelExecEnvName(String modelExecEnvName,
            String addonAPckageType);
    
    ModelExecutionPackage findById(String id);
    
    List<ModelExecutionPackage> findByPackageFolderAndPackageType(String packageFolder, String packageType);
    
    ModelExecutionPackage findByPackageNameAndModelExecEnvName(String packageName,String modelExecEnvName);

}
