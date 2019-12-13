package com.ca.umg.business.pooling.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractPersistable;

@Entity
@Table(name = "POOL_USAGE_ORDER")
public class PoolUsageOrderMappingEntity extends AbstractPersistable {

    private static final long serialVersionUID = 6512657658736110721L;

    
    @NotNull(message = "Pool Id cannot be null")
    @NotBlank(message = "Pool Id cannot be blank")
    @Column(name = "POOL_ID")
    @Property
    private String poolId;
    
    
    @NotNull(message = "Pool Usage Id cannot be null")
    @NotBlank(message = "Pool Usage Id cannot be blank")
    @Column(name = "POOL_USAGE_ID")
    @Property
	private String poolUsageId;
    
    
//    @NotNull(message = "Pool Usage Try Order cannot be null")
//    @NotBlank(message = "Pool Usage Try Order cannot be blank")
    @Column(name = "POOL_TRY_ORDER")
    @Property
    private Integer poolTryOrder;
	
	
	public String getPoolId() {
		return poolId;
	}
	
	public void setPoolId(String poolId) {
		this.poolId = poolId;
	}
	
	public String getPoolUsageId() {
		return poolUsageId;
	}
	
	public void setPoolUsageId(String poolUsageId) {
		this.poolUsageId = poolUsageId;
	}
	
	public Integer getPoolTryOrder() {
		return poolTryOrder;
	}
	
	public void setPoolTryOrder(Integer poolTryOrder) {
		this.poolTryOrder = poolTryOrder;
	}
}