package org.airflow.reservations.model;

import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.List;

/**
 * Estructura para la lista de espera.
 */
public class WaitingList {
    private final PriorityQueue<WaitingListEntry> queue;

    public WaitingList() {
        this.queue = new PriorityQueue<>();
    }

    /**
     * Agrega una entrada a la lista de espera.
     */
    public void addEntry(WaitingListEntry entry) {
        queue.offer(entry);
    }

    /**
     * Retira y retorna la siguiente entrada (mayor prioridad y más antiguo).
     */
    public WaitingListEntry pollNext() {
        return queue.poll();
    }

    /**
     * Retorna todas las entradas en la lista de espera (sin modificarla).
     */
    public List<WaitingListEntry> getAllEntries() {
        return new ArrayList<>(queue);
    }

    /**
     * Verifica si la lista está vacía.
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }
}