package com.example.c195.controller;

import com.example.c195.DAO.AppointmentDaoImpl;
import com.example.c195.DAO.CustomerDaoImpl;
import com.example.c195.DAO.DBConnection;
import com.example.c195.model.Appointment;
import com.example.c195.model.Customer;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    private TableColumn<Appointment, Integer> contactIdColumn;
    @FXML
    private TextField appointmentIdTextField;
    @FXML
    private TableColumn<Appointment, Integer> monthAppointmentIDColumn;
    @FXML
    private TableColumn<Appointment, String> monthAppointmentTitleColumn;
    @FXML
    private TableColumn<Appointment, String> monthAppointmentDescriptionColumn;
    @FXML
    private TableColumn<Appointment, String> monthAppointmentLocationColumn;
    @FXML
    private TableColumn<Appointment, String> monthAppointmentContactColumn;
    @FXML
    private TableColumn<Appointment, String> monthAppointmentTypeColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> monthAppointmentStartColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> monthAppointmentEndColumn;
    @FXML
    private TableColumn<Appointment, Integer> monthAppointmentCustomerIDColumn;
    @FXML
    private TableColumn<Appointment, Integer> monthAppointmentUserIDColumn;

    @FXML
    private TableColumn<Appointment, Integer> weekAppointmentIDColumn;
    @FXML
    private TableColumn<Appointment, String> weekAppointmentTitleColumn;
    @FXML
    private TableColumn<Appointment, String> weekAppointmentDescriptionColumn;
    @FXML
    private TableColumn<Appointment, String> weekAppointmentLocationColumn;
    @FXML
    private TableColumn<Appointment, String> weekAppointmentContactColumn;
    @FXML
    private TableColumn<Appointment, String> weekAppointmentTypeColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> weekAppointmentStartColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> weekAppointmentEndColumn;
    @FXML
    private TableColumn<Appointment, Integer>   weekAppointmentCustomerIDColumn;
    @FXML
    private TableColumn<Appointment, Integer> weekAppointmentUserIDColumn;

    @FXML private TableView<Customer> customerTable;

    private Connection connection;
    private int userId;

    @FXML
    private TableColumn<Customer, Integer> customerIDColumn;
    @FXML
    private TableColumn<Customer, Integer> associatedCustomerIDColumn;
    @FXML
    private TableColumn<Customer, String> nameColumn;

    @FXML
    private TableColumn<Customer, String> addressColumn;

    @FXML
    private TableColumn<Customer, String> postalCodeColumn;

    @FXML
    private TableColumn<Customer, String> phoneNumberColumn;
    @FXML
    private TableColumn<Customer, String> divisionColumn;
    @FXML
    private TableView<Appointment> appointmentTable;

    @FXML
    private TableView<Appointment> weekAppointmentTable;
    @FXML
    private TableView<Appointment> monthAppointmentTable;

    @FXML
    private TableColumn<Appointment, Integer> appointmentUserIDColumn;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIDColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentTitleColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentTypeColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentDescriptionColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentLocationColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentContactColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentStartColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentEndColumn;
    @FXML
    private TableView<Customer> customerTableView;



    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /** The method populateTables uses all the separate populate table methods
     * to populate the dashboard customer and apppointment tables
     */
    public void populateTables() {
        populateCustomerTable();
        populateAppointmentTable();
        populateMonthAppointmentTable();
        populateWeekAppointmentTable();
    }
    public void exit() {System.exit(0);}


    /**
     * This method checks for upcoming appointments within the next 15 minutes.
     * If an appointment is found, creates an alert for the user.
     *
     * @throws SQLException if there is an error accessing the database
     */
    public void checkForUpcomingAppointments() throws SQLException {
        ObservableList<Appointment> appointments = AppointmentDaoImpl.getAllAppointments(connection);
        ZonedDateTime current = ZonedDateTime.now(ZoneId.systemDefault());

        for (Appointment appointment : appointments) {

            ZonedDateTime start = appointment.getStart().atZone(ZoneId.systemDefault());

            Duration duration = Duration.between(current, start);

            if (duration.toMinutes() >= 0 && duration.toMinutes() <= 15) {
                createAlert(appointment);
            }
        }
    }

    /** creates the alert for the checkForUpcomingAppointments method*/
    private void createAlert(Appointment appointment) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Appointment Alert");
        alert.setHeaderText("Upcoming Appointment");
        alert.setContentText("Customer " + appointment.getCustomerId()
                + " has an appointment starting soon at " + appointment.getStart().toString());
        alert.showAndWait();
    }
    public LocalDateTime toLocalTimeZone(LocalDateTime dateTimeInUTC) {
        ZonedDateTime zdt = dateTimeInUTC.atZone(ZoneId.of("UTC"));
        ZonedDateTime localZdt = zdt.withZoneSameInstant(ZoneId.systemDefault());
        return localZdt.toLocalDateTime();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            this.connection = DBConnection.makeConnection();

            //customer table
            customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerID"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
            divisionColumn.setCellValueFactory(new PropertyValueFactory<>("divisionID"));
            postalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
            phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
            //appointments table
            appointmentUserIDColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
            associatedCustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            appointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            appointmentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            appointmentLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
            appointmentContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactId"));
            appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
            appointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
            appointmentEndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
            appointmentContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactId"));
            //Month View
            monthAppointmentCustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            monthAppointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            monthAppointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            monthAppointmentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            monthAppointmentLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
            monthAppointmentContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactId"));
            monthAppointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
            monthAppointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
            monthAppointmentEndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
            monthAppointmentUserIDColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
            //Week View
            weekAppointmentCustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            weekAppointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            weekAppointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            weekAppointmentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            weekAppointmentLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
            weekAppointmentContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactId"));
            weekAppointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
            weekAppointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
            weekAppointmentEndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));
            weekAppointmentUserIDColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));

            contactIdColumn = new TableColumn<>("Contact ID");
            contactIdColumn.setCellValueFactory(new PropertyValueFactory<>("contactId"));
            weekAppointmentTable.getColumns().add(contactIdColumn);
            monthAppointmentTable.getColumns().add(contactIdColumn);

            checkForUpcomingAppointments();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserId(int userId) {
        this.userId = userId;
        populateTables();
        populateMonthAppointmentTable();
        populateWeekAppointmentTable();
    }
    /** populates the customer table */
    private void populateCustomerTable() {
        try {
            ObservableList<Customer> customers = CustomerDaoImpl.getAllCustomers(connection);
            customerTable.setItems(customers);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    /** opens the add customer screen
     * @throws IOException
     * */
    @FXML
    void addNewCustomer(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        Object scene = FXMLLoader.load(getClass().getResource("/com/example/c195/add-customer.fxml"));
        stage.setScene(new Scene((Parent) scene));
        stage.show();
    }
    /** opens the add appointment screen
     * @throws IOException
     * */
    @FXML
    private void scheduleAppointment() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/c195/add-appointment.fxml"));
        Parent root = loader.load();
        AddAppointmentController appointmentController = loader.getController();
        appointmentController.setConnection(connection);
        appointmentController.setUserId(this.userId);
        appointmentController.setAppointmentList(appointmentTable.getItems());
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.showAndWait();
        populateAppointmentTable();
    }

    /** populates the main appointment table with all of the appointments
     * */
    private void populateAppointmentTable() {
        try {
            ObservableList<Appointment> appointments = AppointmentDaoImpl.getAllAppointments(connection);
            for (Appointment appointment : appointments) {
                LocalDateTime localStart = toLocalTimeZone(appointment.getStart());
                LocalDateTime localEnd = toLocalTimeZone(appointment.getEnd());

                appointment.setStart(localStart);
                appointment.setEnd(localEnd);
            }
            appointmentTable.setItems(appointments);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** populates the week appointment table
     */
    private void populateWeekAppointmentTable() {
        try {

            ObservableList<Appointment> appointments = AppointmentDaoImpl.getAllWeekAppointments(connection);
            for (Appointment appointment : appointments) {
                LocalDateTime localStart = toLocalTimeZone(appointment.getStart());
                LocalDateTime localEnd = toLocalTimeZone(appointment.getEnd());

                appointment.setStart(localStart);
                appointment.setEnd(localEnd);
            }
            weekAppointmentTable.setItems(appointments);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** populates the month appointment table
     */
    private void populateMonthAppointmentTable() {
        try {

            ObservableList<Appointment> appointments = AppointmentDaoImpl.getAllMonthAppointments(connection);
            for (Appointment appointment : appointments) {
                LocalDateTime localStart = toLocalTimeZone(appointment.getStart());
                LocalDateTime localEnd = toLocalTimeZone(appointment.getEnd());

                appointment.setStart(localStart);
                appointment.setEnd(localEnd);
            }
            monthAppointmentTable.setItems(appointments);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** removes the selected customer from the database.
     * @throws SQLException
     */
    @FXML
    private void deleteCustomer() throws SQLException {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Customer Selected");
            alert.setContentText("Please select a customer to delete.");
            alert.showAndWait();
            return;
        }

        ObservableList<Appointment> appointments = AppointmentDaoImpl.getAllAppointmentsByCustomer(connection, selectedCustomer.getCustomerID());
        if (appointments != null && !appointments.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Customer Has Existing Appointments");
            alert.setContentText("Cannot delete a customer who has existing appointments. Please delete the appointments first.");
            alert.showAndWait();
            return;
        }
        try {
            connection.setAutoCommit(false);
            System.out.println(selectedCustomer.getCustomerID());
            CustomerDaoImpl.deleteCustomer(connection, selectedCustomer.getCustomerID());
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        populateCustomerTable();
    }

    /** this method gets the selected customer. If no customer is selected an error message is printed
     * If a customer is selected, the update customer screen opens.
     * @throws IOException
     */
    @FXML
    private void updateCustomer() throws IOException {
        Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/c195/update-customer.fxml"));
            Parent parent = loader.load();
            CustomerController updateCustomerController = loader.getController();
            updateCustomerController.setCustomer(selectedCustomer);
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.show();
        } else {
            System.out.println("No customer selected");
        }
    }

    /** removes the selected appointment from the database. It checks that at least one type of appointment has been selected.
     * If no appointment is selected, the user is presented with an alert.
     */
    public void deleteAppointment() {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        Appointment monthSelectedAppointment = monthAppointmentTable.getSelectionModel().getSelectedItem();
        Appointment weekSelectedAppointment = weekAppointmentTable.getSelectionModel().getSelectedItem();

        int appointmentId = -1;
        if (selectedAppointment != null) {
            appointmentId = selectedAppointment.getAppointmentId();
        } else if (monthSelectedAppointment != null) {
            appointmentId = monthSelectedAppointment.getAppointmentId();
        } else if (weekSelectedAppointment != null) {
            appointmentId = weekSelectedAppointment.getAppointmentId();
        }

        if (appointmentId == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Appointment Selected");
            alert.setContentText("Please select an appointment to delete.");
            alert.showAndWait();
            return;
        }

        try {
            connection.setAutoCommit(false);

            AppointmentDaoImpl.deleteAppointment(connection, appointmentId);

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        populateAppointmentTable();
        populateMonthAppointmentTable();
        populateWeekAppointmentTable();

    }
    /** this method opens the update appointment screen if an appointment has been selected
     * to update. It prints an error message if no appointment has been selected.
     */
    @FXML
    private void updateAppointment() throws IOException {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        Appointment monthselectedAppointment = monthAppointmentTable.getSelectionModel().getSelectedItem();
        Appointment weekSelectedAppointment = weekAppointmentTable.getSelectionModel().getSelectedItem();

        Appointment toUpdate = null;
        String sourceTable = "";

        if (selectedAppointment != null) {
            toUpdate = selectedAppointment;
            sourceTable = "appointmentTable";
        } else if (monthselectedAppointment != null) {
            toUpdate = monthselectedAppointment;
            sourceTable = "monthAppointmentTable";
        } else if (weekSelectedAppointment != null) {
            toUpdate = weekSelectedAppointment;
            sourceTable = "weekAppointmentTable";
        }

        if (toUpdate != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/c195/update-appointment.fxml"));
            Parent parent = loader.load();
            UpdateAppointmentController updateAppointmentController = loader.getController();
            updateAppointmentController.setAppointment(toUpdate, sourceTable);
            updateAppointmentController.setUserId(this.userId);
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.show();
        } else {
            System.out.println("No appointment selected");

        }
    }
    /** opens the customer report screen*/
    @FXML
    private void getCustomerReports() throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/c195/customer-appointment-report.fxml"));
            Parent parent = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.show();

    }
    /** opens the contact report screen*/
    @FXML
    private void getContactReports() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/c195/contact-report.fxml"));
        Parent parent = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.show();

    }

    /** opens the duplicate customer report screen*/
    @FXML
    private void getDuplicateCustomers() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/c195/duplicate-report.fxml"));
        Parent parent = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.show();

    }


}
