package com.ca.umg.business.hazelcaststats.delegate;

import java.util.Map;

public interface HazelCastStatusDelegate {

	/**
	 * gets all the entries from the hazelcast
	 * 
	 * @return
	 */
	public Map<Object, Object> getAllHazelCastEntries();

}
