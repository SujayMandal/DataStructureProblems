package com.ca.umg.business.migration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.migration.info.VersionImportInfo;

@Ignore
@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class MigrationAdapterTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationAdapterTest.class);

    @Inject
    MigrationAdapterFactory factory;  
    
    FileInputStream oldZipIs;
    
    FileInputStream newZipIs;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        try {
            initZipInputStreams();
        } catch (SystemException | IOException e) {
            e.printStackTrace();
        }
    }
    
    @Ignore
    public void testOldAdapter() {
        try {
            MigrationAdapter oldAdapter = factory.getMigrationAdapter(oldZipIs, "TestNameOld.zip");
            VersionImportInfo versionInfo = oldAdapter.extractVersionPackage();
            assertNotNull(versionInfo);
            assertNotNull(versionInfo.getChecksum());
            assertNotNull(versionInfo.getReadChecksum());
            assertNotNull(versionInfo.getVersionMigrationWrapper());
            assertNotNull(versionInfo.getVersionMigrationWrapper().getModelDefinitionType());
            assertNotNull(versionInfo.getVersionMigrationWrapper().getModelDocName());
            assertNotNull(versionInfo.getVersionMigrationWrapper().getModelLibraryJarName());
            assertNotNull(versionInfo.getVersionMigrationWrapper().getModelXMLName());
            assertNotNull(versionInfo.getVersionMigrationWrapper().getZipChecksumAlgo());
            assertNull(versionInfo.getVersionMigrationWrapper().getMigrationAuditInfo());
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            Assert.fail();
        }
    }
    
    @Ignore
    public void testNewAdapter() {
        try {
            MigrationAdapter oldAdapter = factory.getMigrationAdapter(newZipIs, "TestNameNew.zip");
            VersionImportInfo versionInfo = oldAdapter.extractVersionPackage();
            assertNotNull(versionInfo);
            assertEquals("Dummy", versionInfo.getChecksum());
            assertEquals("Dummy", versionInfo.getReadChecksum());
            assertNotNull(versionInfo.getVersionMigrationWrapper());
            assertNotNull(versionInfo.getVersionMigrationWrapper().getModelDefinitionType());
            assertNotNull(versionInfo.getVersionMigrationWrapper().getModelDocName());
            assertNotNull(versionInfo.getVersionMigrationWrapper().getModelLibraryJarName());
            assertNotNull(versionInfo.getVersionMigrationWrapper().getModelXMLName());
            assertNull(versionInfo.getVersionMigrationWrapper().getZipChecksumAlgo());
            assertNotNull(versionInfo.getVersionMigrationWrapper().getMigrationAuditInfo());
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
            Assert.fail();
        }
    }
    
    public void initZipInputStreams() throws SystemException, IOException {
        oldZipIs = new FileInputStream(new File("./src/test/resources/migration/Ocwen_Old_Model_2.0.zip"));
        newZipIs = new FileInputStream(new File("./src/test/resources/migration/MyVersion_New_1.0.zip"));
    }
}
