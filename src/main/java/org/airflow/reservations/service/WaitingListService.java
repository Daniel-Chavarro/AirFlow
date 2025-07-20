package org.airflow.reservations.service;

import org.airflow.reservations.dao.WaitingListDao;
import org.airflow.reservations.model.WaitingListEntry;

import java.sql.SQLException;
import java.util.List;

public class WaitingListService {
    private final WaitingListDao dao;

    public WaitingListService(WaitingListDao dao) {
        this.dao = dao;
    }

    public void registerUser(String userId, int priority) throws SQLException {
        if (dao.userExists(userId)) {
            System.out.println("El usuario ya está en la lista de espera.");
            return;
        }
        long timestamp = System.currentTimeMillis();
        dao.addEntry(new WaitingListEntry(userId, priority, timestamp));
    }

    public WaitingListEntry assignNextUser() throws SQLException {
        return dao.pollNext();
    }

    public List<WaitingListEntry> getWaitingList() throws SQLException {
        return dao.getAllEntries();
    }
}