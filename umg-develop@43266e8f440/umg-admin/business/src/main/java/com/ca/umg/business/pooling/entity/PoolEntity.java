package com.ca.umg.business.pooling.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.pojomatic.annotations.Property;

import com.ca.framework.core.db.domain.AbstractPersistable;

@Entity
@Table(name = "POOL")
@SuppressWarnings("PMD")
public class PoolEntity extends AbstractPersistable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "Pool Name cannot be null")
    @NotBlank(message = "Pool Name cannot be blank")
    @Column(name = "POOL_NAME")
    @Property
    private String poolName;

    @Column(name = "POOL_DESCRIPTION")
    @Property
    private String poolDesc;

    @Column(name = "POOL_STATUS")
    @Property
    private String poolStatus;

    // @NotNull(message = "Default Pool cannot be null")
    // @NotBlank(message = "Default Pool cannot be blank")
    @Column(name = "IS_DEFAULT_POOL")
    @Property
    private int defaultPool;

    @NotNull(message = "Environment cannot be null")
    @NotBlank(message = "Environment cannot be blank")
    @Column(name = "EXECUTION_LANGUAGE")
    @Property
    private String executionLanguage;

    // @NotNull(message = "Modelet Count cannot be null")
    // @NotBlank(message = "Modelet Count cannot be blank")
    @Column(name = "MODELET_COUNT")
    @Property
    private Integer modeletCount;

    @NotNull(message = "Modelet Capacity cannot be null")
    @NotBlank(message = "Modelet Capacity cannot be blank")
    @Column(name = "MODELET_CAPACITY")
    @Property
    private String modeletCapacity;

    // @NotNull(message = "Pool Priority cannot be null")
    // @NotBlank(message = "Pool Priority cannot be blank")
    @Column(name = "PRIORITY")
    @Property
    private Integer priority;

    @Column(name = "WAIT_TIMEOUT")
    @Property
    private Integer waitTimeout;

    @Column(name = "EXECUTION_ENVIRONMENT")
    @Property
    private String executionEnvironment;

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public String getPoolDesc() {
        return poolDesc;
    }

    public void setPoolDesc(String poolDesc) {
        this.poolDesc = poolDesc;
    }

    public String getPoolStatus() {
        return poolStatus;
    }

    public void setPoolStatus(String poolStatus) {
        this.poolStatus = poolStatus;
    }

    public int isDefaultPool() {
        return defaultPool;
    }

    public void setDefaultPool(int defaultPool) {
        this.defaultPool = defaultPool;
    }

    public String getExecutionLanguage() {
        return executionLanguage;
    }

    public void setExecutionLanguage(String environment) {
        this.executionLanguage = environment;
    }

    public Integer getModeletCount() {
        return modeletCount;
    }

    public void setModeletCount(Integer modeletCount) {
        this.modeletCount = modeletCount;
    }

    public String getModeletCapacity() {
        return modeletCapacity;
    }

    public void setModeletCapacity(String modeletCapacity) {
        this.modeletCapacity = modeletCapacity;
    }

    public void setPriority(final Integer priority) {
        this.priority = priority;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setWaitTimeout(final Integer waitTimeout) {
        this.waitTimeout = waitTimeout;
    }

    public Integer getWaitTimeout() {
        return waitTimeout;
    }

    public String getExecutionEnvironment() {
        return executionEnvironment;
    }

    public void setExecutionEnvironment(String executionEnvironment) {
        this.executionEnvironment = executionEnvironment;
    }
}