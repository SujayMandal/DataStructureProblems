package com.ca.sdc.webui.core.filter;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecurityHeadersInterceptor  extends HandlerInterceptorAdapter {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception { //NOPMD

        //response.setHeader("Strict-Transport-Security","max-age=31536000 ; includeSubDomains");
        // response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        //response.setHeader("X-XSS-Protection", "1; mode=block");
        //response.setHeader("Content-Security-Policy", "default-src 'self'");

        super.postHandle(request, response, handler, modelAndView);
    }

}
