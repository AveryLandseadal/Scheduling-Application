package controller;

import DAO.countryDAO;
import DAO.firstLevelDivisionDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import DAO.JDBC;
import model.Country;
import model.Customer;
import model.firstLevelDivision;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class ModifyCustomer {
    public Button cancelButton;
    public Button save;
    public TextField addCustomerID;
    public TextField addName;
    public TextField addAddress;
    public TextField addPostalCode;
    public TextField addPhone;
    public ComboBox<String> addCountry;
    public ComboBox<String> states;
    public TextField addDivision;

    public void initialize() throws SQLException {
        ObservableList<countryDAO> countriesList  = countryDAO.getCountries();
        ObservableList<String> countryNames = FXCollections.observableArrayList();
        ObservableList<String> firstLevelDivisionAllNames = FXCollections.observableArrayList();
        countriesList.stream().map(Country::getCountryName).forEach(countryNames::add);
        addCountry.setItems(countryNames);
        states.setItems(firstLevelDivisionAllNames);
        }

    public void sendModifyCustomer(Customer selectedItem) throws SQLException {

        addCustomerID.setText(String.valueOf(selectedItem.getCustomerID()));
        addName.setText(selectedItem.getCustomerName());
        addAddress.setText(selectedItem.getAddress());
        addPostalCode.setText(selectedItem.getPostalCode());
        addPhone.setText(selectedItem.getPhone());
        addCountry.setValue(String.valueOf(selectedItem));
        states.setValue(selectedItem.getDivision());
        addDivision.setText(String.valueOf(selectedItem.getDivisionID()));

        // needed to set the ComboBoxes based on the division
        String divisionString = null;
        String countryString = null;
        ObservableList<countryDAO> getCountries = countryDAO.getCountries();
        ObservableList<firstLevelDivisionDAO> getDivisionNames = firstLevelDivisionDAO.getFirstLevelDivision();
        ObservableList<String> firstLevelDivisions = FXCollections.observableArrayList();

            //get country ID and set as int
        for (firstLevelDivision firstLevelDivision: getDivisionNames) {
            firstLevelDivisions.add(firstLevelDivision.getDivision());
            int countryID = firstLevelDivision.getCountryID();

            // get the state based on the division ID
            if (firstLevelDivision.getDivisionID() == selectedItem.getDivisionID()) {
                divisionString = firstLevelDivision.getDivision();

                // get country based on country ID
                for (Country country : getCountries) {
                    if (country.getCountryID() == countryID) {
                        countryString = country.getCountryName();
                    }
                }
            }
        }
        states.setValue(divisionString);
        addCountry.setValue(countryString);
    }

    public void onActionCancelButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../view/SchedulingScreen.fxml"));
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onActionAddCustomer(ActionEvent event) {
        try{
            if (addName.getText().isEmpty() || addAddress.getText().isEmpty() || addPostalCode.getText().isEmpty() ||
                    addPhone.getText().isEmpty() || addCountry.getValue().isEmpty() || states.getSelectionModel().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("All of the fields must be filled out!");
                alert.showAndWait();
                return;
            }
            String insertStatement = "UPDATE customers SET Customer_ID = ?, Customer_Name = ?, Address = ?, " +
                    "Postal_Code = ?, Phone = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
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
                // getting data truncation errors, changing from string.value of to .getText
                preparedStatement.setInt(1, Integer.parseInt(addCustomerID.getText()));
                preparedStatement.setString(2, addName.getText());
                preparedStatement.setString(3, addAddress.getText());
                preparedStatement.setString(4, addPostalCode.getText());
                preparedStatement.setString(5, addPhone.getText());
                preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.setString(7, "admin");
                preparedStatement.setInt(8,addDivisionID);
                preparedStatement.setInt(9, Integer.parseInt(addCustomerID.getText()));
                preparedStatement.execute();

                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("Customer was modified successfully!");
                alert1.showAndWait();



                Parent root = FXMLLoader.load(getClass().getResource("../view/SchedulingScreen.fxml"));
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void onActionCountryDropdown(ActionEvent event) throws SQLException {
        JDBC.openConnection();

        String selectedCountry = addCountry.getSelectionModel().getSelectedItem();

        ObservableList<firstLevelDivisionDAO> getFirstLevelDivision = firstLevelDivisionDAO.getFirstLevelDivision();

        ObservableList<String> usStates = FXCollections.observableArrayList();
        ObservableList<String> ukStates = FXCollections.observableArrayList();
        ObservableList<String> canadaStates = FXCollections.observableArrayList();

        //couldn't get this to work, finally found a solution : extended firstLevelDivision and added super(), works now
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

    }
}
