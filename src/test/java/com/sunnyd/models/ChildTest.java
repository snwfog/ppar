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
        Child c = new Child();
        Assert.assertNull(c.getId());
        Assert.assertNull(c.getCreationDate());
        Assert.assertNull(c.getLastModifiedDate());
        Assert.assertNull(c.getChildName());
        Assert.assertNull(c.getFirstName());
        Assert.assertNull(c.getLastName());
        Assert.assertNull(c.getStatus());
        Assert.assertFalse(c.getUpdateFlag());
        c.setStatus("aoidjaoidja");
        c.setFirstName("monkey");
        c.setLastName("d");
        c.setChildName("luffy");
        Assert.assertTrue(c.save());
        Integer id = 1;
        Assert.assertEquals(id.intValue(), c.getId().intValue());
    }

    @Test (dependsOnMethods = { "TestSave" })
    public static void TestFind() {
        Child c = new Child().find(1);
        Assert.assertEquals("luffy", c.getChildName());
        Assert.assertEquals("monkey", c.getFirstName());
        Assert.assertEquals("d", c.getLastName());
        Integer id = 1;
        Assert.assertEquals(id.intValue(), c.getId().intValue());
    }

    @Test(dependsOnMethods = { "TestFind" })
    public void TestUpdate() {
        Child c = new Child().find(1);
        Assert.assertEquals("luffy", c.getChildName());
        Assert.assertEquals("monkey", c.getFirstName());
        Assert.assertEquals("d", c.getLastName());
        c.setChildName("mark");
        c.setFirstName("john");
        c.setLastName("malkovich");
        Assert.assertTrue(c.update());
        Integer id = 1;
        Assert.assertEquals(id.intValue(), c.getId().intValue());

    }
   
    @Test(dependsOnMethods = { "TestUpdate" })
    public void TestDestroy() {
        Child c = new Child().find(1);
        c.setChildName("a");
        c.setFirstName("b");
        c.setLastName("c");
        c.update();
       
        Assert.assertTrue(c.destroy());
        Assert.assertNull(c.getId());
    }

}
