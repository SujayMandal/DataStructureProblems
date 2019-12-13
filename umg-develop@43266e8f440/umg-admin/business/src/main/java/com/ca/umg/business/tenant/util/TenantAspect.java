/**
 * 
 */
package com.ca.umg.business.tenant.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

import com.ca.framework.core.requestcontext.RequestContext;

/**
 * @author kamathan
 *
 */
@Order(1)
@Aspect
public class TenantAspect {

    @Around("execution(* com.ca.umg.business.tenant.delegate.TenantDelegate.*(..))")
    public Object makeAdminAware(ProceedingJoinPoint joinPoint) throws Throwable {
        RequestContext context = RequestContext.getRequestContext();
        if (context != null) {
            context.setAdminAware(true);
        }
        try {
            return joinPoint.proceed();
        } finally {
            if (context != null) {
                context.setAdminAware(false);
            }
        }
    }
}
