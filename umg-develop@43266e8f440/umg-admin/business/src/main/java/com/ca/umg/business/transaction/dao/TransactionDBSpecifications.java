package com.ca.umg.business.transaction.dao;

import java.util.Locale;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.ca.umg.business.transaction.entity.Transaction;
import com.ca.umg.business.util.AdminUtil;

public final class TransactionDBSpecifications {
	private final static String RUN_AS_OF_DATE="runAsOfDate";

    private TransactionDBSpecifications() {
    }

    public static Specification<Transaction> withLibraryName(final String libraryName) {
        return new Specification<Transaction>() {
            @Override
            public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(libraryName) && !"Any".equalsIgnoreCase(libraryName)) {
                    return criteriaBuilder.equal(criteriaBuilder.lower(root.<String> get("libraryName")),
                            libraryName.toLowerCase(Locale.getDefault()));
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<Transaction> withTenantModelName(final String tenantModelName) {
        return new Specification<Transaction>() {
            @Override
            public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(tenantModelName) && !"Any".equalsIgnoreCase(tenantModelName)) {
                    return criteriaBuilder.equal(criteriaBuilder.lower(root.<String> get("tenantModelName")),
                            tenantModelName.toLowerCase(Locale.getDefault()));
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<Transaction> withClientTransactionId(final String clientTxnID) {
        return new Specification<Transaction>() {
            @Override
            public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(clientTxnID)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get("clientTransactionID")),
                            AdminUtil.getLikePattern(clientTxnID));
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<Transaction> betweenTransactionRunDates(final Long startDate, final Long endDate) {
        return new Specification<Transaction>() {
            @Override
            public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (startDate != null && endDate != null && endDate > startDate) {
                    return criteriaBuilder.between(root.<Long> get(RUN_AS_OF_DATE), startDate, endDate);
                }
                // The default implementation is that the To_Date is calculated
                // for the current DateTime and not for future DateTime.
                else if (endDate == null && startDate != null && startDate < System.currentTimeMillis()) {
                    return criteriaBuilder.between(root.<Long> get(RUN_AS_OF_DATE), startDate, System.currentTimeMillis());
                }
                return criteriaBuilder.conjunction();
            }
        };
    }
    
    public static Specification<Transaction> withTransactionMajorVersion(final Integer majorVersion) {
        return new Specification<Transaction>() {
            @Override
            public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (majorVersion != null) {
                    return criteriaBuilder.equal(root.<Integer> get("majorVersion"), majorVersion);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<Transaction> withTransactionMinorVersion(final Integer minorVersion) {
        return new Specification<Transaction>() {
            @Override
            public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (minorVersion != null) {
                    return criteriaBuilder.equal(root.<Integer> get("minorVersion"), minorVersion);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<Transaction> withTestTransaction(final boolean isTestTransaction) {
        return new Specification<Transaction>() {
            @Override
            public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("isTest"), isTestTransaction);
            }
        };
    }
    /**
     * This specification will filter the error type selected from the list box
     * UMG-1288
     */
    public static Specification<Transaction> withErrorTypeLike(final String ErrorType) {
        return new Specification<Transaction>() {
            @Override
            public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(ErrorType) && !"Any".equalsIgnoreCase(ErrorType)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get("errorCode")),	
                    		AdminUtil.getLikePattern(ErrorType.toLowerCase(Locale.getDefault())));
                }
                return criteriaBuilder.conjunction();
            }
        };
    }
    /**
     * This specification will filter data, based on the error search text box 
     * UMG-1288
     */
    public static Specification<Transaction> withErrorDescriptionLike(final String ErrorDesc) {
        return new Specification<Transaction>() {
            @Override
            public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
              if (StringUtils.isNotBlank(ErrorDesc)) {
                	Path<Byte[]> byt = root.<Byte[]> get("errorDescription"); 
                	
                    return criteriaBuilder.like(criteriaBuilder.lower(byt.as(String.class)),	
                    		AdminUtil.getLikePattern(ErrorDesc.toLowerCase(Locale.getDefault())) );
                }
                return criteriaBuilder.conjunction();
            }
        };
    }
    
    /**
     * This specification will filter data, based on the error code entered in the search text box,  
     * UMG-1288
     */
    public static Specification<Transaction> withErrorCodeLike(final String ErrorType) {
        return new Specification<Transaction>() {
            @Override
            public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(ErrorType)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get("errorCode")),	
                    		AdminUtil.getLikePattern(ErrorType.toLowerCase(Locale.getDefault())));
                }
                return criteriaBuilder.conjunction();
            }
        };
    }
}