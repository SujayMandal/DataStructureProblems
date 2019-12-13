package com.fa.dp.business.sop.week0.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.fa.dp.business.sop.week0.entity.DPSopWeek0Param;

@Repository
public class SOPWeek0FilterDaoImpl implements SOPWeek0FilterDao {

    @PersistenceContext
    private EntityManager entityManager;

	@Override
	public List<DPSopWeek0Param> getSOPWeek0FilteredRecords(
			String inputFileName, List<String> statusList, Long fromDate,
			Long toDate) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<DPSopWeek0Param> query = builder.createQuery(DPSopWeek0Param.class);
        final Root r = query.from(DPSopWeek0Param.class);

        Predicate predicate = builder.conjunction();

        if (inputFileName != null) {
            predicate = builder.and(predicate,
                    builder.equal(r.get("sopWeek0ProcessStatus").get("inputFileName"),inputFileName));
        }

        if(CollectionUtils.isNotEmpty(statusList)) {
            predicate = builder.and(predicate, r.get("sopWeek0ProcessStatus").get("status").in(statusList));
        }

        if(fromDate != null && toDate != null) {
            predicate = builder.and(predicate, builder.between(r.get("sopWeek0ProcessStatus").get("createdDate"),fromDate,toDate));
        }

        query.where(predicate);
        List<DPSopWeek0Param> dpSopWeek0Param = entityManager.createQuery(query).getResultList();
        return dpSopWeek0Param;
    }
}
