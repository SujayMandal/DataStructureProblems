package com.ca.framework.core.delegate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirstUtil {
    public static final Logger LOG = LoggerFactory.getLogger(FirstUtil.class.getName());

    public void testOne() {
        String kumar = "The great";
        kumar = "Testing";
        LOG.info(kumar);
    }

    public void testTwo() {
        String kumar = "The great";
        kumar = "Testing";
        LOG.info(kumar);
    }

}
