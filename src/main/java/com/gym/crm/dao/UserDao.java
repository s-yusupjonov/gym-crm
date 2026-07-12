package com.gym.crm.dao;

import com.gym.crm.domain.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDao extends AbstractDao<User> {

    @Autowired
    public UserDao(SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
    }

    public Optional<User> findByUsername(String username) {
        return currentSession()
                .createQuery("from User where username = :username", User.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    public boolean existsByUsername(String username) {
        Long count = currentSession()
                .createQuery("select count(u) from User u where u.username = :username", Long.class)
                .setParameter("username", username)
                .uniqueResult();
        return count != null && count > 0;
    }
}