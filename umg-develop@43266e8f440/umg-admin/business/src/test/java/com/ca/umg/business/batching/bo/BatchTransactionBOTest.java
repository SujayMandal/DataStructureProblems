package com.ca.umg.business.batching.bo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.batching.dao.BatchRuntimeTransactionMappingDAO;
import com.ca.umg.business.batching.dao.BatchTransactionDAO;
import com.ca.umg.business.batching.entity.BatchRuntimeTransactionMapping;
import com.ca.umg.business.batching.entity.BatchTransaction;
import com.ca.umg.business.model.AbstractModelTest;


@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
// TODO fix ignored test cases
public class BatchTransactionBOTest extends AbstractModelTest{
	
	/*@InjectMocks*/
    @Inject
    private BatchTransactionBO batchTransactionBO;
	
	@Inject
	private BatchTransactionDAO batchTransactionDAO;
	
	@Inject
	private BatchRuntimeTransactionMappingDAO batchRuntimeTransactionMappingDAO;
	
	private RequestContext requestContext;	
	
	
	@Before
	public void setup(){
		  requestContext = getLocalhostTenantContext();
	}

	@Test
	public void testCreateBatch() {		
		try {
			String batchId = createBatch("testbatch");
			assertNotNull(batchId);
			batchTransactionDAO.delete(batchId);
		} catch (SystemException | BusinessException e) {
			fail(e.getLocalizedMessage());
		}
		
		
	}

	@Test
	public void testGetBatch() {
		
		try {
			String batchId = createBatch("testbatch-1");
			assertNotNull(batchId);
			BatchTransaction bt =  batchTransactionBO.getBatch(batchId);
			assertNotNull(bt);
			assertTrue("Retrieved batch is not the same as the created batch",bt.getBatchInputFile().equals("testbatch-1"));
			batchTransactionDAO.delete(batchId);
		} catch (SystemException | BusinessException e) {
			fail(e.getLocalizedMessage());
		}
		
	}
	
	
	@Test
	public void testUpdateBatchTotalCount() {
		try {
			String batchId = createBatch("testbatch-1");
			assertNotNull(batchId);
			BatchTransaction bt = batchTransactionBO.updateBatch(batchId, 100, null);
			assertTrue("Batch size isn't getting updated correctly",100L==bt.getTotalRecords());
			batchTransactionDAO.delete(batchId);
		} catch (SystemException | BusinessException e) {
			fail(e.getLocalizedMessage());
		}
	}
	

	@Test
	public void testUpdateBatchDetails() {
		try {
			String batchId = createBatch("testbatch-1");
            batchTransactionBO.updateBatch(batchId, 100, 49, 51, "SUCCESS", null);
			BatchTransaction bt = batchTransactionBO.getBatch(batchId);
			assertEquals("Total batch size hasn't been updated correctly",bt.getTotalRecords().longValue(),100L);
			assertEquals("Success count hasn't been updated correctly",bt.getSuccessCount().longValue(),49L);
			assertEquals("Failure count hasn't been updated correctly",bt.getFailCount().longValue(),51L);
			assertTrue("Batch status hasn't been updated correctly",bt.getStatus().equals("SUCCESS"));
			batchTransactionDAO.delete(batchId);
		} catch (SystemException | BusinessException e) {
			fail(e.getLocalizedMessage());
		}
		
	}

	@Test
	public void testAddBatchTransactionMappings() {
		try {
			String batchId_1 = createBatch("testbatch-1");
			List<String> transactionIds = new ArrayList<String>(2);
			transactionIds.add("test_transaction_1");
			transactionIds.add("test_transaction_2");
			batchTransactionBO.addBatchTransactionMappings(batchId_1,transactionIds);
			assertEquals("Batch transaction mapping(s) not added successfully",batchRuntimeTransactionMappingDAO.findAll().size(),2);
			batchRuntimeTransactionMappingDAO.deleteAll();
			batchTransactionDAO.deleteAll();
		} catch (SystemException | BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Test
	public void testAddBatchTransactionMapping() {
		try {
			String batchId_1 = createBatch("testbatch-1");
			BatchRuntimeTransactionMapping brtm = new BatchRuntimeTransactionMapping();
			brtm.setBatchTransaction(batchId_1);
			brtm.setTransaction("test");
			batchTransactionBO.addBatchTransactionMapping(brtm);
			List<BatchRuntimeTransactionMapping> brtmList = batchRuntimeTransactionMappingDAO.findAll();
			assertNotNull(brtmList);
			batchRuntimeTransactionMappingDAO.deleteAll();
			batchTransactionDAO.deleteAll();
		} catch (SystemException | BusinessException e) {
			fail(e.getLocalizedMessage());
		}
	}

	private String createBatch(String batchFileName) throws SystemException, BusinessException{
		return batchTransactionBO.createBatch(batchFileName);
	}
}
