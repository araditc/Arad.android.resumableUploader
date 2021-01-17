package com.araditc.uploader.struct.responseStruct;

import com.google.gson.annotations.SerializedName;

public abstract class MediaResponse {
    @SerializedName("minithumbnail")
    private Minithumbnail minithumbnail;
    @SerializedName("thumbnail")
    private LargeUploadResponse.Sizes thumbnail;
    @SerializedName("width")
    private int width;
    @SerializedName("height")
    private int height;
    @SerializedName("file_name")
    private String file_name;
    @SerializedName("mime_type")
    private String mime_type;

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Minithumbnail getMinithumbnail() {
        return minithumbnail;
    }

    public void setMinithumbnail(Minithumbnail minithumbnail) {
        this.minithumbnail = minithumbnail;
    }

    public LargeUploadResponse.Sizes getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(LargeUploadResponse.Sizes thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }
}
