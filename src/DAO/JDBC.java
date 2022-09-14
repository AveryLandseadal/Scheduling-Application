package DAO;

import java.sql.*;

public class JDBC {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost:3306/";
    private static final String databaseName = "ChangeMe";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "root"; // Username
    private static String password = "Password"; // Password
    public static Connection connection;  // Connection Interface

   public static PreparedStatement preparedStatement;

    public static Connection openConnection() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(jdbcUrl, userName, password);
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
        return connection;
    }

    public static Connection getConnection() {

        return connection;
    }

    public static void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    public static void setStatement(Connection connection, String sqlStatement) throws SQLException{
        preparedStatement = connection.prepareStatement(sqlStatement);
    }
    public static PreparedStatement getPreparedStatement(){
        return preparedStatement;
    }
}

