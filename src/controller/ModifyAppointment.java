package controller;

import DAO.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import DAO.JDBC;
import model.Appointment;
import model.Country;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static DAO.appointmentDAO.getAppointment;


public class ModifyAppointment {

    public TextField appointmentCustomerID;
    public TextField titleTextField;
    public TextField descriptionTextField;
    public DatePicker endDate;
    public DatePicker startDate;
    public ComboBox<String> contactComboBox;
    public ComboBox<String> startHours;
    public ComboBox<String> endHours;
    public ComboBox<Integer> userIDComboBox;
    public ComboBox<String> typeComboBox;
    public TextField appointmentID;
    public ComboBox<String> countryComboBox;
    public ComboBox<String> stateComboBox;
    public Button saveAppointment;
    public Button cancelButton;

    public void initialize() throws SQLException {
        ObservableList<String> appointmentHours = FXCollections.observableArrayList("00:00:00", "00:15:00",
                "00:30:00", "00:45:00", "01:00:00", "01:15:00", "01:30:00", "01:45:00", "02:00:00", "02:15:00",
                "02:30:00", "02:45:00", "03:00:00", "03:15:00", "03:30:00", "03:45:00", "04:00:00", "04:15:00",
                "04:30:00", "04:45:00", "05:00:00", "05:15:00", "05:30:00", "05:45:00", "06:00:00", "06:15:00",
                "06:30:00", "06:45:00", "07:00:00", "07:15:00", "07:30:00", "07:45:00", "08:00:00", "08:15:00",
                "08:30:00", "08:45:00", "09:00:00", "09:15:00", "09:30:00", "09:45:00", "10:00:00", "10:15:00",
                "10:30:00", "10:45:00", "11:00:00", "11:15:00", "11:30:00", "11:45:00", "12:00:00", "12:15:00",
                "12:30:00", "12:45:00", "13:00:00", "13:15:00", "13:30:00", "13:45:00", "14:00:00", "14:15:00",
                "14:30:00", "14:45:00", "15:00:00", "15:15:00", "15:30:00", "15:45:00", "16:00:00", "16:15:00",
                "16:30:00", "16:45:00", "17:00:00", "17:15:00", "17:30:00", "17:45:00", "18:00:00", "18:15:00",
                "18:30:00", "18:45:00", "19:00:00", "19:15:00", "19:30:00", "19:45:00", "20:00:00", "20:15:00",
                "20:30:00", "20:45:00", "21:00:00", "21:15:00", "21:30:00", "21:45:00", "22:00:00", "22:15:00",
                "22:30:00", "22:45:00", "23:00:00", "23:15:00", "23:30:00", "23:45:00");
        ObservableList<String> types = FXCollections.observableArrayList("De-Briefing", "Planning Session");
        ObservableList<countryDAO> countriesList = countryDAO.getCountries();
        ObservableList<String> countryNames = FXCollections.observableArrayList();
        ObservableList<contactDAO> contactList = contactDAO.getContacts();
        ObservableList<String> firstLevelDivisionNames = FXCollections.observableArrayList();
        ObservableList<String> contacts = FXCollections.observableArrayList();
        contactList.stream().map(model.Contact::getContactName).forEach(contacts::add);
        countriesList.stream().map(Country::getCountryName).forEach(countryNames::add);
        startHours.setItems(appointmentHours);
        endHours.setItems(appointmentHours);
        countryComboBox.setItems(countryNames);
        stateComboBox.setItems(firstLevelDivisionNames);
        userIDComboBox.setItems(userDAO.getUser());
        contactComboBox.setItems(contacts);
        typeComboBox.setItems(types);

        // Disables past dates from being selected
        startDate.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate todaysDate = LocalDate.now();
                setDisable(empty || date.compareTo(todaysDate) < 0);
            }});

        // Disables past dates from being selected
        endDate.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate todaysDate = LocalDate.now();
                setDisable(empty || date.compareTo(todaysDate) < 0);
            }});
    }

    public void sendModifyAppointment(Appointment selectedItem) throws SQLException {

        appointmentID.setText(String.valueOf(selectedItem.getAppointmentID()));
        appointmentCustomerID.setText(String.valueOf(selectedItem.getCustomerID()));
        userIDComboBox.setValue(selectedItem.getUserID());
        titleTextField.setText(String.valueOf(selectedItem.getTitle()));
        descriptionTextField.setText(String.valueOf(selectedItem.getDescription()));
        countryComboBox.setValue(String.valueOf(selectedItem));
        stateComboBox.setValue(selectedItem.getLocation());
        contactComboBox.setValue(String.valueOf(selectedItem.getContactID()));
        typeComboBox.setValue(String.valueOf(selectedItem.getType()));


        String divisionString = null;
        String countryString = null;
        String contactString = null;
        ObservableList<countryDAO> getCountries = countryDAO.getCountries();
        ObservableList<firstLevelDivisionDAO> getDivisionNames = firstLevelDivisionDAO.getFirstLevelDivision();
        ObservableList<String> firstLevelDivisions = FXCollections.observableArrayList();
        ObservableList<contactDAO> getContacts = contactDAO.getContacts();
        ObservableList<String> contacts = FXCollections.observableArrayList();

        //get country ID and set as int
        for (model.firstLevelDivision firstLevelDivision : getDivisionNames) {
            firstLevelDivisions.add(firstLevelDivision.getDivision());
            int countryID = firstLevelDivision.getCountryID();

            // get the state based on the division ID
            if (Objects.equals(firstLevelDivision.getDivision(), selectedItem.getLocation())) {
                divisionString = firstLevelDivision.getDivision();

                // get country based on country ID
                for (Country country : getCountries) {
                    if (country.getCountryID() == countryID) {
                        countryString = country.getCountryName();
                    }
                }
            }
        }
        countryComboBox.setValue(countryString);
        // get contact ID
        for (model.Contact contact : getContacts) {
            contacts.add(contact.getContactName());
            int contactID = contact.getContactID();

            // if both contact id's match then get the name
            if (Objects.equals(contact.getContactID(), selectedItem.getContactID())) {
                contactString = contact.getContactName();
            }}
        contactComboBox.setValue(contactString);

        // get selected and convert Start Date
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String startDate1 = String.valueOf(selectedItem.getStart());
        LocalDate localDateStartDate = LocalDate.parse(startDate1,dateTimeFormatter);
        startDate.setValue(localDateStartDate);

        // Get selected and convert Start Hours
        String start3 = String.valueOf(selectedItem.getStart());
        LocalTime newStart1 = LocalTime.parse(start3,dateTimeFormatter);
        startHours.setValue(newStart1 + ":00");

        // Get selected and convert End Date
        String end = String.valueOf(selectedItem.getEnd());
        LocalDate localDateEnd = LocalDate.parse(end,dateTimeFormatter);
        endDate.setValue(localDateEnd);

        // Get selected and convert End Hours
        String end2 = String.valueOf(selectedItem.getEnd());
        LocalTime newEnd2 = LocalTime.parse(end2,dateTimeFormatter);
        endHours.setValue(newEnd2 + ":00");

    }

    public void onActionSaveAppointment(ActionEvent event) throws SQLException {
        if (titleTextField.getText().isEmpty() || descriptionTextField.getText().isEmpty()
                || stateComboBox.getValue().isEmpty() || typeComboBox.getValue().isEmpty()
                || userIDComboBox.getValue().describeConstable().isEmpty()
                || contactComboBox.getValue().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("All of the fields must be filled out!");
            alert.showAndWait();
        } else {
            Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION);
            alert2.setContentText("Are you sure you want to save the appointment?");
            alert2.showAndWait();

            String insertStatement = "UPDATE appointments SET Appointment_ID = ?, Title = ?, Description = ?, " +
                    "Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, " +
                    "Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
            Integer addContactID = 0;
            int customerID = Integer.parseInt(appointmentCustomerID.getText());
            int aptID = Integer.parseInt(appointmentID.getText());
            for (contactDAO Contact : contactDAO.getContacts()) {
                if (contactComboBox.getSelectionModel().getSelectedItem().equals(Contact.getContactName())) {
                    addContactID = Contact.getContactID();
                }
            }
            try {

                DateTimeFormatter dateTimeFormatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");
                DateTimeFormatter dateTimeFormatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                // Get start and end dates
                LocalDate localStartDate = startDate.getValue();
                LocalDate localEndDate = endDate.getValue();

                // Convert start and end to a string
                String stringStartDate = startDate.getValue().format(dateTimeFormatterDate);
                String stringStartHours = startHours.getValue();
                String stringEndDate = endDate.getValue().format(dateTimeFormatterDate);
                String stringEndTime = endHours.getValue();

                // Converts start and end to UTC.
                String startUTC = (stringStartDate + " " + stringStartHours);
                String endUTC = (stringEndDate + " " + stringEndTime);

                LocalTime localTimeStart = LocalTime.parse(startHours.getValue(), dateTimeFormatterTime);
                LocalTime localTimeEnd = LocalTime.parse(endHours.getValue(), dateTimeFormatterTime);


                // combines start and end dates and times
                LocalDateTime dateTimeStart = LocalDateTime.of(localStartDate, localTimeStart);
                LocalDateTime dateTimeEnd = LocalDateTime.of(localEndDate, localTimeEnd);


                // Converts to whatever zone the users' machine is set to
                ZonedDateTime zonedDateTimeStart = ZonedDateTime.of(dateTimeStart, ZoneId.systemDefault());
                ZonedDateTime zonedDateTimeEnd = ZonedDateTime.of(dateTimeEnd, ZoneId.systemDefault());

                ZonedDateTime convertUTCStart = zonedDateTimeStart.withZoneSameInstant(ZoneId.of("UTC"));
                ZonedDateTime convertUTCEnd = zonedDateTimeEnd.withZoneSameInstant(ZoneId.of("UTC"));
                LocalDateTime convertedUTCStart = LocalDateTime.from(convertUTCStart);
                LocalDateTime convertedUTCEnd = LocalDateTime.from(convertUTCEnd);

                //Converts to EST
                ZonedDateTime convertStartEST = zonedDateTimeStart.withZoneSameInstant(ZoneId.of("America/New_York"));
                ZonedDateTime convertEndEST = zonedDateTimeEnd.withZoneSameInstant(ZoneId.of("America/New_York"));


                // converts EST times to local
                LocalTime appointmentStartCheck = convertStartEST.toLocalTime();
                LocalTime appointmentEndCheck = convertEndEST.toLocalTime();

                // Start and end times for the business
                LocalTime businessStart = LocalTime.of(8, 0, 0);
                LocalTime businessEnd = LocalTime.of(22, 0, 0);

                //used to check if appointments are overlapping
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime startUTCCheck = LocalDateTime.parse(startUTC, formatter);
                LocalDateTime endUTCCheck = LocalDateTime.parse(endUTC, formatter);


                if (appointmentStartCheck.isBefore(businessStart)
                        || appointmentEndCheck.isAfter(businessEnd)
                        || businessEnd.isBefore(businessStart)
                        || businessEnd.isAfter(businessEnd)) {

                    Alert alert = new Alert(Alert.AlertType.ERROR, "Time is outside of business hours! "
                            + businessStart + " - " + businessEnd + " EST");
                    alert.showAndWait();
                    return;
                }

                if (endUTC.equals(startUTC)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Start and end time cannot be the same!");
                    alert.showAndWait();
                    return;
                }
                if (dateTimeEnd.isBefore(dateTimeStart)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("End time cannot be before start time! Please check your dates and times.");
                    alert.showAndWait();
                    return;

                }


                for (Appointment appointment : getAppointment()) {
                    int customerID1 = (appointment.getCustomerID());
                    LocalDateTime getStart = LocalDateTime.parse(appointment.getStart(), formatter);
                    LocalDateTime getEnd = LocalDateTime.parse(appointment.getEnd(), formatter);
                    Integer appointmentID1 = Integer.parseInt(appointmentID.getText());
                    Integer customerCheck = Integer.parseInt(appointmentCustomerID.getText());


                    if ((appointmentID1 != appointment.getAppointmentID()) && (customerID1 == customerCheck)
                            && (convertedUTCEnd.isEqual(getStart) || convertedUTCEnd.isAfter(getStart))
                            && (convertedUTCEnd.isEqual(getEnd) || convertedUTCEnd.isBefore(getEnd))) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("End time overlaps with existing appointment.");
                        alert.showAndWait();
                        return;
                    }


                    if ((appointmentID1 != appointment.getAppointmentID()) && (customerID1 == customerCheck)
                            && (convertedUTCStart.isEqual(getStart) || convertedUTCStart.isAfter(getStart))
                            && (convertedUTCStart.isEqual(getEnd) || convertedUTCStart.isBefore(getEnd))) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Start time overlaps with existing appointment.");
                        alert.showAndWait();
                        return;
                    }
                    if ((appointmentID1 != appointment.getAppointmentID()) && (customerID1 == customerCheck)
                            && (convertedUTCStart.isEqual(getStart))) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Start time overlaps with existing appointment.");
                        alert.showAndWait();
                        return;
                    }
                    if ((appointmentID1 != appointment.getAppointmentID()) && (customerID1 == customerCheck)
                            && (convertedUTCEnd.isEqual(getEnd))) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("End time overlaps with existing appointment.");
                        alert.showAndWait();
                        return;
                    }
                    if ((appointmentID1 != appointment.getAppointmentID()) && (customerID1 == customerCheck)
                            && (convertedUTCStart.isBefore(getStart)) && (convertedUTCEnd.isAfter(getEnd))) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Appointment overlaps with existing appointment.");
                        alert.showAndWait();
                        return;
                    }
                }

            JDBC.setStatement(JDBC.getConnection(), insertStatement);
                PreparedStatement preparedStatement = JDBC.getPreparedStatement();

                // getting data truncation errors, changing from string.value of to .getText
                ///send the following to DB:
                preparedStatement.setInt(1, Integer.parseInt(appointmentID.getText()));
                preparedStatement.setString(2, titleTextField.getText());
                preparedStatement.setString(3, descriptionTextField.getText());
                preparedStatement.setString(4, stateComboBox.getSelectionModel().getSelectedItem());
                preparedStatement.setString(5, typeComboBox.getSelectionModel().getSelectedItem());
                preparedStatement.setTimestamp(6, Timestamp.valueOf(startUTC));
                preparedStatement.setTimestamp(7, Timestamp.valueOf(endUTC));
                preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.setString(9, "admin");
                preparedStatement.setInt(10, Integer.parseInt(appointmentCustomerID.getText()));
                preparedStatement.setInt(11, userIDComboBox.getSelectionModel().getSelectedItem());
                preparedStatement.setInt(12, addContactID);
                preparedStatement.setInt(13, Integer.parseInt(appointmentID.getText()));
                preparedStatement.execute();


                Parent root = FXMLLoader.load(getClass().getResource("../view/SchedulingScreen.fxml"));
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } catch (SQLException | NumberFormatException | IOException e) {
                throw new RuntimeException(e);
            }
            {

            }


        }
    }

    public void onActionCancelButton(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../view/SchedulingScreen.fxml"));
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onActionCountryDropdown(ActionEvent event) throws SQLException {
        JDBC.openConnection();
        String selectedCountry = String.valueOf(countryComboBox.getSelectionModel().getSelectedItem());
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
            case "U.S" -> stateComboBox.setItems(usStates);
            case "UK" -> stateComboBox.setItems(ukStates);
            case "Canada" -> stateComboBox.setItems(canadaStates);
        }
    }
}


