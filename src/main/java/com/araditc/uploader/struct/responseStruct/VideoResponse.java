package com.araditc.uploader.struct.responseStruct;

import com.google.gson.annotations.SerializedName;

public class VideoResponse extends MediaResponse {
    @SerializedName("duration")
    private String duration;
    @SerializedName("video")
    private Video video;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public static class Video {
        private Integer size;
        private String url;

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
