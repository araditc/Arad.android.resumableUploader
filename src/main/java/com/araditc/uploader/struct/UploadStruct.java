package com.araditc.uploader.struct;


import com.araditc.uploader.struct.responseStruct.MetaStruct;

import java.util.ArrayList;
import java.util.List;

public class UploadStruct extends MetaStruct {
    private int id;

    private String fileId;

    private String sequenceNumber;

    private int status;

    private long createdAt;

    private long updatedAt;

    public UploadStruct convert(AradUploadDbStruct uploadDbStruct) {
        this.id = uploadDbStruct.getId();
        this.fileId = uploadDbStruct.getFileId();
        this.sequenceNumber = uploadDbStruct.getSequenceNumber();
        this.status = uploadDbStruct.getStatus();
        this.createdAt = uploadDbStruct.getCreatedAt();
        this.updatedAt = uploadDbStruct.getUpdatedAt();
        this.status = uploadDbStruct.getStatus();

        return this;
    }

    public static List<UploadStruct> convert(List<AradUploadDbStruct> uploadDbStructList) {
        List<UploadStruct> uploadStructList=new ArrayList<>();
        for (AradUploadDbStruct data:uploadDbStructList){
         uploadStructList.add(new UploadStruct().convert(data));
        }

        return uploadStructList;
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
