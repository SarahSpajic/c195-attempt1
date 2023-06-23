        package com.example.c195.controller;

        import com.example.c195.DAO.AppointmentDaoImpl;
        import com.example.c195.DAO.CustomerDaoImpl;
        import com.example.c195.DAO.DBConnection;
        import com.example.c195.model.Appointment;
        import com.example.c195.model.Customer;
        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import javafx.fxml.FXML;
        import javafx.fxml.Initializable;
        import javafx.scene.control.ComboBox;
        import javafx.scene.control.Label;
        import javafx.scene.control.TextField;

        import java.net.URL;
        import java.sql.Connection;
        import java.time.LocalDate;
        import java.time.Month;
        import java.util.ResourceBundle;

        public class CustomerReportsController implements Initializable {

            private Connection connection;
            @FXML
            private ComboBox<Customer> customerChoiceBox;
            @FXML
            private ComboBox<String> monthField;

            @FXML
            private TextField appointmentTypeTextField;

            @FXML
            private Label totalCountLabel;

            public void setConnection(Connection connection) {
                this.connection = connection;
            }


            /** Populates the month field with the names of all months in a year
             * and sets the current month as the default value
             */
            private void populateMonthField() {
                ObservableList<String> months = FXCollections.observableArrayList(
                        Month.JANUARY.name(), Month.FEBRUARY.name(), Month.MARCH.name(), Month.APRIL.name(), Month.MAY.name(),
                        Month.JUNE.name(), Month.JULY.name(), Month.AUGUST.name(), Month.SEPTEMBER.name(), Month.OCTOBER.name(),
                        Month.NOVEMBER.name(), Month.DECEMBER.name()
                );
                monthField.setItems(months);
                monthField.setValue(LocalDate.now().getMonth().name());

            }
            /** The method generateReport gets the values the user inputs for month,
             * the selected customer, and the text entered into the appointment type field.
             * The customer id is used to get the associated appointments for the customer.
             * This also sets the text for the total count based on the number of
             * appointments returned.
             */
            @FXML
            public void generateReport() {
                String selectedMonth = monthField.getSelectionModel().getSelectedItem();
                Month month = Month.valueOf(selectedMonth.toUpperCase());
                Customer selectedCustomer = customerChoiceBox.getSelectionModel().getSelectedItem();
                String enteredAppointmentType = appointmentTypeTextField.getText();
                int customerId = selectedCustomer.getCustomerID();

                try {
                    ObservableList<Appointment> data = fetchCustomerAppointmentReportData(customerId, month, enteredAppointmentType);

                    int totalAppointments = data.size();
                    totalCountLabel.setText(String.valueOf(totalAppointments));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


             /**  This method gets all customers from the customers table and populates
             * The customer choice box.
             * */
            private void populateCustomerChoiceBox () {
                try {
                    ObservableList<Customer> customers = CustomerDaoImpl.getAllCustomers(connection);
                    customerChoiceBox.setItems(customers);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            private ObservableList<Appointment> fetchCustomerAppointmentReportData(int customerId, Month month, String appointmentType) throws Exception {

                ObservableList<Appointment> data = FXCollections.observableArrayList();

                Connection connection = DBConnection.makeConnection();

                ObservableList<Appointment> appointments = AppointmentDaoImpl.getAllMonthAppointmentsByCustomer(connection, customerId, month, appointmentType);
                data.addAll(appointments);

                return data;
            }

            @Override
            public void initialize(URL url, ResourceBundle resourceBundle) {

                try {
                    this.connection = DBConnection.makeConnection();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                customerChoiceBox.getItems();
                populateCustomerChoiceBox();
                populateMonthField();
            }



        }