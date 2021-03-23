package com.yxdz.serialport;

/**
 * @ClassName: Constants
 * @Desription:
 * @author: Dreamcoding
 * @date: 2018/8/1
 */
public interface Constants {

//    String SERIAL_PORT_PATH="/dev/ttyS4";
//    String SERIAL_PORT_PATH="/dev/ttyS1";
    String SERIAL_PORT_PATH="/dev/ttyS3";
    int SERIAL_PORT_BAUDRATE=9600;

    //开门指令
    String SERIAL_PORT_CMD_OPEN="30";
    //复位从机命令
    String SERIAL_PORT_CMD_RESET="31";
    //重置从机命令
    String SERIAL_PORT_CMD_RESET_READER="32";
    //获取版本命令
    String SERIAL_PORT_CMD_VERION="33";
    //心跳包
    String SERIAL_PORT_CMD_HEART="40";
    //读卡器数据
    String SERIAL_PORT_CMD_STATU_DATA="41";

    byte DEFAULT_TIME=0x2;

    String DEVICE_CARD_DECODE = "DEVICE_CARD_DECODE";
}
