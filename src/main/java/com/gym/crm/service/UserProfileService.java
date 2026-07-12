package com.gym.crm.service;

import com.gym.crm.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
public class UserProfileService {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;

    private final SecureRandom random = new SecureRandom();
    private final UserDao userDao;

    @Autowired
    public UserProfileService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Transactional(readOnly = true)
    public String generateUsername(String firstName, String lastName) {
        String base = firstName + "." + lastName;

        if (!userDao.existsByUsername(base)) {
            return base;
        }

        int suffix = 1;
        String candidate = base + suffix;

        while (userDao.existsByUsername(candidate)) {
            suffix++;
            candidate = base + suffix;
        }

        return candidate;
    }

    public String generatePassword() {
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }

        return sb.toString();
    }
}