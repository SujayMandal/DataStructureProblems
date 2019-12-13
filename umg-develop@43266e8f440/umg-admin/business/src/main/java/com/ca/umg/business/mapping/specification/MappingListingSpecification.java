/**
 * 
 */
package com.ca.umg.business.mapping.specification;

import java.util.Locale;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.ca.umg.business.mapping.entity.Mapping;

/**
 * 
 * @author kabiju
 *
 */
public final class MappingListingSpecification {
	public static final String TID_NAME_CONST = "name";
	public static final String TID_DESCR_CONST = "description";
	public static final String PERCENT_CONST = "%";
	public static final String CREATED_DATE_CONST = "createdDate";
	public static final String CREATED_BY_CONST = "createdBy";
	public static final String STATUS_CONST = "status";


	public static final String MAPPING_CONST = "mapping";

	public static final String UMG_NAME_CONST = "umgName";



	private MappingListingSpecification() {

	}



	public static Specification<Mapping> withTIDNameLike(final String name) {
		return new Specification<Mapping>() {
			@Override
			public Predicate toPredicate(final Root<Mapping> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(name)) {
					return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get(TID_NAME_CONST)),
							PERCENT_CONST+ name.toLowerCase(Locale.getDefault())+PERCENT_CONST);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<Mapping> withTIDDescriptionLike(final String description) {
		return new Specification<Mapping>() {
			@Override
			public Predicate toPredicate(final Root<Mapping> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(description)) {
					return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get(TID_DESCR_CONST)),
							PERCENT_CONST+ description.toLowerCase(Locale.getDefault())+PERCENT_CONST);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<Mapping> withStatus(final String status) {
		return new Specification<Mapping>() {
			@Override
			public Predicate toPredicate(final Root<Mapping> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(status)) {
					return criteriaBuilder.equal(criteriaBuilder.lower(root.<String> get(STATUS_CONST)),
							status.toLowerCase(Locale.getDefault()));
				}
				return criteriaBuilder.conjunction();
			}
		};
	}
	public static Specification<Mapping> withCreatedBy(final String createdBy) {
		return new Specification<Mapping>() {
			@Override
			public Predicate toPredicate(final Root<Mapping> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(createdBy)) {
					return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get(CREATED_BY_CONST)),
					        PERCENT_CONST + createdBy.toLowerCase(Locale.getDefault()) + PERCENT_CONST);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<Mapping> withCreatedDateFrom(final Long fromDate) {
		return new Specification<Mapping>() {
			@Override
			public Predicate toPredicate(final Root<Mapping> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
				if (fromDate != null) {
					return criteriaBuilder.greaterThanOrEqualTo(root.<Long> get(CREATED_DATE_CONST), fromDate);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}
	public static Specification<Mapping> withCreatedDateTo(final Long toDate) {
		return new Specification<Mapping>() {
			@Override
			public Predicate toPredicate(final Root<Mapping> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
				if (toDate != null) {
					return criteriaBuilder.lessThanOrEqualTo(root.<Long> get(CREATED_DATE_CONST), toDate);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}
}
