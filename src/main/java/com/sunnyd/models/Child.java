package com.sunnyd.models;

import java.util.HashMap;

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
    
    public Child(HashMap<String, Object> HM) {
        super(HM);
    }
    
    public static void main(String[] args) {
//        Child a = new Child();
//        a.setChildName("Monday");
//        a.setFirstName("D");
//        a.setLastName("Luffy");
//        a.save();
        
        Child b = Child.find(18);
        System.out.println(b.getId());
        System.out.println(b.getChildName());
        System.out.println(b.getFirstName());
        System.out.println(b.getLastName());
        
        
    }

    
    public String getChildName(){
        return childName;
    }
    
    public void setChildName(String childName){
        this.childName = childName;
        setUpdateFlag(true);
    }
    

   

}
