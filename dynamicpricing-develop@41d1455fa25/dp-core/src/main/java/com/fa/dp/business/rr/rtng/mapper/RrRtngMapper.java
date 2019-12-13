package com.fa.dp.business.rr.rtng.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Named;

import com.fa.dp.business.info.Response;
import com.fa.dp.business.rr.rtng.dao.AbstractDBClient;
import com.fa.dp.business.rr.rtng.dao.RtngDBClient;
import com.fa.dp.business.validation.input.info.DPProcessParamInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.core.context.SecurityContextHolder;

@Named
public class RrRtngMapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(RtngDBClient.class);

	@Autowired
	ApplicationContext applicationContext;

	public List<Response> fetchRRAndRTNGResponse(DPProcessParamInfo dpInfo, ExecutorService executorService)
			throws InterruptedException, ExecutionException {
		LOGGER.info("Enter RrRtngMapper :: method fetchRRAndRTNGResponse");
		List<Response> rrRTNGList = new ArrayList<>();
		try {
			Map<String, AbstractDBClient> externalBeans = applicationContext.getBeansOfType(AbstractDBClient.class);
			externalBeans.entrySet().forEach(entry -> {
				AbstractDBClient abstractDBClient = (AbstractDBClient) entry.getValue();

				final Map<String, String> mdcContext = MDC.getCopyOfContextMap();
				
				Callable<Response> delegatingCallable = DelegatingSecurityContextCallable
						.create(new Callable<Response>() {
							@Override
							public Response call() throws Exception {
								if (mdcContext != null) MDC.setContextMap(mdcContext);
								return abstractDBClient.execute(dpInfo);
							}

						}, SecurityContextHolder.getContext());
				try {
					Future<? extends Response> future = executorService.submit(delegatingCallable);
					rrRTNGList.add(future.get());
				} catch (Exception e) {
					LOGGER.error("Error while Adding RR or RTNG Response " + e.getMessage());
				}
			});
		} catch (Exception e) {
			LOGGER.error("Exception :" + e.getMessage());
		}
		LOGGER.info("Exit RrRtngMapper :: method fetchRRAndRTNGResponse");
		return rrRTNGList;

	}
}
