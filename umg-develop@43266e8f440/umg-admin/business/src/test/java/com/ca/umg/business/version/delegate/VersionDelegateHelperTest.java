package com.ca.umg.business.version.delegate;

import static org.junit.Assert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionHierarchyInfo;

import ma.glasnost.orika.impl.ConfigurableMapper;

public class VersionDelegateHelperTest {

    @Mock
    private ConfigurableMapper mapper;

    @InjectMocks
    private VersionDelegateHelper classUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void createVersionHierarchyTest() {
        List<Version> versions = buildVersions();
        List<VersionHierarchyInfo> hierarchyInfos = classUnderTest.createVersionHierarchy(versions);
        assertThat(4, Is.is(hierarchyInfos.size()));
    }

    private List<Version> buildVersions() {
        Version version = null;
        ModelLibrary modelLibrary = null;
        Model model = null;
        Mapping mapping = null;
        List<Version> versions = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            version = new Version();
            modelLibrary = new ModelLibrary();
            modelLibrary.setName("LIB_" + i / 5);
            model = new Model();
            model.setName("MODEL_" + i / 3);
            mapping = new Mapping();
            mapping.setModel(model);
            version.setMapping(mapping);
            version.setModelLibrary(modelLibrary);
            version.setMajorVersion(i % 3);
            version.setMinorVersion(0);
            versions.add(version);
        }
        return versions;
    }

}
