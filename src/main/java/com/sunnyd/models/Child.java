package com.sunnyd.models;

import java.util.HashMap;
import java.util.Map;

import com.sunnyd.annotations.ActiveRecordInheritFrom;
import com.sunnyd.annotations.ActiveRecordField;

@ActiveRecordInheritFrom(childClassof = "Person")
public class Child extends Person {
    public static final String tableName = "childs";
    
    @ActiveRecordField
    private String childName;
    
    public Child(){
        super();
    }
    
    public Child(Map<String, Object> HM) {
        super(HM);
    }
    
    public static void main(String[] args) {
        Child a = new Child();
        a.setChildName("Monday");
        a.setFirstName("Ddddd");
        a.setLastName("Luffy");
        a.save();
    }

    
    public String getChildName(){
        return childName;
    }
    
    public void setChildName(String childName){
        this.childName = childName;
        setUpdateFlag(true);
    }
    

}
