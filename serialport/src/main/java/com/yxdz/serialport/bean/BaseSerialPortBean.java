package com.yxdz.serialport.bean;


import com.yxdz.serialport.util.DataProcessUtil;

/**
 * @ClassName: BaseSerialPortBean
 * @Desription: SerialPort基类
 * @author: Dreamcoding
 * @date: 2017/11/3
 */
public class BaseSerialPortBean {

    protected String rand;
    protected String command;
    protected String seqNo;
    protected String length;
    protected String check;

    public BaseSerialPortBean(String rand, String command, String seqNo, String length, String check){
        this.rand=rand;
        this.command=command;
        this.seqNo=seqNo;
        this.length=length;
        this.check=check;
    }

    public String getRand() {
        return rand;
    }

    public void setRand(String rand) {
        this.rand = rand;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getCheck() {
        return check;
    }

    public byte getRandByte(){
        return DataProcessUtil.hexStringToByteArray(rand)[0];
    }

    public byte getCommandByte(){
        return DataProcessUtil.hexStringToByteArray(command)[0];
    }

    public byte getSeqNoByte(){
        return DataProcessUtil.hexStringToByteArray(seqNo)[0];
    }

    public byte getLengthByte(){
        return DataProcessUtil.hexStringToByteArray(length)[0];
    }

    public boolean isCheck(){
        byte cheak= DataProcessUtil.hexStringToByteArray(check)[0];
        byte cheak2= (byte) (getRandByte()^getCommandByte()^getSeqNoByte()^getLengthByte());
        if (cheak==cheak2){
            return true;
        }
        return false;
    }

    public void setCheck(String check) {
        this.check = check;
    }


}
