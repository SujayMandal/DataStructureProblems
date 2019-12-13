/**
 * 
 */
package com.ca.umg.rt.transformer;

import static com.ca.umg.rt.util.MessageVariables.ME2_WAITING_TIME;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.support.MessageBuilder;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.modelet.ModeletClientInfo;
import com.ca.pool.model.TransactionCriteria;
import com.ca.umg.me2.bo.ModelExecutorBO;
import com.ca.umg.me2.util.ModelExecResponse;
import com.ca.umg.me2.util.ModeletResult;
import com.ca.umg.modelet.constants.ErrorCodes;
import com.ca.umg.rt.exception.codes.RuntimeExceptionCode;
import com.ca.umg.rt.util.MessageVariables;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class responsible for delegating model execution request to model
 * 
 * @author kamathan
 *
 */
public class Me2Executor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Me2Executor.class);

    private static final String CLIENT_ID = "clientID";
    private ModelExecutorBO modelExecutorBO;

    @ServiceActivator
    public Object execute(Message<?> message) throws SystemException, BusinessException {
        LOGGER.debug("Received request to execute model");
        long startTime = System.currentTimeMillis();
        Map payload = (Map) message.getPayload();
        TransactionCriteria transactionCriteria = (TransactionCriteria) payload.get(MessageVariables.TRANSACTION_CRITERIA);
        Map requestMap = (Map) payload.get("tenantRequestHeader");
        if(requestMap.containsKey(CLIENT_ID) && requestMap.get(CLIENT_ID) != null){
        	transactionCriteria.setClientID(requestMap.get(CLIENT_ID).toString());
        }
        ModeletResult modeletResult = new ModeletResult();
        modeletResult.setTransactionCriteria(transactionCriteria);
        payload.remove(MessageVariables.TRANSACTION_CRITERIA);

        String modelInput = null;

        long writeValueAsStringStartTime = System.currentTimeMillis();
        modelInput = (String)payload.get(MessageVariables.MODEL_REQUEST_STRING);
        LOGGER.debug("Write Value As String time:: {} ", System.currentTimeMillis() - writeValueAsStringStartTime);
        Map<String, Object> modelResponse = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
        	if(requestMap.containsKey(CLIENT_ID) && requestMap.get(CLIENT_ID) != null){
                Map<String, Map> map = (Map<String, Map>)mapper.readValue((String)payload.get(MessageVariables.MODEL_REQUEST_STRING), Map.class);
              Map<String , Map> headerInfo = map.get("headerInfo");
              Map<String , Object> transactionInfo = headerInfo.get("transactionCriteria");
              transactionInfo.put(CLIENT_ID, requestMap.get(CLIENT_ID));
              modelInput = mapper.writeValueAsString(map);
                } else {
                modelInput = (String)payload.get(MessageVariables.MODEL_REQUEST_STRING);
                }

            modelResponse = modelExecutorBO.executeModel(modelInput, modeletResult);
            Map<String, Object> responseHeaderInfo =  (Map<String, Object>)modelResponse.get("responseHeaderInfo");
            if(responseHeaderInfo!=null && StringUtils.equals(ErrorCodes.ME00045, (String)responseHeaderInfo.get("errorCode"))) {
            	throw new SystemException(RuntimeExceptionCode.RSE000835, new Object[] {(String)responseHeaderInfo.get("errorMessage")});
            }
        } catch(SystemException | BusinessException e) {
        	setModeletInfoInException(payload, modeletResult);
            payload.put(ME2_WAITING_TIME, System.currentTimeMillis() - startTime);
            throw e;
        } catch(IOException e) {
            setModeletInfoInException(payload, modeletResult);
          payload.put(ME2_WAITING_TIME, System.currentTimeMillis() - startTime);
      }

        ModelExecResponse<Map<String, Object>> modelExecResponse = new ModelExecResponse<Map<String, Object>>();
        modelExecResponse.setResponse(modelResponse);
        modelExecResponse.setPoolCriteria(modeletResult.getTransactionCriteria().toString());
        setModeletInfo(modelExecResponse, modeletResult);
        modelExecResponse.setMe2ExecutionTime(System.currentTimeMillis() - startTime);

        payload.put("me2Response", modelExecResponse);
        LOGGER.debug("Executed model request successfully.");
        LOGGER.debug("ME2 execution time {}.", System.currentTimeMillis() - startTime);
        return MessageBuilder.withPayload(payload).copyHeaders(message.getHeaders()).build();
    }

    public void setModelExecutorBO(ModelExecutorBO modelExecutorBO) {
        this.modelExecutorBO = modelExecutorBO;
    }

    /*private TransactionCriteria fetchTransactionCriteria(Map modelRequest) {
        Map header = (Map) modelRequest.get("headerInfo");
        return (TransactionCriteria) header.get("transactionCriteria");

    }*/
    
    //set modelet info in case exception has occured in getting model response
    private void setModeletInfoInException(final Map payload, final ModeletResult modeletResult){
    	ModelExecResponse<Map<String, Object>> modelExecResponse = new ModelExecResponse<Map<String, Object>>();
        if(modeletResult.getTransactionCriteria() != null){
        	modelExecResponse.setPoolCriteria(modeletResult.getTransactionCriteria().toString());
        }	
        setModeletInfo(modelExecResponse, modeletResult);
        payload.put("me2Response", modelExecResponse);
    }

    private void setModeletInfo(final ModelExecResponse<Map<String, Object>> execResponse, final ModeletResult modeletResult) {
        final ModeletClientInfo modeletClientInfo = modeletResult.getModeletClientInfo();
        if (modeletClientInfo != null) {
            execResponse.setHost(modeletClientInfo.getHost());
            execResponse.setPort(modeletClientInfo.getPort());
            execResponse.setMemberHost(modeletClientInfo.getMemberHost());
            execResponse.setMemberPort(modeletClientInfo.getMemberPort());
            execResponse.setPoolName(modeletClientInfo.getPoolName());
            execResponse.setServerType(modeletClientInfo.getServerType());
            execResponse.setContextPath(modeletClientInfo.getContextPath());
            execResponse.setrServePort(modeletClientInfo.getrServePort());
        }
    }
}
