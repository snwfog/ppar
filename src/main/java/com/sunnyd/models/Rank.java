package com.sunnyd.models;

import com.sunnyd.Base;
import com.sunnyd.IModel;
import com.sunnyd.annotations.*;

import java.util.HashMap;

public class Rank extends Base implements IModel {

    public static final String tableName = "ranks";

    @ActiveRecordField
    private String rankName;

    @ActiveRecordField
    private Integer minPoint;

    public Rank() {
        super();
    }

    public Rank(HashMap<String, Object> HM) {
        super(HM);
    }

    public String getRankName() {
        return rankName;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
        setUpdateFlag(true);
    }

    public Integer getMinPoint() {
        return minPoint;
    }

    public void setMinPoint(Integer minPoint) {
        this.minPoint = minPoint;
        setUpdateFlag(true);
    }

}
