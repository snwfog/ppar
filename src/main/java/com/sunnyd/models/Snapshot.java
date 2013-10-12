package com.sunnyd.models;

import com.sunnyd.Base;
import com.sunnyd.IModel;
import com.sunnyd.annotations.*;
import java.util.HashMap;

public class Snapshot extends Base implements IModel {

    public static final String tableName = "snapshots";

    public Snapshot() {
        super();
    }

    public Snapshot(HashMap<String, Object> HM) {
        super(HM);
    }
}
