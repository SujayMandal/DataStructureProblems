package com.ca.umg.business.pooling.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.pojomatic.annotations.PojomaticPolicy;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractAuditable;

@Entity
@Table(name = "MODELET_RESTART_CONFIG")
@Audited
public class ModeletRestartConfig extends AbstractAuditable {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	/**
	 * holds modelName
	 */
	@Column(name = "MODELNAME_VERSION")
	@Property
	private String modelNameAndVersion;
	/**
	 * holds modelName
	 */
	@Column(name = "RESTART_COUNT")
	@Property
	private int restartCount;
	
	@Column(name = "TENANT_ID", nullable = true, insertable = true, updatable = true, length = 36)
    @Property(policy = PojomaticPolicy.TO_STRING)
    private String tenantId;

	public int getRestartCount() {
		return restartCount;
	}

	public void setRestartCount(int restartCount) {
		this.restartCount = restartCount;
	}
	

	public String getModelNameAndVersion() {
		return modelNameAndVersion;
	}

	public void setModelNameAndVersion(String modelNameAndVersion) {
		this.modelNameAndVersion = modelNameAndVersion;
	}
	
	 public String getTenantId() {
	        return tenantId;
	 }
	
	 public void setTenantId(String tenantId) {
	       this.tenantId = tenantId;
	 }

}
