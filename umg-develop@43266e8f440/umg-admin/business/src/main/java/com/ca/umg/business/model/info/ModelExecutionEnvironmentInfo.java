/**
 * 
 */
package com.ca.umg.business.model.info;

import com.ca.framework.core.info.BaseInfo;

/**
 * @author nigampra
 *
 */
public class ModelExecutionEnvironmentInfo extends BaseInfo {

	private String executionEnvironment;
	
	private String environmentVersion;
	
	private String name;
	
	private char active;
	
	public String getExecutionEnvironment() {
		return executionEnvironment;
	}

	public void setExecutionEnvironment(String executionEnvironment) {
		this.executionEnvironment = executionEnvironment;
	}

	public String getEnvironmentVersion() {
		return environmentVersion;
	}

	public void setEnvironmentVersion(String environmentVersion) {
		this.environmentVersion = environmentVersion;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public char getActive() {
		return active;
	}

	public void setActive(char active) {
		this.active = active;
	}

   
}
