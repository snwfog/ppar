package com.sunnyd.models;

import com.sunnyd.Base;
import com.sunnyd.IModel;
import com.sunnyd.annotations.*;

import java.util.HashMap;

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
    
    public static void main (String[] args){
        Peer p = new Peer();
        p.setFirstName("asoidjasoidjaisjdioj");
        p.setLastName("Zhang");
        p.setEmail("mail");
        p.setPoint(0);
        System.out.println(p.save());
    }

}
