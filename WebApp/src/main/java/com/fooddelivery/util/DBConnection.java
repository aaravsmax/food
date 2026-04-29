package com.fooddelivery.util;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;
    private static String DRIVER_CLASS;

    static {
        String envUrl = System.getenv("FOODDB_URL");
        
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            if (envUrl.startsWith("postgres://") || envUrl.startsWith("postgresql://")) {
                try {
                    // Parse postgres://user:password@host:port/database?params
                    URI uri = new URI(envUrl);
                    String host = uri.getHost();
                    int port = uri.getPort();
                    String path = uri.getPath();
                    String query = uri.getQuery();
                    
                    String userInfo = uri.getUserInfo();
                    if (userInfo != null) {
                        String[] split = userInfo.split(":", 2);
                        DB_USER = split[0];
                        if (split.length > 1) {
                            DB_PASSWORD = split[1];
                        }
                    } else {
                        DB_USER = System.getenv("FOODDB_USER");
                        DB_PASSWORD = System.getenv("FOODDB_PASSWORD");
                    }
                    
                    // Reconstruct into JDBC format: jdbc:postgresql://host:port/database?params
                    DB_URL = "jdbc:postgresql://" + host + (port != -1 ? ":" + port : "") + path;
                    if (query != null) {
                        DB_URL += "?" + query;
                    }
                    DRIVER_CLASS = "org.postgresql.Driver";
                } catch (Exception e) {
                    DB_URL = envUrl;
                    DB_USER = System.getenv("FOODDB_USER");
                    DB_PASSWORD = System.getenv("FOODDB_PASSWORD");
                    DRIVER_CLASS = "org.postgresql.Driver";
                }
            } else {
                DB_URL = envUrl;
                DB_USER = System.getenv("FOODDB_USER");
                DB_PASSWORD = System.getenv("FOODDB_PASSWORD");
                if (!DB_URL.startsWith("jdbc:")) {
                    DB_URL = "jdbc:" + DB_URL;
                }
                DRIVER_CLASS = DB_URL.contains("postgresql") ? "org.postgresql.Driver" : "org.h2.Driver";
            }
        } else {
            // Default: localhost development (H2 in memory)
            DB_URL = "jdbc:h2:mem:fooddb;MODE=PostgreSQL;INIT=RUNSCRIPT FROM 'classpath:fooddb_h2.sql'";
            DB_USER = "sa";
            DB_PASSWORD = "";
            DRIVER_CLASS = "org.h2.Driver";
        }
        
        if (DB_USER == null) DB_USER = "sa";
        if (DB_PASSWORD == null) DB_PASSWORD = "";
    }

    private static boolean initialized = false;

    private static synchronized void initializeDatabase(Connection conn) {
        if (initialized) return;
        try {
            java.sql.Statement stmt = conn.createStatement();
            
            // Create users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(50) PRIMARY KEY, " +
                    "password VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100), " +
                    "fullname VARCHAR(100), " +
                    "createdat TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "lastlogin TIMESTAMP)");
                    
            // Create orders table
            stmt.execute("CREATE TABLE IF NOT EXISTS orders (" +
                    "orderid VARCHAR(50) PRIMARY KEY, " +
                    "customername VARCHAR(100) NOT NULL, " +
                    "restaurant VARCHAR(100) NOT NULL, " +
                    "amount DOUBLE PRECISION NOT NULL, " +
                    "status VARCHAR(50) NOT NULL, " +
                    "createdat TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "updatedat TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
                    
            // Create default admin user if not exists
            java.sql.ResultSet rs = stmt.executeQuery("SELECT count(*) FROM users WHERE username='admin'");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO users (username, password, email, fullname) " +
                             "VALUES ('admin', 'admin123', 'admin@fooddelivery.com', 'Administrator')");
            }
            rs.close();
            stmt.close();
            
            initialized = true;
        } catch (Exception e) {
            System.err.println("Failed to auto-initialize database tables: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        try {
            Class.forName(DRIVER_CLASS);
            Connection conn;
            if (DB_USER == null && DB_PASSWORD == null) {
                 conn = DriverManager.getConnection(DB_URL);
            } else {
                 conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
            
            // Auto-create tables if they don't exist (especially for Render PostgreSQL)
            initializeDatabase(conn);
            
            return conn;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database Driver not found! Expected: " + DRIVER_CLASS, e);
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