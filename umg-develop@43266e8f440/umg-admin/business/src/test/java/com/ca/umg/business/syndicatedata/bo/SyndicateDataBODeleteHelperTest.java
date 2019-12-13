package com.ca.umg.business.syndicatedata.bo;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.business.syndicatedata.dao.SyndicateDataDAO;
import com.ca.umg.business.syndicatedata.dao.SyndicateVersionDataDAO;
import com.ca.umg.business.syndicatedata.entity.SyndicateData;

public class SyndicateDataBODeleteHelperTest {

    @Mock
    private SyndicateDataDAO mockSyndicateDataDAO;

    @Mock
    private SyndicateVersionDataDAO mockSyndicateVersionDataDAO;

    @InjectMocks
    private SyndicateDataBODeleteHelper classUnderTest = new SyndicateDataBODeleteHelper();
    
    @Mock
    private SystemParameterProvider systemParameterProvider;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void deleteLastRemainingVersion() throws BusinessException, SystemException {
        List<SyndicateData> versions = new ArrayList<SyndicateData>();
        SyndicateData version1 = new SyndicateData();
        version1.setTableName("someTable");
        version1.setVersionId(1L);
        versions.add(version1);
        when(mockSyndicateDataDAO.findByContainerNameOrderByVersionIdDesc("name")).thenReturn(versions);

        classUnderTest.delete("name", 1L);

        verify(mockSyndicateDataDAO, times(0)).save(Mockito.any(SyndicateData.class));
        verify(mockSyndicateVersionDataDAO).deleteVersionData("someTable", 1L);
        verify(mockSyndicateVersionDataDAO).dropTable("someTable");
        verify(mockSyndicateDataDAO).delete(version1);
    }

    @Test
    public void deleteLastVersionInSetOfVersions() throws BusinessException, SystemException {
        List<SyndicateData> versions = new ArrayList<SyndicateData>();
        SyndicateData version1 = new SyndicateData();
        version1.setTableName("someTable");
        version1.setVersionId(3L);
        versions.add(version1);
        SyndicateData version2 = new SyndicateData();
        version2.setTableName("someTable");
        version2.setVersionId(2L);
        versions.add(version2);
        SyndicateData version3 = new SyndicateData();
        version3.setTableName("someTable");
        version3.setVersionId(1L);
        versions.add(version3);
        when(mockSyndicateDataDAO.findByContainerNameOrderByVersionIdDesc("name")).thenReturn(versions);

        classUnderTest.delete("name", 3L);

        verify(mockSyndicateDataDAO, times(0)).save(Mockito.any(SyndicateData.class));
        verify(mockSyndicateVersionDataDAO).deleteVersionData("someTable", 3L);
        verify(mockSyndicateVersionDataDAO, times(0)).dropTable("someTable");
        verify(mockSyndicateDataDAO).delete(version1);
    }

    @Test
    public void deleteFirstVersionInSetOfVersions() throws BusinessException, SystemException {
        List<SyndicateData> versions = new ArrayList<SyndicateData>();
        SyndicateData version1 = new SyndicateData();
        version1.setTableName("someTable");
        version1.setVersionId(3L);
        versions.add(version1);
        SyndicateData version2 = new SyndicateData();
        version2.setTableName("someTable");
        version2.setVersionId(2L);
        versions.add(version2);
        SyndicateData version3 = new SyndicateData();
        version3.setTableName("someTable");
        version3.setVersionId(1L);
        versions.add(version3);
        when(mockSyndicateDataDAO.findByContainerNameOrderByVersionIdDesc("name")).thenReturn(versions);

        classUnderTest.delete("name", 1L);

        verify(mockSyndicateDataDAO, times(0)).save(Mockito.any(SyndicateData.class));
        verify(mockSyndicateVersionDataDAO).deleteVersionData("someTable", 1L);
        verify(mockSyndicateVersionDataDAO, times(0)).dropTable("someTable");
        verify(mockSyndicateDataDAO).delete(version3);
    }

    @Test
    public void deleteVersionInMiddleInSetOfVersions() throws BusinessException, SystemException {
        List<SyndicateData> versions = new ArrayList<SyndicateData>();
        SyndicateData version1 = new SyndicateData();
        version1.setTableName("someTable");
        version1.setVersionId(3L);
        versions.add(version1);
        SyndicateData version2 = new SyndicateData();
        version2.setTableName("someTable");
        version2.setVersionId(2L);
        version2.setValidTo(1234567L);
        versions.add(version2);
        SyndicateData version3 = new SyndicateData();
        version3.setTableName("someTable");
        version3.setVersionId(1L);
        versions.add(version3);
        when(mockSyndicateDataDAO.findByContainerNameOrderByVersionIdDesc("name")).thenReturn(versions);

        classUnderTest.delete("name", 2L);

        assertThat(version3.getValidTo(), is(1234567L));
        verify(mockSyndicateDataDAO).save(version3);
        verify(mockSyndicateVersionDataDAO).deleteVersionData("someTable", 2L);
        verify(mockSyndicateVersionDataDAO, times(0)).dropTable("someTable");
        verify(mockSyndicateDataDAO).delete(version2);
    }

}
