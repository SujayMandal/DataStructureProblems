package com.ca.umg.modelet.runtime.factory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.connection.Connector;
import com.ca.framework.core.connection.LocalConnector;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.pool.model.ExecutionLanguage;
import com.ca.umg.modelet.common.SystemInfo;
import com.ca.umg.modelet.constants.ErrorCodes;
import com.ca.umg.modelet.runtime.ModeletRuntime;
import com.ca.umg.modelet.runtime.impl.ExcelRuntime;
import com.ca.umg.modelet.runtime.impl.MatlabRuntime;
import com.ca.umg.modelet.runtime.impl.RRuntime;
import com.ca.umg.modelet.runtime.impl.RserveRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class RuntimeFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeFactory.class);

    @Inject
    private SystemInfo systemInfo;

    @Inject
    private UmgFileProxy umgFileProxy;
    
    @Inject
    private CacheRegistry cacheRegistry;

    @Inject
    @Named(LocalConnector.BEAN_NAME)
    private Connector connector;

    private MatlabRuntime matlabRuntime;

    private RserveRuntime rServeRuntime;
    
    private RRuntime rRuntime;

    private ExcelRuntime excelRuntime;

    private final List<ModeletRuntime> modeletRuntimes = new ArrayList<ModeletRuntime>();

    public List<ModeletRuntime> getRuntimeInstance() throws SystemException {
        LOGGER.error("systemInfo is {}", systemInfo);
        LOGGER.error("systemInfo execution language {}", (systemInfo != null) ? systemInfo.getExecutionLanguage(): null);
        switch (ExecutionLanguage.getEnvironment(systemInfo.getExecutionLanguage())) {
        case R:
        	if(systemInfo.getrMode() != null && StringUtils.equalsIgnoreCase(SystemConstants.R_SERVE, systemInfo.getrMode())) {
        		rServeRuntime = new RserveRuntime(systemInfo, umgFileProxy, connector, cacheRegistry);
        		modeletRuntimes.add(rServeRuntime);
        	} else {
        		rRuntime = new RRuntime(systemInfo, umgFileProxy, cacheRegistry);
        		modeletRuntimes.add(rRuntime);
        	}
            break;
        case EXCEL:
            excelRuntime = new ExcelRuntime(systemInfo);
            modeletRuntimes.add(excelRuntime);
            break;
        case MATLAB:
            matlabRuntime = new MatlabRuntime(systemInfo, umgFileProxy);
            modeletRuntimes.add(matlabRuntime);
            break;
        default:
            SystemException.newSystemException(ErrorCodes.ME0000, new Object[] { systemInfo.getExecutionLanguage() });
        }
        return modeletRuntimes;
    }

    public void initializeRuntimes() throws SystemException {
        final List<ModeletRuntime> modeletRuntimes = getRuntimeInstance();
        for (ModeletRuntime modeletRuntime : modeletRuntimes) {
            modeletRuntime.initializeRuntime();
        }
    }

    public void destroyRuntimes() throws SystemException {
        final List<ModeletRuntime> modeletRuntimes = getRuntimeInstance();
        for (ModeletRuntime modeletRuntime : modeletRuntimes) {
            modeletRuntime.destroyRuntime();
        }
    }

    public void loadModels() throws SystemException {
        /*
         * final ModeletRuntime modeletRuntime = getRuntimeInstance(type); modeletRuntime.loadModels();
         */
    }

    public MatlabRuntime getMatlabRuntime() {
        return matlabRuntime;
    }

    public RserveRuntime getRserveRuntime() {
        return rServeRuntime;
    }
    
    public RRuntime getrRuntime() {
        return rRuntime;
    }

    public void reinitializeRRuntime() throws SystemException {
        try {
            rRuntime.destroyRuntime();
        } finally {
            // modeletRuntimes.remove(rRuntime);
            // rRuntime = new RRuntime(systemInfo, umgFileProxy);
            rRuntime.initializeRuntime();
            // modeletRuntimes.add(rRuntime);
        }
    }
    
    public void reinitializeRserveRuntime() throws SystemException {
        try {
        	rServeRuntime.destroyRuntime();
        } finally {
            // modeletRuntimes.remove(rRuntime);
            // rRuntime = new RRuntime(systemInfo, umgFileProxy);
        	rServeRuntime.initializeRuntime();
            // modeletRuntimes.add(rRuntime);
        }
    }

    public ExcelRuntime getExcelRuntime() {
        return excelRuntime;
    }
}