//package com.ca.umg.me2.bo;
//
//import static com.ca.umg.me2.pool.PoolManagerCreator.createModeletClientInfo;
//import static com.ca.umg.me2.pool.PoolManagerCreator.createPool;
//import static com.ca.umg.me2.pool.PoolManagerCreator.createPoolCriteia;
//import static com.ca.umg.me2.pool.PoolManagerCreator.createPoolUsageOrder;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.fail;
//import static org.mockito.Matchers.any;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import org.apache.logging.log4j.core.util.KeyValuePair;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import com.ca.umg.me2.exception.codes.ModelExecutorExceptionCodes;
//import com.ca.umg.me2.util.ModelExecConstants;
//import com.ca.umg.me2.util.ModelExecutionDistributor;
//import com.ca.umg.me2.util.ModeletResult;
//
//public class ModelExecutorBOTest {
//
//    @InjectMocks
//    private ModelExecutorBOImpl modelExecutorBO;
//
//    @Mock
//    private ModelExecutionDistributor modeletExecutionDistributor;
//
//    private ModeletClient modeletClient = null;
//
//    private ModeletClientInfo modeletClientInfo = null;
//
//    private KeyValuePair<ModeletClientInfo, ModeletClient> modeletDetails = null;
//
//    @Mock
//    private SystemParameterProvider systemParameterProvider;
//
//    private PoolManager poolManager;
//
//    @Before
//    public void setup() {
//        poolManager = new PoolManagerImpl();
//        modeletClientInfo = createModeletClientInfo();
//        createPool(poolManager);
//        createPoolCriteia(poolManager);
//        createPoolUsageOrder(poolManager);
//
//        MockitoAnnotations.initMocks(this);
//        modeletClient = mock(ModeletClient.class);
//        modeletClientInfo = mock(ModeletClientInfo.class);
//        modeletClientInfo.setHost("localhost");
//        modeletClientInfo.setPort(7901);
//        modeletDetails = new KeyValuePair<ModeletClientInfo, ModeletClient>(modeletClientInfo, modeletClient);
//
//        try {
//            doNothing().when(modeletClient).createConnection();
//            doNothing().when(modeletClient).shutdownConnection();
//        } catch (SystemException exp) {
//            fail(exp.getMessage());
//        }
//
//    }
//
//    @Test
//    public void testExecute() {
//        String modelInfo = "{\"headerInfo\":{\"modelName\":\"computeAQMKNPV\",\"version\":\"1.0\",\"engine\":\"matlab\",\"responseSize\":2},\"payload\":[       {\"fieldName\":\"FclsFeeStartPeriod\",\"sequence\":1,\"dataType\":\"double\",       \"collection\":false,\"value\":0.0 }]   }";
//        try {
//            // when(modeletExecutionDistributor.getAvailableFreeModelet(CRITERIA)).thenReturn(modeletDetails);
//            when(modeletClient.sendData(modelInfo)).thenReturn("\"response\":\"model executed successfully.\"");
//            when(systemParameterProvider.getParameter(ModelExecConstants.RETRY_COUNT)).thenReturn("3");
//
//            final ModeletResult modeletResult = new ModeletResult();
//            modelExecutorBO.executeModel(modelInfo, modeletResult);
//            assertNotNull(modeletResult);
//            assertNotNull(modeletResult.getModeletResponse());
//            verify(modeletClient, times(1)).sendData(modelInfo);
//        } catch (SystemException | BusinessException exp) {
//            fail(exp.getMessage());
//        }
//    }
//
//    @Test
//    public void testExecuteNoModelet() {
//        String modelInfo = "{\"headerInfo\":{\"modelName\":\"computeAQMKNPV\",\"version\":\"1.0\",\"engine\":\"matlab\",\"responseSize\":2},\"payload\":[       {\"fieldName\":\"FclsFeeStartPeriod\",\"sequence\":1,\"dataType\":\"double\",       \"collection\":false,\"value\":0.0 }]   }";
//        try {
//            // test for no modelet client in registry
//            when(systemParameterProvider.getParameter(ModelExecConstants.RETRY_COUNT)).thenReturn("3");
//
//            final ModeletResult modeletResult = new ModeletResult();
//            modelExecutorBO.executeModel(modelInfo, modeletResult);
//
//        } catch (SystemException exp) {
//            assertEquals(ModelExecutorExceptionCodes.MSE0000001, exp.getCode());
//        } catch (BusinessException exp) {
//            // TODO Auto-generated catch block
//            exp.printStackTrace();
//        }
//
//        try {
//            // test for no modelet client
//            modeletDetails.setValue(null);
//            // when(modeletExecutionDistributor.getAvailableFreeModelet(CRITERIA)).thenReturn(modeletDetails);
//            final ModeletResult modeletResult = new ModeletResult();
//            modelExecutorBO.executeModel(modelInfo, modeletResult);
//        } catch (SystemException | BusinessException exp) {
//            assertEquals(ModelExecutorExceptionCodes.MSE0000001, exp.getCode());
//        }
//    }
//
//    @Test
//    public void testExecuteNoConnection() {
//        String modelInfo = "{\"headerInfo\":{\"modelName\":\"computeAQMKNPV\",\"version\":\"1.0\",\"engine\":\"matlab\",\"responseSize\":2},\"payload\":[       {\"fieldName\":\"FclsFeeStartPeriod\",\"sequence\":1,\"dataType\":\"double\",       \"collection\":false,\"value\":0.0 }]   }";
//        try {
//            when(modeletExecutionDistributor.getAvailableFreeModelet(any(TransactionCriteria.class))).thenReturn(modeletDetails);
//            when(systemParameterProvider.getParameter(ModelExecConstants.RETRY_COUNT)).thenReturn("3");
//            doThrow(new SystemException("MSE000008", new Object[] { "" })).when(modeletClient).createConnection();
//            final ModeletResult modeletResult = new ModeletResult();
//            modelExecutorBO.executeModel(modelInfo, modeletResult);
//        } catch (SystemException | BusinessException exp) {
//            assertEquals("MSE000008", exp.getCode());
//        }
//    }
//
//    @Test
//    public void testExecutionFailure() {
//        String modelInfo = "{\"headerInfo\":{\"modelName\":\"computeAQMKNPV\",\"version\":\"1.0\",\"engine\":\"matlab\",\"responseSize\":2},\"payload\":[       {\"fieldName\":\"FclsFeeStartPeriod\",\"sequence\":1,\"dataType\":\"double\",       \"collection\":false,\"value\":0.0 }]   }";
//        try {
//            // when(modeletExecutionDistributor.getAvailableFreeModelet(CRITERIA)).thenReturn(modeletDetails);
//            when(systemParameterProvider.getParameter(ModelExecConstants.RETRY_COUNT)).thenReturn("3");
//            doThrow(new SystemException("MSE000009", new Object[] { "" })).when(modeletClient).sendData(modelInfo);
//            final ModeletResult modeletResult = new ModeletResult();
//            modelExecutorBO.executeModel(modelInfo, modeletResult);
//        } catch (SystemException | BusinessException exp) {
//            assertEquals("MSE000009", exp.getCode());
//        }
//    }
// }
