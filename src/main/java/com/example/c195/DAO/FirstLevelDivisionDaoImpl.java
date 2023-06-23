package com.example.c195.DAO;

import com.example.c195.model.Country;
import com.example.c195.model.FirstLevelDivision;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;



public class FirstLevelDivisionDaoImpl {
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    public ObservableList<Country> getAllCountries() throws Exception {
        Connection connection = DBConnection.makeConnection();
        String query = "SELECT * FROM countries";

        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        ObservableList<Country> countries = FXCollections.observableArrayList();

        while (rs.next()) {
            int countryID = rs.getInt("Country_ID");
            String countryName = rs.getString("Country");

            Country country = new Country(countryID, countryName);
            countries.add(country);
        }

        return countries;
    }
    /** This method checks for the divisions(states) associated with each country id */
    public ObservableList<FirstLevelDivision> getDivisionsByCountryID(int countryID) throws Exception {
        Connection connection = DBConnection.makeConnection();
        String query = "SELECT * FROM first_level_divisions WHERE Country_ID = ?";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, countryID);
        ResultSet rs = ps.executeQuery();

        ObservableList<FirstLevelDivision> divisions = FXCollections.observableArrayList();

        while (rs.next()) {
            int divisionID = rs.getInt("Division_ID");
            String divisionName = rs.getString("Division");

            FirstLevelDivision division = new FirstLevelDivision(divisionID, divisionName, countryID);
            divisions.add(division);
        }

        return divisions;
    }

}
