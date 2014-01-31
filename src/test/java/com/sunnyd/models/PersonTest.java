package com.sunnyd.models;

import com.sunnyd.Base;
import com.sunnyd.IModel;
import com.sunnyd.database.fixtures.Prep;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.Date;

public class PersonTest extends Base implements IModel {

    public static final String tableName = "persons";

    /****************************** TEST ********************************************************/

    private static final boolean purgeExistingRecord = true;

    @BeforeClass
    public void init() throws SQLException {
        Prep.init(tableName);
    }

    public void prepTable() throws SQLException {

        Prep.purgeAllRecord("grand_childs", false);
        Prep.resetPrimaryKey("grand_childs", false);
        Prep.purgeAllRecord("childs", false);
        Prep.resetPrimaryKey("childs", false);
        Prep.purgeAllRecord(tableName, false);
        Prep.resetPrimaryKey(tableName, false);
    }

    @Test
    public void TestSave() {
        try {
            prepTable();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Person a = new Person();
        Assert.assertNull(a.getId());
        Assert.assertNull(a.getCreationDate());
        Assert.assertNull(a.getLastModifiedDate());
        Assert.assertNull(a.getFirstName());
        Assert.assertNull(a.getLastName());
        Assert.assertEquals(a.getStatus(), "defaultStatus");
        Assert.assertFalse(a.getUpdateFlag());
        a.setStatus("aoidjaoidja");
        a.setFirstName("a");
        a.setLastName("b");
        a.setStatus("aisdjoaijd");
        Assert.assertTrue(a.save());
        Integer id = 1;
        Assert.assertEquals(a.getId().intValue(), id.intValue());

    }

    @Test(dependsOnMethods = { "TestSave" })
    public void TestFind() {
        Person a = new Person().find(1);
        Date today = new Date();
        Assert.assertEquals(a.getStatus(), "defaultStatus");
        Assert.assertEquals("a", a.getFirstName());
        Assert.assertEquals("b", a.getLastName());
        Integer id = 1;
        Assert.assertEquals(id.intValue(), a.getId().intValue());

    }

    @Test(dependsOnMethods = { "TestFind" })
    public void TestUpdate() {
        Person p = new Person().find(1);
        Assert.assertEquals("a", p.getFirstName());
        Assert.assertEquals("b", p.getLastName());
        p.setFirstName("john");
        p.setLastName("malkovich");
        Assert.assertTrue(p.update());
        Integer id = 1;
        Person updateP = new Person().find(1);
        Assert.assertEquals(id.intValue(), updateP.getId().intValue());
        Assert.assertEquals("john", updateP.getFirstName());

    }

    @Test(dependsOnMethods = { "TestUpdate" })
    public void TestDestroy() {
        Person p = new Person().find(1);
        p.setFirstName("b");
        p.setLastName("c");
        p.update();
        Assert.assertTrue(p.destroy());
        Assert.assertNull(p.getId());

    }

}
