package com.example.c195.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FirstLevelDivision {
    private int divisionID;
    private String divisionName;
    private String countryName;
    private int countryID;
    private ObservableList<FirstLevelDivision> firstLevelDivisions = FXCollections.observableArrayList();

    public FirstLevelDivision(int divisionID, String divisionName, int countryID) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.countryID = countryID;
    }

    public FirstLevelDivision(int divisionID, String divisionName, String countryName) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
        this.countryName = countryName;
    }

    public FirstLevelDivision(int divisionID, String divisionName) {
        this.divisionID = divisionID;
        this.divisionName = divisionName;
    }

    public ObservableList<FirstLevelDivision> getDivisions() {
        return firstLevelDivisions;
    }
    public int getDivisionID() {
        return divisionID;
    }

    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }
    public String getCountryName() {
        return divisionName;
    }

    public void setCountryName(String divisionName) {
        this.divisionName = divisionName;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    @Override
    public String toString() {
        return divisionName;
    }

}
