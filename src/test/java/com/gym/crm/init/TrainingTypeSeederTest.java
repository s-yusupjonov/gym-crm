package com.gym.crm.init;

import com.gym.crm.domain.TrainingType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeSeederTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Transaction transaction;

    @Mock
    private Query<Long> query;

    @Test
    void seedShouldPersistDefaultTypesWhenTableIsEmpty() {
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.createQuery("select count(t) from TrainingType t", Long.class)).thenReturn(query);
        when(query.uniqueResult()).thenReturn(0L);

        TrainingTypeSeeder seeder = new TrainingTypeSeeder(sessionFactory);
        seeder.seed();

        verify(session, times(6)).persist(any(TrainingType.class));
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    void seedShouldNotPersistWhenTypesAlreadyExist() {
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.createQuery("select count(t) from TrainingType t", Long.class)).thenReturn(query);
        when(query.uniqueResult()).thenReturn(3L);

        TrainingTypeSeeder seeder = new TrainingTypeSeeder(sessionFactory);
        seeder.seed();

        verify(session, never()).persist(any());
        verify(transaction).commit();
    }

    @Test
    void seedShouldNotPersistWhenCountIsNull() {
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.createQuery("select count(t) from TrainingType t", Long.class)).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        TrainingTypeSeeder seeder = new TrainingTypeSeeder(sessionFactory);
        seeder.seed();

        verify(session, never()).persist(any());
        verify(transaction).commit();
    }
}
