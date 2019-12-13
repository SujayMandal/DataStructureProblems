package com.ca.umg.rt.modelet.sync.scheduler;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.ca.umg.rt.modelet.sync.processor.ModeletPoolSyncProcessor;


/**
 * This scheduler run after certain configured time interval to sync all
 * modelets.
 * 
 * @author yogeshku
 *
 */
public class ModeletPoolSyncScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModeletPoolSyncScheduler.class);

	@Inject
	private ModeletPoolSyncProcessor modeletPoolSyncProcessor;

	@Value("${modelet.enable.schedule}")
	private Boolean enableScheduling;

	public void schedule() {
		if (enableScheduling) {
			LOGGER.info("Scheduling started for synchronisation of modelets with respect to cache.");
			modeletPoolSyncProcessor.synchronizeModelets();
		}
	}
}
