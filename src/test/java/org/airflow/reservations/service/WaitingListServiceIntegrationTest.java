package org.airflow.reservations.service;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WaitingListServiceIntegrationTest {

    @Test
    void testRegisterAndAssignWaitingList() {
        WaitingListService service = new WaitingListService();
        int WAITLIST_STATUS = 1;    // Ajusta según tus valores reales
        int CONFIRMED_STATUS = 2;   // Ajusta según tus valores reales
        int FLIGHT_ID = 1;          // Cambia por el vuelo que quieres probar

        try {
            // Registrar dos usuarios a la lista de espera
            service.registerUserToWaitingList(5, FLIGHT_ID, WAITLIST_STATUS);
            service.registerUserToWaitingList(8, FLIGHT_ID, WAITLIST_STATUS);

            // Verifica que están en la lista de espera
            List<Integer> waitlistUsers = service.getWaitingListUserIds(WAITLIST_STATUS, FLIGHT_ID);
            System.out.println("Lista de espera: " + waitlistUsers);
            assertTrue(waitlistUsers.contains(5));
            assertTrue(waitlistUsers.contains(8));

            // Asigna asiento al primero en espera
            service.assignNextUser(WAITLIST_STATUS, CONFIRMED_STATUS, FLIGHT_ID);

            // Lista de espera actualizada
            List<Integer> updatedWaitlist = service.getWaitingListUserIds(WAITLIST_STATUS, FLIGHT_ID);
            System.out.println("Lista de espera actualizada: " + updatedWaitlist);
            assertTrue(updatedWaitlist.size() == waitlistUsers.size() - 1);

        } catch (SQLException e) {
            fail("Error SQL: " + e.getMessage());
        }
    }
}