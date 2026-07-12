package com.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T> {

    private final SessionFactory sessionFactory;
    private final Class<T> entityClass;

    protected AbstractDao(SessionFactory sessionFactory, Class<T> entityClass) {
        this.sessionFactory = sessionFactory;
        this.entityClass = entityClass;
    }

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public T save(T entity) {
        currentSession().persist(entity);
        return entity;
    }

    public T update(T entity) {
        return currentSession().merge(entity);
    }

    public Optional<T> findById(Long id) {
        return Optional.ofNullable(currentSession().get(entityClass, id));
    }

    public List<T> findAll() {
        return currentSession()
                .createQuery("from " + entityClass.getSimpleName(), entityClass)
                .list();
    }

    public void delete(T entity) {
        currentSession().remove(entity);
    }
}