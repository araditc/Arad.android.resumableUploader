package com.araditc.uploader.interfaces;

import androidx.lifecycle.MutableLiveData;

import com.araditc.uploader.data.databse.UploadHistoryDAO;
import com.araditc.uploader.struct.UploadHistoryStruct;

import java.util.List;

public interface UploadHistoryContract {


    interface Model {

        void saveUploadHistory(String fileId, int totalSequenceNumber, int status, UploadHistoryDAO.TransActionResult transActionResult);

        void updateUploadHistory(String fileId, int status, UploadHistoryDAO.TransActionResult transActionResult);

        void findHistoryByUploadId(String fileId,
                                   UploadHistoryDAO.TransActionResult transActionListResult);

        List<UploadHistoryStruct> fetchAllHistory();

        MutableLiveData<List<UploadHistoryStruct>> getUploadHistoryMutableLiveData();
    }
}