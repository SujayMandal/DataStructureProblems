package com.ca.umg.business.mapping.delegate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.TransformerUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileCopyUtils;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.mapping.dao.AbstractMappingTest;
import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.mapping.entity.MappingInput;
import com.ca.umg.business.mapping.info.MappingDescriptor;
import com.ca.umg.business.mapping.info.MappingHierarchyInfo;
import com.ca.umg.business.mapping.info.MappingInfo;
import com.ca.umg.business.mapping.info.MappingsCopyInfo;
import com.ca.umg.business.mapping.info.QueryLaunchInfo;
import com.ca.umg.business.mapping.info.TidIoDefinition;
import com.ca.umg.business.mid.extraction.info.MappingViewInfo;
import com.ca.umg.business.mid.extraction.info.TidIOInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelDefinition;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.validation.ValidationError;
import com.ca.umg.business.version.entity.Version;
import com.ca.umg.business.version.info.VersionStatus;

@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
// TODO fix ignored test cases
public class MappingDelegateImplTest extends AbstractMappingTest {
	@Inject
	private MappingDelegate mappingDelegate;

	private final String tenantCode5 = null;
	private final String description = "flatnd Model test for junit testing";
	private final String userName = "System";
	private final String xmlFile = "com/ca/umg/business/mapping/delegate/UMG-MATLAB-IO.XML";

	@Before
	public void setup() {
		getLocalhostTenantContext();
	}

	@Test
	public void testDeleteTidMapping() throws SystemException, BusinessException {
		Mapping mapping = createMapping("TIDIOMAPPINGDeltest-5",
				createModel("tidioModelDelTest5", "desc", "doc", "sample", "text/xml", "sample"), tenantCode5,
				"tid for model_5", "Sample MID Json");
		assertNotNull(mapping);

		MappingInput mappingInput = createMappingInput(mapping, "sampleMappingData.DelTest4".getBytes(),
				"sampleTidDelTest4".getBytes(), tenantCode5);
		assertNotNull(mappingInput);
		assertNotNull(mappingInput.getId());

		MappingInput savedMappingId = getMappingInputDAO().findByMapping(mapping);
		assertNotNull(savedMappingId);
		assertEquals(mappingInput, savedMappingId);

		boolean delSuccess = mappingDelegate.deleteMapping("TIDIOMAPPINGDeltest-5");
		assertEquals(true, delSuccess);

		MappingInput savedMappingIdAfterDel = getMappingInputDAO().findByMapping(mapping);
		assertNull(savedMappingIdAfterDel);
	}

	@Test
	public void testcreateInputMapForQuery() throws SystemException, BusinessException, IOException {
		KeyValuePair<String, List<ValidationError>> mappingSaveRslt = null;
		QueryLaunchInfo queryLaunchInfo = null;
		String tidname = null;
		InputStream inputStream = null;
		String type = BusinessConstants.TYPE_INPUT_MAPPING;
		String umgInputName = "flatndtest_input";
		try {
			Model model = createModel(umgInputName);
			ModelDefinition modelDefinition = new ModelDefinition();
			inputStream = this.getClass().getClassLoader().getResourceAsStream(xmlFile);
			byte[] ioDefinition = FileCopyUtils.copyToByteArray(inputStream);
			modelDefinition.setIoDefinition(ioDefinition);
			modelDefinition.setModel(model);
			modelDefinition.setType(type);
			model.setModelDefinition(modelDefinition);
			model.setCreatedBy(userName);
			model.setCreatedDate(DateTime.now());
			model.setLastModifiedBy(userName);
			model.setLastModifiedDate(DateTime.now());
			model.setName(umgInputName);

			getModelDAO().save(model);
			String tidName = saveOutputMapping(umgInputName, BusinessConstants.TYPE_OUTPUT_MAPPING);
			MappingDescriptor mappingDescriptor = mappingDelegate.generateMapping(umgInputName);
			mappingDescriptor.setTidName(tidName);
			mappingDescriptor.setDescription(description);
			mappingSaveRslt = mappingDelegate.saveMappingDescription(mappingDescriptor, umgInputName, "validate");
			tidname = mappingSaveRslt.getKey();
			queryLaunchInfo = mappingDelegate.createInputMapForQuery(type, tidname);
			Assert.assertNotNull(queryLaunchInfo);
			// checking for output mapping also
			queryLaunchInfo = mappingDelegate.createInputMapForQuery(BusinessConstants.TYPE_OUTPUT_MAPPING, tidname);
			Assert.assertNotNull(queryLaunchInfo);

			assertNotNull(queryLaunchInfo.getMidOutput());
			assertNull(queryLaunchInfo.getTidInput());
			assertNotNull(queryLaunchInfo.getTidName());
			assertEquals("OUTPUTMAPPING", queryLaunchInfo.getType());

			mappingDelegate.deleteMapping(tidname);
		} catch (SystemException | BusinessException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	@Test
	public void testcreateOutputMapForQuery() throws SystemException, BusinessException, IOException {
		KeyValuePair<String, List<ValidationError>> mappingSaveRslt = null;
		QueryLaunchInfo queryLaunchInfo = null;
		String tidname = null;
		String type = BusinessConstants.TYPE_OUTPUT_MAPPING;
		String umgInputName = "flatndtest_output_1";
		Model model = createModel(umgInputName);
		ModelDefinition modelDefinition = new ModelDefinition();
		InputStream inputStream = null;
		byte[] ioDefinition;
		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(xmlFile);

			ioDefinition = FileCopyUtils.copyToByteArray(inputStream);

			modelDefinition.setIoDefinition(ioDefinition);
			modelDefinition.setModel(model);
			modelDefinition.setType(type);
			model.setModelDefinition(modelDefinition);
			model.setCreatedBy(userName);
			model.setCreatedDate(DateTime.now());
			model.setLastModifiedBy(userName);
			model.setLastModifiedDate(DateTime.now());
			model.setName(umgInputName);

			getModelDAO().save(model);
			MappingDescriptor mappingDescriptor = mappingDelegate.generateMapping(umgInputName);
			mappingDescriptor.setDescription(description);
			mappingSaveRslt = mappingDelegate.saveMappingDescription(mappingDescriptor, umgInputName, "validate");
			tidname = mappingSaveRslt.getKey();

			queryLaunchInfo = mappingDelegate.createInputMapForQuery(type, tidname);
			Assert.assertNotNull(queryLaunchInfo);
			// checking for output mapping also
			queryLaunchInfo = mappingDelegate.createInputMapForQuery(BusinessConstants.TYPE_OUTPUT_MAPPING, tidname);
			Assert.assertNotNull(queryLaunchInfo);

			assertNotNull(queryLaunchInfo.getMidOutput());
			assertNull(queryLaunchInfo.getTidInput());
			assertNotNull(queryLaunchInfo.getTidName());
			assertEquals("OUTPUTMAPPING", queryLaunchInfo.getType());

			mappingDelegate.deleteMapping(tidname);
		} catch (SystemException | BusinessException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	@Test
	public void testSaveInputMappingDescription() throws SystemException, BusinessException, IOException {
		String umgInputName = "flatndtest_input_1";
		String tidname = saveMapping(umgInputName, BusinessConstants.TYPE_INPUT_MAPPING).getKey();
		assertTrue(StringUtils.isNotBlank(tidname));
		mappingDelegate.deleteMapping(tidname);
	}

	@Test
	public void testSaveOutputMappingDescription() throws SystemException, BusinessException, IOException {
		String umgOutputName = "flatndtest_output";
		String tidname = saveMapping(umgOutputName, BusinessConstants.TYPE_OUTPUT_MAPPING).getKey();
		assertTrue(StringUtils.isNotBlank(tidname));
		mappingDelegate.deleteMapping(tidname);
	}

	@Test
	public void testSaveMappingDescriptionWithVersionStatusChanged()
			throws SystemException, BusinessException, IOException {
		KeyValuePair<String, List<ValidationError>> mappingSaveRslt = null;
		String umgName = "flatndtest_output_vertest";
		String mappingType = BusinessConstants.TYPE_INPUT_MAPPING;

		Model model = createModel(umgName);
		ModelDefinition modelDefinition = createModelDefinition(mappingType, model);
		model.setModelDefinition(modelDefinition);
		try {
			getModelDAO().save(model);
			String tidName = saveOutputMapping(umgName, BusinessConstants.TYPE_OUTPUT_MAPPING);
			MappingDescriptor mappingDescriptor = mappingDelegate.generateMapping(umgName);
			mappingDescriptor.setDescription(description);
			mappingDescriptor.setTidName(tidName);
			createMappingForExposeToTnt(mappingDescriptor);

			mappingSaveRslt = mappingDelegate.saveMappingDescription(mappingDescriptor, umgName, "validate");
			String tidname = mappingSaveRslt.getKey();
			Mapping mapping = getMappingDAO().findByName(tidname);
			ModelLibrary modelLib = createVersion(mapping);
			mappingDescriptor.setTidName(tidname);
			mappingSaveRslt = mappingDelegate.saveMappingDescription(mappingDescriptor, umgName, "validate");
			List<Version> verList = getVersionDAO().findAll();
			assertEquals("SAVED", verList.get(0).getStatus());

			deleteTestDataList(model, modelLib, verList, tidname);

		} catch (SystemException | BusinessException e) {
			e.printStackTrace();
		}
	}

	private KeyValuePair<String, List<ValidationError>> saveMapping(String umgName, String mappingType)
			throws IOException {
		Model model = createModel(umgName);
		ModelDefinition modelDefinition = createModelDefinition(mappingType, model);
		model.setModelDefinition(modelDefinition);
		try {
			getModelDAO().save(model);
			String tidname = saveOutputMapping(umgName, mappingType);
			MappingDescriptor mappingDescriptor = mappingDelegate.generateMapping(umgName);
			mappingDescriptor.setTidName(tidname);
			mappingDescriptor.setDescription(description);
			return mappingDelegate.saveMappingDescription(mappingDescriptor, umgName, "validate");
		} catch (SystemException | BusinessException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String saveOutputMapping(String umgName, String mappingType) throws BusinessException, SystemException {
		MappingDescriptor mappingDescriptor = mappingDelegate.generateMapping(umgName);
		KeyValuePair<String, List<ValidationError>> mapper = mappingDelegate.saveMappingDescription(mappingDescriptor,
				umgName, null);
		return mapper.getKey();
	}

	private void createMappingForExposeToTnt(MappingDescriptor mappingDescriptor) {
		// adding for expose to tnt
		TidIOInfo tidIOInfo = mappingDescriptor.getTidTree();

		TidParamInfo tidParamInfoNew = new TidParamInfo();

		for (TidParamInfo tidParamInfo : tidIOInfo.getTidInput()) {
			if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase("SampleParameter1",
					tidParamInfo.getFlatenedName())) {
				tidParamInfoNew.setChildren(null);
				tidParamInfoNew.setDataFormat(null);
				tidParamInfoNew.setDatatype(tidParamInfo.getDatatype());
				tidParamInfoNew.setDataTypeStr(tidParamInfo.getDataTypeStr());
				tidParamInfoNew.setDescription("SampleParamExposeTnt desc");
				tidParamInfoNew.setExposedToTenant(true);
				tidParamInfoNew.setExpressionId(null);
				tidParamInfoNew.setExprsnOutput(false);
				tidParamInfoNew.setFlatenedName("SampleParamExposeTnt");
				tidParamInfoNew.setMandatory(false);
				tidParamInfoNew.setMapped(true);
				tidParamInfoNew.setName("SampleParamExposeTnt");
				tidParamInfoNew.setPrecision(0);
				tidParamInfoNew.setSequence(9);
				tidParamInfoNew.setSize(0);
				tidParamInfoNew.setSqlId(null);
				tidParamInfoNew.setSqlOutput(false);
				tidParamInfoNew.setSyndicate(false);
				tidParamInfoNew.setText(null);
				tidParamInfoNew.setUserSelected(false);
				tidParamInfoNew.setValue(null);
				break;
			}
		}
		tidIOInfo.getTidInput().add(tidParamInfoNew);

		MappingViewInfo mappingViewInfoNew = new MappingViewInfo();
		mappingViewInfoNew.setMappedTo("SampleParameter1");
		mappingViewInfoNew.setMappingParam("SampleParamExposeTnt");
		mappingDescriptor.getTidMidMapping().getInputMappingViews().add(mappingViewInfoNew);

		// end adding for expose to tnt
	}

	private ModelDefinition createModelDefinition(String type, Model model) throws IOException {
		ModelDefinition modelDefinition = new ModelDefinition();
		InputStream inputStream = null;
		byte[] ioDefinition;
		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(xmlFile);
		} finally {
			ioDefinition = FileCopyUtils.copyToByteArray(inputStream);
			IOUtils.closeQuietly(inputStream);
		}
		modelDefinition.setIoDefinition(ioDefinition);
		modelDefinition.setModel(model);
		modelDefinition.setType(type);
		return modelDefinition;
	}

	private Model createModel(String umgName) {
		String modelName = "Model"
				+ (new SimpleDateFormat("MM-dd-YY-HH-mm-ss").format(Calendar.getInstance().getTime()));
		Model model = new Model();
		model.setName(modelName);
		model.setDescription(description);
		model.setUmgName(umgName);
		model.setIoDefinitionName("ioDefinitionName");
		model.setDocumentationName("documentationName");
		model.setCreatedBy(userName);
		model.setCreatedDate(DateTime.now());
		model.setLastModifiedBy(userName);
		model.setLastModifiedDate(DateTime.now());
		model.setName(umgName);
		return model;
	}

	private ModelLibrary createVersion(Mapping mapping) {
		ModelLibrary modelLib = buildModelLibrary("testVerStaChange", "testingVerStaChange", "testVerStaChange",
				"antlr-2.7.2", "MATLAB", "INTERNAL", "2a53206963dfa78e33746b6f8367f7d9970fa36865a825d7bfbce1784dc0f4d4",
				"SHA256", "Matlab-7.16");
		modelLib = getModelLibraryDAO().saveAndFlush(modelLib);
		Version version = new Version();
		version.setName("verStat1");
		version.setDescription("verStat1");
		version.setMajorVersion(8);
		version.setMinorVersion(1);
		version.setStatus(VersionStatus.TESTED.getVersionStatus());
		version.setMapping(mapping);
		version.setModelLibrary(modelLib);
		version.setVersionDescription("this is version desc v5");
		getVersionDAO().saveAndFlush(version);
		return modelLib;
	}

	public void deleteTestDataList(Model model, ModelLibrary modelLibrary, List<Version> versionList, String tidName) {
		for (Version version : versionList) {
			getVersionDAO().delete(version);
			getVersionDAO().flush();
		}
		getModelLibraryDAO().delete(modelLibrary);
		getModelLibraryDAO().flush();
		try {
			mappingDelegate.deleteMapping(tidName);
		} catch (BusinessException | SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getModelDAO().delete(model);
		getModelDAO().flush();

	}

	@Test
	public void testTidCopy() throws SystemException, BusinessException, IOException {
		KeyValuePair<String, List<ValidationError>> mappingSaveRslt = null;
		MappingDescriptor exstMappingDesc = null;
		MappingDescriptor newMappingDesc = null;
		String tidname = null;
		String newMidname = null;
		String type = BusinessConstants.TYPE_INPUT_MAPPING;
		String umgInputNameExstng = "existingModel";
		String umgInputNameNew = "newModel";
		InputStream inputStream = null;
		byte[] ioDefinition;
		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(xmlFile);
			ioDefinition = FileCopyUtils.copyToByteArray(inputStream);


		Model model = createModel(umgInputNameExstng);
		ModelDefinition modelDefinition = new ModelDefinition();
		modelDefinition.setIoDefinition(ioDefinition);
		modelDefinition.setModel(model);
		modelDefinition.setType(type);
		model.setModelDefinition(modelDefinition);

		Model modelNew = createModel(umgInputNameNew);
		ModelDefinition modelDefinitionNew = new ModelDefinition();
		modelDefinitionNew.setIoDefinition(ioDefinition);
		modelDefinitionNew.setModel(modelNew);
		modelDefinitionNew.setType(type);
		modelNew.setModelDefinition(modelDefinitionNew);

			getModelDAO().save(model);
			getModelDAO().save(modelNew);

			String tidName = saveOutputMapping(umgInputNameExstng, BusinessConstants.TYPE_OUTPUT_MAPPING);
			exstMappingDesc = mappingDelegate.generateMapping(umgInputNameExstng);
			exstMappingDesc.setTidName(tidName);
			exstMappingDesc.setDescription(description);
			mappingSaveRslt = mappingDelegate.saveMappingDescription(exstMappingDesc, umgInputNameExstng, "validate");
			exstMappingDesc.setTidName(mappingSaveRslt.getKey());
			mappingSaveRslt = mappingDelegate.saveMappingDescription(exstMappingDesc, umgInputNameExstng, "validate");
			tidname = mappingSaveRslt.getKey();
			newMidname = modelNew.getUmgName();

			newMappingDesc = mappingDelegate.generateMapping(newMidname, exstMappingDesc.getTidName(), tidname, "");
			Assert.assertNotNull(newMappingDesc);
			Assert.assertNotNull(newMappingDesc);
			mappingDelegate.deleteMapping(tidname);
		} catch (SystemException | BusinessException e) {
			e.printStackTrace();
		}
	 finally {
		IOUtils.closeQuietly(inputStream);
	}
	}

	@Ignore
	public void testTidCopySameModel() throws SystemException, BusinessException, IOException {
		KeyValuePair<String, List<ValidationError>> mappingSaveRslt = null;
		MappingDescriptor exstMappingDesc = null;
		MappingDescriptor newMappingDesc = null;
		String tidname = null;
		String type = BusinessConstants.TYPE_INPUT_MAPPING;
		String umgInputNameExstng = "existingModel1";
		InputStream inputStream = null;
		byte[] ioDefinition;
		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(xmlFile);
			ioDefinition = FileCopyUtils.copyToByteArray(inputStream);
			Model model = createModel(umgInputNameExstng);
			ModelDefinition modelDefinition = new ModelDefinition();
			modelDefinition.setIoDefinition(ioDefinition);
			modelDefinition.setModel(model);
			modelDefinition.setType(type);
			model.setModelDefinition(modelDefinition);

			getModelDAO().save(model);

			exstMappingDesc = mappingDelegate.generateMapping(umgInputNameExstng);
			exstMappingDesc.setDescription(description);
			mappingSaveRslt = mappingDelegate.saveMappingDescription(exstMappingDesc, umgInputNameExstng, "validate");
			exstMappingDesc.setTidName(mappingSaveRslt.getKey());
			mappingSaveRslt = mappingDelegate.saveMappingDescription(exstMappingDesc, umgInputNameExstng, "validate");
			tidname = mappingSaveRslt.getKey();

			Thread.sleep(60000);
			// testing the scenario for derived model name and tidname's model are same
			newMappingDesc = mappingDelegate.generateMapping(model.getUmgName(), exstMappingDesc.getTidName(), tidname,
					"description");
			Assert.assertNotNull(newMappingDesc);
			mappingDelegate.deleteMapping(tidname);
			mappingDelegate.deleteMapping(newMappingDesc.getTidName());
		} catch (SystemException | BusinessException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	@Test
	public void testGetTidIoDefinitions() throws SystemException, BusinessException, IOException {
		List<TidIoDefinition> tidIoDefinition = null;
		KeyValuePair<String, List<ValidationError>> mappingSaveRslt = null;
		String tidname = null;
		String type = BusinessConstants.TYPE_INPUT_MAPPING;
		String umgInputName = "flatndtest_input1";
		Model model = createModel(umgInputName);
		ModelDefinition modelDefinition = new ModelDefinition();
		InputStream inputStream = null;
		byte[] ioDefinition;
		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(xmlFile);
			ioDefinition = FileCopyUtils.copyToByteArray(inputStream);
		modelDefinition.setIoDefinition(ioDefinition);
		modelDefinition.setModel(model);
		modelDefinition.setType(type);
		model.setModelDefinition(modelDefinition);
		model.setCreatedBy(userName);
		model.setCreatedDate(DateTime.now());
		model.setLastModifiedBy(userName);
		model.setLastModifiedDate(DateTime.now());
		model.setName(umgInputName);

			getModelDAO().save(model);
			String tidName = saveOutputMapping(umgInputName, BusinessConstants.TYPE_OUTPUT_MAPPING);
			MappingDescriptor mappingDescriptor = mappingDelegate.generateMapping(umgInputName);
			mappingDescriptor.setTidName(tidName);
			mappingDescriptor.setDescription(description);
			mappingSaveRslt = mappingDelegate.saveMappingDescription(mappingDescriptor, umgInputName, "validate");
			tidname = mappingSaveRslt.getKey();

			tidIoDefinition = mappingDelegate.getTidIoDefinitions(tidname, false);
			Assert.assertNotNull(tidIoDefinition);

			TidIoDefinition tidIoDef = tidIoDefinition.get(0);

			Assert.assertNull(tidIoDef.getArrayValue());
			Assert.assertNotNull(tidIoDef.getDescription());
			Assert.assertNull(tidIoDef.getErrorMessage());
			Assert.assertNotNull(tidIoDef.getHtmlElement());
			Assert.assertNotNull(tidIoDef.getName());
			Assert.assertNotNull(tidIoDef.getValidationMethod());
			Assert.assertNotNull(tidIoDef.getValue());
			Assert.assertNotNull(tidIoDef.getDatatype());

			Assert.assertFalse(tidIoDef.isArrayType());
			Assert.assertFalse(tidIoDef.isError());
			Assert.assertTrue(tidIoDef.isMandatory());

			Assert.assertEquals(19, tidIoDefinition.size());
			mappingDelegate.deleteMapping(tidname);
		} catch (SystemException | BusinessException e) {
			e.printStackTrace();
		}
	 finally {
		IOUtils.closeQuietly(inputStream);
	}
	}

	@Test
	public void testListAll() throws SystemException, BusinessException {
		Mapping mapping = createMapping("TIDIOMAPPINGListAlltest",
				createModel("tidioModelListAllTest5", "desc", "doc", "sample", "text/xml", "sample"), tenantCode5,
				"tid for model_5", "Sample MID Json");
		assertNotNull(mapping);

		List<MappingInfo> mapInfoList = mappingDelegate.listAll();

		assertNotNull(mapInfoList);
		assertEquals("TIDIOMAPPINGListAlltest", mapInfoList.get(0).getName());
		deleteMapping(mapping);

	}

	@Test
	public void testFind() throws SystemException, BusinessException, FileNotFoundException {

		Mapping mapping = createMapping("TIDIOMAPPINGFindtest",
				createModel("tidioModelFindTest", "desc", "doc", "sample", "text/xml", "sample"), tenantCode5,
				"tid for model_FindTest", "Sample MID Json");

		MappingInfo createdMapInfo = mappingDelegate.find(mapping.getId());

		assertNotNull(createdMapInfo);
		assertEquals("TIDIOMAPPINGFindtest", createdMapInfo.getName());
		deleteMapping(mapping);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testGetMappingHierarchyInfos() throws SystemException, BusinessException, FileNotFoundException {

		Mapping mapping = createMapping("TIDIOMAPPINGGetMapHiertest",
				createModel("tidioModelGetMapHierTest", "desc", "doc", "sample", "text/xml", "sample"), tenantCode5,
				"tid for model_GetMapHierTest", "Sample MID Json");

		List<MappingHierarchyInfo> listMapInfo = mappingDelegate.getMappingHierarchyInfos();

		assertNotNull(listMapInfo);
		Collection mappingInfos = CollectionUtils.collect(listMapInfo,
				TransformerUtils.invokerTransformer("getMappingInfos"));
		Collection mappingNames = new ArrayList();
		if (CollectionUtils.isNotEmpty(mappingInfos)) {
			for (Object coll : mappingInfos) {
				mappingNames.addAll(
						CollectionUtils.collect((Collection) coll, TransformerUtils.invokerTransformer("getName")));
			}
		}
		Assert.assertTrue(mappingNames.contains("TIDIOMAPPINGGetMapHiertest"));
		Assert.assertTrue(CollectionUtils.collect(listMapInfo, TransformerUtils.invokerTransformer("getModelName"))
				.contains("tidioModelGetMapHierTest"));
		deleteMapping(mapping);
	}

	@Test
	public void testGetListOfMappingNames() throws SystemException, BusinessException, FileNotFoundException {

		Mapping mapping = createMapping("TIDIOMAPPINGGetListOfMappingtest",
				createModel("tidioModelGetListOfMappingTest", "desc", "doc", "sample", "text/xml", "sample"),
				tenantCode5, "tid for model_GetMapHierTest", "Sample MID Json");

		List<String> listMapNames = mappingDelegate.getListOfMappingNames("tidioModelGetListOfMappingTest");

		assertNotNull(listMapNames);
		assertEquals("TIDIOMAPPINGGetListOfMappingtest", listMapNames.get(0));
		deleteMapping(mapping);
	}

	@Test
	public void testFindByModelName() throws SystemException, BusinessException, FileNotFoundException {

		Mapping mapping = createMapping("TIDIOMAPPINGFindByModeltest",
				createModel("tidioModelFindByModelTest", "desc", "doc", "sample", "text/xml", "sample"), tenantCode5,
				"tid for model_GetMapHierTest", "Sample MID Json");

		List<MappingInfo> listMapInfo = mappingDelegate.findByModelName("tidioModelFindByModelTest");

		assertNotNull(listMapInfo);
		assertEquals("TIDIOMAPPINGFindByModeltest", listMapInfo.get(0).getName());
		deleteMapping(mapping);
	}

	@Test
	public void testFindByName() throws SystemException, BusinessException, FileNotFoundException {

		Mapping mapping = createMapping("TIDIOMAPPINGFindByNametest",
				createModel("tidioModelFindByNameTest", "desc", "doc", "sample", "text/xml", "sample"), tenantCode5,
				"tid for model_GetMapHierTest", "Sample MID Json");

		MappingInfo mapInfo = mappingDelegate.findByName("TIDIOMAPPINGFindByNametest");

		assertNotNull(mapInfo);
		assertEquals("TIDIOMAPPINGFindByNametest", mapInfo.getName());
		deleteMapping(mapping);
	}

	@Test
	public void testGetTidListForCopy() throws SystemException, BusinessException, FileNotFoundException {
		Model model = createModel("tidioModeGetTidListForCopyTest", "desc", "doc", "sample", "text/xml", "sample");
		Mapping mapping = createMapping("TIDIOMAPPINGGetTidListForCopytest", model, tenantCode5,
				"tid for model_GetMapHierTest", "Sample MID Json");
		String umgInputName = "flatndtest_input_2";
		ModelLibrary modelLib = createVersion(mapping);

		List<MappingsCopyInfo> listMappingsCopyInfo = mappingDelegate.getTidListForCopy();

		assertNotNull(listMappingsCopyInfo);
		assertTrue(CollectionUtils.collect(listMappingsCopyInfo, TransformerUtils.invokerTransformer("getTidName"))
				.contains("TIDIOMAPPINGGetTidListForCopytest"));
		List<Version> verList = getVersionDAO().findAll();
		deleteTestDataList(model, modelLib, verList, mapping.getName());
	}

	@Test
	public void testIsReferenced() throws SystemException, BusinessException, IOException {

		KeyValuePair<String, List<ValidationError>> mappingSaveRslt = null;
		String tidname = null;
		String type = BusinessConstants.TYPE_INPUT_MAPPING;
		String umgInputName = "flatndtest_input_2";
		Model model = createModel(umgInputName);
		ModelDefinition modelDefinition = new ModelDefinition();
		InputStream inputStream = null;
		byte[] ioDefinition;
		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(xmlFile);
			ioDefinition = FileCopyUtils.copyToByteArray(inputStream);
			modelDefinition.setIoDefinition(ioDefinition);
			modelDefinition.setModel(model);
			modelDefinition.setType(type);
			model.setModelDefinition(modelDefinition);
			model.setCreatedBy(userName);
			model.setCreatedDate(DateTime.now());
			model.setLastModifiedBy(userName);
			model.setLastModifiedDate(DateTime.now());
			model.setName(umgInputName);
			getModelDAO().save(model);
			MappingDescriptor mappingDescriptor = mappingDelegate.generateMapping(umgInputName);

			String tidName = saveOutputMapping(umgInputName, BusinessConstants.TYPE_OUTPUT_MAPPING);
			mappingDescriptor.setTidName(tidName);

			mappingDescriptor.setDescription(description);
			mappingSaveRslt = mappingDelegate.saveMappingDescription(mappingDescriptor, umgInputName, "validate");
			tidname = mappingSaveRslt.getKey();

			List<String> tidParamNames = new ArrayList<>();
			tidParamNames.add("test");
			tidParamNames.add("SampleParameter2");

			Map<String, Boolean> mapIsReferenced = mappingDelegate.isReferenced(tidParamNames, tidname,
					BusinessConstants.TYPE_INPUT_MAPPING);

			assertNotNull(mapIsReferenced);
			assertTrue(mapIsReferenced.get("SampleParameter2"));
			assertFalse(mapIsReferenced.get("test"));
			mappingDelegate.deleteMapping(tidname);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	@Test
	public void testIsReferencedForOutputmapping() throws SystemException, BusinessException, IOException {

		KeyValuePair<String, List<ValidationError>> mappingSaveRslt = null;
		String tidname = null;
		InputStream inputStream = null;
		try {
			String type = BusinessConstants.TYPE_OUTPUT_MAPPING;
			String umgInputName = "flatndtest_output_2";
			Model model = createModel(umgInputName);
			ModelDefinition modelDefinition = new ModelDefinition();
			inputStream = this.getClass().getClassLoader().getResourceAsStream(xmlFile);
			byte[] ioDefinition = FileCopyUtils.copyToByteArray(inputStream);
			modelDefinition.setIoDefinition(ioDefinition);
			modelDefinition.setModel(model);
			modelDefinition.setType(type);
			model.setModelDefinition(modelDefinition);
			model.setCreatedBy(userName);
			model.setCreatedDate(DateTime.now());
			model.setLastModifiedBy(userName);
			model.setLastModifiedDate(DateTime.now());
			model.setName(umgInputName);
			getModelDAO().save(model);
			MappingDescriptor mappingDescriptor = mappingDelegate.generateMapping(umgInputName);
			mappingDescriptor.setDescription(description);
			mappingSaveRslt = mappingDelegate.saveMappingDescription(mappingDescriptor, umgInputName, "validate");
			tidname = mappingSaveRslt.getKey();

			List<String> tidParamNames = new ArrayList<>();
			tidParamNames.add("test");
			Map<String, Boolean> mapIsReferenced = mappingDelegate.isReferenced(tidParamNames, tidname,
					BusinessConstants.TYPE_OUTPUT_MAPPING);

			assertNull(mapIsReferenced);
			mappingDelegate.deleteMapping(tidname);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
		
	}

	@Test
	public void testgetMappingStatus() throws SystemException, BusinessException {
		Mapping mapping = createMapping("TIDIOMAPPINGListAlltest21",
				createModel("tidioModelListAllTest21", "desc", "doc", "sample", "text/xml", "sample"), tenantCode5,
				"tid for model_5", "Sample MID Json");
		assertNotNull(mapping);

		String mappingStatus = mappingDelegate.getMappingStatus("TIDIOMAPPINGListAlltest21");

		assertNotNull(mappingStatus);
		assertEquals("FINALIZED", mappingStatus);
		deleteMapping(mapping);

	}

}
