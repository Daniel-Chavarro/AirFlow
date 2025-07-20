package org.airflow.reservations.model;

public class WaitingListEntry implements Comparable<WaitingListEntry> {
    private String userId;
    private int priority;
    private long timestamp;

    public WaitingListEntry(String userId, int priority, long timestamp) {
        this.userId = userId;
        this.priority = priority;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public int getPriority() {
        return priority;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // El orden será primero por prioridad (mayor valor = mayor prioridad), luego por antigüedad
    @Override
    public int compareTo(WaitingListEntry other) {
        if (this.priority != other.priority) {
            return Integer.compare(other.priority, this.priority); // Mayor prioridad primero
        }
        return Long.compare(this.timestamp, other.timestamp); // Más antiguo primero
    }
}
