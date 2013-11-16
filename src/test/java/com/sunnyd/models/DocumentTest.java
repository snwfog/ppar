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
    }
    
    public void prepTable() throws SQLException {
        Prep.purgeAllRecord("peers", true);
        Prep.resetPrimaryKey("peers");    
        Prep.purgeAllRecord(tableName, true);
        Prep.resetPrimaryKey(tableName);
    }

    @Test
    public void TestSave() {

        Document d = new Document();
        d.setDocName("footb");
        Assert.assertTrue(d.save());

        
    }
    
   
   /* @Test (dependsOnMethods = { "TestSave" })
    public static void TestFind(){
        Document d = new Document().find(1);
        Assert.assertEquals("footb", d.getDocName());
        Assert.assertEquals("wais", d.getPeer().getFirstName());
        //Assert.assertEquals("aiodjoadjoia", a.getDocuments());
        Integer id = 1;
        Assert.assertEquals(id.intValue(), d.getId().intValue());

    }

    */
    

}
