package com.ca.umg.business.dbauth.dao;

import static com.ca.framework.core.exception.BusinessException.newBusinessException;
import static com.ca.umg.business.dbauth.dao.UMGUserQuery.INSERT_USER_LOG_ACTIVITY;
import static com.ca.umg.business.dbauth.dao.UMGUserQuery.LOCK_USER_QUERY;
import static com.ca.umg.business.dbauth.dao.UMGUserQuery.SELECT_TOP_3_USER_ACTIVITIES;
import static com.ca.umg.business.exception.codes.BusinessExceptionCodes.BSE0000510;
import static com.ca.umg.business.exception.codes.BusinessExceptionCodes.BSE0000511;
import static com.ca.umg.business.exception.codes.BusinessExceptionCodes.BSE0000512;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.slf4j.LoggerFactory.getLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.dbauth.UserLoginActivity;
import com.ca.umg.business.dbauth.UserLoginAudit;

@SuppressWarnings("PMD")
@Repository
public class UMGUserLoginActivityDaoImpl implements UMGUserLoginActivityDao {

	private static final Logger LOGGER = getLogger(UMGUserLoginActivityDaoImpl.class);

	@Inject
	@Named(value = "dataSource")
	private DataSource dataSource;

	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void insertUserLoginActivity(final UserLoginAudit loginAudit) throws BusinessException {
		Boolean insertStatus = FALSE;
		try {
			LOGGER.debug("Query to insert user login activity:" + INSERT_USER_LOG_ACTIVITY);
			insertStatus = jdbcTemplate.execute(INSERT_USER_LOG_ACTIVITY, new PreparedStatementCallback<Boolean>() {
				@Override
				public Boolean doInPreparedStatement(final PreparedStatement ps) throws SQLException {
					try {
						ps.setString(1, loginAudit.getUsername());
						ps.setString(2, loginAudit.getTenantCode());
						ps.setString(3, loginAudit.getIpAddress());
						ps.setLong(4, loginAudit.getAccessTime());
						ps.setString(5, loginAudit.getActivity().getActivity());
						ps.setString(6, loginAudit.getReasonCode());
						final int rowCount = ps.executeUpdate();
						if (rowCount == 1) {
							return TRUE;
						} else {
							return FALSE;
						}
					} catch (SQLException sqle) {
						throw sqle;
					}
				}
			});

		} catch (DataAccessException dae) {
			LOGGER.debug(dae.getMessage());
			Object[] arguments = new Object[1];
			arguments[0] = loginAudit.getUsername();
			throw newBusinessException(BSE0000512, arguments, dae);
		}

		if (insertStatus == FALSE) {
			Object[] arguments = new Object[1];
			arguments[0] = loginAudit.getUsername();
			throw newBusinessException(BSE0000512, arguments);
		}
	}

	@Override
	public List<UserLoginAudit> getTop3Activies(final String username, final String tenantCode) throws BusinessException {
		List<UserLoginAudit> userActivityList = null;
		try {
			LOGGER.debug("Query to fecth user activities based on username:" + SELECT_TOP_3_USER_ACTIVITIES);
			userActivityList = jdbcTemplate.execute(SELECT_TOP_3_USER_ACTIVITIES, new PreparedStatementCallback<List<UserLoginAudit>>() {
				@Override
				public List<UserLoginAudit> doInPreparedStatement(final PreparedStatement ps) throws SQLException {
					try {
						ps.setString(1, username);
						ps.setString(2, tenantCode);
						return createUserActivies(ps.executeQuery());
					} catch (SQLException sqle) {
						throw sqle;
					}
				}
			});

		} catch (DataAccessException dae) {
			LOGGER.debug(dae.getMessage());
			Object[] arguments = new Object[1];
			arguments[0] = username;
			throw newBusinessException(BSE0000510, arguments, dae);
		}

		return userActivityList;
	}

	private List<UserLoginAudit> createUserActivies(final ResultSet rs) throws SQLException {
		List<UserLoginAudit> userActivies = new ArrayList<UserLoginAudit>();
		try{
			if (rs != null) {
				try {
					while (rs.next()) {
						if (rs.getRow() > 0) {
							final UserLoginAudit userActivity = new UserLoginAudit();
							userActivity.setUsername(rs.getString("username"));
							userActivity.setIpAddress(rs.getString("sys_ip_address"));
							userActivity.setAccessTime(rs.getLong("access_on"));
							userActivity.setReasonCode(rs.getString("reason_code"));
							userActivity.setTenantCode(rs.getString("tenant_code"));
							userActivity.setActivity(UserLoginActivity.getUserLoginActivity(rs.getString("activity")));
							userActivies.add(userActivity);
						}
					}
				} catch (SQLException sqle) {
					LOGGER.debug("User exists, but has some problem");
					LOGGER.debug(sqle.getMessage());
					throw sqle;
				} finally {
					if (rs != null) {
							rs.close();
					}
				}
				}
			}catch (SQLException sqle) {
				LOGGER.debug("Not able to close the Result set");
				throw sqle;
			}

		return userActivies;
	}

	@Override
	public void lockUser(final String username, final String tenantCode) throws BusinessException {
		Boolean updateStatus = FALSE;
		try {
			LOGGER.debug("Query to lock user :" + LOCK_USER_QUERY);
			updateStatus = jdbcTemplate.execute(LOCK_USER_QUERY, new PreparedStatementCallback<Boolean>() {
				@Override
				public Boolean doInPreparedStatement(final PreparedStatement ps) throws SQLException {
					try {
						ps.setString(1, username);
						ps.setString(2, tenantCode);
						final int rowCount = ps.executeUpdate();
						if (rowCount == 1) {
							return TRUE;
						} else {
							return FALSE;
						}
					} catch (SQLException sqle) {
						throw sqle;
					}
				}
			});

		} catch (DataAccessException dae) {
			LOGGER.debug(dae.getMessage());
			Object[] arguments = new Object[1];
			arguments[0] = username;
			throw newBusinessException(BSE0000511, arguments, dae);
		}

		if (updateStatus == FALSE) {
			Object[] arguments = new Object[1];
			arguments[0] = username;
			throw newBusinessException(BSE0000511, arguments);
		}
	}
}