package org.airflow.reservations.model;

public enum Priority {
    PREMIUM,
    REGULAR,
    EMERGENCIA;

    public static Priority fromString(String value) {
        switch (value.toUpperCase()) {
            case "PREMIUM": return PREMIUM;
            case "REGULAR": return REGULAR;
            case "EMERGENCIA": return EMERGENCIA;
            default: throw new IllegalArgumentException("Nivel de prioridad inválido: " + value);
        }
    }
}