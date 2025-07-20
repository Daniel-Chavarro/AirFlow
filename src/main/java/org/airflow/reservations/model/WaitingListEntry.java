package org.airflow.reservations.model;

/**
 * Representa una entrada en la lista de espera.
 */
public class WaitingListEntry implements Comparable<WaitingListEntry> {
    private final String userId;
    private final int priority;
    private final long timestamp;

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

    /**
     * Ordena primero por prioridad descendente, luego por antigüedad ascendente.
     */
    @Override
    public int compareTo(WaitingListEntry other) {
        if (this.priority != other.priority) {
            return Integer.compare(other.priority, this.priority); // Mayor prioridad primero
        }
        return Long.compare(this.timestamp, other.timestamp); // Más antiguo primero
    }

    @Override
    public String toString() {
        return "WaitingListEntry{" +
                "userId='" + userId + '\'' +
                ", priority=" + priority +
                ", timestamp=" + timestamp +
                '}';
    }
}
