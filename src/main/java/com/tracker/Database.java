package com.tracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// handles everything database related
// one connection shared across the whole app
public class Database {

    // the single connection object
    private static Connection conn;

    // call this once from Main.java when the app starts
    public static void connect() {
        try {
            // creates tracker.db file if it doesn't exist
            conn = DriverManager.getConnection("jdbc:sqlite:tracker.db");
            System.out.println("connected to database");
        } catch (SQLException e) {
            System.out.println("database error: " + e.getMessage());
        }
    }

    // call this when the app closes
    public static void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("close error: " + e.getMessage());
        }
    }
}