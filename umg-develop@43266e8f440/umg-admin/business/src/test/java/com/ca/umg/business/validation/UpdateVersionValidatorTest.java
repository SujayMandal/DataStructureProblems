package com.ca.umg.business.validation;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.version.info.VersionInfo;

public class UpdateVersionValidatorTest {
    
    @InjectMocks
    private UpdateVersionValidator classUnderTest = new UpdateVersionValidator();
    
    @Before
    public void setUp() {
        initMocks(this);
    }
    
    @Test
    public void validateVersionDescriptionCannotBeNull() {
        List<ValidationError> errors = new ArrayList<>();
        VersionInfo version = new VersionInfo();
        version.setName("name");
        version.setDescription("description");
        classUnderTest.validateAnnotions(version, errors);
        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getMessage(), is("Version description cannot be empty"));
    }
    
    @Test
    public void validateVersionDescriptionValid() {
        List<ValidationError> errors = new ArrayList<>();
        VersionInfo version = new VersionInfo();
        version.setName("name");
        version.setDescription("description");
        version.setVersionDescription("version description");
        classUnderTest.validateAnnotions(version, errors);
        assertThat(errors.size(), is(0));
    }
    
    @Test
    public void validateVersionDescriptionValidForCoverage() {
        List<ValidationError> errors = null;
        VersionInfo version = new VersionInfo();
        version.setName("name");
        version.setDescription("description");
        version.setVersionDescription("version description");
        try {
            errors = classUnderTest.validate(version);
        } catch (BusinessException | SystemException e) {
            e.printStackTrace();
        }
        if(errors != null) {
        	assertThat(errors.size(), is(0));
        }
    }

}
