package com.ca.umg.runtime;

import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.config.ModeletConfig;
import com.ca.umg.modelet.runtime.ModeletRuntime;
import com.ca.umg.modelet.runtime.RuntimeProcess;
import com.ca.umg.modelet.runtime.factory.RuntimeFactory;
import com.ca.umg.modelet.runtime.factory.RuntimeProcessFactory;
import com.ca.umg.modelet.runtime.impl.MatlabRuntime;
import com.ca.umg.modelet.runtime.impl.MatlabRuntimeProcess;
import com.ca.umg.modelet.runtime.impl.RRuntime;
import com.ca.umg.modelet.runtime.impl.RRuntimeProcess;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ModeletConfig.class })
public class RuntimeProcessFatcoryTest {

    @Inject
    private RuntimeFactory runtimeFactory;

    @Inject
    private RuntimeProcessFactory processFactory;

    @Test
    @Ignore
    public void getRuntimeInstanceTest() throws SystemException {
        List<ModeletRuntime> modeletRuntimes = runtimeFactory.getRuntimeInstance();
        Assert.assertTrue(modeletRuntimes.get(0) instanceof MatlabRuntime);
        // modeletRuntime = runtimeFactory.getRuntimeInstance("R");
        Assert.assertTrue(modeletRuntimes.get(1) instanceof RRuntime);
    }

    @Test
    public void getRuntimeProcessInstanceTest() {
        RuntimeProcess runtimeProcess = processFactory.getRuntimeProcessInstance("MATLAB", runtimeFactory);
        Assert.assertTrue(runtimeProcess instanceof MatlabRuntimeProcess);
        runtimeProcess = processFactory.getRuntimeProcessInstance("R", runtimeFactory);
        Assert.assertTrue(runtimeProcess instanceof RRuntimeProcess);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getRuntimeProcessInstanceExceptionTest() {
        processFactory.getRuntimeProcessInstance("M", runtimeFactory);
    }

}
