package org.airflow.reservations.DAO;

import org.airflow.reservations.model.User;
import org.airflow.reservations.utils.ConnectionDB;
import org.airflow.reservations.utils.PasswordUtils;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UsersDAO class.
 * Tests CRUD operations and specialized queries for User entities.
 */
public class UsersDAOTest {
    private Connection connection;
    private UsersDAO usersDAO;
    private static int testUserId;

    /**
     * Sets up the test environment before each test.
     * Establishes a database connection and creates test data.
     *
     * @throws SQLException if a database error occurs
     */
    @BeforeEach
    void setUp() throws SQLException {
        connection = ConnectionDB.getConnection();
        usersDAO = new UsersDAO(connection);
        createTestData();
    }

    /**
     * Cleans up the test environment after each test.
     * Removes test data and closes the database connection.
     *
     * @throws SQLException if a database error occurs
     */
    @AfterEach
    void tearDown() throws SQLException {
        cleanupTestData();

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    /**
     * Creates test user data in the database for testing purposes.
     *
     * @throws SQLException if a database error occurs
     */
    private void createTestData() throws SQLException {
        LocalDateTime createdAt = LocalDateTime.now();
        String hashedPassword = PasswordUtils.hashPassword("testpassword");

        try (Statement statement = connection.createStatement()) {
            String insertUser = "INSERT INTO users (name, last_name, email, password, isSuperUser, created_at) VALUES " +
                    "('TestUser', 'TestLastName', 'test@example.com', '" + hashedPassword + "', false, '" + createdAt + "')";

            statement.executeUpdate(insertUser, Statement.RETURN_GENERATED_KEYS);
            var rs = statement.getGeneratedKeys();
            if (rs.next()) {
                testUserId = rs.getInt(1);
            }
        }
    }

    /**
     * Removes test user data from the database after tests are complete.
     *
     * @throws SQLException if a database error occurs
     */
    private void cleanupTestData() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM users WHERE email = 'test@example.com'");
        }
    }

    /**
     * Tests the getAll method to ensure it retrieves all users from the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetAll() throws SQLException {
        ArrayList<User> users = usersDAO.getAll();

        assertNotNull(users);
        assertFalse(users.isEmpty(), "User list should not be empty");

        boolean found = false;
        for (User user : users) {
            if (user.getId() == testUserId) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Test user should be in the list");
    }

    /**
     * Tests the getById method to ensure it retrieves a specific user by ID.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetById() throws SQLException {
        User user = usersDAO.getById(testUserId);

        assertNotNull(user);
        assertEquals(testUserId, user.getId());
        assertEquals("TestUser", user.getName());
        assertEquals("TestLastName", user.getLast_name());
        assertEquals("test@example.com", user.getEmail());
    }

    /**
     * Tests the create method to ensure it properly inserts a new user into the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testCreate() throws SQLException {
        User newUser = new User();
        newUser.setName("CreateUser");
        newUser.setLast_name("CreateLastName");
        newUser.setEmail("create@example.com");
        newUser.setPassword(PasswordUtils.hashPassword("createpassword"));
        newUser.setSuperUser(false);
        newUser.setCreated_at(LocalDateTime.now());

        usersDAO.create(newUser);

        ArrayList<User> users = usersDAO.getAll();
        boolean found = false;
        int newId = -1;

        for (User u : users) {
            if (u.getEmail().equals("create@example.com")) {
                found = true;
                newId = u.getId();
                break;
            }
        }

        assertTrue(found, "New user should have been created");

        // Clean up the created user
        if (newId != -1) {
            usersDAO.delete(newId);
        }
    }

    /**
     * Tests the update method to ensure it properly updates an existing user in the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testUpdate() throws SQLException {
        User user = usersDAO.getById(testUserId);

        user.setName("UpdatedUser");
        user.setLast_name("UpdatedLastName");

        usersDAO.update(testUserId, user);

        User updatedUser = usersDAO.getById(testUserId);
        assertEquals("UpdatedUser", updatedUser.getName());
        assertEquals("UpdatedLastName", updatedUser.getLast_name());
        assertEquals("test@example.com", updatedUser.getEmail());
    }

    /**
     * Tests the delete method to ensure it properly removes a user from the database.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testDelete() throws SQLException {
        // Create a user to delete
        User userToDelete = new User();
        userToDelete.setName("DeleteUser");
        userToDelete.setLast_name("DeleteLastName");
        userToDelete.setEmail("delete@example.com");
        userToDelete.setPassword(PasswordUtils.hashPassword("deletepassword"));
        userToDelete.setSuperUser(false);
        userToDelete.setCreated_at(LocalDateTime.now());

        usersDAO.create(userToDelete);

        // Find the ID of the newly created user
        ArrayList<User> users = usersDAO.getAll();
        int deleteId = -1;
        for (User u : users) {
            if (u.getEmail().equals("delete@example.com")) {
                deleteId = u.getId();
                break;
            }
        }

        // Delete the user
        usersDAO.delete(deleteId);

        // Verify it's been deleted
        users = usersDAO.getAll();
        boolean found = false;
        for (User u : users) {
            if (u.getId() == deleteId) {
                found = true;
                break;
            }
        }

        assertFalse(found, "User should have been deleted");
    }

    /**
     * Tests the getUserByEmail method to ensure it retrieves users with email matching the search string.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void testGetByEmail() throws SQLException {
        ArrayList<User> users = usersDAO.getByEmail("test@example");

        assertNotNull(users);
        assertFalse(users.isEmpty(), "Should find users with email containing 'test@example'");

        boolean found = false;
        for (User user : users) {
            if (user.getEmail().equals("test@example.com")) {
                found = true;
                assertEquals("TestUser", user.getName());
                assertEquals("TestLastName", user.getLast_name());
                break;
            }
        }

        assertTrue(found, "Test user should be in the results");

        // Test with a partial email that should match
        users = usersDAO.getByEmail("example.com");
        assertFalse(users.isEmpty(), "Should find users with email containing 'example.com'");

        // Test with an email that shouldn't match
        users = usersDAO.getByEmail("nonexistent@email.com");
        assertTrue(users.isEmpty(), "Should not find users with email containing 'nonexistent@email.com'");
    }

}
