package com.araditc.uploader.struct;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmModule;

public class AradUploadDbStruct extends RealmObject {
    @PrimaryKey
    private int id;

    private String fileId;

    private String sequenceNumber;

    private int status;

    private long createdAt;

    private long updatedAt;

    public AradUploadDbStruct() {
    }

    public AradUploadDbStruct(AradUploadDbStruct uploadDbStruct) {
        this.id = uploadDbStruct.getId();
        this.fileId = uploadDbStruct.getFileId();
        this.sequenceNumber = uploadDbStruct.getSequenceNumber();
        this.status = uploadDbStruct.getStatus();
        this.createdAt = uploadDbStruct.getCreatedAt();
        this.updatedAt = uploadDbStruct.getUpdatedAt();
    }

    public void init(AradUploadDbStruct uploadDbStruct) {
        this.fileId = uploadDbStruct.getFileId();
        this.sequenceNumber = uploadDbStruct.getSequenceNumber();
        this.status = uploadDbStruct.getStatus();
        this.createdAt = uploadDbStruct.getCreatedAt();
        this.updatedAt = uploadDbStruct.getUpdatedAt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
