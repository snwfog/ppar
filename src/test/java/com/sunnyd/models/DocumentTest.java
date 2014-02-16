package com.sunnyd.models;

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
import com.sunnyd.database.Manager;
import com.sunnyd.database.fixtures.Prep;

public class DocumentTest extends Base implements IModel{
    
    public static final String tableName = "documents";
    
    /****************************** TEST ********************************************************/

    private static final boolean purgeExistingRecord = true;

    @BeforeClass
    public void init() throws SQLException {
        Prep.init(tableName);
        Prep.init("peers");
        
    }
    
    public void prepTable() throws SQLException {
        Prep.purgeAllRecord(tableName, false);
        Prep.resetPrimaryKey(tableName, false);
        Prep.purgeAllRecord("peers", false);
        Prep.resetPrimaryKey("peers", false);


    

    }

    @Test
    public void TestSave() {
        try {
            prepTable();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Document d = new Document();
        Assert.assertNull(d.getId());
        Assert.assertNull(d.getCreationDate());
        Assert.assertNull(d.getLastModifiedDate());
        Assert.assertNull(d.getDocName());
        Assert.assertNull(d.getPeer());
        Assert.assertFalse(d.getUpdateFlag());
        d.setDocName("footb");
        Peer p = new Peer();
        p.setFirstName("wais");
        Assert.assertTrue(p.save());
        d.setPeerId(p.getId());
        Assert.assertTrue(d.save());
        Integer id = 1;
        Assert.assertEquals(d.getId().intValue(), id.intValue());
       
    }
    
   
    @Test (dependsOnMethods = { "TestSave" })
    public static void TestFind(){
        Document d = new Document().find(1);
        Assert.assertEquals("footb", d.getDocName());
        Assert.assertEquals("wais", d.getPeer().getFirstName());
        Assert.assertEquals("wais", d.getPb().getFirstName());
        //Assert.assertEquals("aiodjoadjoia", a.getDocuments());
        Integer id = 1;
        Assert.assertEquals(id.intValue(), d.getId().intValue());

    }

    
    

}
