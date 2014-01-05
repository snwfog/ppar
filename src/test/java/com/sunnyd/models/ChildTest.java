package com.sunnyd.models;

import com.sunnyd.database.fixtures.Prep;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.sunnyd.annotations.ActiveRecordInheritFrom;

import java.sql.SQLException;

@ActiveRecordInheritFrom(childClassof = "Person")
public class ChildTest extends PersonTest {
    public static final String tableName = "childs";
    public static final String parentTableName = "persons";
    
    private Integer childId = null;


    /****************************** TEST ********************************************************/

    private static final boolean purgeExistingRecord = true;

    @BeforeClass
    public void init() throws SQLException {
        Prep.init(tableName);
    }

    public void prepTable() throws SQLException {
        Prep.purgeAllRecord("grand_childs", true);
        Prep.resetPrimaryKey("grand_childs");
        Prep.purgeAllRecord(tableName, true);
        Prep.resetPrimaryKey(tableName);
        Prep.purgeAllRecord(parentTableName, true);
        Prep.resetPrimaryKey(parentTableName);
    }

    @Test
    public void TestSave() {
        try {
            prepTable();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //Fake first person
        Person tempPerson = new Person();
        tempPerson.setFirstName("TEMPMPMPMPMP");
        tempPerson.save();
        
        Child c = new Child();
        Assert.assertNull(c.getId());
        Assert.assertNull(c.getCreationDate());
        Assert.assertNull(c.getLastModifiedDate());
        Assert.assertNull(c.getChildName());
        Assert.assertNull(c.getFirstName());
        Assert.assertNull(c.getLastName());
        Assert.assertNull(c.getStatus());
        Assert.assertFalse(c.getUpdateFlag());
        
        //Set Data
        c.setStatus("aoidjaoidja");
        c.setFirstName("monkey");
        c.setLastName("d");
        c.setChildName("luffy");
        Assert.assertTrue(c.save());
        
        Assert.assertEquals(tempPerson.getId()+1, c.getId().intValue());
        
        //Validate Inheritance
        Person a = new Person().find(c.getId());
        Assert.assertEquals(a.getFirstName(), c.getFirstName());
        Assert.assertEquals(a.getLastName(), c.getLastName());
        
        
        //Prepare for find test
        childId = c.getId();
        
        
    }

    @Test (dependsOnMethods = { "TestSave" })
    public void TestFind() {
        Child c = new Child().find(childId);
        Assert.assertEquals("luffy", c.getChildName());
        Assert.assertEquals("monkey", c.getFirstName());
        Assert.assertEquals("d", c.getLastName());
    }

    @Test(dependsOnMethods = { "TestFind" })
    public void TestUpdate() {
        Child c = new Child().find(childId);
        c.setChildName("mark");
        c.setFirstName("john");
        c.setLastName("malkovich");
        Assert.assertTrue(c.update());
        
        
        //Retrieve child from database to validate that it is saved
        Child updateC = new Child().find(childId); 
        Assert.assertEquals("mark", updateC.getChildName());
        Assert.assertEquals("john", updateC.getFirstName());
        Assert.assertEquals("malkovich", updateC.getLastName());
        
        

    }
   
    @Test(dependsOnMethods = { "TestUpdate" })
    public void TestDestroy() {
        Child c = new Child().find(childId);
        Assert.assertTrue(c.destroy());
        Assert.assertNull(c.getId());
    }

}
