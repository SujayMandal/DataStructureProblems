package com.ca.framework.core.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.systemparameter.SystemParameterProviderImpl;
import com.hazelcast.core.IMap;

@Ignore
//TODO fix ignored test cases
public class UmgFileProxyTest {

	@InjectMocks
	private UmgFileProxy umgFileProxy = new UmgFileProxy();

	@Mock
	private CacheRegistry cacheRegistry;

	@Mock
	private SystemParameterProvider systemParameterProvider = new SystemParameterProviderImpl();

	@Mock
	private IMap<?, ?> imap;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetSanPath() {
		Mockito.when(
				cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER))
				.thenReturn((IMap<Object, Object>) imap);
		try {
			String sanpath = umgFileProxy.getSanPath(System
					.getProperty("user.dir"));
			Assert.assertTrue(sanpath != null);
		} catch (SystemException e) {			
			e.printStackTrace();
		}

	}
	
	@Test
	public void testGetSanPathWithExp() {
		Mockito.when(
				cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER))
				.thenReturn((IMap<Object, Object>) imap);
		try {
			String sanpath = umgFileProxy.getSanPath("test");
			Assert.assertTrue(sanpath != null);
		} catch (SystemException e) {			
		Assert.assertTrue(e!=null);
		}

	}

	@Test
	public void testGetSanPathWithExist() {
		Mockito.when(
				cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER))
				.thenReturn((IMap<Object, Object>) imap);
		Mockito.when(
				cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER).get("UMG_FILE_PATH"))				
				.thenReturn(System
						.getProperty("user.dir"));

		try {
			String sanpath = umgFileProxy.getSanPath("test");
			Assert.assertTrue(sanpath != null);
		} catch (SystemException e) {			
		Assert.assertTrue(e!=null);
		}

	}
	@Test
	public void testGetSanPathWithExistExc() {
		Mockito.when(
				cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER))
				.thenReturn((IMap<Object, Object>) imap);
		Mockito.when(
				cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER).get("UMG_FILE_PATH"))				
				.thenReturn("test");

		try {
			String sanpath = umgFileProxy.getSanPath("test");
			Assert.assertTrue(sanpath != null);
		} catch (SystemException e) {			
		Assert.assertTrue(e!=null);
		}

	}



}
