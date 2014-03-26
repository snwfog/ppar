package com.sunnyd.models;

import com.sunnyd.Base;
import com.sunnyd.annotations.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Peer extends Base {
    public static final String tableName = "peers";

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
    private Integer point;

    @ActiveRecordField
    private Integer rankId = null;
    
    @ActiveRecordField
    private Date dateOfBirth = null;

    @ActiveRecordField
    private String personalWebsite;

    @ActiveRelationHasMany
    private List<Document> documents;
    

    public Peer() {
        super();
    }

    public Peer(Map<String, Object> HM) {
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

    public String getPersonalWebsite() {
        return personalWebsite;
    }

    public void setPersonalWebsite(String personalWebsite) {
        this.personalWebsite = personalWebsite;
        setUpdateFlag(true);
    }

    public List<Document> getDocuments() {
        initRelation("documents");
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
    
    public static void main(String[] args) {
//        System.out.println(new Peer().findAll(null));
//        Peer a = new Peer();
//        a.setFirstName("fdf");
//        System.out.println(a.getRankId());
//        System.out.println(a.getDocuments());
//        
//        Document d = new Document();
//        d.setDocName("aiodjoadjoia");
//        List<Document> docArray = a.getDocuments();
//        docArray.add(d);
//        a.setDocuments(docArray);
//        d.save();
//        
//        
//        
//        a.save();
//        a.find(a.getId());
//        //a.destroy();
//        System.out.println(a.getFirstName());
//        a.destroy();
        
        Peer a = new Peer().find(1);
//        System.out.println(a.getBlubberFUK());
    }

    public Integer getRankId() {
        return rankId;
    }

    public void setRankId(Integer rankId) {
        this.rankId = rankId;
        setUpdateFlag(true);
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

}
