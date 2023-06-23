package com.example.c195.controller;

import com.example.c195.DAO.AppointmentDaoImpl;
import com.example.c195.DAO.ContactDaoImpl;
import com.example.c195.DAO.DBConnection;
import com.example.c195.model.Appointment;
import com.example.c195.model.Contact;
import com.example.c195.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

        public class ContactReportsController implements Initializable {

            private Connection connection;
            @FXML
            private ComboBox<Contact> contactChoiceBox;

            @FXML
            private TableView<Appointment> contactAppointmentTable;

            @FXML
            private Label totalCountLabel;
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
            private TableColumn<Customer, Integer> associatedCustomerIDColumn;

            public void setConnection(Connection connection) {
                this.connection = connection;
            }

            /**
             * Generates a report of contacts and their associated appointments and creates a total based on
             * the selected contact's appointments.
             *
             */
            @FXML
            public void generateReport() {

                Contact selectedContact = contactChoiceBox.getSelectionModel().getSelectedItem();


                int contactId = selectedContact.getContactID();

                try {
                    ObservableList<Appointment> data = fetchCustomerAppointmentReportData(contactId);

                    int totalAppointments = data.size();
                    totalCountLabel.setText(String.valueOf(totalAppointments));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                populateContactAppointmentTable();
            }

            /**
             * Gets the appointment data from the Appointment table
             * using the getContactAppointmentsById method in the AppointmentDao
             * @param contactId is passed to this method cased on the contact id selected from the combobox
             */

            private ObservableList<Appointment> fetchCustomerAppointmentReportData(int contactId) throws Exception {

                ObservableList<Appointment> data = FXCollections.observableArrayList();

                Connection connection = DBConnection.makeConnection();

                ObservableList<Appointment> appointments = AppointmentDaoImpl.getContactAppointmentsById(connection, contactId);
                data.addAll(appointments);
                return data;
            }


            /**
             * Populates the contact choice box by getting all the contacts from the contact table via the getAllContact method.
             */

            private void populateContactChoiceBox () {
                try {
                    ObservableList<Contact> contacts = ContactDaoImpl.getAllContacts(connection);
                    contactChoiceBox.setItems(contacts);
                    System.out.println(contacts);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            /**
             * Generates a report of contacts and heir associated appointments and creates a total based on
             * the selected contact's appointments.
             *
             */
            public LocalDateTime toLocalTimeZone(LocalDateTime dateTimeInUTC) {
                ZonedDateTime zdt = dateTimeInUTC.atZone(ZoneId.of("UTC"));
                ZonedDateTime localZdt = zdt.withZoneSameInstant(ZoneId.systemDefault());
                return localZdt.toLocalDateTime();
            }

            /**
             * Generates a report of contacts and heir associated appointments and creates a total based on
             * the selected contact's appointments.
             *
             */
            private void populateContactAppointmentTable() {
                try {

                    ObservableList<Appointment> appointments = AppointmentDaoImpl.getAllWeekAppointments(connection);
                    for (Appointment appointment : appointments) {
                        LocalDateTime localStart = toLocalTimeZone(appointment.getStart());
                        LocalDateTime localEnd = toLocalTimeZone(appointment.getEnd());

                        appointment.setStart(localStart);
                        appointment.setEnd(localEnd);
                    }
                    contactAppointmentTable.setItems(appointments);
                } catch (Exception e) {
                    e.printStackTrace();
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
                contactChoiceBox.getItems();
                populateContactChoiceBox();

            }

            }
