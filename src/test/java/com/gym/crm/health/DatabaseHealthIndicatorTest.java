package com.gym.crm.health;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseHealthIndicatorTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Test
    void healthShouldReportUpWhenConnectionIsValid() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(true);

        Health health = new DatabaseHealthIndicator(dataSource).health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("reachable", health.getDetails().get("database"));
    }

    @Test
    void healthShouldReportDownWhenConnectionIsNotValid() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(false);

        Health health = new DatabaseHealthIndicator(dataSource).health();

        assertEquals(Status.DOWN, health.getStatus());
    }

    @Test
    void healthShouldReportDownWhenConnectionThrows() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("connection refused"));

        Health health = new DatabaseHealthIndicator(dataSource).health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("unreachable", health.getDetails().get("database"));
    }
}