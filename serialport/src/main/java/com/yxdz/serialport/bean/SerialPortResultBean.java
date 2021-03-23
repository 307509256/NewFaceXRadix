package com.yxdz.serialport.bean;

/**
 * @ClassName: SerialPortResultBean
 * @Desription:
 * @author: Dreamcoding
 * @date: 2017/12/27
 */
public class SerialPortResultBean extends BaseSerialPortBean {

    private String result;

    public SerialPortResultBean(String rand, String command, String seqNo, String length, String check, String result) {
        super(rand, command, seqNo, length, check);
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
