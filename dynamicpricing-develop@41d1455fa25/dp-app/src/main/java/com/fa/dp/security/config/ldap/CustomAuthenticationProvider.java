package com.fa.dp.security.config.ldap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

/**
 * Handles authentication of the user against the Active Directory.
 */
@Named
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationProvider.class);
    /**
     * domain to search the user
     */
    @Value("${domain}")
    private String domain;

    /**
     * holds ldap url to connect to for authentication
     */
    @Value("${ldapUrl}")
    private String ldapUrl;

    /**
     * Custom user details mapper for mapping the custom role
     */
    @Inject
    private UserDetailsMapper userDetailsMapper;

    private ActiveDirectoryLdapAuthenticationProvider provider;

    @PostConstruct
    public void init() {
        LOGGER.info("Initializing Custom LDAP Provider with LDAP server {} and domain name {}.", ldapUrl, domain);
        provider = new ActiveDirectoryLdapAuthenticationProvider(domain, ldapUrl);
        provider.setSearchFilter("(sAMAccountName={1})");
        provider.setUserDetailsContextMapper(userDetailsMapper);
        provider.setConvertSubErrorCodesToExceptions(true);
    }

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        Authentication authenticated = null;
        authenticated = provider
                .authenticate(new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials()));
        LOGGER.info("Authentication of user {} {}.", authenticated.getPrincipal(),
                authenticated.isAuthenticated() ? "success" : "failed");
        return authenticated;
    }

    @Override
    public boolean supports(Class<?> arg0) {
        return true;
    }
}
