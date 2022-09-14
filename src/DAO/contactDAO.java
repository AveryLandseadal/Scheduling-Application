package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Contact;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class contactDAO extends Contact {
    public contactDAO(int contactID, String contactName, String email){
        super(contactID, contactName, email);
    }

    public static ObservableList<contactDAO> getContacts() throws SQLException {
            ObservableList<contactDAO> contactObservableList = FXCollections.observableArrayList();
            String contactSql = "SELECT * from contacts";
        try (PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(contactSql)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String contactName = resultSet.getString("Contact_Name");
                    int contactID = resultSet.getInt("Contact_ID");
                    String email = resultSet.getString("Email");
                    contactDAO Contact = new contactDAO(contactID,contactName,email);
                    contactObservableList.add(Contact);
                }
                return contactObservableList;
        }

    }
}



