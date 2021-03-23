package com.yxkj.facexradix.room.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

/**
 * @ClassName: User
 * @Desription: user bean
 * @author: Dreamcoding
 * @date: 2018/12/6
 */
@Entity(indices = {@Index(value = {"userId"})})
public class Record implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    String userId;
    long openTime;
    int openType;
    int statu;
    String card;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(long openTime) {
        this.openTime = openTime;
    }

    public int getOpenType() {
        return openType;
    }

    public void setOpenType(int openType) {
        this.openType = openType;
    }

    public int getStatu() {
        return statu;
    }

    public void setStatu(int statu) {
        this.statu = statu;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
