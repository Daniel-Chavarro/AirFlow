package org.airflow.reservations.service;

public interface EmailService {
    void send(String to, String subject, String body);
}