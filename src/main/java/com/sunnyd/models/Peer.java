package com.sunnyd.models;

import com.sunnyd.Base;
import static com.sunnyd.Base.find;
import com.sunnyd.IModel;
import com.sunnyd.annotations.*;
import com.sunnyd.database.Manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Peer extends Base implements IModel {
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
    private Integer rankId;

    @ActiveRecordField
    private String personalWebsite;

    @ActiveRelationHasMany
    private List<Document> documents;

    public Peer() {
        super();
    }

    public Peer(HashMap<String, Object> HM) {
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
    
    public void setDocuments(List<Document> documents){
        this.documents = documents;
    }

    public List<Document> getDocuments(){
        initRelation("documents");
        return this.documents;
//        HashMap<String, Object> condition = new HashMap<String, Object>();
//        condition.put("peerId", this.getId());
//        
//        ArrayList<HashMap<String, Object>> foundDocuments = Manager.findAll("documents", condition);
//        int size = foundDocuments.size();
//        documents = new Document[size-1];
//        
//        for (int i=0; i<size;i++){
//            Document d = new Document(foundDocuments.get(i));
//            documents[i] = d;
//        }
//        /return documents;
    }

    public static void main(String[] args) {
        Peer a = new Peer();
        a.setFirstName("lucas");
        System.out.println(Arrays.asList(a.getDocuments()).toString());
        Document d = new Document().find(2);
        List<Document> docArray = a.getDocuments();
        docArray.add(d);
        System.out.println(d);
        a.setDocuments(docArray);
        a.save();
    }

}
