package com.fa.dp.core.transaction.dao;

import java.util.Locale;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.fa.dp.core.transaction.domain.Transaction;
import com.fa.dp.core.util.RAClientUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public final class TransactionDBSpecifications {

    private final static String TRANSACTION_DATE = "transactionDate";

    private TransactionDBSpecifications() {
    }

    public static Specification<Transaction> withModelName(final String modelName) {
        return new Specification<Transaction>() {
            @Override
            public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(modelName) && !"Any".equalsIgnoreCase(modelName)) {
                    return criteriaBuilder.equal(criteriaBuilder.lower(root.<String> get("modelName")),
                            modelName.toLowerCase(Locale.getDefault()));
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<Transaction> withStatus(final String status) {
        return new Specification<Transaction>() {
            @Override
            public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(status) && !"Any".equalsIgnoreCase(status)) {
                    return criteriaBuilder.equal(criteriaBuilder.lower(root.<String> get("status")),
                            status.toLowerCase(Locale.getDefault()));
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
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get("clientTransactionId")),
                            RAClientUtil.getLikePattern(clientTxnID));
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<Transaction> withModelMajorVersion(final Integer majorVersion) {
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

    public static Specification<Transaction> withModelMinorVersion(final Integer minorVersion) {
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

    /**
     * This specification will filter data, based on the run date start with, UMG-2064
     */

    public static Specification<Transaction> transactionRunDatesGreaterThanOrEqualTo(final Long fromDate) {
        return new Specification<Transaction>() {
            @Override
            public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (fromDate != null) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.<Long> get(TRANSACTION_DATE), fromDate);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    /**
     * This specification will filter data, based on the run date end with, UMG-2064
     */
    public static Specification<Transaction> transactionRunDatesLessThanOrEqualTo(final Long toDate) {
        return new Specification<Transaction>() {
            @Override
            public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (toDate != null) {
                    return criteriaBuilder.lessThanOrEqualTo(root.<Long> get(TRANSACTION_DATE), toDate);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

}