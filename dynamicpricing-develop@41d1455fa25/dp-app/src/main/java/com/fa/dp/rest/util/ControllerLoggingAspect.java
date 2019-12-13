package com.fa.dp.rest.util;

import com.fa.dp.business.constant.DPAConstants;
import com.fa.dp.business.week0.info.DashboardFilterInfo;
import com.fa.dp.core.systemparam.util.AppType;
import com.fa.dp.core.util.RAClientConstants;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class ControllerLoggingAspect {

    //@Around("execution(* com.javasampleapproach.log4j2.controller.*.*(..))") -- used for all the classes in controller package future usage
    @Around("execution(* com.fa.dp.rest.DPFileProcessingController.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object value = null;
        for(Object argument : joinPoint.getArgs()) {
            if (argument instanceof String) {
                String productType = (String) argument;
                if(productMatchAndAddToMdc(productType)){
                    break;
                } else {
                    continue;
                }
            } else if (argument instanceof DashboardFilterInfo) {
                DashboardFilterInfo dashboardFilterInfo = (DashboardFilterInfo) argument;
                if(productMatchAndAddToMdc(dashboardFilterInfo.getWeekType())){
                    break;
                }
            }
        }
        value = joinPoint.proceed();
        MDC.remove(RAClientConstants.PRODUCT_TYPE);
        return value;
    }

    private Boolean productMatchAndAddToMdc(String productType) {
        Boolean matched = Boolean.FALSE;
        if(StringUtils.equalsAnyIgnoreCase(productType,DPAConstants.WEEK0)
                || StringUtils.equalsAnyIgnoreCase(productType,DPAConstants.WEEKN)){
            MDC.put(RAClientConstants.PRODUCT_TYPE, productType);
            matched = Boolean.TRUE;
        }
        return matched;
    }
}
