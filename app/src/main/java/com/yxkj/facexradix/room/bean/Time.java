package com.yxkj.facexradix.room.bean;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;


@Entity(indices = {@Index(value = {"id"},unique = true)})
public class Time implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;


    int tzIndex;
    String sun1Fr;
    String sun1To;
    String sun2Fr;
    String sun2To;
    String sun3Fr;
    String sun3To;
    String sun4Fr;
    String sun4To;

    String mon1Fr;
    String mon1To;
    String mon2Fr;
    String mon2To;
    String mon3Fr;
    String mon3To;
    String mon4Fr;
    String mon4To;

    String tue1Fr;
    String tue1To;
    String tue2Fr;
    String tue2To;
    String tue3Fr;
    String tue3To;
    String tue4Fr;
    String tue4To;

    String wed1Fr;
    String wed1To;
    String wed2Fr;
    String wed2To;
    String wed3Fr;
    String wed3To;
    String wed4Fr;
    String wed4To;

    String thu1Fr;
    String thu1To;
    String thu2Fr;
    String thu2To;
    String thu3Fr;
    String thu3To;
    String thu4Fr;
    String thu4To;

    String fri1Fr;
    String fri1To;
    String fri2Fr;
    String fri2To;
    String fri3Fr;
    String fri3To;
    String fri4Fr;
    String fri4To;

    String sat1Fr;
    String sat1To;
    String sat2Fr;
    String sat2To;
    String sat3Fr;
    String sat3To;
    String sat4Fr;
    String sat4To;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTzIndex() {
        return tzIndex;
    }

    public void setTzIndex(int tzIndex) {
        this.tzIndex = tzIndex;
    }

    public String getSun1Fr() {
        return sun1Fr;
    }

    public void setSun1Fr(String sun1Fr) {
        this.sun1Fr = sun1Fr;
    }

    public String getSun1To() {
        return sun1To;
    }

    public void setSun1To(String sun1To) {
        this.sun1To = sun1To;
    }

    public String getSun2Fr() {
        return sun2Fr;
    }

    public void setSun2Fr(String sun2Fr) {
        this.sun2Fr = sun2Fr;
    }

    public String getSun2To() {
        return sun2To;
    }

    public void setSun2To(String sun2To) {
        this.sun2To = sun2To;
    }

    public String getSun3Fr() {
        return sun3Fr;
    }

    public void setSun3Fr(String sun3Fr) {
        this.sun3Fr = sun3Fr;
    }

    public String getSun3To() {
        return sun3To;
    }

    public void setSun3To(String sun3To) {
        this.sun3To = sun3To;
    }

    public String getSun4Fr() {
        return sun4Fr;
    }

    public void setSun4Fr(String sun4Fr) {
        this.sun4Fr = sun4Fr;
    }

    public String getSun4To() {
        return sun4To;
    }

    public void setSun4To(String sun4To) {
        this.sun4To = sun4To;
    }

    public String getMon1Fr() {
        return mon1Fr;
    }

    public void setMon1Fr(String mon1Fr) {
        this.mon1Fr = mon1Fr;
    }

    public String getMon1To() {
        return mon1To;
    }

    public void setMon1To(String mon1To) {
        this.mon1To = mon1To;
    }

    public String getMon2Fr() {
        return mon2Fr;
    }

    public void setMon2Fr(String mon2Fr) {
        this.mon2Fr = mon2Fr;
    }

    public String getMon2To() {
        return mon2To;
    }

    public void setMon2To(String mon2To) {
        this.mon2To = mon2To;
    }

    public String getMon3Fr() {
        return mon3Fr;
    }

    public void setMon3Fr(String mon3Fr) {
        this.mon3Fr = mon3Fr;
    }

    public String getMon3To() {
        return mon3To;
    }

    public void setMon3To(String mon3To) {
        this.mon3To = mon3To;
    }

    public String getMon4Fr() {
        return mon4Fr;
    }

    public void setMon4Fr(String mon4Fr) {
        this.mon4Fr = mon4Fr;
    }

    public String getMon4To() {
        return mon4To;
    }

    public void setMon4To(String mon4To) {
        this.mon4To = mon4To;
    }

    public String getTue1Fr() {
        return tue1Fr;
    }

    public void setTue1Fr(String tue1Fr) {
        this.tue1Fr = tue1Fr;
    }

    public String getTue1To() {
        return tue1To;
    }

    public void setTue1To(String tue1To) {
        this.tue1To = tue1To;
    }

    public String getTue2Fr() {
        return tue2Fr;
    }

    public void setTue2Fr(String tue2Fr) {
        this.tue2Fr = tue2Fr;
    }

    public String getTue2To() {
        return tue2To;
    }

    public void setTue2To(String tue2To) {
        this.tue2To = tue2To;
    }

    public String getTue3Fr() {
        return tue3Fr;
    }

    public void setTue3Fr(String tue3Fr) {
        this.tue3Fr = tue3Fr;
    }

    public String getTue3To() {
        return tue3To;
    }

    public void setTue3To(String tue3To) {
        this.tue3To = tue3To;
    }

    public String getTue4Fr() {
        return tue4Fr;
    }

    public void setTue4Fr(String tue4Fr) {
        this.tue4Fr = tue4Fr;
    }

    public String getTue4To() {
        return tue4To;
    }

    public void setTue4To(String tue4To) {
        this.tue4To = tue4To;
    }

    public String getWed1Fr() {
        return wed1Fr;
    }

    public void setWed1Fr(String wed1Fr) {
        this.wed1Fr = wed1Fr;
    }

    public String getWed1To() {
        return wed1To;
    }

    public void setWed1To(String wed1To) {
        this.wed1To = wed1To;
    }

    public String getWed2Fr() {
        return wed2Fr;
    }

    public void setWed2Fr(String wed2Fr) {
        this.wed2Fr = wed2Fr;
    }

    public String getWed2To() {
        return wed2To;
    }

    public void setWed2To(String wed2To) {
        this.wed2To = wed2To;
    }

    public String getWed3Fr() {
        return wed3Fr;
    }

    public void setWed3Fr(String wed3Fr) {
        this.wed3Fr = wed3Fr;
    }

    public String getWed3To() {
        return wed3To;
    }

    public void setWed3To(String wed3To) {
        this.wed3To = wed3To;
    }

    public String getWed4Fr() {
        return wed4Fr;
    }

    public void setWed4Fr(String wed4Fr) {
        this.wed4Fr = wed4Fr;
    }

    public String getWed4To() {
        return wed4To;
    }

    public void setWed4To(String wed4To) {
        this.wed4To = wed4To;
    }

    public String getThu1Fr() {
        return thu1Fr;
    }

    public void setThu1Fr(String thu1Fr) {
        this.thu1Fr = thu1Fr;
    }

    public String getThu1To() {
        return thu1To;
    }

    public void setThu1To(String thu1To) {
        this.thu1To = thu1To;
    }

    public String getThu2Fr() {
        return thu2Fr;
    }

    public void setThu2Fr(String thu2Fr) {
        this.thu2Fr = thu2Fr;
    }

    public String getThu2To() {
        return thu2To;
    }

    public void setThu2To(String thu2To) {
        this.thu2To = thu2To;
    }

    public String getThu3Fr() {
        return thu3Fr;
    }

    public void setThu3Fr(String thu3Fr) {
        this.thu3Fr = thu3Fr;
    }

    public String getThu3To() {
        return thu3To;
    }

    public void setThu3To(String thu3To) {
        this.thu3To = thu3To;
    }

    public String getThu4Fr() {
        return thu4Fr;
    }

    public void setThu4Fr(String thu4Fr) {
        this.thu4Fr = thu4Fr;
    }

    public String getThu4To() {
        return thu4To;
    }

    public void setThu4To(String thu4To) {
        this.thu4To = thu4To;
    }

    public String getFri1Fr() {
        return fri1Fr;
    }

    public void setFri1Fr(String fri1Fr) {
        this.fri1Fr = fri1Fr;
    }

    public String getFri1To() {
        return fri1To;
    }

    public void setFri1To(String fri1To) {
        this.fri1To = fri1To;
    }

    public String getFri2Fr() {
        return fri2Fr;
    }

    public void setFri2Fr(String fri2Fr) {
        this.fri2Fr = fri2Fr;
    }

    public String getFri2To() {
        return fri2To;
    }

    public void setFri2To(String fri2To) {
        this.fri2To = fri2To;
    }

    public String getFri3Fr() {
        return fri3Fr;
    }

    public void setFri3Fr(String fri3Fr) {
        this.fri3Fr = fri3Fr;
    }

    public String getFri3To() {
        return fri3To;
    }

    public void setFri3To(String fri3To) {
        this.fri3To = fri3To;
    }

    public String getFri4Fr() {
        return fri4Fr;
    }

    public void setFri4Fr(String fri4Fr) {
        this.fri4Fr = fri4Fr;
    }

    public String getFri4To() {
        return fri4To;
    }

    public void setFri4To(String fri4To) {
        this.fri4To = fri4To;
    }

    public String getSat1Fr() {
        return sat1Fr;
    }

    public void setSat1Fr(String sat1Fr) {
        this.sat1Fr = sat1Fr;
    }

    public String getSat1To() {
        return sat1To;
    }

    public void setSat1To(String sat1To) {
        this.sat1To = sat1To;
    }

    public String getSat2Fr() {
        return sat2Fr;
    }

    public void setSat2Fr(String sat2Fr) {
        this.sat2Fr = sat2Fr;
    }

    public String getSat2To() {
        return sat2To;
    }

    public void setSat2To(String sat2To) {
        this.sat2To = sat2To;
    }

    public String getSat3Fr() {
        return sat3Fr;
    }

    public void setSat3Fr(String sat3Fr) {
        this.sat3Fr = sat3Fr;
    }

    public String getSat3To() {
        return sat3To;
    }

    public void setSat3To(String sat3To) {
        this.sat3To = sat3To;
    }

    public String getSat4Fr() {
        return sat4Fr;
    }

    public void setSat4Fr(String sat4Fr) {
        this.sat4Fr = sat4Fr;
    }

    public String getSat4To() {
        return sat4To;
    }

    public void setSat4To(String sat4To) {
        this.sat4To = sat4To;
    }
}
