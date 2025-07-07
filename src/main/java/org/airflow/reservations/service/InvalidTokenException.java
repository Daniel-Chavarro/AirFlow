package org.airflow.reservations.service;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) { super(message); }
}