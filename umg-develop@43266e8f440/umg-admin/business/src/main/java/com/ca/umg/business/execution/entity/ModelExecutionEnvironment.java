/**
 * 
 */
package com.ca.umg.business.execution.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractAuditable;

/**
 * @author kamathan
 *
 */
@Entity
@Table(name = "MODEL_EXECUTION_ENVIRONMENTS")
public class ModelExecutionEnvironment extends AbstractAuditable {

	private static final long serialVersionUID = 5210753812180255776L;


    @NotNull(message = "Execution environment cannot be null. ")
	@NotBlank(message = "Execution environment cannot be blank.")
	@Column(name = "EXECUTION_ENVIRONMENT")
	@Property
	private String executionEnvironment;

	@NotNull(message = "Environment version cannot be null.")
	@NotBlank(message = "Environment version cannot be blank.")
	@Column(name = "ENVIRONMENT_VERSION")
	@Property
	private String environmentVersion;

    @NotNull(message = "Name cannot be null.")
    @NotBlank(message = "Name cannot be blank.")
    @Column(name = "NAME")
    @Property
    private String name;
    
    @NotNull(message = "active cannot be null.")
    @NotBlank(message = "active cannot be blank.")
    @Column(name = "IS_ACTIVE")
    @Property
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
