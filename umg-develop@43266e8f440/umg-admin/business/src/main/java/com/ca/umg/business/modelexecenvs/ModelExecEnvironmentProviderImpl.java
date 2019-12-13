package com.ca.umg.business.modelexecenvs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.pool.model.ExecutionLanguage;
import com.ca.umg.business.exception.codes.BusinessExceptionCodes;

/**
 * @author basanaga This class used to keep the model execution environments in
 *         hazelcast cache
 */

@Named
public class ModelExecEnvironmentProviderImpl implements ModelExecEnvironmentProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelExecEnvironmentProviderImpl.class);
	private static final String GET_EXECUTION_ENVIRONMENTS= "SELECT DISTINCT(MEE.EXECUTION_ENVIRONMENT) ENVIRONMENT FROM MODEL_EXECUTION_ENVIRONMENTS MEE";
	private static final String GET_EXEC_ENVIRONMENT_NAMES= "SELECT DISTINCT(NAME) NAME FROM MODEL_EXECUTION_ENVIRONMENTS MEE WHERE MEE.EXECUTION_ENVIRONMENT=?";	
	private static final String GET_R_ACTIVE_EXEC_ENVIRONMENT= "SELECT NAME FROM MODEL_EXECUTION_ENVIRONMENTS MEE WHERE MEE.IS_ACTIVE='T' AND MEE.EXECUTION_ENVIRONMENT='R'";

	@Inject
	private CacheRegistry cacheRegistry;

	@Inject
	private DataSource dataSource;

	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	private void buildEnvironmentNames() throws SystemException {// NOPMD
		LOGGER.info("Started building environment names...");
		jdbcTemplate = new JdbcTemplate(dataSource);
		List<String> environments = getEnvironments();
		for (String environment : environments) {
			cacheRegistry.getMap(PoolConstants.MODEL_EXECUTION_ENVIRONMENTS).put(environment, getNames(environment));
			if(StringUtils.equals(environment, ExecutionLanguage.R.getValue())){
				cacheRegistry.getMap(PoolConstants.ACTIVE_EXECUTION_ENVIRONMENTS).put(StringUtils.upperCase(environment), getRActiveExecEnvironments());	
			}else{
				cacheRegistry.getMap(PoolConstants.MODEL_EXECUTION_ENVIRONMENTS).put(environment, getNames(environment));
				cacheRegistry.getMap(PoolConstants.ACTIVE_EXECUTION_ENVIRONMENTS).put(StringUtils.upperCase(environment), getNames(environment));				
			}
		}
		LOGGER.info("Finished building environments.");
	}

	private List<String> getEnvironments() throws SystemException {
		List<String> environments = new ArrayList();
		try {
			environments = jdbcTemplate.queryForList(GET_EXECUTION_ENVIRONMENTS,String.class);
		} catch (DataAccessException ex) {
			LOGGER.error("exception while getting execution environments.Exception is ", ex);
			SystemException.newSystemException(BusinessExceptionCodes.BSE000143, new Object[] { ex.getMessage() });

		}
		return environments;
	}

	private List<String> getNames(String environment) throws SystemException {
		List<String> names = new ArrayList();
		try {
			names = jdbcTemplate.queryForList(
					GET_EXEC_ENVIRONMENT_NAMES,new Object[]{environment},String.class);
		} catch (DataAccessException ex) {
			LOGGER.error("exception while getting execution environments.Exception is ", ex);
			SystemException.newSystemException(BusinessExceptionCodes.BSE000143, new Object[] { ex.getMessage() });

		}
		return names;

	}
	
	private List<String> getRActiveExecEnvironments() throws SystemException {
		List<String> names = new ArrayList();		
		try {
			names = jdbcTemplate.queryForList(
					GET_R_ACTIVE_EXEC_ENVIRONMENT,String.class);			
		} catch (DataAccessException ex) {
			LOGGER.error("exception while getting execution environments.Exception is ", ex);
			SystemException.newSystemException(BusinessExceptionCodes.BSE000143, new Object[] { ex.getMessage() });

		}
		return names;
	}	

	@Override
	public List<String> getAllExecutionEnvironmentNames() {
		Map<String, Object> environments = cacheRegistry.getMap(PoolConstants.MODEL_EXECUTION_ENVIRONMENTS);
		Set<Entry<String, Object>> environmentsSet = environments.entrySet();
		List<String> envs = new ArrayList();
		for (Entry<String, Object> entrySet : environmentsSet) {
			envs.addAll((List) entrySet.getValue());
		}
		return envs;
	}

    @Override
    public Map<String, List<String>> getExecutionEnvironmentMap() {
        Map<String, List<String>> excEnvMap = new HashMap();
        Map<String, Object> environmentsMap = cacheRegistry.getMap(PoolConstants.ACTIVE_EXECUTION_ENVIRONMENTS);
        Set<Entry<String, Object>> environmentsSet = environmentsMap.entrySet();
        for (Entry<String, Object> entrySet : environmentsSet) {
            excEnvMap.put(entrySet.getKey(), (List<String>) entrySet.getValue());
        }
        return excEnvMap;
    }

	@Override
	public List<String> getNamesByEnvironment(String environment) {
		Map<String, Object> environments = cacheRegistry.getMap(PoolConstants.MODEL_EXECUTION_ENVIRONMENTS);
		return (List<String>) environments.get(environment);

	}

}
