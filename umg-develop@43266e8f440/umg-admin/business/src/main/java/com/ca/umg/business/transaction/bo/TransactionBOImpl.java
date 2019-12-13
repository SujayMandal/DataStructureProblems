package com.ca.umg.business.transaction.bo;

import static org.springframework.data.jpa.domain.Specifications.where;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.business.common.info.PagingInfo;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.transaction.dao.TransactionDAO;
import com.ca.umg.business.transaction.dao.TransactionDBSpecifications;
import com.ca.umg.business.transaction.dao.TransactionDBSpecificationsExtra;
import com.ca.umg.business.transaction.entity.Transaction;
import com.ca.umg.business.transaction.info.TransactionFilter;
import com.ca.umg.business.transaction.mongo.dao.MongoTransactionDAO;
import com.ca.umg.business.transaction.mongo.entity.TransactionDocument;
import com.ca.umg.business.transaction.util.TransactionUtil;

@Named
public class TransactionBOImpl implements TransactionBO {

    // private static final Logger LOGGER = LoggerFactory.getLogger(TransactionBOImpl.class.getName());

    private static final int NUMBER_OF_RECORDS_PER_PAGE = 100;

    private static final int FIRST_PAGE_ALWAYS = 0;

    @Inject
    private TransactionDAO transactionDAO;

    @Inject
    private SystemParameterProvider systemParameterProvider;

    @Inject
    private MongoTransactionDAO txnDocumentDAO;
    @Override
    public Page<Transaction> listAll(TransactionFilter transactionFilter) throws BusinessException, SystemException {
		Specification<Transaction> libNameSpec = TransactionDBSpecifications.withLibraryName(transactionFilter.getLibraryName());
		//Specification<Transaction> dateSpec = TransactionDBSpecifications.betweenTransactionRunDates(transactionFilter.getRunAsOfDateFrom(), transactionFilter.getRunAsOfDateTo());
		Specification<Transaction> clientTxnIdSpec = TransactionDBSpecifications.withClientTransactionId(transactionFilter.getClientTransactionID());
		Specification<Transaction> tenantModelSpec = TransactionDBSpecifications.withTenantModelName(transactionFilter.getTenantModelName());
		Specification<Transaction> majorVersionSpec = TransactionDBSpecifications.withTransactionMajorVersion(transactionFilter.getMajorVersion());
		Specification<Transaction> minorVersionSpec = TransactionDBSpecifications.withTransactionMinorVersion(transactionFilter.getMinorVersion());
		
		
		Specification<Transaction> startDateSpec = TransactionDBSpecificationsExtra.transactionRunDatesGreaterThanOrEqualTo(transactionFilter.getRunAsOfDateFrom());
		
		Specification<Transaction> endDateSpec = TransactionDBSpecificationsExtra.transactionRunDatesLessThanOrEqualTo(transactionFilter.getRunAsOfDateTo());

		String errorCodePattern = TransactionUtil.getErrorCodePattern(transactionFilter, systemParameterProvider);

		String errorDescription = "";
		if(transactionFilter.getErrorDescription() != null){
			errorDescription = transactionFilter.getErrorDescription();
		}
		
		//TODO commented this as method is not used anywhere for umg-4200 
				//need to change according to new filter object if this method is used
		//Specification<Transaction> isTestTxnSpec = TransactionDBSpecifications.withTestTransaction(transactionFilter.isTestTxn());
		Specification<Transaction> isTestTxnSpec = null;
		
		Specification<Transaction> errorType = TransactionDBSpecifications.withErrorTypeLike(errorCodePattern);
		Specification<Transaction> errorDesc = TransactionDBSpecifications.withErrorDescriptionLike(errorDescription);
		Specification<Transaction> errorCode = TransactionDBSpecifications.withErrorCodeLike(errorDescription);
        
        return  transactionDAO.findAll(
        		   where(startDateSpec).and(endDateSpec).and(clientTxnIdSpec).and(tenantModelSpec).and(majorVersionSpec).and(minorVersionSpec)
                                  .and(libNameSpec).and(isTestTxnSpec).and(errorType).and(where(errorDesc).or(errorCode)),
        		  constructPageSpecification(transactionFilter));
    }

    /**
     * Returns a new object which specifies the the wanted result page.
     * 
     * @param pageIndex
     *            The index of the wanted result page
     * @return
     */
    private Pageable constructPageSpecification(PagingInfo pageInfo) {
    	PageRequest pageRequest=null;
    	if(pageInfo!=null){
    		pageRequest= new PageRequest(pageInfo.getPage() == 0 ? 0 : pageInfo.getPage() - 1, pageInfo.getPageSize(), sortOption(pageInfo));
    	}else{
    		pageRequest=  new PageRequest(FIRST_PAGE_ALWAYS, NUMBER_OF_RECORDS_PER_PAGE,sortByCreatedDate() );
    	}
    	return pageRequest;
    }

    private Sort sortByCreatedDate() {
        return new Sort(Sort.Direction.DESC, BusinessConstants.CREATED_DATE);
    }
    /**
     * This method will return Sort object based on which column is clicked in the UI to sort
     *  
     */
    private Sort sortOption(PagingInfo pageInfo) {
    	Sort sort=null;
    	if(pageInfo.getSortColumn()==null||pageInfo.getSortColumn().isEmpty()){
    		sort=sortByCreatedDate();
    	}else{
    		sort=new Sort(pageInfo.isDescending()?Sort.Direction.DESC:Sort.Direction.ASC, pageInfo.getSortColumn());
    	}
        return sort;
    }

    @Override
    public List<String> findAllLibraries() throws BusinessException, SystemException {
        return transactionDAO.findAllLibraries();
    }

    @Override
    public List<String> findAllTenantModelNames() throws BusinessException, SystemException {
        return transactionDAO.findAllTenantModelNames();
    }

    @Override
    public Transaction getTransactionByTxnId(String txnId) throws BusinessException, SystemException {
        return transactionDAO.findOne(txnId);
    }

	@Override
	public Long getMaxRunAsOfDate() throws BusinessException, SystemException {
		return transactionDAO.findMaxRunAsOfDate();
	}

    @Override
    public TransactionDocument getTxnDocumentByTxnId(String txnId) throws SystemException {
        return txnDocumentDAO.getTenantAndModelIO(txnId);
    }

}
