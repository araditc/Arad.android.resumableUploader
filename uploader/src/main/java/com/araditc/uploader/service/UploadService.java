package com.araditc.uploader.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.araditc.uploader.AppConfig;
import com.araditc.uploader.constants.UploadHistoryTypes;
import com.araditc.uploader.data.api.ApiClient;
import com.araditc.uploader.data.databse.UploadHistoryDAO;
import com.araditc.uploader.jobs.UploadWorker;
import com.araditc.uploader.model.UploadHistoryModel;
import com.araditc.uploader.notificaiton.NotificationBuilder;
import com.araditc.uploader.struct.UploadHistoryStruct;
import com.araditc.uploader.struct.responseStruct.MediaResponse;

import java.util.HashMap;
import java.util.UUID;

import static androidx.work.WorkInfo.State.FAILED;
import static androidx.work.WorkInfo.State.RUNNING;
import static androidx.work.WorkInfo.State.SUCCEEDED;

public class UploadService extends IntentService {
    private String filePath;
    private int type;
    private int uploadId;
    private WorkManager workManager;
    private Data.Builder builder;
    private UploadHistoryModel uploadHistoryModel;
    private static UploadWorker.UploadResult progressUploadListener;
    private Intent intent;
    private NotificationCompat.Builder notificationBuilder;
    UploadWorker.UploadResult uploadListener;

    NotificationManager notificationManager;
    private int[] failedChunk;

    public UploadService() {
        super("upload_manager");
    }

    private void restart() {
        notificationBuilder = NotificationBuilder.buildForegroundNotification(NotificationBuilder.STATUS.START_TYPE);
        startForeground(NotificationBuilder.NOTIFY_ID, notificationBuilder.build());
    }

    public static final String TOKEN_KEY = "token";
    public static final String UPLOAD_ID_KEY = "upload_id";
    public static final String FILE_PATH_KEY = "file_path";
    public static final String TYPE_KEY = "type";
    public static final String FAILED_CHUNK_KEY = "fail_chunk_sequence_number";
    private static HashMap<Integer, UUID> workerIds = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 19)
            restart();

        notificationManager = (NotificationManager) AppConfig.newInstance().application.getSystemService(Context.NOTIFICATION_SERVICE);

        workManager = WorkManager.getInstance(this);
        builder = new Data.Builder();

        uploadListener = new UploadWorker.UploadResult() {

            @Override
            public void onUploadComplete(int fileId, MediaResponse mediaResponse) {
                notificationBuilder.setContentTitle("upload completed!");
                notificationBuilder.setContentText("");
                notificationManager.notify(NotificationBuilder.NOTIFY_ID, notificationBuilder.build());

            }

            @Override
            public void onUploadProgress(int index, int fileId, int total, int value, int percent) {
                notificationBuilder.setContentText(percent + " %");

                notificationBuilder.setContentText(percent + " %");
                notificationManager.notify(NotificationBuilder.NOTIFY_ID, notificationBuilder.build());
            }

            @Override
            public void onErrorUploadProgress(int index, int fileId, int total, int value, int percent) {

            }

            @Override
            public void onUploadFailed(String message) {
                notificationBuilder.setContentTitle("upload failed!");
                notificationBuilder.setContentText("");
                notificationManager.notify(NotificationBuilder.NOTIFY_ID, notificationBuilder.build());

            }
        };

        UploadWorker.addUploadResultListener(uploadListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;

        uploadHistoryModel = new UploadHistoryModel(ApiClient.GetClient(intent.getStringExtra(TOKEN_KEY))
                , new UploadHistoryDAO());

        uploadId = intent.getIntExtra(UPLOAD_ID_KEY, -1);
        filePath = intent.getStringExtra(FILE_PATH_KEY);
        type = intent.getIntExtra(TYPE_KEY, -1);
        failedChunk = intent.getIntArrayExtra(FAILED_CHUNK_KEY);

        uploadHistoryModel.saveUploadHistory(uploadId + "",
                0,
                UploadHistoryTypes.IN_PROGRESS,
                null);

        UUID cancelUploadId = prepareChunkUpload(startId, failedChunk, uploadId, intent.getStringExtra(TOKEN_KEY));

        workerIds.put(uploadId, cancelUploadId);

        return START_STICKY;
    }

    public static void setProgressUploadListener(UploadWorker.UploadResult progressUploadListener) {
        UploadService.progressUploadListener = progressUploadListener;

        UploadWorker.addUploadResultListener(progressUploadListener);
    }

    public static void unRegisterProgressUploadListener() {
        UploadWorker.removeUploadResultListener(UploadService.progressUploadListener);
        UploadService.progressUploadListener = null;

    }

    public UUID prepareChunkUpload(int startId, int[] failedChunk, int uploadId, String token) {
        if (workManager == null || filePath == null || filePath.equals("")) return null;


        builder.putString(TOKEN_KEY, token);
        builder.putString(UploadWorker.FILE_PATH, filePath);
        builder.putInt(UploadWorker.FILE_TYPE, type);
        builder.putInt(UploadWorker.FILE_ID, uploadId);

        if (failedChunk != null)
            builder.putIntArray(UploadWorker.FAIL_CHUNK_ARRAY, failedChunk);

        WorkRequest uploadWorkRequest =
                new OneTimeWorkRequest.Builder(UploadWorker.class)
                        .setInputData(builder.build())
                        .build();

        workManager.enqueue(uploadWorkRequest);

        workManager.getWorkInfoByIdLiveData(uploadWorkRequest.getId()).observeForever(workInfo -> {

            if (workInfo.getState() == FAILED || workInfo.getState() == WorkInfo.State.BLOCKED || workInfo.getState() == WorkInfo.State.CANCELLED) {

                uploadHistoryModel.updateUploadHistory(uploadId + ""
                        , UploadHistoryTypes.NOT_COMPLETED, new UploadHistoryDAO.TransActionResult() {
                            @Override
                            public void onSuccess(UploadHistoryStruct uploadHistoryStruct) {
                                if (isUploadShouldStop()) {
                                    stopService(intent);
                                }

                            }

                            @Override
                            public void onError(Throwable error) {

                            }
                        });
                progressUploadListener.onUploadFailed("");


            } else if (workInfo.getState() == RUNNING) {
                uploadHistoryModel.updateUploadHistory(uploadId + "",
                        UploadHistoryTypes.IN_PROGRESS,
                        null);

            } else if (workInfo.getState() == SUCCEEDED) {
                uploadHistoryModel.updateUploadHistory(uploadId + "",
                        UploadHistoryTypes.IS_COMPLETED,
                        new UploadHistoryDAO.TransActionResult() {
                            @Override
                            public void onSuccess(UploadHistoryStruct uploadHistoryStruct) {

                                if (isUploadShouldStop()) {
                                    stopService(intent);
                                }
                            }

                            @Override
                            public void onError(Throwable error) {

                            }
                        });


            } else if (workInfo.getState() == WorkInfo.State.ENQUEUED) {
                uploadHistoryModel.saveUploadHistory(uploadId + "",
                        0,
                        UploadHistoryTypes.STARTED,
                        null);

            }
        });

        return uploadWorkRequest.getId();
    }

    private boolean isUploadShouldStop() {
        for (UploadHistoryStruct history : uploadHistoryModel.fetchAllHistory()) {

            if (history.getStatus() == UploadHistoryTypes.IN_PROGRESS) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        UploadWorker.removeUploadResultListener(uploadListener);
    }


    public static void cancelUpload(int uploadId) {
        if (workerIds.get(uploadId) != null)
            WorkManager.getInstance().cancelWorkById(workerIds.get(uploadId));
    }

}
