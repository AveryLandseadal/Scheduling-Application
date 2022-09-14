package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class appointmentDAO {
    public static ObservableList<Appointment> getAppointment() throws SQLException {

        ObservableList<Appointment> appointmentObservableList = FXCollections.observableArrayList();

        String appointmentSql = "SELECT * from appointments";

        PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(appointmentSql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int appointmentID = resultSet.getInt("Appointment_ID");
            String title = resultSet.getString("Title");
            String description = resultSet.getString("Description");
            String location = resultSet.getString("Location");
            String type = resultSet.getString("Type");
            String start = resultSet.getString("Start");
            String end = resultSet.getString("End");
            int customerID = resultSet.getInt("Customer_ID");
            int userID = resultSet.getInt("User_ID");
            int contactID = resultSet.getInt("Contact_ID");
            Appointment appointment = new Appointment(appointmentID, title, description, location, type, start, end, customerID, userID, contactID);
            appointmentObservableList.add(appointment);
        }
        return appointmentObservableList;
    }

    public static int deleteAppointment(int customer, Connection connection) throws SQLException {
        String query = "DELETE FROM appointments WHERE Appointment_ID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, customer);
        int result = preparedStatement.executeUpdate();
        preparedStatement.close();
        return result;
    }
    }

