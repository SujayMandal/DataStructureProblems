package com.ca.umg.business.version.audit;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.internal.InAuditExpression;
import org.hibernate.envers.query.internal.property.EntityPropertyName;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

import com.ca.umg.business.version.entity.Version;

@Named
public class VersionAuditFactory {
    @Inject
    private EntityManagerFactory entityManagerFactory;

    private EntityManager em;

    private EntityManager getEntityManager() {
        em = entityManagerFactory.createEntityManager();
        return em;
    }

    private AuditReader getAuditReader() {
        return AuditReaderFactory.get(getEntityManager());
    }

    public <T> List<T> getAllDeletedRevisions(Class<T> entityClass, Version version) {
        try {
            AuditReader reader = getAuditReader();
            AuditQuery query = reader.createQuery().forRevisionsOfEntity(entityClass, false, true);
            query.add(AuditEntity.revisionType().eq(RevisionType.DEL));
            query.add(new InAuditExpression(new EntityPropertyName("name"), new Object[] { version.getName() }));
            List<Object[]> resultData = query.getResultList();
            List<T> deletedEntityList = new ArrayList<T>();
            for (int i = 0; i < resultData.size(); i++) {
                deletedEntityList.add((T) resultData.get(i)[0]);
        }
            return deletedEntityList;
        } finally {
            EntityManagerFactoryUtils.closeEntityManager(this.em);
        }

    }


}
