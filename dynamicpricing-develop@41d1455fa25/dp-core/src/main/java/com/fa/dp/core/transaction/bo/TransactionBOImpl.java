package com.fa.dp.core.transaction.bo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.transaction.dao.TransactionDBSpecifications;
import com.fa.dp.core.transaction.dao.TransactionDao;
import com.fa.dp.core.transaction.dao.TransactionIODao;
import com.fa.dp.core.transaction.domain.Transaction;
import com.fa.dp.core.transaction.domain.TransactionIO;
import com.fa.dp.core.util.RAClientConstants;
import com.fa.dp.core.util.RAClientUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

@Named
public class TransactionBOImpl implements TransactionBO {

	@Inject
	private TransactionDao transactionDao;

	@Inject
	private TransactionIODao transactionIODao;

	@Override
	public List<Transaction> findAllTransactions(String modelName, String modelVersion, String status,
			String transactionId, String fromDate, String toDate) throws SystemException {

		Specification<Transaction> clientTxnIdSpec = TransactionDBSpecifications.withClientTransactionId(transactionId);
		Specification<Transaction> tenantModelSpec = TransactionDBSpecifications.withModelName(modelName);
		Specification<Transaction> majorVersionSpec = TransactionDBSpecifications.withModelMajorVersion(
				Integer.parseInt(StringUtils.substringBefore(modelVersion, RAClientConstants.CHAR_DOT)));
		Specification<Transaction> minorVersionSpec = TransactionDBSpecifications.withModelMinorVersion(
				Integer.parseInt(StringUtils.substringAfter(modelVersion, RAClientConstants.CHAR_DOT)));

		Specification<Transaction> statusSpec = TransactionDBSpecifications.withStatus(status);

		Specification<Transaction> startDateSpec = TransactionDBSpecifications.transactionRunDatesGreaterThanOrEqualTo(
				RAClientUtil.convertTimeToMills(fromDate, RAClientConstants.RA_CLIENT_DATE_FORMAT));

		Specification<Transaction> endDateSpec = TransactionDBSpecifications.transactionRunDatesLessThanOrEqualTo(
				RAClientUtil.convertTimeToMills(toDate, RAClientConstants.RA_CLIENT_DATE_FORMAT));

		return transactionDao.findAll(Specifications.where(startDateSpec).and(endDateSpec).and(clientTxnIdSpec)
				.and(tenantModelSpec).and(majorVersionSpec).and(minorVersionSpec).and(statusSpec));
	}

	@Override
	public void save(Transaction transaction, byte[] request, byte[] response) throws SystemException {
		Transaction savedTransaction = transactionDao.save(transaction);
		TransactionIO transactionIO = new TransactionIO();
		transactionIO.setTransaction(savedTransaction);
		transactionIO.setInput(request);
		transactionIO.setOutput(response);
		transactionIODao.save(transactionIO);
	}

	@Override
	public List<Transaction> getTransactionDetails(String modelName) {
		// TODO Auto-generated method stub
		return transactionDao.findBymodelNameIn(modelName);
	}

	@Override
	public List<Transaction> findTransactions(String modelName, String fromDate, String toDate) throws SystemException {

		Specification<Transaction> tenantModelSpec = TransactionDBSpecifications.withModelName(modelName);

		Specification<Transaction> startDateSpec = TransactionDBSpecifications.transactionRunDatesGreaterThanOrEqualTo(
				RAClientUtil.convertTimeToMills(fromDate, RAClientConstants.RA_CLIENT_DATE_FORMAT));

		Specification<Transaction> endDateSpec = TransactionDBSpecifications.transactionRunDatesLessThanOrEqualTo(
				RAClientUtil.convertTimeToMills(toDate, RAClientConstants.RA_CLIENT_DATE_FORMAT));
		return transactionDao.findAll(Specifications.where(startDateSpec).and(endDateSpec).and(tenantModelSpec));
	}

}
