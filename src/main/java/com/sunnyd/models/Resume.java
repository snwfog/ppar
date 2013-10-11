package com.sunnyd.models;

import java.util.HashMap;

import com.sunnyd.annotations.inherit;
import com.sunnyd.annotations.tableAttr;

@inherit(childClassof = "Document")
public class Resume extends Document{
    
    public static final String tableName = "resumes";
    
    public Resume(){
        super();
    }
    
    public Resume(HashMap<String, Object> HM) {
        super(HM);
    }

}
