/**
 * 
 */
package com.ca.framework.core.cache.registry.impl;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.hazelcast.core.IQueue;

/**
 * @author kamathan
 *
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)

@Ignore
//TODO fix ignored test cases
public class CacheRegistryTest {

    @Inject
    private CacheRegistry cacheRegistry;

    /**
     * Test method for {@link com.ca.framework.core.cache.registry.impl.DefaultCacheRegistry#getCache()}.
     */
    @Test
    public final void testGetCache() {
        IQueue<Object> cache = cacheRegistry.getDistributedQueue();
        assertNotNull(cache);
    }
}
