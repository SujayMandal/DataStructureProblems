package com.ca.umg.business.mapping.bo;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.jpa.domain.Specification;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.common.info.SearchOptions;
import com.ca.umg.business.mapping.dao.AbstractMappingTest;
import com.ca.umg.business.mapping.dao.MappingDAO;
import com.ca.umg.business.mapping.dao.MappingInputDAO;
import com.ca.umg.business.mapping.dao.MappingOutputDAO;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.entity.MappingInput;
import com.ca.umg.business.mapping.entity.MappingOutput;
import com.ca.umg.business.mapping.info.MappingStatus;
import com.ca.umg.business.mapping.info.MappingsCopyInfo;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.version.dao.VersionContainerDAO;

@RunWith(MockitoJUnitRunner.class)
public class MappingBOImplTest extends AbstractMappingTest {

    @InjectMocks
    private MappingBO mappingBO = new MappingBOImpl();

    @Mock
    private MappingDAO mappingDAOMock;

    @Mock
    private MappingInputDAO mappingInputDAOMock;

    @Mock
    private MappingOutputDAO mappingOutputDAOMock;
    
    @Mock
    private VersionContainerDAO versionContainerDAO;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void testListAll() throws SystemException, BusinessException {
        List<Mapping> mappingList = new ArrayList<Mapping>();
        Mapping mapping = getMapping();
        mappingList.add(mapping);
        when(mappingDAOMock.findAll((org.springframework.data.domain.Sort) Mockito.any())).thenReturn(mappingList);
        List<Mapping> resultList = mappingBO.listAll();
        Assert.assertNotNull(resultList);
        Assert.assertEquals(1, resultList.size());

    }

    @Test
    public void testCreate() throws SystemException, BusinessException {
        Mapping mapping = getMapping();
        when(mappingDAOMock.saveAndFlush(mapping)).thenReturn(mapping);
        Mapping resultMapping = mappingBO.create(mapping);
        Assert.assertNotNull(resultMapping);
        Assert.assertEquals("mappingId01", resultMapping.getId());
    }

    @Test
    public void testFind() throws SystemException, BusinessException {
        Mapping mapping = getMapping();
        when(mappingDAOMock.findOne("mappingId01")).thenReturn(mapping);
        Mapping resultMapping = mappingBO.find("mappingId01");
        Assert.assertNotNull(resultMapping);
        Assert.assertEquals("mappingId01", resultMapping.getId());
    }

    @Test
    public void testCreateMappingInput() throws SystemException, BusinessException {
        MappingInput mappingInput = new MappingInput();
        mappingInput.setId("mappingId01");
        mappingInput.setMappingData("mappingData".getBytes());
        mappingInput.setMapping(getMapping());
        mappingInput.setTenantInterfaceDefn("tenantInterfaceDefn".getBytes());
        when(mappingInputDAOMock.saveAndFlush(mappingInput)).thenReturn(mappingInput);
        MappingInput resultInput = mappingBO.createMappingInput(mappingInput);
        Assert.assertNotNull(resultInput);
        Assert.assertEquals("mappingId01", resultInput.getId());
    }

    @Test
    public void testCreateMappingOutput() throws SystemException, BusinessException {
        MappingOutput mappingOutput = new MappingOutput();
        mappingOutput.setId("mappingId01");
        mappingOutput.setMappingData("mappingData".getBytes());
        mappingOutput.setMapping(getMapping());
        mappingOutput.setTenantInterfaceDefn("tenantInterfaceDefn".getBytes());
        when(mappingOutputDAOMock.saveAndFlush(mappingOutput)).thenReturn(mappingOutput);
        MappingOutput resultOutput = mappingBO.createMappingOutput(mappingOutput);
        Assert.assertNotNull(resultOutput);
        Assert.assertEquals("mappingId01", resultOutput.getId());
    }

    @Test
    public void testFindByName() throws SystemException, BusinessException {
        when(mappingDAOMock.findByName("mappingName")).thenReturn(getMapping());
        Mapping resultMapping = mappingBO.findByName("mappingName");
        Assert.assertNotNull(resultMapping);
        Assert.assertEquals("mappingName", resultMapping.getName());
    }

    @Test
    public void testFindInputByMapping() throws SystemException, BusinessException {
        Mapping mapping = getMapping();
        MappingInput mappingInput = new MappingInput();
        mappingInput.setId("mappingId01");
        when(mappingInputDAOMock.findByMapping(mapping)).thenReturn(mappingInput);
        MappingInput resultInput = mappingBO.findInputByMapping(mapping);
        Assert.assertNotNull(resultInput);
        Assert.assertEquals("mappingId01", resultInput.getId());
    }

    @Test
    public void testFindOutputByMapping() throws SystemException, BusinessException {
        Mapping mapping = getMapping();
        MappingOutput mappingOutput = new MappingOutput();
        mappingOutput.setId("mappingId01");
        when(mappingOutputDAOMock.findByMapping(mapping)).thenReturn(mappingOutput);
        MappingOutput resultOutput = mappingBO.findOutputByMapping(mapping);
        Assert.assertNotNull(resultOutput);
        Assert.assertEquals("mappingId01", resultOutput.getId());
    }

    @Test
    public void testDeleteTidMapping() throws SystemException, BusinessException {
        Mapping mapping = getMapping();

        MappingOutput mappingOutput = new MappingOutput();
        mappingOutput.setId("mappingOutputId01");

        MappingInput mappingInput = new MappingInput();
        mappingInput.setId("mappingInputId01");

        when(mappingDAOMock.findByName("mappingName")).thenReturn(getMapping());
        when(mappingOutputDAOMock.findByMapping(mapping)).thenReturn(mappingOutput);
        when(mappingInputDAOMock.findByMapping(mapping)).thenReturn(mappingInput);

        mappingOutputDAOMock.delete(mappingOutput);
        mappingInputDAOMock.delete(mappingInput);
        mappingDAOMock.delete(mapping);

        Assert.assertTrue(mappingBO.deleteTidMapping("mappingName"));

    }

    @Test
    public void testGetListOfMappingNames() throws SystemException, BusinessException {
        when(mappingDAOMock.getListOfMappingNames("testModelName")).thenReturn(asList("Mapping1", "Mapping2"));
        List<String> resultList = mappingBO.getListOfMappingNames("testModelName");
        Assert.assertNotNull(resultList);
        Assert.assertEquals(2, resultList.size());
    }

    @Test
    public void testFindByModelName() throws SystemException, BusinessException {
        when(mappingDAOMock.findByModelName("testModelName")).thenReturn(asList(getMapping()));
        List<Mapping> resultList = mappingBO.findByModelName("testModelName");
        Assert.assertNotNull(resultList);
        Assert.assertEquals(1, resultList.size());

    }

    @Test
    public void testGetListOfMappingNamesById() throws SystemException, BusinessException {
        when(mappingDAOMock.getListOfMappingNamesById("testModelId")).thenReturn(asList("Mapping1", "Mapping2"));
        List<String> resultList = mappingBO.getListOfMappingNamesById("testModelId");
        Assert.assertNotNull(resultList);
        Assert.assertEquals(2, resultList.size());
    }

    @Test
    public void testGetAllTidsForCopy() throws BusinessException {
        List<MappingsCopyInfo> maps = new ArrayList<MappingsCopyInfo>();
        MappingsCopyInfo mappingsCopyInfo = new MappingsCopyInfo();
        mappingsCopyInfo.setTidName("tidName");
        mappingsCopyInfo.setVersion("version");
        mappingsCopyInfo.setVersionNo("1.0");
        maps.add(mappingsCopyInfo);
        when(versionContainerDAO.getDataForTidCopy()).thenReturn(maps);
        List<MappingsCopyInfo> resultInfo = mappingBO.getAllTidsForCopy();
        Assert.assertNotNull(resultInfo);
        Assert.assertEquals(1, resultInfo.size());

    }
    
    @Test
    public void testFindFinalizedMappings() throws SystemException, BusinessException {
        when(mappingDAOMock.findByModelNameAndStatus("testModelName",MappingStatus.FINALIZED.getMappingStatus())).thenReturn(asList(getMapping()));
        List<Mapping> resultList = mappingBO.findFinalizedMappings("testModelName");
        Assert.assertNotNull(resultList);
        Assert.assertEquals(1, resultList.size());

    }
    
    @Test
    public void testGetMappingStatus() throws SystemException, BusinessException {
        when(mappingDAOMock.getMappingStatus("testModelName")).thenReturn((getMapping().getStatus()));
        String result = mappingBO.getMappingStatus("testModelName");
        Assert.assertNotNull(result);
        Assert.assertEquals("SAVED", result);

    }
    @Test
    public void testFindAllMappings() throws SystemException, BusinessException {
    	when((mappingDAOMock.findAll(any(Specification.class)))).thenReturn(getMappingList());
    	
    	SearchOptions searchOptions=new SearchOptions();
		searchOptions.setPage(0);
		searchOptions.setPageSize(5);
		searchOptions.setSearchText("mappingId01");
        List<Mapping> result = mappingBO.findAllMappings(searchOptions);
        Assert.assertNotNull(result);
        Assert.assertEquals("mappingName", result.get(0).getName());

    }
    
    private List<Mapping> getMappingList() {
        Mapping mapping = new Mapping();
        mapping.setId("mappingId01");
        mapping.setName("mappingName");
        mapping.setModel(new Model());
        mapping.setModelIO("TestBytes".getBytes());
        mapping.setStatus("SAVED");
        List<Mapping> mappingList=new ArrayList<Mapping>();
        mappingList.add(mapping);
        return mappingList;
    }

    private Mapping getMapping() {
        Mapping mapping = new Mapping();
        mapping.setId("mappingId01");
        mapping.setName("mappingName");
        mapping.setModel(new Model());
        mapping.setModelIO("TestBytes".getBytes());
        mapping.setStatus("SAVED");
        return mapping;
    }
    
    
    
}
