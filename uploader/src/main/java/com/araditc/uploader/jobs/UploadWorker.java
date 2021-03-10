package com.araditc.uploader.jobs;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.araditc.uploader.AppConfig;
import com.araditc.uploader.constants.Phrase;
import com.araditc.uploader.constants.UploadHistoryTypes;
import com.araditc.uploader.constants.UploadTypes;
import com.araditc.uploader.data.api.ApiClient;
import com.araditc.uploader.data.databse.UploadDAO;
import com.araditc.uploader.data.databse.UploadHistoryDAO;
import com.araditc.uploader.model.UploadHistoryModel;
import com.araditc.uploader.model.UploadModel;
import com.araditc.uploader.service.UploadService;
import com.araditc.uploader.struct.UploadHistoryStruct;
import com.araditc.uploader.struct.responseStruct.MediaResponse;
import com.araditc.uploader.utils.FileUtils;
import com.araditc.uploader.utils.ImageUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class UploadWorker extends Worker {
    public static String FILE_ID = "file_id";
    public static String FILE_PATH = "file_path";
    public static String FILE_TYPE = "file_type";
    public static String FAIL_CHUNK_ARRAY = "fail_chunk_array_sequence_number";
    private final List<Integer> interruptedChunkList = new ArrayList<>();
    private Integer startChunk = -1;
    private static List<UploadResult> uploadResult = new ArrayList<>();
    private int progressValue;
    private UploadModel uploadModel;
    private UploadHistoryModel uploadHistoryModel;

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        progressValue = 0;
    }

    @NonNull
    @Override
    public Result doWork() {
        AppConfig.newInstance().configRealm(getApplicationContext());

        uploadModel = new UploadModel(ApiClient.GetClient(getInputData().getString(UploadService.TOKEN_KEY), 5, 5, 5)
                , new UploadDAO());

        uploadHistoryModel = new UploadHistoryModel(ApiClient.GetClient(getInputData().getString(UploadService.TOKEN_KEY))
                , new UploadHistoryDAO());

        return upload(getInputData().getString(FILE_PATH), getInputData().getInt(FILE_ID, -1));
    }

    @Override
    public void onStopped() {
        uploadHistoryModel=null;
        uploadModel=null;

        super.onStopped();
    }

    private Result upload(String filePath, int fileId) {

        final int cSize = 512 * 1024; // size of chunk
        File file = new File(filePath);
        final long pieces = file.length();
        final int cTotalCount;


        if ((double) pieces % (double) cSize > 0)
            cTotalCount = (int) (((double) pieces / (double) cSize) + 1);
        else
            cTotalCount = (int) ((double) pieces / (double) cSize);


        setPreviousFailChunk(cTotalCount);

        try {

            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));

            //skip start index
            int startPosition = 0;

            if (startChunk != -1) {
                stream.skip(cSize * (startChunk + 1));
                startPosition = startChunk + 1;
            }

            for (int i = startPosition; i < cTotalCount; i++) {
                byte[] buffer = new byte[cSize];

                int readCount = stream.read(buffer);
                if (readCount == -1) {
                    break;
                } else if (readCount < cSize) {
                    byte[] remindByte = new byte[readCount];

                    for (int k = 0; k < readCount; k++)
                        remindByte[k] = buffer[k];

                    buffer = remindByte;
                }

                Response<ResponseBody> bodyResponse = uploadModel.uploadChunkPart(buffer, i, fileId,
                        cTotalCount, ImageUtils.getTypePath(filePath));

                if (bodyResponse != null && bodyResponse.isSuccessful()) {
                    int finalI = i;
                    uploadModel.saveChunk(fileId, finalI, null);
                    increaseProgressValue();
                    returnProgress(finalI, cTotalCount, getProgressValue(),
                            (int) (((float) getProgressValue() / (float) cTotalCount) * 100f));
                } else {
                    interruptedChunkList.add(i);
                    returnErrorProgress(i, cTotalCount, getProgressValue(), (int) (((float) getProgressValue() / (float) cTotalCount) * 100f));
                }
            }


            for (int j = 0; j < interruptedChunkList.size(); j++) {

                byte[] chunkArrayBySequenceNumber = findChunkArrayBySequenceNumber(interruptedChunkList.get(j), cSize, file);

                Response<ResponseBody> uploadChunkPart = uploadModel.uploadChunkPart(chunkArrayBySequenceNumber,
                        interruptedChunkList.get(j),
                        fileId,
                        cTotalCount,
                        ImageUtils.getTypePath(filePath));

                if (uploadChunkPart == null || !uploadChunkPart.isSuccessful()) {
                    uploadHistoryModel.delete(getInputData().getInt(FILE_ID, -1) + "");

                    return Result.failure();

                } else {
                    uploadModel.saveChunk(fileId, interruptedChunkList.get(j), null);
                    increaseProgressValue();
                    returnProgress(interruptedChunkList.get(j), cTotalCount, getProgressValue(),
                            (int) (((float) getProgressValue() / (float) cTotalCount) * 100f));
                }
            }


            Response<ResponseBody> response = uploadModel.completeUpload(Phrase.COMPLETE_UPLOAD_END_POINT
                    , fileId + "", cTotalCount + "", FileUtils.getMD5Checksum(filePath));

            if (response != null && response.isSuccessful()) {
                UploadResult listener = new UploadResult() {
                    @Override
                    public void onUploadComplete(int fileId, MediaResponse mediaResponse) {
                        broadCastCompleteResult(fileId, mediaResponse);
                    }

                    @Override
                    public void onUploadProgress(int index, int fileId, int total, int value, int percent) {
                        broadCastProgressResult(index, fileId, total, value, percent);
                    }

                    @Override
                    public void onErrorUploadProgress(int index, int fileId, int total, int value, int percent) {
                        broadCastErrorProgressResult(index, fileId, total, value, percent);

                    }

                    @Override
                    public void onUploadFailed(String message) {
                        broadCastFailResult(message);
                    }
                };

                uploadHistoryModel.updateUploadHistory(getInputData().getInt(FILE_ID, -1) + ""
                        , UploadHistoryTypes.IS_COMPLETED, new UploadHistoryDAO.TransActionResult() {
                            @Override
                            public void onSuccess(UploadHistoryStruct uploadHistoryStruct) {
                                switch (getInputData().getInt(FILE_TYPE, -1)) {
                                    case UploadTypes.IMAGE:
                                        uploadModel.getImageUrl(listener, fileId);
                                        break;

                                    case UploadTypes.DOCUMENT:
                                        uploadModel.getDocumentUrl(listener, fileId);
                                        break;

                                    case UploadTypes.VIDEO:
                                        uploadModel.getVideoUrl(listener, fileId);
                                        break;

                                    case UploadTypes.AUDIO:
                                        uploadModel.getAudioUrl(listener, fileId);
                                        break;

                                    case UploadTypes.PROFILE:
                                        uploadModel.getProfileUrl(listener, fileId);
                                        break;
                                }

                            }

                            @Override
                            public void onError(Throwable error) {

                            }
                        });

                return Result.success();

            } else {

//                uploadHistoryModel.updateUploadHistory(getInputData().getInt(FILE_ID, -1) + ""
//                        , UploadHistoryTypes.NOT_COMPLETED, null);

                int anInt = getInputData().getInt(FILE_ID, -1);
                uploadHistoryModel.delete(getInputData().getInt(FILE_ID, -1) + "");
                broadCastFailResult("");

                return Result.failure();

            }

        } catch (IOException ignored) {
            uploadHistoryModel.updateUploadHistory(getInputData().getInt(FILE_ID, -1) + ""
                    , UploadHistoryTypes.NOT_COMPLETED, null);
            broadCastFailResult("");

            return Result.failure();
        }


    }

    private void broadCastCompleteResult(int fileId, MediaResponse mediaResponse) {
        if (uploadResult == null || uploadResult.size() == 0) return;

        for (UploadResult listener : uploadResult) {
            listener.onUploadComplete(fileId, mediaResponse);
        }
    }

    private void broadCastFailResult(String message) {
        if (uploadResult == null || uploadResult.size() == 0) return;

        for (UploadResult listener : uploadResult) {
            listener.onUploadFailed(message);
        }
    }

    private void broadCastProgressResult(int index, int fileId, int total, int value, int percent) {
        if (uploadResult == null || uploadResult.size() == 0) return;

        for (UploadResult listener : uploadResult) {
            listener.onUploadProgress(index, fileId, total, value, percent);
        }
    }

    private void broadCastErrorProgressResult(int index, int fileId, int total, int value, int percent) {
        if (uploadResult == null || uploadResult.size() == 0) return;

        for (UploadResult listener : uploadResult) {
            listener.onErrorUploadProgress(index, fileId, total, value, percent);
        }
    }

    private void returnProgress(int index, int cTotalCount, int progressValue, int percent) {
        if (uploadResult != null)
            broadCastProgressResult(index, getInputData().getInt(FILE_ID, -1), cTotalCount, getProgressValue(), percent);
    }

    private void returnErrorProgress(int index, int cTotalCount, int progressValue, int percent) {
        if (uploadResult != null)
            broadCastErrorProgressResult(index, getInputData().getInt(FILE_ID, -1), cTotalCount, getProgressValue(), percent);
    }

    //set fail chunk upload in interrupted list
    private void setPreviousFailChunk(int totalSize) {
        int[] failChunkArray = getInputData().getIntArray(FAIL_CHUNK_ARRAY);

        if (failChunkArray != null) {

            for (int i = 0; i < totalSize; i++) {
                if (failChunkArray.length > i && failChunkArray[i] > startChunk) {
                    startChunk = failChunkArray[i];
                }
            }

            for (int k = 0; k < totalSize; k++) {
                if (k >= startChunk) break;

                if (!findItem(failChunkArray, k)) {
                    interruptedChunkList.add(k);
                } else {
                    increaseProgressValue();
                    returnProgress(k, totalSize, getProgressValue(), (int) (((float) getProgressValue() / (float) totalSize) * 100f));
                }
            }

        }
    }

    public synchronized int increaseProgressValue() {
        return ++progressValue;
    }

    public synchronized int getProgressValue() {
        return progressValue;
    }

    public synchronized void setProgressValue(int progressValue) {
        this.progressValue = progressValue;
    }

    private static boolean findItem(int[] arr, int toCheckValue) {
        boolean isValid = false;
        for (int element : arr) {
            if (element == toCheckValue) {
                isValid = true;
                break;
            }
        }

        return isValid;
    }

    private byte[] findChunkArrayBySequenceNumber(int number, int cSize, File file) {
        try {
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[cSize];

            stream.skip(number * cSize);
            stream.read(buffer);

            return buffer;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String getMD5EncryptedString(String encTarget) {
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception while encrypting to md5");
            e.printStackTrace();
        } // Encryption algorithm
        mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while (md5.length() < 32) {
            md5 = "0" + md5;
        }
        return md5;
    }

    public interface UploadResult {
        void onUploadComplete(int fileId, MediaResponse mediaResponse);

        void onUploadProgress(int index, int fileId, int total, int value, int percent);

        void onErrorUploadProgress(int index, int fileId, int total, int value, int percent);

        void onUploadFailed(String message);
    }

    public static void addUploadResultListener(UploadResult listener) {
        if (uploadResult != null)
            uploadResult.add(listener);
    }

    public static void removeUploadResultListener(UploadResult listener) {
        if (uploadResult != null && uploadResult.size() > 0)
            uploadResult.remove(listener);
    }
}
