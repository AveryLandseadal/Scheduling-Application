package model;

public class Appointment {
    private int appointmentID;
    private String title;
    private String description;
    private String location;
    private String type;
    private String start;
    private String end;
    private int customerID;
    private int userID;
    private int contactID;

    public Appointment(int appointmentID, String title, String description, String location, String type, String start, String end, int customerID, int userID, int contactID) {
        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;

    }

    public int getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID() {
        this.appointmentID = appointmentID;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle() {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription() {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation() {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType() {
        this.type = type;
    }

    public String getStart(){
        return start;
    }

    public String setStart(){
        this.start = start;
        return start;
    }

    public String getEnd(){
        return end;
    }

    public String setEnd(){
        this.end = end;
        return end;
    }
    public int getCustomerID(){
        return customerID;
    }

    public void setCustomerID(){
        this.customerID = customerID;
    }
    public int getUserID(){
        return userID;
    }

    public void setUserID(){
        this.userID = userID;
    }
    public int getContactID(){
        return contactID;
    }

    public void setContactID(){
        this.contactID = contactID;
    }
}