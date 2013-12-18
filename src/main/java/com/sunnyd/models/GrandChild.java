package com.sunnyd.models;

import java.util.HashMap;
import java.util.Map;

import com.sunnyd.annotations.ActiveRecordInheritFrom;
import com.sunnyd.annotations.ActiveRecordField;

@ActiveRecordInheritFrom(childClassof="Child")
public class GrandChild extends Child{
    public static final String tableName = "grand_childs";

    @ActiveRecordField
    private String grandChildName;
    
    public GrandChild(){
        super();
    }
    
    public GrandChild(Map<String, Object> HM) {
        super(HM);
    }
    
    public static void main(String[] args) {
        GrandChild b = new GrandChild().find(1);
        b.setGrandChildName("adsad");
        b.setChildName("grandMoney");
        b.setFirstName("aaaaaaa");
        b.setLastName("GrandLuffy");
        b.update();
//        Integer id = b.getId();
//        System.out.println(id);
//        GrandChild a = new GrandChild().find(id);
////        a.setChildName("grandch1ld");
////        a.update();
////        a = GrandChild.find(b.getId());
//        System.out.println(a.getGrandChildName());
//        System.out.println(a.getChildName());
//        System.out.println(a.getFirstName());
//        System.out.println(a.getLastName());
//        System.out.println(a.getCreationDate().toString());
//        System.out.println(a.getLastModifiedDate().toString());

        
        
    }

    public String getGrandChildName() {
        return grandChildName;
    }

    public void setGrandChildName(String grandChildName) {
        this.grandChildName = grandChildName;
        setUpdateFlag(true);
    }
}
