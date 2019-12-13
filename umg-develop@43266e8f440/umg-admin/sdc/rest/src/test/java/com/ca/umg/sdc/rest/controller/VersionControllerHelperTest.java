package com.ca.umg.sdc.rest.controller;

import static junit.framework.Assert.fail;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.ca.framework.core.exception.BusinessException;
import com.ca.umg.business.version.info.CreateVersionInfo;
import com.ca.umg.business.version.info.VersionInfo;

public class VersionControllerHelperTest {

    private VersionControllerHelper classUnderTest;

    @Test
    public void mapToVersionInfo() throws BusinessException {
        CreateVersionInfo input = new CreateVersionInfo();
        input.setTenantModelName("tenant model name");
        input.setTenantModelDescription("tenant model description");
        input.setMajorVersion(3);
        input.setVersionDescription("version description");
        input.setLibraryRecord("library umg name");
        input.setTidName("TID name");
        input.setVersionType("minor");

        VersionInfo versionInfo = classUnderTest.mapToVersionInfo(input);

        assertThat(versionInfo.getName(), is("tenant model name"));
        assertThat(versionInfo.getDescription(), is("tenant model description"));
        assertThat(versionInfo.getMajorVersion(), is(3));
        assertThat(versionInfo.getVersionDescription(), is("version description"));
        assertThat(versionInfo.getModelLibrary().getUmgName(), is("library umg name"));
        assertThat(versionInfo.getMapping().getName(), is("TID name"));
    }

    @Test
    public void mapToVersionInfoCreatingMinorVersionAndMajorIsEmpty() throws BusinessException {
        CreateVersionInfo input = new CreateVersionInfo();
        input.setVersionType("minor");

        try {
            classUnderTest.mapToVersionInfo(input);
            fail("Business Exception should be thrown");
        } catch (BusinessException e) {
            assertThat(e.getCode(), is("BSE000073"));
        }

    }

}
