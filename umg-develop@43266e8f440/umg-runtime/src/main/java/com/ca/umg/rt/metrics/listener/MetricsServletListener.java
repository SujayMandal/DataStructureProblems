/*
 * MetricsServletListener.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.rt.metrics.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlet.InstrumentedFilterContextListener;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;

/**
 * 
 * **/
public class MetricsServletListener
	implements ServletContextListener
{
	private MetricRegistry metricRegistry;
	private HealthCheckRegistry healthCheckRegistry;
	private final MetricsServletContextListener            metricsServletContextListener       = new MetricsServletContextListener();
	private final HealthCheckServletContextListener        healthCheckServletContextListener   = new HealthCheckServletContextListener();
	private final MetricsInstrumentedFilterContextListener myInstrumentedFilterContextListener = new MetricsInstrumentedFilterContextListener();

	/**
	 * Retrieve {@link MetricRegistry} bean and {@link HealthCheckRegistry} spring context and initialize i Metric listeners.
	 *
	 * @param event DOCUMENT ME!
	 **/
	@Override
	public void contextInitialized(ServletContextEvent event)
	{
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
		metricRegistry      = wac.getBean(MetricRegistry.class);
		healthCheckRegistry = wac.getBean(HealthCheckRegistry.class);
		metricsServletContextListener.contextInitialized(event);
		healthCheckServletContextListener.contextInitialized(event);
		myInstrumentedFilterContextListener.contextInitialized(event);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param event DOCUMENT ME!
	 **/
	@Override
	public void contextDestroyed(ServletContextEvent event)
	{
	}

	class MetricsServletContextListener
		extends MetricsServlet.ContextListener
	{
		@Override
		protected MetricRegistry getMetricRegistry()
		{
			return metricRegistry;
		}
	}

	class HealthCheckServletContextListener
		extends HealthCheckServlet.ContextListener
	{
		@Override
		protected HealthCheckRegistry getHealthCheckRegistry()
		{
			return healthCheckRegistry;
		}
	}

	class MetricsInstrumentedFilterContextListener
		extends InstrumentedFilterContextListener
	{
		@Override
		protected MetricRegistry getMetricRegistry()
		{
			return metricRegistry;
		}
	}
}
