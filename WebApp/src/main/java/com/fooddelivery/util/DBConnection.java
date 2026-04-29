package com.fooddelivery.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DRIVER_CLASS = "org.h2.Driver";

    // Use environment variables for database configuration
    private static final String DB_URL = getDbUrl();
    private static final String DB_USER = getDbUser();
    private static final String DB_PASSWORD = getDbPassword();

    /**
     * Get database URL from environment variable or use default
     */
    private static String getDbUrl() {
        String dbUrl = System.getenv("FOODDB_URL");
        if (dbUrl != null && !dbUrl.isEmpty()) {
            return dbUrl;
        }
        // Default: localhost development (H2 in memory)
        return "jdbc:h2:mem:fooddb;MODE=PostgreSQL;INIT=RUNSCRIPT FROM 'classpath:fooddb_h2.sql'";
    }

    /**
     * Get database username from environment variable or use default
     */
    private static String getDbUser() {
        String user = System.getenv("FOODDB_USER");
        if (user != null && !user.isEmpty()) {
            return user;
        }
        return "sa";
    }

    /**
     * Get database password from environment variable
     * Tries common default passwords if not set
     */
    private static String getDbPassword() {
        // First, check environment variable
        String password = System.getenv("FOODDB_PASSWORD");
        if (password != null && !password.isEmpty()) {
            return password;
        }
        
        // Try common default passwords
        String[] commonPasswords = {"", "postgres", "password", "root"};
        for (String pwd : commonPasswords) {
            try {
                Class.forName(DRIVER_CLASS);
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, pwd);
                conn.close();
                // Success! Return this password
                return pwd;
            } catch (Exception e) {
                // This password didn't work, try next
            }
        }
        
        // If none work, log helpful message and return empty
        System.err.println("\n⚠️ DATABASE CONNECTION WARNING ⚠️");
        System.err.println("Could not connect to the database with default passwords.");
        System.err.println("\nTo fix this:");
        System.err.println("1. Set the FOODDB_PASSWORD environment variable:");
        System.err.println("   export FOODDB_PASSWORD='your_database_password'");
        System.err.println("2. Then restart Tomcat");
        System.err.println("\nCommon passwords tried: '', 'postgres', 'password', 'root'");
        System.err.println("If you don't know your database password, reset it or contact your DBA.\n");
        
        return "";
    }

    public static Connection getConnection() {
        try {
            Class.forName(DRIVER_CLASS);
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found! Ensure mysql-connector-java is in classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed! Check credentials and database server.", e);
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }
}