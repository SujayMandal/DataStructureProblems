/**
 * 
 */
package com.ca.umg.sdc.rest.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.ca.umg.business.migration.MigrationAdapterFactory;
import com.ca.umg.business.migration.audit.info.MigrationAuditInfo;
import com.ca.umg.business.migration.delegate.VersionMigrationDelegate;
import com.ca.umg.business.migration.info.VersionMigrationWrapper;

/**
 * @author nigampra
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class VersionMigrationControllerTest {

    @Inject
    private VersionMigrationDelegate versionMigrationDelegate;

    @Inject
    private VersionMigrationController controller;

    @Autowired
    private WebApplicationContext ctx;

    @Mock
    private MigrationAdapterFactory migrationAdapterFactory;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(ctx).build();
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void testExportVersion() throws Exception {
        MigrationAuditInfo migrationAuditInfo = new MigrationAuditInfo();
        when(versionMigrationDelegate.logVersionExport("614c7869-7222-4e54-8d58-01ef882fc967")).thenReturn(migrationAuditInfo);
        when(versionMigrationDelegate.exportVersion("614c7869-7222-4e54-8d58-01ef882fc967", migrationAuditInfo)).thenReturn(
                buildVersionMigrationWrapper());
        /*
         * this.mockMvc.perform( get("/version/export/{tenantModelName}/{version}/{id}", "AQ", "1.0",
         * "614c7869-7222-4e54-8d58-01ef882fc967")) .andExpect(status().isOk());
         */
    }

    private VersionMigrationWrapper buildVersionMigrationWrapper() {
        VersionMigrationWrapper vmw = new VersionMigrationWrapper();
        vmw.setModelDefinitionType("Model Def Type");
        vmw.setModelDocName("model-doc.pdf");
        vmw.setModelLibraryJarName("antlr-2.7.2.jar");
        vmw.setModelXMLName("computeAQMKNPV_mandatory-output_date_synd.xml");
        return vmw;
    }
}
