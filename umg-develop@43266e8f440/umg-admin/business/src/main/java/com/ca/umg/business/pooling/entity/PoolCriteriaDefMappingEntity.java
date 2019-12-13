package com.ca.umg.business.pooling.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractPersistable;

@Entity
@Table(name = "POOL_CRITERIA_DEF_MAPPING")
public class PoolCriteriaDefMappingEntity extends AbstractPersistable {

    private static final long serialVersionUID = -789196778281352042L;

    
    @NotNull(message = "Pool Id cannot be null")
    @NotBlank(message = "Pool Id cannot be blank")
    @Column(name = "POOL_ID")
    @Property
	private String poolId;

    
    @NotNull(message = "Pool Criteria Value cannot be null")
    @NotBlank(message = "Pool Criteria Value cannot be blank")
    @Column(name = "POOL_CRITERIA_VALUE")
    @Property
    private String poolCriteriaValue;
	
	
	public String getPoolId() {
		return poolId;
	}
	
	public void setPoolId(String poolId) {
		this.poolId = poolId;
	}
	
	public String getPoolCriteriaValue() {
		return poolCriteriaValue;
	}
	
	public void setPoolCriteriaValue(String poolCriteriaValue) {
		this.poolCriteriaValue = poolCriteriaValue;
	}
}