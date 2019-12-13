package com.ca.umg.modelet.transport.handler;

import static com.ca.framework.core.exception.BusinessException.newBusinessException;
import static com.ca.modelet.ModelCommands.DESTROY_SERVER;
import static com.ca.modelet.ModelCommands.EXECUTE;
import static com.ca.modelet.ModelCommands.GET_STATUS;
import static com.ca.modelet.ModelCommands.LOAD_MODEL;
import static com.ca.modelet.ModelCommands.START_RSERVE;
import static com.ca.modelet.ModelCommands.STOP_RSERVE;
import static com.ca.modelet.ModelCommands.UNLOAD_MODEL;
import static com.ca.modelet.ModelCommands.getModelCommands;
import static com.ca.umg.modelet.common.ModeletEngine.R;
import static java.lang.Long.valueOf;
import static java.lang.System.currentTimeMillis;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.modelet.common.FieldInfo;
import com.ca.umg.modelet.common.ModelRequestInfo;
import com.ca.umg.modelet.common.ModelResponseInfo;
import com.ca.umg.modelet.common.ModeletEngine;
import com.ca.umg.modelet.common.ResponseHeaderInfo;
import com.ca.umg.modelet.common.SystemInfo;
import com.ca.umg.modelet.constants.ErrorCodes;
import com.ca.umg.modelet.exception.ModeletExceptionCodes;
import com.ca.umg.modelet.ioconverter.ME2RequestExpander;
import com.ca.umg.modelet.runtime.RuntimeProcess;
import com.ca.umg.modelet.runtime.factory.RuntimeFactory;
import com.ca.umg.modelet.runtime.factory.RuntimeProcessFactory;
import com.ca.umg.modelet.transport.cache.Registry;
import com.ca.umg.modelet.util.Modeletutil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;

@Named(value = "socketHandler")
@SuppressWarnings("PMD")
public class ModeletSocketHandler implements InboundRequestHandler {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ModeletSocketHandler.class);

	@Inject
	private RuntimeProcessFactory factory;

	@Inject
	private RuntimeFactory runtimeFactory;

	@Inject
	private Registry registry;

	@Inject
	private SystemInfo systemInfo;

	/**
	 * Flag to mark modelet server for self destruction
	 */
	private boolean selfDestroy = false;

	// @Inject
	// private SystemInfo systemInfo;

	final private ObjectMapper mapper = new ObjectMapper();

	private volatile boolean isModelExecuteReuqest;

	private volatile boolean isModeletStopReuqest;

	private final static int NO_OF_THREADS = 2;

	private static final String EXCEL = "Excel";
	
	private static final String EXEC_COMMAND_DELIMITER = "-ExecutionCommandUsed-";

	private final ExecutorService threadExecutor = Executors
			.newFixedThreadPool(NO_OF_THREADS);

	public void handle(final ServerSocket serverSocket) throws SystemException {
		mapper.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN,
				Boolean.TRUE);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.setVisibilityChecker(VisibilityChecker.Std.defaultInstance().withFieldVisibility(JsonAutoDetect.Visibility.ANY)); 
		long start = currentTimeMillis();
		while (!serverSocket.isClosed()) {
			ModelResponseInfo modelResponseInfo = null;
			Socket clientSocket = null;
			final ResponseHeaderInfo headerInfo = new ResponseHeaderInfo();
			try {
				LOGGER.info("Waiting for client socket connection on port {}.",
						serverSocket.getLocalPort());
				clientSocket = serverSocket.accept();
				final ModelExecuteTask task = new ModelExecuteTask(
						clientSocket, serverSocket);
				threadExecutor.submit(task);
			} catch (final IOException e) {
				if (!serverSocket.isClosed()) {
					if(headerInfo != null ){
					headerInfo.setError(String.valueOf(Boolean.TRUE));
					headerInfo.setErrorMessage(e.getMessage());
					headerInfo.setErrorCode(ErrorCodes.ME0005);
					}
					modelResponseInfo = new ModelResponseInfo();
					setModeletExecutionTime(start, modelResponseInfo);
					modelResponseInfo.setResponseHeaderInfo(headerInfo);
				}
				LOGGER.error(
						"Exception occured while closing client connection.", e);
			}
		}
	}

	private void execute(final Socket clientSocket,
			final ServerSocket serverSocket) throws SystemException {
		mapper.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN,
				Boolean.TRUE);
		long start = currentTimeMillis();
		DataInputStream dataInStream = null;
		ModelRequestInfo modelRequestInfo = null;
		ModelResponseInfo modelResponseInfo = null;
		String response = "";
		DataOutputStream dataOutStream = null;
		ResponseHeaderInfo headerInfo = null;
		String requestJSON = null;
		double cpuUsageAtExTime = 0;
		String memoreyUsageAtExTime = "";
		try {
			cpuUsageAtExTime = Double.valueOf(Modeletutil.getCpuLoad());
			memoreyUsageAtExTime = Modeletutil.freeMemory();
			start = currentTimeMillis();
			dataInStream = new DataInputStream(clientSocket.getInputStream());
			dataOutStream = new DataOutputStream(clientSocket.getOutputStream());
			headerInfo = new ResponseHeaderInfo();
			long reqStartTime = System.currentTimeMillis();
			final int len = dataInStream.readInt();
			LOGGER.error("Bytes received: " + len);
			final byte dataBytes[] = new byte[len];
			dataInStream.readFully(dataBytes);
			long fileReadTime = System.currentTimeMillis();
			requestJSON = new String(dataBytes);

			/*
			 * BufferedReader bufferedReader = new BufferedReader(new
			 * FileReader(System.getProperty("sanpath") + File.separator +
			 * "ModelInputSample.txt")); requestJSON =
			 * IOUtils.toString(bufferedReader); bufferedReader.close();
			 */
			// LOGGER.error("ModelOutputSample.txt read time: " +
			// (System.currentTimeMillis() - fileReadTime));
			// requestJSON = new String(dataBytes);
			LOGGER.error("Request read time: "
					+ (System.currentTimeMillis() - reqStartTime));
			// LOGGER.info("requestJSON: " + requestJSON);
			long transStart = System.currentTimeMillis();
			modelRequestInfo = transformRequestToJSON(requestJSON);

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("payload before convertion is : {}", mapper
						.writeValueAsString(modelRequestInfo.getPayload()));
			}

			if (modelRequestInfo.getHeaderInfo().isModelSizeReduction()) {
				LOGGER.info("Model Size Reduction is initiated done for this request");
				ME2RequestExpander.expand(modelRequestInfo);
				LOGGER.info("payload affter convertion is : {}", mapper
						.writeValueAsString(modelRequestInfo.getPayload()));
			} else {
				LOGGER.info("Model Size Reduction is not done for this request");
			}

			LOGGER.error("Request transformation time: "
					+ (System.currentTimeMillis() - transStart));
			LOGGER.info("Received model execution request for model {}.",
					modelRequestInfo.getHeaderInfo().getModelLibraryName());

			// LOGGER.debug("Received request JSON {}.", requestJSON);
			// reinitRRuntime(modelRequestInfo);

			final String commandName = modelRequestInfo.getHeaderInfo()
					.getCommandName();
			LOGGER.error("Model Command Received >>>>>>>>>>> {}.", commandName);
			if (commandName == null || getModelCommands(commandName) == EXECUTE) {

				if (isModelExecuteReuqest) {
					newBusinessException(
							ModeletExceptionCodes.MOSE000101,
							new String[] { "Modelet is already executing another model. It cannot execute multiple model executions parallel." });
				}

				isModelExecuteReuqest = true;
				// MDC Logging
				appendLoggingHeader(modelRequestInfo);
				modelResponseInfo = getRuntimeProcess(modelRequestInfo)
						.execute(modelRequestInfo, systemInfo);
			} else if (commandName != null
					&& getModelCommands(commandName) == LOAD_MODEL) {

				if (isModelExecuteReuqest) {
					newBusinessException(
							ModeletExceptionCodes.MOSE000101,
							new String[] { "Modelet is already executing another model. It cannot execute multiple model executions parallel." });
				}

				isModelExecuteReuqest = true;
				// MDC Logging
				appendLoggingHeader(modelRequestInfo);
				modelResponseInfo = getRuntimeProcess(modelRequestInfo)
						.loadModel(modelRequestInfo);
			} else if (commandName != null
					&& getModelCommands(commandName) == UNLOAD_MODEL) {

				if (isModelExecuteReuqest) {
					newBusinessException(
							ModeletExceptionCodes.MOSE000101,
							new String[] { "Modelet is already executing another model. It cannot execute multiple model executions parallel." });
				}

				isModelExecuteReuqest = true;
				// MDC Logging
				appendLoggingHeader(modelRequestInfo);
				modelResponseInfo = getRuntimeProcess(modelRequestInfo)
						.unloadModel(modelRequestInfo);
			} else if (commandName != null
					&& getModelCommands(commandName) == DESTROY_SERVER) {

				if (isModeletStopReuqest) {
					newBusinessException(
							ModeletExceptionCodes.MOSE000101,
							new String[] { "Modelet is already requested to stop. It cannot execute multiple stop parallel." });
				}

				isModeletStopReuqest = true;
				selfDestroy = true;

				List<FieldInfo> fieldList = new ArrayList<FieldInfo>();
				FieldInfo fi = new FieldInfo();
				fi.setModelParameterName("type");
				fi.setValue(null);
				fieldList.add(fi);
				fi = new FieldInfo();
				fi.setModelParameterName("status");
				fi.setValue("shutdownStarted");
				fieldList.add(fi);
				modelResponseInfo = new ModelResponseInfo();
				modelResponseInfo.setPayload(fieldList);

				LOGGER.info("Received request to destroy modelet server.");
			} else if (commandName != null
					&& getModelCommands(commandName) == STOP_RSERVE) {

				if (isModelExecuteReuqest) {
					newBusinessException(
							ModeletExceptionCodes.MOSE000101,
							new String[] { "Modelet is already executing another model. It cannot execute multiple model executions parallel." });
				}

				isModelExecuteReuqest = true;
				selfDestroy = true;
				// MDC Logging
				appendLoggingHeader(modelRequestInfo);

				List<FieldInfo> fieldList = new ArrayList<FieldInfo>();
				FieldInfo fi = new FieldInfo();
				fi.setModelParameterName("type");
				fi.setValue(null);
				fieldList.add(fi);
				fi = new FieldInfo();
				fi.setModelParameterName("status");
				fi.setValue("stopping Rserve");
				fieldList.add(fi);
				modelResponseInfo = new ModelResponseInfo();
				modelResponseInfo.setPayload(fieldList);

				LOGGER.info("Received request to destroy r serve process.");

			} else if (commandName != null
					&& getModelCommands(commandName) == START_RSERVE) {
				if (isModelExecuteReuqest) {
					newBusinessException(
							ModeletExceptionCodes.MOSE000101,
							new String[] { "Modelet is already executing another model. It cannot execute multiple model executions parallel." });
				}

				isModelExecuteReuqest = true;

				// MDC Logging
				appendLoggingHeader(modelRequestInfo);
				getRuntimeProcess(modelRequestInfo).startRServeProcess();
				registry.register();
				List<FieldInfo> fieldList = new ArrayList<FieldInfo>();
				FieldInfo fi = new FieldInfo();
				fi.setModelParameterName("type");
				fi.setValue(null);
				fieldList.add(fi);
				fi = new FieldInfo();
				fi.setModelParameterName("status");
				fi.setValue("Starting Rserve");
				fieldList.add(fi);
				modelResponseInfo = new ModelResponseInfo();
				modelResponseInfo.setPayload(fieldList);

				LOGGER.info("Received request to start R serve process.");
			} else if(commandName != null
					&& getModelCommands(commandName) == GET_STATUS) {
				modelResponseInfo = new ModelResponseInfo();
				ResponseHeaderInfo responseHeaderInfo = new ResponseHeaderInfo();
				responseHeaderInfo.setExecutionResponse(systemInfo.getStatus());
				modelResponseInfo.setResponseHeaderInfo(responseHeaderInfo );
			}

			setModeletExecutionTime(start, modelResponseInfo);
			/* changes for UMG-5015 */
			if (modelResponseInfo != null
					&& modelResponseInfo.getResponseHeaderInfo() != null) {
				headerInfo.setExecutionCommand(modelResponseInfo
						.getResponseHeaderInfo().getExecutionCommand());
				headerInfo.setExecutionResponse(modelResponseInfo
						.getResponseHeaderInfo().getExecutionResponse());
				headerInfo.setExecutionLogs(modelResponseInfo
						.getResponseHeaderInfo().getExecutionLogs());
			}
			headerInfo.setError(String.valueOf(Boolean.FALSE));
			
			// LOGGER.error("Modelet Execution Time: {}",
			// modelResponseInfo.getModeletExecutionTime());
			LOGGER.info("Executed model {}.", modelRequestInfo.getHeaderInfo()
					.getModelLibraryName());
			long respTransTime = System.currentTimeMillis();
			if(modelResponseInfo !=  null){
			modelResponseInfo.setResponseHeaderInfo(headerInfo);
			modelResponseInfo.setCpuUsageAtStart(cpuUsageAtExTime);
			modelResponseInfo.setFreeMemoreyAtStart(memoreyUsageAtExTime);
			}
			setSystemInfo(modelResponseInfo);
			response = responseToJSON(modelResponseInfo);
			LOGGER.error("Response transformation time: "
					+ (System.currentTimeMillis() - respTransTime));

			LOGGER.error("MSH No netwrok time: "
					+ (System.currentTimeMillis() - fileReadTime));
			// BufferedWriter bufferedWriter = new BufferedWriter(new
			// FileWriter("D:\\UMG\\Sample\\ModelOutputSample.txt"));
			// long fileWriteTime = System.currentTimeMillis();
			// FileWriter fileWriter = new
			// FileWriter(System.getProperty("sanpath") + File.separator +
			// "ModelOutputSample.txt");
			// fileWriter.write(response);
			// fileWriter.flush();
			// fileWriter.close();
			// LOGGER.error("ModelOutputSample.txt write time: " +
			// (System.currentTimeMillis() - fileWriteTime));
			// response = responseToJSONWithoutPayload(modelResponseInfo);
			// response = responseToJSON(modelResponseInfo);
			// LOGGER.debug("Sending response JSON {}.", response);
		} catch (final IOException e) {
			if (!serverSocket.isClosed()) {
				if(headerInfo == null){
					headerInfo = new ResponseHeaderInfo();
				}
				headerInfo.setError(String.valueOf(Boolean.TRUE));
				headerInfo.setErrorMessage(e.getMessage());
				headerInfo.setErrorCode(ErrorCodes.ME0005);
				modelResponseInfo = new ModelResponseInfo();
				setModeletExecutionTime(start, modelResponseInfo);
				modelResponseInfo.setResponseHeaderInfo(headerInfo);
				modelResponseInfo.setCpuUsageAtStart(cpuUsageAtExTime);
				modelResponseInfo.setFreeMemoreyAtStart(memoreyUsageAtExTime);
				setSystemInfo(modelResponseInfo);
				response = responseToJSON(modelResponseInfo);
			}
			LOGGER.error("Exception occured while closing client connection.",
					e);
		} catch (BusinessException | SystemException e) {
			LOGGER.error("Exception occured while executing the model", e);
			LOGGER.error("Error message :: {} ", StringUtils.substringBefore(e.getLocalizedMessage(), EXEC_COMMAND_DELIMITER));
			headerInfo.setError(String.valueOf(Boolean.TRUE));
			// headerInfo.setErrorMessage(StringUtils.substringBefore(e.getLocalizedMessage(), EXEC_COMMAND_DELIMITER));
			headerInfo.setErrorCode(e.getCode());
			headerInfo.setExecutionCommand(StringUtils.substringAfter(e.getLocalizedMessage(), EXEC_COMMAND_DELIMITER));
			modelResponseInfo = new ModelResponseInfo();
			setModeletExecutionTime(start, modelResponseInfo);
			modelResponseInfo.setResponseHeaderInfo(headerInfo);
			if (!StringUtils.equalsIgnoreCase(
					systemInfo.getExecutionLanguage(), EXCEL)) {
				modelResponseInfo.setPayload(e);
			}
			// response = responseToJSON(modelResponseInfo);
			modelResponseInfo.setCpuUsageAtStart(cpuUsageAtExTime);
			modelResponseInfo.setFreeMemoreyAtStart(memoreyUsageAtExTime);
			modelResponseInfo.getResponseHeaderInfo().setErrorMessage(e.getLocalizedMessage());
			setSystemInfo(modelResponseInfo);
			response = responseToJSON(modelResponseInfo);
		} catch (final Exception e) { // NOPMD
			LOGGER.error("Exception occured.", e);
			if(headerInfo !=  null){
			headerInfo.setError(String.valueOf(Boolean.TRUE));
			headerInfo.setErrorMessage(e.getMessage());
			headerInfo.setErrorCode(ErrorCodes.ME0003);
			}
			modelResponseInfo = new ModelResponseInfo();
			setModeletExecutionTime(start, modelResponseInfo);
			modelResponseInfo.setResponseHeaderInfo(headerInfo);
			modelResponseInfo.setPayload(e);
			modelResponseInfo.setCpuUsageAtStart(cpuUsageAtExTime);
			modelResponseInfo.setFreeMemoreyAtStart(memoreyUsageAtExTime);
			setSystemInfo(modelResponseInfo);
			response = responseToJSON(modelResponseInfo);
		} finally {
			sendResponse(response, dataOutStream, clientSocket);
			headerInfo = new ResponseHeaderInfo();
			release(dataInStream, dataOutStream);
			LOGGER.error("Modelet Execution Time: {}",
					(System.currentTimeMillis() - start));
			isModelExecuteReuqest = false;
			isModeletStopReuqest = false;
			modelResponseInfo = null; // NOPMD
			response = null;
			if (selfDestroy) {
				registry.unregister();
				if (StringUtils.equalsIgnoreCase(SystemConstants.R_SERVE,
						systemInfo.getrMode())) {
					getRuntimeProcess(modelRequestInfo).stopRServeProcess();
				}
				// shutdown hook should get enabled
				LOGGER.info("Self destruction of modelet process has started.");
				registry.shutdownHazelcastClient();
				System.exit(0);
			}
		}
	}

	/**
	 * MDC Logging for the modelet component
	 * 
	 * @param modelRequestInfo
	 */
	private void appendLoggingHeader(ModelRequestInfo modelRequestInfo) {
		if (modelRequestInfo.getHeaderInfo().getTransactionCriteria() != null) {
			MDC.put(Modeletutil.MDC_LOGGING_RA_TRANSACTION_ID, modelRequestInfo
					.getHeaderInfo().getTransactionCriteria()
					.getUmgTransactionId());
			MDC.put(Modeletutil.MDC_LOGGING_TENANT_CODE, modelRequestInfo
					.getHeaderInfo().getTransactionCriteria().getTenantCode());
			MDC.put(Modeletutil.MDC_LOGGING_MODEL_NAME, modelRequestInfo
					.getHeaderInfo().getTransactionCriteria().getModelName());
			MDC.put(Modeletutil.MDC_LOGGING_MODEL_VERSION, modelRequestInfo
					.getHeaderInfo().getTransactionCriteria().getModelVersion());
		}
	}

	private void release(InputStream inputStream, OutputStream outputStream) {
		if (inputStream != null) {
			try {
				inputStream.close();
				inputStream = null; // NOPMD
			} catch (IOException e) {
				inputStream = null; // NOPMD
			}
		}

		if (outputStream != null) {
			try {
				outputStream.close();
				outputStream = null; // NOPMD
			} catch (IOException e) {
				outputStream = null; // NOPMD
			}
		}
	}

	private void setModeletExecutionTime(final long start,
			final ModelResponseInfo modelResponseInfo) {
		final long end = currentTimeMillis();
		modelResponseInfo.setModeletExecutionTime(valueOf(end - start));
	}

	private void sendResponse(final String response,
			final DataOutputStream dataOutStream, final Socket clientSocket) {
		if (clientSocket != null && !clientSocket.isClosed()) {
			try {
				final long startTime = System.currentTimeMillis();
				if (dataOutStream != null) {
					// System.out.println("response: " + response);
					dataOutStream.writeInt(response.getBytes().length);
					dataOutStream.write(response.getBytes());
					dataOutStream.flush();
					LOGGER.error("Bytes Sent: " + response.getBytes().length);
				}
				clientSocket.close();
				LOGGER.error("Response write to ME2 time : "
						+ (System.currentTimeMillis() - startTime));
			} catch (final IOException ioe) {
				LOGGER.error("Exception occured while writing to out stream.",
						ioe);
			} catch (final Exception e) { // NOPMD
				LOGGER.error("Exception occured while writing to out stream.",
						e);
			}
		}
	}

	@Override
	public RuntimeProcess getRuntimeProcess(
			final ModelRequestInfo modelRequestInfo) {
		final long startTime = System.currentTimeMillis();
		final RuntimeProcess process = factory.getRuntimeProcessInstance(
				modelRequestInfo.getHeaderInfo().getEngine(), runtimeFactory);
		LOGGER.info("Runtime Process:"
				+ modelRequestInfo.getHeaderInfo().getEngine());
		LOGGER.error("Get runtime time taken: "
				+ (System.currentTimeMillis() - startTime));
		return process;
	}

	private ModelRequestInfo transformRequestToJSON(final String requestJSON)
			throws SystemException {
		ModelRequestInfo modelRequestInfo = null;
		try {
			
			modelRequestInfo = mapper.readValue(requestJSON,
					ModelRequestInfo.class);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error("Exception occured while parsing request json", e);
			SystemException.newSystemException(ErrorCodes.ME0004, new String[] {
					"request", e.getMessage() }, e);
		} catch (final IOException e) {
			LOGGER.error("Exception occured while parsing response json", e);
			SystemException.newSystemException(ErrorCodes.ME0004, new String[] {
					"request", e.getMessage() }, e);
		}
		return modelRequestInfo;
	}

	private String responseToJSON(ModelResponseInfo modelResponseInfo)
			throws SystemException {
		String response = null;
		try {
			response = mapper.writeValueAsString(modelResponseInfo);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error("Exception occured while parsing response json", e);
			SystemException.newSystemException(ErrorCodes.ME0004, new String[] {
					"response", e.getMessage() }, e);
		} catch (final IOException e) {
			LOGGER.error("Exception occured while parsing response json", e);
			SystemException.newSystemException(ErrorCodes.ME0004, new String[] {
					"response", e.getMessage() }, e);
		}
		return response;
	}

	private String responseToJSONWithoutPayload(
			final ModelResponseInfo modelResponseInfo) throws SystemException {
		String response = null;
		try {
			response = mapper.writeValueAsString(modelResponseInfo);
		} catch (JsonParseException | JsonMappingException e) {
			LOGGER.error("Exception occured while parsing response json", e);
			SystemException.newSystemException(ErrorCodes.ME0004, new String[] {
					"response", e.getMessage() }, e);
		} catch (final IOException e) {
			LOGGER.error("Exception occured while parsing response json", e);
			SystemException.newSystemException(ErrorCodes.ME0004, new String[] {
					"response", e.getMessage() }, e);
		}
		return response;
	}

	public void reinitRRuntime(final ModelRequestInfo modelRequestInfo)
			throws SystemException {
		if (ModeletEngine.valueOf(modelRequestInfo.getHeaderInfo().getEngine()
				.toUpperCase(Locale.getDefault())) == R) {
			runtimeFactory.reinitializeRRuntime();
		}
	}

	class ModelExecuteTask implements Callable<Integer> {
		private final Socket clientSocket;
		private final ServerSocket serverSocket;

		private ModelExecuteTask(final Socket clientSocket,
				final ServerSocket serverSocket) {
			this.clientSocket = clientSocket;
			this.serverSocket = serverSocket;
		}

		@Override
		public Integer call() throws Exception {
			execute(clientSocket, serverSocket);
			return 0;
		}
	}

	public void createStopTimer() {
		final TimerTask task = new StopModeletTask();
		final Timer timer = new Timer();
		timer.schedule(task, 30000);
	}

	class StopModeletTask extends TimerTask {
		@Override
		public void run() {
			LOGGER.error("Stopping Modelet");
			System.exit(0);
		}
	}

	private void setSystemInfo(final ModelResponseInfo modelResponseInfo) {
		modelResponseInfo.setFreeMemorey(Modeletutil.freeMemory());
		modelResponseInfo.setCpuUsage(Double.valueOf(Modeletutil.getCpuLoad()));
	}
}