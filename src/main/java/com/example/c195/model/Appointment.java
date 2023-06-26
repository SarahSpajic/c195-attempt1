package com.example.c195.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * This class represents an APPOINTMENT and its associated CUSTOMERS
 **/
public class Appointment {
    private int contactId;

    private ObservableList<Customer> associatedCustomers = FXCollections.observableArrayList();
    private int appointmentId;
    private int customerId;
    private String title;
    private String description;
    private String location;
    private String type;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private int userId;

    public Appointment(int customerId, int appointmentId, String title, String description, String location, int contactId, String type, LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        this.customerId = customerId;
        this.appointmentId = appointmentId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contactId = contactId;
        this.type = type;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.userId = userId;
    }

    public Appointment(int customerId, String title, String description, String location, int contactId, String type, LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        this.customerId = customerId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contactId = contactId;
        this.type = type;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.userId = userId;
    }


    public Appointment() {

    }
    private String startMonth;

    public Appointment(int customerId, String title, String description, String location, String type, LocalDateTime start, LocalDateTime end) {
        this.startMonth = start.getMonth().name();
    }

    public Appointment(int appointmentId, int customerId) {
    }

    public Appointment(int customerID, LocalDateTime startDateTime) {
    }

    public Appointment(int customerId, LocalDateTime appointmentStart, LocalDateTime appointmentEnd) {
    }

    public Appointment(LocalDateTime start, LocalDateTime end) {
    }

    public Appointment(int customerId, LocalDateTime start, LocalDateTime end, int userId) {
    }

    public Appointment(ResultSet rs) {
    }

    public Appointment(int appointmentId, int customerId, String title, String description, String location, int contactId, String type, LocalDateTime start, LocalDateTime end) {
    }

    public Appointment(int customerId) {
    }


    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public ObservableList<Customer> getAssociatedCustomers() {
        return associatedCustomers;
    }

    public void setAssociatedCustomers(ObservableList<Customer> associatedCustomers) {
        this.associatedCustomers = associatedCustomers;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public void setStart(LocalDateTime start) {
        ZonedDateTime zdt = start.atZone(ZoneId.systemDefault());
        ZonedDateTime utc = zdt.withZoneSameInstant(ZoneId.of("UTC"));
        this.startDateTime = utc.toLocalDateTime();
    }

    public void setEnd(LocalDateTime end) {
        ZonedDateTime zdt = end.atZone(ZoneId.systemDefault());
        ZonedDateTime utc = zdt.withZoneSameInstant(ZoneId.of("UTC"));
        this.endDateTime = utc.toLocalDateTime();
    }


    public LocalDateTime getStart() {
        return startDateTime;
    }

    public LocalDateTime getEnd() {
        return endDateTime;
    }



    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


}

