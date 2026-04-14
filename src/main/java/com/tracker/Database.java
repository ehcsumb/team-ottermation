package com.tracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * handles all database operations for the app.
 * creates tables on first run and provides methods for login and registration.
 *
 * @author Khiem Vo
 * @since 4/6/2026
 */
public class Database {

    private static Connection conn;

    /**
     * opens a connection to tracker.db and creates tables if they don't exist.
     * call this once from main.java when the app starts.
     */
    public static void connect() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:tracker.db");
            createTables();
        } catch (SQLException e) {
            System.out.println("database error: " + e.getMessage());
        }
    }

    /**
     * creates all four tables if they don't exist yet.
     * also seeds the default admin account and task types.
     *
     * @throws SQLException if a table creation query fails
     */
    private static void createTables() throws SQLException {
        Statement s = conn.createStatement();

        // stores login info and role for each user
        s.execute("""
            CREATE TABLE IF NOT EXISTS users (
                id       INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role     TEXT NOT NULL DEFAULT 'user'
            )
        """);

        // stores tasks created by each user
        s.execute("""
            CREATE TABLE IF NOT EXISTS tasks (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id     INTEGER NOT NULL,
                title       TEXT NOT NULL,
                description TEXT,
                due_date    TEXT,
                priority    TEXT DEFAULT 'Medium',
                task_type   TEXT,
                done        INTEGER DEFAULT 0,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """);

        // stores global app settings, only ever one row
        s.execute("""
            CREATE TABLE IF NOT EXISTS settings (
                id               INTEGER PRIMARY KEY DEFAULT 1,
                default_priority TEXT DEFAULT 'Medium'
            )
        """);

        // stores task types managed by admin
        s.execute("""
            CREATE TABLE IF NOT EXISTS task_types (
                id   INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            )
        """);

        // seed default admin account
        s.execute("""
            INSERT OR IGNORE INTO users (username, password, role)
            VALUES ('admin', 'admin', 'admin')
        """);

        // seed one settings row
        s.execute("INSERT OR IGNORE INTO settings (id) VALUES (1)");

        // seed default task types
        s.execute("INSERT OR IGNORE INTO task_types (name) VALUES ('Appointment')");
        s.execute("INSERT OR IGNORE INTO task_types (name) VALUES ('Issue')");
    }

    /**
     * checks a username and password against the users table.
     *
     * @param username the username to check
     * @param password the password to check
     * @return a user object if credentials match, null if not
     */
    public static User login(String username, String password) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, username, role FROM users WHERE username = ? AND password = ?"
            );
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("role"));
            }
        } catch (SQLException e) {
            System.out.println("login error: " + e.getMessage());
        }
        return null;
    }

    /**
     * inserts a new user into the users table with role 'user'.
     *
     * @param username the new username
     * @param password the new password
     * @return true if registration worked, false if username is already taken
     */
    public static boolean register(String username, String password) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (username, password) VALUES (?, ?)"
            );
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * returns the shared database connection.
     * used by dao classes to run their own queries.
     *
     * @return the active sqlite connection
     */
    public static Connection getConnection() {
        return conn;
    }

    /**
     * closes the database connection when the app shuts down.
     */
    public static void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("close error: " + e.getMessage());
        }
    }
}
