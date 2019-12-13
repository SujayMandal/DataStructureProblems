package com.ca.umg.business.model.specification;

import java.util.Locale;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.ca.umg.business.model.entity.ModelLibrary;

public final class ModelLibrarySpecification {
    private static final String PERCENT_CONST = "%";

    private ModelLibrarySpecification() {
    }
    
    public static Specification<ModelLibrary> withLibraryName(final String searchText) {
        return new Specification<ModelLibrary>() {
            @Override
            public Predicate toPredicate(Root<ModelLibrary> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(searchText)) {
                    return criteriaBuilder.equal(criteriaBuilder.lower(root.<String> get("name")),searchText.toLowerCase(Locale.getDefault()));
                }
                return criteriaBuilder.conjunction();
            }
        };
    }
    
    public static Specification<ModelLibrary> withLibraryNameLike(final String searchText) {
        return new Specification<ModelLibrary>() {
            @Override
            public Predicate toPredicate(Root<ModelLibrary> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(searchText)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get("name")),
                            PERCENT_CONST + searchText.toLowerCase(Locale.getDefault()) + PERCENT_CONST);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }
    
    public static Specification<ModelLibrary> withUMGNameLike(final String umgName) {
        return new Specification<ModelLibrary>() {
            @Override
            public Predicate toPredicate(Root<ModelLibrary> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(umgName)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get("umgName")),
                            PERCENT_CONST + umgName.toLowerCase(Locale.getDefault()) + PERCENT_CONST);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }
    
    public static Specification<ModelLibrary> withDescriptionLike(final String searchText) {
        return new Specification<ModelLibrary>() {
            @Override
            public Predicate toPredicate(Root<ModelLibrary> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(searchText)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get("description")), PERCENT_CONST
                            + searchText.toLowerCase(Locale.getDefault()) + PERCENT_CONST);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }
    
    public static Specification<ModelLibrary> withExecutionType(final String searchText) {
        return new Specification<ModelLibrary>() {
            @Override
            public Predicate toPredicate(Root<ModelLibrary> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(searchText)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get("executionType")), PERCENT_CONST
                            + searchText.toLowerCase(Locale.getDefault()) + PERCENT_CONST);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<ModelLibrary> withExecutionLanguage(final String searchText) {
        return new Specification<ModelLibrary>() {
            @Override
            public Predicate toPredicate(Root<ModelLibrary> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(searchText)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get("executionLanguage")), PERCENT_CONST
                            + searchText.toLowerCase(Locale.getDefault()) + PERCENT_CONST);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<ModelLibrary> withPackageNameLike(final String searchText) {
        return new Specification<ModelLibrary>() {
            @Override
            public Predicate toPredicate(Root<ModelLibrary> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(searchText)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get("jarName")),
                            PERCENT_CONST + searchText.toLowerCase(Locale.getDefault()) + PERCENT_CONST);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<ModelLibrary> withCreatedBy(final String searchText) {
        return new Specification<ModelLibrary>() {
            @Override
            public Predicate toPredicate(Root<ModelLibrary> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(searchText)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get("createdBy")),
                            PERCENT_CONST
                            + searchText.toLowerCase(Locale.getDefault()) + PERCENT_CONST);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<ModelLibrary> withCreatedDateFrom(final Long fromDate) {
        return new Specification<ModelLibrary>() {
            @Override
            public Predicate toPredicate(Root<ModelLibrary> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (fromDate != null) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.<Long> get("createdDate"), fromDate);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<ModelLibrary> withCreatedDateTill(final Long tillDate) {
        return new Specification<ModelLibrary>() {
            @Override
            public Predicate toPredicate(Root<ModelLibrary> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (tillDate != null) {
                    return criteriaBuilder.lessThanOrEqualTo(root.<Long> get("createdDate"), tillDate);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }
}
