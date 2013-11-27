package com.sunnyd.models;

import com.sunnyd.Base;
import com.sunnyd.IModel;
import com.sunnyd.annotations.*;
import com.sunnyd.database.Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Person extends Base implements IModel {
    public static final String tableName = "persons";

    @ActiveRecordField
    private String firstName;

    @ActiveRecordField
    private String lastName;
    
//    @ActiveRecordField
//    private Double price; 

//    @hasMany
//    private Resume[] resumes;
    
    private String status;

    public Person(){
        super();
    }
    public Person(Map<String, Object> HM) {
        super(HM);
    }

    public static void main(String[] args) {
        Person a = new Person().find(2);
        System.out.println(a);

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        setUpdateFlag(true);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        setUpdateFlag(true);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public boolean save(){
        if(firstName != null && firstName.isEmpty())
            return false;
        return super.save();
    }
    
    
//    public Double getPrice(){
//        return this.price;
//    }
//
//    public void setPrice(Double price){
//        this.price = price;
//        setUpdateFlag(true);
//    }
}
