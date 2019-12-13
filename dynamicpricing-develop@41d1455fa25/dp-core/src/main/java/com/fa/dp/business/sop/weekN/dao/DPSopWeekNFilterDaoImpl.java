package com.fa.dp.business.sop.weekN.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.fa.dp.business.sop.weekN.entity.DPSopWeekNParam;

@Repository
public class DPSopWeekNFilterDaoImpl implements DPSopWeekNFilterDao {

    @PersistenceContext
    private EntityManager entityManager;

	@Override
	public List<DPSopWeekNParam> getSOPWeekNFilteredRecords(
			String inputFileName, List<String> statusList, Long fromDate,
			Long toDate) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<DPSopWeekNParam> query = builder.createQuery(DPSopWeekNParam.class);
        final Root r = query.from(DPSopWeekNParam.class);

        Predicate predicate = builder.conjunction();

        if (inputFileName != null) {
            predicate = builder.and(predicate,
                    builder.equal(r.get("sopWeekNProcessStatus").get("inputFileName"),inputFileName));
        }

        if(CollectionUtils.isNotEmpty(statusList)) {
            predicate = builder.and(predicate, r.get("sopWeekNProcessStatus").get("status").in(statusList));
        }

        if(fromDate != null && toDate != null) {
            predicate = builder.and(predicate, builder.between(r.get("sopWeekNProcessStatus").get("createdDate"),fromDate,toDate));
        }

        query.where(predicate);
        List<DPSopWeekNParam> dpSopWeekNParam = entityManager.createQuery(query).getResultList();
        return dpSopWeekNParam;
    }
}
