package org.airflow.reservations.service;

import org.airflow.reservations.DAO.ReservationDAO;
import org.airflow.reservations.model.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ReservationService class using mocked dependencies.
 * Tests the waitlist logic without requiring database connectivity.
 */
@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {
    
    @Mock
    private ReservationDAO mockReservationDAO;
    
    private ReservationService reservationService;
    
    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(mockReservationDAO);
    }
    
    /**
     * Tests getWaitlist method with mocked DAO.
     */
    @Test
    void testGetWaitlist() throws SQLException {
        // Arrange
        ArrayList<Reservation> mockWaitlist = new ArrayList<>();
        Reservation reservation1 = new Reservation(1, 1, 1, 1, LocalDateTime.now().minusHours(2));
        Reservation reservation2 = new Reservation(2, 2, 1, 1, LocalDateTime.now().minusHours(1));
        mockWaitlist.add(reservation1);
        mockWaitlist.add(reservation2);
        
        when(mockReservationDAO.getWaitlistReservations()).thenReturn(mockWaitlist);
        
        // Act
        ArrayList<Reservation> result = reservationService.getWaitlist();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
        verify(mockReservationDAO, times(1)).getWaitlistReservations();
    }
    
    /**
     * Tests getWaitlistByFlight method with mocked DAO.
     */
    @Test
    void testGetWaitlistByFlight() throws SQLException {
        // Arrange
        ArrayList<Reservation> mockWaitlist = new ArrayList<>();
        Reservation reservation = new Reservation(1, 1, 1, 1, LocalDateTime.now());
        mockWaitlist.add(reservation);
        
        when(mockReservationDAO.getWaitlistReservationsByFlight(1)).thenReturn(mockWaitlist);
        
        // Act
        ArrayList<Reservation> result = reservationService.getWaitlistByFlight(1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getFlight_FK());
        verify(mockReservationDAO, times(1)).getWaitlistReservationsByFlight(1);
    }
    
    /**
     * Tests assignNextWaitingUser method with non-empty waitlist.
     */
    @Test
    void testAssignNextWaitingUser() throws SQLException {
        // Arrange
        ArrayList<Reservation> mockWaitlist = new ArrayList<>();
        Reservation oldestReservation = new Reservation(1, 1, 1, 1, LocalDateTime.now().minusHours(2));
        Reservation newerReservation = new Reservation(2, 2, 1, 1, LocalDateTime.now().minusHours(1));
        mockWaitlist.add(oldestReservation);
        mockWaitlist.add(newerReservation);
        
        when(mockReservationDAO.getWaitlistReservations()).thenReturn(mockWaitlist);
        
        // Act
        Reservation result = reservationService.assignNextWaitingUser();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(2, result.getStatus_FK()); // Should be changed to confirmed
        verify(mockReservationDAO, times(1)).getWaitlistReservations();
        verify(mockReservationDAO, times(1)).update(1, oldestReservation);
    }
    
    /**
     * Tests assignNextWaitingUser method with empty waitlist.
     */
    @Test
    void testAssignNextWaitingUserEmptyWaitlist() throws SQLException {
        // Arrange
        ArrayList<Reservation> emptyWaitlist = new ArrayList<>();
        when(mockReservationDAO.getWaitlistReservations()).thenReturn(emptyWaitlist);
        
        // Act
        Reservation result = reservationService.assignNextWaitingUser();
        
        // Assert
        assertNull(result);
        verify(mockReservationDAO, times(1)).getWaitlistReservations();
        verify(mockReservationDAO, never()).update(anyInt(), any(Reservation.class));
    }
    
    /**
     * Tests assignNextWaitingUserByFlight method with non-empty waitlist.
     */
    @Test
    void testAssignNextWaitingUserByFlight() throws SQLException {
        // Arrange
        ArrayList<Reservation> mockWaitlist = new ArrayList<>();
        Reservation reservation = new Reservation(1, 1, 1, 1, LocalDateTime.now());
        mockWaitlist.add(reservation);
        
        when(mockReservationDAO.getWaitlistReservationsByFlight(1)).thenReturn(mockWaitlist);
        
        // Act
        Reservation result = reservationService.assignNextWaitingUserByFlight(1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(2, result.getStatus_FK()); // Should be changed to confirmed
        verify(mockReservationDAO, times(1)).getWaitlistReservationsByFlight(1);
        verify(mockReservationDAO, times(1)).update(1, reservation);
    }
    
    /**
     * Tests assignNextWaitingUserByFlight method with empty waitlist.
     */
    @Test
    void testAssignNextWaitingUserByFlightEmptyWaitlist() throws SQLException {
        // Arrange
        ArrayList<Reservation> emptyWaitlist = new ArrayList<>();
        when(mockReservationDAO.getWaitlistReservationsByFlight(999)).thenReturn(emptyWaitlist);
        
        // Act
        Reservation result = reservationService.assignNextWaitingUserByFlight(999);
        
        // Assert
        assertNull(result);
        verify(mockReservationDAO, times(1)).getWaitlistReservationsByFlight(999);
        verify(mockReservationDAO, never()).update(anyInt(), any(Reservation.class));
    }
    
    /**
     * Tests createReservation method.
     */
    @Test
    void testCreateReservation() throws SQLException {
        // Arrange
        Reservation newReservation = new Reservation(0, 1, 1, 1, LocalDateTime.now());
        
        // Act
        reservationService.createReservation(newReservation);
        
        // Assert
        verify(mockReservationDAO, times(1)).create(newReservation);
    }
    
    /**
     * Tests getAllReservations method.
     */
    @Test
    void testGetAllReservations() throws SQLException {
        // Arrange
        ArrayList<Reservation> mockReservations = new ArrayList<>();
        when(mockReservationDAO.getAll()).thenReturn(mockReservations);
        
        // Act
        ArrayList<Reservation> result = reservationService.getAllReservations();
        
        // Assert
        assertNotNull(result);
        verify(mockReservationDAO, times(1)).getAll();
    }
    
    /**
     * Tests getReservationById method.
     */
    @Test
    void testGetReservationById() throws SQLException {
        // Arrange
        Reservation mockReservation = new Reservation(1, 1, 1, 1, LocalDateTime.now());
        when(mockReservationDAO.getById(1)).thenReturn(mockReservation);
        
        // Act
        Reservation result = reservationService.getReservationById(1);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(mockReservationDAO, times(1)).getById(1);
    }
    
    /**
     * Tests getReservationsByUser method.
     */
    @Test
    void testGetReservationsByUser() throws SQLException {
        // Arrange
        ArrayList<Reservation> mockReservations = new ArrayList<>();
        when(mockReservationDAO.getByUserId(1)).thenReturn(mockReservations);
        
        // Act
        ArrayList<Reservation> result = reservationService.getReservationsByUser(1);
        
        // Assert
        assertNotNull(result);
        verify(mockReservationDAO, times(1)).getByUserId(1);
    }
    
    /**
     * Tests getReservationsByFlight method.
     */
    @Test
    void testGetReservationsByFlight() throws SQLException {
        // Arrange
        ArrayList<Reservation> mockReservations = new ArrayList<>();
        when(mockReservationDAO.getByFlightId(1)).thenReturn(mockReservations);
        
        // Act
        ArrayList<Reservation> result = reservationService.getReservationsByFlight(1);
        
        // Assert
        assertNotNull(result);
        verify(mockReservationDAO, times(1)).getByFlightId(1);
    }
}