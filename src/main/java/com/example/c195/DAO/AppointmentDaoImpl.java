package com.example.c195.DAO;

import com.example.c195.model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class AppointmentDaoImpl {
    /**
     * This method gets all appointments from the appointment table.
     * the database query finds appointments for the month identified.
     * This is used to populate the month tables
     * @param userId defines the user id # to find the associated appointments
     */
    public static ObservableList<Appointment> getMonthAppointmentsByUserId(Connection connection, int userId) throws SQLException {
        String query = "SELECT * FROM appointments WHERE MONTH(start) = MONTH(CURRENT_DATE()) AND YEAR(start) = YEAR(CURRENT_DATE())";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        return getAppointmentsFromResultSet(rs);
    }
    /**
     * This method gets all appointments from the appointment table.
     * the database query finds appointments for the week identified.
     * This is used to populate the week tables
     * @param userId defines the user id # to find the associated appointments
     */
    public static ObservableList<Appointment> getWeekAppointmentsByUserId(Connection connection, int userId) throws SQLException {
        String query = "SELECT * FROM appointments WHERE WEEK(start) = WEEK(CURRENT_DATE()) AND YEAR(start) = YEAR(CURRENT_DATE());";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        return getAppointmentsFromResultSet(rs);
    }
    /**
     * This method creates a new list of appointments, which is used to
     * populate the month and week tables and get the table of appointments based on a contact id.
     */
    private static ObservableList<Appointment> getAppointmentsFromResultSet(ResultSet rs) throws SQLException {
        ObservableList<Appointment> appointmentObservableList = FXCollections.observableArrayList();
        ZoneId utcZone = ZoneId.of("UTC");
        ZoneId localZone = ZoneId.systemDefault();

        while (rs.next()) {
            int customerId = rs.getInt("customer_id");
            int appointmentId = rs.getInt("appointment_id");
            String appointmentTitle = rs.getString("title");
            String appointmentDescription = rs.getString("description");
            String appointmentLocation = rs.getString("location");
            String appointmentType = rs.getString("type");
            int appointmentContact = rs.getInt("contact_id");
            LocalDateTime appointmentStartUTC = rs.getObject("start", LocalDateTime.class);
            LocalDateTime appointmentEndUTC = rs.getObject("end", LocalDateTime.class);

            ZonedDateTime startDateTimeUTC = appointmentStartUTC.atZone(utcZone);
            ZonedDateTime endDateTimeUTC = appointmentEndUTC.atZone(utcZone);

            ZonedDateTime startDateTimeLocal = startDateTimeUTC.withZoneSameInstant(localZone);
            ZonedDateTime endDateTimeLocal = endDateTimeUTC.withZoneSameInstant(localZone);

            LocalDateTime appointmentStart = startDateTimeLocal.toLocalDateTime();
            LocalDateTime appointmentEnd = endDateTimeLocal.toLocalDateTime();

            int userId = rs.getInt("user_id");

            Appointment appointment = new Appointment(customerId, appointmentId, appointmentTitle, appointmentDescription, appointmentLocation, appointmentContact, appointmentType, appointmentStart, appointmentEnd, userId);

            appointmentObservableList.add(appointment);
        }

        return appointmentObservableList;
    }

    /**
     * This method returns all appointments, which is used to populate the appointment table in the dashboard.
     */

    public static ObservableList<Appointment> getAllAppointments(Connection connection) throws SQLException {
        String query = "SELECT customer_id, appointment_id, title, description, location, type, contact_id, start, end, user_id FROM appointments;";

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        ObservableList<Appointment> appointmentObservableList = FXCollections.observableArrayList();

        ZoneId utcZone = ZoneId.of("UTC");
        ZoneId localZone = ZoneId.systemDefault();

        while (rs.next()) {
            int customerId = rs.getInt("customer_id");
            int appointmentId = rs.getInt("appointment_id");
            String appointmentTitle = rs.getString("title");
            String appointmentDescription = rs.getString("description");
            String appointmentLocation = rs.getString("location");
            String appointmentType = rs.getString("type");
            int appointmentContact = rs.getInt("contact_id");
            LocalDateTime appointmentStartUTC = rs.getObject("start", LocalDateTime.class);
            LocalDateTime appointmentEndUTC = rs.getObject("end", LocalDateTime.class);

            ZonedDateTime startDateTime = appointmentStartUTC.atZone(utcZone).withZoneSameInstant(localZone);
            ZonedDateTime endDateTime = appointmentEndUTC.atZone(utcZone).withZoneSameInstant(localZone);

            int userId = rs.getInt("user_id");

            Appointment appointment = new Appointment(customerId, appointmentId, appointmentTitle, appointmentDescription, appointmentLocation, appointmentContact, appointmentType, startDateTime.toLocalDateTime(), endDateTime.toLocalDateTime(), userId);
            appointmentObservableList.add(appointment);
        }

        return appointmentObservableList;
    }


    /**
     * This method returns appointments based on a customer id.This is used inside the appointment dao to
     * locate overlapping appointments for customers.
     * While there are existing appointments for the customer, the list will continue to populate
     * if an appointment starts before another appointment ends, an error is printed.
     * this is used in the AddAppointmentController to generate an alert when the usr has overlapping appointments
     * @param customerId is used to identify the customer's appointment(s) being searched.
     */
    public static Pair<List<Appointment>, Boolean> getCustomerAppointments(Connection connection, int customerId) throws SQLException {
        String query = "SELECT start, end, user_id FROM appointments WHERE customer_id = ? ORDER BY start";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();

        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        LocalDateTime lastAppointmentEnd = null;
        boolean hasOverlap = false;

        while (rs.next()) {
            LocalDateTime start = rs.getObject("start", LocalDateTime.class);
            LocalDateTime end = rs.getObject("end", LocalDateTime.class);
            int userId = rs.getInt("user_id");

            if (lastAppointmentEnd != null && start.isBefore(lastAppointmentEnd)) {
                System.out.println("There is an overlap with an existing appointment.");
                hasOverlap = true; // Set the flag to true if there is an overlap
            } else {
                Appointment appointment = new Appointment(customerId, start, end, userId);
                appointments.add(appointment);
                lastAppointmentEnd = end;
            }
        }

        return new Pair<>(appointments, hasOverlap); // Return the appointment list and the flag indicating whether there are overlapping appointments
    }

    /** This method is used to retrieve all the contacts from the contact table
     * and returns a new set of appointments (via the getAppointmentsFromResultSet method)
     *
     */
    public static ObservableList<Appointment> getContactAppointmentsById(Connection connection, int contactId) throws SQLException {
        String query = "SELECT * FROM appointments WHERE contact_id=?";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, contactId);
        ResultSet rs = ps.executeQuery();
        return getAppointmentsFromResultSet(rs);


    }

    /** This method is used to add appointments to the appointment table
     * and returns a new set of appointments (via the getAppointmentsFromResultSet method)
     * since the database generates the appointment id, the RETURN_GENERATED_KEYS is used to display the new
     * appointment id when the user returns to the dashboard.
     */
    public static int addAppointment (Connection connection, Appointment appointment) throws SQLException {

            String query = "INSERT INTO appointments (customer_id, title, description, location, contact_id, type, start, end, user_id) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, appointment.getCustomerId());
            ps.setString(2, appointment.getTitle());
            ps.setString(3, appointment.getDescription());
            ps.setString(4, appointment.getLocation());
            ps.setInt(5, appointment.getContactId());
            ps.setString(6, appointment.getType());
            ps.setObject(7, appointment.getStart());
            ps.setObject(8, appointment.getEnd());
            ps.setInt(9, appointment.getUserId());


            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int newAppointmentId = rs.getInt(1);
                return newAppointmentId;
            } else {
                throw new SQLException("No ID found. Failed to add an appointment.");
            }

    }

    /** This method is used to get all appointments for the week based on the
     * current date
     */
        public static ObservableList<Appointment> getAllWeekAppointments (Connection connection) throws SQLException {
            String query = "SELECT * FROM appointments WHERE WEEK(start) = WEEK(CURRENT_DATE()) AND YEAR(start) = YEAR(CURRENT_DATE());";

            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            return getAppointmentsFromResultSet(rs);

        }
    /** This method is used to get all appointments for the month based on the
     * current date
     */
        public static ObservableList<Appointment> getAllMonthAppointments (Connection connection) throws SQLException {
            String query = "SELECT * FROM appointments WHERE MONTH(start) = MONTH(CURRENT_DATE()) AND YEAR(start) = YEAR(CURRENT_DATE());";

            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            return getAppointmentsFromResultSet(rs);

        }
    /** This method is used to create a list of all appointments
     * for a customer in a specified month. This method is used in the customer reports controller.
     */
    public static ObservableList<Appointment> getAllMonthAppointmentsByCustomer(Connection connection, int customerId, Month month, String type) throws SQLException {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        String query = "SELECT * FROM Appointments WHERE customer_id = ? AND MONTH(start) = ? AND YEAR(start) = YEAR(CURDATE()) AND type = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.setInt(1, customerId);
        preparedStatement.setInt(2, month.getValue());
        preparedStatement.setString(3, type);

        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            String title = rs.getString("title");
            String description = rs.getString("description");
            String location = rs.getString("location");
            LocalDateTime start = rs.getTimestamp("start").toLocalDateTime();
            LocalDateTime end = rs.getTimestamp("end").toLocalDateTime();

            Appointment appointment = new Appointment( customerId, title, description, location, type, start, end);
            appointments.add(appointment);


        }

        return appointments;

    }
    /** This method is used to create a list of all appointments
     * for a customer
     */
    public static ObservableList<Appointment> getAllAppointmentsByCustomer(Connection connection, int customerId ) throws SQLException {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        String query = "SELECT * FROM Appointments WHERE customer_id = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        preparedStatement.setInt(1, customerId);

        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {

            Appointment appointment = new Appointment(customerId);
            appointments.add(appointment);

        }
        return appointments;
    }


    /** This method removes the selected appointment from the database.
     */
    public static void deleteAppointment (Connection connection, int appointmentId) throws SQLException {
            String query = "DELETE FROM appointments WHERE Appointment_ID = ?";

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, appointmentId);

            ps.executeUpdate();
        }



    public void updateAppointment (Connection connection, Appointment updatedAppointment) throws SQLException {
            String query = "UPDATE appointments SET Customer_ID = ?, Appointment_ID = ?, Title = ?, Description = ?, Location = ?, Contact_ID = ?, Type = ?, Start = ?, End = ?, User_ID = ? WHERE Appointment_ID = ?";

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, updatedAppointment.getCustomerId());
            ps.setInt(2, updatedAppointment.getAppointmentId());
            ps.setString(3, updatedAppointment.getTitle());
            ps.setString(4, updatedAppointment.getDescription());
            ps.setString(5, updatedAppointment.getLocation());
            ps.setInt(6, updatedAppointment.getContactId());
            ps.setString(7, updatedAppointment.getType());
            ps.setObject(8, Timestamp.valueOf(updatedAppointment.getStart()));
            ps.setObject(9, Timestamp.valueOf(updatedAppointment.getEnd()));
            ps.setInt(10, updatedAppointment.getUserId());
            ps.setInt(11, updatedAppointment.getAppointmentId());


            ps.executeUpdate();
        }


    }