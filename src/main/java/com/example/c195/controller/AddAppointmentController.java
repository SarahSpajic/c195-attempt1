package com.example.c195.controller;

import com.example.c195.DAO.AppointmentDaoImpl;
import com.example.c195.DAO.ContactDaoImpl;
import com.example.c195.DAO.CustomerDaoImpl;
import com.example.c195.DAO.DBConnection;
import com.example.c195.model.Appointment;
import com.example.c195.model.Contact;
import com.example.c195.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AddAppointmentController implements Initializable {

    @FXML
    private TextField appointmentIdField;
    private Appointment selectedAppointment;
    private Appointment appointment;
    @FXML
    private TextField titleTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField locationTextField;
    @FXML
    private ComboBox<Contact> contactChoiceBox;
    @FXML
    private ComboBox<Customer> customerChoiceBox;
    @FXML
    private TextField typeTextField;
    @FXML
    private DatePicker addAppointmentStartDate;
    @FXML
    private ComboBox<String> addAppointmentStartTime;
    @FXML
    private DatePicker addAppointmentEndDate;
    @FXML
    private ComboBox<String> addAppointmentEndTime;
    private ObservableList<Appointment> appointmentList;

    private Connection connection;
    private ResourceBundle resources;
    private AppointmentDaoImpl appointmentDao;
    private CustomerDaoImpl customerDao;


    private int userId;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /** */
    @FXML
    private void addAppointment(ActionEvent event) throws IOException {
        String title = titleTextField.getText();
        String description = descriptionTextField.getText();
        String location = locationTextField.getText();
        String type = typeTextField.getText();
        LocalDate selectedStartDate = addAppointmentStartDate.getValue();
        LocalDate selectedEndDate = addAppointmentEndDate.getValue();
        LocalTime startTime = LocalTime.parse(addAppointmentStartTime.getValue(), DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime endTime = LocalTime.parse(addAppointmentEndTime.getValue(), DateTimeFormatter.ofPattern("HH:mm"));

        LocalDateTime startDateTime = LocalDateTime.of(selectedStartDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(selectedEndDate, endTime);

        ZonedDateTime startZonedDateTime = startDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime endZonedDateTime = endDateTime.atZone(ZoneId.systemDefault());

        ZonedDateTime startDateTimeUTC = startZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endDateTimeUTC = endZonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));

        Contact selectedContact = contactChoiceBox.getSelectionModel().getSelectedItem();
        Customer selectedCustomer = customerChoiceBox.getSelectionModel().getSelectedItem();
        int customerId = selectedCustomer.getCustomerID();

        Appointment newAppointment = new Appointment(selectedCustomer.getCustomerID(), title, description, location, selectedContact.getContactID(), type, startDateTimeUTC.toLocalDateTime(), endDateTimeUTC.toLocalDateTime(), this.userId);

        try {
            AppointmentDaoImpl.addAppointment(connection, newAppointment);
            if (AppointmentDaoImpl.getCustomerAppointments(connection, customerId)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Appointment Alert");
                alert.setHeaderText("Overlapping Appointments");
                alert.setContentText("Customer " + selectedCustomer.getName() + " has an overlapping appointment");
                alert.showAndWait();
                return;
            }

                if (isWeekendAppointment(newAppointment)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Weekend Appointment");
                    alert.setContentText("Appointments cannot be booked on weekends.");
                    alert.showAndWait();
                    return;
                }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/c195/dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.populateTables();

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.close();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }

    private boolean isWeekendAppointment(Appointment appointment) {
        DayOfWeek dayOfWeek = appointment.getStart().getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }


    public void setAppointmentList (ObservableList < Appointment > appointmentList) {
            this.appointmentList = appointmentList;
        }

    /**
     * This method populates the choice box for all contacts. It connects to the database and retrieves the contact table via
     * getAllContacts method in the ContactDao and sets the items in the choice box to all of the contacts currently in the table.
     */
        private void populateContactChoiceBox () {
            try {
                ObservableList<Contact> contacts = ContactDaoImpl.getAllContacts(connection);
                contactChoiceBox.setItems(contacts);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    /**
     * This method populates the choice box for all customers.
     * It connects to the database and retrieves the contact table via
     * getAllCustomers method in the CustomerDao
     * and sets the items in the choice box to all the contacts currently in the table.
     */
        private void populateCustomerChoiceBox () {
            try {
                ObservableList<Customer> customers = CustomerDaoImpl.getAllCustomers(connection);
                customerChoiceBox.setItems(customers);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    /**
     * This method populates the list of appointment times.
     * It creates a list of 15-minute appointments from 08:00 to 22:00
     * and sets them into the appointment start and end time ComboBoxes.
     */
        private void populateAppointmentTimes () {
            ObservableList<String> appointmentTimes = FXCollections.observableArrayList();

            LocalTime firstAppointment = LocalTime.of(8, 0);
            LocalTime lastAppointment = LocalTime.of(22, 0);

            while (firstAppointment.isBefore(lastAppointment)) {
                appointmentTimes.add(firstAppointment.format(DateTimeFormatter.ofPattern("HH:mm")));
                firstAppointment = firstAppointment.plusMinutes(15);
            }

            addAppointmentStartTime.setItems(appointmentTimes);
            addAppointmentEndTime.setItems(appointmentTimes);
        }
    /**
     * This method sets the provided appointment details into the respective text fields
     *
     * @param appointment the Appointment object whose details are to be set into the text fields
     */

        public void setAppointment (Appointment appointment){
            this.selectedAppointment = appointment;
            this.userId = userId;
            titleTextField.setText(appointment.getTitle());
            descriptionTextField.setText(appointment.getDescription());
            locationTextField.setText(appointment.getLocation());
            typeTextField.setText(appointment.getType());

            appointmentIdField.setText(String.valueOf(appointment.getAppointmentId()));
        }

        /**
         * @param url
         * @param resourceBundle
         */
        @Override
        public void initialize (URL url, ResourceBundle resourceBundle){
            setUserId(this.userId);
            this.resources = resourceBundle;
            try {
                this.connection = DBConnection.makeConnection();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            try {
                ObservableList<Customer> customersObservableList = customerDao.getAllCustomers(connection);

                customerChoiceBox.setItems(customersObservableList);
                customerChoiceBox.setCellFactory((comboBox) -> new ListCell<>() {
                    @Override
                    protected void updateItem(Customer customer, boolean empty) {
                        super.updateItem(customer, empty);
                        if (customer == null || empty) {
                            setText(null);
                        } else {
                            setText(customer.getName());
                        }
                    }
                });

                customerChoiceBox.setConverter(new StringConverter<Customer>() {
                    @Override
                    public String toString(Customer customer) {
                        if (customer == null) {
                            return null;
                        } else {
                            return customer.getName();
                        }
                    }

                    @Override
                    public Customer fromString(String customerId) {
                        return customersObservableList.stream()
                                .filter(customer -> customer.getCustomerID() == Integer.parseInt(customerId))
                                .findFirst()
                                .orElse(null);
                    }
                });

            } catch (SQLException e) {
                e.printStackTrace();
            }

            populateAppointmentTimes();
            populateContactChoiceBox();
            populateCustomerChoiceBox();


        }
    }