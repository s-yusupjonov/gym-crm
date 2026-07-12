package com.gym.crm.service;

import com.gym.crm.dao.UserDao;
import com.gym.crm.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserDao userDao;

    @Autowired
    public AuthenticationService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional(readOnly = true)
    public boolean matchCredentials(String username, String password) {
        return userDao.findByUsername(username)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public void authenticate(String username, String password) {
        if (!matchCredentials(username, password)) {
            log.warn("Authentication failed for username={}", username);
            throw new AuthenticationException("Invalid username or password");
        }
    }
}