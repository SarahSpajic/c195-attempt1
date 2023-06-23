package com.example.c195.controller;

import com.example.c195.DAO.CustomerDaoImpl;
import com.example.c195.DAO.DBConnection;
import com.example.c195.DAO.FirstLevelDivisionDaoImpl;
import com.example.c195.model.Country;
import com.example.c195.model.Customer;
import com.example.c195.model.FirstLevelDivision;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class CustomerController implements Initializable {

    private Customer selectedCustomer;
    private int selectedIndex;
    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private ComboBox<Country> countryComboBox;

    @FXML
    private ComboBox<FirstLevelDivision> stateComboBox;

    private int customerID;
    @FXML
    private TextField customerIdField;
    @FXML
    private int divisionID;
    @FXML
    private TextField nameField;

    @FXML
    private TextField addressField;

    @FXML
    private TextField postalCodeField;

    @FXML
    private TextField phoneNumberField;

    private Connection connection;
    private CustomerDaoImpl customerDao;
    private FirstLevelDivisionDaoImpl firstLevelDivisionDao;
    private ObservableList<Customer> customers = FXCollections.observableArrayList();

    public void setConnection(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            this.connection = DBConnection.makeConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        firstLevelDivisionDao = new FirstLevelDivisionDaoImpl();
        customerDao = new CustomerDaoImpl();
            countryComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                stateComboBox.setDisable(false);
                populateDivisions(String.valueOf(newValue));
            });
        countryComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            stateComboBox.setDisable(false);
        });
        populateCountries();
    }

    /**
     * This method populates the country drop down box
     */
    private void populateCountries() {
        try {
            ObservableList<Country> countries = firstLevelDivisionDao.getAllCountries();
            countryComboBox.setItems(countries);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * This method populates the division drop down box
     */
    private void populateDivisions(String countryName) {
        try {
            Country selectedCountry = countryComboBox.getSelectionModel().getSelectedItem();
            if(selectedCountry == null){
                return;
            }
            ObservableList<FirstLevelDivision> divisions = firstLevelDivisionDao.getDivisionsByCountryID(selectedCountry.getId());
            stateComboBox.setItems(divisions);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method adds a new customer to the database and the customer table
     */
    @FXML
    private void addCustomer(ActionEvent event) throws IOException {
        String name = nameField.getText();
        String address = addressField.getText();
        String postalCode = postalCodeField.getText();
        String phoneNumber = phoneNumberField.getText();
        FirstLevelDivision selectedDivision = stateComboBox.getValue();

        if (selectedDivision == null) {
            return;
        }

        Customer newCustomer = new Customer(customerID, name, address, postalCode, phoneNumber, divisionID);
        newCustomer.setDivisionID(selectedDivision.getCountryID());

        try {
            customerDao.addCustomer(connection, newCustomer);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/c195/dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();

            dashboardController.populateTables();

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setCustomer(Customer customer) {
        this.selectedCustomer = customer;
        nameField.setText(customer.getName());
        addressField.setText(customer.getAddress());
        phoneNumberField.setText(customer.getPhoneNumber());
        postalCodeField.setText(customer.getPostalCode());

        for (FirstLevelDivision division : stateComboBox.getItems()) {
            if (division.getDivisionID() == customer.getDivisionID()) {
                stateComboBox.setValue(division);
                break;
            }
        }

    }


    @FXML

    private void updateCustomerAction(ActionEvent event) throws IOException {
        String name = nameField.getText();
        String address = addressField.getText();
        String postalCode = postalCodeField.getText();
        String phoneNumber = phoneNumberField.getText();
        FirstLevelDivision selectedDivision = stateComboBox.getValue();
        int customerId = selectedCustomer.getCustomerID();

        if (selectedDivision == null) {
            return;
        }
        if (selectedCustomer == null) {
            return;
        }
        Customer updatedCustomer = new Customer(selectedCustomer.getCustomerID(), name, address, postalCode, phoneNumber, selectedDivision.getDivisionID());

        try {
            customerDao.updateCustomer(connection, updatedCustomer);
            Customer prevCustomer = customers.stream()
                    .filter(customer -> customer.getCustomerID() == selectedCustomer.getCustomerID())
                    .findFirst()
                    .orElse(null);
            if (prevCustomer != null) {
                customers.remove(prevCustomer);
            }
            customers = FXCollections.observableArrayList();
            customers.add(updatedCustomer);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/c195/dashboard.fxml"));
            Parent root = loader.load();
            DashboardController dashboardController = loader.getController();
            dashboardController.populateTables();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}


