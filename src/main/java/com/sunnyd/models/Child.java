package com.sunnyd.models;

import java.util.HashMap;

import com.sunnyd.annotations.inherit;
import com.sunnyd.annotations.tableAttr;

@inherit(childClassof = "Person")
public class Child extends Person {
    public static final String tableName = "childs";
    
    @tableAttr
    private String childName;
    
    public Child(){
        super();
    }
    
    public Child(HashMap<String, Object> HM) {
        super(HM);
    }
    
    public static void main(String[] args) {
        Child a = Child.find(1);
        System.out.println(a.getChildName());
        System.out.println(a.getFirstName());
        System.out.println(a.getLastName());
    }

    
    public String getChildName(){
        return childName;
    }
    
    public void setChildName(String childName){
        this.childName = childName;
    }
    

   

}
