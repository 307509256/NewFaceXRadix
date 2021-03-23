package com.yxdz.serialport.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.yxdz.serialport.Constants;
import com.yxdz.serialport.SerialPort;
import com.yxdz.serialport.SerialProtModule;
import com.yxdz.serialport.listener.SerialPortListener;
import com.yxdz.serialport.util.DataProcessUtil;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @ClassName: SerialPortService
 * @Desription: 串口通信服务
 * @author: Dreamcoding
 * @date: 2018/12/6
 */
public class SerialPortService extends Service {
    private static final String TAG = "SerialPortService";
    private SerialPort serialPort = null;
    private ScheduledExecutorService scheduledExecutorService;
    private ExecutorService executorService;
    private SerialProtModule serialProtModule;
    private SerialPortListener serialPortListener;

    private SerialPortBinder serialPortBinder = new SerialPortBinder();


    @Override
    public IBinder onBind(Intent intent) {
        return serialPortBinder;
    }

    @Override
    public void onCreate() {
        executorService = Executors.newSingleThreadExecutor();
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        initSerialPort();
//        scheduledExecutorService.scheduleWithFixedDelay(runnable,2,2, TimeUnit.MINUTES);
    }

    public class SerialPortBinder extends Binder {
        /**
         * 设置对串口数据的监听
         *
         * @param serialPortListener
         */
        public void setSerialPortListener(SerialPortListener serialPortListener) {
            // log.d(TAG, "setSerialPortListener:" + serialPortListener);
            SerialPortService.this.serialPortListener = serialPortListener;
            if (SerialPortService.this.serialProtModule != null) {
                serialProtModule.setSerialPortListener(serialPortListener);
            }
        }


        /**
         * 输出继电器开门，用于外接控制器模式
         * @param door 门编号
         * @param time  开门时间
         * @param type  开门类型 0x00：不动作；0x01：普通开门；0x02：常开；0x03：常闭；0x04：恢复；
         */
        public void outRelayWithControl(byte door, byte time, byte type) {
            if (SerialPortService.this.serialProtModule != null) {
                serialProtModule.outRelay(door, time, type);
            }
        }



        /**
         * 获取卡号输出的数据
         * @param weigen  韦根位数(16进制)
         * @param cardNo 卡号（16进制）
         * @return
         */
        public void outCard(byte weigen,String cardNo) {
            if (SerialPortService.this.serialProtModule != null) {
                serialProtModule.outCard(weigen, cardNo);
            }
        }

        /**
         * 输出蜂鸣器
         */
        public void outBeer() {
            if (SerialPortService.this.serialProtModule != null) {
                serialProtModule.outBeer();
            }
        }

        /**
         * 输出挟持密码警报
         * @param reuslt
         */
        public void outHoldWarm(boolean reuslt) {
            if (SerialPortService.this.serialProtModule != null) {
                serialProtModule.outHoldWarm(reuslt);
            }
        }

        /***
         * 输出
         * @param weigen
         * @param data
         */
        public void outPassword(byte weigen, byte data) {
            if (SerialPortService.this.serialProtModule != null) {
                serialProtModule.outKey(weigen, data);
            }
        }


        public void outPassword(byte weigen, byte type,String data) {
            if (SerialPortService.this.serialProtModule != null) {
                serialProtModule.outKeys(weigen, type,data);
            }
        }

        public void resetSlave() {
            if (SerialPortService.this.serialProtModule != null) {
                serialProtModule.resetSlave();
            }
        }

        public void getVersion() {
            if (SerialPortService.this.serialProtModule != null) {
                serialProtModule.getVersion();
            }
        }

        public void resetReader() {
            if (SerialPortService.this.serialProtModule != null) {
                serialProtModule.resetReader();
            }
        }
    }


    private void initSerialPort() {
        try {
            serialPort = new SerialPort(new File(Constants.SERIAL_PORT_PATH), Constants.SERIAL_PORT_BAUDRATE, 0);
            Log.v(SerialProtModule.TAG, "串口启动..." + serialPort);
            serialProtModule = new SerialProtModule(serialPort, executorService);
        } catch (Exception e) {
            Log.e(TAG, "串口初始化异常:" + e.getMessage(), e);
            serialPortListener.onError(e);
        }
    }


    /**
     * 检查串口心跳
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (DataProcessUtil.untilNow(serialProtModule.getHearTime()) > 120) {
                    Log.i(TAG, "通讯中断,重启串口通讯...");
                    if (serialProtModule != null) {
                        serialProtModule.setOpen(false);
                        serialProtModule.close();
                    }
                    initSerialPort();
                }
            } catch (Exception e) {
                Log.e(TAG, "重启串口通讯异常:" + e.getMessage(), e);
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService = null;
        }
    }
}
