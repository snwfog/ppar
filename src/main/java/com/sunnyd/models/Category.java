package com.sunnyd.models;

import com.sunnyd.Base;
import com.sunnyd.IModel;
import com.sunnyd.annotations.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ActiveRecordModel
public class Category extends Base implements IModel {

    public static final String tableName = "categories";

    @ActiveRecordField
    private String categoryName;

    @ActiveRecordField
    private String description;
    
    @ActiveRelationManyToMany(relationTable = "groups_categories")
    private List<Group> groups;

    public Category() {
        super();
    }

    public Category(Map<String, Object> HM) {
        super(HM);
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        setUpdateFlag(true);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        setUpdateFlag(true);
    }

    public List<Group> getGroups() {
        initRelation("groups");
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public static void main(String[] args) {
        Category a = new Category().find(1);
        System.out.println(Arrays.asList(a.getGroups()).toString());
    }
}
