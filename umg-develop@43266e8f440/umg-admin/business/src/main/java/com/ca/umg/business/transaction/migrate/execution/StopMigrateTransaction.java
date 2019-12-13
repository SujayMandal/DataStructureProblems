package com.ca.umg.business.transaction.migrate.execution;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class StopMigrateTransaction {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(StopMigrateTransaction.class);
	
	private Boolean stopMigration = Boolean.FALSE;

	public Boolean getStopMigration() {
		return stopMigration;
	}

	public void setStopMigration(Boolean stopMigration) {
		LOGGER.info("Setting the stop migration flag in StopMigrateTransaction::setStopMigration with value : "+stopMigration);
		this.stopMigration = stopMigration;
	}

}
