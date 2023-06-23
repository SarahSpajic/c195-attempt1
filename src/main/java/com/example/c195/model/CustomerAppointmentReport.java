package com.example.c195.model;

public class CustomerAppointmentReport {
    private String monthAndType;
    private int count;

    public void monthlyAppointmentReport(String monthAndType, int count) {
        this.monthAndType = monthAndType;
        this.count = count;
    }
}
