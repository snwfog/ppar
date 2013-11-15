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
    
    public static final String tableName = "peer_tests";
    public static final String relatedTableName = "document_tests";

    @ActiveRecordField
    private String firstName;

    @ActiveRecordField
    private String lastName;

    @ActiveRecordField
    private String email;

    @ActiveRecordField
    private String userName;

    @ActiveRecordField
    private String password;

    @ActiveRecordField
    private Integer point = null;

    @ActiveRecordField
    private Integer rankId = null;

    @ActiveRecordField
    private String personalWebsite;

    @ActiveRelationHasMany
    private List<DocumentTest> documents;

    public PeerTest() {
        super();
    }

    public PeerTest(HashMap<String, Object> HM) {
        super(HM);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        setUpdateFlag(true);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        setUpdateFlag(true);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        setUpdateFlag(true);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        setUpdateFlag(true);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        setUpdateFlag(true);
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
        setUpdateFlag(true);
    }

    public Integer getRankId() {
        return rankId;
    }

    public void setRankId(Integer rankId) {
        this.rankId = rankId;
        setUpdateFlag(true);
    }

    public String getPersonalWebsite() {
        return personalWebsite;
    }

    public void setPersonalWebsite(String personalWebsite) {
        this.personalWebsite = personalWebsite;
        setUpdateFlag(true);
    }
    
    public void setDocuments(List<DocumentTest> documents){
        this.documents = documents;
    }

    public List<DocumentTest> getDocuments(){
        initRelation("documents");
        return this.documents;
    }

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
        PeerTest a = new PeerTest();
        Assert.assertNull(a.getId());
        Assert.assertNull(a.getCreationDate());
        Assert.assertNull(a.getLastModifiedDate());
        Assert.assertNull(a.getFirstName());
        Assert.assertNull(a.getLastName());
        
        Assert.assertFalse(a.getUpdateFlag());
        a.setFirstName("a");
        a.setLastName("b");
        DocumentTest d = new DocumentTest();
        d.setDocName("aiodjoadjoia");
        List<DocumentTest> docArray = a.getDocuments();
        docArray.add(d);
        a.setDocuments(docArray);
        Assert.assertTrue(d.save());
        Assert.assertTrue(a.save());
        Integer id = 1;
        Assert.assertEquals(a.getId().intValue(), id.intValue());
       
    }
    
   
    @Test (dependsOnMethods = { "TestSave" })
    public static void TestFind(){
        PeerTest a = new PeerTest().find(1);
        Assert.assertEquals("a", a.getFirstName());
        Assert.assertEquals("b", a.getLastName());
        //Assert.assertEquals("aiodjoadjoia", a.getDocuments());
        Integer id = 1;
        Assert.assertEquals(id.intValue(), a.getId().intValue());

    }
    
    @Test(dependsOnMethods = { "TestFind" })
    public void TestUpdate() {
        PeerTest p = new PeerTest().find(1);
        Assert.assertEquals("a", p.getFirstName());
        Assert.assertEquals("b", p.getLastName());
        //Assert.assertEquals(documents.toArray(), p.getDocuments());
        
        p.setFirstName("john");
        p.setLastName("malkovich");
        
        DocumentTest d = new DocumentTest().find(1);
        d.setDocName("bbbbbbb");
        List<DocumentTest> docArray = p.getDocuments();
        docArray.add(d);
        p.setDocuments(docArray);
        Assert.assertTrue(p.update());
        Integer id = 1;
        Assert.assertEquals(id.intValue(), p.getId().intValue());

    }
    
    @Test(dependsOnMethods = { "TestUpdate" })
    public void TestDestroy() {
        PeerTest p = new PeerTest().find(1);
        Assert.assertEquals("john", p.getFirstName());
        Assert.assertEquals("malkovich", p.getLastName());
       
        Assert.assertTrue(p.Destroy());
        Assert.assertNull(p.getId());
       // Assert.assertNull(p.getDocuments());
    }
    


}
