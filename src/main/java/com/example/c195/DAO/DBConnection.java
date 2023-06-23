package com.example.c195.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {
    private static final String DB_URL="jdbc:mysql://localhost:3306/client_schedule";
    public static final String username="sqlUser";
    public static final String password="Passw0rd!";
    static Connection conn;
    public static Connection makeConnection() throws ClassNotFoundException, SQLException, Exception{
        conn= DriverManager.getConnection(DB_URL,username,password);
        return conn;
    }
    public static void closeConnection() throws ClassNotFoundException,SQLException, Exception{
        conn.close();
    }



}
