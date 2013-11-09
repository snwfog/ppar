package com.sunnyd.models;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    
    public static final String tableName = "document_tests";

    @ActiveRecordField
    private String docName;
    @ActiveRecordField
    private String thumbnailPath;
    @ActiveRecordField
    private Date lastModifiedDate;
    @ActiveRecordField
    private Date creationDate;
    @ActiveRelationHasOne
    private PeerTest peer;
    @ActiveRecordField
    private Integer peerTestId;
    
    
    public DocumentTest() {
        super();
    }

    public DocumentTest(HashMap<String, Object> HM) {
        super(HM);
    }

    public String getDocName() {
        return docName;
    }

    public PeerTest getPeer(){
        initRelation("peer");
        return peer;
    }
    
    public void setPeerTestId(Integer peerId){
        this.peerTestId = peerId;
        setUpdateFlag(true);
    }
    
    public int getPeerTestId(){
        return this.peerTestId;
    }
    
    public void setDocName(String docName) {
        this.docName = docName;
        setUpdateFlag(true);
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
        setUpdateFlag(true);
    }
    
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
        setUpdateFlag(true);
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        setUpdateFlag(true);
    }
    
    /****************************** TEST ********************************************************/

    private static final boolean purgeExistingRecord = true;

    @BeforeClass
    public void init() throws SQLException {
        Prep.init(tableName);
    }
    
    public void prepTable() throws SQLException {
        Prep.purgeAllRecord("peer_tests", false);
        Prep.resetPrimaryKey("peer_tests");    
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
        DocumentTest d = new DocumentTest();
        Assert.assertNull(d.getId());
        Assert.assertNull(d.getCreationDate());
        Assert.assertNull(d.getLastModifiedDate());
        Assert.assertNull(d.getDocName());
        Assert.assertNull(d.getPeer());
        Assert.assertFalse(d.getUpdateFlag());
        d.setDocName("footb");
        Assert.assertTrue(d.save());
        Integer id = 1;
        Assert.assertEquals(d.getId().intValue(), id.intValue());
       
    }
    
   
    @Test (dependsOnMethods = { "TestSave" })
    public static void TestFind(){
        DocumentTest d = new DocumentTest().find(1);
        Assert.assertEquals("footb", d.getDocName());
       // Assert.assertEquals("a", d.getPeer().getFirstName());
        //Assert.assertEquals("aiodjoadjoia", a.getDocuments());
        Integer id = 1;
        Assert.assertEquals(id.intValue(), d.getId().intValue());

    }

    
    

}
