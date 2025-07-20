package org.airflow.reservations.service;

import org.airflow.reservations.model.WaitingList;
import org.airflow.reservations.model.WaitingListEntry;

import java.util.List;

/**
 * Servicio para manejar la lógica de la lista de espera.
 */
public class WaitingListService {
    private final WaitingList waitingList;

    public WaitingListService(WaitingList waitingList) {
        this.waitingList = waitingList;
    }

    /**
     * Registra un usuario en la lista de espera con prioridad.
     */
    public void registerUser(String userId, int priority) {
        long timestamp = System.currentTimeMillis();
        WaitingListEntry entry = new WaitingListEntry(userId, priority, timestamp);
        waitingList.addEntry(entry);
    }

    /**
     * Asigna asiento al usuario con mayor prioridad (si hay asientos disponibles).
     * Retorna el usuario asignado o null si la lista está vacía.
     */
    public WaitingListEntry assignNextUser() {
        if (!waitingList.isEmpty()) {
            return waitingList.pollNext();
        }
        return null;
    }

    /**
     * Consulta todos los usuarios en la lista de espera.
     */
    public List<WaitingListEntry> getWaitingList() {
        return waitingList.getAllEntries();
    }

    /**
     * Verifica si la lista de espera está vacía.
     */
    public boolean isEmpty() {
        return waitingList.isEmpty();
    }
}