package org.airflow.reservations.model;

import java.util.PriorityQueue;

public class WaitingList {
    // Un PriorityQueue para mantener el orden por prioridad
    private PriorityQueue<WaitingListEntry> entries;

    public WaitingList() {
        entries = new PriorityQueue<>();
    }

    public void addEntry(WaitingListEntry entry) {
        entries.add(entry);
    }

    public WaitingListEntry pollNext() {
        return entries.poll();
    }
}