package com.sunnyd.models;

import java.util.Date;
import java.util.HashMap;

import com.sunnyd.Base;
import com.sunnyd.IModel;
import com.sunnyd.annotations.tableAttr;

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
    @tableAttr
    private Integer peerId;
    
    public Document(){
        super();
    }
    
    public Document(HashMap<String, Object> HM) {
        super(HM);
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }
    
   

    public String getThumbnailPath() {
        return thumbnailPath;
    }
    
    public Integer getPeerId(){
	return peerId;
    }

    public void setPeerId(Integer peerId){
    this.peerId = peerId;
    }
    
    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public static void main(String[] args) {
        Document d = Document.find(1);
        System.out.println(d.getDocName());
        System.out.println(d.getLastModifiedDate());
        System.out.println(d.getThumbnailPath());
        System.out.println(d.getCreationDate());
    }

    public Date getLastModifiedDate() {
	return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
	this.lastModifiedDate = lastModifiedDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
