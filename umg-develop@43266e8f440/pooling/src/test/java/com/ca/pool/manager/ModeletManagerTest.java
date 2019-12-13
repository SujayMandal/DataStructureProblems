/**
 * 
 */
package com.ca.pool.manager;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.connection.ConnectorType;
import com.ca.framework.core.connection.SSHConnector;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterConstants;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.modelet.ModeletClientInfo;
import com.ca.modelet.client.ModeletClient;
import com.ca.modelet.common.ServerType;
import com.ca.pool.model.ExecutionLanguage;
import com.hazelcast.core.IMap;

/**
 * @author kamathan
 *
 */
public class ModeletManagerTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private CacheRegistry cacheRegistry;

    @Mock
    private ModeletHelper modeletHelper;

    @InjectMocks
    private ModeletManager modeletManager = new ModeletManagerImpl();

    /**
     * @throws java.lang.Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Before
    public void setUp() throws Exception {
        initMocks(this);

        SSHConnector sshConnector = mock(SSHConnector.class);
        when(applicationContext.getBean(SSHConnector.class)).thenReturn(sshConnector);

        doNothing().when(sshConnector).openConnection();
        when(sshConnector.executeCommand(any(String.class))).thenReturn(true);
        doNothing().when(sshConnector).closeConnection();

        ModeletClient modeletClient = mock(ModeletClient.class);
        when(modeletHelper.buildModeletClient(any(ModeletClientInfo.class))).thenReturn(modeletClient);

        doNothing().when(modeletClient).createConnection();
        when(modeletClient.sendData(anyString())).thenReturn("");
    }

    /**
     * Test method for
     * {@link com.ca.pool.manager.ModeletManagerImpl#startModelet(com.ca.modelet.ModeletClientInfo, java.lang.String)} .
     */
    @Test
    public void testStartModelet() {
        try {

            IMap iMap = mock(IMap.class);

            when(iMap.get(SystemParameterConstants.R_MODELET_STARTUP_SCRIPT_RSERVE)).thenReturn(
                    "export LD_LIBRARY_PATH=/usr/lib64/R/library/rJava/jri; export R_HOME=/usr/lib64/R/; nohup java -XX:MaxPermSize=256m -Xmx1024m  -Druntime=MATLAB -Dlogroot=#port# -Dloglevel=#port# -Dport=#port# -DserverType=#serverType# -DsanPath=/sanpath -Dworkspace=/opt/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true   -Dlog4j.configurationFile=file:/opt/raconf/log4j2.xml -Dhazelcast.config=/opt/raconf/hazelcast-config.xml -DhttpConnectionPooling.properties=file:/opt/raconf/httpConnectionPooling.properties -DrunMatlab=#runMatlab# -DrunR=#runR# -jar /opt/umg/modelet.one-jar.jar > /opt/umg/#port#.out 2>&1 &");
            when(iMap.get(SystemParameterConstants.SSH_PORT)).thenReturn(22);
            when(iMap.get(SystemParameterConstants.SSH_KEY)).thenReturn(null);
            when(iMap.get(SystemParameterConstants.SSH_USER)).thenReturn("root");
            when(iMap.get(SystemParameterConstants.SSH_PASSWORD)).thenReturn(null);
            when(iMap.get(SystemParameterConstants.SSH_IDENTITY)).thenReturn(null);
            when(iMap.get(SystemParameterConstants.MODELET_RESTART_DELAY)).thenReturn("1");

            Mockito.when(cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER)).thenReturn(iMap);

            ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
            modeletClientInfo.setHost("localhost");
            modeletClientInfo.setPort(7900);
            modeletClientInfo.setExecutionLanguage(ExecutionLanguage.R.getValue());
            modeletClientInfo.setServerType(ServerType.SOCKET.getServerType());
            modeletClientInfo.setExecEnvironment("Linux");
            modeletManager.startModelet(modeletClientInfo, ConnectorType.SSH.getType());
        } catch (SystemException | BusinessException e) {
            fail(e.getMessage());
        }
    }

    @Test(expected = SystemException.class)
    public void testStartModeletFailNoCommand() throws SystemException, BusinessException {
        IMap iMap = mock(IMap.class);
        when(iMap.get(SystemParameterConstants.R_MODELET_STARTUP_SCRIPT)).thenReturn(null);
        when(iMap.get(SystemParameterConstants.SSH_PORT)).thenReturn(22);
        when(iMap.get(SystemParameterConstants.SSH_KEY)).thenReturn(null);
        when(iMap.get(SystemParameterConstants.SSH_USER)).thenReturn("root");
        when(iMap.get(SystemParameterConstants.SSH_PASSWORD)).thenReturn(null);
        when(iMap.get(SystemParameterConstants.SSH_IDENTITY)).thenReturn(null);
        when(iMap.get(SystemParameterConstants.MODELET_RESTART_DELAY)).thenReturn("1");
        Mockito.when(cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER)).thenReturn(iMap);

        ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
        modeletClientInfo.setHost("localhost");
        modeletClientInfo.setPort(7900);
        modeletClientInfo.setExecutionLanguage(ExecutionLanguage.R.getValue());
        modeletClientInfo.setServerType(ServerType.SOCKET.getServerType());
        modeletClientInfo.setExecEnvironment("Linux");
        modeletManager.startModelet(modeletClientInfo, ConnectorType.SSH.getType());
    }

    @Test(expected = Exception.class)
    public void testStartModeletFailNoConnectorType() throws SystemException, BusinessException {
        IMap iMap = mock(IMap.class);
        when(iMap.get(SystemParameterConstants.R_MODELET_STARTUP_SCRIPT)).thenReturn(
                "export LD_LIBRARY_PATH=/usr/lib64/R/library/rJava/jri; export R_HOME=/usr/lib64/R/; nohup java -XX:MaxPermSize=256m -Xmx1024m  -Druntime=MATLAB -Dlogroot=#port# -Dloglevel=#port# -Dport=#port# -DserverType=#serverType# -DsanPath=/sanpath -Dworkspace=/opt/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true   -Dlog4j.configurationFile=file:/opt/raconf/log4j2.xml -Dhazelcast.config=/opt/raconf/hazelcast-config.xml -DhttpConnectionPooling.properties=file:/opt/raconf/httpConnectionPooling.properties -DrunMatlab=#runMatlab# -DrunR=#runR# -jar /opt/umg/modelet.one-jar.jar > /opt/umg/#port#.out 2>&1 &");
        when(iMap.get(SystemParameterConstants.SSH_PORT)).thenReturn(22);
        when(iMap.get(SystemParameterConstants.SSH_KEY)).thenReturn(null);
        when(iMap.get(SystemParameterConstants.SSH_USER)).thenReturn("root");
        when(iMap.get(SystemParameterConstants.SSH_PASSWORD)).thenReturn(null);
        when(iMap.get(SystemParameterConstants.SSH_IDENTITY)).thenReturn(null);
        when(iMap.get(SystemParameterConstants.MODELET_RESTART_DELAY)).thenReturn("1");
        Mockito.when(cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER)).thenReturn(iMap);

        ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
        modeletClientInfo.setHost("localhost");
        modeletClientInfo.setPort(7900);
        modeletClientInfo.setExecutionLanguage(ExecutionLanguage.R.getValue());
        modeletClientInfo.setServerType(ServerType.SOCKET.getServerType());
        modeletClientInfo.setExecEnvironment("Linux");
        modeletManager.startModelet(modeletClientInfo, "invalid");
    }

    /**
     * Test method for {@link com.ca.pool.manager.ModeletManagerImpl#stopModelet(com.ca.modelet.ModeletClientInfo)}.
     */
    @Test
    public void testStopModelet() {
        try {

            IMap iMap = mock(IMap.class);

            when(iMap.get(SystemParameterConstants.R_MODELET_STARTUP_SCRIPT)).thenReturn(
                    "export LD_LIBRARY_PATH=/usr/lib64/R/library/rJava/jri; export R_HOME=/usr/lib64/R/; nohup java -XX:MaxPermSize=256m -Xmx1024m  -Druntime=MATLAB -Dlogroot=#port# -Dloglevel=#port# -Dport=#port# -DserverType=#serverType# -DsanPath=/sanpath -Dworkspace=/opt/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true   -Dlog4j.configurationFile=file:/opt/raconf/log4j2.xml -Dhazelcast.config=/opt/raconf/hazelcast-config.xml -DhttpConnectionPooling.properties=file:/opt/raconf/httpConnectionPooling.properties -DrunMatlab=#runMatlab# -DrunR=#runR# -jar /opt/umg/modelet.one-jar.jar > /opt/umg/#port#.out 2>&1 &");
            when(iMap.get(SystemParameterConstants.SSH_PORT)).thenReturn(22);
            when(iMap.get(SystemParameterConstants.SSH_KEY)).thenReturn(null);
            when(iMap.get(SystemParameterConstants.SSH_USER)).thenReturn("root");
            when(iMap.get(SystemParameterConstants.SSH_PASSWORD)).thenReturn(null);
            when(iMap.get(SystemParameterConstants.SSH_IDENTITY)).thenReturn(null);
            when(iMap.get(SystemParameterConstants.MODELET_RESTART_DELAY)).thenReturn("1");

            ModeletClient modeletClient = mock(ModeletClient.class);
            when(modeletHelper.buildModeletClient(any(ModeletClientInfo.class))).thenReturn(modeletClient);

            doNothing().when(modeletClient).createConnection();
            when(modeletClient.sendData(anyString())).thenReturn("");

            ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
            modeletClientInfo.setHost("localhost");
            modeletClientInfo.setPort(7900);
            modeletClientInfo.setExecutionLanguage(ExecutionLanguage.R.getValue());
            

            modeletManager.stopModelet(modeletClientInfo);

        } catch (SystemException | BusinessException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test method for
     * {@link com.ca.pool.manager.ModeletManagerImpl#restartModelet(com.ca.modelet.ModeletClientInfo, java.util.Map)}.
     */
    @Test
    public void testRestartModelet() {
        try {
            IMap iMap = mock(IMap.class);

            when(iMap.get(SystemParameterConstants.R_MODELET_STARTUP_SCRIPT)).thenReturn(
                    "export LD_LIBRARY_PATH=/usr/lib64/R/library/rJava/jri; export R_HOME=/usr/lib64/R/; nohup java -XX:MaxPermSize=256m -Xmx1024m  -Druntime=MATLAB -Dlogroot=#port# -Dloglevel=#port# -Dport=#port# -DserverType=#serverType# -DsanPath=/sanpath -Dworkspace=/opt/umg/matlab_workspace -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector -DisThreadContextMapInheritable=true   -Dlog4j.configurationFile=file:/opt/raconf/log4j2.xml -Dhazelcast.config=/opt/raconf/hazelcast-config.xml -DhttpConnectionPooling.properties=file:/opt/raconf/httpConnectionPooling.properties -DrunMatlab=#runMatlab# -DrunR=#runR# -jar /opt/umg/modelet.one-jar.jar > /opt/umg/#port#.out 2>&1 &");
            when(iMap.get(SystemParameterConstants.SSH_PORT)).thenReturn(22);
            when(iMap.get(SystemParameterConstants.SSH_KEY)).thenReturn(null);
            when(iMap.get(SystemParameterConstants.SSH_USER)).thenReturn("root");
            when(iMap.get(SystemParameterConstants.SSH_PASSWORD)).thenReturn(null);
            when(iMap.get(SystemParameterConstants.SSH_IDENTITY)).thenReturn(null);
            when(iMap.get(SystemParameterConstants.MODELET_RESTART_DELAY)).thenReturn("1");

            Mockito.when(cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER)).thenReturn(iMap);

            ModeletClientInfo modeletClientInfo = new ModeletClientInfo();
            modeletClientInfo.setHost("localhost");
            modeletClientInfo.setPort(7900);
            modeletClientInfo.setExecutionLanguage(ExecutionLanguage.R.getValue());
            modeletManager.restartModelet(modeletClientInfo, new HashMap<String, String>());
        } catch (SystemException e) {
            fail(e.getMessage());
        }
    }

}
