package com.sunnyd.models;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.sunnyd.Base;
import com.sunnyd.IModel;
import com.sunnyd.annotations.ActiveRelationHasMany;
import com.sunnyd.annotations.ActiveRelationHasOne;
import com.sunnyd.annotations.ActiveRecordField;
import com.sunnyd.database.Manager;

public class Document extends Base implements IModel {
    public static final String tableName = "documents";

    @ActiveRecordField
    private String docName;
    @ActiveRecordField
    private String thumbnailPath;
    @ActiveRecordField
    private Date lastModifiedDate;
    @ActiveRecordField
    private Date creationDate;
    @ActiveRelationHasOne
    private Peer peer;
    @ActiveRecordField
    private Integer peerId;
    @ActiveRecordField
    private String docType;


    public Document() {
        super();
    }

    public Document(Map<String, Object> HM) {
        super(HM);
    }

    public String getDocName() {
        return docName;
    }

    public Peer getPeer(){
        if(peer == null){
            HashMap<String, Object> foundPeer = (HashMap<String, Object> ) Manager.find(peerId, "peers");
            this.peer = new Peer(foundPeer);
        }
        return peer;
    }

    public void setPeerId(Integer peerId){
        this.peerId = peerId;
    }

    public int getPeerId(){
        return this.peerId;
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

    public static void main(String[] args) {
        Document d = new Document();
        d.setDocName("mydoc");
        d.setPeerId(1); // why this setPeerId is not working @mike?
        System.out.println(d.save());

//        Document d = Document.find(4);
        System.out.println(d.getPeer().getFirstName());
        System.out.println(d.getPeer().getLastName());
        //System.out.println(d.getPeer().getCreationDate());
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


    public String getDocType() {
        return docType;
    }

    public void setDocType(String type) {
        if(type.toLowerCase().trim().contentEquals("resume") | type.toLowerCase().trim().contentEquals("coverLetter")){
            this.docType = type;
            setUpdateFlag(true);
        }
    }


}