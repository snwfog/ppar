package com.sunnyd.models;

import java.sql.SQLException;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.sunnyd.annotations.ActiveRecordInheritFrom;
import com.sunnyd.database.fixtures.Prep;

@ActiveRecordInheritFrom(childClassof = "ChildTest")
public class GrandChildTest extends ChildTest {
    public static final String tableName = "grand_childs";
    public static final String parentTableName = "childs";
    public static final String grandParentTableName = "persons";

    /****************************** TEST ********************************************************/

    private static final boolean purgeExistingRecord = true;

    @BeforeClass
    public void init() throws SQLException {
        Prep.init(tableName);
    }

    public void prepTable() throws SQLException {
        Prep.purgeAllRecord(tableName, false);
        Prep.purgeAllRecord(parentTableName, false);
        Prep.purgeAllRecord(grandParentTableName, false);
        Prep.resetPrimaryKey(tableName);
        Prep.resetPrimaryKey(parentTableName);
        Prep.resetPrimaryKey(grandParentTableName);
    }

    @Test
    public void TestSave() {
        try {
            prepTable();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        GrandChild gc = new GrandChild();
        Assert.assertNull(gc.getId());
        Assert.assertNull(gc.getCreationDate());
        Assert.assertNull(gc.getLastModifiedDate());
        Assert.assertNull(gc.getGrandChildName());
        Assert.assertNull(gc.getChildName());
        Assert.assertNull(gc.getFirstName());
        Assert.assertNull(gc.getLastName());
        Assert.assertNull(gc.getStatus());
        Assert.assertFalse(gc.getUpdateFlag());
        gc.setChildName("grandMonkey");
        gc.setFirstName("GrandD");
        gc.setLastName("GrandLuffy");
        gc.setGrandChildName("GrandChild");
        Assert.assertTrue(gc.save());
        Integer id = 1;
        Assert.assertEquals(id.intValue(), gc.getId().intValue());
    }

    @Test(dependsOnMethods = { "TestSave" })
    public static void TestFind() {
        GrandChild gc = new GrandChild().find(1);
        Assert.assertEquals("grandMonkey", gc.getChildName());
        Assert.assertEquals("GrandD", gc.getFirstName());
        Assert.assertEquals("GrandLuffy", gc.getLastName());
        Assert.assertEquals("GrandChild", gc.getGrandChildName());
        Integer id = 1;
        Assert.assertEquals(id.intValue(), gc.getId().intValue());
    }

    @Test(dependsOnMethods = { "TestFind" })
    public void TestUpdate() {
        GrandChild gc = new GrandChild().find(1);
        Assert.assertEquals("GrandChild", gc.getGrandChildName());
        Assert.assertEquals("grandMonkey", gc.getChildName());
        Assert.assertEquals("GrandD", gc.getFirstName());
        Assert.assertEquals("GrandLuffy", gc.getLastName());
        gc.setGrandChildName("d");
        gc.setChildName("a");
        gc.setFirstName("b");
        gc.setLastName("c");
        Assert.assertTrue(gc.update());
        Integer id = 1;
        Assert.assertEquals(id.intValue(), gc.getId().intValue());

    }

    @Test(dependsOnMethods = { "TestUpdate" })
    public void TestDestroy() {
        GrandChild gc = new GrandChild().find(1);
        gc.setGrandChildName("d");
        gc.setChildName("a");
        gc.setFirstName("b");
        gc.setLastName("c");
        gc.update();
       
        Assert.assertTrue(gc.destroy());
        Assert.assertNull(gc.getId());
    }

}
