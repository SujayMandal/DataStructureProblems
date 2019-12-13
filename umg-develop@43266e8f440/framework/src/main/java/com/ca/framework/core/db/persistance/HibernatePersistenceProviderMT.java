package com.ca.framework.core.db.persistance;

import java.util.Map;

import javax.persistence.Cache;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.spi.PersistenceUnitInfo;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManager;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.internal.metamodel.EntityTypeImpl;

import com.ca.framework.core.requestcontext.RequestContext;


public class HibernatePersistenceProviderMT extends HibernatePersistenceProvider {
    @SuppressWarnings("rawtypes")
    public EntityManagerFactory createEntityManagerFactory(
            String persistenceUnitName, Map overridenProperties) {
        return wrapEntityManagerFactory(super.createEntityManagerFactory(
                persistenceUnitName, overridenProperties));
    }

    public EntityManagerFactory createContainerEntityManagerFactory(
            PersistenceUnitInfo info, Map properties) {
        return wrapEntityManagerFactory(super
                .createContainerEntityManagerFactory(info, properties));
    }

    private EntityManagerFactoryWrapper wrapEntityManagerFactory(
            EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory == null ? null
                : new EntityManagerFactoryWrapper(
                        (HibernateEntityManagerFactory) entityManagerFactory);
    }

    public static class EntityManagerFactoryWrapper implements
            HibernateEntityManagerFactory {
        private static final long serialVersionUID = -1014935565316877549L;

        private HibernateEntityManagerFactory parentEntityManagerFactory;

        public EntityManagerFactoryWrapper(
                HibernateEntityManagerFactory entityManagerFactory) {
            parentEntityManagerFactory = entityManagerFactory;
        }

        public EntityManager createEntityManager() {
            return initSession(parentEntityManagerFactory.createEntityManager());
        }

        @SuppressWarnings("rawtypes")
        public EntityManager createEntityManager(Map p_map) {
            return initSession(parentEntityManagerFactory
                    .createEntityManager(p_map));
        }

        private EntityManager initSession(EntityManager entityManager) {
            if (RequestContext.getRequestContext() != null && RequestContext.getRequestContext().getTenantCode() != null) {
                Session session = ((HibernateEntityManager) entityManager)
                        .getSession();
                if (session.getEnabledFilter("system_tenantFilter") != null) {
                    return entityManager;
                }

                session.enableFilter("system_tenantFilter");
                Filter tenantFilter = session
                        .getEnabledFilter("system_tenantFilter");
                String tenantFilterString = null;

                tenantFilterString = RequestContext.getRequestContext().getTenantCode();

                tenantFilter.setParameter("tenantFilterString",
                        tenantFilterString);
            }
            return entityManager;
        }

        @Override
        public void close() {
            parentEntityManagerFactory.close();

        }

        @Override
        public Cache getCache() {
            return parentEntityManagerFactory.getCache();
        }

        @Override
        public CriteriaBuilder getCriteriaBuilder() {
            return parentEntityManagerFactory.getCriteriaBuilder();
        }

        @Override
        public Metamodel getMetamodel() {
            return parentEntityManagerFactory.getMetamodel();
        }

        @Override
        public PersistenceUnitUtil getPersistenceUnitUtil() {
            return parentEntityManagerFactory.getPersistenceUnitUtil();
        }

        @Override
        public Map<String, Object> getProperties() {
            return parentEntityManagerFactory.getProperties();
        }

        @Override
        public boolean isOpen() {
            return parentEntityManagerFactory.isOpen();
        }

        @Override
        public SessionFactory getSessionFactory() {
            return parentEntityManagerFactory.getSessionFactory();
        }

        @Override
        public EntityTypeImpl getEntityTypeByName(String arg0) {
            return parentEntityManagerFactory.getEntityTypeByName(arg0);
        }

        @Override
        public <T> void addNamedEntityGraph(String arg0, EntityGraph<T> arg1) {
            parentEntityManagerFactory.addNamedEntityGraph(arg0, arg1);
        }

        @Override
        public void addNamedQuery(String arg0, Query arg1) {
            parentEntityManagerFactory.addNamedQuery(arg0, arg1);
        }

        @Override
        public EntityManager createEntityManager(SynchronizationType arg0) {            
            return parentEntityManagerFactory.createEntityManager(arg0);
        }

        @Override
        public EntityManager createEntityManager(SynchronizationType arg0,
                Map arg1) {
            return parentEntityManagerFactory.createEntityManager(arg0, arg1);
        }

        @Override
        public <T> T unwrap(Class<T> arg0) {
            return parentEntityManagerFactory.unwrap(arg0);
        }
    }
}