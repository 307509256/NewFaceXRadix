package com.yxkj.facexradix.room.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;


@Entity(indices = {@Index(value = {"sn"})})
public class IosToken {


    @PrimaryKey(autoGenerate = true)
    private long id;
    private String sn;
    private String tokne;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getTokne() {
        return tokne;
    }

    public void setTokne(String tokne) {
        this.tokne = tokne;
    }
}
