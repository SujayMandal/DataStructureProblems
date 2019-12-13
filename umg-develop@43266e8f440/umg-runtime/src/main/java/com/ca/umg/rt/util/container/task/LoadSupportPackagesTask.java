/**
 * 
 */
package com.ca.umg.rt.util.container.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.rmodel.dao.RModelDAO;
import com.ca.framework.core.rmodel.info.SupportPackage;
import com.ca.framework.core.task.AbstractCallableTask;
import com.ca.umg.rt.core.deployment.constants.RuntimeConstants;

/**
 * @author kamathan
 *
 */
public class LoadSupportPackagesTask extends AbstractCallableTask<Map<String, Map<String, List<SupportPackage>>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadSupportPackagesTask.class);

    private final RModelDAO rModelDAO;

    public LoadSupportPackagesTask(String tenantCode, RModelDAO rModelDAO) {
        super(tenantCode);
        this.rModelDAO = rModelDAO;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Map<String, Map<String, List<SupportPackage>>> call() throws SystemException {
        LOGGER.info("Initiated loading of support packages for tenant {}.", getTenantCode());
        Map<String, List<SupportPackage>> versionSupportPkgMap = new HashMap<String, List<SupportPackage>>();
        Map<String, Map<String, List<SupportPackage>>> tenantSupportPkgMap = new HashMap<String, Map<String, List<SupportPackage>>>();
        setRequestContext();
        try {
            List<SupportPackage> supportPackages = rModelDAO.getAllSupportPackages(getTenantCode());
            if (CollectionUtils.isNotEmpty(supportPackages)) {
                for (SupportPackage supportPackage : supportPackages) {
                    String key = StringUtils.join(supportPackage.getVersionName(), RuntimeConstants.CHAR_HYPHEN,
                            supportPackage.getMajorVersion(), RuntimeConstants.CHAR_HYPHEN, supportPackage.getMinorVersion());
                    if (versionSupportPkgMap.containsKey(key)) {
                        versionSupportPkgMap.get(key).add(supportPackage);
                    } else {
                        List<SupportPackage> packages = new ArrayList<SupportPackage>();
                        packages.add(supportPackage);
                        versionSupportPkgMap.put(key, packages);
                    }
                }
            }
            tenantSupportPkgMap.put(getTenantCode(), versionSupportPkgMap);
        } finally {
            destroyRequestContext();
        }
        return tenantSupportPkgMap;
    }
}
