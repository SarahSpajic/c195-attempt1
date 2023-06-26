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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class UpdateAppointmentController implements Initializable {

    @FXML
    private TextField appointmentIdField;
    private Appointment selectedAppointment;
    private String selectedTable;
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


    private Connection connection;
    private ResourceBundle resources;
    private AppointmentDaoImpl appointmentDao;
    private CustomerDaoImpl customerDao;
    private int userId;


    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setResources(ResourceBundle resources) {
        this.resources = resources;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    /** populates the contact choice box with all existing contacts
     */
    private void populateContactChoiceBox() {
        try {
            ObservableList<Contact> contacts = ContactDaoImpl.getAllContacts(connection);
            contactChoiceBox.setItems(contacts);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /** used to create a list of appointment times to select from in the combo box.
     */
    private void populateAppointmentTimes() {
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

    /** sets the updated appointment info based on the selected appointment and
     * the type of table (main, month, or week)
     */
    public void setAppointment(Appointment appointment, String sourceTable) {
        this.userId = userId;
        this.selectedAppointment = appointment;
        this.selectedTable = sourceTable;
        titleTextField.setText(appointment.getTitle());
        descriptionTextField.setText(appointment.getDescription());
        locationTextField.setText(appointment.getLocation());
        typeTextField.setText(appointment.getType());
        appointmentIdField.setText(String.valueOf(appointment.getAppointmentId()));
        addAppointmentStartDate.setValue(appointment.getStart().toLocalDate());
        addAppointmentEndDate.setValue(appointment.getEnd().toLocalDate());
        addAppointmentStartTime.setValue(appointment.getStart().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        addAppointmentEndTime.setValue(appointment.getEnd().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));

    }
    /**
     * This method handles the update appointment action.
     * It first retrieves the appointment details from the text fields.
     * It selects the appointment list based on the selected table and
     * replaces the old appointment with the updated one in the selected list.
     * It updates the appointment in the database and the dashboard.
     *
     * @param event  The action event.
     *
     * @throws IOException  If there is an issue loading the FXML resource.
     * @throws SQLException If there is an issue with any SQL operation such as
     *                      retrieving appointments or updating the appointment.
     */
    @FXML
    private void updateAppointmentAction(ActionEvent event) throws IOException, SQLException {
        int customerId = selectedAppointment.getCustomerId();
        int appointmentId = selectedAppointment.getAppointmentId();
        appointmentIdField.setText(String.valueOf(appointmentId));
        String title = titleTextField.getText();
        String description = descriptionTextField.getText();
        String location = locationTextField.getText();
        String type = typeTextField.getText();
        LocalDate startDate = addAppointmentStartDate.getValue();
        LocalDate endDate = addAppointmentEndDate.getValue();
        String startTimeStr = addAppointmentStartTime.getValue();
        String endTimeStr = addAppointmentEndTime.getValue();
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);
        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

        Contact selectedContact = contactChoiceBox.getSelectionModel().getSelectedItem();

        int contactId = selectedContact.getContactID();

        ObservableList<Appointment> appointments;

        switch (selectedTable) {
            case "appointmentTable":
                appointments = AppointmentDaoImpl.getAllAppointments(connection);
                break;
            case "monthAppointmentTable":
                appointments = AppointmentDaoImpl.getAllMonthAppointments(connection);
                break;
            case "weekAppointmentTable":
                appointments = AppointmentDaoImpl.getAllWeekAppointments(connection);
                break;
            default:
                throw new RuntimeException("Invalid table selection");
        }


        Appointment updatedAppointment = new Appointment(customerId, appointmentId, title, description, location, contactId, type, startDateTime, endDateTime, this.userId);
        if (appointmentDao == null) {
            appointmentDao = new AppointmentDaoImpl();
        }

        try {
            appointmentDao.updateAppointment(connection, updatedAppointment);

            Appointment prevAppointment = appointments.stream()
                    .filter(appointment -> appointment.getAppointmentId() == appointmentId)
                    .findFirst()
                    .orElse(null);

            appointments.remove(prevAppointment);
            appointments.add(updatedAppointment);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/c195/dashboard.fxml"));
            Parent root = loader.load();
            DashboardController dashboardController = loader.getController();
            dashboardController.populateTables();
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lambda function to filter a specific customer from a list of customers.
     * The function uses the customer ID for filtering.
    * A boolean value indicating whether the customer object's ID matches the provided ID.
     * @param url
     * @param resourceBundle
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUserId(this.userId);
        System.out.println(userId);
        this.resources = resourceBundle;
        try {
            this.connection = DBConnection.makeConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.appointmentDao = new AppointmentDaoImpl();

        try {
            ObservableList<Customer> customersObservableList = customerDao.getAllCustomers(connection);

            customerChoiceBox.setItems(customersObservableList);
            customerChoiceBox.setCellFactory((comboBox) -> {
                return new ListCell<Customer>() {
                    @Override
                    protected void updateItem(Customer customer, boolean empty) {
                        super.updateItem(customer, empty);
                        if (customer == null || empty) {
                            setText(null);
                        } else {
                            setText(customer.getName());
                        }
                    }
                };
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
            throw new RuntimeException(e);
        }
        populateAppointmentTimes();
        populateContactChoiceBox();
        populateContactChoiceBox();
    }
}
