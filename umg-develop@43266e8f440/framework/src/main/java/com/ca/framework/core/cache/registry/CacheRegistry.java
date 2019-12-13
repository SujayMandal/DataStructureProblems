

/**
 * 
 */
package com.ca.framework.core.cache.registry;

import java.util.concurrent.locks.Lock;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;

/**
 * @author kamathan
 *
 */
public interface CacheRegistry {

	public static final String DEFAULT_POOL_NAME = "DEFAULT_POOL";
	
	public static final String UMG_PROPERTIES_MAP = "UMG_PROPERTIES_MAP";
	
	public static final String BATCH_TERMINATE_MAP = "BATCH_TERMINATE_MAP";
	
	/**
	 * Returns distributed blocking queue instance
	 * 
	 * @return
	 */
	IQueue<Object> getDistributedQueue();

	    /**
     * Get distributed map.
     * 
     * @param <K>
     * @param <V>
     * 
     * @param name
     * @return
     */
    <K, V> IMap<K, V> getMap(String name);

	/**
	 * Get distributed topic.
	 * 
	 * @param name
	 * @return
	 */
	ITopic<Object> getTopic(String name);

	Integer getMemberPort();
	
	public ISet<Object> getSet(final String name);
	
	public IList<Object> getList(final String name);
	
	public IQueue<Object> getDistributedPoolQueue(final String name);

	public Lock getDistributedLock(final String key);

    public String getMemberAddress();
    
    public HazelcastInstance getHazelcastInstance();
	

}
