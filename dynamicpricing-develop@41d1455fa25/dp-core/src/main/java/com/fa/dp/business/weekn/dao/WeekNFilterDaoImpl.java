package com.fa.dp.business.weekn.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.fa.dp.business.weekn.entity.DPProcessWeekNParam;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

@Repository
public class WeekNFilterDaoImpl implements WeekNFilterDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DPProcessWeekNParam> getWeekNFilteredRecords(final List<String> statusList,
            final Long fromDate, final Long toDate) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<DPProcessWeekNParam> query = builder.createQuery(DPProcessWeekNParam.class);
        final Root r = query.from(DPProcessWeekNParam.class);
        Predicate predicate = builder.conjunction();

        if(CollectionUtils.isNotEmpty(statusList)) {
            predicate = builder.and(predicate, r.get("dpWeekNProcessStatus").get("status").in(statusList));
        }

        if(fromDate != null && toDate != null) {
            predicate = builder.and(predicate, builder.between(r.get("dpWeekNProcessStatus").get("lastModifiedDate"),fromDate,toDate));
        }

        query.where(predicate);
        List<DPProcessWeekNParam> dpProcessWeekNParams = entityManager.createQuery(query).getResultList();
        return dpProcessWeekNParams;
    }
}
