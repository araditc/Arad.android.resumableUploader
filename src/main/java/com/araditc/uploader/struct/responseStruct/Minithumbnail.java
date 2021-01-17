package com.araditc.uploader.struct.responseStruct;

import com.google.gson.annotations.SerializedName;

public class Minithumbnail {
    @SerializedName("width")
    private int width;
    @SerializedName("height")
    private int height;
    @SerializedName("data")
    private String data;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}