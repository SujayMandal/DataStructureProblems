package com.ca.umg.business.accessprivilege.bo;

import static java.util.Locale.ENGLISH;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.altisource.iam.dto.RFUserDetails;
import com.altisource.iam.dto.TenantAuthzProfile;
import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.accessprivilege.Privileges;
import com.ca.umg.business.accessprivilege.dao.AccessPrivilegeDAO;
import com.ca.umg.business.accessprivilege.dao.SwitchTenantDAO;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.dbauth.UMGUserDetails;
import com.ca.umg.business.util.AdminUtil;

@Named
@SuppressWarnings({"PMD.CyclomaticComplexity"})
public class SwitchTenantBOImpl implements SwitchTenantBO {
    
    private static final Logger LOGGER = getLogger(SwitchTenantBOImpl.class);
    
    private static final String TENANT_CODE = "tenantCode";
    private static final String USER_NAME = "userName";
    private static final String ROLE_LIST = "roleList";
    private static final String TENANT_LIST = "tenantList";
    private static final String SYS_ADMIN = "sysAdmin";
    
    @Inject 
    private SwitchTenantDAO switchTenantDAO;
    
    @Inject 
    private AccessPrivilegeDAO accessPrivilegeDAO;
    
    @Inject
    private CacheRegistry cacheRegistry;

    @Override
    public void switchAndSetTenant(String tenantCode, HttpServletRequest request) throws SystemException {
        LOGGER.error("Switch tenant started for tenant Code: " + tenantCode);
        UserDetails userDetails = null;
        RFUserDetails rfUserDetails =(RFUserDetails) request.getSession().getAttribute(BusinessConstants.RF_USER_DETAILS);
        if (rfUserDetails != null) {
            LOGGER.error("Switch tenant started for RF tenant Code: " + tenantCode);
            userDetails = getSwitchedUmgUserDetails(rfUserDetails, tenantCode);
            LOGGER.error("Switch tenant ended for RF tenant Code: " + tenantCode);
        } else {
            LOGGER.error("Switch tenant started for DB tenant Code: " + tenantCode);
            userDetails = switchTenantDAO.switchAndSetTenant(tenantCode);
            LOGGER.error("Switch tenant ended for DB tenant Code: " + tenantCode);
        }
        
        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        getContext().setAuthentication(token);
        LOGGER.error("Switch tenant ended for tenant Code: " + tenantCode);
    }
    
    private UserDetails getSwitchedUmgUserDetails (RFUserDetails rfUserDetails, String tenantCode) throws SystemException {
        UserDetails umgUserDetails = null;
        Map<String, Object> rfTenantObj = getTenantObjMapFromRF(rfUserDetails, tenantCode);
        umgUserDetails = getUmgUserDetailsObj (rfTenantObj);
        return umgUserDetails;
    } 
    
    @Override
    public UserDetails getUmgUserDetails (RFUserDetails rfUserDetails, String tenantCode) throws SystemException {
        UserDetails umgUserDetails = null;
        try {
            LOGGER.debug("Changing the RF auth Object to umg auth object");
            Map<String, Object> rfTenantMap = getTenantObjMapFromRF(rfUserDetails, tenantCode);
            umgUserDetails = getUmgUserDetailsObj (rfTenantMap);
        } catch (SystemException e) {
            LOGGER.error("Error occured while getting the data for RF : {} ",e);
            throw e;
        }
        return umgUserDetails;
    }
    
    /**
     * gets the first rf object alphabetically sorted from auth principal  
     * @param rfUserDetails
     * @return
     * @throws SystemException 
     */
    private Map<String, Object> getTenantObjMapFromRF(RFUserDetails rfUserDetails, String tenantCodeToChange) throws SystemException {
        LOGGER.debug("Getting the Tenant Object Map from RF auth Object started");
        String tenantCode = "";
        Map<String, Object> rfTenantMap = new HashMap<>();

        Collection<TenantAuthzProfile> tenantAuthzProfiles = rfUserDetails.getAuthzToken().getTenantAuthzProfiles();
        if (CollectionUtils.isNotEmpty(tenantAuthzProfiles)) {
            List<String> tenantList = new ArrayList<>();
            Map<String, Object> rfTntMap = new HashMap<>();
            for (TenantAuthzProfile tenantAuthzProfile : tenantAuthzProfiles) {
                String tntname = tenantAuthzProfile.getTenantName();
                tntname = StringUtils.lowerCase(tntname, ENGLISH);
                tenantList.add(tntname);
                rfTntMap.put(tntname, tenantAuthzProfile);
            }
            Collections.sort(tenantList);
            if (tenantCodeToChange != null) {
                tenantCode = tenantCodeToChange;
            } else {
                tenantCode = CollectionUtils.isNotEmpty(tenantList) ? tenantList.get(BusinessConstants.NUMBER_ZERO) : "";
            }
            LOGGER.debug("Tenant name from tenantAuthzProfiles of RF auth object is {}", tenantCode);
            rfTenantMap.put(TENANT_CODE, tenantCode);
            rfTenantMap.put(USER_NAME, rfUserDetails.getUsername());
            List<String> roleList = null;
            if (MapUtils.isNotEmpty(rfTntMap) && rfTntMap.get(tenantCode) != null) {
                roleList = new ArrayList<>(((TenantAuthzProfile)rfTntMap.get(tenantCode)).getRoles());
            }
            rfTenantMap.put(ROLE_LIST, roleList);
            rfTenantMap.put(TENANT_LIST, tenantList);
        } else {
            if (tenantCodeToChange != null) {
                tenantCode = tenantCodeToChange;
            } else {
                tenantCode = rfUserDetails.getAuthzToken().getBranding();
            }
            LOGGER.debug("Tenant name from tenantAuthzToken of RF auth object is {}", tenantCode);
            rfTenantMap.put(TENANT_CODE, tenantCode);
            rfTenantMap.put(USER_NAME, rfUserDetails.getUsername());
            rfTenantMap.put(ROLE_LIST, null);
            rfTenantMap.put(TENANT_LIST, null);
        }
        
        LOGGER.debug("UserName from RF of logged in user is : {} ",rfUserDetails.getUsername());
        rfTenantMap.put(SYS_ADMIN, Boolean.FALSE);
        //over writing the tenant list with all tenants from cache if sys admin and
        // over writing the tenant code if it is not switching tenant
        if (isUserSystemAdmin(rfUserDetails.getUsername())) {
            LOGGER.debug("Logged in user is sys admin");
            List<String> tntList = getSortedTntListFromCache();
            rfTenantMap.put(TENANT_LIST, tntList);
            if (tenantCodeToChange == null) {
                rfTenantMap.put(TENANT_CODE, tntList.get(BusinessConstants.NUMBER_ZERO));
            }
            rfTenantMap.put(SYS_ADMIN, Boolean.TRUE);
        } 
        
        checkIfTntExistsInList(tenantCodeToChange, rfTenantMap);
        LOGGER.debug("Tenant Object Map from RF auth Object is : {}",rfTenantMap);
        return rfTenantMap;
    }
    
    private void checkIfTntExistsInList (String tenantCodeToChange, Map<String, Object> rfTenantMap ) {
      //checking if tenantCodeToChange is present in tenant list
        if (tenantCodeToChange != null) { 
            Boolean tntDoesNotExist = Boolean.FALSE;
            if (rfTenantMap.get(TENANT_LIST) != null ) {
                List<String> tntList = (List<String>)rfTenantMap.get(TENANT_LIST);
                if (CollectionUtils.isEmpty(tntList) || !tntList.contains(tenantCodeToChange)) {
                    LOGGER.error("Tenant list {} does not contain tenantCode to be Changed {} for the user",tntList, tenantCodeToChange);
                    tntDoesNotExist = Boolean.TRUE;
                }
            } else {
                LOGGER.error("Tenant List is empty so user is not mapped to tenantCode to be Changed {} for switching",tenantCodeToChange);
                tntDoesNotExist = Boolean.TRUE;
            }
            if (tntDoesNotExist) {
                throw new UsernameNotFoundException("There was an error with your Username/Password combination. Please try again");
            }
        }
    }
    
    
    /**
     * returns a new {@link UMGUserDetails} object
     * @param rfTenantMap
     * @return
     * @throws SystemException 
     */
    private UserDetails getUmgUserDetailsObj (Map<String, Object> rfTenantMap) throws SystemException {
        LOGGER.debug("Tenant Object Map to get umg userdetails is : {}",rfTenantMap);
        UserDetails umgUserDetails = null;
        String username = (String) rfTenantMap.get(USER_NAME);
        String tenantCode = (String) rfTenantMap.get(TENANT_CODE);
        List<String> roleList = (ArrayList<String>) rfTenantMap.get(ROLE_LIST);
        String commaRoleList = null;
        String commaPermList = null;
        Boolean sysAdmin = (Boolean) rfTenantMap.get(SYS_ADMIN);
        List<Privileges> pagePrivilegesList = null;
        List<Privileges> actionPrivilegesList = null;
        Boolean actualAdminAware = AdminUtil.getActualAdminAware();
        try {
            if (sysAdmin) {
                pagePrivilegesList = accessPrivilegeDAO.getPagesPrivilegesList();
                actionPrivilegesList = accessPrivilegeDAO.getActionPrivilegesList();
            }
            
            if (!sysAdmin) {
                if (CollectionUtils.isNotEmpty(roleList)) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (String role : roleList) {
                        stringBuffer.append(role).append(BusinessConstants.CHAR_COMMA);
                    }
                    commaRoleList = stringBuffer.toString();
                    commaRoleList = StringUtils.substringBeforeLast(commaRoleList, BusinessConstants.CHAR_COMMA);
                }
                LOGGER.debug("Role list to be searched is : {} for user : {} ",commaRoleList,username);
                AdminUtil.setAdminAwareTrue();
                if (CollectionUtils.isNotEmpty(roleList) && StringUtils.isNotBlank(tenantCode)) { 
                    commaPermList = accessPrivilegeDAO.getPrivilegesForRFTntRoles(roleList, tenantCode);
                }
            }
            
            LOGGER.debug("Permission list to be searched is : {} for user : {} ",commaPermList,username);
            
            umgUserDetails = new UMGUserDetails(username, StringUtils.EMPTY, BusinessConstants.NUMBER_ONE, tenantCode, 
                    commaRoleList, commaPermList, sysAdmin, pagePrivilegesList, actionPrivilegesList);
            
            if (rfTenantMap.get(TENANT_LIST) != null) {
                List<String> tntList = (List<String>)rfTenantMap.get(TENANT_LIST);
                ((UMGUserDetails) umgUserDetails).setTenantList(tntList);
            }
        } catch (SystemException e) {
            LOGGER.error("Error occured while getting the data for RF : {} ",e);
            throw e;
        } finally {
        	AdminUtil.setActualAdminAware(actualAdminAware);
        }
        return umgUserDetails;
    }
    
    private List<String> getSortedTntListFromCache () {
        List<String> tenantList = new ArrayList<>();
        for(Object tntCode : cacheRegistry.getMap(FrameworkConstant.TENANT_MAP).keySet()) {
            tenantList.add(StringUtils.lowerCase((String)tntCode, ENGLISH));
        }
        Collections.sort(tenantList);
        return tenantList;
    }
    
    private Boolean isUserSystemAdmin (String userName) throws SystemException {
        Boolean isSysAdmin = Boolean.FALSE;
        Boolean actualAdminAware = AdminUtil.getActualAdminAware();
        try {
        	AdminUtil.setAdminAwareTrue();
            List<String> rfUsersList = accessPrivilegeDAO.getRfUsersList();
            LOGGER.debug("Rf users list is :  {}",rfUsersList);
            if (rfUsersList.contains(userName)) {
                LOGGER.debug("Setting the sysadmin flag to true for rf user : {}", userName);
                isSysAdmin = Boolean.TRUE;
            }
        } finally {
        	AdminUtil.setActualAdminAware(actualAdminAware);
        }
        return isSysAdmin;
    }

}
