package com.ca.umg.business.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.version.command.error.Error;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CSVUtilTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(CSVUtilTest.class);

	@Before
	public void setup() {

	}

	@Test
	public void testreadManifestFileWithoutErrors() {
		InputStream fis = null;
		List<Error> errors = new ArrayList<Error>();
		try {
			fis = readFileData("./src/test/resources/R-Manifest.csv");
			CSVUtil.readManifestFile(IOUtils.toByteArray(fis), errors);
			Assert.assertTrue(errors.size() == 0);
		} catch (SystemException e) {
			LOGGER.error("SystemException: ", e);
			Assert.fail();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("IOException: ", e);
		} finally {
			IOUtils.closeQuietly(fis);
		}

	}

	@Test
	public void testreadManifestFileWithWrongHierarchy() {
		InputStream fis = null;
		List<Error> errors = new ArrayList<Error>();
		try {
			fis = readFileData("./src/test/resources/R-ManifestWithWrongHierarchy.csv");
			CSVUtil.readManifestFile(IOUtils.toByteArray(fis), errors);
			Assert.assertTrue(errors.size() == 1);
		} catch (SystemException e) {
			Assert.fail();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("IOException: ", e);
		} finally {
			IOUtils.closeQuietly(fis);
		}

	}

	@Test
	public void testreadManifestFileWithWrongHeader() {
		InputStream fis = null;
		List<Error> errors = new ArrayList<Error>();
		try {
			fis = readFileData("./src/test/resources/R-ManifestWithWrongHeader.csv");
			CSVUtil.readManifestFile(IOUtils.toByteArray(fis), errors);
			Assert.assertTrue(errors.size() == 1);
		} catch (SystemException e) {
			LOGGER.error("SystemException: ", e);
			Assert.fail();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("IOException: ", e);
		} finally {
			IOUtils.closeQuietly(fis);
		}

	}

	private InputStream readFileData(String fileName) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(fileName));
		} catch (FileNotFoundException e) {
			LOGGER.error("IOException: ", e);
		} finally {
			IOUtils.closeQuietly(fis);
		}

		return fis;
	}

}
