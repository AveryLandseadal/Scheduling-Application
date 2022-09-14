package model;

public class User {

    public static int userID;
    private static String userName;
    private String userPassword;


    public User(int userID, String userName, String userPassword) {
        this.userID = userID;
        this.userName = userName;
        this.userPassword = userPassword;
    }

    public static int getUserID() {
        return userID;
    }

    public int setUserID(int userID){
        this.userID = userID;
        return userID;
    }

    public static String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }
}