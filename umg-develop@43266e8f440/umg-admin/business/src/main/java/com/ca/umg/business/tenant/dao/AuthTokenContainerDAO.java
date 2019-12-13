package com.ca.umg.business.tenant.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.ca.framework.core.encryption.EncryptionUtil;
import com.ca.framework.core.info.tenant.AuthTokenInfo;
import com.ca.framework.core.info.tenant.TenantInfo;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.util.AdminUtil;

/**
 * @author basanaga
 *
 */
@Repository

public class AuthTokenContainerDAO {

    private static final String AUTHTOKENS_FOR_TENANT = "SELECT * FROM AUTHTOKEN WHERE TENANT_ID=? ORDER BY CREATED_ON ASC";
    private static final String ACTIVE_AUTHTOKEN = "SELECT * FROM AUTHTOKEN WHERE TENANT_ID=? AND STATUS='Active'"; 
    private static final String GET_TENANT_CODE = "SELECT CODE FROM TENANT WHERE ID=?";

    @Inject
    @Named(value = "dataSource")
    private DataSource dataSource;


    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initializeTemplate() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

   
    /**
     * This method used to get the all authtokens for the tenantId
     * @param tenantId
     * @return
     */
    public List<AuthTokenInfo> getAllAuthTokensForTenantId(String tenantId) {
        return jdbcTemplate.query(AUTHTOKENS_FOR_TENANT, new Object[] { tenantId },new AuthTokenRowMapper());
    }

    
    /**
     * This method used to get active authtoken
     * @param tenantId
     * @return
     */
    public   List<AuthTokenInfo> getActiveAuthToken(String tenantId) {
        return jdbcTemplate.query(ACTIVE_AUTHTOKEN, new Object[] { tenantId },
                new AuthTokenRowMapper());

    }
    
    /**
     * This method used to get the all authtokens for the tenantId
     * @param tenantId
     * @return
     */
    public String getTenantCode(String tenantId) {
        return jdbcTemplate.queryForObject(GET_TENANT_CODE, new Object[] { tenantId },String.class);
    }

  

}

/**
 * @author basanaga
 *
 */
class AuthTokenRowMapper implements RowMapper<AuthTokenInfo> {
    @Override
    public AuthTokenInfo mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        final AuthTokenInfo info = new AuthTokenInfo();
        info.setId(rs.getString("ID"));
        TenantInfo tenantInfo = new TenantInfo();        
        tenantInfo.setId(rs.getString("TENANT_ID"));
        info.setTenantInfo(tenantInfo);        
        info.setActiveFrom(rs.getLong("ACTIVE_FROM"));
        info.setAuthCode(EncryptionUtil.decryptToken(rs.getString("AUTH_CODE")));        
        info.setActiveUntil(rs.getLong("ACTIVE_UNTIL"));
        info.setStatus(rs.getString("STATUS"));
        info.setComment(rs.getString("COMMENT"));
        info.setCreatedDate(new DateTime(rs.getLong("CREATED_ON")));
        info.setLastModifiedDate(new DateTime(rs.getLong("LAST_UPDATED_ON")));
        info.setCreatedBy(rs.getString("CREATED_BY"));
        info.setLastModifiedBy(rs.getString("LAST_UPDATED_BY"));
        info.setCreatedDateTime(AdminUtil.getDateFormatMillisForEst(info.getCreatedDate().getMillis(), null));
        if (info.getActiveFrom() != BusinessConstants.NUMBER_ZERO_LONG) {
            info.setActiveFromStr(AdminUtil.getDateFormatMillisForEst(info.getActiveFrom(), null));
        }
        if (info.getActiveUntil() != BusinessConstants.NUMBER_ZERO_LONG) {
                info.setActiveUntilStr(AdminUtil.getDateFormatMillisForEst(info.getActiveUntil(), null));
        }
        return info;
    }
}