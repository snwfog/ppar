package com.sunnyd.models;

import java.util.Date;
import java.util.HashMap;
import com.sunnyd.Base;
import com.sunnyd.IModel;
import com.sunnyd.annotations.hasOne;
import com.sunnyd.annotations.tableAttr;
import com.sunnyd.database.Manager;

public class Document extends Base implements IModel {
    public static final String tableName = "documents";

    @tableAttr
    private String docName;
    @tableAttr
    private String thumbnailPath;
    @tableAttr
    private Date lastModifiedDate;
    @tableAttr
    private Date creationDate;
    @hasOne
    private Peer peer;
    @tableAttr
    private Integer peerId;
    
    
    public Document() {
        super();
    }

    public Document(HashMap<String, Object> HM) {
        super(HM);
    }

    public String getDocName() {
        return docName;
    }

    public Peer getPeer(){
        HashMap<String, Object> foundPeer = Manager.find(peerId, "peers");
        peer = new Peer(foundPeer);
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
        d.setPeerId(3); // why this setPeerId is not working @mike?
        System.out.println(d.save());
        
//        Document d = Document.find(4);
        System.out.println(d.getPeer().getFirstName());
        System.out.println(d.getPeer().getLastName());
        System.out.println(d.getPeer().getCreationDate());
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

}
