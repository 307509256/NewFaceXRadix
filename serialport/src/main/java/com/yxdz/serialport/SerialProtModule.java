package com.yxdz.serialport;

import android.util.Log;

import com.yxdz.serialport.bean.BaseSerialPortBean;
import com.yxdz.serialport.bean.SerialPortResultBean;
import com.yxdz.serialport.bean.SerialPortStatuBean;
import com.yxdz.serialport.listener.SerialPortListener;
import com.yxdz.serialport.util.DataProcessUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

/**
 * @ClassName: SerialProtModule
 * @Desription:
 * @author: Dreamcoding
 * @date: 2018/12/7
 */
public class SerialProtModule {

    public final static String TAG = "SerialPortModuleX";
    private SerialPort serialPort;
    private ExecutorService executorService;
    private SerialPortCommand serialPortCommand;
    private SerialPortListener serialPortListener;

    private boolean isOpen;
    private long hearTime = 0;
    private InputStream inputStream;
    private OutputStream outputStream;


    public SerialProtModule(SerialPort serialPort, ExecutorService executorService) {
        this.executorService = executorService;
        this.serialPort = serialPort;
        serialPortCommand = new SerialPortCommand();
        initSerialPortConfig();
    }


    public void setSerialPortListener(SerialPortListener serialPortListener) {
        this.serialPortListener = serialPortListener;
    }

    private void initSerialPortConfig() {
        //初始化串口参数数据
        // log.d(TAG, "[初始化串口参数]");
        isOpen = true;
        inputStream = serialPort.getInputStream();
        outputStream = serialPort.getOutputStream();
        executorService.execute(serialPortRunnable);
    }

    private byte[] dataA = new byte[25];
    private int dataASize = 0;
    private long dataATime;
    private byte[] buffer = new byte[25];
    private Runnable serialPortRunnable = new Runnable() {
        @Override
        public void run() {
            while (isOpen) {
                int size;
                try {
                    if (inputStream == null) {
                        break;
                    }
                    size = inputStream.read(buffer);
                    if (size > 0) {
                        byte[] data = Arrays.copyOf(buffer, size);
//                        // log.d(TAG, "数据:" + DataProcessUtil.byteArraytoHex(data));
                        if (data[0] == -86 && data[size - 1] == -35 && size > 7) {
                            BaseSerialPortBean baseSerialPortBean = serialPortCommand.parseData(buffer);
                            if (baseSerialPortBean != null && baseSerialPortBean.isCheck()) {
                                // log.d(TAG, "[接收数据]：" + DataProcessUtil.byteArraytoHex(data));
                                deal(baseSerialPortBean);
                            } else {
                                byte[] result = null;
                                result = serialPortCommand.getResetSlaveData();
                                // log.d(TAG, "收到非法数据,重新发送复位指令:" + DataProcessUtil.byteArraytoHex(result));
                                outputStream.write(result);
                                outputStream.flush();
                                continue;
                            }
                        } else if (data[0] == -86 && data[size - 1] != -35) {
                            dataATime = System.currentTimeMillis();
                            dataASize = 0;
                            System.arraycopy(data, 0, dataA, dataASize, size);
                            dataASize += size;
                        } else if (data[0] != -86 && data[size - 1] != -35) {
                            System.arraycopy(data, 0, dataA, dataASize, size);
//                            // log.d(TAG, "----------------追加数据：" + DataProcessUtil.byteArraytoHex(dataA));
                            dataASize += size;
                        } else if (data[0] != -86 && data[size - 1] == -35) {
                            if ((System.currentTimeMillis() - dataATime) <= 1000) {
                                System.arraycopy(data, 0, dataA, dataASize, size);
                                // log.d(TAG, "[收到整合数据]:" + DataProcessUtil.byteArraytoHex(dataA));
                                BaseSerialPortBean baseSerialPortBean = serialPortCommand.parseData(dataA);
                                Arrays.fill(dataA, (byte) 0);
                                dataASize = 0;
                                if (baseSerialPortBean != null && baseSerialPortBean.isCheck()) {
                                    deal(baseSerialPortBean);
                                } else {
                                    byte[] result = null;
                                    result = serialPortCommand.getResetSlaveData();
                                    // log.d(TAG, "收到非法数据,重新发送复位指令:" + DataProcessUtil.byteArraytoHex(result));
                                    outputStream.write(result);
                                    outputStream.flush();
                                    continue;
                                }
                            }
                        }
                    }
                    Arrays.fill(buffer, (byte) 0);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    serialPortListener.onError(e);
                }
            }
        }
    };


    public void deal(BaseSerialPortBean baseSerialPortBean) throws IOException {
        byte[] result = null;
        //设置序列号
        serialPortCommand.setSlaveSeqNo(baseSerialPortBean.getSeqNoByte());
        switch (baseSerialPortBean.getCommand()) {
            case Constants.SERIAL_PORT_CMD_HEART:
                //心跳包
                result = serialPortCommand.getBaseData(SerialPortCommand.COMMAND_SLAVE_HEARD, (byte) 0x00);
                // log.d(TAG, "[底板心跳回复]:" + DataProcessUtil.byteArraytoHex(result));
                outputStream.write(result);
                outputStream.flush();
                hearTime = System.currentTimeMillis();
                break;
            case Constants.SERIAL_PORT_CMD_OPEN:
                // log.d(TAG, "[收到驱动信号回复]");
                break;
            case Constants.SERIAL_PORT_CMD_RESET_READER:
                // log.d(TAG, "[收到重置命令回复]");
                break;
            case Constants.SERIAL_PORT_CMD_VERION:
                // log.d(TAG, "[收到版本命令回复]");
                SerialPortResultBean serialPortResultBean = (SerialPortResultBean) baseSerialPortBean;
                if(serialPortListener!=null){
                    serialPortListener.onVersion(DataProcessUtil.hexToASCII(serialPortResultBean.getResult()));
                }
                break;
            case Constants.SERIAL_PORT_CMD_STATU_DATA:
                //读取卡号
                SerialPortStatuBean serialPortStatuBean = (SerialPortStatuBean) baseSerialPortBean;
                switch (serialPortStatuBean.getSonCommand()) {
                    case "30":
                        //卡号数据
                        if (serialPortListener != null) {
                            String cardNo = serialPortStatuBean.getData();
                            if (cardNo.length() == 32) {
                                result = serialPortCommand.getBaseData(SerialPortCommand.COMMAND_SLAVE_STATU, (byte) 0x00);
                                // log.d(TAG, "[卡号取反]:" + DataProcessUtil.formatCard(cardNo));
//                                String tenCardNo = DataProcessUtil.bytes2cardNo(DataProcessUtil.hexStringToByteArray(DataProcessUtil.formatCard(cardNo)), 0, SPUtils.getInstance().getInt(Constants.DEVICE_CARD_DECODE,16));
//                                String tenCardNo = DataProcessUtil.extractCardno(DataProcessUtil.hexStringToByteArray(cardNo));
                                String CardNo = DataProcessUtil.formatCard(cardNo);
                                // log.d(TAG, "[原卡号：]" + cardNo + ",[卡号输出]:" + CardNo);
                                serialPortListener.onCardNo(serialPortStatuBean.getDoorNumber(), CardNo);
                            } else {
                                result = serialPortCommand.getBaseData(SerialPortCommand.COMMAND_SLAVE_STATU, (byte) 0xB0);
                                // log.d(TAG, "[原卡号（卡号有问题）：]" + cardNo);
                            }
                        } else {
                            result = serialPortCommand.getBaseData(SerialPortCommand.COMMAND_SLAVE_STATU, (byte) 0xB0);
                            // log.d(TAG, "[本地监听错误]");
                        }
                        // log.d(TAG, "[刷卡信号回复]:" + DataProcessUtil.byteArraytoHex(result));
                        break;
                    case "31":
                        //键盘输入数据
                        // log.d(TAG, "[键盘输入信号]:" + serialPortStatuBean.toString());
                        result = serialPortCommand.getBaseData(SerialPortCommand.COMMAND_SLAVE_STATU, (byte) 0x00);
                        // log.d(TAG, "[键盘输入回复]:" + DataProcessUtil.byteArraytoHex(result));
                        break;
                    case "32":
                        //门磁状态
                        // log.d(TAG, "[门磁状态信号]");
                        serialPortListener.onDoorMagnet(serialPortStatuBean.getDoorNumber(), serialPortStatuBean.getStatu());
                        result = serialPortCommand.getBaseData(SerialPortCommand.COMMAND_SLAVE_STATU, (byte) 0x00);
                        // log.d(TAG, "[门磁状态结果回复]:" + DataProcessUtil.byteArraytoHex(result));
                        break;
                    case "33":
                        //警报输入
                        // log.d(TAG, "[门警报输入信号]:" + serialPortStatuBean.toString());
                        serialPortListener.onAlarm(serialPortStatuBean.getDoorNumber(), serialPortStatuBean.getStatu());
                        result = serialPortCommand.getBaseData(SerialPortCommand.COMMAND_SLAVE_STATU, (byte) 0x00);
                        // log.d(TAG, "[警报输入结果回复]:" + DataProcessUtil.byteArraytoHex(result));
                        break;
                    case "34":
                        //无效卡
                        // log.d(TAG, "[无效卡信号]:" + serialPortStatuBean.toString());
                        serialPortListener.onInvalidCard();
                        result = serialPortCommand.getBaseData(SerialPortCommand.COMMAND_SLAVE_STATU, (byte) 0x00);
                        // log.d(TAG, "[无效卡结果回复]:" + DataProcessUtil.byteArraytoHex(result));
                        break;
                    default:
                        result = serialPortCommand.getBaseData(SerialPortCommand.COMMAND_SLAVE_STATU, (byte) 0xB0);
                        // log.d(TAG, "[数据错误]");
                        // log.d(TAG, "[回复输出信息]：" + DataProcessUtil.byteArraytoHex(result));
                }
                outputStream.write(result);
                outputStream.flush();
                break;
            case Constants.SERIAL_PORT_CMD_RESET:
                // log.d(TAG, "[收到复位信号回复]");
                break;
            default:
                result = serialPortCommand.getResetSlaveData();
                // log.d(TAG, "cmd:" + baseSerialPortBean.getCommand());
                // log.d(TAG, "[收到非法指令内容]");
                outputStream.write(result);
                outputStream.flush();
                break;
        }
    }

    public void outRelay(byte door, byte time, byte type) {
        byte[] openData = serialPortCommand.getOpenData(door, time, type);
        // log.d(TAG, "[发送开门指令]:" + DataProcessUtil.byteArraytoHex(openData));
        try {
            outputStream.write(openData);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            serialPortListener.onError(e);
        }
    }


    /**
     * 获取卡号输出的数据
     *
     * @param weigen 韦根位数(16进制)
     * @param cardNo 卡号（16进制）
     * @return
     */
    public void outCard(byte weigen, String cardNo) {
        byte[] outCardNo = serialPortCommand.getOutCardData(weigen, cardNo);
        // log.d(TAG, "[输出卡号指令]:" + DataProcessUtil.byteArraytoHex(outCardNo));
        try {
            outputStream.write(outCardNo);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            serialPortListener.onError(e);
        }
    }


    /**
     * 控制蜂鸣器
     */
    public void outBeer() {
        byte[] data = serialPortCommand.getBeer();
        // log.d(TAG, "[输出蜂鸣器指令]:" + DataProcessUtil.byteArraytoHex(data));
        try {
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            serialPortListener.onError(e);
        }
    }


    /**
     * 输出挟持密码警报
     *
     * @param result
     */
    public void outHoldWarm(boolean result) {
        byte[] outCardNo = serialPortCommand.getOutWarm(result);
        // log.d(TAG, "[输出警报（挟持密码）指令]:" + DataProcessUtil.byteArraytoHex(outCardNo));
        try {
            outputStream.write(outCardNo);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            serialPortListener.onError(e);
        }
    }


    /**
     * 输出键盘数据，用于一个一个输出
     *
     * @param weigen
     * @param data
     */
    public void outKey(byte weigen, byte data) {
        byte[] outCardNo = serialPortCommand.getOutKey(weigen, data);
        // log.d(TAG, "[输出按键单个密码指令]:" + DataProcessUtil.byteArraytoHex(outCardNo));
        try {
            outputStream.write(outCardNo);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            serialPortListener.onError(e);
        }
    }

    /**
     * 输出键盘数据，用于全部输出
     *
     * @param weigen
     * @param data
     * @param type   0xee 江森项目，0xcc 标准项目
     */
    public void outKeys(byte weigen, byte type, String data) {
        byte[] outCardNo = serialPortCommand.getOutKeys(weigen, type, data);
        // log.d(TAG, "[输出按键全部密码指令]:" + DataProcessUtil.byteArraytoHex(outCardNo));
        try {
            outputStream.write(outCardNo);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            serialPortListener.onError(e);
        }
    }

    /**
     * 复位从机命令
     */
    public void resetSlave() {
        byte[] data = serialPortCommand.getResetSlaveData();
        // log.d(TAG, "[输出复位从机指令]:" + DataProcessUtil.byteArraytoHex(data));
        try {
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            serialPortListener.onError(e);
        }
    }

    /**
     * 重置从机命令
     */
    public void resetReader() {
        byte[] data = serialPortCommand.getResetReader();
        // log.d(TAG, "[输出重置从机指令]:" + DataProcessUtil.byteArraytoHex(data));
        try {
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            serialPortListener.onError(e);
        }
    }

    /**
     * 获取从机版本号命令
     */
    public void getVersion() {
        byte[] data = serialPortCommand.getVersion();
        // log.d(TAG, "[输出获取从机版本号指令]:" + DataProcessUtil.byteArraytoHex(data));
        try {
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            serialPortListener.onError(e);
        }
    }


    public void setOpen(boolean open) {
        isOpen = open;
    }

    public long getHearTime() {
        return hearTime;
    }

    public void close() {
        serialPort.close();
        serialPort = null;
        isOpen = false;
    }

}
