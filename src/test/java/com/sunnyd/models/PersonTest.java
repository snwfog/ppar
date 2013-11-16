package com.sunnyd.models;

import com.sunnyd.Base;
import com.sunnyd.IModel;
import com.sunnyd.annotations.*;
import com.sunnyd.database.fixtures.Prep;
import com.sunnyd.models.Person;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

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
        Prep.resetPrimaryKey("grand_childs");
        Prep.purgeAllRecord("childs", false);
        Prep.resetPrimaryKey("childs");
        Prep.purgeAllRecord(tableName, false);
        Prep.resetPrimaryKey(tableName);
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
        Assert.assertNull(a.getStatus());
        Assert.assertFalse(a.getUpdateFlag());
        a.setStatus("aoidjaoidja");
        a.setFirstName("a");
        a.setLastName("b");
        Assert.assertTrue(a.save());
        Integer id = 1;
        Assert.assertEquals(a.getId().intValue(), id.intValue());

    }

    @Test(dependsOnMethods = { "TestSave" })
    public static void TestFind() {
        Person a = new Person().find(1);
        Date today = new Date();
        Assert.assertNull(a.getStatus());
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
        Assert.assertEquals(id.intValue(), p.getId().intValue());

    }

    @Test(dependsOnMethods = { "TestUpdate" })
    public void TestDestroy() {
        Person p = new Person().find(1);
        p.setFirstName("b");
        p.setLastName("c");
        p.update();

        Assert.assertTrue(p.Destroy());
        Assert.assertNull(p.getId());

    }

}
