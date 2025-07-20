package org.airflow.reservations.service;

import org.airflow.reservations.dao.WaitingListDao;
import org.airflow.reservations.model.WaitingListEntry;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WaitingListServiceIntegrationTest {

    private WaitingListService service;

    @BeforeEach
    void setUp() {
        service = new WaitingListService(new WaitingListDao());
    }

    @Test
    @Order(1)
    void testRegisterUserAndAssign() throws SQLException {
        service.registerUser("userA", 2);
        service.registerUser("userB", 5);
        service.registerUser("userC", 3);

        WaitingListEntry assigned = service.assignNextUser();
        assertEquals("userB", assigned.getUserId());

        assigned = service.assignNextUser();
        assertEquals("userC", assigned.getUserId());

        assigned = service.assignNextUser();
        assertEquals("userA", assigned.getUserId());
    }

    @Test
    @Order(2)
    void testPreventDuplicateUsers() throws SQLException {
        service.registerUser("userA", 1);
        service.registerUser("userA", 3); // Duplicado

        List<WaitingListEntry> entries = service.getWaitingList();
        long count = entries.stream().filter(e -> e.getUserId().equals("userA")).count();
        assertEquals(1, count);
    }
}