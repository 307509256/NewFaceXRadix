package com.yxdz.serialport.listener;

/**
 * @ClassName: ${CLASS_NAME}
 * @Desription:
 * @author: Dreamcoding
 * @date: 2018/12/7
 */
public interface SerialPortListener {
    void onCardNo(String doorNumber, String cardNo);
    void onVersion(String verison);
    void onPassword(String doorNumber, String key);
    void onAlarm(String doorNumber, String statu);
    void onInvalidCard();
    void onDoorMagnet(String doorNumber, String statu);
    void onError(Exception e);
}
