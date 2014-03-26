package com.sunnyd.models;

import com.sunnyd.Base;
import com.sunnyd.annotations.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveRecordModel
public class Category extends Base{

    public static final String tableName = "categories";

    @ActiveRecordField
    private String categoryName;

    @ActiveRecordField
    private String description;
    
    @ActiveRelationManyToMany(relationTable = "groups_categories")
    private List<Group> groups;
    
    
    @ActiveRelationManyToMany(relationTable = "g_c", thisForeignKeyName = "cId", collectionForeignKeyName = "gId") 
    private List<Group> gs;

    public Category() {
        super();
        categoryName = "mmmmmm";
        description = "1111122";
    }

    public Category(Map<String, Object> HM) {
        super(HM);
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Category setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        setUpdateFlag(true);
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Category setDescription(String description) {
        this.description = description;
        setUpdateFlag(true);
        return this;
    }

    public List<Group> getGroups() {
        initRelation("groups");
        return groups;
    }

    public Category setGroups(List<Group> groups) {
        this.groups = groups;
        return this;
    }

    public static void main(String[] args) {
      Map<String, Object> qqq = new HashMap<>();
      qqq.put("description", "asdasdads");
      Category a = new Category(qqq);
      System.out.println(a.toMap(true));
      
      System.out.println(Arrays.asList(a.getGroups()).toString());
    }

    public List<Group> getGs() {
        initRelation("gs");
        return gs;
    }

    public void setGs(List<Group> gs) {
        this.gs = gs;
        this.setUpdateFlag(true);
    }
}
