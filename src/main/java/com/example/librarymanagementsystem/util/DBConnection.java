package com.example.librarymanagementsystem.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:sqlite:database/library.db";

    /** Returns a connection to the SQLite database */
    public static Connection getConnection() {
        try {
            // Optional: load the SQLite JDBC driver (not strictly needed for modern JDBC)
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to connect to database!");
            e.printStackTrace();
        }
        return null;
    }
}