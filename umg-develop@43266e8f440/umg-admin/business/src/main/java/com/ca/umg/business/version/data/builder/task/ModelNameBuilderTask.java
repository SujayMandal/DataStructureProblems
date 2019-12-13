/**
 * 
 */
package com.ca.umg.business.version.data.builder.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.task.AbstractCallableTask;
import com.ca.umg.business.version.dao.VersionContainerDAO;

public class ModelNameBuilderTask extends AbstractCallableTask<Map<String, Set<String>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelNameBuilderTask.class);

    private final VersionContainerDAO containerDAO;
    private final String tenantName;
    
    public ModelNameBuilderTask(final String tenantCode, final String tenantName, final VersionContainerDAO containerDAO) {
        super(tenantCode);
        this.containerDAO = containerDAO;
        this.tenantName = tenantName;
    }

    @Override
    public Map<String, Set<String>> call() throws SystemException {
        Map<String, Set<String>> allVersions = new HashMap<String, Set<String>>();
        setRequestContext();
        try {
            Set<String> modelNamesSet = containerDAO.getAllModelVersions();
            LOGGER.info("Found {} model names for tenant {}.", modelNamesSet.size(), getTenantCode());
            allVersions.put(tenantName, modelNamesSet);
        } catch (Exception e) { // NOPMD
            LOGGER.error("Exception occured while building models list", e);
        } finally {
            destroyRequestContext();
        }
        return allVersions;
    }
}
