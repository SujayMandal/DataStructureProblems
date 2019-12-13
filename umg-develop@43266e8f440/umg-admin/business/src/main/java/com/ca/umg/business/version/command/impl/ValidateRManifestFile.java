package com.ca.umg.business.version.command.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.publishing.status.constants.PublishingStatus;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.info.ModelLibraryExecPackageMappingInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.transaction.entity.Environment;
import com.ca.umg.business.util.AdminUtil;
import com.ca.umg.business.util.CSVUtil;
import com.ca.umg.business.version.command.annotation.CommandDescription;
import com.ca.umg.business.version.command.base.AbstractCommand;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.business.version.info.VersionInfo;

@Named
@Scope("prototype")
@CommandDescription(name = "validateRManifestFile")
public class ValidateRManifestFile extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateRManifestFile.class);

    private static final String VALIDATE_R_MANIFEST_FILE = "validateRManifestFile";

    @Inject
    private ModelDelegate modelDelegate;

    @Inject
    private CacheRegistry cacheRegistry;

    @Override
    public void execute(Object data) throws BusinessException, SystemException {
        List<Error> errors = new ArrayList<Error>();
        boolean breakExecution = Boolean.FALSE;
        VersionInfo versionInfo = null;
        try {
            if (checkData(errors, data, VALIDATE_R_MANIFEST_FILE, VersionInfo.class)) {
                versionInfo = ((VersionInfo) data);
                ModelLibraryInfo modelLibraryInfo = versionInfo.getModelLibrary();
                if (modelLibraryInfo.getManifestFile() != null) {
                    String activeRversion = null;
                    List<ModelLibraryExecPackageMappingInfo> supportPackageInfos = CSVUtil.readManifestFile(versionInfo
                            .getModelLibrary().getManifestFile().getDataArray(), errors);
                    AdminUtil.setAdminAwareTrue();
                    Map<String, Object> environmentsMap = cacheRegistry.getMap(PoolConstants.ACTIVE_EXECUTION_ENVIRONMENTS);
                    Set<Entry<String, Object>> environmentsSet = environmentsMap.entrySet();
                    for (Entry<String, Object> entrySet : environmentsSet) {
                        if (StringUtils.equalsIgnoreCase(entrySet.getKey(), Environment.R.getValue())){
                            activeRversion = ((List<String>) entrySet.getValue()).get(0);
                        }
                    }
                    modelDelegate.getModelAddonPackages(activeRversion, supportPackageInfos, modelLibraryInfo.getExecEnv(),
                            errors);
                    if (CollectionUtils.isNotEmpty(errors)) {
                        breakExecution = Boolean.TRUE;
                    } else {
                        versionInfo.getModelLibrary().setSupportPackages(supportPackageInfos);
                    }
                }
            } else {
                breakExecution = Boolean.TRUE;
            }
        } catch (Exception ex) { // NOPMD
            breakExecution = Boolean.TRUE;
            errors.add(new Error(ex.getMessage(), VALIDATE_R_MANIFEST_FILE, StringUtils.EMPTY));
        }finally{
            AdminUtil.setAdminAwareFalse();
            sendStatusMessage(errors, data, PublishingStatus.VALIDATING_MANIFEST.getStatus());
        }
        getErrorController().setErrors(errors);
        getErrorController().setExecutionBreak(breakExecution);
    }

    @Override
    public void rollback(Object data) throws BusinessException, SystemException {
        LOGGER.info("Rollback called for " + VALIDATE_R_MANIFEST_FILE + ".But No Action required");
    }

    @Override
    public boolean isCreated() throws BusinessException, SystemException {
        return true;
    }



}
