/**
 * 
 */
package com.ca.umg.business.migration.helper;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.CheckSumUtil;
import com.ca.umg.business.mapping.delegate.MappingDelegate;
import com.ca.umg.business.migration.info.VersionMigrationInfo;
import com.ca.umg.business.migration.info.VersionMigrationWrapper;
import com.ca.umg.business.model.delegate.ModelDelegate;
import com.ca.umg.business.model.info.ModelInfo;
import com.ca.umg.business.model.info.ModelLibraryInfo;
import com.ca.umg.business.syndicatedata.delegate.SyndicateDataQueryDelegate;
import com.ca.umg.business.version.delegate.VersionDelegate;

/**
 * @author kamathan
 *
 */
public class VersionMigrationHelperTest {

    @Mock
    private ModelDelegate modelDelegate;

    @Mock
    private MappingDelegate mappingDelegate;

    @Mock
    private SyndicateDataQueryDelegate syndicateDataQueryDelegate;

    @Mock
    private VersionDelegate versionDelegate;

    @InjectMocks
    private VersionMigrationHelper versionMigrationHelper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test method for
     * {@link com.ca.umg.business.migration.helper.VersionMigrationHelper#importModelLibrary(com.ca.umg.business.migration.info.VersionMigrationWrapper, java.util.List)}
     * .
     * 
     * @throws SystemException
     * @throws BusinessException
     */
    @Test
    public void testImportModelLibrary() throws BusinessException, SystemException {
        when(modelDelegate.searchModelLibraryByJarAndChecksum(anyString(), anyString())).thenReturn(null);
        when(modelDelegate.createModelLibrary(any(ModelLibraryInfo.class))).thenReturn(new ModelLibraryInfo());

        List<String> messages = new ArrayList<String>();
        ModelLibraryInfo modelLibraryInfo = versionMigrationHelper.importModelLibrary(
                buildVersionMigrationWrapper("modelDoc".getBytes(), "model1", "modelXml".getBytes(), "modelJar".getBytes(),
                        "modelLib1", "modelXml", buildVersionMigrationInfo("SHA256", "modelJar".getBytes(), null, null)),
                messages);
        assertNotNull(modelLibraryInfo);
    }

    private VersionMigrationInfo buildVersionMigrationInfo(String modelLibAlgo, byte[] modelJar, String modelName,
            String modelDescription) {
        VersionMigrationInfo versionMigrationInfo = new VersionMigrationInfo();
        if (StringUtils.isNotBlank(modelLibAlgo)) {
            versionMigrationInfo.setModelLibraryChecksumAlgo(modelLibAlgo);
        }
        if (modelJar != null) {
            versionMigrationInfo.setModelLibraryChecksum(CheckSumUtil.getCheckSumValue(modelJar, modelLibAlgo));
        }

        versionMigrationInfo.setModelName(modelName);
        versionMigrationInfo.setModelDescription(modelDescription);
        return versionMigrationInfo;
    }

    private VersionMigrationWrapper buildVersionMigrationWrapper(byte[] modelDoc, String modelDocName, byte[] modelXML,
            byte[] modelLibraryJar, String modelLibraryJarName, String modelXMLName, VersionMigrationInfo versionMigrationInfo) {
        VersionMigrationWrapper versionMigrationWrapper = new VersionMigrationWrapper();
        versionMigrationWrapper.setModelDoc(modelDoc);
        versionMigrationWrapper.setModelDocName(modelDocName);
        versionMigrationWrapper.setModelIODefinition(modelXML);
        versionMigrationWrapper.setModelLibraryJar(modelLibraryJar);
        versionMigrationWrapper.setModelLibraryJarName(modelLibraryJarName);
        versionMigrationWrapper.setModelXMLName(modelXMLName);
        versionMigrationWrapper.setVersionMigrationInfo(versionMigrationInfo);
        versionMigrationWrapper.setModelExcelName("MATLAB_Template.xlsx");
        versionMigrationWrapper.setModelExcelDefinition("MATLAB_Template.xlsx".getBytes());
        return versionMigrationWrapper;
    }

    /**
     * Test method for
     * {@link com.ca.umg.business.migration.helper.VersionMigrationHelper#importModel(com.ca.umg.business.migration.info.VersionMigrationWrapper)}
     * .
     * 
     * @throws SystemException
     * @throws BusinessException
     */
    @Test
    public void testImportModel() throws BusinessException, SystemException {
        when(modelDelegate.createModel(any(ModelInfo.class))).thenReturn(new ModelInfo());
        versionMigrationHelper.importModel(buildVersionMigrationWrapper("doc".getBytes(), "doc1", "ioDefn".getBytes(), null,
                null, "IO Definition", buildVersionMigrationInfo(null, null, "model1", "model desc")));
    }

    /**
     * Test method for
     * {@link com.ca.umg.business.migration.helper.VersionMigrationHelper#importMapping(com.ca.umg.business.migration.info.VersionMigrationWrapper, com.ca.umg.business.model.info.ModelInfo)}
     * .
     * 
     * @throws BusinessException
     * @throws SystemException
     */
    @Test
    @Ignore
    public void testImportMapping() throws SystemException, BusinessException {
        VersionMigrationWrapper versionMigrationWrapper = buildVersionMigrationWrapper(
                "sample doc".getBytes(),
                "sample doc",
                "io definition".getBytes(),
                "sample model library jar".getBytes(),
                "sample model library jar",
                "sample xml",
                buildVersionMigrationInfo("SHA256", "sample model library jar".getBytes(), "sample model name",
                        "sample model description"));
        versionMigrationHelper.importMapping(versionMigrationWrapper, new ModelInfo());
    }

    /**
     * Test method for
     * {@link com.ca.umg.business.migration.helper.VersionMigrationHelper#importQueries(com.ca.umg.business.migration.info.VersionMigrationInfo, com.ca.umg.business.mapping.info.MappingInfo)}
     * .
     */
    @Test
    @Ignore
    public void testImportQueries() {
        // fail("Not yet implemented");
    }

    /**
     * Test method for
     * {@link com.ca.umg.business.migration.helper.VersionMigrationHelper#importVersion(com.ca.umg.business.migration.info.VersionDetail, com.ca.umg.business.model.info.ModelLibraryInfo, com.ca.umg.business.mapping.info.MappingInfo)}
     * .
     */
    @Test
    @Ignore
    public void testImportVersion() {
        // fail("Not yet implemented");
    }

}
