package com.fa.dp.business.week0.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.fa.dp.business.week0.entity.DPProcessParam;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

@Repository
public class Week0FilterDaoImpl implements Week0FilterDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DPProcessParam> getWeek0FilteredRecords(final String inputFileName, final List<String> statusList,
            final Long fromDate, final Long toDate) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<DPProcessParam> query = builder.createQuery(DPProcessParam.class);
        final Root r = query.from(DPProcessParam.class);

        Predicate predicate = builder.conjunction();

        if (inputFileName != null) {
            predicate = builder.and(predicate,
                    builder.equal(r.get("dynamicPricingFilePrcsStatus").get("inputFileName"),inputFileName));
        }

        if(CollectionUtils.isNotEmpty(statusList)) {
            predicate = builder.and(predicate, r.get("dynamicPricingFilePrcsStatus").get("status").in(statusList));
        }

        if(fromDate != null && toDate != null) {
            predicate = builder.and(predicate, builder.between(r.get("dynamicPricingFilePrcsStatus").get("uploadTimestamp"),fromDate,toDate));
        }


        query.where(predicate);
        List<DPProcessParam> dpProcessParams = entityManager.createQuery(query).getResultList();
        return dpProcessParams;
    }
}
