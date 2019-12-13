package com.ca.pool.dao;

import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.ca.pool.model.Pool;
import com.ca.pool.model.PoolCriteria;
import com.ca.pool.model.PoolCriteriaDefMapping;
import com.ca.pool.model.PoolUsageOrderMapping;

public class PoolDAOImpl implements PoolDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(PoolDAOImpl.class);

    @Inject
    @Named(value = "dataSource")
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private static final String ALL_POOL_QUERY = "SELECT ID, POOL_NAME, POOL_DESCRIPTION, IS_DEFAULT_POOL, EXECUTION_LANGUAGE, EXECUTION_ENVIRONMENT, MODELET_COUNT, MODELET_CAPACITY, POOL_STATUS, PRIORITY, WAIT_TIMEOUT "
            + "FROM POOL";

    private static final String ALL_POOL_CRITERIA_QUERY = "SELECT ID, CRITERIA_NAME, CRITERIA_PRIORITY FROM POOL_CRITERIA ORDER BY CRITERIA_PRIORITY ASC";

    private static final String ALL_POOL_CRITERIA_MAP_QRY = "SELECT pcd.ID, pcd.POOL_ID, p.POOL_NAME, pcd.POOL_CRITERIA_VALUE FROM POOL_CRITERIA_DEF_MAPPING pcd join POOL p on pcd.POOL_ID = p.ID";

    private static final String ALL_POOL_USAGE_ORDER_QRY = "SELECT puo.ID, puo.POOL_ID, main_pool.POOL_NAME, puo.POOL_USAGE_ID, "
            + "sub_pool.POOL_NAME as 'USAGE_POOL_NAME', puo.POOL_TRY_ORDER "
            + "FROM POOL_USAGE_ORDER puo join POOL main_pool on puo.POOL_ID = main_pool.ID join POOL sub_pool on puo.POOL_USAGE_ID = sub_pool.ID";

    private static final boolean ADMIN_AWARE = true;

    @PostConstruct
    public void initializeTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Pool> loadAllPool() {
        boolean actualAdminAware = getActualAdminAware();
        setAdminAware(ADMIN_AWARE);
        LOGGER.info("FETCH_ALL_POOL_QUERY:" + ALL_POOL_QUERY);

        final List<Pool> poolList = jdbcTemplate.query(ALL_POOL_QUERY, new RowMapper<Pool>() {

            @Override
            public Pool mapRow(ResultSet rs, int rowNum) throws SQLException {
                final Pool pool = new Pool();
                pool.setId(rs.getString("ID"));
                pool.setPoolName(rs.getString("POOL_NAME"));
                pool.setPoolDesc(rs.getString("POOL_DESCRIPTION"));
                pool.setDefaultPool(rs.getInt("IS_DEFAULT_POOL"));
                pool.setExecutionLanguage(rs.getString("EXECUTION_LANGUAGE"));
                pool.setExecutionEnvironment(rs.getString("EXECUTION_ENVIRONMENT"));
                pool.setModeletCount(rs.getInt("MODELET_COUNT"));
                pool.setModeletCapacity(rs.getString("MODELET_CAPACITY"));
                pool.setPoolStatus(rs.getString("POOL_STATUS"));
                pool.setPriority(rs.getInt("PRIORITY"));
                pool.setWaitTimeout(rs.getInt("WAIT_TIMEOUT"));
                pool.setOldWaitTimeout(rs.getInt("WAIT_TIMEOUT"));
                return pool;
            }
        });

        setAdminAware(actualAdminAware);

        return poolList;
    }

    @Override
    public List<PoolCriteria> loadAllPoolCriteria() {
        boolean actualAdminAware = getActualAdminAware();
        setAdminAware(ADMIN_AWARE);
        LOGGER.info("FETCH_ALL_POOL_CRITERIA_QUERY:" + ALL_POOL_CRITERIA_QUERY);

        final List<PoolCriteria> poolCriteriaList = jdbcTemplate.query(ALL_POOL_CRITERIA_QUERY, new RowMapper<PoolCriteria>() {

            @Override
            public PoolCriteria mapRow(ResultSet rs, int rowNum) throws SQLException {
                final PoolCriteria poolCriteria = new PoolCriteria();

                poolCriteria.setId(rs.getString("ID"));
                poolCriteria.setCriteriaName(rs.getString("CRITERIA_NAME"));
                poolCriteria.setCriteriaPriority(rs.getInt("CRITERIA_PRIORITY"));

                return poolCriteria;
            }
        });

        setAdminAware(actualAdminAware);

        return poolCriteriaList;
    }

    @Override
    public List<PoolCriteriaDefMapping> loadAllPoolCriteriaDefMapping() {
        boolean actualAdminAware = getActualAdminAware();
        setAdminAware(ADMIN_AWARE);
        LOGGER.info("FETCH_ALL_POOL_CRITERIA_MAP_QUERY:" + ALL_POOL_CRITERIA_MAP_QRY);

        final List<PoolCriteriaDefMapping> poolCriteriaDefMappingList = jdbcTemplate.query(ALL_POOL_CRITERIA_MAP_QRY,
                new RowMapper<PoolCriteriaDefMapping>() {
                    @Override
                    public PoolCriteriaDefMapping mapRow(ResultSet rs, int rowNum) throws SQLException {
                        final PoolCriteriaDefMapping poolCriteriaDefMapping = new PoolCriteriaDefMapping();
                        poolCriteriaDefMapping.setId(rs.getString("ID"));
                        poolCriteriaDefMapping.setPoolId(rs.getString("POOL_ID"));
                        poolCriteriaDefMapping.setPoolName(rs.getString("POOL_NAME"));
                        poolCriteriaDefMapping.setPoolCriteriaValue(rs.getString("POOL_CRITERIA_VALUE"));
                        return poolCriteriaDefMapping;
                    }
                });

        setAdminAware(actualAdminAware);

        return poolCriteriaDefMappingList;
    }

    @Override
    public List<PoolUsageOrderMapping> loadAllPoolUsageOrderMapping() {
        boolean actualAdminAware = getActualAdminAware();
        setAdminAware(ADMIN_AWARE);
        LOGGER.info("FETCH_ALL_POOL_USAGE_ORDER_QUERY:" + ALL_POOL_USAGE_ORDER_QRY);

        final List<PoolUsageOrderMapping> poolUsageOrderMappingList = jdbcTemplate.query(ALL_POOL_USAGE_ORDER_QRY,
                new RowMapper<PoolUsageOrderMapping>() {

                    @Override
                    public PoolUsageOrderMapping mapRow(ResultSet rs, int rowNum) throws SQLException {
                        final PoolUsageOrderMapping poolUsageOrder = new PoolUsageOrderMapping();
                        poolUsageOrder.setId(rs.getString("ID"));
                        poolUsageOrder.setPoolId(rs.getString("POOL_ID"));
                        poolUsageOrder.setPoolName(rs.getString("POOL_NAME"));
                        poolUsageOrder.setPoolUsageId(rs.getString("POOL_USAGE_ID"));
                        poolUsageOrder.setPoolUsageName(rs.getString("USAGE_POOL_NAME"));
                        poolUsageOrder.setPoolTryOrder(rs.getInt("POOL_TRY_ORDER"));
                        return poolUsageOrder;
                    }
                });

        setAdminAware(actualAdminAware);

        return poolUsageOrderMappingList;
    }

    private void setAdminAware(boolean adminAware) {
        if (getRequestContext() != null) {
            getRequestContext().setAdminAware(adminAware);
        }
    }

    private boolean getActualAdminAware() {
        boolean isAdminAware = false;
        if (getRequestContext() != null) {
            isAdminAware = getRequestContext().isAdminAware();
        }
        return isAdminAware;
    }
}