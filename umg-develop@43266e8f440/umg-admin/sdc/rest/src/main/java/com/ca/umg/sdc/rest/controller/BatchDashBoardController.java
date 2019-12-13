package com.ca.umg.sdc.rest.controller;

import static com.ca.framework.core.requestcontext.RequestContext.getRequestContext;

import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.ConversionUtil;
import com.ca.umg.business.batching.bo.BatchUsageExcelReport;
import com.ca.umg.business.batching.dao.BatchDashboardFilter;
import com.ca.umg.business.batching.dao.BatchTransactionInfoWrapper;
import com.ca.umg.business.batching.delegate.BatchUsageReportDelegate;
import com.ca.umg.business.batching.delegate.BatchingDelegate;
import com.ca.umg.business.batching.entity.BatchTransaction;
import com.ca.umg.business.batching.report.usage.BatchUsageReportFilter;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.tenant.report.usage.delegate.TenantUsageReportDelegate;
import com.ca.umg.business.transaction.util.TransactionUtil;
import com.ca.umg.sdc.rest.utils.RestResponse;

@Controller
@RequestMapping("/batchDashBoard")
@SuppressWarnings("PMD")
public class BatchDashBoardController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(BatchDashBoardController.class);

	@Inject
	private BatchingDelegate batchingDelegate;
	
	@Inject
    private SystemParameterProvider systemParameterProvider;
	
	@Inject
	private TenantUsageReportDelegate tenantUsageReportDelegate;
	
	@Inject
	private BatchUsageReportDelegate batchUsageReportDelegate;

	/**
	 * 
	 * Given a batch ID, this controller method initiates the process of
	 * invalidating the corresponding batch
	 * 
	 */
	@RequestMapping(value = "/invalidateBatch/{batchId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse invalidateBatch(@PathVariable("batchId") String batchId) {
		RestResponse response = new RestResponse();
		try {
			batchingDelegate.invalidateBatch(batchId);
			response.setError(false);
		} catch (BusinessException | SystemException ex) {
			response.setError(true);
			response.setErrorCode(ex.getCode());
			response.setMessage(ex.getLocalizedMessage());
		}
		return response;
	}

	@RequestMapping(value = "/downloadInputFile/{batchId}/{fileName}.{ext}", method = RequestMethod.GET)
	public void downloadInputFile(@PathVariable String batchId,
			@PathVariable String fileName, @PathVariable String ext,
			HttpServletResponse response) {
		try {
			byte[] content = batchingDelegate.getBatchInputFileContent(batchId);
			exportFile(response, fileName + "." + ext, content);
		} catch (BusinessException | SystemException ex) {
			LOGGER.error(ex.getLocalizedMessage(), ex);
		}

	}
	
	 @RequestMapping(value = "/downloadSelectedBatchIO", method = RequestMethod.POST)
	 @ResponseBody
	 public void downloadSelectedBatchIO(@RequestParam("selectedBatchIds") String selectedBatchIds, HttpServletResponse response)
	            throws BusinessException, SystemException {
		 createZipForTntAndModelIO(selectedBatchIds, response);
	 }
	
	private void createZipForTntAndModelIO (String selectedBatchIds, HttpServletResponse response) {
    	LOGGER.info("started batchIO download for batchIds:" + selectedBatchIds);
    	ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(response.getOutputStream());
            StringTokenizer batchIdsTokenizer = new StringTokenizer(selectedBatchIds, ",");
            while (batchIdsTokenizer.hasMoreTokens()) {
                String batchId = batchIdsTokenizer.nextToken();
                BatchTransaction batchTranDocument = batchingDelegate.getBatch(batchId);
                if (batchTranDocument != null) {
                    setResponseHeader(response, BusinessConstants.BATCH_IO);
                    writeDatatoZipFile(zos, batchTranDocument);
                } else {
                    writeErrorData(response, batchId, null);
                }
            }
        } catch (SystemException | IOException | BusinessException exception) {
            LOGGER.error("Error while writing to response outputstream for batch IO data  ", exception);
            if (exception.getLocalizedMessage() != null) {
                writeErrorData(response, selectedBatchIds, exception.getLocalizedMessage());
            } else {
                writeErrorData(response, selectedBatchIds, exception.getMessage());
            }
        } finally {
            try {
                if (zos != null) {
                    zos.finish();
                    zos.close();
                }
                if(response.getOutputStream() != null){
                response.getOutputStream().flush();
                response.getOutputStream().close();
                }
            } catch (IOException e) {
                LOGGER.error("Error while closing response outputstream for tenant/model IO ", e);
            }
        }
        LOGGER.info("modelandTenantIO download for batch txnIds:" + selectedBatchIds + "ended");
    }

    private void writeErrorData(HttpServletResponse response, String txnId, String msg) {
        try {
            String headerValue = String.format("attachment; filename=\"%s\"", "error_" + txnId + ".txt");
            response.setHeader("Content-Disposition", headerValue);
            String errorMsg = null;
            if (msg == null) {
                errorMsg = "No Data found for the transactionId's :" + txnId;
                response.getOutputStream().write(errorMsg.getBytes());
            } else {
                errorMsg = msg;
                response.getOutputStream().write(errorMsg.getBytes());
            }
        } catch (IOException excep) {
            LOGGER.error("Error while Writting error data  ", excep);
        }
    }

    private void writeDatatoZipFile(ZipOutputStream zos, BatchTransaction batchTranDocument) 
    		throws IOException, SystemException, BusinessException {
        String batchIpFileName = batchTranDocument.getBatchInputFile();
        String batchOpFileName = batchTranDocument.getBatchOutputFile();
        if (batchIpFileName != null) {
        	TransactionUtil.addToZipFileForBatch(batchIpFileName, 
            		batchingDelegate.getBatchFileContent(batchTranDocument, BusinessConstants.BATCH_IP), zos);
        }
        if (batchOpFileName != null) {
        	TransactionUtil.addToZipFileForBatch(batchOpFileName, 
        		batchingDelegate.getBatchFileContent(batchTranDocument, BusinessConstants.BATCH_OP), zos);
        }
    }
    
    private void setResponseHeader(HttpServletResponse response, String zipFileName) {
        response.setHeader("Content-Type", "application/zip");
        response.setHeader("Content-Disposition", "attachment;filename=" + zipFileName);
    }

	@RequestMapping(value = "/downloadOutputFile/{batchId}/{fileName}.{ext}", method = RequestMethod.GET)
	public void downloadOutputFile(@PathVariable String batchId,
			@PathVariable String fileName, @PathVariable String ext,
			HttpServletResponse response) {
		try {
			byte[] content = batchingDelegate
					.getBatchOutputFileContent(batchId);
			exportFile(response, fileName + "." + ext, content);
		} catch (BusinessException | SystemException ex) {
			LOGGER.error(ex.getLocalizedMessage(), ex);
		}
	}

	private void exportFile(HttpServletResponse response, String fileName,
			byte[] content) {
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"",
				fileName);
		try {
			response.setHeader(headerKey, headerValue);
			if (content != null) {
				response.getOutputStream().write(content);
			} else {
				headerValue = String.format("attachment; filename=\"%s\"",
						"error.txt");
				response.setHeader(headerKey, headerValue);
				response.getWriter().write(fileName + " Not Found !");
			}
		} catch (IOException e) {
			LOGGER.error("Downloading Failed.", e);
		}
	}
	
	
	@RequestMapping(value = "/getPagedBatchData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<BatchTransactionInfoWrapper> getPagedBatchData(@RequestBody BatchDashboardFilter filter) {
		final RestResponse<BatchTransactionInfoWrapper> response = new RestResponse<BatchTransactionInfoWrapper>();
		BatchTransactionInfoWrapper wrapper = new BatchTransactionInfoWrapper();
		try {
			wrapper = batchingDelegate.getPagedBatchData(filter);
			response.setResponse(wrapper);
		} catch (BusinessException | SystemException ex) {
			response.setError(true);
			response.setErrorCode(ex.getCode());
			response.setMessage(ex.getLocalizedMessage());
		}
		return response;
	}
	
	@RequestMapping(value="/getBtchSelectRcrdCntLimit",method = RequestMethod.GET)
    @ResponseBody
    public RestResponse<String> getSelectedRecordsCountLimit () {
    	 RestResponse<String> response = new RestResponse<>();
         String countLimit = null;
         try {
        	 countLimit = systemParameterProvider.getParameter(BusinessConstants.BATCH_DSHBRD_SEL_RECRD_LIMIT);
             if (countLimit != null) {
                 response.setResponse(countLimit);

             } else {
            	 Integer numberFive = BusinessConstants.NUMBER_FIVE;
            	 response.setResponse(numberFive.toString());
             }
         } catch (Exception e) {// NOPMD
             LOGGER.error(e.getLocalizedMessage(), e);
             //response.setErrorCode(e.getCode());
             response.setError(true);
             response.setMessage(e.getLocalizedMessage());
         }
         return response;
    }
	
	@RequestMapping(value = "/terminateBatch/{batchId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public RestResponse<String> terminateBatch(@PathVariable("batchId") String batchId) {
		RestResponse<String> response = new RestResponse<String>();
		try {
			final String status = batchingDelegate.terminateBatch(batchId);
			if (status == null) {
				response.setError(true);				
			} else {
				response.setError(false);
			}
			response.setMessage(status);
		} catch (Exception ex) {
			response.setError(true);
			response.setMessage(ex.getLocalizedMessage());
		}
		return response;
	}
	
	private void setResponseProperties(final HttpServletResponse response, final String reportFileName) {
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=" + reportFileName);
	}
	
	// rest layer to get usage report for selected transactions
	@RequestMapping(value = "/downloadBatchUsageReportByTransactionList", method = RequestMethod.POST)
	@ResponseBody
	public void downloadBatchUsageReportByTransactionList(@RequestParam("selectedTransactionList") final List<String> selectedTransactionList,
			@RequestParam("cancelRequestId") final String cancelRequestId, @RequestParam("sortColumn") final String sortColumn,
			@RequestParam("descending") final boolean descending, final HttpServletResponse response) {
		
		BatchUsageReportFilter batchUsageReportFilter = new BatchUsageReportFilter();
		batchUsageReportFilter.setSelectedTransactions(selectedTransactionList);
		batchUsageReportFilter.setPage(-1);
		final String tenantCode = getRequestContext().getTenantCode();
		batchUsageReportFilter.setTenantCode(tenantCode);
		batchUsageReportFilter.setCancelRequestId(checkForNull(cancelRequestId));
		batchUsageReportFilter.setSortColumn(sortColumn);
		batchUsageReportFilter.setDescending(descending);
		batchUsageReportFilter.setSearchString(null);

		try {
			LOGGER.info("BatchUsageReportFilter:" + batchUsageReportFilter);
			final SqlRowSet sqlRowSet = batchUsageReportDelegate.loadBatchTransactionsRowSetByTransactionList(batchUsageReportFilter);
			final BatchUsageExcelReport report = new BatchUsageExcelReport(sqlRowSet);
			if (!tenantUsageReportDelegate.getUsageSearchRequestCancelStatusFromCache(batchUsageReportFilter.getCancelRequestId())) {
				Workbook wb = report.createBatchReport();
				final String reportFileName = report.getReportFileName(tenantCode, report.getMinCreatedDate(), report.getMaxCreatedDate());
				setResponseProperties(response, reportFileName);
				wb.write(response.getOutputStream());
				response.flushBuffer();
			}
		} catch (IOException se) {
			LOGGER.error("Error while writing Batch Tenant Usage Report to response outputstream ", se);
		} catch (SystemException exception) {
			LOGGER.error("Error while writing to response outputstream for Batch tenant/model IO data  ", exception);
		} catch (BusinessException be) {
			LOGGER.error("Error while dowloading Batch report writing to response outputstream for tenant IO for the Batch transaction : ", be);
		} finally {
			try {
				response.getOutputStream().close();
			} catch (IOException e) {
				LOGGER.error("Error while closing response outputstream for tenant IO data for the Batch transaction : " + e);
			}
		}
	}
	
	// rest layer to get usage report for all transactions based on filters
	 @RequestMapping(value = "/downloadBatchUsageReportByFilter", method = RequestMethod.POST)
		@ResponseBody
		public void downloadUsageReportByFilter(@RequestParam(value = "txnFilterData") String txnFilterDataJson, 
	    		@RequestParam(value = "advanceTransactionFilter") String advanceTransactionFilterJson,
				final HttpServletResponse response) {
					BatchUsageReportFilter txnFilterData = null;
			    	try {
			    		final String tenantCode = getRequestContext().getTenantCode();
			    		txnFilterData = ConversionUtil.convertJson(txnFilterDataJson, BatchUsageReportFilter.class);
			    		txnFilterData.setTenantCode(tenantCode);
						LOGGER.info("BatchUsageReportFilter:" + txnFilterData);
						final SqlRowSet sqlRowSet = batchUsageReportDelegate.loadAllBatchTransactionsRowSet(txnFilterData);
						final BatchUsageExcelReport report = new BatchUsageExcelReport(sqlRowSet);
						if (!tenantUsageReportDelegate.getUsageSearchRequestCancelStatusFromCache(txnFilterData.getCancelRequestId())) {
							Workbook wb = report.createBatchReport();
							final String reportFileName = report.getReportFileName(tenantCode, report.getMinCreatedDate(), report.getMaxCreatedDate());
							setResponseProperties(response, reportFileName);
							wb.write(response.getOutputStream());
							response.flushBuffer();
						}
					} catch (IOException se) {
						LOGGER.error("Error while writing Batch Tenant Usage Report to response outputstream ", se);
					} catch (SystemException exception) {
						LOGGER.error("Error while writing to response outputstream for Batch tenant/model IO data  ", exception);
					} catch (BusinessException be) {
						LOGGER.error("Error while dowloading Batch report writing to response outputstream for tenant IO for the Batch transaction : ", be);
					} finally {
						try {
							response.getOutputStream().close();
						} catch (IOException e) {
							LOGGER.error("Error while closing response outputstream for tenant IO data for the Batch transaction : " + e);
						}
					}
		}
	
	private String checkForNull(final String value){
		if(value.equalsIgnoreCase("null")||value.isEmpty()){
			return null;
		}else{
			return value;
		}
	}
}
