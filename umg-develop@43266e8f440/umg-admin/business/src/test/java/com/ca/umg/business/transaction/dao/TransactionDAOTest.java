package com.ca.umg.business.transaction.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.data.jpa.domain.Specifications.where;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.umg.business.BaseTest;
import com.ca.umg.business.transaction.entity.Transaction;
import com.ca.umg.business.transaction.info.TransactionFilter;

@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
public class TransactionDAOTest extends BaseTest {

    private Long ONE_DAY = Long.parseLong("86400000");
    private Long JUL_31_2014 = Long.parseLong("1406745000000");

    @Before
    public void setup() {
        getLocalhostTenantContext();
    }

    @Test
    public void transactionCreateTest() {
        byte[] data = new byte[1];
        Transaction txn = createTransactionData("TestTxn-1098", "TestVersionName", "TestLibName", 1, 2, "Success",
                System.currentTimeMillis(), false, null, null);
        Transaction savedTxn = getTransactionDAO().save(txn);
        assertNotNull(savedTxn.getId());
    }

    @Test
    public void transactionFindTest() {
        byte[] data = new byte[1];
        Transaction txn = createTransactionData("TestTxn-1098", "TestVersionName", "TestLibName", 1, 2, "Success",
                System.currentTimeMillis(), false, null, null);
        getTransactionDAO().save(txn);
        assertEquals(true, getTransactionDAO().exists(txn.getId()));
    }

    @Test
    public void transactionFindAllTest() {
        getTransactionDAO().deleteAllInBatch();
        byte[] data = new byte[1];
        for (int i = 0; i < 10; i++) {
            Transaction txn = createTransactionData("TestTxn-" + i, "TestVersionName", "TestLibName", 1, 2, "Success",
                    System.currentTimeMillis(), false, null, null);
            getTransactionDAO().save(txn);
        }
        List<Transaction> txnList = getTransactionDAO().findAll();
        assertEquals(10, txnList.size());
    }

    private List<Specification<Transaction>> buildSpecs(TransactionFilter transactionFilter) {
        List<Specification<Transaction>> specList = new ArrayList<Specification<Transaction>>();
        Specification<Transaction> libNameSpec = TransactionDBSpecifications.withLibraryName(transactionFilter.getLibraryName());
        Specification<Transaction> dateSpec = TransactionDBSpecifications.betweenTransactionRunDates(
                transactionFilter.getRunAsOfDateFrom(), transactionFilter.getRunAsOfDateTo());
        Specification<Transaction> clientTxnIdSpec = TransactionDBSpecifications.withClientTransactionId(transactionFilter
                .getClientTransactionID());
        Specification<Transaction> tenantModelSpec = TransactionDBSpecifications.withTenantModelName(transactionFilter
                .getTenantModelName());
        Specification<Transaction> majorVersionSpec = TransactionDBSpecifications.withTransactionMajorVersion(transactionFilter
                .getMajorVersion());
        Specification<Transaction> minorVersionSpec = TransactionDBSpecifications.withTransactionMinorVersion(transactionFilter
                .getMinorVersion());
      //TODO commented this as method is not used anywhere for umg-4200 
      		//need to change according to new filter object if this method is used
        //Specification<Transaction> isTestTxnSpec = TransactionDBSpecifications.withTestTransaction(transactionFilter.isTestTxn());
        specList.add(libNameSpec);
        specList.add(dateSpec);
        specList.add(clientTxnIdSpec);
        specList.add(tenantModelSpec);
        specList.add(majorVersionSpec);
        specList.add(minorVersionSpec);
      //TODO commented this as method is not used anywhere for umg-4200 
  		//need to change according to new filter object if this method is used
        //specList.add(isTestTxnSpec);
        return specList;
    }

    @Test
    public void transactionEmptyFilterListTest() {
        getTransactionDAO().deleteAllInBatch();
        byte[] data = new byte[1];
        for (int i = 0; i < 10; i++) {
            Long date = JUL_31_2014 + ONE_DAY;
            Transaction txn = createTransactionData("TestTxn-" + i, "TestVersionName", "TestLibName", 1, 2, "Success", date,
                    false, null, null);
            getTransactionDAO().save(txn);
        }
        TransactionFilter transactionFilter = new TransactionFilter();
        List<Specification<Transaction>> specList = buildSpecs(transactionFilter);
        Specifications<Transaction> whereClauseSpec = where(specList.get(0));
        for (int i = 1; i < specList.size(); i++) {
            whereClauseSpec = whereClauseSpec.and(specList.get(i));
        }
        List<Transaction> txnList = getTransactionDAO().findAll(whereClauseSpec);
        assertEquals(10, txnList.size());
    }

    @Test
    public void transactionClientTxnIdFilterListTest() {
        getTransactionDAO().deleteAllInBatch();
        byte[] data = new byte[1];
        for (int i = 0; i < 10; i++) {
            Long date = JUL_31_2014 + ONE_DAY;
            Transaction txn = createTransactionData("TestTxn-" + i, "TestVersionName", "TestLibName", 1, 2, "Success", date,
                    false, null, null);
            getTransactionDAO().save(txn);
        }
        TransactionFilter transactionFilter = new TransactionFilter();
        transactionFilter.setClientTransactionID("TestTxn-1");
        List<Specification<Transaction>> specList = buildSpecs(transactionFilter);
        Specifications<Transaction> whereClauseSpec = where(specList.get(0));
        for (int i = 1; i < specList.size(); i++) {
            whereClauseSpec = whereClauseSpec.and(specList.get(i));
        }
        List<Transaction> txnList = getTransactionDAO().findAll(whereClauseSpec);
        assertEquals(1, txnList.size());
    }

    @Test
    public void transactionTenantModelNameFilterListTest() {
        getTransactionDAO().deleteAllInBatch();
        byte[] data = new byte[1];
        for (int i = 0; i < 10; i++) {
            Long date = JUL_31_2014 + ONE_DAY;
            Transaction txn = createTransactionData("TestTxn-" + i, "TestVersionName" + i, "TestLibName", 1, 2, "Success", date,
                    false, null, null);
            getTransactionDAO().save(txn);
        }
        TransactionFilter transactionFilter = new TransactionFilter();
        transactionFilter.setTenantModelName("TestVersionName3");
        List<Specification<Transaction>> specList = buildSpecs(transactionFilter);
        Specifications<Transaction> whereClauseSpec = where(specList.get(0));
        for (int i = 1; i < specList.size(); i++) {
            whereClauseSpec = whereClauseSpec.and(specList.get(i));
        }
        List<Transaction> txnList = getTransactionDAO().findAll(whereClauseSpec);
        assertEquals(1, txnList.size());
    }

    @Test
    public void transactionLibraryNameFilterListTest() {
        getTransactionDAO().deleteAllInBatch();
        byte[] data = new byte[1];
        for (int i = 0; i < 10; i++) {
            Transaction txn = null;
            if (i % 2 == 0) {
                txn = createTransactionData("TestTxn-" + i, "TestVersionName" + i, "TestLibName", 1, 2, "Success",
                        System.currentTimeMillis(), false, null, null);
            } else {
                txn = createTransactionData("TestTxn-" + i, "TestVersionName" + i, "TestNewLibName", 1, 2, "Success",
                        System.currentTimeMillis(), false, null, null);
            }
            getTransactionDAO().save(txn);
        }
        TransactionFilter transactionFilter = new TransactionFilter();
        transactionFilter.setLibraryName("TestLibName");
        List<Specification<Transaction>> specList = buildSpecs(transactionFilter);
        Specifications<Transaction> whereClauseSpec = where(specList.get(0));
        for (int i = 1; i < specList.size(); i++) {
            whereClauseSpec = whereClauseSpec.and(specList.get(i));
        }
        List<Transaction> txnList = getTransactionDAO().findAll(whereClauseSpec);
        assertEquals(5, txnList.size());
    }

    @Test
    public void transactionMajorVersionFilterListTest() {
        getTransactionDAO().deleteAllInBatch();
        byte[] data = new byte[1];
        for (int i = 0; i < 10; i++) {
            Transaction txn = null;
            if (i % 2 == 0) {
                txn = createTransactionData("TestTxn-" + i, "TestVersionName" + i, "TestLibName", 1, 2, "Success",
                        System.currentTimeMillis(), false, null, null);
            } else {
                txn = createTransactionData("TestTxn-" + i, "TestVersionName" + i, "TestNewLibName", 2, 2, "Success",
                        System.currentTimeMillis(), false, null, null);
            }
            getTransactionDAO().save(txn);
        }
        TransactionFilter transactionFilter = new TransactionFilter();
        transactionFilter.setMajorVersion(1);
        List<Specification<Transaction>> specList = buildSpecs(transactionFilter);
        Specifications<Transaction> whereClauseSpec = where(specList.get(0));
        for (int i = 1; i < specList.size(); i++) {
            whereClauseSpec = whereClauseSpec.and(specList.get(i));
        }
        List<Transaction> txnList = getTransactionDAO().findAll(whereClauseSpec);
        assertEquals(5, txnList.size());
    }

    @Test
    public void transactionMinorVersionFilterListTest() {
        getTransactionDAO().deleteAllInBatch();
        byte[] data = new byte[1];
        for (int i = 0; i < 10; i++) {
            Transaction txn = null;
            if (i % 2 == 0) {
                txn = createTransactionData("TestTxn-" + i, "TestVersionName" + i, "TestLibName", 1, 2, "Success",
                        System.currentTimeMillis(), false, null, null);
            } else {
                txn = createTransactionData("TestTxn-" + i, "TestVersionName" + i, "TestNewLibName", 2, 2, "Success",
                        System.currentTimeMillis(), false, null, null);
            }
            getTransactionDAO().save(txn);
        }
        TransactionFilter transactionFilter = new TransactionFilter();
        transactionFilter.setMinorVersion(2);
        List<Specification<Transaction>> specList = buildSpecs(transactionFilter);
        Specifications<Transaction> whereClauseSpec = where(specList.get(0));
        for (int i = 1; i < specList.size(); i++) {
            whereClauseSpec = whereClauseSpec.and(specList.get(i));
        }
        List<Transaction> txnList = getTransactionDAO().findAll(whereClauseSpec);
        assertEquals(10, txnList.size());
    }

    @Test
    public void transactionRunAsOfDateFilterListTest() {
        getTransactionDAO().deleteAllInBatch();
        byte[] data = new byte[1];
        Long date = JUL_31_2014 + ONE_DAY;
        for (int i = 0; i < 10; i++) {
            Transaction txn = createTransactionData("TestTxn-" + i, "TestVersionName" + i, "TestLibName", 1, 2, "Success", date,
                    false, null, null);
            getTransactionDAO().save(txn);
            date += ONE_DAY;
        }
        TransactionFilter transactionFilter = new TransactionFilter();
        transactionFilter.setRunAsOfDateFrom(JUL_31_2014);
        transactionFilter.setRunAsOfDateTo(JUL_31_2014 + ONE_DAY + ONE_DAY + ONE_DAY + ONE_DAY + ONE_DAY);
        List<Specification<Transaction>> specList = buildSpecs(transactionFilter);
        Specifications<Transaction> whereClauseSpec = where(specList.get(0));
        for (int i = 1; i < specList.size(); i++) {
            whereClauseSpec = whereClauseSpec.and(specList.get(i));
        }
        List<Transaction> txnList = getTransactionDAO().findAll(whereClauseSpec);
        assertEquals(5, txnList.size());
    }

    @Test
    public void transactionWithOnlyRunAsOfDateFromFilterListTest() {
        getTransactionDAO().deleteAllInBatch();
        byte[] data = new byte[1];
        Long date = JUL_31_2014 + ONE_DAY;
        for (int i = 0; i < 10; i++) {
            Transaction txn = createTransactionData("TestTxn-" + i, "TestVersionName" + i, "TestLibName", 1, 2, "Success", date,
                    false, null, null);
            getTransactionDAO().save(txn);
            date += ONE_DAY;
        }
        TransactionFilter transactionFilter = new TransactionFilter();
        transactionFilter.setRunAsOfDateFrom(JUL_31_2014 + ONE_DAY + ONE_DAY + ONE_DAY);
        List<Specification<Transaction>> specList = buildSpecs(transactionFilter);
        Specifications<Transaction> whereClauseSpec = where(specList.get(0));
        for (int i = 1; i < specList.size(); i++) {
            whereClauseSpec = whereClauseSpec.and(specList.get(i));
        }
        List<Transaction> txnList = getTransactionDAO().findAll(whereClauseSpec);
        assertEquals(8, txnList.size());
    }

    @Ignore
    public void transactionWithOnlyTestTxnFilterListTest() {
        getTransactionDAO().deleteAllInBatch();
        byte[] data = new byte[1];
        for (int i = 0; i < 10; i++) {
            Transaction txn = null;
            if (i % 2 == 0) {
                txn = createTransactionData("TestTxn-" + i, "TestVersionName" + i, "TestLibName", 1, 2, "Success",
                        System.currentTimeMillis(), false, null, null);
            } else {
                txn = createTransactionData("TestTxn-" + i, "TestVersionName" + i, "TestNewLibName", 2, 2, "Success",
                        System.currentTimeMillis(), true, null, null);
            }
            getTransactionDAO().save(txn);
        }
        TransactionFilter transactionFilter = new TransactionFilter();
      //TODO commented this as method is not used anywhere for umg-4200 
  		//need to change according to new filter object if this method is used
        //transactionFilter.setShowTestTxn(true);
        List<Specification<Transaction>> specList = buildSpecs(transactionFilter);
        Specifications<Transaction> whereClauseSpec = where(specList.get(0));
        for (int i = 1; i < specList.size(); i++) {
            whereClauseSpec = whereClauseSpec.and(specList.get(i));
        }
        List<Transaction> txnList = getTransactionDAO().findAll(whereClauseSpec);
        assertEquals(5, txnList.size());
    }
    
    @Test
    public void transactionErrorCodeTest(){
        byte[] data = new byte[1];
        Transaction txn = createTransactionData("TestTxn-1286", "TestVersionName", "TestLibName", 1, 2, "Fail",
                System.currentTimeMillis(), false, "RSE000808",
                "ME2 returned error. Error code is: MSE0000001, Error message is Modelet Client not available.");
        Transaction savedTxn = getTransactionDAO().save(txn);
        assertNotNull(savedTxn.getId());
        assertNotNull(savedTxn.getErrorCode());
        assertNotNull(savedTxn.getErrorDescription());
    
    }
}
