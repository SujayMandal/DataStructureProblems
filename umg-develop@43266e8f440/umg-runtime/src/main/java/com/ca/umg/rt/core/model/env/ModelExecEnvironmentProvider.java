package com.ca.umg.rt.core.model.env;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.PoolConstants;
import com.ca.framework.core.exception.SystemException;
import com.ca.pool.model.ExecutionLanguage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.FatalBeanException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Named
public class ModelExecEnvironmentProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModelExecEnvironmentProvider.class);

	private static final String GET_EXECUTION_ENVIRONMENTS = "SELECT DISTINCT(MEE.EXECUTION_ENVIRONMENT) ENVIRONMENT FROM MODEL_EXECUTION_ENVIRONMENTS MEE";
	private static final String GET_EXEC_ENVIRONMENT_NAMES = "SELECT DISTINCT(NAME) NAME FROM MODEL_EXECUTION_ENVIRONMENTS MEE WHERE MEE.EXECUTION_ENVIRONMENT=?";
	private static final String GET_R_ACTIVE_EXEC_ENVIRONMENT = "SELECT NAME FROM MODEL_EXECUTION_ENVIRONMENTS MEE WHERE MEE.IS_ACTIVE='T' AND MEE.EXECUTION_ENVIRONMENT='R'";

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
			if(StringUtils.equals(environment, ExecutionLanguage.R.getValue())) {
				cacheRegistry.getMap(PoolConstants.ACTIVE_EXECUTION_ENVIRONMENTS)
						.put(StringUtils.upperCase(environment), getRActiveExecEnvironments());
			} else {
				cacheRegistry.getMap(PoolConstants.MODEL_EXECUTION_ENVIRONMENTS).put(environment, getNames(environment));
				cacheRegistry.getMap(PoolConstants.ACTIVE_EXECUTION_ENVIRONMENTS)
						.put(StringUtils.upperCase(environment), getNames(environment));
			}
		}
		LOGGER.info("Finished building environments.");
	}

	private List<String> getEnvironments() {
		List<String> environments = new ArrayList();
		try {
			environments = jdbcTemplate.queryForList(GET_EXECUTION_ENVIRONMENTS, String.class);
		} catch (DataAccessException ex) {
			LOGGER.error("exception while getting execution environments.Exception is ", ex);
			new FatalBeanException("", ex);
		}
		return environments;
	}

	private List<String> getNames(String environment) {
		List<String> names = new ArrayList();
		try {
			names = jdbcTemplate.queryForList(GET_EXEC_ENVIRONMENT_NAMES, new Object[] {environment}, String.class);
		} catch (DataAccessException ex) {
			LOGGER.error("exception while getting execution environments.Exception is ", ex);
			new FatalBeanException("", ex);
		}
		return names;

	}

	private List<String> getRActiveExecEnvironments() {
		List<String> names = new ArrayList();
		try {
			names = jdbcTemplate.queryForList(GET_R_ACTIVE_EXEC_ENVIRONMENT, String.class);
		} catch (DataAccessException ex) {
			LOGGER.error("exception while getting execution environments.Exception is ", ex);
			new FatalBeanException("", ex);
		}
		return names;
	}

}
