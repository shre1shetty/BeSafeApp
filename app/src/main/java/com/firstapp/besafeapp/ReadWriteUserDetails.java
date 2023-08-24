package com.firstapp.besafeapp;

public class ReadWriteUserDetails {
    public  String fullName,dob,womenChild,ContactNo;
    public ReadWriteUserDetails(){};
    public ReadWriteUserDetails(String textFullName,String textDob,String textWomenChild,String textContactNo){
        this.fullName=textFullName;
        this.dob=textDob;
        this.womenChild=textWomenChild;
        this.ContactNo=textContactNo;
    }
}
