package com.ca.umg.business.modelexecenvs;

import com.ca.framework.core.exception.SystemException;

import java.util.List;
import java.util.Map;

/**
 * @author basanaga
 * 
 * This class used to keep the model execution environments in hazelcast cache
 *
 */
public interface ModelExecEnvironmentProvider {

	List<String> getAllExecutionEnvironmentNames() throws SystemException;
	List<String> getNamesByEnvironment(String environment); 
    Map<String, List<String>> getExecutionEnvironmentMap();
}
