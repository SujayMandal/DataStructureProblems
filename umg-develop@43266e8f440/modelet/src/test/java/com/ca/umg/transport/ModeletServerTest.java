package com.ca.umg.transport;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
import com.ca.umg.modelet.converter.impl.MatlabConverter;
import com.ca.umg.modelet.runtime.impl.MatlabRuntime;
import com.ca.umg.modelet.runtime.impl.MatlabRuntimeProcess;
import com.ca.umg.modelet.transport.factory.ModeletServerFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ModeletConfig.class})
@Ignore
// TODO fix ignored test cases
public class ModeletServerTest {
    
    @Inject
    private ModeletServerFactory factory;
    
    @Inject
    private SystemInfo systemInfo;
    
    @Inject
    @Named("matRuntimeProcess")
    private MatlabRuntimeProcess runtimeProcess;
    
    @Before
    public void setup() {
    	 try {
	        Converter mockConverter = Mockito.mock(MatlabConverter.class);
	        when(mockConverter.unmarshall(any())).thenReturn("response");
	        MatlabModel model = Mockito.mock(MatlabModel.class);
       
            doNothing().when(model).executeModel(anyList(), anyList());
       
            MatlabRuntime matlabRuntime = Mockito.mock(MatlabRuntime.class);
            when(matlabRuntime.getModel(any(HeaderInfo.class))).thenReturn(model);
            runtimeProcess.setConverter(mockConverter);
            runtimeProcess.setMatlabRuntime(matlabRuntime);
         } catch (BusinessException | SystemException e) {
             
         }

    }
    
    @Ignore
    public void httpServerHandlerTest() {
        ModeletClient client = null;
        try {
            factory.initializeServer("http", 7771);
            Thread.sleep(1000);
            systemInfo.setServerType("http");
            //systemInfo.setRuntimeType("MATLAB");
            client = new HttpModeletClient("localhost", 7771, "");
            client.createConnection();
            String reposnse = client.sendData("{" +
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
            client.shutdownConnection();
            Assert.assertNotNull(reposnse);
        } catch (SystemException e) {
            Assert.fail(e.getMessage());
        } catch(Exception e) {
            Assert.fail(e.getMessage());
        }
    }
    
    @Ignore
    public void socketServerHandlerTest() {
        ModeletClient client = null;
        try {
            factory.initializeServer("socket", 7772);
            Thread.sleep(1000);
            systemInfo.setServerType("socket");
            //systemInfo.setRuntimeType("MATLAB");
            client = new SocketModeletClient("localhost", 7772);
            client.createConnection();
            String reposnse = client.sendData("{" +
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
            Assert.assertNotNull(reposnse);
            client.shutdownConnection();
        } catch (SystemException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } catch(Exception e) {
            Assert.fail(e.getMessage());
        }
    }
    
    @Test(expected=SystemException.class)
    public void socketServerHandlerReusePortTest() throws SystemException {
        systemInfo.setServerType("socket");
        factory.initializeServer("socket", 7773);
        //creating new server on same port throws exception
        factory.initializeServer("socket", 7773);
    }
    
    @Test(expected=SystemException.class)
    public void socketServerHandlerIllegalPortTest() throws SystemException {
        systemInfo.setServerType("socket");
        factory.initializeServer("socket", -1);
    }
    
    @Test(expected=SystemException.class)
    public void httpServerHandlerReusePortTest() throws SystemException {
        systemInfo.setServerType("http");
        factory.initializeServer("http", 7774);
        //creating new server on same port throws exception
        factory.initializeServer("http", 7774);
    }
    
    @Test(expected=SystemException.class)
    public void httpServerHandlerIllegalPortTest() throws SystemException {
        systemInfo.setServerType("http");
        factory.initializeServer("http", -1);
    }
    
}
