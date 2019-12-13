package com.ca.umg.modelet.common;

import com.ca.framework.core.exception.SystemException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by repvenk on 1/10/2017.
 */
public class RServeModel {


    private static final Logger LOGGER = LoggerFactory.getLogger(RModel.class);

    private final RServeDelegator rEngineInvoker;

    public RServeModel(final RServeDelegator rEngineInvoker) {
        this.rEngineInvoker = rEngineInvoker;
    }

    public Map<String, Object> executeModel(final List<com.ca.umg.modelet.lang.type.DataType> inputList,SystemInfo systemInfo,HeaderInfo headerInfo) throws SystemException {
        final long startTime = System.currentTimeMillis();
        String command = createRCommand(inputList);
        LOGGER.info("R command to be executed : {}", command);
        Map<String, Object> output = new HashMap<>();
        Object result = null;
        /* changes for UMG-5015 */
        try {
            result = rEngineInvoker.executeModel(command);
            output.put("command", command);
            output.put("result", result);
            output.put("rText", rEngineInvoker.getrText());
        } finally {
            final long endTime = System.currentTimeMillis();
            LOGGER.error("Actual R Model Execution time:" + (endTime - startTime));
        }
        LOGGER.error("rText is ================= "+output.get("rText"));

        return output;
    }

    private String createRCommand(final List<com.ca.umg.modelet.lang.type.DataType> inputList) {
        final StringBuffer strCommand = new StringBuffer();
        final String tmp = rEngineInvoker.getModelKey().getModelMethod() + "(";

        strCommand.append(tmp);

        if (inputList != null && !inputList.isEmpty()) {
            for (int i = 0; i < inputList.size(); i++) {
                if (inputList.get(i) == null) {
                    strCommand.append("NA");
                } else {
                    strCommand.append(inputList.get(i).toNative());
                }
                if (i < inputList.size() - 1) {
                    strCommand.append(", ");
                }
            }
        }

        strCommand.append(')');

        return strCommand.toString();
    }
}
