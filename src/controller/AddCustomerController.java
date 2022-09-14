package controller;

import DAO.countryDAO;
import DAO.firstLevelDivisionDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import DAO.JDBC;
import model.Country;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;

public class AddCustomerController {
    public Button cancelButton;
    public TextField addCustomerID;
    public TextField addName;
    public TextField addAddress;
    public TextField addPostalCode;
    public TextField addPhone;
    public Button save;
    public ComboBox <String> addCountry;

    public ComboBox <String> states;
    public TextField addDivision;

    public void onActionCancelButton(ActionEvent event) throws IOException, SQLException {
        Parent root = FXMLLoader.load(getClass().getResource("../view/SchedulingScreen.fxml"));
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();}

    public void initialize(){
        Integer addCustomerID = null;
        try {
            ObservableList<countryDAO> countriesList  = countryDAO.getCountries();
            ObservableList<String> countryNames = FXCollections.observableArrayList();
            ObservableList<firstLevelDivisionDAO> allFirstLevelDivisions = firstLevelDivisionDAO.getFirstLevelDivision();
            ObservableList<String> firstLevelDivisionAllNames = FXCollections.observableArrayList();
            countriesList.stream().map(Country::getCountryName).forEach(countryNames::add);
            addCountry.setItems(countryNames);
            states.setItems(firstLevelDivisionAllNames);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void onActionAddCustomer(ActionEvent event) throws SQLException, IOException {
        try {
            if (addName.getText().isEmpty() || addAddress.getText().isEmpty() || addPostalCode.getText().isEmpty() ||
                    addPhone.getText().isEmpty() || addCountry.getValue().isEmpty() || states.getSelectionModel().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("All of the fields must be filled out!");
                alert.showAndWait();

            } else {
                String insertStatement = "INSERT INTO customers (Customer_ID, Customer_Name, Address, " +
                        "Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?,?,?,?,?,?,?,?,?,?)";

                Integer addCustomerID = 0;
                Integer addDivisionID = 0;
                for (firstLevelDivisionDAO firstLevelDivision : firstLevelDivisionDAO.getFirstLevelDivision()) {
                    if (states.getSelectionModel().getSelectedItem().equals(firstLevelDivision.getDivision())) {
                        addDivisionID = firstLevelDivision.getDivisionID();
                        addDivision.setText(String.valueOf(addDivisionID));
                    }
                }

                try {

                    JDBC.setStatement(JDBC.getConnection(), insertStatement);
                    PreparedStatement preparedStatement = JDBC.getPreparedStatement();

                    //send the following to DB:
                    preparedStatement.setInt(1, addCustomerID);
                    preparedStatement.setString(2, addName.getText());
                    preparedStatement.setString(3, addAddress.getText());
                    preparedStatement.setString(4, addPostalCode.getText());
                    preparedStatement.setString(5, addPhone.getText());
                    preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                    preparedStatement.setString(7, "admin");
                    preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                    preparedStatement.setString(9, "admin");
                    preparedStatement.setInt(10, addDivisionID);
                    preparedStatement.execute();

                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert1.setContentText("Customer was created successfully!");
                    alert1.showAndWait();


                    Parent root = FXMLLoader.load(getClass().getResource("../view/SchedulingScreen.fxml"));
                    Stage stage = (Stage) save.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                } finally {

                }
            }
        } finally {

        }
    }

        public void onActionCountryDropdown(ActionEvent event) throws SQLException {
        try {

            JDBC.openConnection();

            String selectedCountry = addCountry.getSelectionModel().getSelectedItem();

            ObservableList<firstLevelDivisionDAO> getFirstLevelDivision = firstLevelDivisionDAO.getFirstLevelDivision();

            ObservableList<String> usStates = FXCollections.observableArrayList();
            ObservableList<String> ukStates = FXCollections.observableArrayList();
            ObservableList<String> canadaStates = FXCollections.observableArrayList();


            getFirstLevelDivision.forEach(firstLevelDivision -> {
                if (firstLevelDivision.getCountryID() == 1) {
                    usStates.add(String.valueOf(firstLevelDivision.getDivision()));
                } else if (firstLevelDivision.getCountryID() == 2) {
                    ukStates.add(String.valueOf(firstLevelDivision.getDivision()));
                } else if (firstLevelDivision.getCountryID() == 3) {
                    canadaStates.add(String.valueOf(firstLevelDivision.getDivision()));
                }
            });
            switch (selectedCountry) {
                case "U.S" -> states.setItems(usStates);
                case "UK" -> states.setItems(ukStates);
                case "Canada" -> states.setItems(canadaStates);
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void onActionStatesDropdown(ActionEvent event) throws SQLException {
        Integer addDivisionID = 0;
        for (firstLevelDivisionDAO firstLevelDivision : firstLevelDivisionDAO.getFirstLevelDivision()) {
            if (states.getSelectionModel().getSelectedItem().equals(firstLevelDivision.getDivision())) {
                addDivisionID = firstLevelDivision.getDivisionID();
                addDivision.setText(String.valueOf(addDivisionID));
        addDivision.setText(String.valueOf(addDivisionID));
    }

        }
            }
    }




