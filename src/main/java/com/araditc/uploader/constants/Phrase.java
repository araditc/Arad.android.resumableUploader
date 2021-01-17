package com.araditc.uploader.constants;


import com.araditc.uploader.BuildConfig;

public class Phrase {
    //    END POINTS
    public static final String BASE_URL = BuildConfig.BASE_URL;
    public static final String BASE_DOWNLOAD_URL = BuildConfig.BASE_DOWNLOAD_URL;

    public static final String MEDIA_IMAGE_END_POINT = "media/image";
    public static final String MEDIA_AUDIO_END_POINT = "media/audio";
    public static final String MEDIA_VIDEO_END_POINT = "media/video";
    public static final String MEDIA_DOCUMENT_END_POINT = "media/document";
    public static final String MEDIA_PROFILE_END_POINT = "media/profile-image";

    public static final String CHUNK_UPLOADER_END_POINT = "files/large-file-upload";
    public static final String COMPLETE_UPLOAD_END_POINT = "files/upload-complete";

}
