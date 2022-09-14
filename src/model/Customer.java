package model;

public class Customer {
    public  int customerID;
    private String customerName;
    private String address;
    private String postalCode;
    private String  phone;
    private int divisionID;

    private String division;
    private String country;

    public Customer(int customerID, String customerName, String address, String postalCode, String phone, int divisionID, String country){
        this.customerID = customerID;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionID = divisionID;
        this.country = country;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID() {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName() {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public String  getPostalCode() {
        return postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public int getDivisionID() {
        return divisionID;
    }

    public String getDivision(){
        return division;
    }

    public String getCountry(){
        return country;
    }

}