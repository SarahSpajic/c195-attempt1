package com.example.c195.controller;

import com.example.c195.DAO.CustomerDaoImpl;
import com.example.c195.DAO.DBConnection;
import com.example.c195.model.Customer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class DuplicateReportsController implements Initializable {

    private Connection connection;
    @FXML
    private ComboBox<Customer> customerChoiceBox;

    @FXML private TableView<Customer> customerDuplicateTable;
    @FXML private TableColumn<Customer, String> name;
    @FXML private TableColumn<Customer, String> phoneNumber;
    @FXML private TableColumn<Customer, Integer> id;

    @FXML private TableColumn<Customer, String> address;
    @FXML private TableColumn<Customer, String> postal_code;

    @FXML
    private Label totalCountLabel;


    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    /**
     * Generates a report of customers with the same name.
     * It fetches the data, updates the total count label and populates the table.
     *
     * @throws Exception if an error occurs while fetching the data or updating the view
     */
    @FXML
    public void generateReport() throws Exception {
        try {
            ObservableList<Customer> data = fetchCustomerDuplicateReportData();

            int totalDuplicates = data.size();
            totalCountLabel.setText(String.valueOf(totalDuplicates));

            populateCustomerTable(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Gets the duplicate customers from the database.
     * It connects to the database, gets the selected customer that the user selects from the choice box, gets the customer name, and creates a
     * list of customers that match the customer name the user has selected.
     *
     */
    private ObservableList<Customer> fetchCustomerDuplicateReportData() throws Exception {
        Connection connection = DBConnection.makeConnection();
        Customer selectedCustomer = customerChoiceBox.getSelectionModel().getSelectedItem();
        String customerName = selectedCustomer.getName();

        ObservableList<Customer> customers =  CustomerDaoImpl.getCustomerByName(connection, customerName);
        return customers;
    }
    /**
     * This method updates the customer table inside the duplicate customers screen based on the data retrieved from the database.
     *
     */
    private void populateCustomerTable(ObservableList<Customer> customers) {
        customerDuplicateTable.setItems(customers);

    }
    /**
     * This method connects to the customer database, get a list of all the customers using the getAllCustomers method
     * the customer property is set based on the selected customer from the choice box
     *
     *
     */
    private void populateCustomerChoiceBox () {
        try {
            ObservableList<Customer> customers = CustomerDaoImpl.getAllCustomers(connection);
            customerChoiceBox.setItems(customers);
            customerChoiceBox.setCellFactory((comboBox) -> {
                return new ListCell<Customer>() {
                    @Override
                    protected void updateItem(Customer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                };
            });

            customerChoiceBox.setButtonCell(new ListCell<Customer>() {
                @Override
                protected void updateItem(Customer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.connection = DBConnection.makeConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        postal_code.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        address.setCellValueFactory(new PropertyValueFactory<>("address"));
        id.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        populateCustomerChoiceBox();
    }
}
