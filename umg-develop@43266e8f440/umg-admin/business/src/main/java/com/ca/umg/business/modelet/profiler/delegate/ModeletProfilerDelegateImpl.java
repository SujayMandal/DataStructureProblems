package com.ca.umg.business.modelet.profiler.delegate;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.delegate.AbstractDelegate;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterConstants;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.modelet.profiler.info.ModeletProfileParamsInfo;
import com.ca.pool.modelet.profiler.key.constant.ModeletProfilerKeyConstant;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;
import com.ca.umg.business.execution.bo.ModelExecutionEnvironmentBO;
import com.ca.umg.business.model.info.ModelExecutionEnvironmentInfo;
import com.ca.umg.business.modelet.profiler.bo.ModeletProfilerBo;
import com.ca.umg.business.modelet.profiler.entity.ModeletProfiler;
import com.ca.umg.business.modelet.profiler.info.ModeletProfilerInfo;
import com.ca.umg.business.modelet.profiler.key.info.ModeletProfilerKeyInfo;
import com.ca.umg.business.modelet.profiler.param.info.ModeletProfilerParamInfo;
import com.ca.umg.business.modelet.profiler.request.info.ModeletProfilerRequest;
import com.ca.umg.business.modelet.system.bo.SystemModeletBo;
import com.ca.umg.business.modelet.system.info.SystemModeletInfo;
import com.ca.umg.business.util.AdminUtil;
import com.hazelcast.core.IMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ca.framework.core.constants.PoolConstants.MODELET_PROFILER;
import static com.ca.framework.core.constants.PoolConstants.MODELET_PROFILER_LIST;

@Named
@SuppressWarnings({"PMD.TooManyMethods"})
public class ModeletProfilerDelegateImpl extends AbstractDelegate implements ModeletProfilerDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModeletProfilerDelegateImpl.class);

    @Inject
    private ModeletProfilerBo modeletProfilerBo;

    @Inject
    private ModelExecutionEnvironmentBO modelExecutionEnvironmentBO;

    @Inject
    private SystemModeletBo systemModeletBo;

    @Inject
    private CacheRegistry cacheRegistry;

    private Map<String, ModeletProfilerKeyInfo> modeletProfileKeyMap;

    private String[] profilerDefaultKey;

    @PostConstruct
    @SuppressWarnings("PMD")
    private void init() throws BusinessException, SystemException {
        modeletProfileKeyMap = new ConcurrentHashMap<>();
        List<ModeletProfilerKeyInfo> profilerKeyInfos = modeletProfilerBo.fetchAllModeletProfilerKey();
        for (ModeletProfilerKeyInfo keyInfo : profilerKeyInfos) {
            modeletProfileKeyMap.put(keyInfo.getCode(), keyInfo);
        }
        modeletProfileKeyMap = Collections.unmodifiableMap(modeletProfileKeyMap);

        profilerDefaultKey = new String[]{ModeletProfilerKeyConstant.MIN_MEMORY.getProfilerKey(),
                ModeletProfilerKeyConstant.MAX_PERM_SIZE.getProfilerKey(), ModeletProfilerKeyConstant.MAX_MEMORY.getProfilerKey(),
                ModeletProfilerKeyConstant.MAX_HEAP_FREE_RATIO.getProfilerKey(), ModeletProfilerKeyConstant.X_CMS_INIT_OCCUP_FRAC.getProfilerKey(),
                ModeletProfilerKeyConstant.X_CONC_MARK_SWEEP_GC.getProfilerKey(), ModeletProfilerKeyConstant.X_PAR_NEW_GC.getProfilerKey(),
                ModeletProfilerKeyConstant.X_CMS_INIT_OCCUP_ONLY.getProfilerKey()};

    }

    @Override
    public List<ModeletProfilerInfo> fetchModeletProfilerByExecEnvironment(String environment, String version) {
        List<ModeletProfiler> profilerList = modeletProfilerBo.fetchModeletProfilerByExecEnvironment(environment, version, "T");
        return convertToList(profilerList, ModeletProfilerInfo.class);
    }

    @Override
    public List<ModeletProfilerParamInfo> fetchModeletProfilerMap(String host, int port) throws BusinessException {

        List<ModeletProfilerParamInfo> profilerParams = null;
        SystemModeletInfo modelet = null;
        ModeletProfilerInfo profiler = null;
        try {
            modelet = systemModeletBo.fetchSystemModelet(host, String.valueOf(port));
            if (modelet != null) {
                profiler = modelet.getModeletProfiler();
            }
            profilerParams = systemModeletBo.fetchModeProfileParams(profiler.getId());
        } catch (SystemException e) {
            LOGGER.error(e.getLocalizedMessage());
        }

        return profilerParams;
    }

    @Override
    @Transactional(rollbackFor = SystemException.class)
    public void createModeletProfile(ModeletProfilerRequest modeletProfiler) throws BusinessException, SystemException {
        ModeletProfilerInfo profilerInfo = new ModeletProfilerInfo();
        profilerInfo.setName(modeletProfiler.getName());
        profilerInfo.setDescription(modeletProfiler.getDescription());

        ModelExecutionEnvironmentInfo modelExecutionEnvironment = modelExecutionEnvironmentBO
                .getModelExecutionEnvById(modeletProfiler.getExecutionEnvironmentId());
        if (modelExecutionEnvironment == null) {
            BusinessException.newBusinessException(BusinessExceptionCodes.BSE001015, new Object[]{});
        }
        profilerInfo.setModelExecutionEnvironment(modelExecutionEnvironment);

        Long countProfiler = modeletProfilerBo.modeletProfilerSizeByName(profilerInfo.getName());
        if (countProfiler > BusinessConstants.NUMBER_ZERO) {
            SystemException.newSystemException(BusinessExceptionCodes.BSE001023, new Object[]{profilerInfo.getName()});
        }

        try {
            profilerInfo = modeletProfilerBo.saveModeletProfiler(profilerInfo);
        } catch (SystemException se) {
            LOGGER.error("modele profiler creation failed.", se);
            throw se;
        }

        List<ModeletProfilerParamInfo> profilerParamInfos = new ArrayList<>();
        for (Map.Entry<String, String> entry : modeletProfiler.getParams().entrySet()) {
            ModeletProfilerParamInfo paramInfo = new ModeletProfilerParamInfo();
            paramInfo.setParamValue(entry.getValue());
            paramInfo.setModeletProfiler(profilerInfo);
            paramInfo.setModeletProfilerKey(modeletProfileKeyMap.get(entry.getKey()));
            profilerParamInfos.add(paramInfo);
        }

        try {
            profilerParamInfos = modeletProfilerBo.saveModeletProfilerParams(profilerParamInfos);
        } catch (SystemException e) {
            LOGGER.error("modele profiler param creation failed.", e);
            throw e;
        }

        List<ModeletProfileParamsInfo> params = new ArrayList<>();
        for (ModeletProfilerParamInfo data : profilerParamInfos) {
            ModeletProfileParamsInfo profilerParam = new ModeletProfileParamsInfo();
            profilerParam.setCode(data.getModeletProfilerKey().getCode());
            profilerParam.setDelimitter(data.getModeletProfilerKey().getDelimitter());
            profilerParam.setType(data.getModeletProfilerKey().getType());
            profilerParam.setProfileName(data.getModeletProfiler().getName());
            profilerParam.setParamValue(data.getParamValue());
            profilerParam.setExecutionEnvironment(data.getModeletProfiler().getModelExecutionEnvironment().getExecutionEnvironment());
            profilerParam.setEnvironmentVersion(data.getModeletProfiler().getModelExecutionEnvironment().getEnvironmentVersion());
            params.add(profilerParam);
        }

        final IMap<String, List<ModeletProfileParamsInfo>> modeletProfilerListData = cacheRegistry.getMap(MODELET_PROFILER_LIST);
        modeletProfilerListData.put(modeletProfiler.getName(), params);
    }

    @Override
    public List<ModeletProfilerRequest> fetchAllModeletProfiler() throws BusinessException, SystemException {
        List<ModeletProfilerInfo> modeletProfilerInfos;
        try {
            modeletProfilerInfos = modeletProfilerBo.fetchAllModeletProfiler();
        } catch (SystemException e) {
            LOGGER.error("fetch all modelet profi;er failed. {}", e);
            throw e;
        }
        List<ModeletProfilerRequest> modeletProfilerrequest = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(modeletProfilerInfos)) {
            for (ModeletProfilerInfo info : modeletProfilerInfos) {
                ModeletProfilerRequest profilerRequest = new ModeletProfilerRequest();
                profilerRequest.setId(info.getId());
                profilerRequest.setName(info.getName());
                profilerRequest.setDescription(info.getDescription());
                profilerRequest.setExecutionEnvironmentId(info.getModelExecutionEnvironment().getId());
                profilerRequest.setExecutionEnvironment(info.getModelExecutionEnvironment().getExecutionEnvironment());
                profilerRequest.setEnvironmentVersion(info.getModelExecutionEnvironment().getEnvironmentVersion());
                modeletProfilerrequest.add(profilerRequest);
            }
        }
        return modeletProfilerrequest;
    }

    @Override
    public ModeletProfilerRequest fetchModeletProfiler(String id) throws BusinessException, SystemException {
        ModeletProfilerRequest modeletProfilerRequest = null;
        try {
            modeletProfilerRequest = modeletProfilerBo.fetchModeletProfiler(id);
        } catch (SystemException e) {
            LOGGER.error("modelet profiler failed. {}", e);
            throw e;
        }
        return modeletProfilerRequest;
    }

    @Override
    public ModeletProfilerRequest fetchDefaultModeletProfilerData() throws BusinessException, SystemException {
        ModeletProfilerRequest modeletProfilerRequest = new ModeletProfilerRequest();

        Map<String, String> paramMap = new HashMap<>();

        for (String key : profilerDefaultKey) {
            paramMap.put(key, (String) cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER)
                    .get(org.apache.commons.lang.StringUtils.join(new Object[]{SystemParameterConstants.PROFILER_DEFAULT_KEY_PREFIX, key})));
        }

        modeletProfilerRequest.setParams(paramMap);
        return modeletProfilerRequest;
    }

    @Override
    public List<ModeletProfilerKeyInfo> fetchModeletProfilerKeys() throws BusinessException {
        return (List<ModeletProfilerKeyInfo>) modeletProfileKeyMap.values();
    }

    @Override
    public List<SystemModeletInfo> fetchSystemModeletList() throws BusinessException, SystemException {
        List<SystemModeletInfo> modeletInfos = null;
        try {
            modeletInfos = systemModeletBo.fetchSystemModeletList();
        } catch (SystemException e) {
            LOGGER.error("Failure in fetching system modelet list.", e);
            throw e;
        }
        return modeletInfos;
    }

    @Override
    public void updateSytemModeletProfiler(String hostName, String port, String profilerId) throws BusinessException, SystemException {
        SystemModeletInfo systemModeletInfo = null;
        try {
            systemModeletInfo = systemModeletBo.updateSytemModeletProfiler(hostName, port, profilerId);

            String key = StringUtils.join(hostName, FrameworkConstant.HYPHEN, port);
            List<ModeletProfilerParamInfo> profileParamList = systemModeletBo.fetchModeProfileParams(systemModeletInfo.getModeletProfiler().getId());

            List<ModeletProfileParamsInfo> params = new ArrayList<>();
            for (ModeletProfilerParamInfo data : profileParamList) {
                ModeletProfileParamsInfo profilerParam = new ModeletProfileParamsInfo();
                profilerParam.setCode(data.getModeletProfilerKey().getCode());
                profilerParam.setDelimitter(data.getModeletProfilerKey().getDelimitter());
                profilerParam.setType(data.getModeletProfilerKey().getType());
                profilerParam.setProfileName(data.getModeletProfiler().getName());
                profilerParam.setParamValue(data.getParamValue());
                profilerParam.setExecutionEnvironment(data.getModeletProfiler().getModelExecutionEnvironment().getExecutionEnvironment());
                profilerParam.setEnvironmentVersion(data.getModeletProfiler().getModelExecutionEnvironment().getEnvironmentVersion());
                params.add(profilerParam);
            }

            cacheRegistry.getMap(MODELET_PROFILER).put(key, params);
        } catch (SystemException e) {
            LOGGER.error("Failure in updating system modelet.", e);
            throw e;
        }
    }

    @Override
    public void removeModeletProfileMap(String modeletId) throws BusinessException, SystemException {
        boolean actualAdminAware = AdminUtil.getActualAdminAware();
        AdminUtil.setAdminAwareTrue();
        try {
            systemModeletBo.unlinkSytemModeletProfiler(modeletId);
        } catch (SystemException e) {
            LOGGER.error("Failure in updating system modelet.", e);
            throw e;
        } finally {
            AdminUtil.setActualAdminAware(actualAdminAware);
        }
    }

    @Override
    @Transactional(rollbackFor = {BusinessException.class, SystemException.class})
    public void updateModeletProfile(ModeletProfilerRequest modeletProfiler) throws BusinessException, SystemException {
        ModeletProfilerInfo modeletProfilerInfo = fetchModeletProfilerById(modeletProfiler);
        String profilerNamePrevious = modeletProfilerInfo.getName();
        populateDbParams(modeletProfiler, modeletProfilerInfo);

        try {
            modeletProfilerBo.saveModeletProfiler(modeletProfilerInfo);
        } catch (SystemException se) {
            LOGGER.error("Problem in updating modelet profiler. {}", se);
            BusinessException.newBusinessException(BusinessExceptionCodes.BSE001016, new Object[]{});
        }

        //Fetch profiler params by db.
        List<ModeletProfilerParamInfo> profilerparamInfoList = saveModeletProfiler(modeletProfiler, modeletProfilerInfo);

        modeletProfilerCacheOperation(modeletProfiler, modeletProfilerInfo, profilerNamePrevious, profilerparamInfoList);
    }

    private void modeletProfilerCacheOperation(ModeletProfilerRequest modeletProfiler, ModeletProfilerInfo modeletProfilerInfo,
                                               String profilerNamePrevious, List<ModeletProfilerParamInfo> profilerparamInfoList) {
        final IMap<String, List<ModeletProfileParamsInfo>> modeletProfilerData = cacheRegistry.getMap(MODELET_PROFILER);

        for (Map.Entry<String, List<ModeletProfileParamsInfo>> entry : modeletProfilerData.entrySet()) {
            LOGGER.info("MODELET_PROFILER cache update before : {} ==>> {}", entry.getKey(), entry.getValue().toString());
            if (StringUtils.equals(profilerNamePrevious, entry.getValue().get(0).getProfileName())) {
                List<ModeletProfileParamsInfo> profilerParamInfoCache = entry.getValue();
                for (ModeletProfileParamsInfo param : profilerParamInfoCache) {
                    param.setProfileName(modeletProfilerInfo.getName());
                    param.setParamValue(modeletProfiler.getParams().get(param.getCode()));
                }
                cacheRegistry.getMap(MODELET_PROFILER).put(entry.getKey(), profilerParamInfoCache);
            }
        }

        for (Map.Entry<Object, Object> entry : cacheRegistry.getMap(MODELET_PROFILER).entrySet()) {
            LOGGER.info("MODELET_PROFILER cache update after : {} ==>> {}", entry.getKey(), entry.getValue().toString());
        }

        //LOGGER.info("MODELET_PROFILER cache update after : {}", cacheRegistry.getMap(MODELET_PROFILER).get(profilerNamePrevious).toString());

        List<ModeletProfileParamsInfo> params = new ArrayList<>();
        for (ModeletProfilerParamInfo data : profilerparamInfoList) {
            ModeletProfileParamsInfo profilerParam = new ModeletProfileParamsInfo();
            profilerParam.setCode(data.getModeletProfilerKey().getCode());
            profilerParam.setDelimitter(data.getModeletProfilerKey().getDelimitter());
            profilerParam.setType(data.getModeletProfilerKey().getType());
            profilerParam.setProfileName(data.getModeletProfiler().getName());
            profilerParam.setParamValue(data.getParamValue());
            profilerParam.setExecutionEnvironment(data.getModeletProfiler().getModelExecutionEnvironment().getExecutionEnvironment());
            profilerParam.setEnvironmentVersion(data.getModeletProfiler().getModelExecutionEnvironment().getEnvironmentVersion());
            params.add(profilerParam);
        }

        final IMap<String, List<ModeletProfileParamsInfo>> modeletProfilerListData = cacheRegistry.getMap(MODELET_PROFILER_LIST);

        LOGGER.info("MODELET_PROFILER cache update before : {}", modeletProfilerListData.get(modeletProfiler.getName()).toString());

        modeletProfilerListData.put(modeletProfiler.getName(), params);

        LOGGER.info("MODELET_PROFILER cache update after : {}",
                cacheRegistry.getMap(MODELET_PROFILER_LIST).get(modeletProfiler.getName()).toString());
    }

    private List<ModeletProfilerParamInfo> saveModeletProfiler(ModeletProfilerRequest modeletProfiler, ModeletProfilerInfo modeletProfilerInfo)
            throws SystemException, BusinessException {
        List<ModeletProfilerParamInfo> profilerparamInfoList = modeletProfilerBo
                .fetchAllModeletProfilerParamsByProfilerId(modeletProfilerInfo.getId());
        List<ModeletProfilerParamInfo> profilerParamsChangedInfos = new ArrayList<>();
        LOGGER.info("profilerparamInfoList size : {}", profilerparamInfoList != null ? profilerparamInfoList.size() : 0);
        LOGGER.info("modeletProfiler.getParams() := {}", modeletProfiler.getParams());

        populateProfilerParamChanged(modeletProfiler, profilerparamInfoList, profilerParamsChangedInfos);

        if (!profilerParamsChangedInfos.isEmpty()) {
            try {
                modeletProfilerBo.saveModeletProfilerParams(profilerParamsChangedInfos);
            } catch (SystemException se) {
                LOGGER.error("Problem in updating modelet profiler. {}", se);
                BusinessException.newBusinessException(BusinessExceptionCodes.BSE001016, new Object[]{});
            }
        }
        return profilerparamInfoList;
    }

    private void populateProfilerParamChanged(ModeletProfilerRequest modeletProfiler, List<ModeletProfilerParamInfo> profilerparamInfoList,
                                              List<ModeletProfilerParamInfo> profilerParamsChangedInfos) throws BusinessException {
        populateProfilerKeyMised(modeletProfiler, profilerparamInfoList, profilerParamsChangedInfos);

        for (ModeletProfilerParamInfo profilerParam : profilerparamInfoList) {
            LOGGER.info("profiler data => key : {}, value : {}", profilerParam.getModeletProfilerKey().getCode(), profilerParam.getParamValue());
            if (StringUtils.isBlank(modeletProfiler.getParams().get(profilerParam.getModeletProfilerKey().getCode()))) {
                BusinessException
                        .newBusinessException(BusinessExceptionCodes.BSE001017, new Object[]{profilerParam.getModeletProfilerKey().getCode()});
            }
            if (!StringUtils.equals(profilerParam.getParamValue(), modeletProfiler.getParams().get(profilerParam.getModeletProfilerKey().getCode()))) {
                profilerParam.setParamValue(modeletProfiler.getParams().get(profilerParam.getModeletProfilerKey().getCode()));
                profilerParamsChangedInfos.add(profilerParam);
            }
        }
    }

    private void populateProfilerKeyMised(ModeletProfilerRequest modeletProfiler, List<ModeletProfilerParamInfo> profilerparamInfoList,
                                          List<ModeletProfilerParamInfo> profilerParamsChangedInfos) {
        if (modeletProfiler.getParams().size() != profilerparamInfoList.size()) {
            List<String> dbProfilerKeys = new ArrayList<>();
            for (ModeletProfilerParamInfo paramInfo : profilerparamInfoList) {
                dbProfilerKeys.add(paramInfo.getModeletProfilerKey().getCode());
            }
            for (Map.Entry<String, String> entry : modeletProfiler.getParams().entrySet()) {
                if (!dbProfilerKeys.contains(entry.getKey())) {
                    ModeletProfilerParamInfo paramInfo = new ModeletProfilerParamInfo();
                    paramInfo.setModeletProfiler(profilerparamInfoList.get(0).getModeletProfiler());
                    paramInfo.setModeletProfilerKey(modeletProfileKeyMap.get(entry.getKey()));
                    paramInfo.setParamValue(entry.getValue());
                    profilerParamsChangedInfos.add(paramInfo);
                    profilerparamInfoList.add(paramInfo);
                }
            }
        }
    }

    private void populateDbParams(ModeletProfilerRequest modeletProfiler, ModeletProfilerInfo modeletProfilerInfo)
            throws SystemException, BusinessException {
        if (!StringUtils.equals(modeletProfiler.getName(), modeletProfilerInfo.getName())) {
            modeletProfilerInfo.setName(modeletProfiler.getName());
            Long countProfiler = modeletProfilerBo.modeletProfilerSizeByName(modeletProfiler.getName());
            if (countProfiler > BusinessConstants.NUMBER_ZERO) {
                SystemException.newSystemException(BusinessExceptionCodes.BSE001023, new Object[]{modeletProfiler.getName()});
            }
        }
        if (!StringUtils.equals(modeletProfiler.getDescription(), modeletProfilerInfo.getDescription())) {
            modeletProfilerInfo.setDescription(modeletProfiler.getDescription());
        }
        if (!StringUtils.equals(modeletProfiler.getExecutionEnvironmentId(), modeletProfilerInfo.getModelExecutionEnvironment().getId())) {
            ModelExecutionEnvironmentInfo modelExecutionEnvironment = modelExecutionEnvironmentBO
                    .getModelExecutionEnvById(modeletProfiler.getExecutionEnvironmentId());
            if (!StringUtils.equals(modelExecutionEnvironment.getExecutionEnvironment(),
                    modeletProfilerInfo.getModelExecutionEnvironment().getExecutionEnvironment())) {
                BusinessException.newBusinessException(BusinessExceptionCodes.BSE001015, new Object[]{});
            }
            modeletProfilerInfo.setModelExecutionEnvironment(modelExecutionEnvironment);
        }
    }

    private ModeletProfilerInfo fetchModeletProfilerById(ModeletProfilerRequest modeletProfiler) throws BusinessException {
        ModeletProfilerInfo modeletProfilerInfo = null;
        try {
            modeletProfilerInfo = modeletProfilerBo.fetchModeletProfilerById(modeletProfiler.getId());
        } catch (SystemException e) {
            LOGGER.error("modelet profiler failed. {}", e);
        }
        if (modeletProfilerInfo == null) {
            BusinessException.newBusinessException(BusinessExceptionCodes.BSE001012, new Object[]{});
        }
        return modeletProfilerInfo;
    }

    @Override
    public void removeModeletProfile(String id) throws BusinessException, SystemException {
        List<SystemModeletInfo> modelets = systemModeletBo.fetchSystemModeletsByProfiler(id);
        if (!modelets.isEmpty()) {
            BusinessException.newBusinessException(BusinessExceptionCodes.BSE001020, new Object[]{});
        }
        ModeletProfilerInfo profilerData = modeletProfilerBo.fetchModeletProfilerById(id);
        modeletProfilerBo.removeModeletProfile(id);
        cacheRegistry.getMap(MODELET_PROFILER_LIST).remove(profilerData.getName());
    }

    @Override
    public List<ModelExecutionEnvironmentInfo> getActiveModelExecutionEnvList() throws BusinessException, SystemException {
        return modelExecutionEnvironmentBO.getActiveModelExecutionEnvList();
    }

    @Override
    public void restartModelets(List<ModeletClientInfo> modeletClientInfoList) throws SystemException, BusinessException {
        modelExecutionEnvironmentBO.restartModelets(modeletClientInfoList);
    }

    @Override
    public String downloadModeletLogs(ModeletClientInfo modeletClientInfo) throws SystemException, BusinessException {
        return modelExecutionEnvironmentBO.downloadModeletLogs(modeletClientInfo);
    }
}
