package com.gym.crm.service;

import com.gym.crm.dao.UserDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    void generateUsernameShouldReturnBaseWhenNoCollision() {
        when(userDao.existsByUsername("John.Doe")).thenReturn(false);

        String username = userProfileService.generateUsername("John", "Doe");

        assertEquals("John.Doe", username);
    }

    @Test
    void generateUsernameShouldAppendSuffixOnSingleCollision() {
        when(userDao.existsByUsername("John.Doe")).thenReturn(true);
        when(userDao.existsByUsername("John.Doe1")).thenReturn(false);

        String username = userProfileService.generateUsername("John", "Doe");

        assertEquals("John.Doe1", username);
    }

    @Test
    void generateUsernameShouldIncrementSuffixUntilFreeSlotFound() {
        when(userDao.existsByUsername("John.Doe")).thenReturn(true);
        when(userDao.existsByUsername("John.Doe1")).thenReturn(true);
        when(userDao.existsByUsername("John.Doe2")).thenReturn(true);
        when(userDao.existsByUsername("John.Doe3")).thenReturn(false);

        String username = userProfileService.generateUsername("John", "Doe");

        assertEquals("John.Doe3", username);
        verify(userDao).existsByUsername("John.Doe");
        verify(userDao).existsByUsername("John.Doe1");
        verify(userDao).existsByUsername("John.Doe2");
        verify(userDao).existsByUsername("John.Doe3");
    }

    @Test
    void generatePasswordShouldHaveExpectedLengthAndCharset() {
        String password = userProfileService.generatePassword();

        assertEquals(10, password.length());
        assertTrue(password.chars().allMatch(c ->
                Character.isLetterOrDigit(c) && c < 128));
    }

    @Test
    void generatePasswordShouldProduceDifferentValuesAcrossCalls() {
        String first = userProfileService.generatePassword();
        String second = userProfileService.generatePassword();

        assertEquals(10, first.length());
        assertEquals(10, second.length());
    }
}
