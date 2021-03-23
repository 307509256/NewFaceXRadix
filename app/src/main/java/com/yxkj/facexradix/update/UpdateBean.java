package com.yxkj.facexradix.update;

public class UpdateBean {


    /**
     * code : 100
     * msg : 成功
     * data : {"id":12,"fileName":"app-release.apk","fileSize":"120505.5","versionNum":"1.3.8","fileUrl":"/access/upload//2020/07/02/app-release.apk","remarks":"test","operTime":"2020-07-02 15:40:27"}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 12
         * fileName : app-release.apk
         * fileSize : 120505.5
         * versionNum : 1.3.8
         * fileUrl : /access/upload//2020/07/02/app-release.apk
         * remarks : test
         * operTime : 2020-07-02 15:40:27
         */

        private int id;
        private String fileName;
        private String fileSize;
        private String versionNum;
        private String fileUrl;
        private String remarks;
        private String operTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }

        public String getVersionNum() {
            return versionNum;
        }

        public void setVersionNum(String versionNum) {
            this.versionNum = versionNum;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getOperTime() {
            return operTime;
        }

        public void setOperTime(String operTime) {
            this.operTime = operTime;
        }
    }
}
