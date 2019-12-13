package com.ca.framework.core.systemparameter;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.db.persistance.CAAbstractRoutingDataSource;

public class SystemParameterProviderImpl implements SystemParameterProvider {
    private Logger logger = LoggerFactory
            .getLogger(SystemParameterProviderImpl.class);
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
        List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT T.SYS_KEY, T.SYS_VALUE FROM SYSTEM_PARAMETER T WHERE T.IS_ACTIVE='Y'");
        if(CollectionUtils.isNotEmpty(result)) {
            for(Map<String, Object> row: result) {
                cacheRegistry.getMap(SYSTEM_PARAMETER).put(
                        row.get("SYS_KEY"), row.get("SYS_VALUE"));
            }
        }
    }

    private String getParameterFromDB(String key) throws SQLException {
        List<Map<String, Object>> result = jdbcTemplate.queryForList("SELECT T.SYS_VALUE FROM SYSTEM_PARAMETER T WHERE T.IS_ACTIVE='Y' AND T.SYS_KEY = ?", new Object[]{key});
        return CollectionUtils.isNotEmpty(result) ? (String)result.get(0).get("SYS_VALUE") : null;
    }

    @Override
    public String getParameter(String key) {
        Object value = cacheRegistry.getMap(SYSTEM_PARAMETER).get(key);
        if (value == null) {
            try {
                logger.info("getting param from db for key : "+key);
                value = this.getParameterFromDB(key);
                if (value != null) {
                    logger.info("adding value from db to cache : "+value);
                    cacheRegistry.getMap(SYSTEM_PARAMETER).put(key, value);
                }
            } catch (SQLException e) {
                logger.error("Error occured during getting parameter",e);
            }
        }
        return value == null ? null : value.toString();
    }

    @Override
    public void refreshCache() {
        init() ;
    }
}
