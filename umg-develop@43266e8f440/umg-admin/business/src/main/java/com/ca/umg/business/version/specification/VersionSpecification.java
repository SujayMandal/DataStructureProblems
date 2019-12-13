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
import com.ca.umg.business.version.entity.Version;

/**
 * @author kamathan
 *
 */
public final class VersionSpecification {
	public static final String NAME_CONST = "name";
	public static final String MAPPING_CONST = "mapping";
	public static final String PERCENT_CONST = "%";
	public static final String MAJOR_VERSION_CONST = "majorVersion";
	public static final String MINOR_VERSION_CONST = "minorVersion";
	public static final String STATUS_CONST = "status";
	public static final String MODEL_CONST = "model";
	public static final String DESCRIPTION_CONST = "description";
	public static final String VERSION_DESCRIPTION_CONST = "versionDescription";
    private VersionSpecification() {

    }

    public static Specification<Version> withName(final String name) {
        return new Specification<Version>() {
            @Override
            public Predicate toPredicate(Root<Version> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(name)) {
                    return criteriaBuilder.equal(criteriaBuilder.lower(root.<String> get(NAME_CONST)),
                            name.toLowerCase(Locale.getDefault()));
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<Version> withMajorVersion(final Integer majorVersion) {
        return new Specification<Version>() {
            @Override
            public Predicate toPredicate(Root<Version> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (majorVersion != null) {
                    return criteriaBuilder.equal(root.<Integer> get(MAJOR_VERSION_CONST), majorVersion);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<Version> withMinorVersion(final Integer minorVersion) {
        return new Specification<Version>() {
            @Override
            public Predicate toPredicate(Root<Version> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (minorVersion != null) {
                    return criteriaBuilder.equal(root.<Integer> get(MINOR_VERSION_CONST), minorVersion);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<Version> withStatus(final String status) {
        return new Specification<Version>() {
            @Override
            public Predicate toPredicate(Root<Version> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(status)) {
                    return criteriaBuilder.equal(criteriaBuilder.lower(root.<String> get(STATUS_CONST)),
                            status.toLowerCase(Locale.getDefault()));
                }
                return criteriaBuilder.conjunction();
            }
        };
    }
    public static Specification<Version> withStatusLike(final String status) {
        return new Specification<Version>() {
            @Override
            public Predicate toPredicate(Root<Version> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(status)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get(STATUS_CONST)),
                    		PERCENT_CONST+ status.toLowerCase(Locale.getDefault()) +PERCENT_CONST);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }
    public static Specification<Version> withContainerDescriptionLike(final String description) {
        return new Specification<Version>() {
            @Override
            public Predicate toPredicate(Root<Version> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(description)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get(DESCRIPTION_CONST)),
                    		PERCENT_CONST+ description.toLowerCase(Locale.getDefault()) +PERCENT_CONST);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }
    public static Specification<Version> withContainerNameLike(final String name) {
        return new Specification<Version>() {
            @Override
            public Predicate toPredicate(Root<Version> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(name)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get(NAME_CONST)),
                    		PERCENT_CONST+ name.toLowerCase(Locale.getDefault()) +PERCENT_CONST);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<Version> withVersionDescriptionLike(final String varDescription) {
        return new Specification<Version>() {
            @Override
            public Predicate toPredicate(Root<Version> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(varDescription)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get(VERSION_DESCRIPTION_CONST)),
                    		PERCENT_CONST+ varDescription.toLowerCase(Locale.getDefault()) +PERCENT_CONST);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }
    
    public static Specification<Version> withModelNameLike(final String modelName) {
        return new Specification<Version>() {
            @Override
            public Predicate toPredicate(Root<Version> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(modelName)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<Mapping> get(MAPPING_CONST).<Model> get(MODEL_CONST).<String> get(NAME_CONST)),
                    		PERCENT_CONST+ modelName.toLowerCase(Locale.getDefault())+PERCENT_CONST);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }
    
    public static Specification<Version> withTIDNameLike(final String mappingName) {
        return new Specification<Version>() {
            @Override
            public Predicate toPredicate(Root<Version> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(mappingName)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<Mapping> get(MAPPING_CONST).<String> get(NAME_CONST)),
                    		PERCENT_CONST+ mappingName.toLowerCase(Locale.getDefault())+PERCENT_CONST);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }
}
