/**
 * 
 */
package com.ca.umg.business.syndicatedata.specification;

import java.util.Locale;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.ca.umg.business.syndicatedata.entity.SyndicateData;

/**
 * @author nigampra
 * 
 */
public final class SyndicateDataSpecification {

	private SyndicateDataSpecification() {

	}

	public static Specification<SyndicateData> withContainerNameLike(
			final String containerName) {
		return new Specification<SyndicateData>() {
			@Override
			public Predicate toPredicate(Root<SyndicateData> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(containerName)) {
					return criteriaBuilder.like(criteriaBuilder.lower(root
							.<String> get("containerName")), "%"
							+ containerName.toLowerCase(Locale.getDefault())
							+ "%");
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<SyndicateData> withVersionNameLike(
			final String versionName) {
		return new Specification<SyndicateData>() {
			@Override
			public Predicate toPredicate(Root<SyndicateData> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(versionName)) {
					return criteriaBuilder.like(criteriaBuilder.lower(root
							.<String> get("versionName")),
							"%" + versionName.toLowerCase(Locale.getDefault())
									+ "%");
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<SyndicateData> withVersionDescLike(
			final String versionDescription) {
		return new Specification<SyndicateData>() {
			@Override
			public Predicate toPredicate(Root<SyndicateData> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(versionDescription)) {
					return criteriaBuilder.like(
							criteriaBuilder.lower(root
									.<String> get("versionDescription")),
							"%"
									+ versionDescription.toLowerCase(Locale
											.getDefault()) + "%");
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<SyndicateData> withVersionId(
			final Long versionId) {
		return new Specification<SyndicateData>() {
			@Override
			public Predicate toPredicate(Root<SyndicateData> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (versionId != null) {
					return criteriaBuilder.equal(root.<Long> get("versionId"),
							versionId);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<SyndicateData> withCreatedByLike(
			final String createdBy) {
		return new Specification<SyndicateData>() {
			@Override
			public Predicate toPredicate(Root<SyndicateData> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(createdBy)) {
					return criteriaBuilder.like(criteriaBuilder.lower(root
							.<String> get("createdBy")),
							"%" + createdBy.toLowerCase(Locale.getDefault())
									+ "%");
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<SyndicateData> withCreatedDateFrom(
			final Long fromDate) {
		return new Specification<SyndicateData>() {
			@Override
			public Predicate toPredicate(Root<SyndicateData> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (fromDate != null) {
					return criteriaBuilder.greaterThanOrEqualTo(
							root.<Long> get("validFrom"), fromDate);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<SyndicateData> withUpdatedByLike(
			final String lastModifiedBy) {
		return new Specification<SyndicateData>() {
			@Override
			public Predicate toPredicate(Root<SyndicateData> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(lastModifiedBy)) {
					return criteriaBuilder.like(criteriaBuilder.lower(root
							.<String> get("lastModifiedBy")), "%"
							+ lastModifiedBy.toLowerCase(Locale.getDefault())
							+ "%");
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<SyndicateData> withCreatedDateTill(
			final Long tillDate) {
		return new Specification<SyndicateData>() {
			@Override
			public Predicate toPredicate(Root<SyndicateData> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (tillDate != null) {
					return criteriaBuilder.lessThanOrEqualTo(
							root.<Long> get("validTo"), tillDate);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

}
