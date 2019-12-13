package com.ca.umg.transport;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.config.ModeletConfig;
import com.ca.umg.modelet.transport.ModletServer;
import com.ca.umg.modelet.transport.factory.ModeletServerFactory;
import com.ca.umg.modelet.transport.impl.HttpModletServer;
import com.ca.umg.modelet.transport.impl.ModletSocketServer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ModeletConfig.class })
public class ModeletServerFactoryTest {

    @Inject
    private ModeletServerFactory factory;

    @Test
    public void getServerInstance() {
        ModletServer modeletServer = factory.getServerInstance("http");
        Assert.assertTrue(modeletServer instanceof HttpModletServer);
        modeletServer = factory.getServerInstance("socket");
        Assert.assertTrue(modeletServer instanceof ModletSocketServer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getServerInstanceException() throws SystemException {
        factory.getServerInstance("httpsd");
    }

}
