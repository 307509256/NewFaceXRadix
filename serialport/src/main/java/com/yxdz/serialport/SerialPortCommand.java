package com.yxdz.serialport;


import android.text.TextUtils;
import android.util.Log;

import com.yxdz.serialport.bean.BaseSerialPortBean;
import com.yxdz.serialport.bean.SerialPortResultBean;
import com.yxdz.serialport.bean.SerialPortStatuBean;
import com.yxdz.serialport.util.DataProcessUtil;

import java.util.Random;

/**
 * @ClassName: SerialPortCommand
 * @Desription:
 * @author: Dreamcoding
 * @date: 2017/10/22
 */
public class SerialPortCommand {

    //包头
    private byte stx = (byte) 0xAA;
    //包尾
    private byte etx = (byte) 0xDD;
    //序列号
    private byte seqNo = (byte) 0x00;

    private byte slaveSeqNo = 0x01;

    String TAG = "SerialPortCommand";

    public final static byte RESPONE_SUCCESS = 0x00;
    public final static byte RESPINE_FAIL_CHECK_OR_SUN = (byte) 0xB0;
    public final static byte RESPONE_FAIL_COMMAND = (byte) 0xB1;

    public final static byte COMMAND_HOST_OPEN = 0x30;
    public final static byte COMMAND_HOST_RESET = 0x31;
    public final static byte COMMAND_HOST_RESET_READER = 0x32;
    public final static byte COMMAND_HOST_VERSION = 0x33;
    public final static byte COMMAND_SLAVE_HEARD = 0x40;
    public final static byte COMMAND_SLAVE_STATU = 0x41;



    public void setSlaveSeqNo(byte slaveSeqNo) {
        this.slaveSeqNo = slaveSeqNo;
    }

    private byte getSeqNo() {
        if (seqNo > 0xfe) {
            seqNo = 0x00;
        }
        return ++seqNo;
    }


    /**
     * 获取开门的数据
     *
     * @param type 开门类型 0x00：不动作；0x01：普通开门；0x02：常开；0x03：常闭；0x04：恢复；
     * @param time 开门时间
     * @param door 继电器编号
     * @return
     */
    public byte[] getOpenData(byte door, byte time, byte type) {
        byte[] dataArr = new byte[13];
        dataArr[0] = stx;
        dataArr[1] = DataProcessUtil.hexStringToByteArray(getRandomHexString())[0];
        dataArr[2] = COMMAND_HOST_OPEN;
        dataArr[3] = getSeqNo();
        dataArr[4] = 0x00;
        dataArr[5] = 0x05;
        byte[] check = getCheckData(dataArr[1], dataArr[2], dataArr[3], dataArr[4], dataArr[5]);
        dataArr[6] = check[1];
        dataArr[1] = check[0];
        //0x00：不动作；0x01：普通开门；0x02：常开；0x03：常闭；0x04：恢复；其它：预留
        dataArr[7] = COMMAND_HOST_OPEN;
        dataArr[8] = 0x00;
        dataArr[9] = time;
        dataArr[10] = door;
        dataArr[11] = type;
        dataArr[12] = etx;
        // log.d(TAG, "Host send:" + DataProcessUtil.byteArraytoHex2(dataArr));
        return dataArr;
    }


    /**
     * 获取卡号输出的数据
     *
     * @param weigen 韦根位数(16进制)
     * @param cardNo 卡号（16进制）
     * @return
     */
    public byte[] getOutCardData(byte weigen, String cardNo) {
        if (TextUtils.isEmpty(cardNo)) {
            return null;
        }
        byte[] dataArr = new byte[18];
        dataArr[0] = stx;
        dataArr[1] = DataProcessUtil.hexStringToByteArray(getRandomHexString())[0];
        dataArr[2] = COMMAND_HOST_OPEN;
        dataArr[3] = getSeqNo();
        dataArr[4] = 0x00;
        dataArr[5] = 0x0A;
        byte[] check = getCheckData(dataArr[1], dataArr[2], dataArr[3], dataArr[4], dataArr[5]);
        dataArr[6] = check[1];
        dataArr[1] = check[0];
        dataArr[7] = 0x31;
        dataArr[8] = weigen;
        byte[] card = DataProcessUtil.hexStringToByteArray(DataProcessUtil.addZeroForNumHead(cardNo, 16));
        System.arraycopy(card, 0, dataArr, 9, 8);
        dataArr[17] = etx;
        // log.d(TAG, "Host send:" + DataProcessUtil.byteArraytoHex2(dataArr));
        return dataArr;
    }

    /**
     * 获取卡号输出的数据
     *
     * @return
     */
    public byte[] getBeer() {
        byte[] dataArr = new byte[9];
        dataArr[0] = stx;
        dataArr[1] = DataProcessUtil.hexStringToByteArray(getRandomHexString())[0];
        dataArr[2] = COMMAND_HOST_OPEN;
        dataArr[3] = slaveSeqNo;
        dataArr[4] = 0x00;
        dataArr[5] = 0x01;
        byte[] temp = getCheckData(dataArr[1], dataArr[2], dataArr[3], dataArr[4], dataArr[5]);
        dataArr[6] = temp[1];
        dataArr[1] = temp[0];
        dataArr[7] = 0x36;
        dataArr[8] = etx;
        // log.d(TAG, "Host send:" + DataProcessUtil.byteArraytoHex2(dataArr));
        return dataArr;
    }



    /**
     * 获取警报输出的数据,
     *
     * @param data 30使能，31关闭
     * @return
     */
    public byte[] getOutWarm(boolean data) {
        byte[] dataArr = new byte[10];
        dataArr[0] = stx;
        dataArr[1] = DataProcessUtil.hexStringToByteArray(getRandomHexString())[0];
        dataArr[2] = COMMAND_HOST_OPEN;
        dataArr[3] = slaveSeqNo;
        dataArr[4] = 0x00;
        dataArr[5] = 0x02;
        byte[] temp = getCheckData(dataArr[1], dataArr[2], dataArr[3], dataArr[4], dataArr[5]);
        dataArr[6] = temp[1];
        dataArr[1] = temp[0];
        dataArr[7] = 0x33;
        if (data) {
            dataArr[8] = 0x30;
        } else {
            dataArr[8] = 0x31;
        }
        dataArr[9] = etx;
        // log.d(TAG, "Host send:" + DataProcessUtil.byteArraytoHex2(dataArr));
        return dataArr;
    }


    /**
     * 获取键盘密码输出的数据
     *
     * @param weigen 韦根输出位数（默认为 0x04，只有两种状态：0x04 和 0x08）
     * @param data   键值
     * @return
     */
    public byte[] getOutKeys(byte weigen, byte type, String data) {

        byte[] dataArr = new byte[12 + data.length()];
        dataArr[0] = stx;
        dataArr[1] = DataProcessUtil.hexStringToByteArray(getRandomHexString())[0];
        dataArr[2] = COMMAND_HOST_OPEN;
        dataArr[3] = getSeqNo();
        dataArr[4] = 0x00;
        int len = data.length() + 4;
        dataArr[5] = DataProcessUtil.hexStringToByteArray(Integer.toHexString(len))[0];

        byte[] check = getCheckData(dataArr[1], dataArr[2], dataArr[3], dataArr[4], dataArr[5]);
        dataArr[6] = check[1];
        dataArr[1] = check[0];
        dataArr[7] = 0x32;
        dataArr[8] = weigen;
        dataArr[9] = type;
        for (int index = 0; index < data.length(); index++) {
            dataArr[10 + index] = DataProcessUtil.hexStringToByteArray(data.charAt(index) + "")[0];
        }
        dataArr[10 + data.length()] = 0x0B;
        dataArr[11 + data.length()] = etx;

        // log.d(TAG, "Host send:" + DataProcessUtil.byteArraytoHex2(dataArr));
        return dataArr;
    }

    /**
     * 获取键值输出，一个一个输出
     *
     * @param weigen
     * @param data
     * @return
     */
    public byte[] getOutKey(byte weigen, byte data) {
        byte[] dataArr = new byte[11];
        dataArr[0] = stx;
        dataArr[1] = DataProcessUtil.hexStringToByteArray(getRandomHexString())[0];
        dataArr[2] = COMMAND_HOST_OPEN;
        dataArr[3] = getSeqNo();
        dataArr[4] = 0x00;
        int len = 0x03;
        dataArr[5] = (byte) Integer.parseInt(len + "", 16);
        byte[] check = getCheckData(dataArr[1], dataArr[2], dataArr[3], dataArr[4], dataArr[5]);
        dataArr[6] = check[1];
        dataArr[1] = check[0];
        dataArr[7] = 0x32;
        dataArr[8] = weigen;
        dataArr[9] = data;
        dataArr[10] = etx;
        // log.d(TAG, "Host send:" + DataProcessUtil.byteArraytoHex2(dataArr));
        return dataArr;
    }


    /**
     * 获取复位从机的指令数据
     *
     * @return
     */
    public byte[] getResetSlaveData() {
        byte[] dataArr = new byte[8];
        dataArr[0] = stx;
        dataArr[1] = DataProcessUtil.hexStringToByteArray(getRandomHexString())[0];
        dataArr[2] = COMMAND_HOST_RESET;
        dataArr[3] = getSeqNo();
        dataArr[4] = 0x00;
        dataArr[5] = 0x00;
        byte[] temp = getCheckData(dataArr[1], dataArr[2], dataArr[3], dataArr[4], dataArr[5]);
        dataArr[6] = temp[1];
        dataArr[1] = temp[0];
        dataArr[7] = etx;
        // log.d(TAG, "Host send:" + DataProcessUtil.byteArraytoHex2(dataArr));
        return dataArr;
    }

    /**
     * 获取读卡器版本号
     *
     * @return
     */
    public byte[] getVersion() {
        byte[] dataArr = new byte[8];
        dataArr[0] = stx;
        dataArr[1] = DataProcessUtil.hexStringToByteArray(getRandomHexString())[0];
        dataArr[2] = COMMAND_HOST_VERSION;
        dataArr[3] = slaveSeqNo;
        dataArr[4] = 0x00;
        dataArr[5] = 0x00;
        byte[] temp = getCheckData(dataArr[1], dataArr[2], dataArr[3], dataArr[4], dataArr[5]);
        dataArr[6] = temp[1];
        dataArr[1] = temp[0];
        dataArr[7] = etx;
        // log.d(TAG, "Host send:" + DataProcessUtil.byteArraytoHex2(dataArr));
        return dataArr;
    }

    /**
     * 获取重置读卡器命令
     *
     * @return
     */
    public byte[] getResetReader() {
        byte[] dataArr = new byte[8];
        dataArr[0] = stx;
        dataArr[1] = DataProcessUtil.hexStringToByteArray(getRandomHexString())[0];
        dataArr[2] = COMMAND_HOST_RESET_READER;
        dataArr[3] = slaveSeqNo;
        dataArr[4] = 0x00;
        dataArr[5] = 0x00;
        byte[] temp = getCheckData(dataArr[1], dataArr[2], dataArr[3], dataArr[4], dataArr[5]);
        dataArr[6] = temp[1];
        dataArr[1] = temp[0];
        dataArr[7] = etx;
        // log.d(TAG, "Host send:" + DataProcessUtil.byteArraytoHex2(dataArr));
        return dataArr;
    }



    /**
     * 获取心跳包数据或输入口状态数据
     *
     * @param cmd    命令为40为心跳包，41为上传状态数据
     * @param result 输入为16进制数，只能输入00,B0,B1
     * @return
     */
    public byte[] getBaseData(byte cmd, byte result) {
        byte[] dataArr = new byte[9];
        dataArr[0] = stx;
        dataArr[1] = DataProcessUtil.hexStringToByteArray(getRandomHexString())[0];
        dataArr[2] = cmd;
        dataArr[3] = slaveSeqNo;
        dataArr[4] = 0x00;
        dataArr[5] = 0x01;

        byte[] temp = getCheckData(dataArr[1], dataArr[2], dataArr[3], dataArr[4], dataArr[5]);
        dataArr[6] = temp[1];
        dataArr[1] = temp[0];

        dataArr[7] = result;
        dataArr[8] = etx;

        // log.d(TAG, "Host send:" + DataProcessUtil.byteArraytoHex2(dataArr));
        return dataArr;
    }

    /**
     * 获取心跳包数据
     *
     * @param cmd
     * @param result
     * @param seqNo
     * @return
     */
    public byte[] getBaseData(byte cmd, byte result, byte seqNo) {
        byte[] dataArr = new byte[9];
        dataArr[0] = stx;
        dataArr[1] = DataProcessUtil.hexStringToByteArray(getRandomHexString())[0];
        dataArr[2] = cmd;
        dataArr[3] = seqNo;
        dataArr[4] = 0x00;
        dataArr[5] = 0x01;

        byte[] temp = getCheckData(dataArr[1], dataArr[2], dataArr[3], dataArr[4], dataArr[5]);
        dataArr[6] = temp[1];
        dataArr[1] = temp[0];

        dataArr[7] = result;
        dataArr[8] = etx;

        // log.d(TAG, "Host send:" + DataProcessUtil.byteArraytoHex2(dataArr));
        return dataArr;
    }


    /**
     * 计算校验位
     *
     * @param byte1 随机码
     * @param byte2 命令
     * @param byte3 命令序号
     * @param byte4 附加数据长度1
     * @param byte5 附加数据长度2
     * @return
     */
    private byte[] getCheckData(byte byte1, byte byte2, byte byte3, byte byte4, byte byte5) {
        byte[] data = new byte[2];
        data[1] = (byte) (byte1 ^ byte2 ^ byte3 ^ byte4 ^ byte5);
        if (DataProcessUtil.byteArraytoHex(data).contains("DD")) {
            data[1] = (byte) ((++byte1) ^ byte2 ^ byte3 ^ byte4 ^ byte5);
            // log.d(TAG, "check---------------------------------------:" + data[1]);
        }
        data[0] = byte1;
        return data;
    }

    /**
     * 数据解析
     *
     * @param data
     * @return
     */
    public BaseSerialPortBean parseData(byte[] data) {
        BaseSerialPortBean baseSerialPortBean = null;
        String dataStr = DataProcessUtil.byteArraytoHex(data);
        String command = dataStr.substring(4, 6);
        switch (command) {
            case Constants.SERIAL_PORT_CMD_OPEN:
                baseSerialPortBean = new SerialPortResultBean(dataStr.substring(2, 4), dataStr.substring(4, 6), dataStr.substring(6, 8), dataStr.substring(10, 12), dataStr.substring(12, 14), dataStr.substring(14, 16));
                break;
            case Constants.SERIAL_PORT_CMD_RESET:
                baseSerialPortBean = new SerialPortResultBean(dataStr.substring(2, 4), dataStr.substring(4, 6), dataStr.substring(6, 8), dataStr.substring(10, 12), dataStr.substring(12, 14), dataStr.substring(14, 16));
                break;
            case Constants.SERIAL_PORT_CMD_RESET_READER:
                baseSerialPortBean = new SerialPortResultBean(dataStr.substring(2, 4), dataStr.substring(4, 6), dataStr.substring(6, 8), dataStr.substring(10, 12), dataStr.substring(12, 14), dataStr.substring(14, 16));
                break;
            case Constants.SERIAL_PORT_CMD_VERION:
                String lenStr=dataStr.substring(10, 12);
                int len=Integer.parseInt(lenStr,16);
                baseSerialPortBean = new SerialPortResultBean(dataStr.substring(2, 4), dataStr.substring(4, 6), dataStr.substring(6, 8), lenStr, dataStr.substring(12, 14), dataStr.substring(14, 14+(2*len)));
                break;
            case Constants.SERIAL_PORT_CMD_HEART:
                baseSerialPortBean = new SerialPortResultBean(dataStr.substring(2, 4), dataStr.substring(4, 6), dataStr.substring(6, 8), dataStr.substring(10, 12), dataStr.substring(12, 14), dataStr.substring(14, 16));
                break;
            case Constants.SERIAL_PORT_CMD_STATU_DATA:
                SerialPortStatuBean serialPortStatuBean = new SerialPortStatuBean(dataStr.substring(2, 4), dataStr.substring(4, 6), dataStr.substring(6, 8), dataStr.substring(10, 12), dataStr.substring(12, 14));
                String sonCommand = dataStr.substring(14, 16);
                serialPortStatuBean.setSonCommand(sonCommand);
                if ("30".equals(sonCommand)) {
                    serialPortStatuBean.setData(dataStr.substring(16, 48));
                } else if ("31".equals(sonCommand)) {
                    serialPortStatuBean.setData(dataStr.substring(16, 32));
                } else if ("32".equals(sonCommand)) {
                    serialPortStatuBean.setDoorNumber(dataStr.substring(16, 18));
                    serialPortStatuBean.setStatu(dataStr.substring(18, 20));
                } else if ("33".equals(sonCommand)) {
                    serialPortStatuBean.setDoorNumber(dataStr.substring(16, 18));
                    serialPortStatuBean.setStatu(dataStr.substring(18, 20));
                }
                baseSerialPortBean = serialPortStatuBean;
                break;
        }
        return baseSerialPortBean;
    }


    /**
     * 校验位验证
     *
     * @param baseSerialPortBean
     * @return
     */
    public boolean checkData(BaseSerialPortBean baseSerialPortBean) {
        return baseSerialPortBean.isCheck();
    }


    /**
     * 获取16进制随机数
     *
     * @return
     */
    private String getRandomHexString() {
        String rand = "A1";
        try {
            rand = Integer.toHexString(16 + new Random().nextInt(220)).toUpperCase();
            if ("DD".equals(rand)) {
                rand = "BB";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return rand;
        }
    }


}
