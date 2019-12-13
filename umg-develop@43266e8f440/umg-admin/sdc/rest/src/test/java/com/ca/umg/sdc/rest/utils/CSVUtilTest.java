package com.ca.umg.sdc.rest.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.syndicatedata.info.SyndicateDataColumnInfo;
import com.ca.umg.business.syndicatedata.info.SyndicateDataVersionInfo;

import junit.framework.Assert;

public class CSVUtilTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVUtilTest.class);
	
	@Test
	public void testGetContainerDefinition() {
		InputStream fis =null;
		SyndicateDataVersionInfo syndDataVersionInfo = null;
		try {
			fis = readFileData("./src/test/resources/testdata/csv/SampleCSV.csv");
			syndDataVersionInfo = CSVUtil.getContainerDefinition(fis, getSyndicateDataColInfo());
		} catch (SystemException | BusinessException e) {
			Assert.fail();
		}
		finally {
			IOUtils.closeQuietly(fis);
		}
		assertNotNull(syndDataVersionInfo);
		assertNotNull(syndDataVersionInfo.getMetaData());
		assertEquals(4, syndDataVersionInfo.getMetaData().size());
	}
	
	private InputStream readFileData(String fileName) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(fileName));
		} catch (FileNotFoundException e) {
			LOGGER.error("FileNotFoundException: ", e);
		}
		finally {
			IOUtils.closeQuietly(fis);
		}
		return fis;
    }
	
	private List<SyndicateDataColumnInfo> getSyndicateDataColInfo() {
		List<SyndicateDataColumnInfo> syndColInfoList = new ArrayList<SyndicateDataColumnInfo>();
		syndColInfoList.add(buildColInfo("FIP", "FIP", "string", 100, true));
		syndColInfoList.add(buildColInfo("Month", "Month", "DATE", 0, true));
		syndColInfoList.add(buildColInfo("HPI", "HPI", "DOUBLE", 0, true));
		syndColInfoList.add(buildColInfo("HPA", "HPA", "DOUBLE", 0, true));
		return syndColInfoList;
	}
	
	private SyndicateDataColumnInfo buildColInfo(String displayName, String desc, String colType, int colSize, boolean isMandatory) {
		SyndicateDataColumnInfo colInfo = new SyndicateDataColumnInfo();
		colInfo.setDisplayName(displayName);
		colInfo.setDescription(desc);
		colInfo.setColumnType(colType);
		colInfo.setColumnSize(colSize);
		colInfo.setMandatory(isMandatory);
		return colInfo;
	}

}
