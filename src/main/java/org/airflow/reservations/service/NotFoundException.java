package org.airflow.reservations.service;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}