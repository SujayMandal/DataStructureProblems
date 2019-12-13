package com.ca.pool.model;

import static com.google.common.base.Objects.toStringHelper;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.ca.framework.core.constants.PoolConstants;

@SuppressWarnings("PMD")
public class Pool implements Serializable, Comparable<Pool> {

    private static final long serialVersionUID = -6077975089919373012L;

    private String id;
    private String poolName;
    private String poolDesc;
    private String poolStatus;
    private int defaultPool;
    private String executionLanguage;
    private Integer modeletCount;
    private Integer inactiveModeletCount;
    private String modeletCapacity;
    private Integer priority;
    private Integer waitTimeout;
    private Integer modeletAdded;
    private Integer modeletRemoved;
    private Integer oldWaitTimeout;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPoolStatus() {
        return poolStatus;
    }

    public void setPoolStatus(String poolStatus) {
        this.poolStatus = poolStatus;
    }

    public int getDefaultPool() {
        return defaultPool;
    }

    public void setDefaultPool(int defaultPool) {
        this.defaultPool = defaultPool;
    }

    public String getExecutionLanguage() {
        return executionLanguage;
    }

    public Integer getInactiveModeletCount() {
        return inactiveModeletCount;
    }

    public void setInactiveModeletCount(Integer inactiveModeletCount) {
        this.inactiveModeletCount = inactiveModeletCount;
    }

    public void setExecutionLanguage(String executionLanguage) {
        this.executionLanguage = executionLanguage;
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

    public Integer getModeletAdded() {
        return modeletAdded;
    }

    public void setModeletAdded(Integer modeletAdded) {
        this.modeletAdded = modeletAdded;
    }

    public Integer getModeletRemoved() {
        return modeletRemoved;
    }

    public void setModeletRemoved(Integer modeletRemoved) {
        this.modeletRemoved = modeletRemoved;
    }

    public Integer getOldWaitTimeout() {
        return oldWaitTimeout;
    }

    public void setOldWaitTimeout(Integer oldWaitTimeout) {
        this.oldWaitTimeout = oldWaitTimeout;
    }

    @Override
    public boolean equals(final Object that) {
        if (that instanceof Pool) {
            final Pool thatPool = (Pool) that;
            return this.getPoolName().equals(thatPool.getPoolName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getPoolName().hashCode();
    }

    @Override
    public int compareTo(Pool that) {
        if (StringUtils.equalsIgnoreCase(this.getExecutionEnvironment(), that.getExecutionEnvironment())
                && this.getExecutionLanguage().equalsIgnoreCase(that.getExecutionLanguage())) {
            return this.getPriority().compareTo(that.getPriority());
        } else if (StringUtils.equalsIgnoreCase(this.getExecutionEnvironment(), that.getExecutionEnvironment())) {
            return this.getExecutionLanguage().compareTo(that.getExecutionLanguage());
        } else {
            return this.getExecutionEnvironment().compareTo(that.getExecutionEnvironment());
        }
    }

    @Override
    public String toString() {
        return toStringHelper(this.getClass()).add("Pool Id:", id).add("Pool Name:", poolName).add("Pool Status:", poolStatus)
                .add("Default Pool:", defaultPool).add("Environment:", executionLanguage).add("Modelet Count:", modeletCount)
                .add("Modelet Capacity:", modeletCapacity).add("Pool Desc:", poolDesc).add("Priority:", priority)
                .add("Wait Timeout:", waitTimeout).toString();
    }

    public static Pool getSystemDefaultPool() {
        final Pool defaultPool = new Pool();
        defaultPool.setPoolName(PoolConstants.DEFAULT_POOL);
        defaultPool.setPriority(-1);
        defaultPool.setModeletCount(0);
        defaultPool.setExecutionLanguage("");
        return defaultPool;
    }

    public Integer getWaitTimeout() {
        return waitTimeout;
    }

    public void setWaitTimeout(Integer waitTimeout) {
        this.waitTimeout = waitTimeout;
    }

    public String getExecutionEnvironment() {
        return executionEnvironment;
    }

    public void setExecutionEnvironment(String executionEnvironment) {
        this.executionEnvironment = executionEnvironment;
    }

}