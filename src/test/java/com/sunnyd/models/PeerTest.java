package com.sunnyd.models;

import com.sunnyd.Base;
import static com.sunnyd.Base.find;
import com.sunnyd.IModel;
import com.sunnyd.annotations.*;
import com.sunnyd.database.Manager;
import com.sunnyd.database.fixtures.Prep;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PeerTest extends Base implements IModel {
    
    public static final String tableName = "peers";
    public static final String relatedTableName = "documents";

    /****************************** TEST ********************************************************/

    private static final boolean purgeExistingRecord = true;

    @BeforeClass
    public void init() throws SQLException {
        Prep.init(tableName);
    }
    
    public void prepTable() throws SQLException {
        Prep.purgeAllRecord(relatedTableName, false);
        Prep.resetPrimaryKey(relatedTableName);    
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
        Peer a = new Peer();
        Assert.assertNull(a.getId());
        Assert.assertNull(a.getCreationDate());
        Assert.assertNull(a.getLastModifiedDate());
        Assert.assertNull(a.getFirstName());
        Assert.assertNull(a.getLastName());
        
        Assert.assertFalse(a.getUpdateFlag());
        a.setFirstName("a");
        a.setLastName("b");
        Document d = new Document();
        d.setDocName("aiodjoadjoia");
        ArrayList<Document> docArray = a.getDocuments();
        docArray.add(d);
        a.setDocuments(docArray);
        Assert.assertTrue(d.save());
        Assert.assertTrue(a.save());
        Integer id = 1;
        Assert.assertEquals(a.getId().intValue(), id.intValue());
       
    }
    
   
    @Test (dependsOnMethods = { "TestSave" })
    public static void TestFind(){
        Peer a = new Peer().find(1);
        Assert.assertEquals("a", a.getFirstName());
        Assert.assertEquals("b", a.getLastName());
        //Assert.assertEquals("aiodjoadjoia", a.getDocuments());
        Integer id = 1;
        Assert.assertEquals(id.intValue(), a.getId().intValue());

    }
    
    @Test(dependsOnMethods = { "TestFind" })
    public void TestUpdate() {
        Peer p = new Peer().find(1);
        Assert.assertEquals("a", p.getFirstName());
        Assert.assertEquals("b", p.getLastName());
        //Assert.assertEquals(documents.toArray(), p.getDocuments());
        
        p.setFirstName("john");
        p.setLastName("malkovich");
        
        Document d = new Document().find(1);
        d.setDocName("bbbbbbb");
        ArrayList<Document> docArray = p.getDocuments();
        docArray.add(d);
        p.setDocuments(docArray);
        Assert.assertTrue(p.update());
        Integer id = 1;
        Assert.assertEquals(id.intValue(), p.getId().intValue());

    }
    
    
    @Test(dependsOnMethods = { "TestUpdate" })
    public void TestDestroy() {
        Peer p = new Peer().find(1);
        Assert.assertEquals("john", p.getFirstName());
        Assert.assertEquals("malkovich", p.getLastName());
        Assert.assertTrue(p.Destroy());
        Assert.assertNull(p.getId());
       
       // Assert.assertNull(p.getDocuments());
    }
    


}
