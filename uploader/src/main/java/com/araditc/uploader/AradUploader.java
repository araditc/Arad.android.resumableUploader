package com.araditc.uploader;

import android.content.Intent;
import android.os.Build;

import androidx.work.WorkManager;

import com.araditc.uploader.constants.Phrase;
import com.araditc.uploader.constants.UploadHistoryTypes;
import com.araditc.uploader.data.api.ApiClient;
import com.araditc.uploader.data.databse.UploadDAO;
import com.araditc.uploader.data.databse.UploadHistoryDAO;
import com.araditc.uploader.jobs.UploadWorker;
import com.araditc.uploader.model.UploadHistoryModel;
import com.araditc.uploader.service.UploadService;
import com.araditc.uploader.struct.UploadHistoryStruct;
import com.araditc.uploader.struct.UploadStruct;

import java.util.List;
import java.util.UUID;

public class AradUploader {
    private UploadDAO uploadDAO;
    private static AradUploader aradUploader;
    private static UploadHistoryModel uploadHistoryModel;

    private AradUploader() {
        uploadDAO = new UploadDAO();
    }

    public static AradUploader newInstance() {

        if (aradUploader == null)
            aradUploader = new AradUploader();

        return aradUploader;
    }


    public void setBaseUrl(String baseUrl) {
        Phrase.BASE_URL = baseUrl;
    }

    public void setDownloadBaseUrl(String downloadBaseUrl) {
        Phrase.BASE_DOWNLOAD_URL = downloadBaseUrl;
    }

    public void upload(String token, String filePath, int type, int uploadId) {
        uploadHistoryModel = new UploadHistoryModel(ApiClient.GetClient(token)
                , new UploadHistoryDAO());

        Intent intent = new Intent();
        intent.setClass(AppConfig.newInstance().application, UploadService.class);
        intent.putExtra(UploadService.TOKEN_KEY, token);
        intent.putExtra(UploadService.UPLOAD_ID_KEY, uploadId);
        intent.putExtra(UploadService.FILE_PATH_KEY, filePath);
        intent.putExtra(UploadService.TYPE_KEY, type);

        uploadHistoryModel.findHistoryByUploadId(uploadId + "", new UploadHistoryDAO.TransActionResult() {
            @Override
            public void onSuccess(UploadHistoryStruct uploadHistoryStruct) {
                if (uploadHistoryStruct.getStatus() == UploadHistoryTypes.IS_COMPLETED) {
                    startUploadService();
                } else {
                    setInterruptedChunk();
                }
            }

            @Override
            public void onError(Throwable error) {
                startUploadService();
            }

            private void startUploadService() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    AppConfig.application.startForegroundService(intent);
                } else {
                    AppConfig.application.startService(intent);
                }

            }

            private void setInterruptedChunk() {
                uploadDAO.findChunks(uploadId + "", new UploadDAO.TransActionListResult() {
                    @Override
                    public void onSuccess(List<UploadStruct> structList) {
                        if (structList != null && structList.size() > 0) {
                            int[] indexList = new int[structList.size()];
                            for (int i = 0; i < structList.size(); i++) {
                                indexList[i] = Integer.parseInt(structList.get(i).getSequenceNumber());
                            }

                            intent.putExtra(UploadService.FAILED_CHUNK_KEY, indexList);

                        }

                        startUploadService();
                    }

                    @Override
                    public void onError(Throwable error) {
                        startUploadService();
                    }
                });

            }
        });
    }

    public static void setProgressUploadListener(UploadWorker.UploadResult progressUploadListener) {
        UploadService.setProgressUploadListener(progressUploadListener);
    }

    public static void unRegisterProgressUploadListener() {
        UploadService.unRegisterProgressUploadListener();

    }

    public static void cancelNotification(UUID cancelId) {
        WorkManager.getInstance().cancelWorkById(cancelId);
    }
}
