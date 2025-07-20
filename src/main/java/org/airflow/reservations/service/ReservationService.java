package org.airflow.reservations.service;

import org.airflow.reservations.model.WaitingListEntry;

/**
 * Servicio para manejar reservas y asignación desde la lista de espera.
 */
public class ReservationService {
    private final WaitingListService waitingListService;

    public ReservationService(WaitingListService waitingListService) {
        this.waitingListService = waitingListService;
    }

    /**
     * Llamar este método cuando se libera un asiento.
     * Asigna automáticamente el asiento al siguiente usuario en la lista de espera.
     */
    public void seatReleased() {
        WaitingListEntry nextUser = waitingListService.assignNextUser();
        if (nextUser != null) {
            // Aquí iría la lógica para crear la reserva al usuario y notificarle
            System.out.println("Asignando asiento a usuario: " + nextUser.getUserId());
            // Ejemplo:
            // createReservation(nextUser.getUserId());
            // sendNotification(nextUser.getUserId());
        } else {
            System.out.println("No hay usuarios en la lista de espera.");
        }
    }
}