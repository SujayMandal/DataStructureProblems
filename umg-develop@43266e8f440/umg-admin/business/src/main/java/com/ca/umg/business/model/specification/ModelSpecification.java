/**
 * 
 */
package com.ca.umg.business.model.specification;

import java.util.Locale;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.ca.umg.business.model.entity.Model;

/**
 * @author nigampra
 * 
 */
public final class ModelSpecification {

	private ModelSpecification() {

	}

	public static Specification<Model> withModelName(final String name) {
		return new Specification<Model>() {
			@Override
			public Predicate toPredicate(Root<Model> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(name)) {
					return criteriaBuilder.equal(
							criteriaBuilder.lower(root.<String> get("name")),
							name.toLowerCase(Locale.getDefault()));
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<Model> withModelNameLike(final String name) {
		return new Specification<Model>() {
			@Override
			public Predicate toPredicate(Root<Model> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(name)) {
					return criteriaBuilder.like(
							criteriaBuilder.lower(root.<String> get("name")),
							"%" + name.toLowerCase(Locale.getDefault()) + "%");
				}
				return criteriaBuilder.conjunction();
			}
		};
	}
	

	public static Specification<Model> withUMGNameLike(final String umgName) {
		return new Specification<Model>() {
			@Override
			public Predicate toPredicate(Root<Model> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(umgName)) {
					return criteriaBuilder
							.like(criteriaBuilder.lower(root
									.<String> get("umgName")),
									"%"
											+ umgName.toLowerCase(Locale
													.getDefault()) + "%");
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<Model> withIODefinitionNameLike(
			final String ioDefinitionName) {
		return new Specification<Model>() {
			@Override
			public Predicate toPredicate(Root<Model> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(ioDefinitionName)) {
					return criteriaBuilder.like(criteriaBuilder.lower(root
							.<String> get("ioDefinitionName")), "%"
							+ ioDefinitionName.toLowerCase(Locale.getDefault())
							+ "%");
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<Model> withDocumentationNameLike(
			final String documentationName) {
		return new Specification<Model>() {
			@Override
			public Predicate toPredicate(Root<Model> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(documentationName)) {
					return criteriaBuilder.like(
							criteriaBuilder.lower(root
									.<String> get("documentationName")),
							"%"
									+ documentationName.toLowerCase(Locale
											.getDefault()) + "%");
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<Model> withCreatedByLike(final String createdBy) {
		return new Specification<Model>() {
			@Override
			public Predicate toPredicate(Root<Model> root,
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

	public static Specification<Model> withCreatedDateFrom(final Long fromDate) {
		return new Specification<Model>() {
			@Override
			public Predicate toPredicate(Root<Model> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (fromDate != null) {
					return criteriaBuilder.greaterThanOrEqualTo(
							root.<Long> get("createdDate"), fromDate);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<Model> withCreatedDateTill(final Long tillDate) {
		return new Specification<Model>() {
			@Override
			public Predicate toPredicate(Root<Model> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (tillDate != null) {
					return criteriaBuilder.lessThanOrEqualTo(
							root.<Long> get("createdDate"), tillDate);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

}
