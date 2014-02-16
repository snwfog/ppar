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
    
    @ActiveRelationHasOne
    private Peer Pb;
    
    @ActiveRecordField
    private Integer peerId;
    
    @ActiveRecordField
    private String docType;
    
    @ActiveRecordField
    private Integer pId;


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
        initRelation("peer");
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
        Document doc = new Document().find(1);
        System.out.println(doc.getPb());
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

    public Integer getpId() {
        return pId;
    }

    public void setpId(Integer pId) {
        this.pId = pId;
        setUpdateFlag(true);
    }

    public Peer getPb() {
        initRelation("Pb");
        return Pb;
    }

    public void setPb(Peer pb) {
        Pb = pb;
        setUpdateFlag(true);
    }


}