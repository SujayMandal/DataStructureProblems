package com.ca.umg.business.dbauth.dao;

import static com.ca.framework.core.exception.BusinessException.newBusinessException;
import static com.ca.umg.business.dbauth.UMGUserStatus.getUMGUserStatus;
import static com.ca.umg.business.dbauth.dao.UMGUserQuery.FIND_USER_QUERY;
import static com.ca.umg.business.dbauth.dao.UMGUserQuery.UPDATE_PASSWORD_QUERY;
import static com.ca.umg.business.exception.codes.BusinessExceptionCodes.BSE0000510;
import static com.ca.umg.business.exception.codes.BusinessExceptionCodes.BSE0000511;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.dbauth.UMGUser;

@SuppressWarnings("PMD")
@Repository
public class UMGUserDaoImpl implements UMGUserDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(UMGUserDaoImpl.class);

	@Inject
	@Named(value = "dataSource")
	private DataSource dataSource;

	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void initializeTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public UMGUser findUser(final String username, final String tenantCode) throws BusinessException {
		UMGUser user = null;
		try {
			LOGGER.debug("Query to fecth user based on username:" + FIND_USER_QUERY);
			user = jdbcTemplate.execute(FIND_USER_QUERY, new PreparedStatementCallback<UMGUser>() {
				@Override
				public UMGUser doInPreparedStatement(final PreparedStatement ps) throws SQLException {
					try {
						ps.setString(1, username);
						ps.setString(2, tenantCode);
						return createUMGUser(ps.executeQuery());
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

		return user;
	}

	@Override
	public void updatePassword(final String username, final String tenantCode, final String newPassword) throws BusinessException {
		Boolean updateStatus = FALSE;
		try {
			LOGGER.debug("Query to update user password:" + UPDATE_PASSWORD_QUERY);
			updateStatus = jdbcTemplate.execute(UPDATE_PASSWORD_QUERY, new PreparedStatementCallback<Boolean>() {
				@Override
				public Boolean doInPreparedStatement(final PreparedStatement ps) throws SQLException {
					try {
						ps.setString(1, newPassword);
						ps.setString(2, username);
						ps.setString(3, tenantCode);
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

	private UMGUser createUMGUser(final ResultSet rs) throws SQLException {
		UMGUser user = null;
		if (rs != null) {
			try {
				rs.next();
				if (rs.getRow() > 0) {
					user = new UMGUser();
					user.setUsername(rs.getString("username"));
					user.setPassword(rs.getString("PASSWORD"));
					user.setEnabled(rs.getInt("enabled"));
					user.setUserStatus(getUMGUserStatus(rs.getInt("enabled")));
					user.setTenantCode(rs.getString("TENANT_CODE"));
					user.setName(rs.getString("name"));
					user.setOfficialEmail(rs.getString("official_email"));
					user.setOrganization(rs.getString("organization"));
					user.setComments(rs.getString("comments"));
					user.setCreatedDate(rs.getLong("created_on"));
					user.setLastActivatedDate(rs.getLong("last_activated_on"));
					user.setLastDeactivatedDate(rs.getLong("last_deactivated_on"));
					user.setRole(rs.getString("role"));
				} else {
					LOGGER.debug("Login User Details: No user found");
				}
			} catch (SQLException sqle) {
				LOGGER.debug("User exists, but has some problem");
				LOGGER.debug(sqle.getMessage());
				throw sqle;
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException sqle) {
						LOGGER.debug("Not able to close the Result set");
					}
				}
			}
		}

		return user;
	}
}