package com.fooddelivery.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Use environment variables for database configuration
    private static final String DB_URL = getDbUrl();
    private static final String DB_USER = getDbUser();
    private static final String DB_PASSWORD = getDbPassword();

    private static String getDriverClass() {
        if (DB_URL != null && DB_URL.contains("postgresql")) {
            return "org.postgresql.Driver";
        }
        return "org.h2.Driver";
    }

    /**
     * Get database URL from environment variable or use default
     */
    private static String getDbUrl() {
        String dbUrl = System.getenv("FOODDB_URL");
        if (dbUrl != null && !dbUrl.isEmpty()) {
            // Render and some PaaS provide postgres:// or postgresql:// instead of jdbc:postgresql://
            if (dbUrl.startsWith("postgres://")) {
                return "jdbc:postgresql://" + dbUrl.substring(11);
            } else if (dbUrl.startsWith("postgresql://")) {
                return "jdbc:postgresql://" + dbUrl.substring(13);
            } else if (!dbUrl.startsWith("jdbc:")) {
                return "jdbc:" + dbUrl;
            }
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
        
        // If URL contains credentials (e.g., jdbc:postgresql://user:pass@host...), no default user
        if (DB_URL != null && DB_URL.contains("@") && !DB_URL.contains("h2:mem")) {
            return null;
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
        
        // If URL contains credentials, no need to guess a password
        if (DB_URL != null && DB_URL.contains("@") && !DB_URL.contains("h2:mem")) {
            return null;
        }
        
        // Try common default passwords
        String[] commonPasswords = {"", "postgres", "password", "root"};
        for (String pwd : commonPasswords) {
            try {
                Class.forName(getDriverClass());
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, pwd);
                conn.close();
                // Success! Return this password
                return pwd;
            } catch (Exception e) {
                // This password didn't work, try next
            }
        }
        
        return "";
    }

    public static Connection getConnection() {
        try {
            Class.forName(getDriverClass());
            
            // If the URL already contains the credentials (like Render), we can just connect directly
            if (DB_USER == null && DB_PASSWORD == null) {
                 return DriverManager.getConnection(DB_URL);
            }
            
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database Driver not found! Expected: " + getDriverClass(), e);
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed! URL: " + DB_URL, e);
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