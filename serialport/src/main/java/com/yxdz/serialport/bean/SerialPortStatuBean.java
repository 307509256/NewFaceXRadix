package com.yxdz.serialport.bean;

/**
 * @ClassName: SerialPortReadCardToolBean
 * @Desription:
 * @author: Dreamcoding
 * @date: 2017/12/28
 */
public class SerialPortStatuBean extends BaseSerialPortBean {

    private String sonCommand;
    private String data;
    private String doorNumber;
    private String statu;

    public SerialPortStatuBean(String rand, String command, String seqNo, String length, String check) {
        super(rand, command, seqNo, length, check);
    }

    public String getSonCommand() {
        return sonCommand;
    }

    public void setSonCommand(String sonCommand) {
        this.sonCommand = sonCommand;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDoorNumber() {
        return doorNumber;
    }

    public void setDoorNumber(String doorNumber) {
        this.doorNumber = doorNumber;
    }

    public String getStatu() {
        return statu;
    }

    /**
     * 门磁状态和警报状态也是用这个
     * @param statu
     */
    public void setStatu(String statu) {
        this.statu = statu;
    }


}
