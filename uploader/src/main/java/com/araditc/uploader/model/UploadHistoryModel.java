package com.araditc.uploader.model;


import androidx.lifecycle.MutableLiveData;

import com.araditc.uploader.data.api.ApiInterface;
import com.araditc.uploader.data.databse.UploadHistoryDAO;
import com.araditc.uploader.interfaces.UploadHistoryContract;
import com.araditc.uploader.struct.UploadHistoryDbStruct;
import com.araditc.uploader.struct.UploadHistoryStruct;

import java.util.List;

public class UploadHistoryModel implements UploadHistoryContract.Model {
    private UploadHistoryDAO uploadHistoryDAO;
    private ApiInterface apiInterface;

    public UploadHistoryModel(ApiInterface apiInterface, UploadHistoryDAO uploadHistoryDAO) {
        this.uploadHistoryDAO = uploadHistoryDAO;
        this.apiInterface = apiInterface;
    }

    @Override
    public void saveUploadHistory(String fileId, int totalSequenceNumber, int status,
                                  UploadHistoryDAO.TransActionResult transActionResult) {
        UploadHistoryDbStruct uploadHistoryDbStruct = new UploadHistoryDbStruct();
        uploadHistoryDbStruct.setCreatedAt(System.currentTimeMillis());
        uploadHistoryDbStruct.setFileId(String.valueOf(fileId));
        uploadHistoryDbStruct.setTotalSequenceNumber(totalSequenceNumber);
        uploadHistoryDbStruct.setStatus(status);

        uploadHistoryDAO.save(uploadHistoryDbStruct, transActionResult);
    }

    @Override
    public void updateUploadHistory(String fileId, int status,
                                    UploadHistoryDAO.TransActionResult transActionListResult) {
        uploadHistoryDAO.update(fileId, status, transActionListResult);
    }

  @Override
    public void delete(String fileId) {
        uploadHistoryDAO.delete(fileId,null);
    }

    @Override
    public void findHistoryByUploadId(String fileId,
                                      UploadHistoryDAO.TransActionResult transActionListResult) {
        uploadHistoryDAO.findByFileId(fileId, transActionListResult);
    }

    @Override
    public List<UploadHistoryStruct> fetchAllHistory() {
       return UploadHistoryStruct.convert(uploadHistoryDAO.findAll());
    }

    @Override
    public MutableLiveData<List<UploadHistoryStruct>> getUploadHistoryMutableLiveData() {
        return uploadHistoryDAO.getUploadHistoryMutableLiveData();
    }
}
