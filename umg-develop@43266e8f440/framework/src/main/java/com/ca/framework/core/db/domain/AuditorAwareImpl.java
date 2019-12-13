package com.ca.framework.core.db.domain;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Implementation for dynamically finding currently logged in user.
 * 
 * @author Anil Kamath
 * 
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth==null?"SYSTEM":auth.getName();
    }
}