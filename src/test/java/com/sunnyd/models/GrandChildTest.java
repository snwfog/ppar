package com.sunnyd.models;

import java.sql.SQLException;
import java.util.HashMap;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.sunnyd.annotations.inherit;
import com.sunnyd.annotations.tableAttr;
import com.sunnyd.database.fixtures.Prep;

@inherit(childClassof = "ChildTest")
public class GrandChildTest extends ChildTest {
    public static final String tableName = "grand_childs";
    public static final String parentTableName = "childs";
    public static final String grandParentTableName = "persons";

    @tableAttr
    private String grandChildName;

    public GrandChildTest() {
        super();
    }

    public GrandChildTest(HashMap<String, Object> HM) {
        super(HM);
    }

    public String getGrandChildName() {
        return grandChildName;
    }

    public void setGrandChildName(String grandChildName) {
        this.grandChildName = grandChildName;
        setUpdateFlag(true);
    }

    /****************************** TEST ********************************************************/

    private static final boolean purgeExistingRecord = true;

    @BeforeClass
    public void init() throws SQLException {
        Prep.init(tableName);
    }

    public void prepTable() throws SQLException {
        Prep.purgeAllRecord(tableName);
        Prep.purgeAllRecord(parentTableName);
        Prep.purgeAllRecord(grandParentTableName);
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
        GrandChildTest gc = new GrandChildTest();
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
        GrandChildTest gc = GrandChildTest.find(1);
        Assert.assertEquals("grandMonkey", gc.getChildName());
        Assert.assertEquals("GrandD", gc.getFirstName());
        Assert.assertEquals("GrandLuffy", gc.getLastName());
        Assert.assertEquals("GrandChild", gc.getGrandChildName());
        Integer id = 1;
        Assert.assertEquals(id.intValue(), gc.getId().intValue());
    }

    @Test
    public void TestUpdate() {
        GrandChildTest gc = GrandChildTest.find(1);
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

    @Test
    public void TestDestroy() {
        GrandChildTest gc = GrandChildTest.find(1);
        gc.setGrandChildName("d");
        gc.setChildName("a");
        gc.setFirstName("b");
        gc.setLastName("c");
        gc.update();
       
        Assert.assertTrue(gc.Destroy());
        Assert.assertNull(gc.getId());
        Assert.assertNull(gc.getCreationDate());
        Assert.assertNull(gc.getLastModifiedDate());
        Assert.assertNull(gc.getGrandChildName());
        Assert.assertNull(gc.getChildName());
        Assert.assertNull(gc.getFirstName());
        Assert.assertNull(gc.getLastName());
        Assert.assertNull(gc.getStatus());
        Assert.assertTrue(gc.getUpdateFlag());
        //Integer id = 1;
        //Assert.assertEquals(id.intValue(), gc.getId().intValue());
    }

}
