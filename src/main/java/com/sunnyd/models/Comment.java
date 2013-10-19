package com.sunnyd.models;

import com.sunnyd.Base;
import com.sunnyd.IModel;
import com.sunnyd.annotations.*;

import java.util.HashMap;

public class Comment extends Base implements IModel {

    public static final String tableName = "comments";

    @ActiveRecordField
    private String message;

    public Comment() {
        super();
    }

    public Comment(HashMap<String, Object> HM) {
        super(HM);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        setUpdateFlag(true);
    }
}
