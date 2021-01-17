package com.araditc.uploader.model;

import com.araditc.uploader.jobs.UploadWorker;
import com.araditc.uploader.constants.Phrase;
import com.araditc.uploader.data.api.ApiInterface;
import com.araditc.uploader.data.databse.UploadDAO;
import com.araditc.uploader.interfaces.UploadContract;
import com.araditc.uploader.struct.AradUploadDbStruct;
import com.araditc.uploader.struct.responseStruct.AudioResponse;
import com.araditc.uploader.struct.responseStruct.DocumentResponse;
import com.araditc.uploader.struct.responseStruct.LargeUploadResponse;
import com.araditc.uploader.struct.responseStruct.VideoResponse;

import java.io.IOException;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadModel implements UploadContract.Model {
    private UploadDAO uploadDAO;
    private ApiInterface apiInterface;

    public UploadModel(ApiInterface apiInterface, UploadDAO uploadDAO) {
        this.uploadDAO = uploadDAO;
        this.apiInterface = apiInterface;
    }

    @Override
    public void saveChunk(int fileId, int sequenceNumber, UploadDAO.TransActionResult transActionResult) {
        AradUploadDbStruct uploadDbStruct = new AradUploadDbStruct();
        uploadDbStruct.setCreatedAt(System.currentTimeMillis());
        uploadDbStruct.setFileId(String.valueOf(fileId));
        uploadDbStruct.setSequenceNumber(String.valueOf(sequenceNumber));
        uploadDbStruct.setStatus(1);
        uploadDAO.saveChunk(uploadDbStruct, transActionResult);
    }

    @Override
    public void findChunksByFileId(int fileId, int sequenceNumber, UploadDAO.TransActionListResult transActionListResult) {
        uploadDAO.findChunks(String.valueOf(fileId), transActionListResult);
    }

    @Override
    public void getImageUrl(UploadWorker.UploadResult uploadResult, int fileId) {
        if (uploadResult == null) return;

        apiInterface.getImageUrl(fileId + "").enqueue(new Callback<LargeUploadResponse>() {
            @Override
            public void onResponse(Call<LargeUploadResponse> call, Response<LargeUploadResponse> response) {
                if (uploadResult == null) return;

                if (response.isSuccessful() && response.body() != null && response.body().getSizes() != null) {
                    LargeUploadResponse body = response.body();
                    body.setUrl(body.getSizes().get(body.getSizes().size() - 1).getPhoto().getUrl());
                    uploadResult.onUploadComplete(fileId, body);
                } else
                    uploadResult.onUploadFailed("");
            }

            @Override
            public void onFailure(Call<LargeUploadResponse> call, Throwable t) {
                uploadResult.onUploadFailed(t.getMessage());
            }
        });
    }

    @Override
    public void getAudioUrl(UploadWorker.UploadResult uploadResult, int fileId) {
        if (uploadResult == null) return;

        apiInterface.getAudioUrl(fileId + "").enqueue(new Callback<AudioResponse>() {
            @Override
            public void onResponse(Call<AudioResponse> call, Response<AudioResponse> response) {
                if (response.isSuccessful()) {
                    AudioResponse body = response.body();
                    body.setUrl(body.getAudio().getUrl());
                    uploadResult.onUploadComplete(fileId, body);
                } else
                    uploadResult.onUploadFailed("");
            }

            @Override
            public void onFailure(Call<AudioResponse> call, Throwable t) {
                uploadResult.onUploadFailed(t.getMessage());
            }
        });

    }

    @Override
    public void getDocumentUrl(UploadWorker.UploadResult uploadResult, int fileId) {
        if (uploadResult == null) return;

        apiInterface.getDocumentUrl(fileId + "").enqueue(new Callback<DocumentResponse>() {
            @Override
            public void onResponse(Call<DocumentResponse> call, Response<DocumentResponse> response) {
                if (uploadResult == null) return;
                if (response.isSuccessful()) {
                    DocumentResponse body = response.body();
                    body.setUrl(body.getDocument().getUrl());
                    uploadResult.onUploadComplete(fileId, body);
                } else
                    uploadResult.onUploadFailed("");
            }

            @Override
            public void onFailure(Call<DocumentResponse> call, Throwable t) {
                uploadResult.onUploadFailed(t.getMessage());
            }
        });
    }

    @Override
    public void getVideoUrl(UploadWorker.UploadResult uploadResult, int fileId) {
        if (uploadResult == null) return;
        apiInterface.getVideoUrl(fileId + "").enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                if (response.isSuccessful()) {
                    VideoResponse body = response.body();
                    body.setUrl(body.getVideo().getUrl());
//                    body.setHeight(resp);
                    uploadResult.onUploadComplete(fileId, body);
                } else
                    uploadResult.onUploadFailed("");
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                uploadResult.onUploadFailed(t.getMessage());
            }
        });
    }

    @Override
    public void getProfileUrl(UploadWorker.UploadResult uploadResult, int fileId) {
        if (uploadResult == null) return;

        apiInterface.getProfileImageUrl(fileId + "").enqueue(new Callback<LargeUploadResponse>() {
            @Override
            public void onResponse(Call<LargeUploadResponse> call, Response<LargeUploadResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSizes() != null) {
                        LargeUploadResponse body = response.body();
                        body.setUrl(body.getSizes().get(body.getSizes().size() - 1).getPhoto().getUrl());
                        uploadResult.onUploadComplete(fileId, body);
                    } else {
                        uploadResult.onUploadComplete(fileId, new LargeUploadResponse());
                    }
                } else
                    uploadResult.onUploadFailed("");
            }

            @Override
            public void onFailure(Call<LargeUploadResponse> call, Throwable t) {
                uploadResult.onUploadFailed(t.getMessage());
            }
        });
    }

    @Override
    public Response<ResponseBody> completeUpload(String fileUrl, String fileId, String total_file_parts, String checkSum) {
        try {
            return apiInterface.completeUpload(Phrase.COMPLETE_UPLOAD_END_POINT
                    , fileId + "", total_file_parts + "", checkSum).execute();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Response<ResponseBody> uploadChunkPart(byte[] avatar, int filePart, int fileId, int totalPart, String format) {
        MultipartBody.Part rAvatar;

        RequestBody rFilePart = RequestBody.create(MediaType.parse("text/plain"), filePart + "");
        RequestBody rFileId = RequestBody.create(MediaType.parse("text/plain"), fileId + "");
        RequestBody rTotalPart = RequestBody.create(MediaType.parse("text/plain"), totalPart + "");
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), avatar);

        int num = new Random().nextInt(999999999) + 999999999;
        final String imageName = "avatar_" + num;

        rAvatar = MultipartBody.Part.createFormData("file", imageName + "." + format, requestFile);

        try {
            return apiInterface.upload(Phrase.CHUNK_UPLOADER_END_POINT,
                    rAvatar,
                    rFilePart,
                    rFileId,
                    rTotalPart).execute();
        } catch (IOException e) {
            return null;
        }
    }
}
