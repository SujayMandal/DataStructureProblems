package com.ca.umg.business.validation;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.version.bo.VersionBO;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionInfo;

@Ignore
// TODO fix ignored test cases
public class CreateVersionValidatorTest {

    @Mock
    private VersionBO mockVersionBO;

    @InjectMocks
    private CreateVersionValidator classUnderTest = new CreateVersionValidator();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void validateMappingCannotBeNull() {
        List<ValidationError> errors = new ArrayList<>();
        VersionInfo version = new VersionInfo();
        version.setName("name");
        version.setDescription("description");
        ModelLibraryInfo modelLibrary = new ModelLibraryInfo();
        modelLibrary.setUmgName("umg name");
        version.setModelLibrary(modelLibrary);
        version.setVersionDescription("version description");

        classUnderTest.validateAnnotions(version, errors);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getMessage(), is("Mapping cannot be empty"));
    }

    @Test
    public void validateModelLibraryCannotBeNull() {
        List<ValidationError> errors = new ArrayList<>();
        VersionInfo version = new VersionInfo();
        version.setName("name");
        version.setDescription("description");
        MappingInfo mapping = new MappingInfo();
        mapping.setName("mapping name");
        version.setMapping(mapping);
        version.setVersionDescription("version description");

        classUnderTest.validateAnnotions(version, errors);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getMessage(), is("Model library cannot be empty"));
    }

    @Test
    public void validateTenantModelNameNull() throws BusinessException, SystemException {
        List<ValidationError> errors = new ArrayList<>();
        VersionInfo version = new VersionInfo();
        version.setName(null);
        version.setDescription("description");
        MappingInfo mapping = new MappingInfo();
        mapping.setName("mapping name");
        version.setMapping(mapping);
        ModelLibraryInfo modelLibrary = new ModelLibraryInfo();
        modelLibrary.setUmgName("umg name");
        version.setModelLibrary(modelLibrary);
        version.setVersionDescription("version description");

        classUnderTest.validateAnnotions(version, errors);

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getMessage(), is("Tenant model name cannot be empty"));
    }

    @Test
    public void validateTenantModelNameGreaterThan50Chars() {
        List<ValidationError> errors = new ArrayList<>();
        VersionInfo version = new VersionInfo();
        version.setName("012345678901234567890123456789012345678901234567890");
        version.setDescription("description");
        MappingInfo mapping = new MappingInfo();
        mapping.setName("mapping name");
        version.setMapping(mapping);
        ModelLibraryInfo modelLibrary = new ModelLibraryInfo();
        modelLibrary.setUmgName("umg name");
        version.setModelLibrary(modelLibrary);
        version.setVersionDescription("version description");

        classUnderTest.validateAnnotions(version, errors);

        assertThat(errors.get(0).getMessage(), is("Tenant model name can be maximum 50 characters"));
    }

    @Test
    public void validateTenantModelDescriptionNull() {
        List<ValidationError> errors = new ArrayList<>();
        VersionInfo version = new VersionInfo();
        version.setName("name");
        version.setDescription(null);
        MappingInfo mapping = new MappingInfo();
        mapping.setName("mapping name");
        version.setMapping(mapping);
        ModelLibraryInfo modelLibrary = new ModelLibraryInfo();
        modelLibrary.setUmgName("umg name");
        version.setModelLibrary(modelLibrary);
        version.setVersionDescription("version description");

        classUnderTest.validateAnnotions(version, errors);

        assertThat(errors.get(0).getMessage(), is("Tenant model description cannot be empty"));
    }

    @Test
    public void validateTenantModelDescriptionGreaterThan200Chars() {
        List<ValidationError> errors = new ArrayList<>();
        VersionInfo version = new VersionInfo();
        version.setName("name");
        version.setDescription("012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
        MappingInfo mapping = new MappingInfo();
        mapping.setName("mapping name");
        version.setMapping(mapping);
        ModelLibraryInfo modelLibrary = new ModelLibraryInfo();
        modelLibrary.setUmgName("umg name");
        version.setModelLibrary(modelLibrary);
        version.setVersionDescription("version description");

        classUnderTest.validateAnnotions(version, errors);

        assertThat(errors.get(0).getMessage(), is("Tenant model description can be maximum 200 characters"));
    }

    @Test
    public void validateVersionDescriptionNull() {
        List<ValidationError> errors = new ArrayList<>();
        VersionInfo version = new VersionInfo();
        version.setName("name");
        version.setDescription("description");
        MappingInfo mapping = new MappingInfo();
        mapping.setName("mapping name");
        version.setMapping(mapping);
        ModelLibraryInfo modelLibrary = new ModelLibraryInfo();
        modelLibrary.setUmgName("umg name");
        version.setModelLibrary(modelLibrary);
        version.setVersionDescription(null);

        classUnderTest.validateAnnotions(version, errors);

        assertThat(errors.get(0).getMessage(), is("Version description cannot be empty"));
    }

    @Test
    public void validateVersionDescriptionGreaterThan200Chars() {
        List<ValidationError> errors = new ArrayList<>();
        VersionInfo version = new VersionInfo();
        version.setName("name");
        version.setDescription("description");
        MappingInfo mapping = new MappingInfo();
        mapping.setName("mapping name");
        version.setMapping(mapping);
        ModelLibraryInfo modelLibrary = new ModelLibraryInfo();
        modelLibrary.setUmgName("umg name");
        version.setModelLibrary(modelLibrary);
        version.setVersionDescription("012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");

        classUnderTest.validateAnnotions(version, errors);

        assertThat(errors.get(0).getMessage(), is("Version description can be maximum 200 characters"));
    }

    @Test
    public void validateIfVersionAlreadyExists() throws BusinessException, SystemException {
        when(mockVersionBO.findByNameAndMappingNameAndModelLibraryUmgName("name", "mapping name", "umg name")).thenReturn(
                new Version());
        VersionInfo version = new VersionInfo();
        version.setName("name");
        version.setDescription("description");
        MappingInfo mapping = new MappingInfo();
        mapping.setName("mapping name");
        version.setMapping(mapping);
        ModelLibraryInfo modelLibrary = new ModelLibraryInfo();
        modelLibrary.setUmgName("umg name");
        version.setModelLibrary(modelLibrary);
        version.setVersionDescription("01234");

        try {
            classUnderTest.validate(version);
            fail("BusinessException has to be thrown");
        } catch (BusinessException e) {
            assertThat(e.getCode(), is("BSE000062"));
        }
    }
    
    @Test
    public void validateVersionDescriptionNullForCoverage() {
        List<ValidationError> errors = new ArrayList<>();
        VersionInfo version = new VersionInfo();
        version.setName("name");
        version.setDescription("description");
        MappingInfo mapping = new MappingInfo();
        mapping.setName("mapping name");
        version.setMapping(mapping);
        ModelLibraryInfo modelLibrary = new ModelLibraryInfo();
        modelLibrary.setUmgName("umg name");
        version.setModelLibrary(modelLibrary);
        version.setVersionDescription(null);
        try {
            errors = classUnderTest.validate(version);
        } catch (BusinessException | SystemException e) {
            e.printStackTrace();
        }
        assertThat(errors.get(0).getMessage(), is("Version description cannot be empty"));
    }

}
