/**
 * This class is the continuation of TransactionDBSpecifications.java
 * separated because of pmd : class has too many methods, consider refactoring it.
 */

package com.ca.umg.business.transaction.dao;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.ca.umg.business.transaction.entity.Transaction;

public final class TransactionDBSpecificationsExtra {
	private final static String RUN_AS_OF_DATE="runAsOfDate";

    private TransactionDBSpecificationsExtra() {
    }

   
    /**
     * This specification will filter data, based on the run date start with,  
     * UMG-2064
     */
    
    public static Specification<Transaction> transactionRunDatesGreaterThanOrEqualTo(final Long fromDate) {
    	 return new Specification<Transaction>() {
             @Override
             public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                 if(fromDate!=null){
                	 return criteriaBuilder.greaterThanOrEqualTo(root.<Long> get(RUN_AS_OF_DATE), fromDate);
                 }
                 return criteriaBuilder.conjunction();
             }
         };
    }
    
    /**
     * This specification will filter data, based on the run date end with,  
     * UMG-2064
     */
    public static Specification<Transaction> transactionRunDatesLessThanOrEqualTo(final Long toDate) {
   	 return new Specification<Transaction>() {
            @Override
            public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if(toDate!=null){
               	 return criteriaBuilder.lessThanOrEqualTo(root.<Long> get(RUN_AS_OF_DATE), toDate);
                }
                return criteriaBuilder.conjunction();
            }
        };
   }    
    
    

}