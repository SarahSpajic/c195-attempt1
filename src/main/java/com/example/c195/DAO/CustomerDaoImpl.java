package com.example.c195.DAO;

import com.example.c195.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDaoImpl {

    /** This method returns a list of all customers */

    public static ObservableList<Customer> getAllCustomers(Connection connection) throws SQLException {
        ResultSet rs = null;
        try {
            String query = "SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, customers.Postal_Code, customers.Phone, customers.Division_ID \n" +
                    "FROM customers INNER JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID";

            PreparedStatement ps = connection.prepareStatement(query);
            rs = ps.executeQuery();

            ObservableList<Customer> customers = FXCollections.observableArrayList();

            while (rs.next()) {
                int customerID = rs.getInt("Customer_ID");
                String customerName = rs.getString("Customer_Name");
                String customerAddress = rs.getString("Address");
                String customerPostalCode = rs.getString("Postal_Code");
                String customerPhone = rs.getString("Phone");
                int divisionID = rs.getInt("Division_ID");
                Customer customer = new Customer(customerID, customerName, customerAddress, customerPostalCode, customerPhone, divisionID);
                customers.add(customer);
            }

            return customers;
        } finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    /** This method returns a list of all customers by a specified name, which is used to obtain a customer's report */
    public static ObservableList<Customer> getCustomerByName(Connection connection, String name) throws Exception {
        String query = "SELECT * FROM customers WHERE customer_name = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        ObservableList<Customer> customersObservableList = FXCollections.observableArrayList();

        while (rs.next()) {
            int customerID = rs.getInt("Customer_ID");
            String customerName = rs.getString("Customer_Name");
            String customerAddress = rs.getString("Address");
            String customerPostalCode = rs.getString("Postal_Code");
            String customerPhone = rs.getString("Phone");
            int divisionID = rs.getInt("Division_ID");
            Customer customer = new Customer(customerID, customerName, customerAddress, customerPostalCode, customerPhone, divisionID);
            customersObservableList.add(customer);
        }
        return customersObservableList;
    }

    /** This method adds a customer to the database based on the user's entered values */
    public static void addCustomer(Connection connection, Customer customer) throws SQLException {


        String query = "INSERT INTO customers(Customer_Name, Address, Postal_Code, Phone, Division_ID) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, customer.getName());
        ps.setString(2, customer.getAddress());
        ps.setString(3, customer.getPostalCode());
        ps.setString(4, customer.getPhoneNumber());
        ps.setInt(5, customer.getDivisionID());

        ps.executeUpdate();
    }

    /** This method updates a customer in the database based on the user's entered values */
    public void updateCustomer(Connection connection, Customer updatedCustomer) throws SQLException {
        String query = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ? WHERE Customer_ID = ?";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, updatedCustomer.getName());
        ps.setString(2, updatedCustomer.getAddress());
        ps.setString(3, updatedCustomer.getPostalCode());
        ps.setString(4, updatedCustomer.getPhoneNumber());
        ps.setInt(5, updatedCustomer.getDivisionID());
        ps.setInt(6, updatedCustomer.getCustomerID());

        ps.executeUpdate();
    }
    /** This method deletes a customer in the database and customer table */
    public static void deleteCustomer (Connection connection, int customerId) throws SQLException {
        String query = "DELETE FROM customers WHERE Customer_ID = ?";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, customerId);

        ps.executeUpdate();
    }


}
