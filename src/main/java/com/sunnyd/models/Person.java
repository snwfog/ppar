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

//    @hasMany
//    private Resume[] resumes;
    
    private String status;

    public Person(){
        super();
    }
    public Person(HashMap<String, Object> HM) {
        super(HM);
    }

    public static void main(String[] args) {
        ArrayList<Map<String, Object>> a = Manager.findAll("persons", null);
        Iterator asd = a.iterator();
        while(asd.hasNext()){
            System.out.println(asd.next());
        }
//
//        Person b = Person.find(2);
//
//        System.out.println(a.getId());
//        System.out.println(a.getFirstName());
//        System.out.println(a.getLastName());
//        b.setFirstName("oaisjdoaijdoa");
//
//        b.update();
//        a.update();
        // System.out.println(a.Destroy());

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

}
