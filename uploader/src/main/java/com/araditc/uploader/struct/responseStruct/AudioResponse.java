package com.araditc.uploader.struct.responseStruct;

import com.google.gson.annotations.SerializedName;

public class AudioResponse extends MediaResponse {
    @SerializedName("duration")
    private String duration;
    @SerializedName("title")
    private String title;
    @SerializedName("performer")
    private String performer;
    @SerializedName("audio")
    private Audio audio;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPerformer() {
        return performer;
    }

    public void setPerformer(String performer) {
        this.performer = performer;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public static class Audio {
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
