/**
 * 
 */
package com.ca.umg.rt.util.container.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.rmodel.dao.RModelDAO;
import com.ca.framework.core.task.AbstractCallableTask;

/**
 * @author kamathan
 *
 */
public class LoadPackageNamesTask extends AbstractCallableTask<Map<String, Map<String, String>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadSupportPackagesTask.class);

    private final RModelDAO rModelDAO;

    public LoadPackageNamesTask(String tenantCode, RModelDAO rModelDAO) {
        super(tenantCode);
        this.rModelDAO = rModelDAO;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.framework.core.task.AbstractCallableTask#call()
     */
    @Override
    public Map<String, Map<String, String>> call() throws SystemException {
        Map<String, Map<String, String>> packageNames = new HashMap<String, Map<String, String>>();
        try {
            LOGGER.info("Initiated loading the model package names for tenant {}", getTenantCode());
            setRequestContext();
            Map<String, String> packages = rModelDAO.getAllModelPackageNames(getTenantCode());
            if (MapUtils.isNotEmpty(packages)) {
                packageNames.put(getTenantCode(), packages);
            }
        } finally {
            destroyRequestContext();
        }
        return packageNames;
    }

}
