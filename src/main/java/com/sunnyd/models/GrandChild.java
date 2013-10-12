package com.sunnyd.models;

import java.util.HashMap;

import com.sunnyd.annotations.inherit;
import com.sunnyd.annotations.tableAttr;

@inherit(childClassof="Child")
public class GrandChild extends Child{
    public static final String tableName = "grand_childs";

    @tableAttr
    private String grandChildName;
    
    public GrandChild(){
        super();
    }
    
    public GrandChild(HashMap<String, Object> HM) {
        super(HM);
    }
    
    public static void main(String[] args) {
        GrandChild b = new GrandChild();
        b.setGrandChildName("GrandChild");
        b.setChildName("grandMoney");
        b.setFirstName("GrandD");
        b.setLastName("GrandLuffy");
        b.save();
        
        System.out.println(b.getId());
        GrandChild a = GrandChild.find(b.getId());
//        a.setChildName("grandch1ld");
//        a.update();
//        a = GrandChild.find(b.getId());
        System.out.println(a.getGrandChildName());
        System.out.println(a.getChildName());
        System.out.println(a.getFirstName());
        System.out.println(a.getLastName());
        System.out.println(a.getCreationDate().toString());
        System.out.println(a.getLastModifiedDate().toString());    
    }

    public String getGrandChildName() {
        return grandChildName;
    }

    public void setGrandChildName(String grandChildName) {
        this.grandChildName = grandChildName;
        setUpdateFlag(true);
    }
}
