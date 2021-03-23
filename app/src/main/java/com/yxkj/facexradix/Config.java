package com.yxkj.facexradix;

/**
 * @PackageName: com.yxdz.facex.bean
 * @Desription:
 * @Author: Dreamcoding
 * @CreatDate: 2019/2/16 17:25
 */
public class Config {


    /**
     * volume : 2
     * ttsMod : 0
     * sleepTime : 30
     * doorDelayTimeForClose : 5
     * outMode : 0
     * openMode : 0
     * holdPassword : 0
     * commonPassword : 0
     *  sceneType : 0
     */

    private int volume;
    private int ttsMod;
    private int sleepTime;
    private int doorDelayTimeForClose;
    private int outMode;
    private String openMode;
    private String holdPassword;
    private String commonPassword;
    private String sceneType;
    private  int mode;
    private int cardFormat;

    public int getCardFormat() {
        return cardFormat;
    }

    public void setCardFormat(int cardFormat) {
        this.cardFormat = cardFormat;
    }

    public int getVolume() {
        return volume;
    }

    public String getSceneType() {
        return sceneType;
    }

    public void setSceneType(String sceneType) {
        this.sceneType = sceneType;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getTtsMod() {
        return ttsMod;
    }

    public void setTtsMod(int ttsMod) {
        this.ttsMod = ttsMod;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public int getDoorDelayTimeForClose() {
        return doorDelayTimeForClose;
    }

    public void setDoorDelayTimeForClose(int doorDelayTimeForClose) {
        this.doorDelayTimeForClose = doorDelayTimeForClose;
    }

    public int getOutMode() {
        return outMode;
    }

    public void setOutMode(int outMode) {
        this.outMode = outMode;
    }

    public String getOpenMode() {
        return openMode;
    }

    public void setOpenMode(String openMode) {
        this.openMode = openMode;
    }

    public String getHoldPassword() {
        return holdPassword;
    }

    public void setHoldPassword(String holdPassword) {
        this.holdPassword = holdPassword;
    }

    public String getCommonPassword() {
        return commonPassword;
    }

    public void setCommonPassword(String commonPassword) {
        this.commonPassword = commonPassword;
    }
}
