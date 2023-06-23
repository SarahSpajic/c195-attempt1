package com.example.c195.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import static com.example.c195.DAO.DBConnection.password;
import static com.example.c195.DAO.DBConnection.username;
import static com.example.c195.DAO.UserDaoImpl.validateLogin;


public class UserActivityTracker {
        private static final String LOG_FILE_PATH = "login_activity.txt";
        boolean success = validateLogin(username, password);
        /** prints the log in activity to a text file */
        public static void userActivity(String username, boolean success) throws IOException {
            try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE_PATH, true))) {
                String timestamp = LocalDateTime.now().toString();
                String status = success ? "Success" : "Failure";
                writer.println("[" + timestamp + "] User: " + username + ", Status: " + status);
            }

        }


}

