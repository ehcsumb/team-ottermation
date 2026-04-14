package com.tracker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * unit tests for the database class.
 * tests login and registration against a real sqlite database.
 *
 * @author Khiem Vo
 * @since 4/7/2026
 */
public class DatabaseTest {

    /**
     * connects to the database before any tests run.
     */
    @BeforeAll
    static void setup() {
        Database.connect();
    }

    /**
     * checks that the seeded admin account can log in successfully.
     */
    @Test
    void testLoginWithAdminAccount() {
        User user = Database.login("admin", "admin");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertTrue(user.isAdmin());
    }

    /**
     * checks that a wrong password returns null.
     */
    @Test
    void testLoginWithWrongPassword() {
        User user = Database.login("admin", "wrongpassword");
        assertNull(user);
    }

    /**
     * checks that a new unique username registers successfully.
     */
    @Test
    void testRegisterNewUser() {
        boolean result = Database.register("testuser", "testpass");
        assertTrue(result);
    }

    /**
     * checks that registering a duplicate username returns false.
     */
    @Test
    void testRegisterDuplicateUsername() {
        Database.register("dupuser", "pass123");
        boolean result = Database.register("dupuser", "pass123");
        assertFalse(result);
    }
}