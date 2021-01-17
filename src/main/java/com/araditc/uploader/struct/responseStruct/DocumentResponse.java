package com.araditc.uploader.struct.responseStruct;

import com.google.gson.annotations.SerializedName;

public class DocumentResponse extends MediaResponse {
    @SerializedName("document")
    private Document document;

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public static class Document {
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
