package com.ca.umg.business.version.command.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.rmodel.info.SupportPackage;
import com.ca.framework.core.rmodel.info.VersionExecInfo;
import com.ca.framework.core.util.ModelLanguages;
import com.ca.framework.event.StaticDataRefreshEvent;
import com.ca.framework.event.util.EventOperations;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.version.bo.VersionBOImpl;
import com.ca.umg.business.version.command.annotation.CommandDescription;
import com.ca.umg.business.version.command.base.AbstractCommand;
import com.ca.umg.business.version.command.error.Error;
import com.ca.umg.business.version.data.VersionDataContainer;
import com.ca.umg.business.version.delegate.VersionDelegate;
import com.ca.umg.business.version.event.VersionRefreshEvent;
import com.ca.umg.business.version.info.VersionInfo;
import com.ca.umg.business.version.listener.VersionRefreshEventListener;
import com.ca.umg.report.model.ModelReportTemplateInfo;

/**
 * This class would do the following steps:</br>
 * <table BORDER CELLPADDING=3 CELLSPACING=1>
 * <tr>
 * <td ALIGN=CENTER COLSPAN = 1><b>Create ({@link #execute(Object)})</b></td>
 * <td ALIGN=CENTER COLSPAN = 1><b>Rollback ({@link #rollback(Object)})</b></td>
 * </tr>
 * <tr>
 * <td>Create version by calling {@link VersionDelegate#create(VersionInfo)}</td>
 * <td>call delete {@link VersionDelegate#delete(String)} using {Id}</td>
 * </tr>
 * <tr>
 * <td>add entry to the cache by calling {@link VersionDataContainer#addVersionToContainer(String, String)} using version name and
 * description</td>
 * <td>remove the entry from cache if this is the last entry -- removed in {@link VersionBOImpl#delete(String)}</td>
 * </tr>
 * </table>
 * 
 * @author raddibas
 *
 */
@Named
@Scope("prototype")
@CommandDescription(name = "createVersion")
public class CreateVersion extends AbstractCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateVersion.class);
    private static final String CREATE_VERSION = "createVersion";

    @Inject
    private VersionDelegate versionDelegate;

    @Inject
    private VersionDataContainer versionDataContainer;

    @Inject
    private CacheRegistry cacheRegistry;

    @Override
    public void execute(Object data) throws BusinessException, SystemException {
        VersionInfo versionInfo = null;
        VersionInfo savedVersionInfo = null;
        List<Error> errors = new ArrayList<Error>();
        boolean breakExecution = Boolean.FALSE;
        try {
            if (checkData(errors, data, CREATE_VERSION, VersionInfo.class)) {
                setExecuted(Boolean.TRUE);
                versionInfo = ((VersionInfo) data);
                ModelReportTemplateInfo reportTemplateInfo = versionInfo.getReportTemplateInfo();
                savedVersionInfo = versionDelegate.create(versionInfo);
                savedVersionInfo.setReportTemplateInfo(reportTemplateInfo);
                if (savedVersionInfo != null) {
                    versionInfo.setId(savedVersionInfo.getId());
                    versionInfo.setMajorVersion(savedVersionInfo.getMajorVersion());
                    versionInfo.setMinorVersion(savedVersionInfo.getMinorVersion());
                    updateCache(versionInfo, EventOperations.ADD.getOperation());
                    versionInfo.setReportTemplateInfo(reportTemplateInfo);
                }
            } else {
                breakExecution = Boolean.TRUE;
            }
        } catch (SystemException | BusinessException ex) {
            LOGGER.error("error in CreateVersion::execute method : ", ex);
            errors.add(new Error(ex.getLocalizedMessage(), BusinessConstants.EXECUTE, ex.getCode()));
            breakExecution = Boolean.TRUE;
        }
        getErrorController().setErrors(errors);
        getErrorController().setExecutionBreak(breakExecution);
    }

    private void updateCache(VersionInfo versionInfo, String operation) throws SystemException {

        updateVersionInfo(versionInfo, operation);

        String versionKey = StringUtils.join(versionInfo.getName(), BusinessConstants.CHAR_HYPHEN, versionInfo.getMajorVersion(),
                BusinessConstants.CHAR_HYPHEN, versionInfo.getMinorVersion());

        // update version execution environment map
        updateVersionExecEnvmap(versionInfo, versionKey, operation);

        if (StringUtils.equalsIgnoreCase(ModelLanguages.R.getLanguage(), versionInfo.getModelLibrary().getExecutionLanguage())) {

            // update support packages
            updateSupportPackages(versionInfo, operation, versionKey);

            // update cache maps
            StaticDataRefreshEvent<String> refreshPackageNamesEvent = buildEvent(StaticDataRefreshEvent.REFRESH_PACKAGE_NAMES,
                    operation, versionKey, versionInfo.getModelLibrary().getPackageName());
            cacheRegistry.getTopic(StaticDataRefreshEvent.REFRESH_PACKAGE_NAMES).publish(refreshPackageNamesEvent);
        }
    }

    private void updateVersionInfo(VersionInfo versionInfo, String operation) throws SystemException {

        VersionRefreshEvent<VersionInfo> event = new VersionRefreshEvent<VersionInfo>();
        event.setEvent(VersionRefreshEvent.REFRESH_VERSION);
        event.setOperation(operation);
        event.setData(versionInfo);
        event.setTenantCode(RequestContext.getRequestContext().getTenantCode());

        switch (EventOperations.valueOf(operation)) {
        case ADD:
            versionDataContainer.addVersionToContainer(RequestContext.getRequestContext().getTenantCode(), versionInfo.getName(),
                    versionInfo.getDescription());
            cacheRegistry.getTopic(VersionRefreshEventListener.VERSION_REFRESH).publish(event);
            versionDataContainer.addModelNameToContainer(versionInfo.getName(), versionInfo.getMajorVersion(), versionInfo.getMinorVersion());
            break;
        case REMOVE:
            long versionCount = versionDelegate.getVersionCountByName(versionInfo.getName());
            if (versionCount == BusinessConstants.NUMBER_ZERO) {
                versionDataContainer.removeVersionFromContainer(RequestContext.getRequestContext().getTenantCode(),
                        versionInfo.getName());
                cacheRegistry.getTopic(VersionRefreshEventListener.VERSION_REFRESH).publish(event);
                versionDataContainer.removeModelNameFromContainer(versionInfo.getName(), versionInfo.getMajorVersion(), versionInfo.getMinorVersion());
            }
            break;
        default:
            break;
        }
    }

    private void updateSupportPackages(VersionInfo versionInfo, String operation, String versionKey) throws SystemException {
        List<SupportPackage> supportPackages = null;
        switch (EventOperations.valueOf(operation)) {
        case ADD:
            supportPackages = versionDelegate.getSupportPackagesForVersion(versionInfo.getName(), versionInfo.getMajorVersion(),
                    versionInfo.getMinorVersion());
            break;
        case REMOVE:
            break;
        default:
            break;
        }

        StaticDataRefreshEvent<List<SupportPackage>> refreshSupportPackagesEvent = buildEvent(
                StaticDataRefreshEvent.REFRESH_SUPPORT_PACKAGES_EVENT, operation, versionKey, supportPackages);
        cacheRegistry.getTopic(StaticDataRefreshEvent.REFRESH_SUPPORT_PACKAGES_EVENT).publish(refreshSupportPackagesEvent);
    }

    private void updateVersionExecEnvmap(VersionInfo versionInfo, String versionKey, String operation) throws SystemException {
    	VersionExecInfo versionExecInfo = null;

        switch (EventOperations.valueOf(operation)) {
        case ADD:
        	versionExecInfo = versionDelegate.getVersionExecutionEnvInfo(versionInfo.getName(), versionInfo.getMajorVersion(),
                    versionInfo.getMinorVersion());
            break;
        case REMOVE:
            break;
        default:
            break;
        }

        LOGGER.debug("request to update the map : {} for key {}",StaticDataRefreshEvent.REFRESH_VERSION_EXC_ENV_MAP,versionKey);
        StaticDataRefreshEvent<VersionExecInfo> versionExcEnvRefreshEvent = buildEvent(
                StaticDataRefreshEvent.REFRESH_VERSION_EXC_ENV_MAP, operation, versionKey, versionExecInfo);

        LOGGER.debug("Event to be raised is : "+versionExcEnvRefreshEvent);
        
        cacheRegistry.getTopic(StaticDataRefreshEvent.REFRESH_VERSION_EXC_ENV_MAP).publish(versionExcEnvRefreshEvent);
    }

    private <T> StaticDataRefreshEvent<T> buildEvent(String eventName, String operation, String versionKey, T t) {
        StaticDataRefreshEvent<T> event = new StaticDataRefreshEvent<T>();
        event.setEvent(eventName);
        event.setOperation(operation);
        event.setData(t);
        event.setTenantCode(RequestContext.getRequestContext().getTenantCode());
        event.setVersionKey(versionKey);
        return event;
    }

    @Override
    public void rollback(Object data) throws BusinessException, SystemException {
        VersionInfo versionInfo = null;
        List<Error> errors = new ArrayList<Error>();
        boolean breakExecution = Boolean.FALSE;
        try {
            if (checkData(errors, data, CREATE_VERSION, VersionInfo.class)) {
                versionInfo = ((VersionInfo) data);
                versionDelegate.delete(versionInfo.getId());
                // refresh the cache
                updateCache(versionInfo, EventOperations.REMOVE.getOperation());
            } else {
                breakExecution = Boolean.TRUE;
            }
        } catch (SystemException | BusinessException ex) {
            breakExecution = Boolean.TRUE;
            LOGGER.error("error in CreateVersion::rollback method : ", ex);
            errors.add(new Error(ex.getLocalizedMessage(), BusinessConstants.ROLLBACK, ex.getCode()));
        }
        getErrorController().setErrors(errors);
        getErrorController().setExecutionBreak(breakExecution);
    }

    @Override
    public boolean isCreated() throws BusinessException, SystemException {
        return true;
    }
}
