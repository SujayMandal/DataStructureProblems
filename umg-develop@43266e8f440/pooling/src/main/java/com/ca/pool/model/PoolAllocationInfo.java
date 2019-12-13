/**
 * 
 */
package com.ca.pool.model;

import java.io.Serializable;
import java.util.List;



import com.ca.modelet.ModeletClientInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author kamathan
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PoolAllocationInfo implements Serializable {

    private static final long serialVersionUID = -3196237555111839884L;

    private Pool pool;

    private List<ModeletClientInfo> modeletClientInfoList;

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public List<ModeletClientInfo> getModeletClientInfoList() {
        return modeletClientInfoList;
    }

    public void setModeletClientInfoList(List<ModeletClientInfo> modeletClientInfoList) {
        this.modeletClientInfoList = modeletClientInfoList;
    }
}
