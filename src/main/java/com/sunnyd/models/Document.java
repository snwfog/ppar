package com.sunnyd.models;

import java.util.HashMap;

import com.sunnyd.Base;
import com.sunnyd.IModel;
import com.sunnyd.annotations.tableAttr;

public class Document extends Base implements IModel {
    public static final String tableName = "documents";

    @tableAttr
    private String docName;

   /* private enum type {
        resume("resume"), coverletter("coverletter");
        private type(String value) {
            this.value = value;
        }

        private final String value;

        public String getValue() {
            return value;
        }
    };  */
    
    private enum type{resume,coverletter}; 
    
    public Document(){
        super();
    }
    
    public Document(HashMap<String, Object> HM) {
        super(HM);
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

}
