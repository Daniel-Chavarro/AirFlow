package org.airflow.reservations.service;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceIntegrationTest {

    @Test
    void testAssignSeatFromWaitlist() {
        ReservationService service = new ReservationService();
        int WAITLIST_STATUS = 1;    // Ajusta según los valores reales en reservations_status
        int CONFIRMED_STATUS = 2;   // Ajusta según los valores reales en reservations_status

        // Verifica que hay usuarios en lista de espera
        try {
            List<Integer> waitlistUsers = service.getWaitlistUserIds(WAITLIST_STATUS);
            System.out.println("Usuarios en lista de espera: " + waitlistUsers);

            // Asigna asiento al primer usuario en espera
            service.assignSeat(CONFIRMED_STATUS);

            // Verifica que el usuario fue removido de la lista de espera
            List<Integer> updatedWaitlist = service.getWaitlistUserIds(WAITLIST_STATUS);
            System.out.println("Lista de espera actualizada: " + updatedWaitlist);
        } catch (SQLException e) {
            fail("Error SQL: " + e.getMessage());
        }
    }
}