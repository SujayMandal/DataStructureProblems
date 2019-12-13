package com.ca.framework.core.monitoring.core;

import java.util.concurrent.Callable;


public interface MonitoringTask extends Callable<Object> {

	public void set ( Monitorable component );
	
	
}
