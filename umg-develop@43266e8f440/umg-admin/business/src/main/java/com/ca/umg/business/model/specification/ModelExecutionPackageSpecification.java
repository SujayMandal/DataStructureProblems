package com.ca.umg.business.model.specification;

import java.util.Locale;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.execution.entity.ModelExecutionPackage;

public final class ModelExecutionPackageSpecification {
	
    private ModelExecutionPackageSpecification() {

	}

    public static Specification<ModelExecutionPackage> withPackageName(
			final String name) {
        return new Specification<ModelExecutionPackage>() {
			@Override
            public Predicate toPredicate(Root<ModelExecutionPackage> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(name)) {
					return criteriaBuilder.like(criteriaBuilder.lower(root
							.<String> get("packageName")),
							BusinessConstants.CHAR_PERCENTAGE +name.toLowerCase(Locale.getDefault()) + BusinessConstants.CHAR_PERCENTAGE);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}
    
    public static Specification<ModelExecutionPackage> withPackageType(
			final String packageType) {
        return new Specification<ModelExecutionPackage>() {
			@Override
            public Predicate toPredicate(Root<ModelExecutionPackage> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(packageType)) {
					return criteriaBuilder.equal(criteriaBuilder.lower(root
							.<String> get("packageType")),
							packageType.toLowerCase(Locale.getDefault()));
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

    public static Specification<ModelExecutionPackage> withPackageFolder(
			final String name) {
        return new Specification<ModelExecutionPackage>() {
			@Override
            public Predicate toPredicate(Root<ModelExecutionPackage> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				if (StringUtils.isNotBlank(name)) {
                    return criteriaBuilder.equal(criteriaBuilder.lower(root.<String> get("packageFolder")),
                            name
							.toLowerCase(Locale.getDefault()));
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

    
    public static Specification<ModelExecutionPackage> withCreatedBy(final String searchText) {
        return new Specification<ModelExecutionPackage>() {
            @Override
            public Predicate toPredicate(Root<ModelExecutionPackage> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (StringUtils.isNotBlank(searchText)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(root.<String> get("createdBy")),
                            BusinessConstants.CHAR_PERCENTAGE
                            + searchText.toLowerCase(Locale.getDefault()) + BusinessConstants.CHAR_PERCENTAGE);
                }
                return criteriaBuilder.conjunction();
            }
        };
    }
    
    public static Specification<ModelExecutionPackage> withEnvironmentName(final String modelExecEnvName) {
        return new Specification<ModelExecutionPackage>() {
			@Override
            public Predicate toPredicate(Root<ModelExecutionPackage> root,
					CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (modelExecEnvName != null) {
                    return criteriaBuilder.equal((Expression<?>) root.get("modelExecEnvName"), modelExecEnvName);
				}
				return criteriaBuilder.conjunction();
			}
		};
	}

}
