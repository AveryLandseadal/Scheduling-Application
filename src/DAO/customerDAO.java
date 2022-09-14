package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class customerDAO {
    public static ObservableList<Customer> getCustomers(Connection connection) throws SQLException {
        ObservableList<Customer> customerObservableList = FXCollections.observableArrayList();

        String customerSql = "SELECT * FROM customers INNER JOIN  first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID INNER JOIN countries ON countries.Country_ID = first_level_divisions.Country_ID";

        PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(customerSql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int customerID = resultSet.getInt("Customer_ID");
            String customerName = resultSet.getString("Customer_Name");
            String address = resultSet.getString("Address");
            String postalCode = resultSet.getString("Postal_Code");
            String phone = resultSet.getString("Phone");
            int divisionID = resultSet.getInt("Division_ID");
            String country = resultSet.getString("Country");
            Customer customer = new Customer(customerID, customerName,address,postalCode,phone,divisionID,country);
            customerObservableList.add(customer);
    }
        return customerObservableList;
    }
}
