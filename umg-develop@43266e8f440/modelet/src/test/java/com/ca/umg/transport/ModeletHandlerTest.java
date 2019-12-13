package com.ca.umg.transport;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.client.HttpModeletClient;
import com.ca.modelet.client.ModeletClient;
import com.ca.modelet.client.SocketModeletClient;
import com.ca.umg.modelet.common.HeaderInfo;
import com.ca.umg.modelet.common.MatlabModel;
import com.ca.umg.modelet.common.SystemInfo;
import com.ca.umg.modelet.config.ModeletConfig;
import com.ca.umg.modelet.converter.Converter;
import com.ca.umg.modelet.runtime.factory.RuntimeProcessFactory;
import com.ca.umg.modelet.runtime.impl.MatlabRuntime;
import com.ca.umg.modelet.runtime.impl.MatlabRuntimeProcess;
import com.ca.umg.modelet.transport.factory.ModeletServerFactory;
import com.ca.umg.modelet.transport.handler.ModeletHttpHandler;
import com.ca.umg.modelet.transport.handler.ModeletSocketHandler;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ModeletConfig.class})

@Ignore
//TODO fix ignored test cases
public class ModeletHandlerTest {
    
    @Inject
    private ModeletServerFactory factory;
    
    @Spy
    private SystemInfo systemInfo;
    
    @Mock
    @Named(value="matRuntimeProcess")
    private MatlabRuntimeProcess runtimeProcess;
    
    @Mock
    @Named(value = "matlabConverter")
    public Converter converter;
    
    @Mock
    @Named(value = "matlabRuntime")
    private MatlabRuntime matlabRuntime;
    
    @Inject
    @Spy
    @InjectMocks
    private RuntimeProcessFactory runtimeProcessFactory;
    
    @Inject
    @Named(value="httpHandler")
    @InjectMocks
    @Spy
    private ModeletHttpHandler modeletHttpHandler;
    
    @Inject
    @Named(value="socketHandler")
    @InjectMocks
    @Spy
    private ModeletSocketHandler modeletSocketHandler;
    
    private String response = "[1,2,3]";
    
    @Before
    public void setup() {
    	try {
	        MockitoAnnotations.initMocks(this);
	        when(converter.unmarshall(any())).thenReturn(response);
	        doCallRealMethod().when(systemInfo).setServerType(any(String.class));
	        //doCallRealMethod().when(systemInfo).setRuntimeType(any(String.class));
	        systemInfo.setServerType("http");
	        //systemInfo.setRuntimeType("MATLAB");
	        doCallRealMethod().when(modeletHttpHandler).setSystemInfo(systemInfo);
	        modeletHttpHandler.setSystemInfo(systemInfo);
	        //doCallRealMethod().when(systemInfo).getRuntimeType();
	        MatlabModel model = Mockito.mock(MatlabModel.class);
	        
            doCallRealMethod().when(runtimeProcess).setConverter(converter);
            runtimeProcess.setConverter(converter);
            doCallRealMethod().when(runtimeProcess).setMatlabRuntime(matlabRuntime);
            runtimeProcess.setMatlabRuntime(matlabRuntime);
            doCallRealMethod().when(runtimeProcess).execute(any(Object.class), systemInfo);
            doNothing().when(model).executeModel(anyList(), anyList());
            when(matlabRuntime.getModel(any(HeaderInfo.class))).thenReturn(model);
            Mockito.doNothing().when(runtimeProcess).releaseMemory(anyList());
        } catch (BusinessException | SystemException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void initializeHttpServer() {
        ModeletClient client = null;
        try {
            factory.initializeServer("http", 7775);
            Thread.sleep(1000);
            systemInfo.setServerType("http");
            //systemInfo.setRuntimeType("MATLAB");
            client = new HttpModeletClient("localhost", 7775, "");
            client.createConnection();
            String response = client.sendData("{" +
                    "\"headerInfo\":{" + 
                    "\"modelName\":\"computeAQMKNPV\"," +
                    "\"version\":\"1.0\"," +
                    "\"engine\":\"matlab\"," + 
                    "\"responseSize\":2" +
                    "}," + 
                    "\"payload\":" + 
                    "[" + 
                    "{" +
                    "\"modelParameterName\":\"FclsFeeStartPeriod\","+
                    "\"sequence\":1," +
                    "\"dataType\":\"double\"," +
                    "\"collection\":false, " +
                    "\"value\":0.0" +
                    "}]}");
            Assert.assertTrue(response.contains(this.response));
            client.shutdownConnection();
        } catch (SystemException e) {
            Assert.fail();
        } catch(Exception e) {
            Assert.fail();
        }
    }
    
    public void httpServerHandlerExceptionTest() throws SystemException {
        ModeletClient client = null;
        try {
            factory.initializeServer("http", 7776);
            Thread.sleep(1000);
            systemInfo.setServerType("http");
            //systemInfo.setRuntimeType("MATLAB");
            client = new HttpModeletClient("localhost", 7776, "");
            client.createConnection();
            String response = client.sendData("{" +
                    "\"headerInfo\":" + 
                    "\"modelName\":\"computeAQMKNPV\"," +
                    "\"version\":\"1.0\"," +
                    "\"engine\":\"matlab\"," + 
                    "\"responseSize\":2" +
                    "}," + 
                    "\"payload\":" + 
                    "[" + 
                    "{" +
                    "\"modelParameterName\":\"FclsFeeStartPeriod\","+
                    "\"sequence\":1," +
                    "\"dataType\":\"double\"," +
                    "\"collection\":false, " +
                    "\"value\":0.0" +
                    "}]}");
            Assert.assertTrue(response.contains("errorCode"));
            client.shutdownConnection();
        } catch (SystemException e) {
            throw e;
        } catch(Exception e) {
            Assert.fail();
        }
    }
    
    @Test
    public void initializeSocketServer() throws SystemException {
        ModeletClient client = null;
        try {
            factory.initializeServer("socket", 7777);
            Thread.sleep(1000);
            systemInfo.setServerType("socket");
            //systemInfo.setRuntimeType("MATLAB");
            client = new SocketModeletClient("localhost", 7777);
            client.createConnection();
            String response = client.sendData("{" +
                    "\"headerInfo\":{" + 
                    "\"modelName\":\"computeAQMKNPV\"," +
                    "\"version\":\"1.0\"," +
                    "\"engine\":\"matlab\"," + 
                    "\"responseSize\":2" +
                    "}," + 
                    "\"payload\":" + 
                    "[" + 
                    "{" +
                    "\"modelParameterName\":\"FclsFeeStartPeriod\","+
                    "\"sequence\":1," +
                    "\"dataType\":\"double\"," +
                    "\"collection\":false, " +
                    "\"value\":0.0" +
                    "}]}");
            Assert.assertTrue(response.contains(this.response));
            client.shutdownConnection();
        } catch (SystemException e) {
            Assert.fail(e.getMessage());
        } catch(Exception e) {
            Assert.fail();
        }
    }
    
    @Test
    @Ignore
    public void socketServerHandlerExceptionTest() throws SystemException {
        ModeletClient client = null;
        try {
            factory.initializeServer("socket", 7778);
            Thread.sleep(1000);
            systemInfo.setServerType("socket");
            //systemInfo.setRuntimeType("MATLAB");
            client = new SocketModeletClient("localhost", 7778);
            client.createConnection();
            String response = client.sendData("{" +
                    "\"headerInfo\":" + 
                    "\"modelName\":\"computeAQMKNPV\"," +
                    "\"version\":\"1.0\"," +
                    "\"engine\":\"matlab\"," + 
                    "\"responseSize\":2" +
                    "}," + 
                    "\"payload\":" + 
                    "[" + 
                    "{" +
                    "\"modelParameterName\":\"FclsFeeStartPeriod\","+
                    "\"sequence\":1," +
                    "\"dataType\":\"double\"," +
                    "\"collection\":false, " +
                    "\"value\":0.0" +
                    "}]}");
            Assert.assertTrue(response.contains("errorCode"));
            client.shutdownConnection();
        } catch (SystemException e) {
            Assert.fail();
        } catch(Exception e) {
            Assert.fail();
        }
    }
    
}
