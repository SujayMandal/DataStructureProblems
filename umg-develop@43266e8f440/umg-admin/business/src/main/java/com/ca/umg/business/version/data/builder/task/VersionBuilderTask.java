/**
 * 
 */
package com.ca.umg.business.version.data.builder.task;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.task.AbstractCallableTask;
import com.ca.umg.business.version.dao.VersionContainerDAO;

/**
 * @author kamathan
 *
 */
public class VersionBuilderTask extends AbstractCallableTask<Map<String, Map<String, String>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionBuilderTask.class);

    private final VersionContainerDAO containerDAO;

    public VersionBuilderTask(final String tenantCode, final VersionContainerDAO containerDAO) {
        super(tenantCode);
        this.containerDAO = containerDAO;
    }

    @Override
    public Map<String, Map<String, String>> call() {
        Map<String, Map<String, String>> allVersions = new HashMap<String, Map<String, String>>();
        setRequestContext();
        try {
            Map<String, String> versionMap = containerDAO.getAllUniqueVersions();
            LOGGER.info("Found {} versions for tenant {}.", MapUtils.isNotEmpty(versionMap) ? versionMap.size() : 0,
                    getTenantCode());
            allVersions.put(getTenantCode(), versionMap);
        } catch (Exception e) { // NOPMD
            LOGGER.error("Exception occured in Version Builder Task", e);
            throw e;
        } finally {
            destroyRequestContext();
        }
        return allVersions;
    }
}
