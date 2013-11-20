package com.sunnyd.models;

import java.util.HashMap;

import com.sunnyd.annotations.ActiveRecordInheritFrom;
import com.sunnyd.annotations.ActiveRecordField;

@ActiveRecordInheritFrom(childClassof = "Document")
public class Resume extends Document {

    public static final String tableName = "resumes";

    public Resume() {
        super();
    }

    public Resume(HashMap<String, Object> HM) {
        super(HM);
    }

}
