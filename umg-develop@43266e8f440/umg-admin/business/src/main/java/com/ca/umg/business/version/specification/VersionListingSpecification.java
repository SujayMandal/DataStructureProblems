/**
 * 
 */
package com.ca.umg.business.version.specification;

import java.util.Locale;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.ca.umg.business.mapping.entity.Mapping;
import com.ca.umg.business.model.entity.Model;
import com.ca.umg.business.model.entity.ModelLibrary;
import com.ca.umg.business.version.entity.Version;

/**
 * @author kamathan
 *
 */
public final class VersionListingSpecification {
	public static final String NAME_CONST = "name";
	public static final String PERCENT_CONST = "%";
	public static final String MAPPING_CONST = "mapping";
	public static final String CREATED_DATE_CONST = "createdDate";
	public static final String CREATED_BY_CONST = "createdBy";
	public static final String UMG_NAME_CONST = "umgName";
	public static final String MODEL_LIBRARY_CONST = "modelLibrary";
	public static final String DESCRIPTION_CONST = "description";
	public static final String LIB_JAR_NAME_CONST = "jarName";
	public static final String MODEL_IO_DEFN_NAME_CONST = "ioDefinitionName";
	public static final String MODEL_CONST = "model";

	private VersionListingSpecification() {

	}
	public static Specification<Version> withLibraryNameLike(final String libraryName) {
		return new Specification<Version>() {
			@Override
			public Predicate toPredicate(final Root<Version> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(libraryName)) {
					return criteriaBuilder.like(criteriaBuilder.lower(root.<ModelLibrary> get(MODEL_LIBRARY_CONST).<String> get(UMG_NAME_CONST)),
							PERCENT_CONST+ libraryName.toLowerCase(Locale.getDefault())+PERCENT_CONST);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<Version> withLibraryJarNameLike(final String libraryName) {
		return new Specification<Version>() {
			@Override
			public Predicate toPredicate(final Root<Version> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(libraryName)) {
					return criteriaBuilder.like(criteriaBuilder.lower(root.<ModelLibrary> get(MODEL_LIBRARY_CONST).<String> get(LIB_JAR_NAME_CONST)),
							PERCENT_CONST+ libraryName.toLowerCase(Locale.getDefault())+PERCENT_CONST);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<Version> withTIDDescriptionLike(final String mappingDescription) {
		return new Specification<Version>() {
			@Override
			public Predicate toPredicate(final Root<Version> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(mappingDescription)) {
					return criteriaBuilder.like(criteriaBuilder.lower(root.<Mapping> get(MAPPING_CONST).<String> get(DESCRIPTION_CONST) ),
							PERCENT_CONST+ mappingDescription.toLowerCase(Locale.getDefault())+PERCENT_CONST);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<Version> withVersionCreatedBy(final String createdBy) {
		return new Specification<Version>() {
			@Override
			public Predicate toPredicate(final Root<Version> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(createdBy)) {
					return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get(CREATED_BY_CONST)),
					        PERCENT_CONST + createdBy.toLowerCase(Locale.getDefault()) + PERCENT_CONST);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<Version> withVersionCreatedByLike(final String createdBy) {
		return new Specification<Version>() {
			@Override
			public Predicate toPredicate(final Root<Version> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(createdBy)) {
					return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get(CREATED_BY_CONST)),
							PERCENT_CONST+ createdBy.toLowerCase(Locale.getDefault())+PERCENT_CONST);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<Version> withCreatedDateFrom(final Long fromDate) {
		return new Specification<Version>() {
			@Override
			public Predicate toPredicate(final Root<Version> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
				if (fromDate != null) {
					return criteriaBuilder.greaterThanOrEqualTo(root.<Long> get(CREATED_DATE_CONST), fromDate);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}
	public static Specification<Version> withCreatedDateTo(final Long toDate) {
		return new Specification<Version>() {
			@Override
			public Predicate toPredicate(final Root<Version> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
				if (toDate != null) {
					return criteriaBuilder.lessThanOrEqualTo(root.<Long> get(CREATED_DATE_CONST), toDate);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

	public static Specification<Version> withModelIoDefnNameLike(final String modelName) {
		return new Specification<Version>() {
			@Override
			public Predicate toPredicate(final Root<Version> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(modelName)) {
					return criteriaBuilder.like(criteriaBuilder.lower(root.<Mapping> get(MAPPING_CONST).<Model> get(MODEL_CONST).<String> get(MODEL_IO_DEFN_NAME_CONST)),
							PERCENT_CONST+ modelName.toLowerCase(Locale.getDefault())+PERCENT_CONST);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}
}
