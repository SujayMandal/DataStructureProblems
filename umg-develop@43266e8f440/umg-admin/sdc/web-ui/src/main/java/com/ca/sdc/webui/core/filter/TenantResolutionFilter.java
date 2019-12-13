package com.ca.sdc.webui.core.filter;

import static com.ca.umg.business.constants.BusinessConstants.MDC_USERNAME;
import static com.ca.umg.report.service.ReportService.RA_REPORT_REQUEST_MAPPING;
import static com.ca.umg.report.service.ReportService.RA_REPORT_REQUEST_MAPPING_API;
import static com.ca.umg.sdc.rest.controller.HomeController.isAdminRole;
import static java.lang.Boolean.valueOf;
import static java.util.Locale.ENGLISH;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;

import com.altisource.iam.dto.RFUserDetails;
import com.altisource.iam.dto.TenantAuthzProfile;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.logging.appender.AppenderConstants;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.umg.business.accessprivilege.Privileges;
import com.ca.umg.business.accessprivilege.dao.AccessPrivilegeDAO;
import com.ca.umg.business.accessprivilege.delegate.SwitchTenantDelegate;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.dbauth.UMGUserDetails;
import com.ca.umg.notification.NotificationConstants;
import com.ca.umg.notification.NotificationServiceBO;

@SuppressWarnings("PMD")
public class TenantResolutionFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantResolutionFilter.class);
    private String staticPagePrivilegeList = null;
    private String staticActionPrivilegeList = null;

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String tenantURL = httpServletRequest.getRequestURL().toString();
        if (tenantURL.contains("/version/getVersionDetails/") || tenantURL.contains("/api/v1.0/search")
                || tenantURL.contains(RA_REPORT_REQUEST_MAPPING + RA_REPORT_REQUEST_MAPPING_API)
                || tenantURL.contains(NotificationConstants.RA_MODEL_APP_REDIRECT)
                || tenantURL.contains("/version/getTenantIODefinition/")) {
            filterChain.doFilter(request, response);
        } else {
            try {
                final String tenantCode = getTenantCode(httpServletRequest);
                LOGGER.debug("Tenant code after TenantResolutionFilter is :: {}", tenantCode);
                Properties properties = new Properties();
                properties.put(RequestContext.TENANT_URL, tenantURL);
                properties.put(RequestContext.TENANT_CODE, tenantCode);
                RequestContext requestContext = new RequestContext(properties);
                httpServletRequest.setAttribute("TENANT_CODE", tenantCode);
                getStaticPrivilegeList();
                changeRfToUmgAuthInSession(httpServletRequest);
                httpServletRequest.setAttribute("TENANT_LIST", getTenantList());
                httpServletRequest.setAttribute("PAGEACCESSLIST", staticPagePrivilegeList);
                httpServletRequest.setAttribute("ACTIONACESSLIST", staticActionPrivilegeList);
                httpServletRequest.setAttribute("IS_ADMIN_ROLE", valueOf(isAdminRole()));
                httpServletRequest.setAttribute("IS_USER_SYSADMIN", getIsSysAdmin());
                httpServletRequest.setAttribute("IS_DB_AUTHENTICATION", valueOf(isDBAuthentication()));
                httpServletRequest.setAttribute("IS_NOTIFICATION_ENABLED", isNotificationNeeded(tenantCode));
                MDC.put(AppenderConstants.MDC_TENANT_CODE, requestContext.getTenantCode());
                filterChain.doFilter(request, response);
                if (RequestContext.getRequestContext() != null) {
                    RequestContext.getRequestContext().destroy();
                }
                MDC.clear();
            } catch (SystemException e) {
                LOGGER.error("An Error occured : {}", e);
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "An error occured please contact system administrator");
            }
        }
    }

    /**
     * sets the umguserdetails in session object and gets backup of rfuserdetails in a different session variable
     * 
     * @param request
     * @throws SystemException
     */
    private void changeRfToUmgAuthInSession(HttpServletRequest request) throws SystemException {
        if (request.getSession() != null && request.getSession().getAttribute("SPRING_SECURITY_CONTEXT") != null) {
            SecurityContext securityContext = (SecurityContext) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT");
            if (securityContext.getAuthentication() != null
                    && securityContext.getAuthentication().getPrincipal() instanceof RFUserDetails) {
                RFUserDetails rfUserDetails = (RFUserDetails) securityContext.getAuthentication().getPrincipal();
                LOGGER.debug("Started the conversion to umg auth object from RF Auth Object : {} ", rfUserDetails);
                // setting the rf auth object in a different variable in session
                request.getSession().setAttribute(BusinessConstants.RF_USER_DETAILS, rfUserDetails);
                UserDetails umgUserDetails = null;
                if (getCurrentWebApplicationContext() != null) {
                    SwitchTenantDelegate switchTenantDelegate = getCurrentWebApplicationContext()
                            .getBean(SwitchTenantDelegate.class);
                    umgUserDetails = switchTenantDelegate.getUmgUserDetails(rfUserDetails, null);
                }
                if (umgUserDetails != null) {
                    LOGGER.debug("umgUserDetails Auth Object to be set in session is {}", umgUserDetails);
                    final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(umgUserDetails,
                            null, umgUserDetails.getAuthorities());
                    ((SecurityContext) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT")).setAuthentication(token);
                    String tenantCode = ((UMGUserDetails) umgUserDetails).getTenantCode();
                    request.setAttribute("TENANT_CODE", tenantCode);
                    RequestContext.getRequestContext().setTenantCode(tenantCode);
                    LOGGER.debug("End of conversion to umg auth object from RF Auth Object :");
                }
            }
        }
    }

    /**
     * returns the boolean value true is sysAdmin else false
     * 
     * @return
     */
    private Boolean getIsSysAdmin() {
        final Authentication auth = getContext().getAuthentication();
        Boolean isUserSystemAdmin = Boolean.FALSE;
        if (auth != null) {
            LOGGER.info("Auth is Not null");
            final Object userOBject = auth.getPrincipal();
            if (userOBject instanceof UMGUserDetails) {
                UMGUserDetails details = (UMGUserDetails) userOBject;
                isUserSystemAdmin = details.getIsSysAdmin();
                LOGGER.debug("The logged in user is sysAdmin : {}", isUserSystemAdmin);
            }
        }
        return isUserSystemAdmin;
    }

    /**
     * gets the static page and action {@link Privileges} list used in ui for mapping elements to permissions
     * 
     * @throws SystemException
     */
    private void getStaticPrivilegeList() throws SystemException {
        if (StringUtils.isEmpty(staticPagePrivilegeList) || StringUtils.isEmpty(staticActionPrivilegeList)) {
            if (getCurrentWebApplicationContext() != null) {
                AccessPrivilegeDAO accessPrivilegeDAO = getCurrentWebApplicationContext().getBean(AccessPrivilegeDAO.class);
                List<Privileges> pagePrivilegesList = accessPrivilegeDAO.getPagesPrivilegesList();
                List<Privileges> actionPrivilegesList = accessPrivilegeDAO.getActionPrivilegesList();
                try {
                    staticPagePrivilegeList = ConversionUtil.convertToJsonString(pagePrivilegesList);
                    staticActionPrivilegeList = ConversionUtil.convertToJsonString(actionPrivilegesList);
                } catch (SystemException e) {
                    LOGGER.error("An Error occured while getting static privilege lists: {}", e);
                    throw e;
                }
            }
        }
    }

    private String getTenantCodeFromRF(final RFUserDetails rfUserDetails) {
        LOGGER.info("Auth is not null and RF User details available.");
        LOGGER.info("Tenant Code is getting from RF User Details");
        String tenantCode = "";

        Collection<TenantAuthzProfile> tenantAuthzProfiles = rfUserDetails.getAuthzToken().getTenantAuthzProfiles();
        if (CollectionUtils.isNotEmpty(tenantAuthzProfiles)) {
            List<String> tenantList = new ArrayList<>();
            for (TenantAuthzProfile tenantAuthzProfile : tenantAuthzProfiles) {
                tenantList.add(StringUtils.lowerCase(tenantAuthzProfile.getTenantName(), ENGLISH));
            }
            Collections.sort(tenantList);
            tenantCode = tenantList.get(BusinessConstants.NUMBER_ZERO);
            LOGGER.info("Tenant name from RF is {}", tenantCode);
        } else {
            tenantCode = rfUserDetails.getAuthzToken().getBranding();
        }

        tenantCode = tenantCode.toLowerCase(ENGLISH);
        MDC.put(MDC_USERNAME, rfUserDetails.getUsername());
        return tenantCode;
    }

    private String getTenantCodeFromLocal(final UMGUserDetails umgUserDetails) {
        LOGGER.info("Auth is not null and UMG details available.");
        LOGGER.info("Tenant Code is getting from UMG User Details");
        MDC.put(MDC_USERNAME, umgUserDetails.getUsername());
        return umgUserDetails.getTenantCode() != null ? umgUserDetails.getTenantCode().toLowerCase(ENGLISH) : "";
    }

    private boolean isDBAuthentication() {
        final Authentication auth = getContext().getAuthentication();
        boolean value = false;
        if (auth != null) {
            LOGGER.info("Auth is Not null");
            final Object userOBject = auth.getPrincipal();

            if (userOBject instanceof UMGUserDetails) {
                value = true;
            }
        }

        return value;

    }

    private String getTenantCode(HttpServletRequest httpServletRequest) {
        String tenantCode = "";
        final Authentication auth = getContext().getAuthentication();
        if (auth != null) {
            LOGGER.info("Auth is Not null");
            Object userOBject = auth.getPrincipal();

            if (userOBject instanceof UMGUserDetails) {
                tenantCode = getTenantCodeFromLocal((UMGUserDetails) auth.getPrincipal());
            } else if (userOBject instanceof RFUserDetails) {
                if (httpServletRequest.getSession().getAttribute("tenant_code_from_RF") == null) {
                    tenantCode = getTenantCodeFromRF((RFUserDetails) auth.getPrincipal());
                    httpServletRequest.getSession().setAttribute("tenant_code_from_RF", tenantCode);
                } else {
                    tenantCode = (String) httpServletRequest.getSession().getAttribute("tenant_code_from_RF");
                }
            } else {
                LOGGER.info("Auth is not null and UMG or RF User details are NOT available.");
                LOGGER.info("Tenant code is not avaiable");
            }
        }

        return tenantCode;
    }

    private List<String> getTenantList() {
        List<String> tntList = null;
        final Authentication auth = getContext().getAuthentication();
        if (auth != null) {
            Object userOBject = auth.getPrincipal();
            if (userOBject instanceof UMGUserDetails) {
                UMGUserDetails umgUserDetails = (UMGUserDetails) userOBject;
                tntList = umgUserDetails.getTenantList();
            }
        }
        return tntList;
    }

    @Override
    public void init(final FilterConfig filterChain) throws ServletException {
        LOGGER.info("init method is called.");
    }

    public static String getClientsIPAddr(final HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (null != ip && !"".equals(ip.trim()) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("X-Forwarded-For");
        if (null != ip && !"".equals(ip.trim()) && !"unknown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    private boolean isNotificationNeeded(final String tenantCode) {
        boolean flag = false;
        if (!StringUtils.isEmpty(tenantCode)) {
            try {
                if (getCurrentWebApplicationContext() != null) {
                    NotificationServiceBO notificationServiceBO = getCurrentWebApplicationContext()
                            .getBean(NotificationServiceBO.class);
                    flag = notificationServiceBO.isNotificationEnabled(tenantCode);
                }
            } catch (SystemException | BusinessException e) {
                flag = false;
            }
        }

        return flag;
    }

}
