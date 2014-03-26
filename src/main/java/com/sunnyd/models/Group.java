package com.sunnyd.models;

import com.sunnyd.Base;
import com.sunnyd.annotations.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Group extends Base {

    public static final String tableName = "comments";

    @ActiveRecordField
    private String groupName;

    @ActiveRecordField
    private String description;
    
    @ActiveRelationManyToMany(relationTable = "groups_categories")
    private List<Category> categories;
    
    @ActiveRelationManyToMany(relationTable = "g_c", thisForeignKeyName = "gId", collectionForeignKeyName = "cId") 
    private List<Category> cs;
    
    public Group() {
        super();
    }

    public Group(Map<String, Object> HM) {
        super(HM);
    }

    public String getGroupName() {
        return groupName;
    }

    public Group setGroupName(String groupName) {
        this.groupName = groupName;
        setUpdateFlag(true);
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Group setDescription(String description) {
        this.description = description;
        setUpdateFlag(true);
        return this;
    }
    
    public List<Category> getCategories() {
        initRelation("categories");
        return categories;
    }

    public Group setCategories(List<Category> categories) {
        this.categories = categories;
        this.setUpdateFlag(true);
        return this;
    }
    

    public List<Category> getCs() {
        initRelation("cs");
        return cs;
    }

    public void setCs(List<Category> cs) {
        this.cs = cs;
        this.setUpdateFlag(true);
    }

}
