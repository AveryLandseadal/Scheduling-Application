package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Country;

import java.sql.*;

public class countryDAO extends Country {
    public countryDAO(int countryID, String countryName)  {
        super(countryID, countryName);
    }

    public static ObservableList<countryDAO> getCountries() throws SQLException {
    ObservableList<countryDAO> countriesObservableList = FXCollections.observableArrayList();
    String countrySql = "SELECT Country_ID, Country from countries";
    PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(countrySql);
    ResultSet resultSet = preparedStatement.executeQuery();
    while (resultSet.next()) {
        int countryID = resultSet.getInt("Country_ID");
        String countryName = resultSet.getString("Country");
        countryDAO country = new countryDAO(countryID, countryName);
        countriesObservableList.add(country);
    }
    return countriesObservableList;
}

    }
