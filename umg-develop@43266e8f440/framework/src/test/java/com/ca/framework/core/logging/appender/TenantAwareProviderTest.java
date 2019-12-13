/**
 * 
 */
package com.ca.framework.core.logging.appender;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author kamathan
 * 
 */
public class TenantAwareProviderTest {

    @Test
    public void testNoProvider() {
        final TenantAwareAppender appender = TenantAwareAppender.createAppender("myName01", null, null, null, null);
        Assert.assertNull("The appender should be null.", appender);
    }

}
