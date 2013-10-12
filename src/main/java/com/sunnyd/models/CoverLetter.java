package com.sunnyd.models;

import java.util.HashMap;
import com.sunnyd.annotations.inherit;
import com.sunnyd.annotations.tableAttr;

@inherit(childClassof = "Document")
public class CoverLetter extends Document {

    public static final String tableName = "cover_letters";

    public CoverLetter() {
        super();
    }

    public CoverLetter(HashMap<String, Object> HM) {
        super(HM);
    }

}
