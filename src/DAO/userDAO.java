package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class userDAO extends User {
    public userDAO(int userID, String userName, String userPassword) {
        super(userID, userName,userPassword);

    }

    public static int checkLogin(String usernameField, String passwordField){
                try
                {
                    String verifyLogin = "SELECT * FROM users WHERE User_Name = '" + usernameField + "' AND Password = '" + passwordField + "'";
                    PreparedStatement preparedStatement = JDBC.openConnection().prepareStatement(verifyLogin);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    resultSet.next();
                    if (resultSet.getString("User_Name").equals(usernameField))
                    {
                        if (resultSet.getString("Password").equals(passwordField)) {
                            return resultSet.getInt("User_ID");

                            //loginMessageLabel.setText("Welcome!");
                        }
                    }
                } catch (SQLException e) {
                    //must return -1, or it will allow the user to proceed even with the wrong user and pass.
                    return -1;
                }
                return 0;
            }

    public static ObservableList<Integer> getUser() throws SQLException {
        ObservableList<Integer> userObservableList = FXCollections.observableArrayList();
        String customerSql = "SELECT User_ID FROM users ORDER BY User_ID";
        PreparedStatement preparedStatement = JDBC.getConnection().prepareStatement(customerSql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            userObservableList.add(resultSet.getInt("User_ID"));
        }
        return userObservableList;
    }

}