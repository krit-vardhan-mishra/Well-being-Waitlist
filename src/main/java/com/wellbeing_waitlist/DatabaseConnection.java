package com.wellbeing_waitlist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

@Repository
public class DatabaseConnection {
    private static final String URL = "DB_URL";
    private static final String USER = "DB_USER";
    private static final String PASSWORD = "DB_PASSWORD";
    
    public static Connection getConnection() throws  SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
