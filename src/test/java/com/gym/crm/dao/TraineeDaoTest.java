package com.gym.crm.dao;
import com.gym.crm.domain.*;
import com.gym.crm.storage.InMemoryStorage;
import org.junit.jupiter.api.*;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;
class TraineeDaoTest {
    private TraineeDao dao;
    @BeforeEach void setUp() {
        InMemoryStorage s = new InMemoryStorage(new HashMap<Long,Trainee>(), new HashMap<Long,Trainer>(), new HashMap<Long,Training>());
        dao = new TraineeDao(s);
    }
    @Test void shouldCreateAndFindTrainee() {
        Trainee t = new Trainee(); t.setFirstName("Test");
        Trainee c = dao.create(t);
        assertNotNull(c.getTraineeId());
        assertTrue(dao.findById(c.getTraineeId()).isPresent());
    }
    @Test void shouldDeleteTrainee() {
        Trainee c = dao.create(new Trainee());
        dao.delete(c.getTraineeId());
        assertTrue(dao.findById(c.getTraineeId()).isEmpty());
    }
    @Test void shouldReturnAll() {
        dao.create(new Trainee()); dao.create(new Trainee());
        assertEquals(2, dao.findAll().size());
    }
}
