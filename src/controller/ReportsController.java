package controller;

import DAO.JDBC;
import DAO.appointmentDAO;
import DAO.contactDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointment;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;


public class ReportsController {
    public ComboBox<String> contactComboBox;
    public Button buttonUserID;
    public Label reportText;
    public Button contactButton;
    public Button backButton;

    public void initialize() throws SQLException {
        JDBC.openConnection();
        ObservableList<contactDAO> contactList = contactDAO.getContacts();
        ObservableList<String> contacts = FXCollections.observableArrayList();
        contactList.stream().map(model.Contact::getContactName).forEach(contacts::add);
        contactComboBox.setItems(contacts);


    }

    public void onActionReportUserID(ActionEvent event) throws SQLException {
        ObservableList<Appointment> appointment = appointmentDAO.getAppointment();
        ObservableList<Integer> user1 = FXCollections.observableArrayList();
        ObservableList<Integer> user2 = FXCollections.observableArrayList();

        appointment.forEach(appointment1 -> {
            if (appointment1.getUserID() == 1) {
                user1.add(appointment1.getAppointmentID());
            } else if (appointment1.getUserID() == 2) {
                user2.add(appointment1.getAppointmentID());
        }});
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Appointment count by user");
            alert.setContentText("Number of appointments by userID: " +
                    "\n1 = " + user1.size() +
                    "\n2 = " + user2.size());

            alert.setResizable(true);
            alert.showAndWait();


    }

    public void onActionContactReport(ActionEvent event) throws SQLException {
        ObservableList<Appointment> appointment = appointmentDAO.getAppointment();
        contactComboBox.getSelectionModel().getSelectedItem();
        Integer addContactID = 0;
        if (contactComboBox.getSelectionModel().isEmpty()){
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setContentText("Please select a contact from the dropdown!");
            alert1.showAndWait();
            return;}

        for (contactDAO Contact : contactDAO.getContacts()) {
            if (contactComboBox.getSelectionModel().getSelectedItem().equals(Contact.getContactName())) {
                addContactID = Contact.getContactID();

            for (Appointment appointments : appointment) {
                if (addContactID == appointments.getContactID()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Report: Customer Appointment by Contact ID");
                    alert.setContentText("Appointments by Contact ID #" + addContactID + ": " +
                            "\nAppointment ID: " + appointments.getAppointmentID() +
                            "\nTitle: " + appointments.getTitle() +
                            "\nType: " + appointments.getType() +
                            "\nDescription: " + appointments.getDescription() +
                            "\nStart Date: " + appointments.getStart() +
                            "\nEnd Date: " + appointments.getEnd() +
                            "\nCustomer ID: " + appointments.getCustomerID() +
                            "\nUser ID: " + appointments.getUserID());
                    alert.showAndWait();
                }
                 }
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setContentText("This contact doesn't have any appointments left!");
                alert1.showAndWait();
                return;}
        }
    }

    public void onActionCustomerAppointments(ActionEvent event) throws SQLException {
        ObservableList<String> debriefing = FXCollections.observableArrayList();
        ObservableList<String> planningSession = FXCollections.observableArrayList();
        ObservableList <Appointment> appointment = appointmentDAO.getAppointment();
        if (appointment == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("There are no appointments to generate a report for!");
            alert.showAndWait();
            return;
        }
        else {

            for (Appointment appointment1 : appointment){
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String type = appointment1.getType();
                if (type.equals("De-Briefing")){
                    debriefing.add(type);
                }
                if (type.equals("Planning Session")){
                    planningSession.add(type);
                }


            }

            }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Type Count");
        alert.setContentText("Number of appointments by type: " +
                "\nPlanning Session: " + planningSession.size() +
                "\nDe-Briefing: " + debriefing.size());
        alert.setResizable(true);
        alert.showAndWait();
        }

    public void onActionBackToSchedule(Event event) throws IOException {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene;
        scene = FXMLLoader.load(getClass().getResource("../view/SchedulingScreen.fxml"));
        stage.setScene(new Scene(scene));
        stage.show();
    }
    }



