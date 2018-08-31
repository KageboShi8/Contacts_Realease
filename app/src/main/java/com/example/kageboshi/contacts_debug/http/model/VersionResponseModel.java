package com.example.kageboshi.contacts_debug.http.model;

public class VersionResponseModel {

    /**
     * code : 0
     * data : {"version":"1.1","url":"http://106.15.186.113/group1/M00/00/00/rBO46VtjxfKAO378ACsFUWePpPY026.apk"}
     * message : 当前有可用版本
     */

    private int code;
    private DataBean data;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        /**
         * version : 1.1
         * url : http://106.15.186.113/group1/M00/00/00/rBO46VtjxfKAO378ACsFUWePpPY026.apk
         */

        private String version;
        private String url;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
