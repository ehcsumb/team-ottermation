package com.tracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// handles everything database related
// one connection shared across the whole app
public class Database {

    private static Connection conn;

    // call this once from Main.java when the app starts
    public static void connect() {
        try {
            // creates tracker.db file if it doesn't exist
            conn = DriverManager.getConnection("jdbc:sqlite:tracker.db");
            createTables();
        } catch (SQLException e) {
            System.out.println("database error: " + e.getMessage());
        }
    }

    // creates all tables if they don't exist yet
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
                priority    TEXT DEFAULT 'medium',
                task_type   TEXT,
                done        INTEGER DEFAULT 0,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """);

        // stores global app settings, only ever one row
        s.execute("""
            CREATE TABLE IF NOT EXISTS settings (
                id               INTEGER PRIMARY KEY DEFAULT 1,
                default_priority TEXT DEFAULT 'medium'
            )
        """);

        // seed a default admin account for testing
        s.execute("""
            INSERT OR IGNORE INTO users (username, password, role)
            VALUES ('admin', 'admin', 'admin')
        """);

        // seed one settings row so there is always something to read
        s.execute("INSERT OR IGNORE INTO settings (id) VALUES (1)");
    }

    // checks username and password against the database
    // returns a User object if correct, null if not
    public static User login(String username, String password) {
        try {
            // use prepared statement to safely insert user input
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT id, username, role FROM users WHERE username = ? AND password = ?"
            );
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            // if a row comes back, credentials are correct
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("role"));
            }
        } catch (SQLException e) {
            System.out.println("login error: " + e.getMessage());
        }

        // no match found
        return null;
    }

    public static void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("close error: " + e.getMessage());
        }
    }
}