package com.sunnyd.models;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.sunnyd.Base;
import com.sunnyd.IModel;
import com.sunnyd.annotations.ActiveRelationHasOne;
import com.sunnyd.annotations.ActiveRecordField;
import com.sunnyd.database.Connector;
import com.sunnyd.database.Manager;
import com.sunnyd.database.fixtures.Prep;

public class ManyToManyTest extends Base implements IModel{
    private static final boolean purgeExistingRecord = true;
    
    private Integer groupId = null;

    @BeforeClass
    public void init() throws SQLException {
        Prep.init("groups");
        Prep.init("categories");
    }
    
    public void prepTable() throws SQLException {
        Prep.purgeAllRecord("groups", false);
        Prep.resetPrimaryKey("groups");    
        Prep.purgeAllRecord("categories", false);
        Prep.resetPrimaryKey("categories");
        Prep.purgeAllRecord("groups_categories", false);
    }

    @Test
    public void TestSaveWithExistingObject() {
        try {
            prepTable();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Group g = new Group();
        g.setGroupName("group group group");
        g.save();
        groupId = g.getId();
        
        Category c1 = new Category();
        c1.setCategoryName("category1");
        c1.save();

        
        
        Category c2 = new Category();
        c2.setCategoryName("category2"); 
        c2.save();

        
        g.getCategories().add(c1);
        g.getCategories().add(c2);
        g.update();
        
        String query = "SELECT * FROM groups_categories WHERE category_id="+c1.getId()+" AND group_id = "+g.getId();
        String query2 = "SELECT * FROM groups_categories WHERE category_id="+c2.getId()+" AND group_id = "+g.getId();
        ResultSet rs = Manager.rawSQLfind(query);
        ResultSet rs2 = Manager.rawSQLfind(query2);
         
        try {
            Assert.assertTrue(rs.next());
            Assert.assertTrue(rs2.next());
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }finally{
            try {
                rs.close();
                rs2.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        try {
            Connection a = Connector.getConnection();
            if(!a.isClosed()){
                a.close();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  

    }
    
    @Test (dependsOnMethods = { "TestSaveWithExistingObject" })
    public void TestInitializing(){
        Group g = new Group().find(groupId); 
        //Verify lazy loading

        Field field;
        try {
            field = g.getClass().getDeclaredField("categories");
            field.setAccessible(true);
            Assert.assertNull(field.get(g));
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            Assert.fail();
            e.printStackTrace();
        }
        Assert.assertNotNull(g.getCategories());
    }
    
    
    
    @Test
    public void TestSaveWithNewObject() {
        Group g = new Group();
        g.setGroupName("group group group");
        
        Category c1 = new Category();
        c1.setCategoryName("new_category1");

        Category c2 = new Category();
        c2.setCategoryName("new_category2"); 

        
        g.getCategories().add(c1);
        g.getCategories().add(c2);
        g.save();
        
        String query = "SELECT * FROM groups_categories WHERE category_id="+c1.getId()+" AND group_id = "+g.getId();
        String query2 = "SELECT * FROM groups_categories WHERE category_id="+c2.getId()+" AND group_id = "+g.getId();
        ResultSet rs = Manager.rawSQLfind(query);
        ResultSet rs2 = Manager.rawSQLfind(query2);
         
        try {
            Assert.assertTrue(rs.next());
            Assert.assertTrue(rs2.next());
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }finally{
            try {
                rs.close();
                rs2.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        try {
            Connection a = Connector.getConnection();
            if(!a.isClosed()){
                a.close();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  

    }
    
    
    
    @Test (dependsOnMethods = { "TestInitializing" })
    public void TestRemovingObjectFromCollection(){
        Group g = new Group().find(groupId); 
        System.out.println(g);
        Category c1 = g.getCategories().remove(0);
        g.setUpdateFlag(true);
        g.update();
        
        String query = "SELECT * FROM groups_categories WHERE category_id="+c1.getId()+" AND group_id = "+g.getId();
        ResultSet rs = Manager.rawSQLfind(query);
        
        try {
            Assert.assertFalse(rs.next());
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                rs.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        try {
            Connection a = Connector.getConnection();
            if(!a.isClosed()){
                a.close();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  
        
    } 
    
    
    @Test (dependsOnMethods = { "TestInitializing" })
    public void TestReplacingCollection(){
        Group g = new Group().find(groupId); 
    } 

}
