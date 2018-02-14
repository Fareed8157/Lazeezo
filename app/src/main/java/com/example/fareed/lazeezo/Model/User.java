package com.example.fareed.lazeezo.Model;

/**
 * Created by fareed on 1/19/2018.
 */

public class User {
    private String Name;
    private String Password;
    private String Phone;
    private String IsStaff;
    private String homeAddress;
    private double balance;


   public User(){

   }

    public User(String name, String password) {
        Name = name;
        Password = password;
        IsStaff="false";
    }


    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }
}
