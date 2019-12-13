package com.ca.systemmodelet;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.db.persistance.CAAbstractRoutingDataSource;
import com.ca.framework.core.entity.ModeletRestartInfo;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.ModeletStatus;
import com.ca.pool.modelet.profiler.info.ModeletProfileParamsInfo;
import com.hazelcast.core.IMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.ca.framework.core.constants.PoolConstants.*;
import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;

/**
 * @author basanaga
 * <p>
 * This class used to set system modelets when runtime starts
 */
public class SystemModeletConfigImpl implements SystemModeletConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(SystemModeletConfigImpl.class);

	private static final String FETCH_SYSTEM_MODELETS_SQL = "SELECT HOST_NAME AS HOST,PORT AS PORT,EXEC_LANGUAGE AS LANGUAGE,MEMBER_HOST AS MEMEBRHOST,EXECUTION_ENVIRONMENT AS EXECUTIONENVIRONMENT,POOL_NAME AS POOLNAME,R_SERVE_PORT AS RSERVEPORT,R_MODE AS RMODE FROM SYSTEM_MODELETS";
	private static final String INSERT_SYSTEM_MODELETS = "INSERT INTO SYSTEM_MODELETS(ID, HOST_NAME, PORT, EXEC_LANGUAGE, MEMBER_HOST, EXECUTION_ENVIRONMENT,POOL_NAME,R_SERVE_PORT,R_MODE, CREATED_BY, CREATED_ON) VALUES ('$modelet_id$', '$host$', $port$, '$executionLanguage$', '$memeberhost$', '$executionEnvironment$','$poolName$','$rServePort$','$rMode$', 'SYSTEM', UNIX_TIMESTAMP())";
	private static final String INSERT_SYSTEM_MODELETS_PROFILER_MAP =
			"INSERT INTO SYSTEM_MODELET_PROFILER_MAP(ID, SYSTEM_MODELET_ID, PROFILER_ID, CREATED_BY, CREATED_ON) VALUES "
					+ "(UUID(), '$modelet_id$', (SELECT mp.ID FROM MODELET_PROFILER mp WHERE mp.NAME = '$profiler_name$'), 'SYSTEM', UNIX_TIMESTAMP())";
	private static final String FETCH_RESTART_MODELETES_SQL = "SELECT ID AS ID,TENANT_ID AS TENANT_CODE, MODELNAME_VERSION AS MODELNAME_VERSION, RESTART_COUNT AS COUNT FROM MODELET_RESTART_CONFIG GROUP BY TENANT_ID,MODELNAME_VERSION";
	private static final String UPDATE_SYSTEM_MODELETS = "UPDATE SYSTEM_MODELETS SET POOL_NAME='$poolName$',R_SERVE_PORT='$rServePort$',R_MODE = '$rMode$',EXEC_LANGUAGE = '$executionLanguage$' WHERE  MEMBER_HOST ='$memeberhost$' AND PORT = '$port$'";
	private static final String CHECK_ENTERY_FOR_MODELET = "SELECT COUNT(*)  FROM SYSTEM_MODELETS WHERE MEMBER_HOST ='$memeberhost$' AND PORT = '$port$'";

	private static final String MODELET_PROFILER_PARAM_FETCH =
			"SELECT sm.HOST_NAME, sm.PORT, mee.EXECUTION_ENVIRONMENT, mee.ENVIRONMENT_VERSION, mp.NAME, mpk.CODE, mpk.`TYPE`, mpk.DELIMITTER, mpp.PARAM_VALUE "
					+ "FROM SYSTEM_MODELETS sm LEFT JOIN SYSTEM_MODELET_PROFILER_MAP smpl ON sm.ID = smpl.SYSTEM_MODELET_ID "
					+ "JOIN MODELET_PROFILER mp ON smpl.PROFILER_ID = mp.ID " + "JOIN MODELET_PROFILER_PARAM mpp ON mp.ID = mpp.PROFILER_ID "
					+ "JOIN MODELET_PROFILER_KEY mpk ON mpk.ID = mpp.PROFILER_KEY_ID "
					+ "LEFT OUTER JOIN MODEL_EXECUTION_ENVIRONMENTS mee ON mp.EXECUTION_ENV_ID = mee.id";

	private static final String MODELET_PROFILER_PARAM_LIST_FETCH =
			"SELECT mee.EXECUTION_ENVIRONMENT, mee.ENVIRONMENT_VERSION, mp.NAME, mpk.CODE, mpk.`TYPE`, mpk.DELIMITTER, mpp.PARAM_VALUE "
					+ "FROM MODELET_PROFILER mp JOIN MODELET_PROFILER_PARAM mpp ON mp.ID = mpp.PROFILER_ID "
					+ "JOIN MODELET_PROFILER_KEY mpk ON mpk.ID = mpp.PROFILER_KEY_ID "
					+ "LEFT OUTER JOIN MODEL_EXECUTION_ENVIRONMENTS mee ON mp.EXECUTION_ENV_ID = mee.id";

	private static final boolean ADMIN_AWARE = true;

	private static final String R_SERVE = "rServe";
	public static final String EXECUTION_ENVIRONMENT_KEY = "EXECUTION_ENVIRONMENT";
	public static final String CODE_KEY = "CODE";
	public static final String DELIMITTER_KEY = "DELIMITTER";
	public static final String PROFILER_NAME_KEY = "NAME";
	public static final String PROFILER_TYPE_KEY = "TYPE";
	public static final String PROFILER_PARAM_VALUE_KEY = "PARAM_VALUE";
	public static final String ENVIRONMENT_VERSION_KEY = "ENVIRONMENT_VERSION";
	private static final String HOST_NAME_KEY = "HOST_NAME";
	private static final String PORT_KEY = "PORT";

	@Inject
	private CacheRegistry cacheRegistry;

	@Inject
	private CAAbstractRoutingDataSource routingDataSource;

	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	private void init() {
		if(jdbcTemplate == null) {
			jdbcTemplate = new JdbcTemplate(routingDataSource.getDefaultTargetDataSource());
		}
		setRaSystemModelets();
		setRestartModeletCounts();
		try {
			this.populateModeletProfilerCache();
		} catch (SystemException e) {
			LOGGER.error("Problem in starting modelet profiler.", e);
		}
	}

	private void setRaSystemModelets() {
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		final Map<String, ModeletClientInfo> allModeletMap = cacheRegistry.getMap(ALL_MODELET_MAP);
		final List<Map<String, Object>> result = jdbcTemplate.queryForList(FETCH_SYSTEM_MODELETS_SQL);
		final IMap<String, ModeletClientInfo> systemModeletsMap = cacheRegistry.getMap(RA_SYSTEM_MODELETS);

		if(CollectionUtils.isNotEmpty(result)) {
			for (Map<String, Object> row : result) {
				ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
				modeletClientInfo.setHost(row.get("HOST").toString());
				modeletClientInfo.setPort((Integer) row.get("PORT"));
				modeletClientInfo.setExecutionLanguage(row.get("LANGUAGE").toString());
				modeletClientInfo.setMemberHost(row.get("MEMEBRHOST").toString());
				modeletClientInfo.setExecEnvironment(row.get("EXECUTIONENVIRONMENT").toString());
				modeletClientInfo.setPoolName(row.get("POOLNAME").toString());
				if(row.get("RSERVEPORT") != null) {
					modeletClientInfo.setrServePort((Integer) row.get("RSERVEPORT"));
				}
				if(row.get("RMODE") != null) {
					modeletClientInfo.setrMode(row.get("RMODE").toString());
				}

				if(allModeletMap.containsKey(modeletClientInfo.getHostKey())) {
					modeletClientInfo.setModeletStatus(allModeletMap.get(modeletClientInfo.getHostKey()).getModeletStatus());
					modeletClientInfo.setServerType(allModeletMap.get(modeletClientInfo.getHostKey()).getServerType());
					if(!StringUtils.equalsIgnoreCase(allModeletMap.get(modeletClientInfo.getHostKey()).getModeletStatus(),
							ModeletStatus.UNREGISTERED.getStatus())) {
						modeletClientInfo.setLoadedModel(allModeletMap.get(modeletClientInfo.getHostKey()).getLoadedModel());
						modeletClientInfo.setLoadedModelVersion(allModeletMap.get(modeletClientInfo.getHostKey()).getLoadedModelVersion());
					}
					if(StringUtils.isNotBlank(allModeletMap.get(modeletClientInfo.getHostKey()).getPoolName()) && !StringUtils
							.equalsIgnoreCase(modeletClientInfo.getPoolName(), allModeletMap.get(modeletClientInfo.getHostKey()).getPoolName())) {
						modeletClientInfo.setPoolName(allModeletMap.get(modeletClientInfo.getHostKey()).getPoolName());
						updateModeletConfig(modeletClientInfo, modeletClientInfo.getPoolName());
					}
				}

				systemModeletsMap.put(modeletClientInfo.getHostKey(), modeletClientInfo);

				allModeletMap.put(modeletClientInfo.getHostKey(), modeletClientInfo);
				LOGGER.error("Adding Modelet into RA_SYSTEM_MODELETS Map, Key : {}, Host : {}, Port : {}", modeletClientInfo.getHostKey(),
						modeletClientInfo.getHost(), modeletClientInfo.getPort());
			}
		}

		setAdminAware(actualAdminAware);
	}

	private void setRestartModeletCounts() {
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);

		final List<Map<String, Object>> result = jdbcTemplate.queryForList(FETCH_RESTART_MODELETES_SQL);
		final IMap<String, List<ModeletRestartInfo>> restartModeletsMap = cacheRegistry.getMap(FrameworkConstant.RESTART_MODELET_COUNT_MAP);

		if(CollectionUtils.isNotEmpty(result)) {
			for (Map<String, Object> row : result) {
				List<ModeletRestartInfo> modeletRestartInfoList = new ArrayList<ModeletRestartInfo>();
				ModeletRestartInfo modeletRestartInfo = new ModeletRestartInfo();
				modeletRestartInfo.setId(row.get(PoolConstants.ID).toString());
				modeletRestartInfo.setTenantId(row.get("TENANT_CODE").toString());
				modeletRestartInfo.setModelNameAndVersion(row.get("MODELNAME_VERSION").toString());
				modeletRestartInfo.setRestartCount(Integer.parseInt(row.get("COUNT").toString()));
				modeletRestartInfo.setExecCount(PoolConstants.NUMBER_ZERO);
				modeletRestartInfoList.add(modeletRestartInfo);
				restartModeletsMap.put(modeletRestartInfo.getTenantId() + "_" + modeletRestartInfo.getModelNameAndVersion(), modeletRestartInfoList);
			}
		}

		setAdminAware(actualAdminAware);
	}

	@Override
	public String createModeletConfig(final ModeletClientInfo modeletClientInfo) {
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		LOGGER.info("New modelet started with the host :" + modeletClientInfo.getHost() + " port :" + modeletClientInfo.getPort()
				+ " execution language :" + modeletClientInfo.getExecutionLanguage());

		String sql = INSERT_SYSTEM_MODELETS;
		String modeletId = UUID.randomUUID().toString();
		sql = StringUtils.replace(sql, "$modelet_id$", modeletId);
		sql = StringUtils.replace(sql, "$host$", modeletClientInfo.getHost());
		sql = StringUtils.replace(sql, "$port$", String.valueOf(modeletClientInfo.getPort()));
		sql = StringUtils.replace(sql, "$executionLanguage$", modeletClientInfo.getExecutionLanguage());
		sql = StringUtils.replace(sql, "$memeberhost$", modeletClientInfo.getMemberHost());
		sql = StringUtils.replace(sql, "$executionEnvironment$", modeletClientInfo.getExecEnvironment());
		sql = StringUtils.replace(sql, "$poolName$", modeletClientInfo.getPoolName());
		sql = StringUtils.replace(sql, "$rMode$", modeletClientInfo.getrMode());
		if(StringUtils.equalsIgnoreCase(SystemConstants.R_SERVE, modeletClientInfo.getrMode())) {
			sql = StringUtils.replace(sql, "$rServePort$", String.valueOf(modeletClientInfo.getrServePort()));
		} else {
			sql = StringUtils.replace(sql, "$rServePort$", String.valueOf(0));
		}

		LOGGER.error("SQL insert Query .....{} ", sql);
		// jdbcTemplate.update(INSERT_SYSTEM_MODELETS, parameters);
		jdbcTemplate.update(sql);

		LOGGER.info("New modelet info inserted into system modelet table successfully");

		setAdminAware(actualAdminAware);

		return modeletId;
	}

	@Override
	public void createModeletProfilerLink(final ModeletClientInfo modeletClientInfo, final String modeletId) {
		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);

		if(StringUtils.isNotEmpty(modeletClientInfo.getProfiler())) {
			String profilerLinkSql = INSERT_SYSTEM_MODELETS_PROFILER_MAP;
			profilerLinkSql = StringUtils.replace(profilerLinkSql, "$modelet_id$", modeletId);
			profilerLinkSql = StringUtils.replace(profilerLinkSql, "$profiler_name$", modeletClientInfo.getProfiler());

			LOGGER.error("SQL insert Query for modelet profiler linking .....{} ", profilerLinkSql);
			jdbcTemplate.update(profilerLinkSql);
		}

		LOGGER.info("New modelet linked to profiler successfully");
		setAdminAware(actualAdminAware);
	}

	@Override
	public void refreshCache() {
		LOGGER.error("Refreshing System Modelet from Database");
		setRaSystemModelets();
	}

	private void setAdminAware(boolean adminAware) {
		if(getRequestContext() != null) {
			getRequestContext().setAdminAware(adminAware);
		}
	}

	private boolean getActualAdminAware() {
		boolean isAdminAware = false;
		if(getRequestContext() != null) {
			isAdminAware = getRequestContext().isAdminAware();
		}
		return isAdminAware;
	}

	@Override
	public void updateModeletConfig(ModeletClientInfo modeletClientInfo, String poolName) {

		boolean actualAdminAware = getActualAdminAware();
		setAdminAware(ADMIN_AWARE);
		String check = CHECK_ENTERY_FOR_MODELET;
		check = StringUtils.replace(check, "$port$", String.valueOf(modeletClientInfo.getPort()));
		check = StringUtils.replace(check, "$memeberhost$", modeletClientInfo.getMemberHost());
		int count = jdbcTemplate.queryForInt(check);
		if(count == 0) {
			setAdminAware(actualAdminAware);
			createModeletConfig(modeletClientInfo);
		} else {
			String sql = UPDATE_SYSTEM_MODELETS;
			sql = StringUtils.replace(sql, "$port$", String.valueOf(modeletClientInfo.getPort()));
			sql = StringUtils.replace(sql, "$memeberhost$", modeletClientInfo.getMemberHost());
			sql = StringUtils.replace(sql, "$poolName$", poolName);
			sql = StringUtils.replace(sql, "$rMode$", modeletClientInfo.getrMode());
			sql = StringUtils.replace(sql, "$executionLanguage$", modeletClientInfo.getExecutionLanguage());
			if(StringUtils.equalsIgnoreCase(R_SERVE, modeletClientInfo.getrMode())) {
				sql = StringUtils
						.replace(sql, "$rServePort$", String.valueOf(modeletClientInfo.getrServePort() != 0 ? modeletClientInfo.getrServePort() : 0));
			} else {
				sql = StringUtils.replace(sql, "$rServePort$", String.valueOf(0));
			}

			LOGGER.error("SQL update Query .....{} ", sql);
			// jdbcTemplate.update(INSERT_SYSTEM_MODELETS, parameters);
			jdbcTemplate.update(sql);

			setAdminAware(actualAdminAware);
		}
	}

	private void populateModeletProfilerCache() throws SystemException {
		final IMap<String, List<ModeletProfileParamsInfo>> modeletProfilerData = cacheRegistry.getMap(MODELET_PROFILER);
		if(modeletProfilerData.isEmpty()) {
			final List<Map<String, Object>> modeletProfilerResult = jdbcTemplate.queryForList(MODELET_PROFILER_PARAM_FETCH);
			if(CollectionUtils.isNotEmpty(modeletProfilerResult)) {
				for (Map<String, Object> data : modeletProfilerResult) {
					String key = StringUtils.join(new String[] {String.valueOf(data.get(HOST_NAME_KEY)), String.valueOf(data.get(PORT_KEY))},
							FrameworkConstant.HYPHEN);

					List<ModeletProfileParamsInfo> profileParamList = modeletProfilerData.get(key);
					if(profileParamList == null)
						profileParamList = new ArrayList<>();

					ModeletProfileParamsInfo param = new ModeletProfileParamsInfo();
					if(data.containsKey(CODE_KEY) && data.get(CODE_KEY) != null)
						param.setCode(String.valueOf(data.get(CODE_KEY)));
					if(data.containsKey(DELIMITTER_KEY) && data.get(DELIMITTER_KEY) != null)
						param.setDelimitter(String.valueOf(data.get(DELIMITTER_KEY)));
					if(data.containsKey(PROFILER_NAME_KEY) && data.get(PROFILER_NAME_KEY) != null)
						param.setProfileName(String.valueOf(data.get(PROFILER_NAME_KEY)));
					if(data.containsKey(PROFILER_TYPE_KEY) && data.get(PROFILER_TYPE_KEY) != null)
						param.setType(String.valueOf(data.get(PROFILER_TYPE_KEY)));
					if(data.containsKey(PROFILER_PARAM_VALUE_KEY) && data.get(PROFILER_PARAM_VALUE_KEY) != null)
						param.setParamValue(String.valueOf(data.get(PROFILER_PARAM_VALUE_KEY)));
					if(data.containsKey(EXECUTION_ENVIRONMENT_KEY) && data.get(EXECUTION_ENVIRONMENT_KEY) != null)
						param.setExecutionEnvironment(String.valueOf(data.get(EXECUTION_ENVIRONMENT_KEY)));
					if(data.containsKey(ENVIRONMENT_VERSION_KEY) && data.get(ENVIRONMENT_VERSION_KEY) != null)
						param.setEnvironmentVersion(String.valueOf(data.get(ENVIRONMENT_VERSION_KEY)));

					profileParamList.add(param);

					LOGGER.error("Adding " + key + "into modelet profiler.");
					cacheRegistry.getMap(MODELET_PROFILER).put(key, profileParamList);
					LOGGER.error("Added " + key + "into modelet profiler.");
				}
			}
		}

		final IMap<String, List<ModeletProfileParamsInfo>> modeletProfilerListData = cacheRegistry.getMap(MODELET_PROFILER_LIST);
		if(modeletProfilerListData.isEmpty()) {
			final List<Map<String, Object>> modeletProfilerResult = jdbcTemplate.queryForList(MODELET_PROFILER_PARAM_LIST_FETCH);
			if(CollectionUtils.isNotEmpty(modeletProfilerResult)) {
				for (Map<String, Object> data : modeletProfilerResult) {
					String key = String.valueOf(data.get(PROFILER_NAME_KEY));

					List<ModeletProfileParamsInfo> profileParamList = modeletProfilerListData.get(key);
					if(profileParamList == null)
						profileParamList = new ArrayList<>();

					ModeletProfileParamsInfo param = new ModeletProfileParamsInfo();
					if(data.containsKey(CODE_KEY) && data.get(CODE_KEY) != null)
						param.setCode(String.valueOf(data.get(CODE_KEY)));
					if(data.containsKey(DELIMITTER_KEY) && data.get(DELIMITTER_KEY) != null)
						param.setDelimitter(String.valueOf(data.get(DELIMITTER_KEY)));
					if(data.containsKey(PROFILER_NAME_KEY) && data.get(PROFILER_NAME_KEY) != null)
						param.setProfileName(String.valueOf(data.get(PROFILER_NAME_KEY)));
					if(data.containsKey(PROFILER_TYPE_KEY) && data.get(PROFILER_TYPE_KEY) != null)
						param.setType(String.valueOf(data.get(PROFILER_TYPE_KEY)));
					if(data.containsKey(PROFILER_PARAM_VALUE_KEY) && data.get(PROFILER_PARAM_VALUE_KEY) != null)
						param.setParamValue(String.valueOf(data.get(PROFILER_PARAM_VALUE_KEY)));
					if(data.containsKey(EXECUTION_ENVIRONMENT_KEY) && data.get(EXECUTION_ENVIRONMENT_KEY) != null)
						param.setExecutionEnvironment(String.valueOf(data.get(EXECUTION_ENVIRONMENT_KEY)));
					if(data.containsKey(ENVIRONMENT_VERSION_KEY) && data.get(ENVIRONMENT_VERSION_KEY) != null)
						param.setEnvironmentVersion(String.valueOf(data.get(ENVIRONMENT_VERSION_KEY)));

					profileParamList.add(param);

					LOGGER.error("Adding " + key + "into modelet profiler list.");
					cacheRegistry.getMap(MODELET_PROFILER_LIST).put(key, profileParamList);
					LOGGER.error("Added " + key + "into modelet profiler list.");
				}
			}
		}

	}

}
