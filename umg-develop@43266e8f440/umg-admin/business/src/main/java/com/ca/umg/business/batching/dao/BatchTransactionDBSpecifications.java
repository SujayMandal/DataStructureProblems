package com.ca.umg.business.batching.dao;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.ca.umg.business.batching.entity.BatchTransaction;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.util.AdminUtil;

public final class BatchTransactionDBSpecifications {

	private BatchTransactionDBSpecifications() {
	}

	public static Specification<BatchTransaction> withFileName(
			final String batchFileName) {
		return new Specification<BatchTransaction>() {
			@Override
			public Predicate toPredicate(Root<BatchTransaction> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(batchFileName)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get(BusinessConstants.BATCH_INPUT_FILE)),
                            AdminUtil
							.getLikePattern(batchFileName));
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<BatchTransaction> withBatchId(
			final String batchId) {
		return new Specification<BatchTransaction>() {
			@Override
			public Predicate toPredicate(Root<BatchTransaction> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(batchId)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get(BusinessConstants.ID)),
							AdminUtil.getLikePattern(batchId));
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<BatchTransaction> withStartTime(
			final Long startTime) {
		return new Specification<BatchTransaction>() {
			@Override
			public Predicate toPredicate(Root<BatchTransaction> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (startTime != null) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.<Long> get(BusinessConstants.START_TIME), startTime);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<BatchTransaction> withEndTime(final Long endTime) {
		return new Specification<BatchTransaction>() {
			@Override
			public Predicate toPredicate(Root<BatchTransaction> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (endTime != null) {
                    return criteriaBuilder.lessThanOrEqualTo(root.<Long> get(BusinessConstants.END_TIME), endTime);
				}

				return criteriaBuilder.conjunction();
			}
		};
	}

}
