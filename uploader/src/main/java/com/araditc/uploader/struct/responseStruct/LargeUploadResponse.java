package com.araditc.uploader.struct.responseStruct;

import com.araditc.uploader.constants.Phrase;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LargeUploadResponse extends MediaResponse {
    @SerializedName("sizes")
    private List<Sizes> sizes;

    public static class Sizes {
        private String type;
        private int width;
        private int height;
        private Photo photo;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
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

        public Photo getPhoto() {
            return photo;
        }

        public void setPhoto(Photo photo) {
            this.photo = photo;
        }
    }

    public static class Photo {
        private String checksum;
        private int size;
        private String url;

        public String getChecksum() {
            return checksum;
        }

        public void setChecksum(String checksum) {
            this.checksum = checksum;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getUrl() {
            return Phrase.BASE_DOWNLOAD_URL + url;
        }

        public String getSimpleUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public List<Sizes> getSizes() {
        return sizes;
    }

    public void setSizes(List<Sizes> sizes) {
        this.sizes = sizes;
    }
}
