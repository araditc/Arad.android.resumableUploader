package com.araditc.uploader.struct.responseStruct;

import com.google.gson.annotations.SerializedName;

public class MetaStruct {
    @SerializedName("pagination")
    private pagination pagination;
    @SerializedName("status_code")
    private int statusCode;
    @SerializedName("message")
    private String message;

    public MetaStruct.pagination getPagination() {
        return pagination;
    }

    public void setPagination(MetaStruct.pagination pagination) {
        this.pagination = pagination;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class pagination {
        private Integer offset;
        private Integer limit;
        private Integer page_size;
        private Integer total_size;

        public Integer getOffset() {
            return offset;
        }

        public void setOffset(Integer offset) {
            this.offset = offset;
        }

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }

        public Integer getPage_size() {
            return page_size;
        }

        public void setPage_size(Integer page_size) {
            this.page_size = page_size;
        }

        public Integer getTotal_size() {
            return total_size;
        }

        public void setTotal_size(Integer total_size) {
            this.total_size = total_size;
        }
    }
}
