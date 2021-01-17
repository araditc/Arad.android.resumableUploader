package com.araditc.uploader.struct;


import com.araditc.uploader.struct.responseStruct.MetaStruct;

import java.util.ArrayList;
import java.util.List;

public class UploadHistoryStruct extends MetaStruct {
    private int id;

    private String fileId;

    private int totalSequenceNumber;

    private int status;

    private long createdAt;

    private long updatedAt;

    public UploadHistoryStruct convert(UploadHistoryDbStruct uploadHistoryDbStruct) {
        this.id = uploadHistoryDbStruct.getId();
        this.fileId = uploadHistoryDbStruct.getFileId();
        this.totalSequenceNumber = uploadHistoryDbStruct.getTotalSequenceNumber();
        this.status = uploadHistoryDbStruct.getStatus();
        this.createdAt = uploadHistoryDbStruct.getCreatedAt();
        this.updatedAt = uploadHistoryDbStruct.getUpdatedAt();
        this.status = uploadHistoryDbStruct.getStatus();

        return this;
    }

    public static List<UploadHistoryStruct> convert(List<UploadHistoryDbStruct> uploadDbStructList) {
        List<UploadHistoryStruct> uploadHistoryStructs = new ArrayList<>();
        for (UploadHistoryDbStruct data : uploadDbStructList) {
            uploadHistoryStructs.add(new UploadHistoryStruct().convert(data));
        }

        return uploadHistoryStructs;
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


    public int getTotalSequenceNumber() {
        return totalSequenceNumber;
    }

    public void setTotalSequenceNumber(int totalSequenceNumber) {
        this.totalSequenceNumber = totalSequenceNumber;
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
