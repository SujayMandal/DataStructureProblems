/*
 * SyndicateDataDAOTest.java
 *
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics 
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.business.syndicatedata.dao;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.umg.business.BaseTest;
import com.ca.umg.business.syndicatedata.entity.SyndicateData;

import junit.framework.Assert;

/**
 * Test case for SyndicateDataDAO.
 * 
 * @author mandavak
 *
 */

@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
public class SyndicateDataDAOTest extends BaseTest {

    SyndicateData syndicateData = null;

    /**
     * Destroy method to call after test case execution.
     */
    @After
    public void destroy() {
        getSyndicateDataDAO().deleteAllInBatch();
    }

    /**
     * Set up data before creating db.
     */
    @Before
    public void setup() {
        syndicateData = createSyndicateData("REO", "REO1", 250L, "REO_TAB", "version1", "version1 desc", 1388514600l, 1391106600l);
        createSyndicateData("REO", "REO2", 251L, "REO_TAB", "version2", "version2 desc", 1391193000l, 1393525800l);
        createSyndicateData("REO", "REO3", 252L, "REO_TAB", "version3", "version3 desc", 1393612200l, 1396204200l);
        createSyndicateData("REO", "REO4", 253L, "REO_TAB", "version4", "version4 desc", 1396290600l, null);
    }

    /**
     * getMinMaxVerContainers method test case
     */
    @Test
    public void getMinVerContainersTest() {
    	getSyndicateDataDAO().deleteAll();
    	createSyndicateData("REO", "REO1", 250L, "REO_TAB", "version1", "version1 desc", 1388514600l, 1391106600l);
        System.out.println("1234567890");
        List<SyndicateData> syndicateDatas = getSyndicateDataDAO().getMinVersionOfEachContainer();
        Assert.assertNotNull(syndicateDatas);
        Assert.assertEquals(1, syndicateDatas.size());
        SyndicateData syndicateData = syndicateDatas.get(0);
        Assert.assertNotNull(syndicateData);
        Assert.assertEquals(syndicateData.getContainerName(), "REO");
        Assert.assertNotSame(Long.valueOf(253), syndicateData.getVersionId());      
    }

    /**
     * findByVersionIdAndContainerName method test case
     */
    @Test
    public void findByVersionIdAndContainerNameTest() {
        SyndicateData syndicateData = getSyndicateDataDAO().findByVersionIdAndContainerName(250L, "REO");
        Assert.assertNotNull(syndicateData);
        Assert.assertEquals(syndicateData.getValidFrom(), new Long(1388514600));
        Assert.assertFalse(syndicateData.getValidTo().equals(new Long("1393525800")));
    }

    /**
     * findByContainerName method test case
     */
    @Test
    public void findByContainerNameTest() {
        List<SyndicateData> syndicateDatas = getSyndicateDataDAO().findByContainerNameOrderByVersionIdDesc("REO");
        Assert.assertNotNull(syndicateDatas);
        SyndicateData syndicateData = syndicateDatas.get(0);
        Assert.assertNotNull(syndicateData);
        Assert.assertEquals(syndicateData.getContainerName(), "REO");
    }

    /**
     * findById method test case
     */
    @Test
    public void findByIdTest() {
        SyndicateData dataSyndicate = getSyndicateDataDAO().findById(syndicateData.getId());
        Assert.assertNotNull(dataSyndicate);
        Assert.assertEquals(Long.valueOf(250), dataSyndicate.getVersionId());
        Assert.assertTrue(dataSyndicate.getContainerName().equals("REO"));
    }

    @Test
    public void findFirstProviderVersionTest() {
        SyndicateData syndicateData = getSyndicateDataDAO().findFirstProviderVersion("REO");
        Assert.assertEquals(250l, syndicateData.getVersionId().longValue());
    }

    @Test
    public void findProviderMaxVersionTest() {
        // Long version = getSyndicateDataDAO().findProviderMaxVersion("REO");
        SyndicateData syndicateData = getSyndicateDataDAO().findProviderMaxVersion("REO");
        Assert.assertNotNull(syndicateData);
        Assert.assertNotNull(syndicateData.getVersionId());
        Assert.assertEquals(253l, syndicateData.getVersionId().longValue());
    }

    @Test
    public void findByContainerNameAndVersionIdGreaterThanTest() {
        Pageable top = new PageRequest(0, 1);
        List<SyndicateData> list = getSyndicateDataDAO().findByContainerNameAndVersionIdGreaterThanOrderByVersionIdAsc("REO",
                252l, top);
        Assert.assertEquals(253l, (list.get(0).getVersionId()).longValue());
    }

    @Test
    public void findByContainerNameAndVersionIdLessThanTest() {
        Pageable top = new PageRequest(0, 1);
        List<SyndicateData> list = getSyndicateDataDAO().findByContainerNameAndVersionIdLessThanOrderByVersionIdDesc("REO", 252l,
                top);
        Assert.assertEquals(251l, (list.get(0).getVersionId()).longValue());
    }
}
