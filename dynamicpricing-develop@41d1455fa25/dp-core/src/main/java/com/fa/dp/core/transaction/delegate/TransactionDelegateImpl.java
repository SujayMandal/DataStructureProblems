package com.fa.dp.core.transaction.delegate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.core.base.delegate.AbstractDelegate;
import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;
import com.fa.dp.core.rest.RAClient;
import com.fa.dp.core.transaction.bo.TransactionBO;
import com.fa.dp.core.transaction.domain.Transaction;
import com.fa.dp.core.transaction.info.TransactionInfo;
import com.fa.dp.core.util.RAClientConstants;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

@Named
public class TransactionDelegateImpl extends AbstractDelegate implements TransactionDelegate {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionDelegateImpl.class);

	@Inject
	private TransactionBO transactionBo;

	@Inject
	private RAClient raClient;

	private ExecutorService executorService;

	@PostConstruct
	public void init() {
		executorService = Executors.newCachedThreadPool();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fa.ra.client.core.transaction.delegate.TransactionDelegate#
	 * getAllTransactions(java.lang.String, int, int, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<TransactionInfo> getAllTransactions(String modelName, String modelVersion, String status,
			String transactionId, String fromDate, String toDate) throws SystemException {
		List<Transaction> allTransactions = new ArrayList<Transaction>();

		Callable<List<Transaction>> clientTransactionTask = fetchTransactionsFromClient(modelName, modelVersion, status,
				transactionId, fromDate, toDate);

		Callable<List<Transaction>> raTransactionTask = fetchTransactionsFromRA(modelName, modelVersion, status,
				transactionId, fromDate, toDate);

		Future<List<Transaction>> clientTransactionsFuture = executorService.submit(clientTransactionTask);

		Future<List<Transaction>> raTransactionsFuture = executorService.submit(raTransactionTask);

		try {
			List<Transaction> clientTransactions = clientTransactionsFuture.get();
			List<Transaction> raTransactions = raTransactionsFuture.get();

			if (CollectionUtils.isNotEmpty(raTransactions)) {
				allTransactions.addAll(clientTransactions);
			}
			if (CollectionUtils.isNotEmpty(clientTransactions)) {
				allTransactions.addAll(raTransactions);
			}
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("An error occurred while fetching transactions", e);
			SystemException.newSystemException(CoreExceptionCodes.RACLNTXN001, new Object[] { e.getMessage() });
		}
		Collections.sort(allTransactions, new TransactionDateComparator());
		return convertToList(allTransactions, TransactionInfo.class);
	}

	private Callable<List<Transaction>> fetchTransactionsFromClient(final String modelName, final String modelVersion,
			final String status, final String transactionId, final String fromDate, final String toDate) {
		return new Callable<List<Transaction>>() {
			@Override
			public List<Transaction> call() throws Exception {
				return transactionBo.findAllTransactions(modelName, modelVersion, status, transactionId, fromDate,
						toDate);
			}
		};

	}

	private Callable<List<Transaction>> fetchTransactionsFromClient(final String modelName, final String fromDate,
			final String toDate) {
		return new Callable<List<Transaction>>() {
			@Override
			public List<Transaction> call() throws Exception {
				return transactionBo.findTransactions(modelName, fromDate, toDate);
			}
		};

	}

	private Callable<List<Transaction>> fetchTransactionsFromRA(final String modelName, final String modelVersion,
			final String status, final String transactionId, final String fromDate, final String toDate) {
		return new Callable<List<Transaction>>() {
			@Override
			public List<Transaction> call() throws Exception {
				return raClient.fetchRATransaction(modelName, modelVersion, status, transactionId, fromDate, toDate);
			}
		};
	}

	private Callable<List<Transaction>> fetchTransactionsFromRA(final String modelName, final String transactiontype,
			final String fromDate, final String toDate) {
		return new Callable<List<Transaction>>() {
			@Override
			public List<Transaction> call() throws Exception {
				return raClient.fetchRATransaction(modelName, transactiontype, fromDate, toDate);
			}
		};
	}

	private static class TransactionDateComparator implements Comparator<Transaction> {

		@Override
		public int compare(Transaction txn1, Transaction txn2) {
			int result = 0;
			if (txn1.getTransactionDate() > txn2.getTransactionDate()) {
				result = 1;
			} else if (txn1.getTransactionDate() > txn2.getTransactionDate()) {
				result = -1;
			}
			return result;
		}

	}

	@Override
	public void saveTransaction(TransactionInfo transactionInfo) throws SystemException {
		Transaction transaction = convert(transactionInfo, Transaction.class);
		transactionBo.save(transaction, transactionInfo.getTenantInput(), transactionInfo.getTenantOutput());
	}

	@Override
	public List<TransactionInfo> getTransactionDetails(String modelName) {
		// TODO Auto-generated method stub
		List<Transaction> transaction = transactionBo.getTransactionDetails(modelName);
		return convertToList(transaction, TransactionInfo.class);
	}

	@Override
	public List<TransactionInfo> getTransactionDetails(String modelName, String fromDate, String toDate) throws
            SystemException {
		// TODO Auto-generated method stub
		List<Transaction> allTransactions = new ArrayList<Transaction>();
		String transactiontype = "Prod";

		StringBuffer frmDate = new StringBuffer(fromDate);
		frmDate.append(RAClientConstants.CHAR_SPACE);
		frmDate.append(RAClientConstants.startHRS);
		frmDate.append(RAClientConstants.CHAR_COLON);
		frmDate.append(RAClientConstants.startMINS);

		StringBuffer enDate = new StringBuffer(toDate);
		enDate.append(RAClientConstants.CHAR_SPACE);
		enDate.append(RAClientConstants.endHRS);
		enDate.append(RAClientConstants.CHAR_COLON);
		enDate.append(RAClientConstants.endMINS);

		Callable<List<Transaction>> clientTransactionTask = fetchTransactionsFromClient(modelName, frmDate.toString(),
				enDate.toString());

		Callable<List<Transaction>> raTransactionTask = fetchTransactionsFromRA(modelName, transactiontype,
				frmDate.toString(), enDate.toString());

		Future<List<Transaction>> clientTransactionsFuture = executorService.submit(clientTransactionTask);

		Future<List<Transaction>> raTransactionsFuture = executorService.submit(raTransactionTask);

		try {
			List<Transaction> clientTransactions = clientTransactionsFuture.get();
			List<Transaction> raTransactions = raTransactionsFuture.get();

			if (CollectionUtils.isNotEmpty(raTransactions)) {
				allTransactions.addAll(raTransactions);
			}
			if (CollectionUtils.isNotEmpty(clientTransactions)) {
				allTransactions.addAll(clientTransactions);
			}
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("An error occurred while fetching transactions", e);
			SystemException.newSystemException(CoreExceptionCodes.RACLNTXN001, new Object[] { e.getMessage() });
			
		} catch (HttpClientErrorException ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
		} catch (ResourceAccessException | HttpStatusCodeException ex) {
			LOGGER.error("Error occurred while invoking runtime request", ex);
		}
		Collections.sort(allTransactions, new TransactionDateComparator());
		return convertToList(allTransactions, TransactionInfo.class);
	}

}
