package controller;

import DAO.appointmentDAO;
import DAO.customerDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import DAO.JDBC;
import model.Appointment;
import model.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

import static DAO.JDBC.connection;
import static DAO.appointmentDAO.getAppointment;

public class SchedulingController implements Initializable {

    static ObservableList<Customer> customerObservableList;
    public TableColumn<Object, Object> appCustomerID;
    public TableColumn<Object, Object> tableContactID;
    public TableColumn<Object, Object> tableTitle;
    public TableColumn<Object, Object> tableDescription;
    public TableColumn<Object, Object> tableLocation;
    public TableColumn<Object, Object> tableType;
    public TableColumn<Object, Object> tableStart;
    public TableColumn<Object, Object> tableEnd;
    public TableColumn<Object, Object> appointmentID;
    public TableColumn<Object, Object> userID;
    public TableView<Appointment> appointmentTable;
    public Button updateAppointment;
    public Button updateCustomerButton;
    public RadioButton weekToggle;
    public RadioButton monthToggle;
    public TableColumn<Object, Object> tableCountry;
    Stage stage;
    Parent scene;
    public Button addCustomerButton;
    public Button addAppointmentButton;
    public TableView<Customer> customersTable;
    public TableColumn<Customer, Integer> tableID;
    public TableColumn<Customer, String> tableName;
    public TableColumn<Customer, String> tableAddress;
    public TableColumn<Customer, String> tablePostal;
    public TableColumn<Customer, String> tablePhone;
    public TableColumn<Customer, Integer> tableDivision;
    public Button deleteCustomerButton;


    ObservableList<Customer> observableList = FXCollections.observableArrayList();
    ObservableList<Appointment> observableListApps = FXCollections.observableArrayList();
    ObservableList<Appointment> observableListAppsMonths = FXCollections.observableArrayList();
    ObservableList<Appointment> observableListAppsWeeks = FXCollections.observableArrayList();
    ObservableList<Appointment> observableListAppsReset = FXCollections.observableArrayList();
    ObservableList<Appointment> observableListAppsDelete = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ToggleGroup toggleGroup = new ToggleGroup();
        monthToggle.setToggleGroup(toggleGroup);
        weekToggle.setToggleGroup(toggleGroup);

        Connection connection = JDBC.getConnection();
        ResultSet resultSetApps;
        try {
            customerObservableList = customerDAO.getCustomers(connection);
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM customers INNER JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID INNER JOIN countries ON countries.Country_ID = first_level_divisions.Country_ID");
            resultSetApps = connection.createStatement().executeQuery("SELECT * from appointments");

            while (resultSet.next()) {
                observableList.add(new Customer(resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"), resultSet.getString("Address"),
                        resultSet.getString("Postal_Code"), resultSet.getString("Phone"),
                        resultSet.getInt("Division_ID"), resultSet.getString("Country")));
            }

            while (resultSetApps.next()) {
                int appointmentID = resultSetApps.getInt("Appointment_ID");
                String title = resultSetApps.getString("Title");
                String description = resultSetApps.getString("Description");
                String location = resultSetApps.getString("Location");
                String type = resultSetApps.getString("Type");
                int customerID = resultSetApps.getInt("Customer_ID");
                int userID = resultSetApps.getInt("User_ID");
                int contactID = resultSetApps.getInt("Contact_Id");

                // Grab start and end from database.
                String startUTC = resultSetApps.getString("Start");
                String endUTC = resultSetApps.getString("End");

                // Make strings into LocalDateTime format.
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime localDateStart = LocalDateTime.parse(startUTC, dateTimeFormatter);
                LocalDateTime localDateEnd = LocalDateTime.parse(endUTC, dateTimeFormatter);


                // Convert LDT to ZonedDateTime to show appointments in local time based on the users machine.
                // needed to convert to UTC first
                final ZoneId utc = ZoneId.of("UTC");
                final ZoneId local = ZoneId.systemDefault();
                ZonedDateTime zonedDateTimeStart = localDateStart.atZone(utc).withZoneSameInstant(local);
                ZonedDateTime zonedDateTimeEnd = localDateEnd.atZone(utc).withZoneSameInstant(local);

                // Coverts back into string to use with Appointment constructor.
                // need to clean up the strings they look like 2022-06-01T12:00-04:00
                // should look like 2022-06-01 08:00:00 - FIXED
                String stringStart = zonedDateTimeStart.format(dateTimeFormatter);
                String stringEnd = zonedDateTimeEnd.format(dateTimeFormatter);


                observableListApps.add(new Appointment(appointmentID, title, description, location, type, stringStart,
                        stringEnd, customerID, userID, contactID));

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        tableID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        tableName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        tableAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        tablePostal.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        tablePhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tableDivision.setCellValueFactory(new PropertyValueFactory<>("divisionID"));
        tableCountry.setCellValueFactory(new PropertyValueFactory<>("country"));

        appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        appCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        tableContactID.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        userID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        tableTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        tableDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tableLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        tableType.setCellValueFactory(new PropertyValueFactory<>("type"));
        tableStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        tableEnd.setCellValueFactory(new PropertyValueFactory<>("end"));

        appointmentTable.setItems(observableListApps);

        customersTable.setItems(observableList);

    }

    @FXML
    public void onActionAddCustomer(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("../view/AddCustomerScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    public void onActionAddAppointment(ActionEvent event) throws IOException {
        if (customersTable.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("A customer must be selected in order to add an appointment");
            alert.showAndWait();
        } else {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../view/AddAppointmentScreen.fxml"));
            loader.load();
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
            AddAppointmentController ADMController = loader.getController();
            ADMController.sendAppointment(customersTable.getSelectionModel().getSelectedItem());

        }
    }

    public void onActionDeleteCustomer(ActionEvent event) throws SQLException {
        JDBC.openConnection();
        if (appointmentTable.getSelectionModel().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("A customer must be selected in order to delete a customer!");
            alert.showAndWait();
        } else{

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete the selected customer? ");
        Optional<ButtonType> confirmation = alert.showAndWait();
        if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {

            String sqlDelete = "DELETE FROM customers WHERE Customer_ID = ?";
            JDBC.setStatement(JDBC.getConnection(), sqlDelete);

            PreparedStatement psDelete = JDBC.getPreparedStatement();
            int customerFromTable = customersTable.getSelectionModel().getSelectedItem().getCustomerID();
            psDelete.setInt(1, customerFromTable);

            try {
                psDelete.execute();
                ObservableList<Customer> refreshCustomersList = customerDAO.getCustomers(connection);
                customersTable.setItems(refreshCustomersList);
            } catch (SQLException e) {
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                alert2.setContentText("There is an appointment scheduled for this customer, " +
                        "please delete the related appointment first!");
                alert2.showAndWait();
            }}
        }
    }

    public void onActionDeleteAppointment(ActionEvent event) throws SQLException {
        Connection connection = JDBC.getConnection();
        JDBC.openConnection();
        if (appointmentTable.getSelectionModel().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("An appointment must be selected in order to delete an appointment!");
            alert.showAndWait();
        } else {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete the selected appointment? ");
        Optional<ButtonType> confirmation = alert.showAndWait();
        if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
            int deleteAppointmentID = appointmentTable.getSelectionModel().getSelectedItem().getAppointmentID();
            appointmentDAO.deleteAppointment(deleteAppointmentID, connection);

            String sqlDelete = "DELETE FROM appointments WHERE Appointment_ID = ?";
            JDBC.setStatement(JDBC.getConnection(), sqlDelete);

            PreparedStatement psDelete = JDBC.getPreparedStatement();
            int customerFromTable = appointmentTable.getSelectionModel().getSelectedItem().getAppointmentID();
            String type = appointmentTable.getSelectionModel().getSelectedItem().getType();
            psDelete.setInt(1, customerFromTable);
            psDelete.execute();

            ObservableList<Appointment> refreshAppointments = getAppointment();

            appointmentTable.setItems(refreshAppointments);
            onActionReset();
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setContentText("Appointment was deleted!" + "\n"
                    + "Appointment ID = " + customerFromTable + "\n"
                    + "Appointment Type = " + type);
            alert1.showAndWait();
        }
        }
}

    public void onActionUpdateAppointment(ActionEvent event) throws IOException, SQLException {
        if (appointmentTable.getSelectionModel().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("An appointment must be selected in order to modify an appointment!");
            alert.showAndWait();
        }
        else {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../view/ModifyAppointmentScreen.fxml"));
            loader.load();
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
            ModifyAppointment ADMController = loader.getController();
            ADMController.sendModifyAppointment(appointmentTable.getSelectionModel().getSelectedItem());
    }
}

    public void onActionUpdateCustomer(ActionEvent event) throws IOException, SQLException {
        if (customersTable.getSelectionModel().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("A customer must be selected in order to modify a customer!");
            alert.showAndWait();
        }
        else {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../view/ModifyCustomerScreen.fxml"));
            loader.load();
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
            ModifyCustomer ADMController = loader.getController();
            ADMController.sendModifyCustomer(customersTable.getSelectionModel().getSelectedItem());
        }
    }

    public void onActionReportButton(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = FXMLLoader.load(getClass().getResource("../view/ReportsScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }

    public void onActionFilterByMonth(ActionEvent event) throws SQLException {
        observableListAppsWeeks.clear();
        ObservableList<Appointment> observableListAppsMonths = FXCollections.observableArrayList();
        Connection connection = JDBC.getConnection();
        ResultSet resultSetApps;
        appointmentTable.getItems().clear();
        customerObservableList = customerDAO.getCustomers(connection);
        resultSetApps = connection.createStatement().executeQuery("SELECT * from appointments");

        while (resultSetApps.next()) {
            int appointmentID = resultSetApps.getInt("Appointment_ID");
            String title = resultSetApps.getString("Title");
            String description = resultSetApps.getString("Description");
            String location = resultSetApps.getString("Location");
            String type = resultSetApps.getString("Type");
            int customerID = resultSetApps.getInt("Customer_ID");
            int userID = resultSetApps.getInt("User_ID");
            int contactID = resultSetApps.getInt("Contact_Id");

            // Grab start and end from database.
            String startUTC = resultSetApps.getString("Start");
            String endUTC = resultSetApps.getString("End");

            // Make strings into LocalDateTime format.
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime localDateStart = LocalDateTime.parse(startUTC, dateTimeFormatter);
            LocalDateTime localDateEnd = LocalDateTime.parse(endUTC, dateTimeFormatter);


            // Convert LDT to ZonedDateTime to show appointments in local time based on the users machine.
            // needed to convert to UTC first
            final ZoneId utc = ZoneId.of("UTC");
            final ZoneId local = ZoneId.systemDefault();
            ZonedDateTime zonedDateTimeStart = localDateStart.atZone(utc).withZoneSameInstant(local);
            ZonedDateTime zonedDateTimeEnd = localDateEnd.atZone(utc).withZoneSameInstant(local);

            // Coverts back into string to use with Appointment constructor.
            // need to clean up the strings they look like 2022-06-01T12:00-04:00
            // should look like 2022-06-01 08:00:00 - FIXED
            String stringStart = zonedDateTimeStart.format(dateTimeFormatter);
            String stringEnd = zonedDateTimeEnd.format(dateTimeFormatter);

            // needed to subtract 1 month from monthStart
            LocalDateTime monthStart = LocalDateTime.now().minusMonths(1);
            LocalDateTime monthEnd = LocalDateTime.now().plusMonths(1);


            if (localDateEnd.isAfter(monthStart) && localDateEnd.isBefore(monthEnd)) {
                observableListAppsMonths.add(new Appointment(appointmentID, title, description, location, type, stringStart,
                        stringEnd, customerID, userID, contactID));

            }
        }

        appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        appCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        tableContactID.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        userID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        tableTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        tableDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tableLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        tableType.setCellValueFactory(new PropertyValueFactory<>("type"));
        tableStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        tableEnd.setCellValueFactory(new PropertyValueFactory<>("end"));


        appointmentTable.setItems(observableListAppsMonths);
    }


    public void onActionReset() throws SQLException {
        observableListAppsReset.clear();
        observableListAppsMonths.clear();
        observableListAppsWeeks.clear();
        monthToggle.setSelected(false);
        weekToggle.setSelected(false);
        try {
        Connection connection = JDBC.getConnection();
        ResultSet resultSetApps;
        resultSetApps = connection.createStatement().executeQuery("SELECT * from appointments");
        while (resultSetApps.next()) {
            int appointmentID = resultSetApps.getInt("Appointment_ID");
            String title = resultSetApps.getString("Title");
            String description = resultSetApps.getString("Description");
            String location = resultSetApps.getString("Location");
            String type = resultSetApps.getString("Type");
            int customerID = resultSetApps.getInt("Customer_ID");
            int userID = resultSetApps.getInt("User_ID");
            int contactID = resultSetApps.getInt("Contact_Id");

            // Grab start and end from database.
            String startUTC = resultSetApps.getString("Start");
            String endUTC = resultSetApps.getString("End");

            // Make strings into LocalDateTime format.
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime localDateStart = LocalDateTime.parse(startUTC, dateTimeFormatter);
            LocalDateTime localDateEnd = LocalDateTime.parse(endUTC, dateTimeFormatter);


            // Convert LDT to ZonedDateTime to show appointments in local time based on the users machine.
            // needed to convert to UTC first
            final ZoneId utc = ZoneId.of("UTC");
            final ZoneId local = ZoneId.systemDefault();
            ZonedDateTime zonedDateTimeStart = localDateStart.atZone(utc).withZoneSameInstant(local);
            ZonedDateTime zonedDateTimeEnd = localDateEnd.atZone(utc).withZoneSameInstant(local);

            // Coverts back into string to use with Appointment constructor.
            // need to clean up the strings they look like 2022-06-01T12:00-04:00
            // should look like 2022-06-01 08:00:00 - FIXED
            String stringStart = zonedDateTimeStart.format(dateTimeFormatter);
            String stringEnd = zonedDateTimeEnd.format(dateTimeFormatter);


            observableListAppsReset.add(new Appointment(appointmentID, title, description, location, type, stringStart,
                    stringEnd, customerID, userID, contactID));
            appointmentTable.setItems(observableListAppsReset);
        }
    } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void onActionFilterByWeek() throws SQLException {
        observableListAppsMonths.clear();
        Connection connection = JDBC.getConnection();
        ResultSet resultSetApps;
            appointmentTable.getItems().clear();
            customerObservableList = customerDAO.getCustomers(connection);
            resultSetApps = connection.createStatement().executeQuery("SELECT * from appointments");

            // originally had this like the customers above, had to change since start and end times must be in the
            // users local time.
            while (resultSetApps.next()) {
                int appointmentID = resultSetApps.getInt("Appointment_ID");
                String title = resultSetApps.getString("Title");
                String description =resultSetApps.getString("Description");
                String location =resultSetApps.getString("Location");
                String type = resultSetApps.getString("Type");
                int customerID = resultSetApps.getInt("Customer_ID");
                int userID = resultSetApps.getInt("User_ID");
                int contactID =resultSetApps.getInt("Contact_Id");

                // Grab start and end from database.
                String startUTC = resultSetApps.getString("Start");
                String endUTC = resultSetApps.getString("End");

                // Make strings into LocalDateTime format.
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime localDateStart = LocalDateTime.parse(startUTC,dateTimeFormatter);
                LocalDateTime localDateEnd = LocalDateTime.parse(endUTC,dateTimeFormatter);


                // Convert LDT to ZonedDateTime to show appointments in local time based on the users machine.
                // needed to convert to UTC first
                final ZoneId utc = ZoneId.of("UTC");
                final ZoneId local = ZoneId.systemDefault();
                ZonedDateTime zonedDateTimeStart = localDateStart.atZone(utc).withZoneSameInstant(local);
                ZonedDateTime zonedDateTimeEnd = localDateEnd.atZone(utc).withZoneSameInstant(local);

                // Coverts back into string to use with Appointment constructor.
                // need to clean up the strings they look like 2022-06-01T12:00-04:00
                // should look like 2022-06-01 08:00:00 - FIXED
                String stringStart = zonedDateTimeStart.format(dateTimeFormatter);
                String stringEnd = zonedDateTimeEnd.format(dateTimeFormatter);

                LocalDateTime weekStart = LocalDateTime.now().minusWeeks(1);
                LocalDateTime weekEnd = LocalDateTime.now().plusWeeks(1);


                if (localDateEnd.isAfter(weekStart) && localDateEnd.isBefore(weekEnd)){
                    observableListAppsWeeks.add(new Appointment(appointmentID, title, description, location, type, stringStart,
                            stringEnd, customerID, userID, contactID));

                    }
                }

        appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        appCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        tableContactID.setCellValueFactory(new PropertyValueFactory<>("contactID"));
        userID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        tableTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        tableDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        tableLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        tableType.setCellValueFactory(new PropertyValueFactory<>("type"));
        tableStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        tableEnd.setCellValueFactory(new PropertyValueFactory<>("end"));


        appointmentTable.setItems(observableListAppsWeeks);


    }
}


