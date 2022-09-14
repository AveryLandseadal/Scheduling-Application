package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.firstLevelDivision;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class firstLevelDivisionDAO extends firstLevelDivision {
        public firstLevelDivisionDAO(int divisionID, String division, int country_ID) throws SQLException {
            super(divisionID, division, country_ID);


        }

    public static ObservableList<firstLevelDivisionDAO> getFirstLevelDivision() throws SQLException {
        ObservableList<firstLevelDivisionDAO> firstLevelDivisionObservableList = FXCollections.observableArrayList();
        String firstLevelDivisionSql = "SELECT Division_ID, Division, Country_ID FROM client_schedule.first_level_divisions";
        PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(firstLevelDivisionSql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int divisionID = resultSet.getInt("Division_ID");
            String division = resultSet.getString("Division");
            int country_ID = resultSet.getInt("Country_ID");
            firstLevelDivisionDAO firstLevelDivision = new firstLevelDivisionDAO(divisionID, division, country_ID);
            firstLevelDivisionObservableList.add(firstLevelDivision);
        }
        return firstLevelDivisionObservableList;

    }
}

