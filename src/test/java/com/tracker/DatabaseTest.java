package com.tracker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {

    @BeforeAll
    static void setup() {
        // connect to database before running tests
        Database.connect();
    }

    @Test
    void testLoginWithAdminAccount() {
        // admin is seeded by default, should always work
        User user = Database.login("admin", "admin");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertTrue(user.isAdmin());
    }

    @Test
    void testLoginWithWrongPassword() {
        // wrong password should return null
        User user = Database.login("admin", "wrongpassword");
        assertNull(user);
    }

    @Test
    void testRegisterNewUser() {
        // new username should register successfully
        boolean result = Database.register("testuser", "testpass");
        assertTrue(result);
    }

    @Test
    void testRegisterDuplicateUsername() {
        // duplicate username should fail
        Database.register("dupuser", "pass123");
        boolean result = Database.register("dupuser", "pass123");
        assertFalse(result);
    }
}