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

    public static Connection getConnection() {
        try {
            Class.forName(DRIVER_CLASS);
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
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