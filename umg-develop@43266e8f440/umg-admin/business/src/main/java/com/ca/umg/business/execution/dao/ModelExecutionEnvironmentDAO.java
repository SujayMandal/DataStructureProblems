/**
 * 
 */
package com.ca.umg.business.execution.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ca.umg.business.execution.entity.ModelExecutionEnvironment;

/**
 * @author kamathan
 *
 */
public interface ModelExecutionEnvironmentDAO extends JpaRepository<ModelExecutionEnvironment, String> {

	ModelExecutionEnvironment findByExecutionEnvironmentAndEnvironmentVersion(String executionEnvironment, String environmentVersion);
	
	/**
	 * This method used to get all the R environments
	 * @param executionEnvironment
	 * @return
	 */
	List<ModelExecutionEnvironment> findByExecutionEnvironment(String executionEnvironment);

    ModelExecutionEnvironment findById(String id);

    ModelExecutionEnvironment findByName(String name);
    
    List<ModelExecutionEnvironment> findByExecutionEnvironmentAndActive(String executionEnvironment, char isActive);

	List<ModelExecutionEnvironment> findByActive(char isActive);
}




