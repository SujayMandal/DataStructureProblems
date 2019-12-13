package com.ca.umg.rt.metrics.reporter;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

public class ReporterTest {

	@Test
	public void fakeReporters() throws Throwable {
		ClassPathXmlApplicationContext ctx = null;
		HazelCastReporter one = null;
		try {
			ctx = new ClassPathXmlApplicationContext("classpath:com/ca/umg/rt/metrics/reporter/reporter-test.xml");
			ctx.start();
			Thread.sleep(1000);
			one = ctx.getBean("hazelCastReporter", HazelCastReporter.class);
			Assert.notNull(one);
		}
		finally {
			if (ctx != null) {
				ctx.stop();
				ctx.close();
			}
		}
	}	

}
