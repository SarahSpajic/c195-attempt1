package com.example.c195.controller;

import com.example.c195.DAO.AppointmentDaoImpl;
import com.example.c195.model.Appointment;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AppointmentDashboardController implements Initializable {
    @FXML
    private TableView<Appointment> monthTableView;
    @FXML
    private TableView<Appointment> weekTableView;
    @FXML
    private RadioButton monthRadioButton;
    @FXML
    private RadioButton weekRadioButton;
    @FXML
    private TabPane tabPane;

    private int appointmentID;
    private Connection connection;
    private ResourceBundle resources;
    private int userId;



    public void setConnection(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            setupTableViews();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        setupRadioButtons();
    }

    /** This method is used to retrieve the appointments by month and the appointments by week and pass it into the
     * populate table method, so that the appointments can be viewed by month or week.
     *
     **/

    private void setupTableViews() throws SQLException {
        populateTable(monthTableView, AppointmentDaoImpl.getMonthAppointmentsByUserId(connection, userId));
        populateTable(weekTableView, AppointmentDaoImpl.getWeekAppointmentsByUserId(connection, userId));
    }
    /**
    * This method sets up the action listener for the 'monthRadioButton'.
    * When the 'monthRadioButton' is selected, it changes the selected tab in the TabPane to the first tab (index 0). */
    private void setupRadioButtons() {
        monthRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                tabPane.getSelectionModel().select(0);
            }
        });

        weekRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                tabPane.getSelectionModel().select(1);
            }
        });
    }
    /**
     * This method sets up the appointments
     * in the main appointment table (shows all appointments)*/
    private void populateTable(TableView<Appointment> tableView, ObservableList<Appointment> appointments) {
        try {
            tableView.setItems(appointments);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
