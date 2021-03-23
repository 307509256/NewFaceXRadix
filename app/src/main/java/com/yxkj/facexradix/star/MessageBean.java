package com.yxkj.facexradix.star;

public class MessageBean {
    String type;
    Object data;
    long timestmp;
    String sn;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public long getTimestmp() {
        return timestmp;
    }

    public void setTimestmp(long timestmp) {
        this.timestmp = timestmp;
    }

    public MessageBean(String type, Object data, long timestmp) {
        this.type = type;
        this.data = data;
        this.timestmp = timestmp;
    }

    public MessageBean() {
    }

    public MessageBean(String type, Object data) {
        this.type = type;
        this.data = data;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
