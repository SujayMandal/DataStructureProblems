package com.ca.umg.business.transaction.mongo.dao;

import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.ca.framework.core.constants.FrameworkConstant;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.exception.codes.FrameworkExceptionCodes;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;



public class MongoTransactionDAOTest {

    @InjectMocks
    private MongoTransactionDAO mongoTransactionDAO = new MongoTransactionDAOImpl();

    @Mock
    private MongoTemplate mongoTemplate;

    @Before
    public void setRequestContext() {
        initMocks(this);
        Properties p = new Properties();
        p.setProperty("TENANT_CODE", "localhost");
        new RequestContext(p);
    }

    @Test
    public void testGetTenantAndModelIO() throws BusinessException, UnknownHostException, IOException, SystemException {
        TransactionDocument txnDocument = new TransactionDocument();
        txnDocument.setTransactionId("12345678");
        insertTxnDocument(txnDocument);
        Mockito.when(mongoTransactionDAO.getTenantAndModelIO("12345678")).thenReturn(txnDocument);
        TransactionDocument doc = mongoTransactionDAO.getTenantAndModelIO("12345678");
        Assert.assertNotNull(doc);
    }

    @Test
    public void testGetTenantAndModelIOWithE() throws BusinessException, UnknownHostException, IOException, SystemException {
        try {
            Mockito.doThrow(SystemException.newSystemException(FrameworkExceptionCodes.BSE000125, new Object[] { 12345678 }))
                    .when(
                    mongoTransactionDAO.getTenantAndModelIO("12345678"));
            mongoTransactionDAO.getTenantAndModelIO("12345678");
        } catch (SystemException exp) {
            Assert.assertNotNull(exp);
        }
    }

    private void insertTxnDocument(TransactionDocument txnDoc) {
        mongoTemplate.save(txnDoc, RequestContext.getRequestContext().getTenantCode() + FrameworkConstant.DOCUMENTS);
    }

    @After
    public void deleteTxns() {
        Properties p = new Properties();
        p.setProperty("TENANT_CODE", "localhost");
        new RequestContext(p);
        mongoTemplate.dropCollection(RequestContext.getRequestContext().getTenantCode() + FrameworkConstant.DOCUMENTS);
    }


}
