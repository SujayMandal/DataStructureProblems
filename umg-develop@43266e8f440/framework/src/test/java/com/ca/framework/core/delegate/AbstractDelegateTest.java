package com.ca.framework.core.delegate;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration

@Ignore
//TODO fix ignored test cases
public class AbstractDelegateTest {

	@Inject
	AbstractDelegate delegate;
	
	@Test
	public final void testConvert() {
		MockSource source = new MockSource();
		source.setName("testName");
		MockDestination dest = delegate.convert(source, MockDestination.class);
		Assert.assertEquals(source.getName(), dest.getName());
	}

	@Test
	public final void testGetMapper() {
		assertNotNull(delegate.getMapper());
	}

}
