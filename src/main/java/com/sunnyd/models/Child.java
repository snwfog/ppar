package com.sunnyd.models;

import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;

import com.sunnyd.annotations.ActiveRecordInheritFrom;
import com.sunnyd.annotations.ActiveRecordField;

@ActiveRecordInheritFrom(childClassof = "Person")
public class Child extends Person {
    public static final String tableName = "childs";
    
    @ActiveRecordField
    private String childName;
    
    public Child(){
        super();
        childName = "defaultChildName";
    }
    
    public Child(Map<String, Object> HM) {
        super(HM);
    }
    
    public static void main(String[] args) {
        DSLContext create = startQuery();      
        Field<?> LAST_NAME    = DSL.field("a.category_name");
        Field<?> creation_date    = DSL.field("a.creation_date");
                // Use plain SQL as select fields
        String a = "mike";
        String query =  create.select(LAST_NAME)
    
                // Use plain SQL as aliased tables (be aware of syntax!)
               .from("categories a")
    
                // Bind a variable in plain SQL
               .where(LAST_NAME.equalIgnoreCase("category1"))
    
                // Use plain SQL again as fields in GROUP BY and ORDER BY clauses
               .orderBy(creation_date).toString();
        
         Map<String, Object> b = new HashMap<String, Object>();
         b.put("categoryName", "aoisjdoiajsdoi");
         System.out.println(new Category().findAll(b));
    }

    
    public String getChildName(){
        return childName;
    }
    
    public void setChildName(String childName){
        this.childName = childName;
        setUpdateFlag(true);
    }
    

}
