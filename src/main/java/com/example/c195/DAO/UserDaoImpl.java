package com.example.c195.DAO;

import com.example.c195.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoImpl extends User {

    public UserDaoImpl(int userID, String user, String password) {
        super(userID, user, password);
    }

    /** This method checks if the user's entered values match the username and password as it exists in the database. */
    public static int validateUser(String username, String password) {
        try {
            String sqlQuery = "SELECT * FROM users WHERE user_name = ? AND password = ?";
            PreparedStatement ps = DBConnection.makeConnection().prepareStatement(sqlQuery);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("User_ID");
                return userId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return -1;
    }
    /** This method returns a boolean that is used in the user activity tracker to show successful and failed log in attempts */
    public static boolean validateLogin(String username, String password) {
        int userId = validateUser(username, password);
        if (userId == -1) {
            return false; // user not found or password incorrect
        } else {
            return true; // user found and password matches
        }
    }
}
