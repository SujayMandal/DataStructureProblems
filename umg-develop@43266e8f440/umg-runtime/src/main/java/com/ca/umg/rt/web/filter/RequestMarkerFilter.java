package com.ca.umg.rt.web.filter;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class RequestMarkerFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestMarkerFilter.class);
    public static final String TRANSACTION_ID = "TRANSACTION_ID";

    @Override
    public void destroy() {
        LOGGER.info("destroy method is called.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
            ServletException {
        MDC.put(TRANSACTION_ID, UUID.randomUUID().toString());
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TRANSACTION_ID);
        }
    }

    @Override
    public void init(FilterConfig filterChain) throws ServletException {
        LOGGER.info("init method is called.");
    }

}