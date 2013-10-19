package com.sunnyd.models;

import com.sunnyd.database.fixtures.Prep;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.sunnyd.annotations.inherit;
import com.sunnyd.annotations.tableAttr;

import java.sql.SQLException;
import java.util.HashMap;

@inherit(childClassof = "PersonTest")
public class ChildTest extends PersonTest {
    public static final String tableName = "childs";
    public static final String parentTableName = "persons";

    @tableAttr
    private String childName;

    public ChildTest() {
        super();
    }

    public ChildTest(HashMap<String, Object> HM) {
        super(HM);
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
        setUpdateFlag(true);
    }

    @Override
    public boolean save() {
        if (childName != null && childName.isEmpty())
            return false;
        return super.save();
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
        Prep.resetPrimaryKey(tableName);
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
        ChildTest c = new ChildTest();
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
        ChildTest c = ChildTest.find(1);
        Assert.assertEquals("luffy", c.getChildName());
        Assert.assertEquals("monkey", c.getFirstName());
        Assert.assertEquals("d", c.getLastName());
        Integer id = 1;
        Assert.assertEquals(id.intValue(), c.getId().intValue());
    }

    @Test
    public void TestUpdate() {
        ChildTest c = ChildTest.find(1);
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
    
    @Test
    public void TestDestroy() {
        ChildTest c = ChildTest.find(1);
        c.setChildName("a");
        c.setFirstName("b");
        c.setLastName("c");
        c.update();
       
        Assert.assertTrue(c.Destroy());
        Assert.assertNull(c.getId());
        Assert.assertNull(c.getCreationDate());
        Assert.assertNull(c.getLastModifiedDate());
        Assert.assertNull(c.getChildName());
        Assert.assertNull(c.getFirstName());
        Assert.assertNull(c.getLastName());
        Assert.assertNull(c.getStatus());
        Assert.assertTrue(c.getUpdateFlag());
        //Integer id = 1;
        //Assert.assertEquals(id.intValue(), gc.getId().intValue());
    }

}
