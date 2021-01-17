package com.araditc.uploader.interfaces;

import android.content.Intent;

import com.araditc.uploader.data.databse.UploadDAO;
import com.araditc.uploader.jobs.UploadWorker;

import okhttp3.ResponseBody;
import retrofit2.Response;

public interface UploadContract {

    interface Model {
        interface PrepareUploadIntent {
            void onUploadIntentReady(Intent intent);

            void onErrorPrepareUploadIntent(int message);
        }

        interface UploadResponse {
            void onProgressUpload(int uploadId, int progress, int percent, int total);

            void onUploadCompleted(int uploadId);

            void onErrorUpload(String message);
        }

        void saveChunk(int fileId, int sequenceNumber, UploadDAO.TransActionResult transActionResult);

        void findChunksByFileId(int fileId, int sequenceNumber, UploadDAO.TransActionListResult transActionListResult);

        interface fetchMediaInfo {
            void onMediaInfoReady(int uploadId, int progress, int percent, int total);

            void onErrorFetchMediaInfo(String message);
        }

        void getImageUrl(UploadWorker.UploadResult uploadResult, int fileId);

        void getVideoUrl(UploadWorker.UploadResult uploadResult, int fileId);

        void getAudioUrl(UploadWorker.UploadResult uploadResult, int fileId);

        void getDocumentUrl(UploadWorker.UploadResult uploadResult, int fileId);

        void getProfileUrl(UploadWorker.UploadResult uploadResult, int fileId);

        Response<ResponseBody> completeUpload(String fileUrl,
                                              String fileId,
                                              String total_file_parts,
                                              String checkSum);

        Response<ResponseBody> uploadChunkPart(byte[] avatar, int filePart, int fileId, int totalPart, String format);
    }
}