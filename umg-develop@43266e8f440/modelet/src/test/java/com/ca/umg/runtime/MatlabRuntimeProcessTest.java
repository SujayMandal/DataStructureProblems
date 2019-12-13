package com.ca.umg.runtime;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.common.FieldInfo;
import com.ca.umg.modelet.common.HeaderInfo;
import com.ca.umg.modelet.common.MatlabModel;
import com.ca.umg.modelet.common.ModelRequestInfo;
import com.ca.umg.modelet.common.ModelResponseInfo;
import com.ca.umg.modelet.config.ModeletConfig;
import com.ca.umg.modelet.converter.Converter;
import com.ca.umg.modelet.converter.impl.MatlabConverter;
import com.ca.umg.modelet.runtime.impl.MatlabRuntime;
import com.ca.umg.modelet.runtime.impl.MatlabRuntimeProcess;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ModeletConfig.class })

@Ignore
//TODO fix ignored test cases
public class MatlabRuntimeProcessTest {

    

    @Spy
    @Inject
    @Named("matRuntimeProcess")
    private MatlabRuntimeProcess runtimeProcess;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Converter mockConverter = Mockito.mock(MatlabConverter.class);
        try {

            // TODO:MS - added this
            when(mockConverter.unmarshall(any())).thenReturn(buildResponse());
            MatlabModel model = Mockito.mock(MatlabModel.class);

            doNothing().when(model).executeModel(anyList(), anyList());

            MatlabRuntime matlabRuntime = Mockito.mock(MatlabRuntime.class);
            when(matlabRuntime.getModel(any(HeaderInfo.class))).thenReturn(model);

            when(matlabRuntime.getModel(any(HeaderInfo.class))).thenReturn(model);

            runtimeProcess.setConverter(mockConverter);
            runtimeProcess.setMatlabRuntime(matlabRuntime);
            doNothing().when(runtimeProcess).releaseMemory(anyList());
        } catch (BusinessException | SystemException e1) {

        }
    }

    private String buildResponse() {
        return "null";
    }

    @Test
    public void executeTest() {
        HeaderInfo headerInfo = new HeaderInfo();
        headerInfo.setModelName("aqmk");
        headerInfo.setEngine("MATLAB");
        headerInfo.setResponseSize(2);
        headerInfo.setVersion("1.0");
        List<FieldInfo> payload = new ArrayList<>();
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setDataType("string");
        fieldInfo.setModelParameterName("field1");
        fieldInfo.setSequence("1");
        fieldInfo.setValue(new Object());
        payload.add(fieldInfo);
        ModelRequestInfo info = new ModelRequestInfo();
        info.setHeaderInfo(headerInfo);
        info.setPayload(payload);
        try {
            ModelResponseInfo modelResponseInfo = runtimeProcess.execute(info, null);
            Assert.assertEquals(info.getHeaderInfo().getModelName(), modelResponseInfo.getModelName());
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }

    @Test
    public void executeModelTest() {
        HeaderInfo headerInfo = new HeaderInfo();
        headerInfo.setModelName("aqmk");
        headerInfo.setEngine("MATLAB");
        headerInfo.setResponseSize(2);
        headerInfo.setVersion("1.0");
        List<FieldInfo> payload = new ArrayList<>();
        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setDataType("string");
        fieldInfo.setModelParameterName("field1");
        fieldInfo.setSequence("1");
        fieldInfo.setValue(new Object());
        payload.add(fieldInfo);
        ModelRequestInfo info = new ModelRequestInfo();
        info.setHeaderInfo(headerInfo);
        info.setPayload(payload);
        try {
            ModelResponseInfo modelResponseInfo = runtimeProcess.execute(info, null);
            Assert.assertEquals(info.getHeaderInfo().getModelName(), modelResponseInfo.getModelName());
        } catch (BusinessException | SystemException e) {
            Assert.fail();
        }
    }

}
