/**
 * 
 */
package com.ca.umg.rt.util.container.task;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.rmodel.dao.RModelDAO;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.framework.core.task.AbstractCallableTask;

/**
 * @author kamathan
 *
 */
public class LoadVersionToEnvironmentMappingTask extends
        AbstractCallableTask<Map<String, Map<String, Map<String, VersionExecInfo>>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadSupportPackagesTask.class);

    private final RModelDAO rModelDAO;

    public LoadVersionToEnvironmentMappingTask(String tenantCode, RModelDAO rModelDAO) {
        super(tenantCode);
        this.rModelDAO = rModelDAO;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.framework.core.task.AbstractCallableTask#call()
     */
    @Override
    public Map<String, Map<String, Map<String, VersionExecInfo>>> call()
            throws SystemException {
        LOGGER.info("Initiated loading of version execution environment details for tenant {}.", getTenantCode());
        Map<String, Map<String, Map<String, VersionExecInfo>>> tenantVersionEnvMap = new HashMap<String, Map<String, Map<String, VersionExecInfo>>>();

        try {
            setRequestContext();            
            tenantVersionEnvMap.put(getTenantCode(),  rModelDAO
                    .getAllVersionEnvironmentMap(getTenantCode()));
           
        } finally {
            destroyRequestContext();
        }
        return tenantVersionEnvMap;
    }

}
