package com.example.c195.controller;

import com.example.c195.DAO.DBConnection;
import com.example.c195.model.UserActivityTracker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.example.c195.DAO.UserDaoImpl.validateUser;

public class SignInController {
    @FXML private TextField userName;
    @FXML private TextField password;
    @FXML private Label timeZoneLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label loginLabel;
    @FXML private Button loginButton;

    private ResourceBundle bundle;

    private Connection connection;

    @FXML
    void initialize() throws Exception {

        this.connection = DBConnection.makeConnection();
        ZoneId zoneId = ZoneId.systemDefault();
        timeZoneLabel.setText("Your current timezone: " + zoneId);
        updateLabels();
    }

    /** This method updates the language based on the user's computer settings */
    private void updateLabels() {
        Locale locale = Locale.getDefault();
        bundle = ResourceBundle.getBundle("messages", locale);
        usernameLabel.setText(bundle.getString("username"));
        passwordLabel.setText(bundle.getString("password"));
        loginLabel.setText(bundle.getString("login"));
        loginButton.setText(bundle.getString("login"));
    }

    /** This method checks the user id and password  and allows the user to log in
     * when the validateUser method returns an id greater than 0.
     * */
    @FXML
    void signIn(ActionEvent event) throws IOException {
        String usernameInput = userName.getText();
        String passwordInput = password.getText();

        int userId = validateUser(usernameInput, passwordInput);
        if (userId > 0) {
            try {
                boolean success = true;
                UserActivityTracker.userActivity(usernameInput, success);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/c195/dashboard.fxml"));
                Parent root = loader.load();

                DashboardController dashboardController = loader.getController();
                dashboardController.setConnection(connection);
                dashboardController.setUserId(userId);
                dashboardController.populateTables();

                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            boolean success = false;
            UserActivityTracker.userActivity(usernameInput, success);
            ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());
            String errorText = bundle.getString("error");
            Alert alert = new Alert(Alert.AlertType.ERROR, errorText);
            alert.showAndWait();
        }
    }
}
