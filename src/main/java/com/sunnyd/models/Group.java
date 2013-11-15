package com.sunnyd.models;

import com.sunnyd.Base;
import com.sunnyd.IModel;
import com.sunnyd.annotations.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Group extends Base implements IModel {

    public static final String tableName = "comments";

    @ActiveRecordField
    private String groupName;

    @ActiveRecordField
    private String description;
    
    @ActiveRelationManyToMany(relationTable = "groups_categories")
    private List<Category> categories;
    
    public Group() {
        super();
    }

    public Group(HashMap<String, Object> HM) {
        super(HM);
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
        setUpdateFlag(true);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        setUpdateFlag(true);
    }
    
    public List<Category> getCategories() {
        initRelation("categories");
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
    
    public static void main(String[] args) {
        Group a = new Group().find(10);
        a.setGroupName("bllllllll");
        
        Category c1 = new Category().find(4);
        c1.setCategoryName("clllllll");
        
        Category c2= new Category().find(2);
       
        Category c3 = new Category();
        
        a.getCategories().add(c1);
        a.getCategories().add(c2);
        a.getCategories().add(c3);
        a.update();
    }

}
