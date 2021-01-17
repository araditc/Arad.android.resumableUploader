package com.araditc.uploader.data.api;

import com.araditc.uploader.constants.Phrase;
import com.araditc.uploader.struct.responseStruct.AudioResponse;
import com.araditc.uploader.struct.responseStruct.DocumentResponse;
import com.araditc.uploader.struct.responseStruct.LargeUploadResponse;
import com.araditc.uploader.struct.responseStruct.MetaStruct;
import com.araditc.uploader.struct.responseStruct.VideoResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {

    /////////////////////////////////////////////// GET ////////////////////////////////////////////

    @GET(Phrase.MEDIA_IMAGE_END_POINT)
    Call<LargeUploadResponse> getImageUrl(
            @Query("file_id") String fileId);

    @GET(Phrase.MEDIA_AUDIO_END_POINT)
    Call<AudioResponse> getAudioUrl(
            @Query("file_id") String fileId);

    @GET(Phrase.MEDIA_VIDEO_END_POINT)
    Call<VideoResponse> getVideoUrl(
            @Query("file_id") String fileId);

    @GET(Phrase.MEDIA_DOCUMENT_END_POINT)
    Call<DocumentResponse> getDocumentUrl(
            @Query("file_id") String fileId);

    @GET(Phrase.MEDIA_PROFILE_END_POINT)
    Call<LargeUploadResponse> getProfileImageUrl(
            @Query("file_id") String fileId);

    @Multipart
    @POST
    Call<ResponseBody> upload(
            @Url String url,
            @Part MultipartBody.Part avatar,
            @Part("file_part") RequestBody rFile_part,
            @Part("file_id") RequestBody rFile_id,
            @Part("total_file_parts") RequestBody rTotalPart
    );

    @POST
    @FormUrlEncoded
    Call<ResponseBody> completeUpload(@Url String fileUrl,
                                      @Field("file_id") String fileId,
                                      @Field("total_file_parts") String total_file_parts,
                                      @Field("checksum") String checkSum);
}